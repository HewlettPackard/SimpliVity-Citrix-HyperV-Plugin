
param(
	[Parameter(Mandatory=$true)]
	[string]
	$inputFile,
	
	[string]
	$logFile
)

$TrustUri = "https://trust.citrixworkspacesapi.net"
$baseUri  = "https://registry.citrixworkspacesapi.net"
$hostname = hostname

# Absolute path of the input file
# $inputFile = "C:\Users\Administrator.MOSCOW\Desktop\citrix_input.psd1"

filter WriteFile {
	#"$(Get-Date -Format G)|$hostname|$_" | Out-File -FilePath $logFile -Append
	"$(Get-Date -Format s)|$hostname|$_" | Out-File -FilePath $logFile -Append -Encoding ASCII 
}

Set-ExecutionPolicy Unrestricted -Force
############### END USER VARIABLES ###############

"APPLOG| Starting cleanup script of Citrix Cloud connector and SCVMM VM" | WriteFile

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
	
    # Read the logging file
    $execution_log = $input.logging.execution
    $output_log = $input.logging.output
} Catch {
    "ERRLOG| Failed to read input $_.ExceptionItemName from datafile $inputFile. $_.Exception.Message" |  WriteFile
	Write-Error "Failed to read input $_.ExceptionItemName from datafile $inputFile. $_.Exception.Message"
    exit 1
}

$password = Convertto-SecureString $ovcPassword -AsPlainText -Force
$scvmmCreds = New-Object PSCredential($ovcUsername, $password)
$vmm = Get-SCVMMServer -ComputerName $scvmmIpAddress -TCPPort $scvmmPort -Credential $scvmmCreds -ErrorVariable scvmmConn

if ($scvmmConn.count -ne 0) {
	Write-Error "$vmName : Failed to connect SCVMM Serve '$scvmmIpAddress' with username 'ovcUsername'" 
	"ERRLOG| $vmName : Failed to connect SCVMM Serve '$scvmmIpAddress' with username 'ovcUsername'" | WriteFile
	exit 1
}

$vm = get-SCVirtualMachine | ? {$_.Name -eq $vmName}

if ($vm -eq $null){
	"APPLOG| Virtual Machine $vmName is already deleted." |  WriteFile
	Write-Host "Virtual Machine $vmName is already deleted."
	exit 0
}

$netAdapter = Get-SCVirtualNetworkAdapter -VM $vm
$vmIP = $netAdapter.IPv4Addresses[0]

"APPLOG| Deconfigure VM '$vmName' with IP address '$vmIP'" |  WriteFile
Write-Host "Deconfigure VM '$vmName' with IP address '$vmIP'"

$adPassword = ConvertTo-SecureString $adPassword -AsPlainText -Force
$adCreds = New-Object PSCredential("$adUsername@$adDomain", $adPassword)

invoke-command -ComputerName $vmIP -Credential $adCreds -ScriptBlock {
	Set-ExecutionPolicy Unrestricted; 
	$cmd = "c:\cwcconnector.exe /uninstall /q"
	Out-File -FilePath C:\ConnectorUninstall.cmd -InputObject $cmd -Encoding ascii

	Set-ExecutionPolicy Unrestricted
	Set-NetFirewallProfile -Profile Domain,Public,Private -Enabled False
}

# Enable PROXY
if ($proxyURL) {
    $proxyIPPort = $proxyURL + ":" + $proxyPort
	"APPLOG| Set Proxy URL $proxyIPPort" | WriteFile
    Write-Host "Set Proxy URL $proxyIPPort"
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
		
		Invoke-Command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock {
			param($creds)
			$Wcl=New-Object System.Net.WebClient
			$Wcl.Proxy.Credentials=$creds
		} -ArgumentList $proxyCreds -ErrorVariable checkCmd
	}
}
# PROXY End

$tempFile = 'c:\TempFile.txt'

Start-Process "C:\Users\Public\HpeCitrixPlugin\psexec.exe" " /accepteula -nobanner -s \\$vmIP c:\ConnectorUninstall.cmd" -RedirectStandardError $tempFile -NoNewWindow -Wait

"APPLOG| Removed Citrix Cloud Connector on $vmName" | WriteFile 
Write-Host "Removed Citrix Cloud Connector on $vmName"

Invoke-Command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock { param($Name, $creds) Remove-computer -UnjoinDomainCredential $creds -ComputerName $Name -WorkgroupName "local" -Force -Restart  } -ArgumentList $vmName,$adCreds

"APPLOG| Unjoin $vmName from AD domain $domain" | WriteFile 
Write-Host "Unjoin $vmName from AD domain $domain"

# Assumption: SCVMM and VM to be in same domain, extract the domain IP from SCVMM
$adDomainIP=(nslookup -type=srv "_ldap._tcp.$adDomain" | Select-String -Pattern 'internet address' | ForEach {$_ -split ' ' | Select -Last 1})

Invoke-Command -ComputerName  $adDomainIP -Credential $adCreds -ScriptBlock { param($Name) Get-ADComputer $Name | Remove-ADObject -Recursive -Confirm:$false  } -ArgumentList $vmName

"APPLOG| Removed '$vmName' entry from Active Directory" | WriteFile 
Write-Host "Removed '$vmName' entry from Active Directory"

"APPLOG| Stop and delete Virtual machine $vmName" | WriteFile
Write-Host "Stop and delete Virtual machine $vmName"
Stop-SCVirtualMachine -VM $vm -Force
Remove-SCVirtualMachine -VM $vm
 
"APPLOG| End - Cleanup script" | WriteFile
Write-Host "End - Cleanup script"
#Write-Host "Starting Process $pwd\cwcconnector.exe with parameters $params –Wait"
#invoke-command -ComputerName  $vmIP -Credential $adCreds -ScriptBlock {param($uri, $param) Set-ExecutionPolicy Unrestricted; Remove-Item -Force c:\cwcconnector.exe -ErrorAction SilentlyContinue; Invoke-WebRequest -Uri $uri -OutFile "C:\cwcconnector.exe"; Start-Process c:\cwcconnector.exe $param –Wait;} -ArgumentList $downloadsUri,$params
  
  