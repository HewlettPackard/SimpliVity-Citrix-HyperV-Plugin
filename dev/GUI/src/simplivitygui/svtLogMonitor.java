/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author  Danny John Brito
 * @Date    06/04/2018
 * @version v0.1
 */

/**
 * svtLogHolder Class is a holder, where we can store string tokens which are
 * parsed from the LogFile from the disk. The idea is to read the data from the 
 * file once and keep it in the memory for rest of the life..
 * As of now, sLogHost is not implemented, but it's certainly easy to implement if
 * there is a need. C++ I could have easily lived with the structure which is much
 * more comfortable...
 */
class svtLogHolder {
    // dummy constructor
    public svtLogHolder() {};
    // caller would call this constructor with 3 parameters
    // to initialze the class to pack it to the data structure
    // which can be used later to add it on the Table
    public svtLogHolder(String sLogDate, String sLogType,
                        String sLogDesc){
        // assignment of String objects
        this.sLogDate = sLogDate;
        this.sLogType = sLogType;
        this.sLogDesc = sLogDesc;
        // this validation helps lot of other unnecessary 
        // operations
        if ( (sLogDate.isEmpty() == false) &&
             (sLogType.isEmpty() == false) &&
             (sLogDesc.isEmpty() == false))
                isValid = true;
        // not required at this point
        // this.sLogHost = "";
    };
    
    // public methods for the caller to verify the values
    // if there is no value, then we need not to add it into the
    // JTable
    public boolean isValid() { return isValid; };
    public String getLogDate() { return sLogDate; };
    public String getLogType() { return sLogType; };
    public String getLogDesc() { return sLogDesc; };
    public String getLogHost() { return sLogHost; };
    
    // Private Variable
    private String sLogDate = null;
    private String sLogType = null;
    private String sLogDesc = null;
    private String sLogHost = null;
    private boolean isValid = false;
};

/**
 * svtLogMonitor - is the class which extends from a JFrame object as the parent 
 * class. This JFrame GUI displays the log string reading from the storage.. There 
 * are 2 different kind of logs at this point.
 * 1. ERROR LOG -> The severity of this type is quite high and useful to the Administrator
 *                  for troubleshooting the plugin problem.
 * 2. APP LOG -> This is just an information for the user what all operations been
 *               success (default option - the log content will be displayed at the startup).
 * 
 */

public class svtLogMonitor extends javax.swing.JFrame {

    /**
     * Creates new form svtLogMonitor
     * The below constructor may not be required to call this point
     * the other constructor with JMenu Parameter will be called
     */
    public svtLogMonitor() {
        initComponents();
        /* Not Required at this point - Danny J
            makeItCentred();
        */
    }
    
    private boolean svtLogFileParser(String sFileName) throws java.io.IOException {
        
        if (sFileName.isEmpty()) return false;
        
        java.io.File iFile = new java.io.File(sFileName);
        java.io.FileReader iFileRead = new java.io.FileReader(iFile);
        java.io.BufferedReader bFileRead = new java.io.BufferedReader(iFileRead);
        
        lLogHolder = new java.util.ArrayList<>();
        String sLine  = null;
        while ((sLine = bFileRead.readLine())!=null)
        {
            java.util.StringTokenizer sToken = new java.util.StringTokenizer(sLine,"|");
            if ( (sToken.countTokens() < 4)) continue;
            String sDate = "  " + sToken.nextToken() + "  ";
            String sHost = sToken.nextToken();
            String sType = "  " + sToken.nextToken() + "  ";
            String sDesc = "  " + sToken.nextToken();
            if( (sDate.isEmpty() == false) && 
                (sType.isEmpty() == false) && 
                (sDesc.isEmpty() == false))
                lLogHolder.add(new svtLogHolder(sDate, sType, sDesc));
        }   
        bFileRead.close();
        iFileRead.close();
        
        return true;
   };

    private void displayMessage(String sMsg, int nType) {
        
        javax.swing.JOptionPane.showMessageDialog(this, sMsg, 
                                    "SimpliVity Citrix Cloud Plugin", nType);
        return;
    }
    
    private void prepareLogTable() {
    
        tblLogMonitor.setAutoResizeMode( javax.swing.JTable.AUTO_RESIZE_OFF );
        
        // Align the header text to LEFT justification
        javax.swing.table.DefaultTableCellRenderer render = 
                (javax.swing.table.DefaultTableCellRenderer) 
                tblLogMonitor.getTableHeader().getDefaultRenderer();
        
        // make Header text as BOLD
        tblLogMonitor.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        render.setHorizontalAlignment(javax.swing.JLabel.LEFT);
        
        final javax.swing.table.TableColumnModel columnModel = tblLogMonitor.getColumnModel();
        for (int column = 0; column < tblLogMonitor.getColumnCount(); column++) {
            int width = 15; // Min width
        for (int row = 0; row < tblLogMonitor.getRowCount(); row++) {
            javax.swing.table.TableCellRenderer renderer = tblLogMonitor.getCellRenderer(row, column);
            java.awt.Component comp = tblLogMonitor.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width +1 , width);
            //comp.setForeground(Color.red);
        }
        columnModel.getColumn(column).setPreferredWidth(width);
    }
}
    
    //public svtLogMonitor( javax.swing.JMenu mnuFile, String sLogFileName) {
    public svtLogMonitor( svtBaseFrame bsFrame, String sLogFileName) {
        
        super("SimpliVity Citrix Cloud Connector Plugin");
        // Initialize all the components
        initComponents();
        // make the JFrame Centred in the Screen
        /*
        makeItCentred();
        */
        objFileUtil.makeMeCentred(this);
        // Get the top level Menu File Component, so that when this class 
        // gets defocused, the menu item can be enabled back. It was already
        // disabled before control reaches here
        //this.mnuFile = mnuFile;
        this.baseParent = bsFrame;
        bsFrame.setVisible(false);
       // Default check "AllLog"
        rdoAllLog.setSelected(true);
        
        try {
           svtLogFileParser(sLogFileName);
        } catch (Exception eIOException) {
            
            displayMessage((sLogFileName + ": " + "FileParsing Exception"),
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            /*
            JOptionPane.showMessageDialog(this, "Error Occured in LogFileParser");
            */
            return;
        };
        setTableRow(true);
        prepareLogTable();
    }    
    
    private void setTableRow( boolean isAll) {

        // Get the object of DataModel
        javax.swing.table.DefaultTableModel tblModel = 
                      (javax.swing.table.DefaultTableModel)tblLogMonitor.getModel();
        // Reset the Table as 0, then you can paste the content again.
        tblModel.setRowCount(0);
        lLogHolder.stream().forEach((str)->{
            // Add it to Log Monitor Table 
            Object[] tblRow = { str.getLogDate(), 
                                str.getLogType(), 
                                str.getLogDesc() 
                              };
            /*
            javax.swing.table.DefaultTableModel tblModel = 
                       (javax.swing.table.DefaultTableModel)tblLogMonitor.getModel();
            */
            // We need to check whether the type is APPLOG or ERRLOG
            if ( (isAll == false && str.getLogType().compareTo("  ERRLOG  ") == 0 )) 
                tblModel.addRow(tblRow);
            else if( isAll == true )
                tblModel.addRow(tblRow);
            else
                ; // intension
        });  
    };

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLogMonitor = new javax.swing.JPanel();
        scrLogMonitor = new javax.swing.JScrollPane();
        tblLogMonitor = new javax.swing.JTable();
        btLogOK = new javax.swing.JButton();
        rdoErrLog = new javax.swing.JRadioButton();
        rdoAllLog = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);

        pnlLogMonitor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tblLogMonitor.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblLogMonitor.setForeground(new java.awt.Color(33, 61, 28));
        tblLogMonitor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "               Date   ", "   Type   ", "                             Log Description   "
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLogMonitor.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblLogMonitor.setColumnSelectionAllowed(true);
        tblLogMonitor.setFillsViewportHeight(true);
        tblLogMonitor.getTableHeader().setReorderingAllowed(false);
        scrLogMonitor.setViewportView(tblLogMonitor);
        tblLogMonitor.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout pnlLogMonitorLayout = new javax.swing.GroupLayout(pnlLogMonitor);
        pnlLogMonitor.setLayout(pnlLogMonitorLayout);
        pnlLogMonitorLayout.setHorizontalGroup(
            pnlLogMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogMonitorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrLogMonitor, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlLogMonitorLayout.setVerticalGroup(
            pnlLogMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogMonitorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrLogMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btLogOK.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btLogOK.setText("OK");
        btLogOK.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btLogOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLogOKActionPerformed(evt);
            }
        });

        rdoErrLog.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rdoErrLog.setText("Error Log");
        rdoErrLog.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rdoErrLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoErrLogActionPerformed(evt);
            }
        });

        rdoAllLog.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        rdoAllLog.setText("All Log");
        rdoAllLog.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rdoAllLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdoAllLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rdoAllLog, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rdoErrLog)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btLogOK, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlLogMonitor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btLogOK, rdoAllLog, rdoErrLog});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlLogMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rdoErrLog, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(rdoAllLog, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btLogOK, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btLogOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLogOKActionPerformed
        // TODO add your handling code here:
        // Before we de-focused from this JFrame, we need to 
        // enable the focus on FILE Menu in the parent JFrame so 
        // that regular operation continues...
        //this.mnuFile.setEnabled(true);
        //baseParent.setLogMonitorState(false);
        baseParent.setVisible(true);
        //baseParent.setEnabled(true);
        // Dispose the current JFrame
        this.dispose();
    }//GEN-LAST:event_btLogOKActionPerformed

    private void rdoErrLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoErrLogActionPerformed
        // TODO add your handling code here:
        // We need to check whether other radio button is checked, if yes
        // we need to uncheck it - Stupid way of programming need to be
        // grouped like Microsoft
        if ( rdoAllLog.isSelected() ) rdoAllLog.setSelected(false);
        setTableRow(false);    
        
    }//GEN-LAST:event_rdoErrLogActionPerformed

    private void rdoAllLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdoAllLogActionPerformed
        // TODO add your handling code here:
        // We need to check whether other radio button is checked, if yes
        // we need to uncheck it - Stupid way of programming need to be
        // grouped like Microsoft
        if ( rdoErrLog.isSelected() ) rdoErrLog.setSelected(false);
        // Log Syntax
        // [date][hostname][type][description]
        // Apr 6 12:30:50 GSE-01 ERRLOG Domain Name/User Credentials are wrong
        // Apr 6 12:31:20 GSE-01 APPLOG Domain Joined Successfully
        setTableRow(true);
    }//GEN-LAST:event_rdoAllLogActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLogOK;
    private javax.swing.JPanel pnlLogMonitor;
    private javax.swing.JRadioButton rdoAllLog;
    private javax.swing.JRadioButton rdoErrLog;
    private javax.swing.JScrollPane scrLogMonitor;
    javax.swing.JTable tblLogMonitor;
    // End of variables declaration//GEN-END:variables
    // Added by GSE
    // Private Variable for the Parent File Menu to Enable
    // because it was disabled when we focused on LogMonitor Frame
    //private javax.swing.JMenu mnuFile;
    private svtBaseFrame baseParent;
    private java.util.List<svtLogHolder> lLogHolder;
    
    //private String sLogFileName ="";
    private String sColDate = "Date";
    private String sColType = "Type";
    private String sColDesc = "Log Description";
    private int nColWidth = 130;
    
    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
}
