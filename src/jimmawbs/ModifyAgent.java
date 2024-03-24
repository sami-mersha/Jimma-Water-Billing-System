package jimmawbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class ModifyAgent extends javax.swing.JFrame {

    int day, month, year;
    boolean notInformed;
    String userName, fullName;

    public ModifyAgent() {
        initComponents();
        birthDatetf.setText("Year-Month-Day");
        ButtonGroup G = new ButtonGroup();
        G.add(malerb);
        G.add(femalerb);
    }

    public void setDateNow() {
        String date = java.time.LocalDate.now().toString();
        String[] dateParts = date.split("-");
        day = Integer.parseInt(dateParts[2]);
        month = Integer.parseInt(dateParts[1]);
        year = Integer.parseInt(dateParts[0]);
    }

    public void fillAgentsData(String username) {
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.agent";

            ResultSet set = st.executeQuery(command);

            String firstName, lastName, gender, dob;
            int phoneNo, adCode;
            while (set.next()) {
                if (username.equals(set.getString("ag_username"))) {
                    userNameLabel.setText("Username: " + username);
                    userName = username;
                    firstName = set.getString("ag_firstname");
                    firstNametf.setText(firstName);
                    lastName = set.getString("ag_lastname");
                    lastNametf.setText(lastName);
                    fullName = firstName + " " + lastName;
                    userNameLabel.setText("Username: " + username);
                    dob = set.getString("ag_dob");
                    birthDatetf.setText(dob);
                    gender = set.getString("ag_sex");
                    if (gender.equals("M")) {
                        malerb.setSelected(true);
                    } else {
                        femalerb.setSelected(true);
                    }
                    phoneNo = set.getInt("ag_phonenumber");
                    phoneNotf.setText("0" + phoneNo);
                    adCode = set.getInt("ag_addresscode");

                    //finding address datas
                    String woreda, city, kebele, homeNo;
                    {
                        Statement st2 = cn.createStatement();
                        String cmd2 = "SELECT * FROM jwbs.address";
                        ResultSet set2 = st2.executeQuery(cmd2);
                        while (set2.next()) {
                            if (adCode == set2.getInt("ad_code")) {
                                woreda = set2.getString("ad_woreda");
                                woredatf.setText(woreda);
                                city = set2.getString("ad_city");
                                citytf.setText(city);
                                kebele = set2.getString("ad_kebele");
                                kebeletf.setText(kebele);
                                homeNo = set2.getString("ad_homenumber");
                                homeNotf.setText(homeNo);
                            }
                        }
                    }

                    break;
                }
            }
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

    public void addToModifiedHistory(String fullName, String username) {
        String event = "Agent with " + fullName + " name & with <" + username + "> username has been modified.";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            int his_code = generateHisCode();

            setDateNow();
            String dateNow = year + "-" + month + "-" + day;
            String cmd1 = "insert into history values(" + his_code + ", 'Ag. Modified', '" + event + "')";
            String cmd2 = "insert into ag_event values('" + username + "', " + his_code + ", '" + dateNow + "')";
            st.executeUpdate(cmd1);
            st.executeUpdate(cmd2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String generateCustomerId() {
        int amount = 1;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "SELECT * FROM jwbs.customer";
            ResultSet rset = st.executeQuery(cmd);
            while (rset.next()) {
                amount++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        int cus_code = amount + 3;

        String new_cus_id = "CS00000" + cus_code;
        return new_cus_id;
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

    public boolean verifyPassword() {
        boolean isValid = false;
        try {
            //prompt for password
            String password = null;
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Agent " + fullName + "'s Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                password = new String(pf.getPassword());
            }

            //extract username
            String username = userName;

            //check validity
            String pwd = null;
            Connection con = getConnection();
            Statement st = con.createStatement();

            String cmd = "select * from jwbs.agent";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (username.equals(set.getString("ag_username"))) {
                    pwd = set.getString("ag_password");
                    break;
                }
            }

            if (pwd.equals(password)) {
                isValid = true;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return isValid;
    }

    public boolean checkPassword(String firstPwd, String secondPwd) {
        JFrame jf = new JFrame();
        boolean valid = true;

        if (firstPwd == null) {
            JOptionPane.showMessageDialog(jf, "Please enter your password!");
            firstPasswordf.setText("Your password here...");
            valid = false;
            notInformed = false;
        } else if (secondPwd == null) {
            JOptionPane.showMessageDialog(jf, "Please confirm your password!");
            secondPasswordf.setText("Confirm your password here...");
            valid = false;
            notInformed = false;
        } else if (firstPwd.equals(secondPwd) == false) {
            JOptionPane.showMessageDialog(jf, "Passwords do not match!\nPlease try again!");
            valid = false;
            notInformed = false;
        } else if (secondPwd.length() > 16) {
            JOptionPane.showMessageDialog(jf, "Your password must be less than 16 character!");
            valid = false;
            notInformed = false;
        } else if (secondPwd.length() < 8) {
            JOptionPane.showMessageDialog(jf, "Your password must be greater or equal to 8 character.\nPlease enter another password!");
            valid = false;
            notInformed = false;
        } else if (secondPwd.equals("12345678")) {
            JOptionPane.showMessageDialog(jf, "The password is too easy to predict.\nPlease enter another password!");
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

    public boolean updateData(String firstName, String lastName, String password, String birthDate, String gender, int phoneNo, String woreda, String city, String kebele, String homeNo) {
        boolean registered = false;
        int adChecker = -1, cusChecker = -1;

        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            String cmd = "select * from jwbs.agent;";
            int adCode = 0;
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                if (userName.equals(set.getString("ag_username"))) {
                    adCode = set.getInt("ag_addresscode");
                }
            }

            cmd = "update jwbs.address set ad_woreda = '" + woreda + "', ad_city = '" + city + "', ad_kebele = '" + kebele + "', ad_homenumber = '" + homeNo + "' where ad_code = " + adCode + "";
            adChecker = st.executeUpdate(cmd);

            cmd = "update jwbs.agent set ag_firstname = '" + firstName + "', ag_lastname = '" + lastName + "', ag_password = '" + password + "', ag_dob = '" + birthDate + "', ag_sex = '" + gender + "', ag_phonenumber = '" + phoneNo + "' where ag_username = '" + userName + "'";

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
            JOptionPane.showMessageDialog(new JFrame(), "Please Check Agents Information(i.e Phone Number)!");
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
        userNameLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        firstPasswordf = new javax.swing.JPasswordField();
        secondPasswordf = new javax.swing.JPasswordField();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(218, 185, 158));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(126, 123, 148));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/plus_60px.png"))); // NOI18N
        jLabel1.setText("Modify Agent");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, exitPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(exitLabel1))
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
                .addGap(258, 258, 258)
                .addComponent(exitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
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

        userNameLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        userNameLabel.setText("Username: ");

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("New Password");

        jLabel15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel15.setText("Confirm Password");

        firstPasswordf.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        secondPasswordf.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(birthDatetf, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(firstNametf))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(38, 38, 38)
                            .addComponent(jLabel5)
                            .addGap(18, 18, 18)
                            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lastNametf, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(33, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(phoneNotf))
                    .addComponent(userNameLabel)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(firstPasswordf, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(secondPasswordf))))
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(birthDatetf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(userNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(firstPasswordf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(secondPasswordf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneNotf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(29, 29, 29))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 340, 340));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Home No.");

        homeNotf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Kebele");

        jLabel12.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel12.setText("Woreda");

        woredatf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("City");

        citytf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

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
        jPanel1.add(submitButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 470, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitLabel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitLabel1MousePressed
        dispose();
    }//GEN-LAST:event_exitLabel1MousePressed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        notInformed = true;
        if (verifyPassword()) {
            try {
                String firstName = firstNametf.getText();
                String lastName = lastNametf.getText();
                String fullName = firstName + " " + lastName;
                String firstPwd = firstPasswordf.getText();
                String secondPwd = secondPasswordf.getText();
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
                if (checkFullName(fullName) && checkPhoneNo(phoneNotf.getText()) && checkPassword(firstPwd, secondPwd) && gender != null) {
                    isModified = updateData(firstName, lastName, secondPwd, birthDate, gender, phoneNo, woreda, city, kebele, homeNo);
                }

                if (isModified) {
                    addToModifiedHistory(fullName, userName);
                    JOptionPane.showMessageDialog(new JFrame(), "Successfully Modified!");
                    dispose();
                } else if (notInformed) {
                    JOptionPane.showMessageDialog(new JFrame(), "Agents data is not added.\nPlease try again!");
                }
            } catch (Exception e) {
                System.out.println(e);
                JOptionPane.showMessageDialog(new JFrame(), "Please fill the required information correctly!");
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Incorrect Password!\nPlease try again!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
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
            java.util.logging.Logger.getLogger(ModifyAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModifyAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModifyAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModifyAgent.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ModifyAgent().setVisible(true);
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
    private javax.swing.JPasswordField firstPasswordf;
    private javax.swing.JTextField homeNotf;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField kebeletf;
    private javax.swing.JTextField lastNametf;
    private javax.swing.JRadioButton malerb;
    private javax.swing.JTextField phoneNotf;
    private javax.swing.JPasswordField secondPasswordf;
    private javax.swing.JButton submitButton;
    private javax.swing.JLabel userNameLabel;
    private javax.swing.JTextField woredatf;
    // End of variables declaration//GEN-END:variables
}
