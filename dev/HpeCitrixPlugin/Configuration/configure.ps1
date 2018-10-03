
param(
	[Parameter(Mandatory=$true)]
	[string]
	$inputFile,
	
	[string]
	$logFile
)

$TrustUri = "https://trust.citrixworkspacesapi.net"
$baseUri  = "https://registry.citrixworkspacesapi.net"
$hostname = $(hostname)
$vmIP = $null

filter WriteFile {
	#"$(Get-Date -Format G) | $_" | Tee -FilePath $logFile -Append
	#"$(Get-Date -UFormat '%b  %e %T')|$hostname|$_" | Out-File -FilePath $logFile -Append -Encoding ASCII 
	"$(Get-Date -Format s)|$hostname|$_" | Out-File -FilePath $logFile -Append -Encoding ASCII 
}

"APPLOG| Starting script to create VM and install Citrix Cloud connector" | WriteFile

Set-ExecutionPolicy Unrestricted -Force
############### END USER VARIABLES ###############

########## BEGIN INPUT DATA VALIDATION ###########
if (-NOT (Test-Path $inputFile)) {
	"ERRLOG| Input Data file is not present in the given path $inputFile" | WriteFile
    Write-Error "Input Data file is not present in the given path $inputFile"
    exit 1
}
########### END INPUT DATA VALIDATION ############

############### BEGIN READ DATA  #################

Try {
    $file = Split-Path $inputFile -Leaf
    $baseDir = Split-Path $inputFile -Parent
    Import-LocalizedData -BindingVariable input -BaseDirectory $baseDir -FileName $file

	# Read input for VM Creation
	$vmName = $input.vm.name
	$vmTemplate = $input.vm.template
	$vmNetwork  = $input.vm.network
	$vmHost = $input.vm.host
	$vmUsername = $input.vm.username
	$vmPassword = $input.vm.password
	
    # Read Active Directory
    $adDomain = $input.ad.domain
    $adUsername  = $input.ad.username
    $adPassword = $input.ad.password
    #$pass_word = [System.Text.Encoding]::Unicode.GetString([System.Convert]::FromBase64String($input.svt.password))

	# Read Proxy Information
	$proxyURL = $input.proxy.URL
	$proxyPort = $input.proxy.port
	$proxyUsername = $input.proxy.username
	$proxyPassword = $input.proxy.password
	
	# Read Simplivity Details
	$ovcIpAddress = $input.ovc.ipaddress
	$ovcUsername = $input.ovc.username
	$ovcPassword = $input.ovc.password
	
	# Read SCVMM Details
	$scvmmIpAddress = $input.scvmm.ipaddress
	$scvmmPort = $input.scvmm.port
	
    # Read Citrix API Access
    $citrixCustomer = $input.citrix.customerName
    $citrixClientId = $input.citrix.clientId
    $citrixClientKey = $input.citrix.clientKey
    $citrixResourceLocation = $input.citrix.resourceLocation

    # Read the logging file
    $execution_log = $input.logging.execution
    $output_log = $input.logging.output
} Catch {
	"ERRLOG| Failed to read input $_.ExceptionItemName from datafile $inputFile. $_.Exception.Message" | WriteFile
    Write-Error "Failed to read input $_.ExceptionItemName from datafile $inputFile. $_.Exception.Message"
    exit 1
}

if (-Not ($vmName -And $vmTemplate -And $adDomain -And $adUsername -And $adPassword -And $citrixCustomer -And $citrixClientId -And $citrixClientKey -And $citrixResourceLocation -And $ovcIpAddress -And $ovcUsername  -And $ovcPassword) ) {
	"ERRLOG| $vmName : Few or all the required parameters are missing" | WriteFile
    Write-Error "$vmName : Few or all the required parameters are missing"
    exit 1
}

Write-Host "$vmName : Input parameters successfully read" 
"APPLOG| $vmName : Input parameters successfully read" | WriteFile

$password = Convertto-SecureString $ovcPassword -AsPlainText -Force
$scvmmCreds = New-Object PSCredential($ovcUsername, $password)
$vmm = Get-SCVMMServer -ComputerName $scvmmIpAddress -TCPPort $scvmmPort -Credential $scvmmCreds -ErrorVariable scvmmConn

if ($scvmmConn.count -ne 0) {
	Write-Error "$vmName : Failed to connect SCVMM Serve '$scvmmIpAddress' with username 'ovcUsername'" 
	"ERRLOG| $vmName : Failed to connect SCVMM Serve '$scvmmIpAddress' with username 'ovcUsername'" | WriteFile
	exit 1
}

Write-Host "$vmName : Connected to SCVMM Server '$scvmmIpAddress'"
"APPLOG| $vmName : Connected to SCVMM Server '$scvmmIpAddress'" | WriteFile

#### OVC
#Ignore Self Signed Certificates and set TLS
Try {
Add-Type @"
       using System.Net;
       using System.Security.Cryptography.X509Certificates;
       public class TrustAllCertsPolicy : ICertificatePolicy {
           public bool CheckValidationResult(
               ServicePoint srvPoint, X509Certificate certificate,
               WebRequest request, int certificateProblem) {
               return true;
           }
       }
"@
   [System.Net.ServicePointManager]::CertificatePolicy = New-Object TrustAllCertsPolicy
   [System.Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
} Catch {
}

# Authenticate - Get SVT Access Token

$uri = "https://$ovcIpAddress/api/oauth/token"
$base64 = [Convert]::ToBase64String([System.Text.UTF8Encoding]::UTF8.GetBytes("simplivity:"))
$body = @{username="$ovcUsername";password="$ovcPassword";grant_type="password"}
$headers = @{}
$headers.Add("Authorization", "Basic $base64")
$response = Invoke-RestMethod -Uri $uri -Headers $headers -Body $body -Method Post 

$atoken = $response.access_token
Write-Host "$vmName : Performing Simplivity operation with OVC token '$atoken'"
"APPLOG| $vmName : Performing Simplivity operation with OVC token '$atoken'" | WriteFile

# Create SVT Auth Header
$headers = @{}
$headers.Add("Authorization", "Bearer $atoken")

$uri = "https://" + $ovcIpAddress + "/api/virtual_machines?show_optional_fields=true&name=" + $vmTemplate
$baseCloneVm = Invoke-RestMethod -Uri $uri -Headers $headers -Method Get

if ($baseCloneVm.count -ne 1) {
    Write-Error "$vmName : Clone template is neither present or duplicate exists (Count: $baseCloneVm.count )"
	"ERRLOG| $vmName : Clone template is neither present or duplicate exists" | WriteFile
    exit 1
}

$versionResp = Invoke-RestMethod -Uri "https://$ovcIpAddress/api/version" -Method Get 
$versionId = $versionResp.REST_API_Version

$baseCloneId = $baseClonevm.virtual_machines[0].id
$isTemplate = $baseClonevm.virtual_machines[0].hypervisor_is_template

$cloneUri = "https://" + $ovcIpAddress + "/api/virtual_machines/" +  $baseCloneId + "/clone"
$cloneData = @{"virtual_machine_name"="$vmName";}

$contentType = "application/vnd.simplivity.v" + $versionId + "+json"
$count = 3
while ($count) {
	
	# Perform Simplivity Clone
	$svtVmCloneTask = Invoke-RestMethod -Uri $cloneUri -Headers $headers -Method POST -Body ($cloneData | ConvertTo-Json) -ContentType $contentType
	$taskuri = "https://" + $ovcIpAddress + "/api/tasks/" +  $svtVmCloneTask.task.id
	
	while ($svtVmCloneTask.task.state -eq "IN_PROGRESS")
	{
		Start-Sleep -s 5
		Write-Host "$vmName : Waiting for Simplivity clone ($($svtVmCloneTask.task.state))"
		"APPLOG| $vmName : Waiting for Simplivity clone ($($svtVmCloneTask.task.state))" | WriteFile
		
		# Get the Task status
		$svtVmCloneTask = Invoke-RestMethod -Uri $taskUri -Headers $headers -Method GET
	}

	if ($svtVmCloneTask.task.state -eq "COMPLETED") {
		write-Host "$vmName : Simplivity clone successful"
		Write-Host ""
		"APPLOG| $vmName : Simplivity clone successful" | WriteFile
		break;
	}
	
	$count = $count - 1
	if ($count -eq 0) {
		write-Error "$vmName : Failed to clone Simplivity ${vmTemplate}"
		"ERRLOG| $vmName : Failed to clone Simplivity ${vmTemplate}" | WriteFile
		exit 1
	}
	
	Write-Host "$vmName : Failed to clone Simplivity ${vmTemplate}. Retrying.."
	"APPLOG| $vmName : Failed to clone Simplivity ${vmTemplate}. Retrying.." | WriteFile
	Start-Sleep -s 5
}

### END OF OVC
$vm = Get-SCVirtualMachine -Name $vmName

$waitCount = 5
while ($vm -eq $null) {
	Start-Sleep -s 5
	Write-Host -NoNewLine "Waiting."

	if ($waitCount -eq 0) {
		"ERRLOG| $vmName : Failed to created new virtual machine '$vmName'. Error $Error[0]" | WriteFile
		Write-Error "$vmName : Failed to created new virtual machine '$vmName'. Error $Error[0]"
		exit 1
	}
	$waitCount = $waitCount - 1
	$vm = Get-SCVirtualMachine -Name $vmName
}

#Write-Host ".ok"
Write-Host "$vmName : Virtual machine '$vmName' is successfully created" 
"APPLOG| $vmName : Virtual machine '$vmName' is successfully created" | WriteFile

# Check and Set Network for VM
$netAdapter = Get-SCVirtualNetworkAdapter -VM $vm
If ($netAdapter.VMNetwork.name -ne $vmNetwork) {
     Write-Host "$vmName : Set VM Network '$vmNetwork' to Virtual machine '$vmName'" 
	"APPLOG| $vmName : Set VM Network '$vmNetwork' to Virtual machine '$vmName'" | WriteFile
    $network = Get-SCVMNetwork | ? {$_.Name -eq $vmNetwork}
    Set-SCVirtualNetworkAdapter -VirtualNetworkAdapter $netAdapter -VMNetwork $network
}

Start-SCVirtualMachine -VM $vm

Write-Host "$vmName : Virtual machine started" 
"APPLOG| $vmName : Virtual machine started" | WriteFile

Start-Sleep -s 20

write-Host -NoNewLine "$vmName : Read updated Virtual Machine details from HyperV"
"APPLOG| $vmName : Read updated Virtual Machine details from HyperV" | WriteFile

$waitCount = 5
while ($vmIP -eq $null) {
	Start-Sleep -s 10
	$dummyVar = Read-SCVirtualMachine -VM $vm
	Write-Host -NoNewLine "."

	if ($waitCount -eq 0) {
		"ERRLOG| $vmName : VM not started as expected. Check networking config and retry" | WriteFile
		Write-Error "$vmName : VM not started as expected. Check networking config and retry"
		exit 1
	}
	$waitCount = $waitCount - 1
	$vmIP = $netAdapter.IPv4Addresses[0]
}

Write-Host ".ok"
#$vm = get-SCVirtualMachine | ? {$_.Name -eq $vmName}
#$netAdapter = Get-SCVirtualNetworkAdapter -VM $vm
#$vmIP = $netAdapter.IPv4Addresses[0]

Write-Host "$vmName : VM successfully Started" | Tee -FilePath $logFile -Append
### SCVMM END

### Active Directory Begin
$adPassword = ConvertTo-SecureString $adPassword -AsPlainText -Force
$adCreds = New-Object PSCredential("$adUsername@$adDomain", $adPassword)

$vmPassword = ConvertTo-SecureString $vmPassword -AsPlainText -Force
$localCreds = New-Object PSCredential($vmUsername, $vmPassword)

"APPLOG| $vmName : Adding VM to AD domain '$adDomain'"  | WriteFile
Write-Host "$vmName : Adding VM to AD domain '$adDomain'"

$count = 3

$mutex = $null
try
{
	# Before renaming VM in the AD, Hyper clone contains same hostname and might cause issues in multi thread apps
    $createdNew = $False

    $mutex = New-Object System.Threading.Mutex($true, "Global\AzureStackDeploymentStatus", [ref]$createdNew)

    if (-not $createdNew)
    {
        try
        {
           $mutex.WaitOne(40000) | Out-Null
        }
        catch [System.Threading.AbandonedMutexException]
        {
            #Thread acquiring mutex as another thread exit without releasing the mutex
        }
    }

	While ($count) {
		Invoke-command -ComputerName  $vmIP -Credential $localCreds -ScriptBlock { 
			param($domain, $creds) 
			Add-computer -DomainName $domain -Credential $creds -Force
		} -ArgumentList $adDomain,$adCreds -ErrorVariable checkCmd

		if ($checkCmd -ne $null) {
			$count = $count - 1
			Write-Host "$vmName : Adding virtual machine to Active Directory failed with error $Error[0], Retrying..." 
			"APPLOG| $vmName : Adding virtual machine to Active Directory failed with error $Error[0], Retrying..." | WriteFile
			if ($count -eq 0) {
				Write-Error "$vmName : Failed to add '$vmName' to active directory $adDomain. Error $Error[0]"
				"ERRLOG| $vmName : Failed to add '$vmName' to active directory $adDomain. Error $Error[0]" | WriteFile
				# @TODO: Remove Virtual Machine
				exit 1
			}
			# Wait for 5 seconds
			Start-Sleep -s 5
		}
		else {
			Remove-Variable checkCmd
			Invoke-command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock { 
				param($newname, $creds)
				Rename-computer -NewName $newname -DomainCredential $creds -Force -Restart
			} -ArgumentList $vmName,$adCreds -ErrorVariable checkCmd

			Write-Host "$vmName : Virtual machine '$vmName' is successfully created" 
			"APPLOG| $vmName : Virtual machine '$vmName' is successfully created" | WriteFile
			break;
		}
	}
}
finally
{
    if ($mutex -ne $null)
    {
        $mutex.ReleaseMutex()
        $mutex.Dispose()
    }
}
	


Write-Host -NoNewLine "$vmName : Waiting for reboot of VM $vmName"
$count = 10
While ($count) {
	Start-Sleep -s 5
	if (Test-NetConnection -ComputerName  $vmIP -InformationLevel Quiet -Port 3389) {
		Write-Host "ok"
		Write-Host "$vmIP:3389 is reachable"
		break;
	}
	Write-Host -NoNewLine "."
	$count = $count - 1
	if ($count -eq 0) {
	    "ERRLOG| $vmName : VM is not up after updating domain" | WriteFile
		Write-Error "$vmName : VM is not up after updating domain"
		# @TODO: Remove Virtual Machine
		exit 1
	}
}

### Active Directory END

### Citrx Cloud Connection - BEGIN
$body = @{}
$body.Add("clientId", $citrixClientId)
$body.Add("clientSecret", $citrixClientKey)

$uri = "https://trust.citrixworkspacesapi.net/" +  $citrixCustomer + "/tokens/clients"
$response = Invoke-RestMethod -Method Post -Body (Convertto-json $body) -Uri $uri -ContentType 'application/json'
$token = $response.token

$headers = @{}
$headers.Add("Authorization", "CWSAuth bearer=$token")

$uri = $baseUri + "/$citrixCustomer/resourcelocations"
$resources = Invoke-RestMethod -Uri $uri -Method "Get" -ContentType "application/json" -Headers $headers

foreach($resource in $resources.items) {
	if ($resource.name -eq $citrixResourceLocation) {
		$resourceId = $resource.id
		"APPLOG| $vmName : Found Resource location '$citrixResourceLocation' of '$citrixCustomer'" | WriteFile
		Write-Host "$vmName : Found Resource location '$citrixResourceLocation' of '$citrixCustomer'"
		break;
	}
}

if (-Not $resourceId) {
	Write-Host "$vmName : Missing resource location '$citrixResourceLocation' in '$citrixCustomer'"
	"APPLOG| $vmName : Missing resource location '$citrixResourceLocation' in '$citrixCustomer'"  | WriteFile	
	
	"APPLOG| $vmName : Creating new resource location '$citrixResourceLocation'"  | WriteFile
	Write-Host "$vmName : Creating new resource location '$citrixResourceLocation'" 

	$body = @{'Name'=$citrixResourceLocation}
	$resource = Invoke-RestMethod -Method "POST" -Body (Convertto-json $body) -Uri $uri -ContentType 'application/json' -Headers $headers
	$resourceId = $resource.id
}

"APPLOG| $vmName : Location uuid of '$citrixResourceLocation' is '$resourceId'" | WriteFile
Write-Host "$vmName : Location uuid of '$citrixResourceLocation' is '$resourceId'"

$runCommand = "C:\cwcconnector.exe /q /Customer:$citrixCustomer /ClientId:$citrixClientId /ClientSecret:$citrixClientKey /Location:$resourceId /AcceptTermsOfService:true"
$downloadsUri = 'https://downloads.cloud.com/$citrixCustomer/connector/cwcconnector.exe'

"APPLOG| $vmName : Downloading cloud connector on the $vmName" | WriteFile
Write-Host "$vmName : Downloading cloud connector on $vmName in the path c:\cwcconnector.exe!!"

# Enable PROXY
if ($proxyURL) {
    $proxyIPPort = $proxyURL + ":" + $proxyPort
	"APPLOG| $vmName : Set Proxy URL $proxyIPPort" | WriteFile
    Write-Host "$vmName : Set Proxy URL $proxyIPPort"
	Remove-Variable checkCmd
	Invoke-Command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock {
		param($proxyIP, $creds)
		
		Set-ExecutionPolicy Unrestricted
		
		netsh winhttp set proxy $proxyIP
		
		$Wcl=New-Object System.Net.WebClient
		$Wcl.Proxy.Credentials=$creds
		
	} -ArgumentList $proxyIPPort,$proxyCreds -ErrorVariable checkCmd
	
	if ($proxyUsername) {
	    $proxySafePass = ConvertTo-SecureString $proxyPassword -AsPlainText -Force
        $proxyCreds = New-Object PSCredential($proxyUsername, $proxySafePass)
		
		Remove-Variable checkCmd
		Invoke-Command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock {
			param($creds)
			$Wcl=New-Object System.Net.WebClient
			$Wcl.Proxy.Credentials=$creds
		} -ArgumentList $proxyCreds -ErrorVariable checkCmd
	}
}
# PROXY End

Invoke-command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock {
    param($uri, $cmd) Set-ExecutionPolicy Unrestricted
	Remove-Item -Force c:\cwcconnector.exe -ErrorAction SilentlyContinue
	$ProgressPreference = 'SilentlyContinue'
	Invoke-WebRequest -Uri $uri -OutFile "C:\cwcconnector.exe" 
	$ProgressPreference = 'Continue'
	Out-File -FilePath C:\ConnectorInstall.cmd -InputObject $cmd -Encoding ascii
} -ArgumentList $downloadsUri, $runCommand

"APPLOG| $vmName : Starting Citrix Cloud installation on '$vmName'" | WriteFile
Write-Host "$vmName : Starting Cloud connector installation with parameters $params –Wait"

Invoke-Command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock {
	Set-ExecutionPolicy Unrestricted
	Set-NetFirewallProfile -Profile Domain,Public,Private -Enabled False
}

$tempFile = 'c:\TempFile.txt'

Start-Process "C:\Users\Public\HpeCitrixPlugin\psexec.exe" " /accepteula -nobanner -s \\$vmIP c:\ConnectorInstall.cmd" -RedirectStandardError $tempFile  -NoNewWindow -Wait
$checkCode = Select-String -Path $tempFile -Pattern 'error code 0'

if ($checkCode -ne $null) {
	"APPLOG| $vmName : Citrix Cloud connector installation completed" | WriteFile
	Write-Host "$vmName : Citrix Cloud connector installation completed!!"
} else {
	"ERRLOG| $vmName : Failed to install Citrix Cloud connector" | WriteFile
	Write-Error "$vmName : Failed to install Citrix Cloud connector!!"
	exit 1
}
### Citrix Cloud Connection - End

# Check and Migrate VM to user given host
if ($vm.vmhost.name -ne $vmHost) {
     Write-Host "$vmName : Migrate Virtual machine '$vmName' to '$vmHost'" 
	"APPLOG| $vmName : Migrate Virtual machine '$vmName' to '$vmHost'" | WriteFile
	
	$moveVm = Move-SCVirtualMachine -VM $vm -VMHost $vmHost -ErrorVariable moveError #-ErrorAction "Stop"
	
	if ($moveError -ne $null) {
		Write-Host "$vmName : Failed to migrate Virtual machine '$vmName' to '$vmHost'" 
		"APPLOG| $vmName : Failed to migrate Virtual machine '$vmName' to '$vmHost'" | WriteFile
		
		Write-Host "$vmName : Repairing the VM '$vmName'" 
		"APPLOG| $vmName : Repairing the Virtual machine '$vmName'" | WriteFile
		
		$repairVm = repair-scvirtualmachine -VM $vm -Force -Dismiss -ErrorVariable repairVar
	}
}