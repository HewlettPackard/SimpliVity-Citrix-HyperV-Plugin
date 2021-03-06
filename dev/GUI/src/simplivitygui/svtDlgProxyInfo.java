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
public class svtDlgProxyInfo extends javax.swing.JDialog {

    /**
     * Creates new form svtDlgProxyInfo
     */
    public svtDlgProxyInfo(java.awt.Frame parent, boolean modal, svtBaseFrame pParent) {
        super(parent, modal);
        initComponents();
        objFileUtil.makeMeCentred(this);
        this.objParent = pParent;
    }

    // Message Dialog to alert user
    private void displayMessage(String sMsg, int nType) {
    
        javax.swing.JOptionPane.showMessageDialog(this, sMsg, 
                        "SimpliVity Citrix Cloud Plugin", nType);
        return;
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlProxyInfo = new javax.swing.JPanel();
        lblURL = new javax.swing.JLabel();
        lblUserId = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        lblPort = new javax.swing.JLabel();
        txtURL = new javax.swing.JTextField();
        txtUserId = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtPort = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlProxyInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Proxy Information"));

        lblURL.setText("Proxy URL *");

        lblUserId.setText("User ID ");

        lblPassword.setText("Password ");

        lblPort.setText("Port *");

        btnOK.setText("OK");
        btnOK.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        txtPort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPortFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlProxyInfoLayout = new javax.swing.GroupLayout(pnlProxyInfo);
        pnlProxyInfo.setLayout(pnlProxyInfoLayout);
        pnlProxyInfoLayout.setHorizontalGroup(
            pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProxyInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPassword)
                    .addComponent(lblUserId)
                    .addComponent(lblURL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlProxyInfoLayout.createSequentialGroup()
                        .addComponent(lblPort)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)))
                .addContainerGap())
        );

        pnlProxyInfoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtPassword, txtURL, txtUserId});

        pnlProxyInfoLayout.setVerticalGroup(
            pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlProxyInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblURL)
                    .addComponent(txtURL, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPort)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlProxyInfoLayout.createSequentialGroup()
                        .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUserId)
                            .addComponent(txtUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlProxyInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlProxyInfoLayout.createSequentialGroup()
                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlProxyInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnOK, txtURL, txtUserId});

        pnlProxyInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancel, txtPassword});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlProxyInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlProxyInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        // The value of URL and the Port cannot be NULL
        // The user ID and the Password can be NULL
        if(txtURL.getText().length()  == 0 || txtPort.getText().length() == 0 ) {
            // raise a message dialog to alert the user it cannot be null
            objFileUtil.logMessage(1, "Required(*) fields cannot be empty");
            displayMessage("Required(*) fields cannot be empty", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // required fields are populated
        // lets update the information at FileUtil object, so that
        // the same can be used later while creating a file for calling
        // powershell script
        objFileUtil.setProxyPassword(txtPassword.getText());
        objFileUtil.setProxyUserName(txtUserId.getText());
        objFileUtil.setProxyPort(txtPort.getText());
        objFileUtil.setProxyURL(txtURL.getText());
        // lets dispose the Dialog
        this.dispose();
        objParent.setVisible(true);
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
        objParent.setVisible(true);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPortFocusLost
        // We need to check whether the value entered is only
        // numeric or not, if not the focus will be set back to
        // the same control, so that end user can reenter the
        // value
        try {
            Integer value = Integer.parseInt(txtPort.getText());
        } catch (NumberFormatException ex){
            txtPort.requestFocus();
        }
    }//GEN-LAST:event_txtPortFocusLost

    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
    private svtBaseFrame objParent = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblURL;
    private javax.swing.JLabel lblUserId;
    private javax.swing.JPanel pnlProxyInfo;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JFormattedTextField txtPort;
    private javax.swing.JTextField txtURL;
    private javax.swing.JTextField txtUserId;
    // End of variables declaration//GEN-END:variables
}
