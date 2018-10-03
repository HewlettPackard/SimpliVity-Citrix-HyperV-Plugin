/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplivitygui;

import java.awt.Font;
import java.io.File;
import javax.swing.SwingConstants;

/**
 *
 * @author Danny John Britto
 * @version 1.0
 * @description
 *      Configuration Frame, which accepts User Inputs, and find out SimpliVity 
 *      Cluster nodes dynamically, also, once the input is validated then it 
 *      passes those inputs to PowerShell script, and invokes the same
 */

public class svtConfPlugin extends javax.swing.JFrame {

    /**
     * Creates new form svtConfPlugin
     */
    public svtConfPlugin() {
        initComponents();
    }

    public svtConfPlugin(svtBaseFrame bsFrame) {
        
        // calling base class to set the FrameName
        super("SimpliVity Citrix Cloud Connector Plugin");
        // initialize all the GUI components
        initComponents();
        // We need to make the form centre in the screen
        //makeItCentred();
        objFileUtil.makeMeCentred(this);
        // This is required for manipulating current Frame and
        // Base Frame
        bsParentFrame = bsFrame;
        bsFrame.setVisible(false);
        btnPlugConf.setEnabled(false);
        //The below code needs to be removed later, this has been
        // given for Citrix Submit
        setCustomTableModel();
        makeControlArray();
        //cmbSimpCluster.removeAllItems();
        cmbImageTemplate.removeAllItems();
        cmbSvtNetwork.removeAllItems();
        
        //cmbSimpCluster.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtClusters()));
        cmbImageTemplate.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtTemplates()));
        
        if (objFileUtil.getSvtNetworks() != null && objFileUtil.getSvtNetworks().length > 0) {
            cmbSvtNetwork.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtNetworks()));
        }
        // set the values to the controls
        for(int ix = 0; ix < 7; ix++)
            controlText[ix].setText(controlValues[ix]);
    }
    
    private void setCustomTableModel() {
        
        // Following is the customer table model, where first column of the table
        // is CheckBox which always returns either true or false, once this
        // tablemodel is added then rest can be as it is.
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel()
        {
          public Class<?> getColumnClass(int column)
          {
            switch(column)
            {
                case 0: // first column in the table
                  return Boolean.class;
                case 1: // rest columns are objects
                  return Object.class;
                case 2:
                    return Object.class;
                case 3:
                    return Object.class;
                case 4:
                    return Object.class;
                default:
                    return Object.class;
            }
          }
          public boolean isCellEditable(int row, int column){ 
              if( column == 0 ) return true;
              return false;}
        };
        tblVMInfo.setModel(model);
        model.addColumn("->");
        model.addColumn("Machine Name");
        model.addColumn("User Name");
        model.addColumn("SimpliVity Host");
        //prepareVMTable();
        // setting Table Header Font as BOLD
        tblVMInfo.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        // Make each cell alignment as CENTER
        javax.swing.table.DefaultTableCellRenderer centerRenderer = 
                            new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        tblVMInfo.setDefaultRenderer(Object.class, centerRenderer);
        
        // set each column initial size
        javax.swing.table.TableColumn tRowSelect = tblVMInfo.getColumn("->");
        javax.swing.table.TableColumn tUsrName   = tblVMInfo.getColumn("User Name");
        javax.swing.table.TableColumn tMachName  = tblVMInfo.getColumn("Machine Name");
        javax.swing.table.TableColumn tSvtHost   = tblVMInfo.getColumn("SimpliVity Host");
        // Check box size
        tRowSelect.setMinWidth(30);
        tRowSelect.setMaxWidth(30);
        //User Name 
        tUsrName.setMinWidth(120);
        tUsrName.setMaxWidth(120);
        // Machine Name ( Virtual Machine)
        tMachName.setMinWidth(160);
        tMachName.setMaxWidth(160);
        // SVT Host Name
        tSvtHost.setMinWidth(160);
        tSvtHost.setMaxWidth(160);
        tblVMInfo.setAutoResizeMode( javax.swing.JTable.AUTO_RESIZE_OFF );
        return;
    }
    
    private void makeControlArray() {
        
        controlText[0]  = txtDomainUsr;
        controlText[1]  = txtDomainPasswd;
        controlText[2]  = txtDomainName;
        
        controlText[3]  = txtCloudUsrId;
        controlText[4]  = txtCloudPasswd;
        controlText[5]  = txtCldCustomerName; 
        controlText[6]  = txtCldRsrLocation;
        //controlText[8]  = txtImageName;      
        // controlText[3]  = txtSvtNetwork;  

        
        // Add corresponding values to the String Array
        // so that we can set the same later
        controlValues[0]  = objFileUtil.getDomainUserName();
        controlValues[1]  = objFileUtil.getDomainPassword();       
        controlValues[2]  = objFileUtil.getDomainName();
        
        controlValues[3]  = objFileUtil.getClientID();
        controlValues[4]  = objFileUtil.getClientKey();
        controlValues[5]  = objFileUtil.getCustomerName();
        controlValues[6]  = objFileUtil.getResourceLocation();
        //controlValues[3]  = objFileUtil.getSvtNetwork();        
        //[8]  = objFileUtil.getScVmmImage();
        
    }
    
    // just accept string variable and display it in the message dialog
    // along with appropriate Type.
    private void displayMessage(String sMsg, int nType) {
        javax.swing.JOptionPane.showMessageDialog(this, sMsg, 
                      "SimpliVity Citrix Cloud Plugin", nType);
        return;
    }
    
    private boolean isValidToProceed() {
    
        // The text controls are already in an array, and 
        // we can only validate the text input'ed in those 
        // controls, we can use the control array to validate
        // the same.
        for( int ix = 0; ix < 7; ix++ ) {
            if( true == (controlText[ix].getText().isEmpty())) {
                controlText[ix].requestFocus();
                return false;
            }
        }
        
        /*
        // We need to validate whether Virtual Machine 
        // Table has 2 VM information, if not we may need to
        // enforce the User to input the values
        if ( tblVMInfo.getRowCount() < 1 ) return false;
        */
        
        return true;
    }
    
    private boolean preparePSInputFile() throws java.io.IOException {

        //String sVmInfo = "";
        for ( int ix = 0; ix < mVVmInfo.size(); ix++ )
        {   
            // if the VM is not active, then we need not to create a file to configure
            // this has been done, after multiple VMs are added to the list
            svtVmInfo objInfo = (svtVmInfo)mVVmInfo.get(ix);
            if(objInfo.isVmActive() == false) continue;

            String sFileLine = "@{\r\nvm = \r\n\t@{\r\n";
            sFileLine += "\tname = " + "\'" + objInfo.getVMName()  + "\'\r\n" +
                        "\ttemplate = " + "\'" + cmbImageTemplate.getSelectedItem().toString() +"\'\r\n" +
                        "\tnetwork = " + "\'" + cmbSvtNetwork.getSelectedItem().toString() +"\'\r\n" +
                        "\thost = " + "\'" + objInfo.getVMSvtHost() +"\'\r\n" +
                        "\tusername =  " + "\'" + objInfo.getVMUserName() +"\'\r\n" +
                        "\tpassword = " + "\'" + objInfo.getVMPassword() +"\'\r\n \t}\r\n";
          
            sFileLine += "ad = \r\n\t@{\r\n";
            sFileLine += "\tdomain = " + "\'" + txtDomainName.getText() +"\'\r\n" +
                        "\tusername = " + "\'" + txtDomainUsr.getText() +"\'\r\n" +
                        "\tpassword = " + "\'" + txtDomainPasswd.getText() +"\'\r\n \t}\r\n";
            ((svtVmInfo)mVVmInfo.get(ix)).setDomainName(txtDomainName.getText());
            ((svtVmInfo)mVVmInfo.get(ix)).setDomainUser(txtDomainUsr.getText());
            // updated based on customer feedback about adding proxy stuff while 
            // downloading citrix plugin's from outside of the network
            sFileLine += "proxy = \r\n\t@{\r\n";
            sFileLine += "\tURL = " + "\'" + objFileUtil.getProxyURL() +"\'\r\n" +
                         "\tport = " + "\'" + objFileUtil.getProxyPort() +"\'\r\n" +
                         "\tusername = " + "\'" + objFileUtil.getProxyUserName() +"\'\r\n" +   
                         "\tpassword = " + "\'" + objFileUtil.getProxyPassword() +"\'\r\n \t}\r\n";
            // end proxy addition
            // Simplivity Info
            sFileLine += "ovc= \r\n\t@{\r\n";
            sFileLine += "\tipaddress= " + "\'" + objFileUtil.getOvcIPAddress() +"\'\r\n" +
                         "\tusername = " + "\'" + objFileUtil.getOvcUserId() +"\'\r\n" +   
                         "\tpassword = " + "\'" + objFileUtil.getOvcPassword() +"\'\r\n \t}\r\n";
            // End Simplivity Info
            // SCVMM Info
            sFileLine += "scvmm= \r\n\t@{\r\n";
            sFileLine += "\tipaddress= " + "\'" + objFileUtil.getScVmmIPAddress() +"\'\r\n" +
                         "\tport = " + "\'" + objFileUtil.getScVmmPort() +"\'\r\n \t}\r\n";
            // End SCVMM Info
            sFileLine += "citrix = \r\n\t@{\r\n";
            sFileLine += "\tclientId = " + "\'" + txtCloudUsrId.getText() +"\'\r\n" +
                        "\tclientKey = " + "\'" + txtCloudPasswd.getText() +"\'\r\n" +
                        "\tcustomerName = " + "\'" + txtCldCustomerName.getText() +"\'\r\n" +
                        "\tresourceLocation = " + "\'" + txtCldRsrLocation.getText() +
                        "\'\r\n \t}\r\n}\r";
            objFileUtil.writeToFile(objInfo.getVMName(), sFileLine, "ConfigPlugin");
        } // Loop
        return true;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDomainInfo = new javax.swing.JPanel();
        lblDomainUsr = new javax.swing.JLabel();
        txtDomainUsr = new javax.swing.JTextField();
        lblDomainPasswd = new javax.swing.JLabel();
        txtDomainPasswd = new javax.swing.JPasswordField();
        lblDomainName = new javax.swing.JLabel();
        txtDomainName = new javax.swing.JTextField();
        pnlCloudInfo = new javax.swing.JPanel();
        lblCloudUsrId = new javax.swing.JLabel();
        txtCldRsrLocation = new javax.swing.JTextField();
        lblCloudPasswd = new javax.swing.JLabel();
        txtCloudPasswd = new javax.swing.JPasswordField();
        lblCldCustName = new javax.swing.JLabel();
        lblCldRsrLocation = new javax.swing.JLabel();
        txtCloudUsrId = new javax.swing.JTextField();
        txtCldCustomerName = new javax.swing.JTextField();
        pnlGeneral = new javax.swing.JPanel();
        lblCloudURI = new javax.swing.JLabel();
        btnSvtNetwork = new javax.swing.JButton();
        cmbSvtNetwork = new javax.swing.JComboBox();
        lblISOLocation = new javax.swing.JLabel();
        cmbImageTemplate = new javax.swing.JComboBox<>();
        cbIncludeVM = new javax.swing.JCheckBox();
        pnlVirtual = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVMInfo = new javax.swing.JTable();
        btnAddVM = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnPlugConf = new javax.swing.JButton();
        btnConfReset = new javax.swing.JButton();
        btnConfCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setName("SimpliVity Cloud Connector Plugin"); // NOI18N
        setResizable(false);

        pnlDomainInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Domain Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        lblDomainUsr.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDomainUsr.setText("User Name*");

        txtDomainUsr.setFocusCycleRoot(true);

        lblDomainPasswd.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDomainPasswd.setText("Password*");

        lblDomainName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDomainName.setText("Domain Name*");

        javax.swing.GroupLayout pnlDomainInfoLayout = new javax.swing.GroupLayout(pnlDomainInfo);
        pnlDomainInfo.setLayout(pnlDomainInfoLayout);
        pnlDomainInfoLayout.setHorizontalGroup(
            pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDomainInfoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblDomainPasswd)
                    .addComponent(lblDomainUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomainName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtDomainPasswd, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDomainName, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDomainUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
        );
        pnlDomainInfoLayout.setVerticalGroup(
            pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDomainInfoLayout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDomainUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomainUsr))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDomainPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomainPasswd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDomainName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomainName))
                .addContainerGap())
        );

        pnlCloudInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Citrix Cloud Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        lblCloudUsrId.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCloudUsrId.setText("Client ID*");

        txtCldRsrLocation.setFocusCycleRoot(true);

        lblCloudPasswd.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCloudPasswd.setText("Client Key*");

        txtCloudPasswd.setFocusCycleRoot(true);

        lblCldCustName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCldCustName.setText("Customer Name*");

        lblCldRsrLocation.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCldRsrLocation.setText("Resource Location*");

        txtCloudUsrId.setFocusCycleRoot(true);

        txtCldCustomerName.setFocusCycleRoot(true);

        javax.swing.GroupLayout pnlCloudInfoLayout = new javax.swing.GroupLayout(pnlCloudInfo);
        pnlCloudInfo.setLayout(pnlCloudInfoLayout);
        pnlCloudInfoLayout.setHorizontalGroup(
            pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCloudInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCldCustName)
                    .addComponent(lblCldRsrLocation)
                    .addComponent(lblCloudPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCloudUsrId, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtCldCustomerName, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCloudPasswd, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCloudUsrId)
                    .addComponent(txtCldRsrLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        pnlCloudInfoLayout.setVerticalGroup(
            pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCloudInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCloudUsrId, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCloudUsrId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCloudPasswd, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCloudPasswd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCldCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCldCustName))
                .addGap(9, 9, 9)
                .addGroup(pnlCloudInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCldRsrLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCldRsrLocation))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        pnlCloudInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCldCustomerName, txtCldRsrLocation, txtCloudPasswd, txtCloudUsrId});

        pnlGeneral.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "SimpliVity Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        lblCloudURI.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCloudURI.setText("SimpliVity Network*");

        btnSvtNetwork.setText("Refresh");
        btnSvtNetwork.setToolTipText("Get latest Networks and Templates");
        btnSvtNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSvtNetworkActionPerformed(evt);
            }
        });

        lblISOLocation.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblISOLocation.setText(" Image Template*");

        cbIncludeVM.setText("Include VMs");
        cbIncludeVM.setToolTipText("Includes Simplivity VMs along with templates");
        cbIncludeVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIncludeVMActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblISOLocation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addComponent(lblCloudURI)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addComponent(cmbSvtNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSvtNetwork, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pnlGeneralLayout.createSequentialGroup()
                        .addComponent(cmbImageTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbIncludeVM, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pnlGeneralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbImageTemplate, cmbSvtNetwork});

        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCloudURI)
                        .addComponent(cmbSvtNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnSvtNetwork, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblISOLocation)
                    .addComponent(cmbImageTemplate, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbIncludeVM, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pnlGeneralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmbImageTemplate, cmbSvtNetwork});

        pnlVirtual.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Virtual Machine Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        tblVMInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblVMInfo.setFont(new java.awt.Font("Latin Modern Sans Quotation", 0, 13)); // NOI18N
        tblVMInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Machine Name", "User Name", "SimpliVity Host"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblVMInfo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblVMInfo.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblVMInfo.getTableHeader().setResizingAllowed(false);
        tblVMInfo.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblVMInfo);
        tblVMInfo.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btnAddVM.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 12)); // NOI18N
        btnAddVM.setText("Add VM");
        btnAddVM.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnAddVM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVMActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 13)); // NOI18N
        btnDelete.setText("Delete VM");
        btnDelete.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnPlugConf.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 12)); // NOI18N
        btnPlugConf.setText("Configure");
        btnPlugConf.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnPlugConf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlugConfActionPerformed(evt);
            }
        });

        btnConfReset.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 12)); // NOI18N
        btnConfReset.setText("Reset");
        btnConfReset.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnConfReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfResetActionPerformed(evt);
            }
        });

        btnConfCancel.setFont(new java.awt.Font("Latin Modern Sans Quotation", 1, 12)); // NOI18N
        btnConfCancel.setText("Cancel");
        btnConfCancel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnConfCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVirtualLayout = new javax.swing.GroupLayout(pnlVirtual);
        pnlVirtual.setLayout(pnlVirtualLayout);
        pnlVirtualLayout.setHorizontalGroup(
            pnlVirtualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(pnlVirtualLayout.createSequentialGroup()
                .addGroup(pnlVirtualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVirtualLayout.createSequentialGroup()
                        .addComponent(btnAddVM, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlVirtualLayout.createSequentialGroup()
                        .addComponent(btnPlugConf, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfReset, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlVirtualLayout.setVerticalGroup(
            pnlVirtualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVirtualLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVirtualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddVM, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVirtualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlugConf, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnlVirtualLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnConfCancel, btnConfReset, btnPlugConf});

        pnlVirtualLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAddVM, btnDelete});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlCloudInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDomainInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlVirtual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(14, 14, 14)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlDomainInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlCloudInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlVirtual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPlugConfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlugConfActionPerformed
        
        //TODO - XXX
        // 1. Validate all the fields are entered
        // 2. Check whether all the rows of JTable are selected or only few
        // 3. based on the selection, you may need to prepare the array 
        //    and create multiple input files for passing to PSConsole Frame
        // 4. We need to restructure the VmInfo vector because some could have
        //    deleted after adding
        // 5. once the above steps are successful, create a file where you can preserve
        //    the values
                
        // lets validate whether all required fields have been
        // input by the user, if NO, then we may need to alert the 
        // user to input it...
        if( isValidToProceed() == false ) {
            displayMessage("Required(*) fields cannot be empty", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            // log it for later use
            objFileUtil.logMessage(1,"Required(*) fields cannot be empty");
            return;                             
        } 
        
        if ( tblVMInfo.getRowCount() < 1 ) {
            displayMessage("Please Add atleast one Virtual Machine", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            // log it for later use
            objFileUtil.logMessage(1,"Please Add atleast one Virtual Machine");
            return;
        }
        // Lets enable JTable row if it's not selected (check box) already
        // so that the user will not get confused
        for (int row = 0; row < tblVMInfo.getRowCount(); row++) {
            tblVMInfo.setValueAt(true, row, 0);
        }
        
        // We may need to prepare the string from user inputs for
        // creating a PowerShell input file, and pass the filename
        // to PowerShell script
        try {
            preparePSInputFile();
        } catch (java.io.IOException eIoException) {
            // If there is an error we may need to catch it and alert 
            // the user
            System.out.println(eIoException);
            displayMessage("FileOperation Exception", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            // log it for later use
            objFileUtil.logMessage(1, 
                    "FileOperations Exception: preparePSInputFile");
            return;
        }    
        
        String[] sPsScrFileName = new String[tblVMInfo.getRowCount()];
        int row = 0;
        // We need to traverse and the find out the Virtual Machine whose 
        // status is "active", only such VMs are added to the list for further
        // processing
        for(int ix = 0; ix < mVVmInfo.size(); ix++){
            // get the object
            svtVmInfo vmInfo = (svtVmInfo)mVVmInfo.get(ix);
            // status is false then no action taken
            if( vmInfo.isVmActive() == false ) continue;
            // prepare the input filename and add it to the list for further
            // action
            sPsScrFileName[row++] = vmInfo.getVMName();
        }
        // set the vector holder to fileutil object, so that the same can be
        // referenced from console class
        objFileUtil.setConfActive(true);
        objFileUtil.setVmInfoArray(mVVmInfo);
        this.dispose();
        // PowerShell Console Class for calling PowerShell thread and
        // display the message on the console
        new svtPSoutput(this.bsParentFrame, objFileUtil.getConfigScrPath(), 
                            sPsScrFileName).show(true);
        return;     
    }//GEN-LAST:event_btnPlugConfActionPerformed

    private void btnConfCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfCancelActionPerformed
        // TODO add your handling code here:
        bsParentFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnConfCancelActionPerformed

    private void btnConfResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfResetActionPerformed
    
        // array size should be taken from constant value
        for ( int ix = 0; ix < 7; ix++ ) {
            controlText[ix].setText("");
        }
        
        // We need to clean the Virtual Machine Information as well
        for ( int row = 0; row < tblVMInfo.getRowCount(); row++ ) {
            for ( int col = 0; col < tblVMInfo.getColumnCount(); col++ ) {
                tblVMInfo.setValueAt("", row, col);
            }
        }
        
        javax.swing.table.DefaultTableModel tblModel = 
                     (javax.swing.table.DefaultTableModel)tblVMInfo.getModel();
        tblModel.setRowCount(0);
    }//GEN-LAST:event_btnConfResetActionPerformed

    private void btnAddVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVMActionPerformed
        
        // We need to validate whether all other fields are populated, if 
        // not we need to throw them an error
        if( isValidToProceed() == false ) {
            displayMessage("Required(*) fields cannot be empty", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            // log it for later use
            objFileUtil.logMessage(1,"Required(*) fields cannot be empty");
            return;                             
        } 
        
        new svtDlgVmInfo(this, tblVMInfo, mVVmInfo, true).setVisible(true);
        if ( tblVMInfo.getRowCount() > 0) {
            btnPlugConf.setEnabled(true);
        }
    }//GEN-LAST:event_btnAddVMActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO:
        // Need to find out which row has been selected
        // if the Row is not selected need to select the row
        // before deleting the VMs
        if( tblVMInfo.getRowCount() == 0 ) {
            displayMessage("No Virtual Machine Information to Delete", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // delete what ever row selected at the JTable control
        // this can be either "one" or multiple rows
        // We also need to update the vector to only hold the VMs which are
        // still eligible for configuration - mVVmInfo
        javax.swing.table.DefaultTableModel tblModel = 
                     (javax.swing.table.DefaultTableModel)tblVMInfo.getModel();
        for ( int ix = (tblVMInfo.getRowCount() - 1); ix >= 0; ix-- ) {
            boolean selected = Boolean.parseBoolean(tblVMInfo.getValueAt(ix, 0).toString());
            String sVmName = tblVMInfo.getValueAt(ix, 1).toString();
            if (selected == true) {
                tblModel.removeRow(ix);
                tblModel.fireTableRowsDeleted(ix, ix);
                // We need to set the status as "false" which ever VM 
                // gets deleted, otherwise it keeps thread for all the 
                // VMs added
                for(int i=0; i < mVVmInfo.size(); i++) {
                    svtVmInfo vmInfo = (svtVmInfo)mVVmInfo.get(i);
                    if(vmInfo.getVMName().compareTo(sVmName)== 0)
                            vmInfo.setVMStatus(false);
                }
            }
        }
        
        // if the JTable has atleast 1 VM to be processed then we can
        // fire the logic
        if(tblVMInfo.getRowCount() == 0 ) btnPlugConf.setEnabled(false);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void cbIncludeVMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIncludeVMActionPerformed
        // TODO add your handling code here:
        cmbImageTemplate.removeAllItems();
        if (cbIncludeVM.isSelected()) {
            cmbImageTemplate.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtVMs()));
        }
        else {
            cmbImageTemplate.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtTemplates()));
        }
    }//GEN-LAST:event_cbIncludeVMActionPerformed

    private void btnSvtNetworkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSvtNetworkActionPerformed
        // TODO add your handling code here:
        cmbSvtNetwork.removeAllItems();
        
        objFileUtil.readSvtNetworks();
        objFileUtil.readSvTInfo();

        cmbSvtNetwork.setModel(new javax.swing.DefaultComboBoxModel(objFileUtil.getSvtNetworks()));
    }//GEN-LAST:event_btnSvtNetworkActionPerformed
    
    // Private Variable declration
    // Previous JForm, and it is a parent form
    private svtBaseFrame bsParentFrame = null; 
    // FileControl Utility object which is a singleton, and 
    // all requested objects are same
    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
    // JTextField Control Array, which is of helpful in multiple
    // ways
    javax.swing.JTextField[] controlText = new javax.swing.JTextField[12];
    // corresponding control values from FileUtil object
    String[] controlValues = new String[12];
    // Vector to hold Virtual Machine Info
    java.util.Vector mVVmInfo = new java.util.Vector();
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddVM;
    private javax.swing.JButton btnConfCancel;
    private javax.swing.JButton btnConfReset;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnPlugConf;
    private javax.swing.JButton btnSvtNetwork;
    private javax.swing.JCheckBox cbIncludeVM;
    private javax.swing.JComboBox<String> cmbImageTemplate;
    private javax.swing.JComboBox cmbSvtNetwork;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCldCustName;
    private javax.swing.JLabel lblCldRsrLocation;
    private javax.swing.JLabel lblCloudPasswd;
    private javax.swing.JLabel lblCloudURI;
    private javax.swing.JLabel lblCloudUsrId;
    private javax.swing.JLabel lblDomainName;
    private javax.swing.JLabel lblDomainPasswd;
    private javax.swing.JLabel lblDomainUsr;
    private javax.swing.JLabel lblISOLocation;
    private javax.swing.JPanel pnlCloudInfo;
    private javax.swing.JPanel pnlDomainInfo;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlVirtual;
    private javax.swing.JTable tblVMInfo;
    private javax.swing.JTextField txtCldCustomerName;
    private javax.swing.JTextField txtCldRsrLocation;
    private javax.swing.JPasswordField txtCloudPasswd;
    private javax.swing.JTextField txtCloudUsrId;
    private javax.swing.JTextField txtDomainName;
    private javax.swing.JPasswordField txtDomainPasswd;
    private javax.swing.JTextField txtDomainUsr;
    // End of variables declaration//GEN-END:variables
}
