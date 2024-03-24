package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountSearchResult extends javax.swing.JFrame {

    public AccountSearchResult() {
        initComponents();
    }

    public void fillAgentsData(String username) {
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.agent";

            ResultSet set = st.executeQuery(command);

            String fullName, gender, dob;
            int phoneNo;
            while (set.next()) {
                if (username.equals(set.getString("ag_username"))) {
                    fullName = set.getString("ag_firstname") + " " + set.getString("ag_lastname");
                    fullNameLabel.setText(fullName);
                    usernameLabel.setText(username);
                    dob = set.getString("ag_dob");
                    birthDateLabel.setText(dob);
                    gender = set.getString("ag_sex");
                    genderLabel.setText(gender);
                    phoneNo = set.getInt("ag_phonenumber");
                    phoneNoLabel.setText("0" + phoneNo);

                    break;
                }
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
        birthDateLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        genderLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        usernameLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        phoneNoLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(153, 0, 51));
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

        birthDateLabel.setBackground(new java.awt.Color(255, 204, 204));
        birthDateLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        birthDateLabel.setText("null");
        jPanel3.add(birthDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 110, 300, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(204, 0, 51));
        jLabel7.setText("DOB.: ");
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

        usernameLabel.setBackground(new java.awt.Color(255, 204, 204));
        usernameLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        usernameLabel.setText("null");
        jPanel3.add(usernameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 70, 300, -1));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(204, 0, 51));
        jLabel11.setText("Username:");
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));
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

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 420, 290));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 480, 380));

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
            java.util.logging.Logger.getLogger(AccountSearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AccountSearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AccountSearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AccountSearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AccountSearchResult().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel birthDateLabel;
    private javax.swing.JLabel exitLabel;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JLabel fullNameLabel;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JLabel phoneNoLabel;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
