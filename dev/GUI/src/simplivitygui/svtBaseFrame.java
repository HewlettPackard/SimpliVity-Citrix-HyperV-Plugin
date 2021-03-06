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
 * @Date 06/04/2018
 *          
 */

/**
 *  Utility Class, which holds all the necessary user input and validate and
 *  write into a file. If the file is not present which means the plugin is 
 *  not installed or recently removed from the system, also we may need to preserve
 *  certain values which would also be done with this class.
 * 
 */
class svtPlugUtility {
    public svtPlugUtility(){};
    
    public svtPlugUtility( String sName ) {
    }
    
    public svtPlugUtility ( String sCloudUserId, String sCloudPasswd, 
                            String sDomainUserId, String sDomainPasswd,
                            String sDomainName, String sISOPath,
                            String sCloudURI) {
        
        this.sCloudUserId  = sCloudUserId;  // Citrix Cloud UserId
        this.sCloudPasswd  = sCloudPasswd;  // Citrix Cloud Passwd
        this.sDomainUserId = sDomainUserId; // Local Domain UserId
        this.sDomainPasswd = sDomainPasswd; // Local Domain Passwd
        this.sDomainName   = sDomainName;   // Local Domain Name
        this.sISOPath      = sISOPath;      // OS ISO path to pickup
        this.sCloudURI     = sCloudURI;     // Cloud URI
    }
    
    // set and get methods
    public String getCloudUserId() { return sCloudUserId; }
    public void setCloudUserId(String sCloudUserId) { 
        this.sCloudUserId = sCloudUserId; 
    }
    
    public String getCloudPasswd() { return sCloudPasswd; }
    public void setCloudPasswd(String sCloudPasswd) { 
        this.sCloudPasswd = sCloudPasswd; 
    }
    
    public String getDomainUserId() { return sDomainUserId; }
    public void setDomainUserId(String sDomainUserId) { 
        this.sDomainUserId = sDomainUserId; 
    }
    
    public String getDomainPasswd() { return sDomainPasswd; }
    public void setDomainPasswd(String sDomainPasswd) { 
        this.sDomainPasswd = sDomainPasswd; 
    }
    
    public String getDomainName()   { return sDomainName; }
    public void setDomainName(String sDomainName)   { 
        this.sDomainName = sDomainName; 
    }
    
    public String getISOPath() { return sISOPath; }
    public void setISOPath(String sISOPath) { 
        this.sISOPath = sISOPath;
    }
    
    public String getCloudURI() { return sCloudURI; }
    public void setCloudURI(String sCloudURI) { 
        this.sCloudURI = sCloudURI; 
    }
    
    public boolean svtDeConfigurePlugin(
            javax.swing.JProgressBar prDisplay){
        
        prDisplay.setVisible(false);
        return true;
    }
    
    // Private Variable declaration
    private String sCloudUserId;
    private String sCloudPasswd;
    private String sDomainUserId;
    private String sDomainPasswd;
    private String sDomainName;
    private String sISOPath;
    private String sCloudURI;
};

public class svtBaseFrame extends javax.swing.JFrame {
    /**
     * Creates new form svtBaseFrame
     */
    public svtBaseFrame() {
        initComponents();
    }
    
    /**
     * 
     * @param sName - Frame Caption
     * 
     */
    public svtBaseFrame( String sName)
    {
        super(sName);
        initComponents();
        objFileUtil.makeMeCentred(this);
        //objFileUtil.setBaseFrameObject(this);
    }
  
    // message dialog to show the error.
    private void displayMessage(String sMsg, int nType) {
    
        javax.swing.JOptionPane.showMessageDialog(this, sMsg, 
                        "SimpliVity Citrix Cloud Plugin", nType);
        return;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        svtMainPanel = new javax.swing.JPanel();
        imgLabel = new javax.swing.JLabel();
        btOK = new javax.swing.JButton();
        mnuBase = new javax.swing.JMenuBar();
        mItmFile = new javax.swing.JMenu();
        mnuItmConfigure = new javax.swing.JMenuItem();
        mnItmUnInst = new javax.swing.JMenuItem();
        mnItmSep01 = new javax.swing.JPopupMenu.Separator();
        mnItmProxy = new javax.swing.JMenuItem();
        mnItmLog = new javax.swing.JMenuItem();
        mnItmSep02 = new javax.swing.JPopupMenu.Separator();
        mItmExit = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mnuItmAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setAutoRequestFocus(false);
        setName("frmBase"); // NOI18N
        setResizable(false);

        svtMainPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        imgLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/simplivitygui/StartPage.png"))); // NOI18N
        imgLabel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btOK.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btOK.setText("OK");
        btOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout svtMainPanelLayout = new javax.swing.GroupLayout(svtMainPanel);
        svtMainPanel.setLayout(svtMainPanelLayout);
        svtMainPanelLayout.setHorizontalGroup(
            svtMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(svtMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(svtMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(imgLabel)
                    .addComponent(btOK, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );
        svtMainPanelLayout.setVerticalGroup(
            svtMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, svtMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imgLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btOK, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        mItmFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mItmFile.setText("Actions");
        mItmFile.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N

        mnuItmConfigure.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        mnuItmConfigure.setText("Plug(in)");
        mnuItmConfigure.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mnuItmConfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmConfigureActionPerformed(evt);
            }
        });
        mItmFile.add(mnuItmConfigure);

        mnItmUnInst.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        mnItmUnInst.setText("Plug(out)");
        mnItmUnInst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnItmUnInstActionPerformed(evt);
            }
        });
        mItmFile.add(mnItmUnInst);
        mItmFile.add(mnItmSep01);

        mnItmProxy.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        mnItmProxy.setText("Proxy");
        mnItmProxy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnItmProxyActionPerformed(evt);
            }
        });
        mItmFile.add(mnItmProxy);

        mnItmLog.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        mnItmLog.setText("Log");
        mnItmLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnItmLogActionPerformed(evt);
            }
        });
        mItmFile.add(mnItmLog);
        mItmFile.add(mnItmSep02);

        mItmExit.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        mItmExit.setText("Exit");
        mItmExit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mItmExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mItmExitActionPerformed(evt);
            }
        });
        mItmFile.add(mItmExit);

        mnuBase.add(mItmFile);

        mnuHelp.setText("Help");
        mnuHelp.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N

        mnuItmAbout.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        mnuItmAbout.setText("About");
        mnuItmAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItmAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mnuItmAbout);

        mnuBase.add(mnuHelp);

        setJMenuBar(mnuBase);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(svtMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(svtMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mItmExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mItmExitActionPerformed
        // Danny John Britto
        // colapse the frame
        dispose();
    }//GEN-LAST:event_mItmExitActionPerformed

    private void mnuItmConfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmConfigureActionPerformed
        // Calling Configuration Plugin JFrame
        /*
         * old calling process
         * new svtConfPlugin(this).show(true);
         */
        this.setVisible(false);
        if(objFileUtil.isOvcInfoPresent() == false) {
            new svtDlgNodeInfo(this, true, this, (short)1).show();
        } else {
            /*
            System.out.println("OVC IP      : " + objFileUtil.getOvcIPAddress());
            System.out.println("OVC UserId  : " + objFileUtil.getOvcUserId());
            System.out.println("OVC Password: " + objFileUtil.getOvcPassword());
            */
            new svtConfPlugin(this).show(true);
        }
        
    }//GEN-LAST:event_mnuItmConfigureActionPerformed

    private void mnItmLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnItmLogActionPerformed
        //LogMonitor JFrame
        new svtLogMonitor(this, objFileUtil.getLogFilePath()).show(true);
    }//GEN-LAST:event_mnItmLogActionPerformed

    private void btOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btOKActionPerformed
        
        // We need to write code to remove all un-attended ".psd1" files
        // in the working directory, so that there will not be any problem
        // next time user uses this application
        
        // at the end of the application we need to find out any 
        // input file left out in the directory, if yes we may need to 
        // delete those files before exiting from the main class
        objFileUtil.resetPSInputFile();
        this.dispose();
    }//GEN-LAST:event_btOKActionPerformed

    private void mnItmUnInstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnItmUnInstActionPerformed
        // Deconfiguration Plugin JFrame
        /*
         * old calling process
         * new svtDeConfPlugin(this).show(true);
        */
        this.setVisible(false);
        if(objFileUtil.isOvcInfoPresent() == false) {
            new svtDlgNodeInfo(this, true, this, (short)0).show();
        } else {
            new svtDeConfPlugin(this).show(true);
        }
    }//GEN-LAST:event_mnItmUnInstActionPerformed

    private void mnuItmAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItmAboutActionPerformed
        // About JDialog 
        final javax.swing.ImageIcon icon = 
                new javax.swing.ImageIcon(objFileUtil.getIconPath());
        javax.swing.JOptionPane.showMessageDialog(this, 
                    " HPE SimpliVity 380 Hyper-V Stack ", 
                    "HPE SimpliVity ", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE,icon);
    }//GEN-LAST:event_mnuItmAboutActionPerformed

    private void mnItmProxyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnItmProxyActionPerformed
        // Proxy Dialog
        this.setVisible(false);
        new svtDlgProxyInfo(this,true, this).show(true);
    }//GEN-LAST:event_mnItmProxyActionPerformed
    
    /*
    public javax.swing.JMenu pMnuFile;
    private svtPlugUtility objPlugUtility = null;
    */
    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btOK;
    private javax.swing.JLabel imgLabel;
    private javax.swing.JMenuItem mItmExit;
    private javax.swing.JMenu mItmFile;
    private javax.swing.JMenuItem mnItmLog;
    private javax.swing.JMenuItem mnItmProxy;
    private javax.swing.JPopupMenu.Separator mnItmSep01;
    private javax.swing.JPopupMenu.Separator mnItmSep02;
    private javax.swing.JMenuItem mnItmUnInst;
    private javax.swing.JMenuBar mnuBase;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JMenuItem mnuItmAbout;
    private javax.swing.JMenuItem mnuItmConfigure;
    private javax.swing.JPanel svtMainPanel;
    // End of variables declaration//GEN-END:variables
}

