/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;

/**
 *
 * @author Danny John Britto (Global Solution Engineering)
 * @version 1.0
 * @date 04/04/2018
 */

// SimpliVity GUI is the entry point for Citrix Cloud Connector Plugin
// which will invoke Java GUI to proceed, before it hand over the control
// to GUI, it does check whether required configuration file exist at 
// expected path, if not the object will be wiped out
public class SimpliVityGUI {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // The below code needs to be called, to fill up
        // necessary configuration file data to the in-memory
        // structure, so that same would be used later
        svtFileUtil fileUtil = svtFileUtil.getInstance();
        if (false == fileUtil.isCfgFileExist()) {
            System.out.println("Configuration File is Missing");
            System.exit(-1);
        }    
        
        // read the configuration file, and populate values into 
        // the memory for further consumption, for other demos like
        // ETSS and discover, most of the values are set in the 
        // configuration file, but we can use both functionality of whether
        // we can still keep the values at the configuration, or user can
        // edit the values as well.
        try {
            fileUtil.readConfigFileContent();
        } catch (java.io.IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            return;
        } 

        // Base Class invocation, Need to add more functionality
        // Danny John Britto - 04/04/2018
        fileUtil.logMessage(0, "Application Invoked from: " + 
                fileUtil.getConfPath() + "*.jar Started Successfully" );
        
        // Parent frame for all subsequent end-user operations like
        // 1. Configuration of Plugin
        // 2. DeConfiguration of Plugin
        // 3. Proxy settings
        // 4. OVC Node information
        // 5. Node with cloud connector software
        new svtBaseFrame("SimpliVity Citrix Cloud Connector Plugin").show();
    }
}
