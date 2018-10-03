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
 * @author johnbrit
 */
public class svtDeConfPlugin extends javax.swing.JFrame {

    /**
     * Creates new form svtDeConfPlugin
     */
    public svtDeConfPlugin() {
        initComponents();
    }

    public svtDeConfPlugin(svtBaseFrame bsFrame) {
        
        initComponents();
        objFileUtil.makeMeCentred(this);
        this.mBaseFrame = bsFrame;
        mBaseFrame.setVisible(false);
        btnDeconfigure.setEnabled(false);
        setCustomTableModel();
        controlAssignment();
        makeControlArray();
         // setting Table Header Font as BOLD
        tblPlugVMInfo.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
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
              if(column == 0 ) return true;
              return false;
          }     
        };
        tblPlugVMInfo.setModel(model);
        model.addColumn("->");
        model.addColumn("Machine Name");
        model.addColumn("User Name");
        model.addColumn("SimpliVity Host");
        prepareVMTable();
        return;
    }
          
    private void prepareVMTable() {
        
        // setting Table Header Font as BOLD
        //tblPlugVMInfo.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        // Make each cell alignment as CENTER
        javax.swing.table.DefaultTableCellRenderer centerRenderer = 
                            new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        tblPlugVMInfo.setDefaultRenderer(Object.class, centerRenderer);
        
        javax.swing.table.TableColumn 
                cRowSelect    = tblPlugVMInfo.getColumn("->");
        javax.swing.table.TableColumn 
                cMachName = tblPlugVMInfo.getColumn("Machine Name");
        javax.swing.table.TableColumn 
                cUserName = tblPlugVMInfo.getColumn("User Name");
        javax.swing.table.TableColumn 
                cSvtHost  = tblPlugVMInfo.getColumn("SimpliVity Host");
        
        cRowSelect.setMinWidth(30);
        cRowSelect.setMaxWidth(30);
        // Machine Name
        cMachName.setMinWidth(200);
        cMachName.setMaxWidth(200);
        // user name
        cUserName.setMinWidth(200);
        cUserName.setMaxWidth(200);
        // SVT Host Name
        cSvtHost.setMinWidth(215);
        cSvtHost.setMaxWidth(215);
        
        tblPlugVMInfo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    }
        private boolean preparePSInputFile() throws java.io.IOException {
        
        for ( int ix = 0; ix < mVMInstInfo.size(); ix++ ) {
           
            svtVmInfo vInfo = (svtVmInfo)mVMInstInfo.get(ix);
            if (vInfo.isVmActive() == false ) continue;
            
            String sFileLine = "@{\r\nvm = \r\n\t@{\r\n";
            
            sFileLine += "\tname = " + "\'" + vInfo.getVMName()
                        +"\'\r\n" + "\ttemplate = " + "\'" + "" +"\'\r\n" +
                        "\tnetwork = " + "\'" + "" +"\'\r\n" +
                        "\thost = " + "\'" + "" +"\'\r\n" +
                        "\tusername = " + "\'" + vInfo.getVMUserName() +"\'\r\n" +
                        "\tpassword = " + "\'" + vInfo.getVMPassword() +"\'\r\n \t}\r\n";

            sFileLine += "ad = \r\n\t@{\r\n";
            sFileLine += "\tdomain = " + "\'" + txtDomainName.getText() +"\'\r\n" +
                        "\tusername = " + "\'" + txtDomainUsr.getText() +"\'\r\n" +
                        "\tpassword = " + "\'" + txtDomainPassword.getText() +"\'\r\n \t}\r\n";
            // we need to add all the text values to FileUtil vector, so that
            // console Frame can use all these information later
            ((svtVmInfo)mVMInstInfo.get(ix)).setDomainName(txtDomainName.getText());
            ((svtVmInfo)mVMInstInfo.get(ix)).setDomainUser(txtDomainUsr.getText());

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
            // End SCVMM Inf
            sFileLine += "citrix = \r\n\t@{\r\n";
            sFileLine += "\tclientId = " + "\'" + "" +"\'\r\n" +
                        "\tclientKey = " + "\'" + "" +"\'\r\n" +
                        "\tcustomerName = " + "\'" + "" +"\'\r\n" +
                        "\tresourceLocation = " + "\'" + "" +
                        "\'\r\n \t}\r\n}\r";
            objFileUtil.writeToFile(vInfo.getVMName(), sFileLine, "DeConfigPlugin");
        }
        return true;
    }
    
    private void fillTableRow() {
        
        javax.swing.table.DefaultTableModel tblModel = 
                      (javax.swing.table.DefaultTableModel)tblPlugVMInfo.getModel();

        for (int ix = 0; ix < mVMInstInfo.size(); ix++){
            tblModel.addRow(new Object[0]);
            svtVmInfo vInfo = (svtVmInfo)mVMInstInfo.get(ix);
            tblModel.setValueAt(false, ix, 0);
            tblModel.setValueAt(vInfo.getVMName(),     ix, 1);
            tblModel.setValueAt(vInfo.getVMUserName(), ix, 2);
            tblModel.setValueAt(vInfo.getVMSvtHost(),  ix, 3);
        }//for loop
       tblPlugVMInfo.setRowSelectionInterval(0, 0);
       tblPlugVMInfo.setValueAt(true, 0, 0);
       fillControlValues(0);
    }// function 
    
    private void controlAssignment() {
        // The below code needs to be added otherwise
        // there would be an error 
        mControlText[0] = txtVMName;
        mControlText[1] = txtVMUserName;
        mControlText[2] = txtDomainPassword;
        mControlText[3] = txtSvtHost;
        mControlText[4] = txtDomainUsr;
        mControlText[5] = txtDomainName;
        mControlText[6] = txtVMPassword;
        
        // We need to set the values from the configuration file, so that
        // user need not to waste their time keying in
        txtDomainUsr.setText(objFileUtil.getDomainUserName());
        txtDomainName.setText(objFileUtil.getDomainName());
        
        if(objFileUtil.getDomainUserName().isEmpty() == true ) 
            txtDomainUsr.requestFocus();
        else
            txtDomainPassword.requestFocus();
    }
    
    private void makeControlArray() {
        
        try {
            if (objFileUtil.readVmInfoFile(mVMInstInfo) == false ) {
                // validation
                txtVMName.setEnabled(false);
                txtVMUserName.setEnabled(false);
                txtVMPassword.setEnabled(false);
                txtSvtHost.setEnabled(false);
                txtDomainUsr.requestFocus();
                // We need to set the variable to make the difference
                // between read information from the VMFile or user inputting
                // the values
                mUserInputReq = true;
                btnUpdate.setText("Add Virtual Machine");
                return;
            }    
        } catch( java.io.IOException eIOException) {
            displayMessage("VM Information is not available", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        //prepareVMTable();
        fillTableRow();
        fillControlValues(0);
        txtDomainPassword.requestFocus();
    }
    
    private void fillControlValues(int rowSelected) {

        int col = 1;
        svtVmInfo vmInfo = (svtVmInfo)mVMInstInfo.get(rowSelected);

        mControlValues[0] = tblPlugVMInfo.getValueAt(rowSelected, col++).toString();
        mControlValues[1] = tblPlugVMInfo.getValueAt(rowSelected, col++).toString();
        // If the user input the value for Domain Password, we should not
        // make it blank, rather we need to show it all the time in the text
        // box
        if( txtDomainPassword.getText().isEmpty() == false )
            mControlValues[2] = txtDomainPassword.getText();
        mControlValues[3] = tblPlugVMInfo.getValueAt(rowSelected, col++).toString();
        mControlValues[4] = vmInfo.getDomainUser();
        mControlValues[5] = vmInfo.getDomainName();
        mControlValues[6] = vmInfo.getVMPassword();
        fillUpControlValues();
    }
    
    // message dialog to show the error.
    private void displayMessage(String sMsg, int nType) {
    
        javax.swing.JOptionPane.showMessageDialog(this, sMsg, 
                        "SimpliVity Citrix Cloud Plugin", nType);
        return;
    }
    
    private void fillUpControlValues() {
        
        for ( int ix = 0; ix < 7; ix++ ) {
            mControlText[ix].setText(mControlValues[ix]);
        }
        return;
    }

    private boolean isGoodToGo() {
        
        // The below code validates what is there in the JFrame, but
        // in case of VM there are 2 VMs available, and we may need both VM
        // data to be captrued before proceeding
        for ( int ix = 0; ix < 7; ix++ ) {
            // prepopulated values from the text boxes, and we just need to 
            // traverse the control array and check whether all the values 
            // are up, if not we need to alert the user to input the same
            if( true == (mControlText[ix].getText().isEmpty()) ) {
                mControlText[ix].requestFocus();
                return false;
            }
        }
        
        // The below validation only require if you read the content from the 
        // file
        if ( mUserInputReq == true ) return true;
        
        // The below code is exclusively check whether the password field 
        // has been input by the user, if not we may need to alert him to 
        // input the same. This is because we cannot save the password in the 
        // file since it's easy to hack into the system by anyone
        for ( int ix = 0; ix < tblPlugVMInfo.getRowCount(); ix++) {
            // set the pasword for the Virtual Machine what is been selected
            // not all the Virtual machine presented in the JTable
            boolean selected = Boolean.parseBoolean(tblPlugVMInfo.getValueAt(ix, 0).toString());
            if( selected == false ) continue;
            svtVmInfo vmInfo = (svtVmInfo)mVMInstInfo.get(ix);
            // verify whether the password is empty, it should be empty
            // most of the time unless the user input the same.
            if (vmInfo.getVMPassword().isEmpty() == true ) {
                // if we identify the row and corresponding password is empty, 
                // then we need to select that particular row, so that user 
                // can easily input the value
                tblPlugVMInfo.setRowSelectionInterval(ix, ix);
                // need to populate the values from the table to other controls
                fillControlValues(ix);
                // gain focus on the password text
                txtVMPassword.requestFocus(); 
                return false;
            }
        }
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
        lblDomainPasswd = new javax.swing.JLabel();
        lblDomainName = new javax.swing.JLabel();
        txtDomainUsr = new javax.swing.JTextField();
        txtDomainName = new javax.swing.JTextField();
        txtDomainPassword = new javax.swing.JPasswordField();
        pnlVirtualMachineInfo = new javax.swing.JPanel();
        lblVMName = new javax.swing.JLabel();
        lblVMUserName = new javax.swing.JLabel();
        lblVMPassword = new javax.swing.JLabel();
        txtVMUserName = new javax.swing.JTextField();
        txtVMPassword = new javax.swing.JPasswordField();
        txtVMName = new javax.swing.JTextField();
        lblSvtHost = new javax.swing.JLabel();
        txtSvtHost = new javax.swing.JTextField();
        btnUpdate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPlugVMInfo = new javax.swing.JTable();
        btnDeconfigure = new javax.swing.JButton();
        btnCacel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SimpliVity Citrix Cloud Connector Plugin");
        setAlwaysOnTop(true);
        setName("frDeConfPlugin"); // NOI18N
        setResizable(false);

        pnlDomainInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Domain Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        lblDomainUsr.setText("User Name*");

        lblDomainPasswd.setText("Password*");

        lblDomainName.setText("Domain Name*");

        javax.swing.GroupLayout pnlDomainInfoLayout = new javax.swing.GroupLayout(pnlDomainInfo);
        pnlDomainInfo.setLayout(pnlDomainInfoLayout);
        pnlDomainInfoLayout.setHorizontalGroup(
            pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDomainInfoLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblDomainPasswd)
                    .addComponent(lblDomainUsr))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDomainUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDomainPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDomainInfoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDomainName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDomainName, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlDomainInfoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDomainName, txtDomainPassword, txtDomainUsr});

        pnlDomainInfoLayout.setVerticalGroup(
            pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlDomainInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDomainUsr)
                    .addComponent(txtDomainUsr, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDomainPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDomainPasswd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDomainInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDomainName)
                    .addComponent(txtDomainName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43))
        );

        pnlDomainInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtDomainName, txtDomainPassword, txtDomainUsr});

        pnlVirtualMachineInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Virtual Machine Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        lblVMName.setText("Machine Name*");

        lblVMUserName.setText("User Name*");

        lblVMPassword.setText("Password*");

        lblSvtHost.setText("SimpliVity Host*");

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVirtualMachineInfoLayout = new javax.swing.GroupLayout(pnlVirtualMachineInfo);
        pnlVirtualMachineInfo.setLayout(pnlVirtualMachineInfoLayout);
        pnlVirtualMachineInfoLayout.setHorizontalGroup(
            pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE)
                    .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                                .addComponent(lblSvtHost)
                                .addGap(9, 9, 9)
                                .addComponent(txtSvtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                                .addComponent(lblVMUserName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVMUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                                .addComponent(lblVMName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVMName, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                            .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                                .addComponent(lblVMPassword)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVMPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))))))
        );

        pnlVirtualMachineInfoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtSvtHost, txtVMName, txtVMPassword, txtVMUserName});

        pnlVirtualMachineInfoLayout.setVerticalGroup(
            pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVirtualMachineInfoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVMName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVMName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVMUserName)
                    .addComponent(txtVMUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblVMPassword)
                    .addComponent(txtVMPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVirtualMachineInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSvtHost)
                    .addComponent(txtSvtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlVirtualMachineInfoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtVMName, txtVMPassword, txtVMUserName});

        tblPlugVMInfo.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tblPlugVMInfo.setModel(new javax.swing.table.DefaultTableModel(
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
        tblPlugVMInfo.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblPlugVMInfo.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPlugVMInfo.getTableHeader().setResizingAllowed(false);
        tblPlugVMInfo.getTableHeader().setReorderingAllowed(false);
        tblPlugVMInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPlugVMInfoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPlugVMInfo);
        tblPlugVMInfo.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btnDeconfigure.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnDeconfigure.setText("DeConfigure");
        btnDeconfigure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeconfigureActionPerformed(evt);
            }
        });

        btnCacel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnCacel.setText("Cancel");
        btnCacel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCacelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlDomainInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(336, 336, 336)
                        .addComponent(pnlVirtualMachineInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnDeconfigure, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCacel, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlVirtualMachineInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlDomainInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(42, 42, 42)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeconfigure, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCacel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCacel, btnDeconfigure});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCacelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCacelActionPerformed
        
        this.dispose();
        mBaseFrame.setVisible(true);
        
    }//GEN-LAST:event_btnCacelActionPerformed

    private void btnDeconfigureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeconfigureActionPerformed
        
        if (isGoodToGo() == false ) {
            displayMessage("Required(*) fields cannot be empty", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // We need to traverse the table and find out which are all the VMs selected
        // to delete, if anything left out then we may need to set that VM as inactive
        int nTotal = 0;
        for ( int ix = 0; ix < tblPlugVMInfo.getRowCount(); ix++ ) {
            boolean selected = Boolean.parseBoolean(tblPlugVMInfo.getValueAt(ix, 0).toString());
            String sVmName = tblPlugVMInfo.getValueAt(ix, 1).toString();
            if (selected == false) {
                for(int i=0; i < mVMInstInfo.size(); i++) {
                    if(((svtVmInfo)mVMInstInfo.get(i)).getVMName().compareTo(sVmName)== 0) {
                       ((svtVmInfo)mVMInstInfo.get(i)).setVMStatus(selected);
                        break;
                    }        
                }
            } else nTotal++;
        }
        
        try {
            // We may need to prepare the string from user inputs for
            // creating a PowerShell input file, and pass the filename
            // to PowerShell script
            preparePSInputFile();
        } catch (java.io.IOException eIoException) {
            // If there is an error we may need to catch it and alert 
            // the user
            displayMessage("FileOperation Exception", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }    
        // TODO
        // The following logic needs to be changed if there are multiple
        // SimpliVity host added to the combo box - Currently it only supports
        // 2 Host as per the design, but need to be changed to support
        // multiple based on the no of host added in SimpliVity
        String[] sPsDeScrFileName = new String[nTotal];
        int row = 0;
        // We need to traverse and the find out the Virtual Machine whose 
        // status is "active", only such VMs are added to the list for further
        // processing
        for(int ix = 0; ix < mVMInstInfo.size(); ix++){
            svtVmInfo vmInfo = (svtVmInfo)mVMInstInfo.get(ix);
            // status is false then no action taken
            if( vmInfo.isVmActive() == false ) continue;
            // prepare the input filename and add it to the list for further
            // action, this will be passed to Console JFrame
            sPsDeScrFileName[row++] = vmInfo.getVMName();
        }
        
        // let the FileUtil object know this is deconfiguration of Virtual machines
        objFileUtil.setConfActive(false);
        objFileUtil.setVmInfoArray(mVMInstInfo);
        // set current window visibility as false so that 
        // only the Console window to be displayed
        //this.setVisible(false);
        this.dispose();
        // pass it to threading class to invoke the external process of
        // PowerShell and execute the same.
        new svtPSoutput(this.mBaseFrame, objFileUtil.getDeConfigScrPath(), 
                            sPsDeScrFileName).show(true);
        return;    
    }//GEN-LAST:event_btnDeconfigureActionPerformed
                                                                                          
    private void tblPlugVMInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPlugVMInfoMouseClicked
        int row = tblPlugVMInfo.getSelectedRow();
        boolean selected = Boolean.parseBoolean(tblPlugVMInfo.getValueAt(row, 0).toString());
        if(selected == true) 
            fillControlValues(row);
    }//GEN-LAST:event_tblPlugVMInfoMouseClicked
    
    private boolean validateUserInputWithVMInfo(){
        
        if (txtDomainPassword.getText().isEmpty() == true || 
            txtVMPassword.getText().isEmpty() == true ) {
            displayMessage("Password Fields cannot be null", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Get Selected Row from the Table and input the password, so that we
        // can proceed
        int row = tblPlugVMInfo.getSelectedRow();
        svtVmInfo vmInfo = (svtVmInfo)mVMInstInfo.get(row);
        vmInfo.setVMPassword(txtVMPassword.getText());
        
        return true;
    }
    
    private boolean validateUserInputWithoutVMInfo(){
        
        // this is required because sometimes users can directly click the
        // add virtual machine btn to add VMs
        if(txtDomainName.getText().isEmpty() == true || 
           txtDomainUsr.getText().isEmpty()  == true || 
           txtDomainPassword.getText().isEmpty() == true){
                displayMessage("Required(*) fields cannot be Empty", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            txtDomainUsr.requestFocus();
            return false;
        }
        
        // if the Domain Information has been keyed in, then we can show the 
        // dialog to get the values of VMs
        new svtDlgVmInfo(this, tblPlugVMInfo, 
                         mVMInstInfo, true).setVisible(true);
        
        if (tblPlugVMInfo.getRowCount() == 0 ) return false;
        
        javax.swing.table.DefaultTableCellRenderer centerRenderer = 
                            new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        tblPlugVMInfo.setDefaultRenderer(Object.class, centerRenderer);
        
        txtSvtHost.setText(tblPlugVMInfo.getValueAt(0, 2).toString());
        // set the values to the Text Boxes
        svtVmInfo vmInfo = (svtVmInfo)mVMInstInfo.get(tblPlugVMInfo.getRowCount() - 1);
        txtVMName.setText(vmInfo.getVMName());
        txtVMUserName.setText(vmInfo.getVMUserName());
        txtVMPassword.setText(vmInfo.getVMPassword());
        
        // set the value to the VM Array Holder for later user
        vmInfo.setDomainName(txtDomainName.getText());
        vmInfo.setDomainUser(txtDomainUsr.getText());
        // if not all the rows are filled then we need to send return it here
        // because we need 2 VMs to be selected
        // XXX
        // if (tblPlugVMInfo.getRowCount() != 2) return false;
        // if the control comes here, which means 2 Rows have been
        // added at the Table Control, we need to enable all the 
        // controls, so that if there are any modification the
        // user can change it.
        txtVMName.setEnabled(true);
        txtVMUserName.setEnabled(true);
        txtVMPassword.setEnabled(true);
        txtSvtHost.setEnabled(true);
        // enable JTable Row as 1
        tblPlugVMInfo.setRowSelectionInterval(0, 0);
        //VMInstInfo holder already has the required information
        vmInfo = (svtVmInfo)mVMInstInfo.get(0);
        txtVMName.setText(vmInfo.getVMName());
        txtVMUserName.setText(vmInfo.getVMUserName());
        txtVMPassword.setText(vmInfo.getVMPassword());
        fillControlValues(0);
        return true;
    }
    
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // We need to read the values from the controls and
        // update into the right value holders
        // We may not need to get the value of Domain Passwd because it always
        // display in the JFrame itself
        
        if(mUserInputReq == false ){
            validateUserInputWithVMInfo();
        } else {
            if (validateUserInputWithoutVMInfo() == false ) 
                return;
        }
        // We need to check whether we need to enable "DeConfigure" button
        // or not, if the values are partial we need to hold it back
        if ( isGoodToGo() == true ) btnDeconfigure.setEnabled(true);
    }//GEN-LAST:event_btnUpdateActionPerformed

    // BaseFrame object, which we need to traverse back
    // once this JFrame is loosing focus from the customer
    private svtBaseFrame mBaseFrame = null;
    // FileUtil object where all the configuration values are 
    // kept, we need this object in all the classes
    private svtFileUtil objFileUtil = svtFileUtil.getInstance();
    // All Text Fields are kept in an array for validation and
    // further addition of operations
    javax.swing.JTextField[] mControlText = new javax.swing.JTextField[7];
    // Vector to hold previous Information which used to install the plugin
    // there are chances that information can be changed between the time of 
    // installation 
    java.util.Vector mVMInstInfo = new java.util.Vector();
    // Text Control Values, and this value would be filled from the vector
    // values what has been filed from the file
    String[] mControlValues = new String[7];
    // Need to get this value for distinguish between read the information from the
    // file vs UserInputing the values
    boolean mUserInputReq = false;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCacel;
    private javax.swing.JButton btnDeconfigure;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDomainName;
    private javax.swing.JLabel lblDomainPasswd;
    private javax.swing.JLabel lblDomainUsr;
    private javax.swing.JLabel lblSvtHost;
    private javax.swing.JLabel lblVMName;
    private javax.swing.JLabel lblVMPassword;
    private javax.swing.JLabel lblVMUserName;
    private javax.swing.JPanel pnlDomainInfo;
    private javax.swing.JPanel pnlVirtualMachineInfo;
    private javax.swing.JTable tblPlugVMInfo;
    private javax.swing.JTextField txtDomainName;
    private javax.swing.JPasswordField txtDomainPassword;
    private javax.swing.JTextField txtDomainUsr;
    private javax.swing.JTextField txtSvtHost;
    private javax.swing.JTextField txtVMName;
    private javax.swing.JPasswordField txtVMPassword;
    private javax.swing.JTextField txtVMUserName;
    // End of variables declaration//GEN-END:variables
}
