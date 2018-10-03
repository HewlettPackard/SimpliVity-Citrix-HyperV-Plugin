/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;

/**
 *
 * @author johnbrit
 */
public class svtVmInfo {
    svtVmInfo( String sVMName, 
                      String sVMUserName,
                      String sVMPassword,
                      String sSvtHost) {
        // VM Name
        this.mSVMName = sVMName;
        // VM User Name
        this.mSVMUserName = sVMUserName;
        // VM User Password
        this.mSVMPassword = sVMPassword;
        // VM Svt Node
        this.mSSvtHost = sSvtHost;
    }
    
     svtVmInfo( String sVMName, 
                String sVMUserName,
                String sSvtHost,
                String sDomainName,
                String sDomainUser) {
         
        // VM Name
        this.mSVMName = sVMName;
        // VM User Name
        this.mSVMUserName = sVMUserName;
        // VM User Password
        this.mSVMPassword = "";
        // VM Svt Node
        this.mSSvtHost = sSvtHost;
        // Domain Name
        this.mDomainName = sDomainName;
        // Domain User Name
        this.mDomainUser = sDomainUser;
    }
    
    public void setVMStatus( boolean status ){
        this.mVmActive = status;
    }
     
    // setter methods for VM dialog to set the values to
    public void setVMName( String sVMName ) {
        this.mSVMName = sVMName;
    }
    
    public void setVMUserName( String sVMUserName ) {
        this.mSVMUserName = sVMUserName;
    }
    
    public void setVMPassword( String sVMPasswd ) {
        this.mSVMPassword = sVMPasswd;
    }
    
    public void setVMSvtHost( String sVMSvtHost ) {
        this.mSSvtHost = sVMSvtHost;
    }
    
    public void setDomainName( String sDomainName ) {
        this.mDomainName = sDomainName;
    }

    public void setDomainUser( String sDomainUser ) {
        this.mDomainUser = sDomainUser;
    }
    
    public void setLastThread( boolean value ) {
        this.mIsLastThread = value;
    }
    
    // Getter Method for other users to access
    // Virtual machine information
    public String getVMName() {
        return this.mSVMName;
    }
    
    public String getVMUserName() {
        return this.mSVMUserName;
    }
    
    public String getVMPassword() {
       return this.mSVMPassword;
    }
    
    public String getVMSvtHost() {
        return this.mSSvtHost;
    }
    
    public String getDomainName() {
        return mDomainName;
    }

    public String getDomainUser() {
        return mDomainUser;
    }
    
    public boolean isVmActive() {
        return this.mVmActive;
    }

    public boolean isLastThread() {
        return this.mIsLastThread;
    }
    
    public void setRunStatus(boolean bStatus) {
        this.mRunSuccess = bStatus;
    }
    
    public boolean getRunStatus() {
        return this.mRunSuccess;
    }
        
    // Virtual Machine Name
    private String mSVMName;
    // Virtual Machine Username
    private String mSVMUserName;
    // Virtual Machine User Password
    private String mSVMPassword;
    // Simplivity Host where VM 
    // is going to be hosted
    private String mSSvtHost;
    // Domain Name
    private String mDomainName;
    // Domain User Name
    private String mDomainUser;
    // VM status
    private boolean mVmActive = true;
    private boolean mIsLastThread = false;
    private boolean mRunSuccess = true;
}


