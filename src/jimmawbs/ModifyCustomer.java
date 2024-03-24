package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ModifyCustomer extends javax.swing.JFrame {

    int day, month, year;
    boolean notInformed;
    String idNumber;

    public ModifyCustomer() {
        initComponents();
        
    }

    public void setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
    }

    public void addToModifyHistory(String fullName, String idNo) {
        String event = "Customer with " + fullName + " name & with <" + idNo + "> id has been modified.";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            int his_code = generateHisCode();

            setDateNow();
            String dateNow = year + "-" + month + "-" + day;
            String cmd1 = "insert into history values(" + his_code + ", 'Cus. Modified', '" + event + "')";
            String cmd2 = "insert into cus_event values('" + idNumber + "', " + his_code + ", '" + dateNow + "')";
            st.executeUpdate(cmd1);
            st.executeUpdate(cmd2);
        } catch (Exception e) {
            System.out.println(e);
        }
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

    public void fillCustomersData(String idn) {
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.customer";
            ResultSet set = st.executeQuery(command);

            String birthDate, gender, woreda, city, kebele, homeNo;
            int phoneNo, adCode = 0;
            while (set.next()) {
                if (idn.equals(set.getString("cus_id"))) {
                    firstNametf.setText(set.getString("cus_firstname"));
                    lastNametf.setText(set.getString("cus_lastname"));
                    idNoLabel.setText("Customer Id: " + idn);
                    birthDate = set.getString("cus_dob");
                    birthDatetf.setText(birthDate);
                    gender = set.getString("cus_sex");
                    if (gender.equals("M")) {
                        malerb.setSelected(true);
                    } else {
                        femalerb.setSelected(true);
                    }
                    phoneNo = set.getInt("cus_phonenumber");
                    phoneNotf.setText("0" + phoneNo);
                    adCode = set.getInt("cus_addresscode");
                    idNumber = idn;
                    break;
                }
            }

            command = "select * from jwbs.address";
            set = st.executeQuery(command);

            while (set.next()) {
                if (adCode == set.getInt("ad_code")) {
                    woreda = set.getString("ad_woreda");
                    woredatf.setText(woreda);
                    city = set.getString("ad_city");
                    citytf.setText(city);
                    kebele = set.getString("ad_kebele");
                    kebeletf.setText(kebele);
                    kebele = set.getString("ad_kebele");
                    kebeletf.setText(kebele);
                    homeNo = set.getString("ad_homenumber");
                    homeNotf.setText(homeNo);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
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

        if (fullName == null || fullName.equals("Full name Here...")) {
            JOptionPane.showMessageDialog(jf, "Please enter full name!");
            firstNametf.setText("First name Here...");
            lastNametf.setText("Last name Here...");
            valid = false;
            notInformed = false;
        } else if (fullName.length() > 25) {
            JOptionPane.showMessageDialog(jf, "Names must be less than 25 character!");
            valid = false;
            notInformed = false;
        }

        return valid;
    }

    //firstName, lastName, birthDate, gender, phoneNo, woreda, city, kebele, homeNo
    public boolean updateData(String id, String firstName, String lastName, String birthDate, String gender, int phoneNo, String woreda, String city, String kebele, String homeNo) {
        boolean registered = false;
        int adChecker = -1, cusChecker = -1;

        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "select * from jwbs.customer;";
            int adCode = 0;
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                if (idNumber.equals(set.getString("cus_id"))) {
                    adCode = set.getInt("cus_addresscode");
                }
            }

            cmd = "update jwbs.address set ad_woreda = '" + woreda + "', ad_city = '" + city + "', ad_kebele = '" + kebele + "', ad_homenumber = '" + homeNo + "' where ad_code = " + adCode + "";
            adChecker = st.executeUpdate(cmd);

            cmd = "update jwbs.customer set cus_firstname = '" + firstName + "', cus_lastname = '" + lastName + "', cus_dob = '" + birthDate + "', cus_sex = '" + gender + "', cus_phonenumber = '" + phoneNo + "' where cus_id = '" + id + "'";

            cusChecker = st.executeUpdate(cmd);
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }

        if (adChecker > 0 && cusChecker > 0) {
            registered = true;
        } else if (adChecker > 0) {
            System.out.println("Only address is updated");
            JOptionPane.showMessageDialog(new JFrame(), "Please Check Customer Information(i.e Phone Number)!");
        } else if (cusChecker > 0) {
            System.out.println("Only Customer is updated");
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        exitPanel = new javax.swing.JPanel();
        exitLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        submitButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        homeNotf = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        woredatf = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        citytf = new javax.swing.JTextField();
        kebeletf = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        phoneNotf = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        birthDatetf = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        firstNametf = new javax.swing.JTextField();
        malerb = new javax.swing.JRadioButton();
        femalerb = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        lastNametf = new javax.swing.JTextField();
        idNoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(195, 130, 182));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Gill Sans MT", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/rotate_left_60px.png"))); // NOI18N
        jLabel1.setText("Modify Customer");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

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
            .addGroup(exitPanelLayout.createSequentialGroup()
                .addComponent(exitLabel)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        exitPanelLayout.setVerticalGroup(
            exitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitLabel))
        );

        jPanel1.add(exitPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 80));

        jPanel2.setBackground(new java.awt.Color(190, 204, 234));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        jLabel7.setText("Personal Informations");

        jLabel13.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        jLabel13.setText("Location");

        submitButton.setBackground(new java.awt.Color(204, 255, 204));
        submitButton.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        submitButton.setForeground(new java.awt.Color(0, 102, 102));
        submitButton.setText("Submit");
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Home No.");

        homeNotf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Kebele");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Woreda");

        woredatf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText("City");

        citytf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        kebeletf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel15.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel15.setText("Address in Jimma Zone");

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
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING))
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
                        .addComponent(jLabel15)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
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

        phoneNotf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

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

        buttonGroup1.add(malerb);
        malerb.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        malerb.setText("Male");

        buttonGroup1.add(femalerb);
        femalerb.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        femalerb.setText("Female");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Last Name");

        lastNametf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

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
                                .addComponent(malerb)
                                .addGap(18, 18, 18)
                                .addComponent(femalerb)))
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
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
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(birthDatetf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(malerb)
                    .addComponent(femalerb))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneNotf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        idNoLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        idNoLabel.setText("Id: ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(292, 292, 292))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13)
                        .addGap(188, 188, 188))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(idNoLabel)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(68, 68, 68)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(46, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idNoLabel)
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(submitButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 800, 380));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabelMousePressed
        dispose();
    }//GEN-LAST:event_exitLabelMousePressed

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

            boolean isModified = false;

            if (checkFullName(fullName) && checkPhoneNo(phoneNotf.getText()) && gender != null) {
                isModified = updateData(idNumber, firstName, lastName, birthDate, gender, phoneNo, woreda, city, kebele, homeNo);
            }

            if (isModified) {
                addToModifyHistory(fullName, idNumber);
                JOptionPane.showMessageDialog(new JFrame(), "Successfully Modified!");
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
            java.util.logging.Logger.getLogger(ModifyCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModifyCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModifyCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModifyCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ModifyCustomer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField birthDatetf;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JTextField citytf;
    private javax.swing.JLabel exitLabel;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JRadioButton femalerb;
    private javax.swing.JTextField firstNametf;
    private javax.swing.JTextField homeNotf;
    private javax.swing.JLabel idNoLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField kebeletf;
    private javax.swing.JTextField lastNametf;
    private javax.swing.JRadioButton malerb;
    private javax.swing.JTextField phoneNotf;
    private javax.swing.JButton submitButton;
    private javax.swing.JTextField woredatf;
    // End of variables declaration//GEN-END:variables
}
