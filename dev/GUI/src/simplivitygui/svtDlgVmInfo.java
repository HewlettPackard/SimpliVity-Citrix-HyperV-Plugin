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
public class svtDlgVmInfo extends javax.swing.JDialog {

    /**
     * Creates new form svtVMDialog
     */
    public svtDlgVmInfo(java.awt.Frame parent, 
                       javax.swing.JTable vmTable,
                       java.util.Vector vmArray,
                       boolean modal) {
        
        super(parent, modal);
        initComponents();
        objFileUtil.makeMeCentred(this);
        fillUpControlArray();
        this.mVMTable = vmTable;
        this.mVMInfoArray = vmArray;
        
        cmbCluster.removeAllItems();
        cmbHost.removeAllItems();
        
        cmbCluster.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtClusters()));
        
        /*
        cmbHost.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtHosts()));
        
        cmbHost.removeAllItems();
        cmbHost.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtHostsOfCluster("PlanoLocal.demo.local")));
        */
    }

    private void fillUpControlArray() {
    
        mControlArray[0] = txtVMName;
        mControlArray[1] = txtVMUserName;
        mControlArray[2] = txtVMPasswd;
    }
    
    private boolean validateInput() {
    
        for ( int ix = 0; ix < mControlSize; ix++ ) {
            if( mControlArray[ix].getText().isEmpty() == true ) {
                mControlArray[ix].requestFocus();
                return false;
            }
        }
        return true;
    }
     
    // just accept string variable and display it in the message dialog
    // along with appropriate Type.
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

        pnlVMDlg = new javax.swing.JPanel();
        lblVMName = new javax.swing.JLabel();
        lblVMUserName = new javax.swing.JLabel();
        lblVMPassword = new javax.swing.JLabel();
        lblHost = new javax.swing.JLabel();
        txtVMName = new javax.swing.JTextField();
        txtVMUserName = new javax.swing.JTextField();
        txtVMPasswd = new javax.swing.JPasswordField();
        cmbCluster = new javax.swing.JComboBox();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblHost1 = new javax.swing.JLabel();
        cmbHost = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SimpliVity Citrix Cloud Connector");
        setAlwaysOnTop(true);
        setModal(true);
        setName("dlgVM"); // NOI18N
        setResizable(false);

        pnlVMDlg.setBorder(javax.swing.BorderFactory.createTitledBorder("Virtual Machine Information"));

        lblVMName.setText("Machine Name *");

        lblVMUserName.setText("User Name *");

        lblVMPassword.setText("Password *");

        lblHost.setText("SimpliVity Cluster*");

        cmbCluster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClusterActionPerformed(evt);
            }
        });

        btnOK.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 12)); // NOI18N
        btnOK.setText("OK");
        btnOK.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 12)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblHost1.setText("SimpliVity Host *");

        cmbHost.setToolTipText("");

        javax.swing.GroupLayout pnlVMDlgLayout = new javax.swing.GroupLayout(pnlVMDlg);
        pnlVMDlg.setLayout(pnlVMDlgLayout);
        pnlVMDlgLayout.setHorizontalGroup(
            pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVMDlgLayout.createSequentialGroup()
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlVMDlgLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlVMDlgLayout.createSequentialGroup()
                                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblVMName)
                                    .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pnlVMDlgLayout.createSequentialGroup()
                                            .addGap(12, 12, 12)
                                            .addComponent(lblVMPassword))
                                        .addComponent(lblVMUserName)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtVMName)
                                    .addComponent(txtVMUserName)
                                    .addComponent(txtVMPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlVMDlgLayout.createSequentialGroup()
                                .addComponent(lblHost1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbHost, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlVMDlgLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(lblHost)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(0, 10, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlVMDlgLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pnlVMDlgLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbCluster, cmbHost, txtVMName, txtVMPasswd, txtVMUserName});

        pnlVMDlgLayout.setVerticalGroup(
            pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVMDlgLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVMName)
                    .addComponent(txtVMName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVMUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVMUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVMPassword)
                    .addComponent(txtVMPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbCluster, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHost))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbHost, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHost1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(pnlVMDlgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlVMDlgLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbCluster, cmbHost, txtVMName, txtVMPasswd, txtVMUserName});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlVMDlg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlVMDlg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // remove the dialog from the memory
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        // TODO:
        // 1. input validation
        // 2. verify whether the same VM name is already exist
        // 3. add those values to parent JTable
        if (validateInput() == false ) {
            displayMessage("Required(*) fields cannot be empty", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            objFileUtil.logMessage(1, "Required(*) fields cannot be empty");
            return;
        }    
       
        // The below validation is for SimpliVity Host
        // We need to validate with existing SimpliVity host, if the row 
        // is already available, if not we can add it as first row in the
        // table
        if ( mVMTable.getRowCount() > 0 ) {
            // if both VMs are requested to place it on the same SVT Host, then
            // HA is not going to work, so we need to force the user to place it
            // on multiple other Host, so that HA can be maintained
            for(int row = 0; row < mVMTable.getRowCount(); row++ ) {
                if ( cmbHost.getSelectedItem().toString().compareTo(
                     mVMTable.getValueAt(row, 3).toString()) == 0 ) {
                    // optionpane message
                    displayMessage("Only One Cloud Connector VM is allowed "
                                    + "per Host", 
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                    // logmessage
                    objFileUtil.logMessage(1, "Only One Cloud Connector VM is allowed "
                                    + "per Host");
                    return;
                }
            }    
        }
        
        // Now we need to set these values to JTable
        javax.swing.table.DefaultTableModel tblModel = 
                      (javax.swing.table.DefaultTableModel)mVMTable.getModel();
       
        int ix = mVMTable.getRowCount();
        tblModel.addRow(new Object[0]);
        tblModel.setValueAt(true, ix, 0);
        tblModel.setValueAt(txtVMName.getText(),                   ix, 1);
        tblModel.setValueAt(txtVMUserName.getText(),               ix, 2);
        tblModel.setValueAt(cmbHost.getSelectedItem().toString(),  ix, 3);
        // We also need to store it in the vector for futher use
        svtVmInfo vmInfo = 
                new svtVmInfo(txtVMName.getText(),
                              txtVMUserName.getText(),
                              txtVMPasswd.getText(),
                              cmbHost.getSelectedItem().toString());
        mVMInfoArray.addElement(vmInfo);

        this.dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void cmbClusterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClusterActionPerformed
        // TODO add your handling code here:        
        if (cmbCluster.getSelectedItem() != null) {
            cmbHost.removeAllItems();
            cmbHost.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtHostsOfCluster(cmbCluster.getSelectedItem().toString())));
        }
    }//GEN-LAST:event_cmbClusterActionPerformed
    
    // Private variables
    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
    private final int mControlSize = 3;
    private javax.swing.JTextField[] mControlArray = 
                new javax.swing.JTextField[mControlSize]; 
    
    private javax.swing.JTable mVMTable = null;
    private java.util.Vector mVMInfoArray = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cmbCluster;
    private javax.swing.JComboBox cmbHost;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblHost1;
    private javax.swing.JLabel lblVMName;
    private javax.swing.JLabel lblVMPassword;
    private javax.swing.JLabel lblVMUserName;
    private javax.swing.JPanel pnlVMDlg;
    private javax.swing.JTextField txtVMName;
    private javax.swing.JPasswordField txtVMPasswd;
    private javax.swing.JTextField txtVMUserName;
    // End of variables declaration//GEN-END:variables
}
