package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DataZone extends javax.swing.JFrame {

    int day, month, year;
    String sMonth;

    public DataZone() {
        initComponents();
        setDateNow();
        switch (month) {
            case 1:
                sMonth = "January";
                break;
            case 2:
                sMonth = "February";
                break;
            case 3:
                sMonth = "March";
                break;
            case 4:
                sMonth = "April";
                break;
            case 5:
                sMonth = "May";
                break;
            case 6:
                sMonth = "June";
                break;
            case 7:
                sMonth = "July";
                break;
            case 8:
                sMonth = "August";
                break;
            case 9:
                sMonth = "September";
                break;
            case 10:
                sMonth = "October";
                break;
            case 11:
                sMonth = "November";
                break;
            case 12:
                sMonth = "December";
                break;
            default:
                sMonth = null;
                break;
        }
        monthLabel.setText("Month: " + sMonth);
    }

    public void setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
    }

    public boolean paymentDone(String id) {
        boolean done = false;

        try {
            Connection c = getConnection();
            Statement s = c.createStatement();

            String cmd = "select * from jwbs.cus_event";
            ResultSet rset = s.executeQuery(cmd);
            setDateNow();
            int ed, em, ey;
            while (rset.next()) {
                if (id.equals(rset.getString("cus_id"))) {
                    String dt = rset.getString("ev_date");
                    int hCode = rset.getInt("his_code");
                    //find history type
                    String hType = null;
                    {
                        Statement st2 = c.createStatement();
                        String ncmd = "select * from jwbs.history";
                        ResultSet rs2 = st2.executeQuery(ncmd);
                        while (rs2.next()) {
                            if (hCode == rs2.getInt("his_code")) {
                                hType = rs2.getString("his_type");
                            }
                        }
                    }
                    String[] dateParts = dt.split("-");
                    ed = Integer.parseInt(dateParts[2]);
                    em = Integer.parseInt(dateParts[1]);
                    ey = Integer.parseInt(dateParts[0]);
                    String edate = ey + "-" + em + "-" + ed;
                    if (year == ey && month == em && hType.equals("Payment")) {
                        done = true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        return done;
    }

    public double getUsageCost(String id) {
        double cost = 0;
        int usCode = 0;
        try {
            Connection c = getConnection();
            Statement s = c.createStatement();
            String cmd = "select * from jwbs.customer";
            ResultSet rs = s.executeQuery(cmd);
            while (rs.next()) {
                if (id.equals(rs.getString("cus_id"))) {
                    usCode = rs.getInt("cus_usagecode");
                }
            }

            cmd = "select * from jwbs.usage_statics";
            ResultSet set = s.executeQuery(cmd);
            while (set.next()) {
                if (usCode == set.getInt("us_code")) {
                    cost = set.getDouble("us_cost");
                }
            }

        } catch (Exception e) {
            System.err.println(e);
        }

        return cost;
    }

    public void fillCustomersTable() {
        try {
            DefaultTableModel tmodel = (DefaultTableModel) dataTable.getModel();
            tmodel.setRowCount(0);

            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.customer";
            ResultSet set = st.executeQuery(command);

            String fullName, idNo, homeNo = null;
            int adCode = 0, usCode = 0;
            double usAmount = 0.0, usCost = 0.0;
            while (set.next()) {
                fullName = set.getString("cus_firstname") + " " + set.getString("cus_lastname");
                idNo = set.getString("cus_id");
                adCode = set.getInt("cus_addresscode");
                usCode = set.getInt("cus_usagecode");

                String command2 = "select * from jwbs.usage_statics";
                Statement st2 = cn.createStatement();
                ResultSet set2 = st2.executeQuery(command2);
                while (set2.next()) {
                    if (usCode == set2.getInt("us_code")) {
                        usAmount = set2.getDouble("us_amount");
                        usCost = set2.getDouble("us_cost");
                    }
                }

                command2 = "select * from jwbs.address";
                set2 = st2.executeQuery(command2);
                while (set2.next()) {
                    if (adCode == set2.getInt("ad_code")) {
                        homeNo = set2.getString("ad_homenumber");
                    }
                }
                //combinig the extracted data into one
                String data[] = {idNo, fullName, (usAmount + ""), (usCost + ""), homeNo};

                DefaultTableModel tbmodel = (DefaultTableModel) dataTable.getModel();
                tbmodel.addRow(data);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public int generateUsageCode() {
        int amount = 1;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "SELECT * FROM jwbs.usage_statics";
            ResultSet rset = st.executeQuery(cmd);
            while (rset.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        int new_usage_code = amount + 2;
        return new_usage_code;
    }

    public static Connection getConnection() {
        Connection con = null;

        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/jwbs";
            String username = "root";
            String password = "S#myDB35";

            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);

        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        exitPanel = new javax.swing.JPanel();
        exitLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        cusIdtf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tarifftf = new javax.swing.JTextField();
        updateButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        monthLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        usAmounttf = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(149, 97, 119));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(208, 176, 240));

        exitPanel.setBackground(new java.awt.Color(255, 255, 255));

        exitLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete_32px.png"))); // NOI18N
        exitLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exitLabelMousePressed(evt);
            }
        });

        javax.swing.GroupLayout exitPanelLayout = new javax.swing.GroupLayout(exitPanel);
        exitPanel.setLayout(exitPanelLayout);
        exitPanelLayout.setHorizontalGroup(
            exitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitLabel))
        );
        exitPanelLayout.setVerticalGroup(
            exitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitLabel))
        );

        jLabel9.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/list_64px.png"))); // NOI18N
        jLabel9.setText("FILL DATA");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(420, 420, 420)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 473, Short.MAX_VALUE)
                .addComponent(exitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        dataTable.setBackground(new java.awt.Color(227, 226, 255));
        dataTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        dataTable.setForeground(new java.awt.Color(51, 0, 51));
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Full Name", "Usage Amount(m^3)", "Cost(Birr)", "Home Number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(dataTable);
        if (dataTable.getColumnModel().getColumnCount() > 0) {
            dataTable.getColumnModel().getColumn(0).setResizable(false);
            dataTable.getColumnModel().getColumn(1).setResizable(false);
            dataTable.getColumnModel().getColumn(2).setResizable(false);
            dataTable.getColumnModel().getColumn(3).setResizable(false);
            dataTable.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 120, 670, 460));

        cusIdtf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel1.add(cusIdtf, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 390, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Usage Amount(cubic meter): ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, -1, 30));

        tarifftf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tarifftf.setText("6.2");
        tarifftf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tarifftfActionPerformed(evt);
            }
        });
        jPanel1.add(tarifftf, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 350, 120, 40));

        updateButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        updateButton.setText("Update");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        jPanel1.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 420, 380, -1));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 540, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Customer Id: ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, -1, 30));

        monthLabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        monthLabel.setForeground(new java.awt.Color(255, 255, 255));
        monthLabel.setText("Month");
        jPanel1.add(monthLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 120, -1, 30));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Tariff(Birr/cubic meter): ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 350, -1, 40));

        usAmounttf.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel1.add(usAmounttf, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 390, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        String cusId = cusIdtf.getText();
        double usAmount = Double.parseDouble(usAmounttf.getText());
        if (paymentDone(cusId)) {
            JOptionPane.showMessageDialog(new JFrame(), "This customer has already made payment for this month.\nPlease wait till the next month!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int usCode = generateUsageCode();
        double prevUsageCost = getUsageCost(cusId);
        try {
            Connection con = getConnection();
            Statement stm = con.createStatement();

            //Calculating cost
            double tariff = Double.parseDouble(tarifftf.getText());
            double usCost = (tariff * usAmount) + prevUsageCost;

            setDateNow();
            String cmd = "insert into usage_statics values(" + usCode + "," + usAmount + "," + usCost + "," + month + "," + year + ");";
            stm.executeUpdate(cmd);

            cmd = "update jwbs.customer set cus_usagecode = " + usCode + " where cus_id = '" + cusId + "'";
            stm.executeUpdate(cmd);

            fillCustomersTable();

        } catch (Exception e) {
            System.out.println("HEre" + e);
        }

    }//GEN-LAST:event_updateButtonActionPerformed

    private void exitLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabelMousePressed
        dispose();
    }//GEN-LAST:event_exitLabelMousePressed

    private void tarifftfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tarifftfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tarifftfActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DataZone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataZone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataZone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataZone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataZone().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField cusIdtf;
    private javax.swing.JTable dataTable;
    private javax.swing.JLabel exitLabel;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel monthLabel;
    private javax.swing.JTextField tarifftf;
    private javax.swing.JButton updateButton;
    private javax.swing.JTextField usAmounttf;
    // End of variables declaration//GEN-END:variables
}
