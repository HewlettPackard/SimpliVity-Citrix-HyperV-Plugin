/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;


class PowerShellExecutor extends Thread {
    
    PowerShellExecutor( String sPSScriptName,
                        String sPsScrInFile,
                        javax.swing.JTextArea txConsole,
                        javax.swing.JProgressBar pbConsole,
                        javax.swing.JButton btConsole,
                        boolean lastThread ) {
        
        // TextArea variable for pasting the output
        mTxConsole = txConsole; 
        // ProgressBar to indicate the progress
        mPbConsole = pbConsole;
        // Button to be disabled initially, once the 
        // operationg is done, it will be re-enabled
        mBtConsole = btConsole;
        // Assign the PowerShell Script
        this.mPSScriptName = sPSScriptName;
        this.mPsScrInFile  = objFileUtil.getConfPath() +  
                             sPsScrInFile + ".psd1";
        
        // modified to run path with spaces, let see whether this works, if not
        // we need to change the logic
        /*
        this.mPsScrInFile = "\"'" + objFileUtil.getConfPath() + sPsScrInFile +
                            ".psd1" + "\"'";
        */
        this.mVmName = sPsScrInFile;
        this.mLastThread = lastThread;
    }
    
    private void resetControls() {
        mPbConsole.setIndeterminate(false);
        // check if any error, then we need to set error:q
        //mPbConsole.setString("External Process Done...");
        mPbConsole.setValue(100);
        mPbConsole.setStringPainted(true);
        return;
    }

    public void run(){
        
        // Thread starting point where PowerShell process
        // is executed in a different thread from GUI thread
        // it executes PowerShell and get the stream out/error
        // and paste it on GUI console
        synchronized(this) {
            try{
                mTxConsole.append(mVmName + " => Thread Started...\n");
                // Make the string and send it to PowerShell script to 
                // get user inputs and execute the process, as of now, the user input 
                // is written to a file and filename will be send to PowerShell Script.
                // We need to modify the logic later, to pass all the parameters thru 
                // memory not thru file - Danny John Britto
                /*
                String sCommand = "powershell.exe " +  mPSScriptName +  " -inputfile " 
                                  + "\"'" + mPsScrInFile + "'\"" 
                                  + " -logfile " + "\"'" + objFileUtil.getLogFilePath()
                                  + "'\""; 
                */
                String sCommand = "powershell.exe \"" +  mPSScriptName + "\"" +
                                  " -inputfile " + "\"" + "\'" + mPsScrInFile + 
                                  "\'" + "\""  + " -logfile " + "\"" + "\'" +
                                  objFileUtil.getLogFilePath() + "\'" + "\""; 
                
                // logmessage
                objFileUtil.logMessage(0, "Command: " + sCommand);
                // Executing the command
                Process powerShellProcess = Runtime.getRuntime().exec(sCommand);
                // Getting the results
                powerShellProcess.getOutputStream().close(); 
                
                String line = "";
                java.io.BufferedReader stdout = new java.io.BufferedReader( 
                            new java.io.InputStreamReader(powerShellProcess.getInputStream()) );

                while ((line = stdout.readLine()) != null) {
                    mTxConsole.append(line + "\n");
                    mTxConsole.setCaretPosition(mTxConsole.getDocument().getLength());
                    //System.out.println(line);
                }
                stdout.close();

                java.io.BufferedReader stderr = new java.io.BufferedReader( 
                            new java.io.InputStreamReader( powerShellProcess.getErrorStream()) );

                while ((line = stderr.readLine()) != null) {
                    mTxConsole.append(line + "\n");
                    mTxConsole.setCaretPosition(mTxConsole.getDocument().getLength());
                    //System.out.println(line);
                }

                stderr.close();
                mPbConsole.setString("Configuration Process Completed...");
            } catch(java.io.IOException eIOException) {
                objFileUtil.logMessage(1, "Exception at PowerShell Thread with VM: " +
                                            mVmName);
                mTxConsole.append(mVmName + " => Thread Terminated with Exception " +
                                             eIOException.toString() + "\n");
                mPbConsole.setString("Exception at Configuration Process...");
                objFileUtil.setThreadRunStatus(mVmName, false);
                return;
            } finally {
                // We need to re-enable Button
                mBtConsole.setEnabled(true);
                try {
                    //System.out.println("FileName: " + mPsScrInFile);
                    // need to uncomment
                    java.io.File file = new java.io.File(mPsScrInFile);
                    if ( file.isFile() ) file.delete();
                }catch(Exception ex) {
                    objFileUtil.logMessage(1, "PowerShell input file exception");
                    mTxConsole.append(ex.toString());
                    mPbConsole.setString("Exception at Configuration Process...");
                    return;
                }
                resetControls();
            }
        }
    }
    
    String mPSScriptName = "";
    String mPsScrInFile = "";
    String mVmName = "";
    boolean mLastThread = false;
    // FileUtil Class
    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
    // JTextArea Console output window
    private javax.swing.JTextArea mTxConsole;
    // JProgressBar for showing progress
    javax.swing.JProgressBar      mPbConsole;
    // OK button, and will be enabled once the
    // process is done
    private javax.swing.JButton   mBtConsole;
}

/**
 *
 * @author johnbrit
 */
public class svtPSoutput extends javax.swing.JFrame {

    /**
     * Creates new form frPowerShellOutput
     */
    // This is normal constructor and can be called
    // either by Configuration Script or DeConfiguration
    // Script at any given point of time, also another requirement
    // is that we can pass the vector as a parameter and fork 
    // multiple threads
    public svtPSoutput( javax.swing.JFrame fParent,
                        String sPSScriptName,
                        String[] sPsScrFileName ) {
        
        // We need to implement this later
        // TODO:
        // if multiple VMs are given to you, then we need to 
        // fork thread which are equal of number of VMs and start
        // creating and adding to Citrix Cloud URI
        super("SimpliVity Citrix Cloud Connector Plugin");
        initComponents();
        objFileUtil.makeMeCentred(this);
        this.mParentFrame     = fParent;
        this.mArPsScrFileName = sPsScrFileName;
        this.mPSScriptName    = sPSScriptName;
        callPowerShellThread();
    }
    
    public svtPSoutput(javax.swing.JFrame fParent, 
                       String sPSScriptName) {
        
        super("SimpliVity Citrix Cloud Connector Plugin");
        initComponents();
        objFileUtil.makeMeCentred(this);
        this.mParentFrame  = fParent;
        this.mPSScriptName = sPSScriptName;
        callPowerShellThread();
    }
    
    private void callPowerShellThread() {
        // The below code is to configure citrix cloud plugin
        // thread will be run only once, if there are errors
        // then it just comes out and do nothing
        //try {
        txPSCmdOutput.append("Invoking External Powershell Process...\n");
        objFileUtil.logMessage(0, "PowerShell invoked with File: " + 
                                this.mPSScriptName );
        //getPowerShellOutput();
        // set text on the progress bar, so that the user can understand 
        // something is happening at the system
        pBPSCmdOutput.setString("Configuration Process InProgress...");
        // Since we do not know how long the script is going to take, we are setting
        // indeterminate state, so that the progress bar would be keep going right to left and 
        // vise versa...
        pBPSCmdOutput.setIndeterminate(true);
        // disable the button, so that user cannot take any action during citrix 
        // configuration task
        btnOK.setEnabled(false);
        // TODO - XXX
        // We need to create threads based on how many VMs added to the list
        // also we need to validate how many threads can be synchronized
        // request help from SimpliVity BU to do so
        try {
            for ( int ix = 0; ix < mArPsScrFileName.length; ix++ ) {
                // creating threads
                boolean lastThread = false;
                if ( ix == mArPsScrFileName.length ) lastThread = true;
                new PowerShellExecutor(this.mPSScriptName, this.mArPsScrFileName[ix], 
                                       txPSCmdOutput, pBPSCmdOutput, btnOK, 
                                       lastThread).start();
            }    
        }catch(Exception exGeneral) {
            txPSCmdOutput.append("Thread Exception at Configuration Process...");
            objFileUtil.logMessage(1, "Unhandled Exception at Configuration Process");
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPSCmdOutput = new javax.swing.JPanel();
        spPSCmdOutput = new javax.swing.JScrollPane();
        txPSCmdOutput = new javax.swing.JTextArea();
        btnOK = new javax.swing.JButton();
        pBPSCmdOutput = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("frPSCommand"); // NOI18N
        setResizable(false);

        pnlPSCmdOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Citrix Cloud Configuration Console...", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        spPSCmdOutput.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        spPSCmdOutput.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        spPSCmdOutput.setAutoscrolls(true);

        txPSCmdOutput.setEditable(false);
        txPSCmdOutput.setBackground(new java.awt.Color(0, 0, 0));
        txPSCmdOutput.setColumns(20);
        txPSCmdOutput.setFont(new java.awt.Font("Monospaced", 1, 13)); // NOI18N
        txPSCmdOutput.setForeground(new java.awt.Color(255, 255, 255));
        txPSCmdOutput.setLineWrap(true);
        txPSCmdOutput.setRows(5);
        txPSCmdOutput.setTabSize(4);
        txPSCmdOutput.setWrapStyleWord(true);
        txPSCmdOutput.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        txPSCmdOutput.setEnabled(false);
        spPSCmdOutput.setViewportView(txPSCmdOutput);

        btnOK.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnOK.setText("OK");
        btnOK.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        pBPSCmdOutput.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pBPSCmdOutput.setBorderPainted(false);
        pBPSCmdOutput.setStringPainted(true);

        javax.swing.GroupLayout pnlPSCmdOutputLayout = new javax.swing.GroupLayout(pnlPSCmdOutput);
        pnlPSCmdOutput.setLayout(pnlPSCmdOutputLayout);
        pnlPSCmdOutputLayout.setHorizontalGroup(
            pnlPSCmdOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnlPSCmdOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPSCmdOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPSCmdOutputLayout.createSequentialGroup()
                        .addComponent(pBPSCmdOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(spPSCmdOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPSCmdOutputLayout.setVerticalGroup(
            pnlPSCmdOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPSCmdOutputLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(spPSCmdOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPSCmdOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pBPSCmdOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlPSCmdOutputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnOK, pBPSCmdOutput});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPSCmdOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPSCmdOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed

        // Before disposing the frame, we need to do couple of things
        // 1. check which are all thread passed, and create a persistent 
        //    storage
        // 2. we need to set the super parent visible
        // 3. write all the console information to file, so that we can have the
        //    trace of whats happening
        //objFileUtil.writeToFile(objFileUtil.getVmInfoFilePath(), sVmInfo);
        try {
            // To preserve the content of successful Threads
            objFileUtil.writeToFile(objFileUtil.getVmInfoFilePath());
            // To preserve the content of Console output
            objFileUtil.writeToConsoleFile(objFileUtil.getConsoleFilePath(), txPSCmdOutput);
        } catch(Exception ex) {
            objFileUtil.logMessage(1, "File Exception : " + ex.toString());
        }
        
        this.dispose();
        mParentFrame.setVisible(true);
    }//GEN-LAST:event_btnOKActionPerformed
    /**
     * @param args the command line arguments
     */
    String mPSScriptName = "";
    String[] mArPsScrFileName = null;
    javax.swing.JFrame mParentFrame = null;
    svtFileUtil objFileUtil = svtFileUtil.getInstance();
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOK;
    private javax.swing.JProgressBar pBPSCmdOutput;
    private javax.swing.JPanel pnlPSCmdOutput;
    private javax.swing.JScrollPane spPSCmdOutput;
    private javax.swing.JTextArea txPSCmdOutput;
    // End of variables declaration//GEN-END:variables
}
