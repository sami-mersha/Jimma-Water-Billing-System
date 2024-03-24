package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class RegisterCustomer extends javax.swing.JFrame {

    int day, month, year;
    boolean notInformed;
    String cusId;

    public RegisterCustomer() {
        initComponents();
        birthDatetf.setText("Year-Month-Day");
        ButtonGroup G = new ButtonGroup();
        G.add(malerb);
        G.add(femalerb);
        printArea.setVisible(false);
    }

    public void setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
    }

    public int generateHisCode() {
        int amount = 1;
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from jwbs.history;";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        int his_code = amount + 1;
        return his_code;
    }

    public void addToRegisterHistory(String fullName, String idNo) {
        String event = "Customer with " + fullName + " name & with <" + idNo + "> id has been registered.";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            int his_code = generateHisCode();

            setDateNow();
            String dateNow = year + "-" + month + "-" + day;
            String cmd1 = "insert into history values(" + his_code + ", 'Registeration', '" + event + "')";
            String cmd2 = "insert into cus_event values('" + cusId + "', " + his_code + ", '" + dateNow + "')";
            st.executeUpdate(cmd1);
            st.executeUpdate(cmd2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String generateCustomerId() {
        int amount = 1, cus_code = 1;
        String new_cus_id = null;
        boolean idExists = true;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            new_cus_id = "CS00000" + cus_code;
            idExists = findId(new_cus_id);

            while (idExists) {
                amount++;
                cus_code = amount + 1;
                new_cus_id = "CS00000" + cus_code;
                String cmd = "SELECT * FROM jwbs.customer";
                ResultSet rset = st.executeQuery(cmd);
                boolean idFound = false;
                while (rset.next()) {
                    if (new_cus_id.equals(rset.getString("cus_id"))) {
                        idFound = true;
                    }
                }

                if (idFound == false) {
                    idExists = false;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return new_cus_id;
    }

    public boolean findId(String id) {
        boolean found = false;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String cmd = "SELECT * FROM jwbs.customer";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (set.getString("cus_id").equals(id)) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return found;
    }

    public int generateAddressCode() {
        int amount = 1;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "SELECT * FROM jwbs.address";
            ResultSet rset = st.executeQuery(cmd);
            while (rset.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        int new_ad_code = amount + 2;
        return new_ad_code;
    }

    public boolean checkPhoneNo(String phoneNo) {
        JFrame jf = new JFrame();
        boolean valid = true;

        if (phoneNo == null) {
            JOptionPane.showMessageDialog(jf, "Please enter your phone number!");
            phoneNotf.setText("Phone number here...");
            notInformed = false;
            valid = false;
        } else if (phoneNo.length() != 10) {
            JOptionPane.showMessageDialog(jf, "Phone numbers must have 10 digit!");
            valid = false;
            notInformed = false;
        }

        return valid;
    }

    public boolean checkFullName(String fullName) {
        JFrame jf = new JFrame();
        boolean valid = true;

        if (fullName.equals("") || fullName.equals("Full name Here...")) {
            JOptionPane.showMessageDialog(jf, "Please enter full name!");
            firstNametf.setText("First name here...");
            lastNametf.setText("Last name here...");
            valid = false;
            notInformed = false;
        } else if (fullName.length() > 25) {
            JOptionPane.showMessageDialog(jf, "Names must be less than 25 character!");
            valid = false;
            notInformed = false;
        }

        return valid;
    }

    public boolean registerData(String firstName, String lastName, String birthDate, String gender, int phoneNo, String woreda, String city, String kebele, String homeNo) {
        int adChecker = -1, cusChecker = -1, adCode = 0;
        boolean registered = false;

        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            int ad_code = generateAddressCode();
            String cmd = "insert into jwbs.address values(" + ad_code + ", 'Ethiopia', 'Oromia', 'Jimma', '" + woreda + "', '" + city + "', '" + kebele + "', '" + homeNo + "')";
            adChecker = st.executeUpdate(cmd);
            adCode = ad_code;

            String cus_id = generateCustomerId();
            cmd = "insert into customer values('" + cus_id + "', '" + firstName + "', '" + lastName + "', '" + birthDate + "', '" + gender + "', " + ad_code + ", " + phoneNo + ", 0)";
            cusChecker = st.executeUpdate(cmd);

            if (cusChecker > 0) {
                cusId = cus_id;
            }

        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (adChecker > 0 && cusChecker > 0) {
            registered = true;
        } else if (adChecker > 0) {
            {
                try {
                    Connection cn = getConnection();
                    Statement stm = cn.createStatement();
                    String com2 = "delete from jwbs.address  where ad_code = " + adCode;
                    stm.executeUpdate(com2);
                } catch (Exception e) {
                    System.err.println(e);
                }

            }
            JOptionPane.showMessageDialog(new JFrame(), "Please Check Customer Information(i.e Phone Number)!");
        } else if (cusChecker > 0) {
            System.out.println("Only Customer is registered");
            JOptionPane.showMessageDialog(new JFrame(), "Please Check Address Information(i.e Home Number)!");
        }
        return registered;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        printArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        exitPanel = new javax.swing.JPanel();
        exitLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        phoneNotf = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        birthDatetf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        firstNametf = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lastNametf = new javax.swing.JTextField();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        femalerb = new javax.swing.JRadioButton();
        malerb = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        homeNotf = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        woredatf = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        citytf = new javax.swing.JTextField();
        kebeletf = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();

        printArea.setColumns(20);
        printArea.setRows(5);
        jScrollPane1.setViewportView(printArea);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(167, 108, 158));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(159, 161, 148));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/plus_60px.png"))); // NOI18N
        jLabel1.setText("Register Customer");

        exitLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/delete_32px.png"))); // NOI18N
        exitLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                exitLabel1MousePressed(evt);
            }
        });

        javax.swing.GroupLayout exitPanelLayout = new javax.swing.GroupLayout(exitPanel);
        exitPanel.setLayout(exitPanelLayout);
        exitPanelLayout.setHorizontalGroup(
            exitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitLabel1)
        );
        exitPanelLayout.setVerticalGroup(
            exitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitLabel1))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(235, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(216, 216, 216)
                .addComponent(exitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(exitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(21, 21, 21))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 79));

        phoneNotf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        phoneNotf.setText("09");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Phone Number");

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("Gender");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Birth Date");

        birthDatetf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("First Name");

        firstNametf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Last Name");

        lastNametf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        femalerb.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        femalerb.setText("Female");

        malerb.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        malerb.setText("Male");

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(malerb)
                .addGap(18, 18, 18)
                .addComponent(femalerb)
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(malerb)
                    .addComponent(femalerb))
                .addContainerGap())
        );
        jLayeredPane1.setLayer(femalerb, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(malerb, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(phoneNotf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(firstNametf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(38, 38, 38)
                                        .addComponent(jLabel5))
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(birthDatetf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lastNametf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(firstNametf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastNametf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(birthDatetf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel5)))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneNotf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 330, -1));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Home No.");

        homeNotf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        homeNotf.setText("HN");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Kebele");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Woreda");

        woredatf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        woredatf.setText("Jimma");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("City");

        citytf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        citytf.setText("Jimma");

        kebeletf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel14.setText("Address in Jimma Zone");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(kebeletf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(citytf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(woredatf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(homeNotf, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jLabel14)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(27, 27, 27)
                        .addComponent(jLabel11))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(woredatf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(citytf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(kebeletf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(homeNotf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 120, -1, 240));

        submitButton.setBackground(new java.awt.Color(204, 255, 204));
        submitButton.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        submitButton.setForeground(new java.awt.Color(0, 102, 102));
        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 400, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabel1MousePressed
        dispose();
    }//GEN-LAST:event_exitLabel1MousePressed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        notInformed = true;
        try {
            String firstName = firstNametf.getText();
            String lastName = lastNametf.getText();
            String fullName = firstName + " " + lastName;
            String birthDate = birthDatetf.getText();

            String gender = null;
            if (malerb.isSelected()) {
                gender = "M";
            } else if (femalerb.isSelected()) {
                gender = "F";
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Please select customers gender!");
                notInformed = false;
            }

            int phoneNo = Integer.parseInt(phoneNotf.getText());

            String woreda = woredatf.getText();
            String city = citytf.getText();
            String kebele = kebeletf.getText();
            String homeNo = homeNotf.getText();

            boolean isRegistered = false;
            if (checkFullName(fullName) && checkPhoneNo(phoneNotf.getText()) && gender != null) {
                isRegistered = registerData(firstName, lastName, birthDate, gender, phoneNo, woreda, city, kebele, homeNo);
            }

            if (isRegistered) {
                addToRegisterHistory(fullName, cusId);
                JOptionPane.showMessageDialog(new JFrame(), "Successfully Added!");
                //printing
                {
                    setDateNow();
                    String date = "" + (year + "/" + month + "/" + day);
                    String message = "Jimma Water Billing Office"
                            + "\nCustomer Registeration Information"
                            + "\n--------------------------------------"
                            + "\nCustomer Name: " + fullName
                            + "\nCutomer id: " + cusId
                            + "\nBirth Date: " + birthDate
                            + "\nSex: " + gender
                            + "\nPhone Number: (+251)" + phoneNo
                            + "\nWoreda: " + woreda
                            + "\nCity: " + city
                            + "\nKebele: " + kebele
                            + "\nHome Number: " + homeNo
                            + "\nRegistered Date: " + date
                            + "\nThank You!";
                    printArea.setText(message);
                    printArea.print();
                }
                dispose();
            } else if (notInformed) {
                JOptionPane.showMessageDialog(new JFrame(), "Customers data is not added.\nPlease try again!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Please fill the required information correctly!");
        }
    }//GEN-LAST:event_submitButtonActionPerformed

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
            java.util.logging.Logger.getLogger(RegisterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegisterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegisterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegisterCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegisterCustomer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField birthDatetf;
    private javax.swing.JTextField citytf;
    private javax.swing.JLabel exitLabel1;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JRadioButton femalerb;
    private javax.swing.JTextField firstNametf;
    private javax.swing.JTextField homeNotf;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField kebeletf;
    private javax.swing.JTextField lastNametf;
    private javax.swing.JRadioButton malerb;
    private javax.swing.JTextField phoneNotf;
    private javax.swing.JTextArea printArea;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField woredatf;
    // End of variables declaration//GEN-END:variables
}
