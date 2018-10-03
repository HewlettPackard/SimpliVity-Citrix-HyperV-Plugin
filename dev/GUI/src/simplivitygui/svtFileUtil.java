/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Danny John Britto
 * @version 1.0
 * @description
 *                  Class for manipulating files like read/write, and this is 
 *                  mostly for the configuration files. The class methods would 
 *                  be added later based on requirements
 * 
 * 
 */
public class svtFileUtil {
    
    // if empty constructor called, then we may need to find out
    // the path of a configuration file, so that we can read the
    // content
    private svtFileUtil() {
        
        // We need to find out the application path and append with
        // configuration file, so that we can read the content from the
        // configuration file which would be used later
        // Non Release Package

/*
        this.sHomeDirPath = System.getProperty("user.dir"); 
                   //+ File.separator + "svtCCP.cfg";
*/                
        /* Release Package */

        try {
            java.security.CodeSource codeSource = SimpliVityGUI.class.getProtectionDomain().getCodeSource();
            //File jarFile = new File(codeSource.getLocation().toURI().getPath());
            sHomeDirPath = (new File(codeSource.getLocation().toURI().getPath())).getParentFile().getPath();
        } catch(Exception ex) {
            System.out.println("Exception at Configuration");
            System.exit(1);
        }

    }

    // if parameterised constructor called, then we need to assign
    // the path name to the member variable
    private svtFileUtil( String sCfgPath ) {
        // assign it to member variable
        this.sHomeDirPath = sCfgPath;
    }
    
    public static svtFileUtil getInstance() {
    
        if ( null == fileUtilInst ) {
            // we need to create the instance and
            // Fill up necessary values and make the 
            // datastructure ready for the user to consume
            fileUtilInst = new svtFileUtil();
            /*
            try {
                // This would be called only once
                readConfigFileContent();
            } catch (java.io.IOException eIOException) {
                // We need to sync it in the log file
                System.out.println("CfgFile Exception: " + 
                                    eIOException.toString());
            } 
            */
        }
        
        return fileUtilInst;
    }
    
       private String getCurrentPath() {
        
        if( true == sUserDirPath.isEmpty() ) {
            return (sHomeDirPath + File.separator);
        }

        return (sUserDirPath + File.separator);
    }
    
    public String getVmInfoFilePath(){
        return (getCurrentPath() + File.separator + sVmInfoFile);
    }
    
    
    public String getIconPath() {
        return (getCurrentPath() + getMapValue("IconName"));
    }
    
    public java.awt.Image getImageIcon() {
        javax.swing.ImageIcon imgIcon = new javax.swing.ImageIcon(getIconPath());
        java.awt.Image image = imgIcon.getImage();
        return image;
    }
    
    // get method for Configuration script
    public String getConfigScrPath() { 
        
        //return (getCurrentPath() + getMapValue("PSConfigScriptPath"));
        String sFileName = "& \"'" + getCurrentPath() + 
                            getMapValue("PSConfigScriptPath") + "\"'";
        return sFileName;        
    }
    
    // get method for DeConfiguration script
    public String getDeConfigScrPath() { 
        //return (getCurrentPath() + getMapValue("PSDeConfigScriptPath"));
        String sFileName = "& \"'" + getCurrentPath() + 
                            getMapValue("PSDeConfigScriptPath") + "\"'";
        return sFileName;
    }
    
    // get method for LogFilePath
    public String getLogFilePath() { 
        return (getCurrentPath() + getMapValue("PluginLogFilePath"));
    }
    
    // temproary fix for displaying the network
    public String getSvtNetwork() {
        return getMapValue("SvtNetwork");
    }
    
    // temproary fix for displaying the network
    public String getScVmmImage() {
       return getMapValue("ScVMMImageLocation");
    }
    
    public String getClientID() {
        return getMapValue("ClientID");
    }
    
    public String getClientKey() {
        return getMapValue("ClientKey");
    }
    
    public String getCustomerName() {
        return getMapValue("CustomerName");
    }
    
    public String getResourceLocation() {
        return getMapValue("ResourceLocation");
    }
    
    public String getDomainUserName() {
        return  getMapValue("DomainUserName");
    }
    
    public String getDomainPassword() {
        return getMapValue("DomainPassword");
    }
    
    public String getDomainName() {
        return getMapValue("DomainName");
    }
    
    public String getVMName() {
        return getMapValue("VMName");
    }
    
    public String getVMUserName() {
        return getMapValue("VMUserName");
    }
    
    public String getVMPassword() {
        return getMapValue("VMPassword");
    }
    
    // temproary fix for input file between
    // GUI and PowerShell
    public String getPSConfigInput() {
        //return (sHomeDirPath + File.separator+ sInputFile);
        return (getCurrentPath() + sInputFile);
    }
    
    public String getConfPath() {
        return getCurrentPath();
    }
    
    public void resetPSInputFile() {
   
        java.io.File dir = new File(getCurrentPath());
        java.io.FilenameFilter filter = new java.io.FilenameFilter() {
            public boolean accept (java.io.File dir, String name) { 
                return name.endsWith(".psd1");
            }
        }; 
        String[] children = dir.list(filter);
        if (children == null) {
           System.out.println("Either dir does not exist or is not a directory"); 
        } else { 
            for (int i=0; i< children.length; i++) {
                String sFileName = children[i];
                java.io.File file = new java.io.File(sFileName);
                if ( file.isFile() ) file.delete();
            } 
        } 
    } 
    
    public void logMessage(int nType, String sMsg){
    
        // log format is as follows
        // date:host:logtype:description 
        // May 4 15:30:10|johnbrit|APPLOG| script started initializing
        // May 4 15:30:10|johnbrit|ERRLOG| script exited with error
        String sLog = java.time.LocalDateTime.now().toString();
        try {
            sLog += "|" + java.net.InetAddress.getLocalHost().getHostName();
        } catch(Exception ex) {
            // empty catch block, most of the time this does not
            // return any error
        }    
        
        // stupid - why the below code is not working ???
        sLog += (nType == 0) ? "|APPLOG|" : "|ERRLOG|";
        
        
        // String is ready to get sync'ed at the log file level.
        sLog += sMsg + "\r\n";
        
        // The below logic would open a file every time
        // and write the log data, and this is required because
        // when we hand off to PowerShell, we cannot open this
        // file, and Powershell would open and start writing the
        // content
        java.io.Writer outWriter = null;
        
        try {
            outWriter = new java.io.BufferedWriter(
                        new java.io.OutputStreamWriter(
                        new java.io.FileOutputStream(getLogFilePath(), true)));
            outWriter.write(sLog);
            
        } catch (java.io.IOException eIOException){
            
            // We need to only use error stream to write it
            System.out.println("LogFile Exception");
        } finally {
            // lets close the streams
            try {
                outWriter.close();
            } catch (Exception ex) {
            /*ignore*/
            }
        }
        return;
    }
    
    public boolean isCfgFileExist() {
        
        // Configuration file always be in the current directory
        String sFileName = sHomeDirPath + File.separator + sCfgFile;
        // We need to check whether the file exist, if it does not
        // exist then we may need to alert the user to copy the 
        // required file to continue
        try {
               java.io.File file = new java.io.File(sFileName);
               if ( file.isFile() == false ) {
                   javax.swing.JOptionPane.showMessageDialog(null, 
                            ("Configuration File Missing at: " + sFileName), 
                            "SimpliVity Citrix Cloud Connector Plugin", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                   return false;
               } 
                //System.out.println("File Deleted...");
           }catch(Exception ex) {
               javax.swing.JOptionPane.showMessageDialog(null, 
                            ("Configuration File Exception" + sFileName), 
                            "SimpliVity Citrix Cloud Connector Plugin", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
               return false;
           }
        
        return true;
    }
    
    public boolean isCfgFileExist( String sFilePath ) {
        
        // Configuration file always be in the current directory
        //String sFileName = sHomeDirPath + File.separator + sCfgFile;
        // We need to check whether the file exist, if it does not
        // exist then we may need to alert the user to copy the 
        // required file to continue
        try {
               java.io.File file = new java.io.File(sFilePath);
               if ( file.isFile() == false ) {
                   javax.swing.JOptionPane.showMessageDialog(null, 
                            ("Virtual Machine Information Missing Please "
                                    + "enter Manually"  ), 
                            "SimpliVity Citrix Cloud Connector Plugin", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                   return false;
               } 
                //System.out.println("File Deleted...");
           }catch(Exception ex) {
               javax.swing.JOptionPane.showMessageDialog(null, 
                            ("Virtual Machine Information File Exceltion: " + 
                             sFilePath), "SimpliVity Citrix Cloud Connector Plugin", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
               return false;
           }
        
        return true;
    }
    
    
    // Method to read the content of configuration file, this file would be in
    // application jar file path
    // SimpliVIty RootDir
    //  ->Jar 
    //  ->log
    //  ->conf
    public boolean readConfigFileContent() throws java.io.IOException {
        
        // prepare for the configuration file path and filename
        String sFileName = sHomeDirPath + File.separator + this.sCfgFile;
        java.io.BufferedReader bReader = null;
        
        try {
            // open streams for reading the content from the file
            bReader = new java.io.BufferedReader(new java.io.FileReader(sFileName));
          //StringBuilder sBuilder = new StringBuilder();
            String sLine; 
            // loop thru to find out all the contents from the configuration
            // file, and need to remove all the leading spaces and tabs, also
            // if the first character is '#', which is comment, we need to 
            // eliminate such line as well
            while( null != (sLine = bReader.readLine()) ) {
                if( true == sLine.isEmpty() ) continue;
                String sTmp0 = sLine.trim();
                if((sTmp0.charAt(0)) == '#' ) continue;
                // We got actual key=value pair
                // we need to trim the spaces and append it to MAP datastructures
                // Actual File Delimiter
                // String[] sToken = sTmp0.split("=");
                // This is only for Citrix Submit
                // THe below logic will not work for value which has '=" in it
                // need to change the logic
                //String[] sToken = sTmp0.split(" = ");
                int ix = sTmp0.indexOf("=");
                if( ix > 0){ 
                    // lets populate the values into map datastructures
                    // we need to remove trailing spaces before we add it
                    String sKey   = sTmp0.substring(0, ix);
                    String sValue = sTmp0.substring(++ix, sTmp0.length());
                    mContentHolder.put(sKey.trim(), sValue.trim());
                }    
            }    
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.toString());
        } finally {
            bReader.close();
        }
        
        // if the value is not "." which means user wants to
        // pass the value where the respective files are kept
        String tmp = getMapValue("Path");
        if (tmp.compareTo(".") != 0 ) {
            sUserDirPath = tmp;
        }    
        
        /*
        // debug
        for (java.util.Map.Entry entry:mContentHolder.entrySet()) 
            System.out.println("KEY: " + entry.getKey() + " VALUE: " + entry.getValue());
        */
        return true;
    }
    
    public boolean readVmInfoFile( java.util.Vector vVmInfoHolder ) throws java.io.IOException {
        
         if (isCfgFileExist(getVmInfoFilePath()) == false ) return false;
         
        if (vVmInfoHolder.size() == 2 ) return true;
        
        java.io.BufferedReader bReader = null;
        
        try {
            // open streams for reading the content from the file
            bReader = new java.io.BufferedReader(
                    new java.io.FileReader(getVmInfoFilePath()));

            String sLine = ""; 
            // loop thru to find out all the contents from the configuration
            // file, and need to remove all the leading spaces and tabs, also
            // if the first character is '#', which is comment, we need to 
            // eliminate such line as well
            while( null != (sLine = bReader.readLine()) ) {
                String[] sToken = sLine.split("->");
                svtVmInfo vmInfo = new svtVmInfo(sToken[0], sToken[1], 
                                                 sToken[2], sToken[3],
                                                 sToken[4]);
                vVmInfoHolder.add(vmInfo);
            }    
        } catch (Exception ex) {
            System.out.println("Exception at VM Information File: " + ex.toString());
            return false;
        } finally {
            bReader.close();
        }
                
        return true;
    }
    
    public boolean writeToFile( String sFileName, String sContent, 
                                String sSourceInfo )
                    throws java.io.IOException {
        
        String fileInput = getConfPath() + File.separator + 
                                                    sFileName + ".psd1";
        
        logMessage(0, sSourceInfo + ": PrepareInputFile: "  + fileInput);
        java.io.FileOutputStream outFile = 
                        new java.io.FileOutputStream(fileInput);

        java.io.DataOutputStream outStream = 
         new java.io.DataOutputStream(new java.io.BufferedOutputStream(outFile));
        outStream.writeBytes(sContent);
        outStream.close();

        return true;
    }
        
    public boolean writeToFile( String sFileName ) throws java.io.IOException {
        
        logMessage(0, "File Persistent Preparation: "  + sFileName);
        
        String sVmInfo = "";
        java.util.Vector holder = getVmInfoArray();
        if(isConfActive() == true ) {
            for(int ix = 0; ix < holder.size(); ix++) {
                svtVmInfo vmInfo = (svtVmInfo)holder.get(ix);
                // Virtual Machine is alive, and the status is successful
                // We need to keep the information for later use
                if(vmInfo.isVmActive() && vmInfo.getRunStatus()){
                    // prepare a string to write it to the persistent storage
                    sVmInfo += vmInfo.getVMName() + "->" + vmInfo.getVMUserName() + "->" +
                               vmInfo.getVMSvtHost() + "->" + vmInfo.getDomainName() + 
                               "->" + vmInfo.getDomainUser() + "\r\n"; 
                }
            }
            
            // we should not allow to flow the control down
            if (sVmInfo.length() == 0) return false;
        } else {
            // We need to traverse the vector and find out if there are any VM which 
            // are not active
            for(int ix = 0; ix < holder.size(); ix++) {
                svtVmInfo vmInfo = (svtVmInfo)holder.get(ix);
                // Virtual Machine is alive, and the status is successful
                // We need to keep the information for later use
                if(vmInfo.isVmActive() == false){
                    // prepare a string to write it to the persistent storage
                    sVmInfo += vmInfo.getVMName() + "->" + vmInfo.getVMUserName() + "->" +
                               vmInfo.getVMSvtHost() + "->" + vmInfo.getDomainName() + 
                               "->" + vmInfo.getDomainUser() + "\r\n"; 
                }
            }
        }

        // if the control is reaching here then it's only for DeConf
        if(sVmInfo.length() == 0  ) {
            try {
                java.io.File file = 
                        new java.io.File(getVmInfoFilePath());
                if ( file.isFile() ) file.delete();
            }catch(Exception ex) {
                logMessage(1, "Exception while deleting VM Information File");
                return false;
            }
            return true;
        }
        
        // if none of the above conditions are true, then the control would come here
        // and we can write what ever information prepared
        java.io.FileOutputStream outFile = null;
        outFile = (isConfActive()) ? new java.io.FileOutputStream(sFileName, true) :
                                     new java.io.FileOutputStream(sFileName);
        
        java.io.DataOutputStream outStream = 
         new java.io.DataOutputStream(new java.io.BufferedOutputStream(outFile));
        outStream.writeBytes(sVmInfo);
        outStream.close();

        return true;
    }
    
    public boolean writeToConsoleFile( String sFileName, 
                        javax.swing.JTextArea consoleText) throws java.io.IOException {

        java.io.FileOutputStream outFile = 
                        //new java.io.FileOutputStream(sFileName, true);
                        new java.io.FileOutputStream(sFileName);
        java.io.DataOutputStream outStream = 
         new java.io.DataOutputStream(new java.io.BufferedOutputStream(outFile));
        outStream.writeBytes(consoleText.getText());
        outStream.close();
        return true;
    }
    
    private String getMapValue( String sKey ) {
        
        // The below file reading happens only once
        // since this class is a singleton class, and 
        // only one object being maintained throughout
        /*
        if (mContentHolder.isEmpty() == true ) {
            try {
                // This would be called only once
                readConfigFileContent();
            } catch (java.io.IOException eIOException) {
                // We need to sync it in the log file
                System.out.println("CfgFile Exception: " + 
                                    eIOException.toString());
            }
        }
        */
        
        // traverse map data structure and compare the key, if it's same
        // then equvalent value to be return out of the member function
        for (java.util.Map.Entry entry:mContentHolder.entrySet()) {
            //System.out.println("KEY: " + entry.getKey() + " VALUE: " + entry.getValue());
            if ( (entry.getKey().toString().compareTo(sKey)) == 0 ) {
                //return (sHomeDirPath + File.separator + entry.getValue().toString());
                return (entry.getValue().toString());
            }
        }    
        
        return "";
    }
    
    public void setVmInfoArray( java.util.Vector vMInfo) {
        this.mVmInfoHolder = vMInfo;
    }

    public void setThreadRunStatus(String sVmName, boolean bStatus) {
    
        for(int ix = 0; ix < mVmInfoHolder.size(); ix++ ){
            svtVmInfo vmInfo = (svtVmInfo)mVmInfoHolder.get(ix);
            if(vmInfo.getVMName().compareTo(sVmName) == 0 ){
                vmInfo.setRunStatus(bStatus);
            }
        }
        return;
    }
    
    public boolean getThreadRunStatus(String sVmName) {
    
        for(int ix = 0; ix < mVmInfoHolder.size(); ix++ ){
            svtVmInfo vmInfo = (svtVmInfo)mVmInfoHolder.get(ix);
            if(vmInfo.getVMName().compareTo(sVmName) == 0 ) 
                return vmInfo.getRunStatus();
        }
        return false;
    }
    
    public java.util.Vector getVmInfoArray() {
        return this.mVmInfoHolder;
    }
    
    public void setConfActive( boolean status ) {
        mVmInfoHolder.clear();
        this.bIsConfActive = status;
    }
    
    public boolean isConfActive() {
        return this.bIsConfActive;
    }
    
    public void makeMeCentred( javax.swing.JFrame currentForm )
    {
       java.awt.Dimension dSize = 
               java.awt.Toolkit.getDefaultToolkit().getScreenSize();
       currentForm.setLocation(dSize.width/2  -  currentForm.getSize().width/2, 
                        dSize.height/2 - currentForm.getSize().height/2);
       //setting the image for all the Frames
       currentForm.setIconImage(getImageIcon());
    }
   
    // overloaded function
    public void makeMeCentred( javax.swing.JDialog currentForm )
    {
       java.awt.Dimension dSize = 
               java.awt.Toolkit.getDefaultToolkit().getScreenSize();
       currentForm.setLocation(dSize.width/2  -  currentForm.getSize().width/2, 
                        dSize.height/2 - currentForm.getSize().height/2);
       //setting the image for all the Frames
       currentForm.setIconImage(getImageIcon());
       currentForm.setTitle("SimpliVity Citrix Cloud Connector Plugin");
    }
     public String getConsoleFilePath(){
        return (getCurrentPath() + File.separator + sConsoleFilePath);
    }
    
    public String getProxyUserName() { return sProxyUserName; }
    public String getProxyPassword() { return sProxyPassword; }
    public String getProxyPort()     { return sProxyPort; }
    public String getProxyURL()      { return sProxyURL; }
    
    public void setProxyUserName(String sProxyUserName) { 
        this.sProxyUserName =  sProxyUserName;
    }
    
    public void setProxyPassword(String sProxyPassword) { 
        this.sProxyPassword = sProxyPassword; 
    }
    
    public void setProxyPort(String sProxyPort) { 
        this.sProxyPort = sProxyPort; 
    }
    
    public void setProxyURL(String sProxyURL) { 
        this.sProxyURL = sProxyURL;
    }
    
    /* Setters and Getters of SCVMM */
    public void setScVmmmIPAddress(String scvmmIP) {
        this.sScVmmIP = scvmmIP;
    }
    
    public String getScVmmIPAddress() {
        return this.sScVmmIP;
    }
  
    public void setScVmmPort(int sPort) {
        this.sScVmmPort = sPort;
    }
    
    public int getScVmmPort() {
        return this.sScVmmPort;
    }
    /* END - SCVMM */

    /* Setters and Getters of OVC */
    public void setOvcIPAddress(String sOvcIP) {
        this.sOvcIPAddress = sOvcIP;
    }
    
    public String getOvcIPAddress() {
        return this.sOvcIPAddress;
    }
    public void setOvcUserId(String sOvcUid) {
        this.sOvcUserId = sOvcUid;
    }
    
    public String getOvcUserId() {
        return this.sOvcUserId;
    }
    
    public void setOvcPassword(String sOvcPasswd) {
        this.sOvcPasswd = sOvcPasswd;
    }
    
    public String getOvcPassword() {
        return this.sOvcPasswd;
    }
    
    public boolean isOvcInfoPresent() {
        
        if(sOvcIPAddress.isEmpty() == false && 
           sOvcUserId.isEmpty()    == false &&
           sOvcPasswd.isEmpty()    == false) {
            return true;
        }
        return false;
    }
    
    public String[] getSvtClusters() {
        return this.sClusters;
    }
    
    public String[] getSvtHosts() {
        return this.sHosts;
    }
    
    public String[] getSvtVMs() {
        return this.sVirtualMachines;
    }
    
    public String[] getSvtTemplates() {
        return this.sTemplates;
    }
    
    public String[] getSvtNetworks() {
        return this.sNetworks;
    }
    
    public String[] getSvtHostsOfCluster(String clusterName) {
        return this.sClusterHosts.get(clusterName);
    }
    
    public void readSvtNetworks() {
        try 
        {
            String svtCmd = "$password = Convertto-SecureString '" + sOvcPasswd + "' -AsPlainText -Force; $creds = New-object pscredential('" +
                    sOvcUserId + "', $password);  $dummy = Get-SCVMMServer -ComputerName " + sScVmmIP + " -TCPPort " + sScVmmPort + 
                    " -Credential $creds; Get-SCVMNetwork | % {$_.Name}";
            String command = "powershell.exe  " + svtCmd;
            // Executing the command
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            // Getting the results
            powerShellProcess.getOutputStream().close();
            String line;

            ArrayList<String> networkArray = new ArrayList<String>();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                networkArray.add(line);
                logMessage(0, "Found VM Network " + line + " in SCVMM");
            }
            stdout.close();
            
            sNetworks = networkArray.toArray(new String[networkArray.size()]);

            BufferedReader stderr = new BufferedReader(new InputStreamReader(
              powerShellProcess.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                System.out.println(line);
                logMessage(1, "Error in in reading SVT Networks " + line);
            }
            stderr.close();
        }
        catch (java.io.IOException ioException) {
            System.out.println(ioException.toString());
            logMessage(1, "Exception while reading SVT VM Networks: " + ioException.toString());
        }
    }
    
    public boolean readSvTInfo() 
    {
        svtRestUtil restObj = svtRestUtil.getInstance();
        try {
            String token = restObj.getAccessToken();
            logMessage(0, "readSvTInfo: SVT Login successful. Token: " + token);
        
            sClusters = restObj.getArray("omnistack_clusters");
            sHosts = restObj.getArray("hosts");
            sVirtualMachines = restObj.getAllSvtVMs();
            sTemplates = restObj.getTemplates();
            sClusterHosts = restObj.getClusterHosts();

            // printList(sTemplates);
            return true;
        }
        catch (Exception e) {
            logMessage(1, "readSvTInfo: Exception while reading SVT Information: " + e.toString());
            return false;
        }
    }
    
    public void printList(String[] list)
    {
        for (int i=0; i<list.length; i++) {
            System.out.println( (i+1) + " : " + list[i]);
        }
    }
    
    public boolean serverListening()
    {
        Socket s = null;
        try
        {
            s = new Socket(sScVmmIP, sScVmmPort);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            if(s != null)
                try {s.close();}
                catch(Exception e){}
        }
    }
    /* END - OVC */
    
    /************* Proxy Module variables ******************/
    // Proxy URL
    private String sProxyURL = "";
    // proxy Port
    private String sProxyPort = "";
    // Proxy UserName
    private String sProxyUserName = "";
    // Proxy User Password
    private String sProxyPassword = "";
    /************* OVC Informations *************************/
    private String sOvcIPAddress = "";
    private String sOvcUserId = "";
    private String sOvcPasswd = "";
    /************* SCVMM Informations *************************/
    private String sScVmmIP = "";
    private int sScVmmPort = 8100; // dummy
    /*************** Simplivity Information *****************/
    private String[] sClusters;
    private String[] sVirtualMachines;
    private String[] sHosts;
    private String[] sTemplates;
    private String[] sNetworks;
    private HashMap<String, String[]> sClusterHosts;
    /********************************************************/
    // Configuration file path
    private String sHomeDirPath = "";
    // if the path is available at the configuration file
    private String sUserDirPath = "";
    // GUI + PowerShell configuration File
    private String sCfgFile = "svtCCP.cfg";
    // VMInformation file
    private String sVmInfoFile = "svtCloudVmInfo.bin";
    // Thread Console output
    private String sConsoleFilePath = "svtConsoleOutput.log";
    // temproary fix - this will be changed later
    // private String sInputFile = "SvtCitrixPSConfig.input";
    // ## Avinash: Powershell expect file format to be psd1
    private String sInputFile = "SvtCitrixPSConfig.psd1";
    private boolean bIsConfActive = true;
    // File content Holder, appends as KEY=VALUE pair
    private java.util.Map <String, String> mContentHolder 
                                = new java.util.HashMap<String, String>();
     // Vector to hold Virtual Machine Info
    java.util.Vector mVmInfoHolder = new java.util.Vector();
    // private object
    private static svtFileUtil fileUtilInst = null;
}

