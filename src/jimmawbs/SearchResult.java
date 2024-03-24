package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SearchResult extends javax.swing.JFrame {

    int day, month, year;

    public SearchResult() {
        initComponents();
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

    public void fillCustomersData(String idn) {
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.customer";
            ResultSet set = st.executeQuery(command);

            String fullName, birthDate, gender, woreda, city, kebele, homeNo;
            int phoneNo, adCode = 0, usCode = 0;
            double usAmount, usCost;
            while (set.next()) {
                if (idn.equals(set.getString("cus_id"))) {
                    fullName = set.getString("cus_firstname") + " " + set.getString("cus_lastname");
                    fullNameLabel.setText(fullName);
                    idLabel.setText(idn);
                    birthDate = set.getString("cus_dob");
                    dobLabel.setText(birthDate);
                    gender = set.getString("cus_sex");
                    genderLabel.setText(gender);
                    phoneNo = set.getInt("cus_phonenumber");
                    phoneNoLabel.setText("0" + phoneNo);
                    adCode = set.getInt("cus_addresscode");
                    usCode = set.getInt("cus_usagecode");
                    break;
                }
            }

            command = "select * from jwbs.usage_statics";
            set = st.executeQuery(command);

            while (set.next()) {
                if (usCode == set.getInt("us_code")) {
                    usAmount = set.getDouble("us_amount");
                    usageAmountLabel.setText(usAmount + " meter cube");
                    usCost = set.getDouble("us_cost");
                    usageCostLabel.setText(usCost + " birr");
                    
                }
            }

            command = "select * from jwbs.address";
            set = st.executeQuery(command);

            while (set.next()) {
                if (adCode == set.getInt("ad_code")) {
                    woreda = set.getString("ad_woreda");
                    woredaLabel.setText(woreda);
                    city = set.getString("ad_city");
                    cityLabel.setText(city);
                    kebele = set.getString("ad_kebele");
                    kebeleLabel.setText(kebele);
                    kebele = set.getString("ad_kebele");
                    kebeleLabel.setText(kebele);
                    homeNo = set.getString("ad_homenumber");
                    homeNumberLabel.setText(homeNo);
                }
            }
            
            if(paymentDone(idn)){
                paymentStatusLabel.setText("Payed!");
            }else{
                paymentStatusLabel.setText("Not Payed!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        exitPanel = new javax.swing.JPanel();
        exitLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        fullNameLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        dobLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        genderLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        idLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        phoneNoLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        woredaLabel = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        cityLabel = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        kebeleLabel = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        usageAmountLabel = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        usageCostLabel = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel24 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        paymentStatusLabel = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        homeNumberLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Gill Sans MT", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Search Result");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, -1, -1));

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

        jPanel1.add(exitPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 10, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 480, 70));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fullNameLabel.setBackground(new java.awt.Color(255, 204, 204));
        fullNameLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        fullNameLabel.setText("null");
        jPanel3.add(fullNameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 300, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(204, 0, 51));
        jLabel5.setText("Full Name: ");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));
        jPanel3.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 370, 14));

        dobLabel.setBackground(new java.awt.Color(255, 204, 204));
        dobLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dobLabel.setText("null");
        jPanel3.add(dobLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 300, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 0, 51));
        jLabel7.setText("DOB: ");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));
        jPanel3.add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 370, 14));

        genderLabel.setBackground(new java.awt.Color(255, 204, 204));
        genderLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        genderLabel.setText("null");
        jPanel3.add(genderLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 300, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 0, 51));
        jLabel9.setText("Gender: ");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, -1));
        jPanel3.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 370, 14));

        idLabel.setBackground(new java.awt.Color(255, 204, 204));
        idLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        idLabel.setText("null");
        jPanel3.add(idLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 300, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 0, 51));
        jLabel11.setText("Cus ID: ");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));
        jPanel3.add(jSeparator5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 370, 14));

        phoneNoLabel.setBackground(new java.awt.Color(255, 204, 204));
        phoneNoLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        phoneNoLabel.setText("null");
        jPanel3.add(phoneNoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 300, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(204, 0, 51));
        jLabel13.setText("Phone No.: ");
        jPanel3.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));
        jPanel3.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 370, 14));

        woredaLabel.setBackground(new java.awt.Color(255, 204, 204));
        woredaLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        woredaLabel.setText("null");
        jPanel3.add(woredaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 230, 300, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(204, 0, 51));
        jLabel15.setText("Woreda: ");
        jPanel3.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, -1));
        jPanel3.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 370, 14));

        cityLabel.setBackground(new java.awt.Color(255, 204, 204));
        cityLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cityLabel.setText("null");
        jPanel3.add(cityLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 270, 300, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(204, 0, 51));
        jLabel17.setText("City: ");
        jPanel3.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, -1, -1));
        jPanel3.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 370, 14));

        kebeleLabel.setBackground(new java.awt.Color(255, 204, 204));
        kebeleLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        kebeleLabel.setText("null");
        jPanel3.add(kebeleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 310, 300, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(204, 0, 51));
        jLabel19.setText("Kebele: ");
        jPanel3.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, -1, -1));
        jPanel3.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 370, 14));

        usageAmountLabel.setBackground(new java.awt.Color(255, 204, 204));
        usageAmountLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        usageAmountLabel.setText("null");
        jPanel3.add(usageAmountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 410, 240, 50));
        jPanel3.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 410, 370, 10));
        jPanel3.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 530, 370, 10));

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(204, 0, 51));
        jLabel22.setText("Payment Status: ");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, -1, 40));
        jPanel3.add(jSeparator11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 370, 14));

        usageCostLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        usageCostLabel.setForeground(new java.awt.Color(51, 51, 51));
        usageCostLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        usageCostLabel.setText(" Birr");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(usageCostLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(usageCostLabel)
                .addGap(20, 20, 20))
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 470, 260, 50));
        jPanel3.add(jSeparator12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 460, 370, 10));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(204, 0, 51));
        jLabel24.setText("Usage Cost: ");
        jPanel3.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 470, -1, 50));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(204, 0, 51));
        jLabel20.setText("Home No.: ");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 350, -1, -1));

        paymentStatusLabel.setBackground(new java.awt.Color(255, 204, 204));
        paymentStatusLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        paymentStatusLabel.setText("null");
        jPanel3.add(paymentStatusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 377, 260, 30));

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(204, 0, 51));
        jLabel23.setText("Usage Amount ");
        jPanel3.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, -1, -1));

        homeNumberLabel.setBackground(new java.awt.Color(255, 204, 204));
        homeNumberLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        homeNumberLabel.setText("null");
        jPanel3.add(homeNumberLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 350, 300, -1));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 24, 420, 570));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 480, 630));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabelMousePressed
        dispose();
    }//GEN-LAST:event_exitLabelMousePressed

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
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SearchResult().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cityLabel;
    private javax.swing.JLabel dobLabel;
    private javax.swing.JLabel exitLabel;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JLabel homeNumberLabel;
    private javax.swing.JLabel idLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JLabel kebeleLabel;
    private javax.swing.JLabel paymentStatusLabel;
    private javax.swing.JLabel phoneNoLabel;
    private javax.swing.JLabel usageAmountLabel;
    private javax.swing.JLabel usageCostLabel;
    private javax.swing.JLabel woredaLabel;
    // End of variables declaration//GEN-END:variables
}
