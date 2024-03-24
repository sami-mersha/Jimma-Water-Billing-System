package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Login extends javax.swing.JFrame {
    
    public Login() {
        initComponents();
    }
    
    //Global Variables
    int day, month, year;
    String username, password;
    
    public void setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
    }

    public boolean findHisCode(int hs){
        boolean found = false;
        try {
            Connection c = getConnection();
            Statement st = c.createStatement();
            String cmd = "select * from jwbs.history";
            ResultSet rs = st.executeQuery(cmd);
            
            while(rs.next()){
                if(hs == rs.getInt("his_code")){
                    found = true;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return found;
    }
    
    public int generateHisCode() {
        int hisCode = 1;
        boolean hisCodeExists = true;
        
        hisCodeExists = findHisCode(hisCode);
        while(hisCodeExists){
            hisCode++;
            hisCodeExists = findHisCode(hisCode);
        }
        
        return hisCode;
    }
    
    public void addToHistory(String userName) {
        String event = "New login with <" + userName + "> username.";
        
        int his_code = generateHisCode();
        
        try {
            Connection con = getConnection();
            Statement st = con.createStatement(); 
            
            setDateNow();
            String dateNow = year + "-" + month + "-" + day;
            String cmd1 = "insert into history values("+ his_code +", 'Login', '" + event +"')";
            String cmd2 = "insert into ag_event values('"+ username + "', "+ his_code + ", '" + dateNow +"')";
            st.executeUpdate(cmd1);
            st.executeUpdate(cmd2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean checkValidity(String usrnm, String pwd) {
        boolean valid = false, userNotFound = true;
        JFrame jf = new JFrame();

        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "SELECT * FROM jwbs.agent";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (usrnm.equals(set.getString("ag_username")) && pwd.equals(set.getString("ag_password"))) {
                    valid = true;
                    userNotFound = false;
                    break;
                } else if (usrnm.equals(set.getString("ag_username"))) {
                    JOptionPane.showMessageDialog(jf, "Incorrect Password!\nPlease try again!", "Login Error", JOptionPane.ERROR_MESSAGE);
                    userNotFound = false;
                }
            }

            if (userNotFound) {
                JOptionPane.showMessageDialog(jf, "Username not found!\nPlease try again!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(jf, e);
        }

        return valid;
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
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        labelUsername = new javax.swing.JLabel();
        buttonLogin = new javax.swing.JButton();
        LabelPassword = new javax.swing.JLabel();
        buttonSignUp = new javax.swing.JPanel();
        labelSignUp = new javax.swing.JLabel();
        textField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 102));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Montserrat Medium", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/office_40px.png"))); // NOI18N
        jLabel1.setText("Jimma Water Billing System");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, -1, -1));

        jPanel2.setBackground(new java.awt.Color(104, 175, 240));

        jPanel3.setBackground(new java.awt.Color(70, 61, 149));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Montserrat SemiBold", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Don't have an account?");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(121, 295, -1, -1));

        labelUsername.setFont(new java.awt.Font("Montserrat SemiBold", 0, 18)); // NOI18N
        labelUsername.setForeground(new java.awt.Color(255, 255, 255));
        labelUsername.setText("Username");
        jPanel3.add(labelUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(121, 81, -1, -1));

        buttonLogin.setBackground(new java.awt.Color(141, 87, 255));
        buttonLogin.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        buttonLogin.setForeground(new java.awt.Color(30, 42, 105));
        buttonLogin.setText("Login");
        buttonLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLoginActionPerformed(evt);
            }
        });
        jPanel3.add(buttonLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 231, -1, -1));

        LabelPassword.setFont(new java.awt.Font("Montserrat SemiBold", 0, 18)); // NOI18N
        LabelPassword.setForeground(new java.awt.Color(255, 255, 255));
        LabelPassword.setText("Password");
        jPanel3.add(LabelPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(121, 153, -1, -1));

        buttonSignUp.setBackground(new java.awt.Color(112, 75, 99));
        buttonSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonSignUpMouseClicked(evt);
            }
        });

        labelSignUp.setBackground(new java.awt.Color(255, 255, 255));
        labelSignUp.setFont(new java.awt.Font("Montserrat SemiBold", 0, 18)); // NOI18N
        labelSignUp.setForeground(new java.awt.Color(255, 255, 255));
        labelSignUp.setText("Sign Up Here");
        labelSignUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelSignUpMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout buttonSignUpLayout = new javax.swing.GroupLayout(buttonSignUp);
        buttonSignUp.setLayout(buttonSignUpLayout);
        buttonSignUpLayout.setHorizontalGroup(
            buttonSignUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonSignUpLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSignUp)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonSignUpLayout.setVerticalGroup(
            buttonSignUpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonSignUpLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(labelSignUp))
        );

        jPanel3.add(buttonSignUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 330, -1, -1));

        textField.setFont(new java.awt.Font("Montserrat Medium", 0, 14)); // NOI18N
        textField.setForeground(new java.awt.Color(1, 32, 78));
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldActionPerformed(evt);
            }
        });
        jPanel3.add(textField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, 380, -1));

        passwordField.setFont(new java.awt.Font("Montserrat Medium", 0, 14)); // NOI18N
        jPanel3.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 190, 380, -1));

        jLabel2.setFont(new java.awt.Font("Montserrat Medium", 0, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Login Here");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(450, 450, 450)
                .addComponent(jLabel2)
                .addGap(478, 478, 478))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(208, 208, 208))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 1040, 520));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoginActionPerformed
        username = textField.getText();
        password = passwordField.getText();
        
        boolean isvalid = false;
        JFrame jf = new JFrame();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(jf, "Username field is empty!", "Login Error", JOptionPane.ERROR_MESSAGE);
            textField.setText("Your username here...");
        } else if (password.isEmpty()) {
            JOptionPane.showMessageDialog(jf, "Password field is empty!", "Login Error", JOptionPane.ERROR_MESSAGE);
        } else {
            isvalid = checkValidity(username, password);
        }

        if (isvalid) {
            password = "";
            dispose();
            addToHistory(username);
            Main m = new Main();
            m.ag_username = username;
            m.setVisible(true);
        }
    }//GEN-LAST:event_buttonLoginActionPerformed

    private void textFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textFieldActionPerformed

    private void buttonSignUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buttonSignUpMouseClicked
        JOptionPane.showMessageDialog(null, "Cannot sign up now Please Contact another Agents!", "Sorry!", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_buttonSignUpMouseClicked

    private void labelSignUpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelSignUpMouseClicked
        JOptionPane.showMessageDialog(null, "Cannot sign up now Please Contact another Agents!", "Sorry!", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_labelSignUpMouseClicked

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
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelPassword;
    private javax.swing.JButton buttonLogin;
    private javax.swing.JPanel buttonSignUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel labelSignUp;
    private javax.swing.JLabel labelUsername;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTextField textField;
    // End of variables declaration//GEN-END:variables
}
