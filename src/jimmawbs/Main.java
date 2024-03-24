package jimmawbs;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class Main extends javax.swing.JFrame {

    String cusIdNumber = null, agUserName = null, cusFullName = null, agFullName = null;

    public Main() {
        initComponents();
        setColor(subPanelHome);
        printArea.setVisible(false);
        try {
            Connection c = getConnection();
            Statement s = c.createStatement();
            String cmd = "use jwbs";
            s.execute(cmd);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    int day, month, year;
    String ag_username = null;
    boolean notInformed = true;

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
    
    public int generateInvoiceCode() {
        int invCode = 1;

        try {
            Connection c = getConnection();
            Statement st = c.createStatement();

            String cmd = "select * from jwbs.invoice";
            ResultSet set = st.executeQuery(cmd);
            while (set.next()) {
                invCode++;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return invCode;
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

    public void addToAgentRemovedHistory(String userName, String fullName) {
        String event = "Agent with " + fullName + " name & with <" + userName + "> username has been removed.";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            int his_code = generateHisCode();

            String cmd1 = "insert into history values(" + his_code + ", 'Ag. Removal', '" + event + "')";
            st.executeUpdate(cmd1);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addToCustomerRemovedHistory(String id, String fullName) {
        String event = "Customer with " + fullName + " name holding <" + id + "> id has been removed.";

        try {
            int his_code = generateHisCode();
            Connection con = getConnection();
            Statement st = con.createStatement();
            String cmd = "insert into history values(" + his_code + ", 'Cus. Removal', '" + event + "')";
            st.executeUpdate(cmd);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void addToPaymentHistory(String cus_id, String cus_name) {
        String event = "New Payment by customer " + cus_name + " with <" + cus_id + "> id.";

        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            int his_code = generateHisCode();

            setDateNow();
            String dateNow = year + "-" + month + "-" + day;
            String cmd1 = "insert into history values(" + his_code + ", 'Payment', '" + event + "')";
            String cmd2 = "insert into cus_event values('" + cus_id + "', " + his_code + ", '" + dateNow + "')";
            st.executeUpdate(cmd1);
            st.executeUpdate(cmd2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void removeAgent(String userName) {

        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.agent";
            ResultSet set = st.executeQuery(command);
            String fullName = null;
            while (set.next()) {
                if (userName.equals(set.getString("ag_username"))) {
                    fullName = set.getString("ag_firstname") + " " + set.getString("ag_lastname");
                    agFullName = fullName;
                    break;
                }
            }

            int response = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to delete Agent " + fullName + "'s Data?", "Confirm to Delete", JOptionPane.YES_NO_CANCEL_OPTION);
            //0 means yes 1 means no 
            if (response == 0) {
                agUserName = userName;
                command = "DELETE FROM jwbs.agent WHERE ag_username = '" + userName + "'";
                int i = -1;
                i = st.executeUpdate(command);
                if (i > 0) {
                    JOptionPane.showMessageDialog(new JFrame(), "Successfully deleted!");
                    notInformed = false;
                }
            } else if (response == 1) {
                JOptionPane.showMessageDialog(new JFrame(), "The data is not deleted!");
                notInformed = false;
            } else {
                notInformed = false;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public boolean removeCustomer(String id) {
        boolean removed = false;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String command = "SELECT * FROM jwbs.customer";
            ResultSet set = st.executeQuery(command);
            String fullName = null, gender = null;
            while (set.next()) {
                if (id.equals(set.getString("cus_id"))) {
                    fullName = set.getString("cus_firstname") + " " + set.getString("cus_lastname");
                    cusFullName = fullName;
                    gender = set.getString("cus_sex");
                    break;
                }
            }

            command = "SELECT * FROM jwbs.cus_event";
            set = st.executeQuery(command);
            String dt = null;
            int mnth = 0;
            while (set.next()) {
                if (id.equals(set.getString("cus_id"))) {
                    dt = set.getString("ev_date");
                    String[] dateParts = dt.split("-");
                    mnth = Integer.parseInt(dateParts[1]);
                    setDateNow();
                    if (mnth != month && notInformed) {
                        if (gender == "M") {
                            JOptionPane.showMessageDialog(new JFrame(), "The customer should have to cover his monthly bill before deleting his account!");
                            return removed;
                        } else {
                            JOptionPane.showMessageDialog(new JFrame(), "The customer should have to cover her monthly bill before deleting his account!");
                            return removed;
                        }
                    } else if (notInformed) {
                        int response = JOptionPane.showConfirmDialog(new JFrame(), "Do you want to delete " + fullName + "'s Data?", "Confirm to Delete", JOptionPane.YES_NO_CANCEL_OPTION);
                        //0 means yes 1 means no 
                        if (response == 0) {
                            cusIdNumber = id;
                            command = "DELETE FROM jwbs.customer WHERE cus_id = '" + id + "'";
                            int i = -1;
                            i = st.executeUpdate(command);
                            
                            if (i > 0) {
                                JOptionPane.showMessageDialog(new JFrame(), "Successfully deleted!");
                                notInformed = false;
                                removed = true;
                            }
                        } else if (response == 1) {
                            JOptionPane.showMessageDialog(new JFrame(), "The data is not deleted!");
                            notInformed = false;
                        } else {
                            notInformed = false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return removed;
    }

    public void payBill(String id) {
        boolean notInformed = true;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();

            //find if the customer has already payed
            String cmd = "select * from jwbs.cus_event";
            ResultSet rset = st.executeQuery(cmd);
            setDateNow();
            int ed, em, ey;
            while (rset.next()) {
                if (id.equals(rset.getString("cus_id"))) {
                    String dt = rset.getString("ev_date");
                    int hCode = rset.getInt("his_code");
                    //find history type
                    String hType = null;
                    {
                        Statement st2 = cn.createStatement();
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
                        JOptionPane.showMessageDialog(new JFrame(), "This customer has already payed on " + edate + ".", "Customer Details", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }

            //finding the amount of credit that already exists
            cmd = "SELECT * FROM jwbs.customer";
            rset = st.executeQuery(cmd);

            String firstName = null, lastName = null, fullName = null;
            int cus_usagecode = 0;
            double us_amount = 0.0;
            double us_cost = 0.0;

            while (rset.next()) {
                if (id.equals(rset.getString("cus_id"))) {
                    cus_usagecode = rset.getInt("cus_usagecode");
                    firstName = rset.getString("cus_firstname");
                    lastName = rset.getString("cus_lastname");
                    fullName = firstName + " " + lastName;
                }
            }

            cmd = "SELECT * FROM jwbs.usage_statics";
            rset = st.executeQuery(cmd);

            while (rset.next()) {
                if (cus_usagecode == rset.getInt("us_code")) {
                    us_amount = rset.getDouble("us_amount");
                    us_cost = rset.getDouble("us_cost");
                }
            }

            if (us_amount == 0) {
                JOptionPane.showMessageDialog(new JFrame(), "Please fill usage amount for this customer<" + id + ">!", "Customer Details", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String statement = "Customer Name: " + fullName + "\nCustomer Id: " + id + "\nUsage Amount: " + us_amount + " m^3\nCost: " + us_cost + " Birr\n";
            JOptionPane.showMessageDialog(new JFrame(), statement, "Customer Details", JOptionPane.INFORMATION_MESSAGE);

            int decision = JOptionPane.showConfirmDialog(new JFrame(), "Existing Credit: " + us_cost + " birr.\nDo you want to make payment for " + fullName + "?", "Confirm to pay credit", JOptionPane.OK_CANCEL_OPTION);
            //decision = 0 means ok

            int inv_code = generateInvoiceCode();

            int a = -1, b = -1, c = -1;
            if (decision == 0) {
                cmd = "insert into jwbs.invoice values(" + inv_code + ", " + us_amount + ", " + us_cost + ", '" + ag_username + "')";
                a = st.executeUpdate(cmd);
                setDateNow();
                String dateNow = year + "-" + month + "-" + day;
                String cmd2 = "insert into payments values('" + id + "', " + inv_code + ", ' " + dateNow + " ')";
                b = st.executeUpdate(cmd2);

                String cmd4 = "SELECT * FROM jwbs.usage_statics";
                ResultSet rset4 = st.executeQuery(cmd4);

                while (rset4.next()) {
                    if (cus_usagecode == rset4.getInt("us_code")) {
                        Statement st5 = cn.createStatement();
                        String cmd5 = "update jwbs.usage_statics set us_amount = " + 0 + ", us_cost = " + 0 + " where us_code = " + cus_usagecode;
                        c = st5.executeUpdate(cmd5);
                    }
                }
            } else {
                notInformed = false;
            }

            if (a > 0 && b > 0 && c > 0) {
                String message = "Jimma Water Billing Office"
                        + "\n-----Invoice Information---------"
                        + "\n---------------------------------"
                        + "\nCustomer Name: " + fullName
                        + "\nCutomer id: " + id
                        + "\nUsage Amount: " + us_amount + " meter cube"
                        + "\nCost for this month(" + month + "): " + us_cost + " birr"
                        + "\nPayed to Agent: " + ag_username
                        + "\nThank You!";
                printArea.setText(message);
                addToPaymentHistory(id, fullName);
                JOptionPane.showMessageDialog(new JFrame(), "Successfully Payed!");
                printArea.print();
            } else if (notInformed) {
                JOptionPane.showMessageDialog(new JFrame(), "failed to update the Payment!\nPlease try again!");
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public boolean verifyPassword() {
        boolean isValid = false;
        try {
            //prompt for password
            String password = null;
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Your Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (okCxl == JOptionPane.OK_OPTION) {
                password = new String(pf.getPassword());
            }

            //extract username
            String username = ag_username;

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

    public boolean findUsername(String username) {
        boolean found = false;
        try {
            Connection cn = getConnection();
            Statement st = cn.createStatement();
            String cmd = "SELECT * FROM jwbs.agent";
            ResultSet set = st.executeQuery(cmd);

            while (set.next()) {
                if (set.getString("ag_username").equals(username)) {
                    found = true;
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return found;
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

    private void setColor(JPanel jp) {
        jp.setBackground(new Color(153, 0, 153));
    }

    private void resetColor(JPanel jp) {
        jp.setBackground(new Color(74, 54, 82));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Main = new javax.swing.JPanel();
        panelHome = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        subPanelHome = new javax.swing.JPanel();
        labelHome = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        subPanelCustomers = new javax.swing.JPanel();
        labelCustomers = new javax.swing.JLabel();
        subPanelAccounts = new javax.swing.JPanel();
        labelAccounts = new javax.swing.JLabel();
        subPanelAbout = new javax.swing.JPanel();
        labelAbout = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelAbout = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        panelCustomers = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        panelAccounts = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        deletePanel = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        panelAbout1 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        printArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Main.setBackground(new java.awt.Color(115, 65, 137));
        Main.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelHome.setBackground(new java.awt.Color(137, 96, 212));
        panelHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel7.setBackground(new java.awt.Color(132, 60, 139));

        jLabel3.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));
        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel3MousePressed(evt);
            }
        });
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/cheap_2_100px.png"))); // NOI18N
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel10MousePressed(evt);
            }
        });
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        jLabel4.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 0, 102));
        jLabel4.setText("Pay Bill");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 150, -1));

        jPanel4.setBackground(new java.awt.Color(204, 204, 255));
        jPanel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel4MousePressed(evt);
            }
        });
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/list_100px.png"))); // NOI18N
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel11MousePressed(evt);
            }
        });
        jPanel4.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 110, -1));

        jLabel5.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 0, 102));
        jLabel5.setText("Fill Data");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 170, -1));

        jPanel9.setBackground(new java.awt.Color(204, 204, 255));
        jPanel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel9MousePressed(evt);
            }
        });
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/time_machine_100px.png"))); // NOI18N
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel17MousePressed(evt);
            }
        });
        jPanel9.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, -1, -1));

        jLabel8.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 0, 102));
        jLabel8.setText("History");
        jLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel8MousePressed(evt);
            }
        });
        jPanel9.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 170, -1));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(600, 600, 600)
                        .addComponent(jLabel3))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel3)
                .addGap(30, 30, 30)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        panelHome.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 760, 420));

        jLabel9.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/home_64px.png"))); // NOI18N
        jLabel9.setText("HOME");
        panelHome.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, -1, 70));

        Main.add(panelHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, -3, 970, 690));

        subPanelHome.setBackground(new java.awt.Color(153, 0, 153));
        subPanelHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                subPanelHomeMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                subPanelHomeMousePressed(evt);
            }
        });
        subPanelHome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelHome.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        labelHome.setForeground(new java.awt.Color(255, 255, 255));
        labelHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/home_64px.png"))); // NOI18N
        labelHome.setText("Home");
        labelHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelHomeMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                labelHomeMousePressed(evt);
            }
        });
        subPanelHome.add(labelHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 70));

        Main.add(subPanelHome, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 230, 90));

        jLabel2.setFont(new java.awt.Font("Montserrat Medium", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Jimma Water Office");
        Main.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, -1));

        subPanelCustomers.setBackground(new java.awt.Color(74, 54, 82));
        subPanelCustomers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                subPanelCustomersMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                subPanelCustomersMousePressed(evt);
            }
        });
        subPanelCustomers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelCustomers.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        labelCustomers.setForeground(new java.awt.Color(255, 255, 255));
        labelCustomers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/add_user_group_man_man_64px.png"))); // NOI18N
        labelCustomers.setText("Customers");
        labelCustomers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelCustomersMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                labelCustomersMousePressed(evt);
            }
        });
        subPanelCustomers.add(labelCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 70));

        Main.add(subPanelCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 210, 230, 90));

        subPanelAccounts.setBackground(new java.awt.Color(74, 54, 82));
        subPanelAccounts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                subPanelAccountsMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                subPanelAccountsMousePressed(evt);
            }
        });
        subPanelAccounts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelAccounts.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        labelAccounts.setForeground(new java.awt.Color(255, 255, 255));
        labelAccounts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/crown_64px.png"))); // NOI18N
        labelAccounts.setText("Accounts");
        labelAccounts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelAccountsMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                labelAccountsMousePressed(evt);
            }
        });
        subPanelAccounts.add(labelAccounts, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 70));

        Main.add(subPanelAccounts, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 300, 230, 90));

        subPanelAbout.setBackground(new java.awt.Color(74, 54, 82));
        subPanelAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                subPanelAboutMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                subPanelAboutMousePressed(evt);
            }
        });
        subPanelAbout.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelAbout.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        labelAbout.setForeground(new java.awt.Color(255, 255, 255));
        labelAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/info_64px.png"))); // NOI18N
        labelAbout.setText("About");
        labelAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelAboutMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                labelAboutMousePressed(evt);
            }
        });
        subPanelAbout.add(labelAbout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, -1, 70));

        Main.add(subPanelAbout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 390, 230, 90));

        jLabel6.setFont(new java.awt.Font("Montserrat Medium", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Jimma City Adminstration");
        Main.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 630, -1, -1));

        jLabel7.setFont(new java.awt.Font("Montserrat Medium", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("2022");
        Main.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 610, -1, -1));

        panelAbout.setBackground(new java.awt.Color(164, 117, 212));
        panelAbout.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel12.setBackground(new java.awt.Color(132, 60, 139));

        jLabel23.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("This software is developed by peer group 5 Electrical and");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Electrical Engineering Students at Jimma Institute of Technology");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23))
                    .addComponent(jLabel24))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel23))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel1)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel24)))
                .addContainerGap(294, Short.MAX_VALUE))
        );

        panelAbout.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 760, 420));

        jLabel28.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/info_64px.png"))); // NOI18N
        jLabel28.setText("ABOUT");
        panelAbout.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, -1, 70));

        Main.add(panelAbout, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, -3, 970, 690));

        panelCustomers.setBackground(new java.awt.Color(137, 173, 202));
        panelCustomers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel17.setBackground(new java.awt.Color(132, 60, 139));

        jLabel34.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 255));

        jPanel16.setBackground(new java.awt.Color(204, 204, 255));
        jPanel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel16MousePressed(evt);
            }
        });
        jPanel16.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/add_user_male_100px.png"))); // NOI18N
        jPanel16.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, -1));

        jLabel33.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(51, 0, 102));
        jLabel33.setText("Register");
        jPanel16.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 160, -1));

        jPanel6.setBackground(new java.awt.Color(204, 204, 255));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel6MousePressed(evt);
            }
        });
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/find_and_replace_100px.png"))); // NOI18N
        jPanel6.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel15.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 0, 102));
        jLabel15.setText("Search");
        jPanel6.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 170, -1));

        jPanel15.setBackground(new java.awt.Color(204, 204, 255));
        jPanel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel15MousePressed(evt);
            }
        });
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/edit_property_100px.png"))); // NOI18N
        jPanel15.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel31.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(51, 0, 102));
        jLabel31.setText("Modify");
        jPanel15.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 170, -1));

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel5MousePressed(evt);
            }
        });
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/delete_bin_100px.png"))); // NOI18N
        jPanel5.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel13.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 0, 102));
        jLabel13.setText("Delete");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 160, -1));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(600, 600, 600)
                        .addComponent(jLabel34))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel34)
                .addGap(30, 30, 30)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelCustomers.add(jPanel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 760, 420));

        jLabel39.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/add_user_group_man_man_64px.png"))); // NOI18N
        jLabel39.setText("CUSTOMERS");
        panelCustomers.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, -1, 70));

        Main.add(panelCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, -3, 970, 690));

        panelAccounts.setBackground(new java.awt.Color(206, 146, 212));
        panelAccounts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel18.setBackground(new java.awt.Color(132, 60, 139));

        jLabel35.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));

        jPanel19.setBackground(new java.awt.Color(204, 204, 255));
        jPanel19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel19MousePressed(evt);
            }
        });
        jPanel19.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/add_user_male_100px.png"))); // NOI18N
        jPanel19.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, -1));

        jLabel37.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(51, 0, 102));
        jLabel37.setText("Register");
        jPanel19.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 160, -1));

        jPanel8.setBackground(new java.awt.Color(204, 204, 255));
        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel8MousePressed(evt);
            }
        });
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/find_and_replace_100px.png"))); // NOI18N
        jPanel8.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel38.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(51, 0, 102));
        jLabel38.setText("Search");
        jPanel8.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 170, -1));

        jPanel20.setBackground(new java.awt.Color(204, 204, 255));
        jPanel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel20MousePressed(evt);
            }
        });
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/edit_property_100px.png"))); // NOI18N
        jPanel20.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel41.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(51, 0, 102));
        jLabel41.setText("Modify");
        jPanel20.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 170, -1));

        deletePanel.setBackground(new java.awt.Color(204, 204, 255));
        deletePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                deletePanelMousePressed(evt);
            }
        });
        deletePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/delete_bin_100px.png"))); // NOI18N
        deletePanel.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel43.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(51, 0, 102));
        jLabel43.setText("Delete");
        deletePanel.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 160, -1));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(600, 600, 600)
                        .addComponent(jLabel35))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(deletePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel35)
                .addGap(30, 30, 30)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deletePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelAccounts.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 760, 420));

        jLabel44.setFont(new java.awt.Font("Montserrat Medium", 0, 36)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/jimmawbs/assets/crown_64px.png"))); // NOI18N
        jLabel44.setText("ACCOUNTS");
        panelAccounts.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, -1, 70));

        Main.add(panelAccounts, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, -3, 970, 690));

        panelAbout1.setBackground(new java.awt.Color(164, 117, 212));
        panelAbout1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel13.setBackground(new java.awt.Color(132, 60, 139));

        jLabel25.setFont(new java.awt.Font("Montserrat Medium", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));

        printArea.setColumns(20);
        printArea.setRows(5);
        jScrollPane2.setViewportView(printArea);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(617, 617, 617)
                        .addComponent(jLabel25))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelAbout1.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 190, 760, 420));

        Main.add(panelAbout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, -3, 970, 690));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Main, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void subPanelHomeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelHomeMouseExited
        setColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
    }//GEN-LAST:event_subPanelHomeMouseExited

    private void subPanelCustomersMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelCustomersMouseExited
        resetColor(subPanelHome);
        setColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
    }//GEN-LAST:event_subPanelCustomersMouseExited

    private void labelHomeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelHomeMouseExited
        setColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
    }//GEN-LAST:event_labelHomeMouseExited

    private void labelCustomersMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCustomersMouseExited
        resetColor(subPanelHome);
        setColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
    }//GEN-LAST:event_labelCustomersMouseExited

    private void labelAccountsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAccountsMouseExited
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        setColor(subPanelAccounts);
        resetColor(subPanelAbout);
    }//GEN-LAST:event_labelAccountsMouseExited

    private void subPanelAccountsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelAccountsMouseExited
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        setColor(subPanelAccounts);
        resetColor(subPanelAbout);
    }//GEN-LAST:event_subPanelAccountsMouseExited

    private void labelAboutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAboutMouseExited
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        setColor(subPanelAbout);
    }//GEN-LAST:event_labelAboutMouseExited

    private void subPanelAboutMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelAboutMouseExited
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        setColor(subPanelAbout);
    }//GEN-LAST:event_subPanelAboutMouseExited

    private void labelHomeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelHomeMousePressed
        setColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
        panelHome.setVisible(true);
        panelCustomers.setVisible(false);
        panelAccounts.setVisible(false);
        panelAbout.setVisible(false);
    }//GEN-LAST:event_labelHomeMousePressed

    private void subPanelHomeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelHomeMousePressed
        setColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
        panelHome.setVisible(true);
        panelCustomers.setVisible(false);
        panelAccounts.setVisible(false);
        panelAbout.setVisible(false);
    }//GEN-LAST:event_subPanelHomeMousePressed

    private void labelCustomersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelCustomersMousePressed
        resetColor(subPanelHome);
        setColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
        panelHome.setVisible(false);
        panelCustomers.setVisible(true);
        panelAccounts.setVisible(false);
        panelAbout.setVisible(false);
    }//GEN-LAST:event_labelCustomersMousePressed

    private void subPanelCustomersMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelCustomersMousePressed
        resetColor(subPanelHome);
        setColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        resetColor(subPanelAbout);
        panelHome.setVisible(false);
        panelCustomers.setVisible(true);
        panelAccounts.setVisible(false);
        panelAbout.setVisible(false);
    }//GEN-LAST:event_subPanelCustomersMousePressed

    private void labelAccountsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAccountsMousePressed
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        setColor(subPanelAccounts);
        resetColor(subPanelAbout);
        panelHome.setVisible(false);
        panelCustomers.setVisible(false);
        panelAccounts.setVisible(true);
        panelAbout.setVisible(false);
    }//GEN-LAST:event_labelAccountsMousePressed

    private void subPanelAccountsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelAccountsMousePressed
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        setColor(subPanelAccounts);
        resetColor(subPanelAbout);
        panelHome.setVisible(false);
        panelCustomers.setVisible(false);
        panelAccounts.setVisible(true);
        panelAbout.setVisible(false);
    }//GEN-LAST:event_subPanelAccountsMousePressed

    private void labelAboutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelAboutMousePressed
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        setColor(subPanelAbout);
        panelHome.setVisible(false);
        panelCustomers.setVisible(false);
        panelAccounts.setVisible(false);
        panelAbout.setVisible(true);
    }//GEN-LAST:event_labelAboutMousePressed

    private void subPanelAboutMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_subPanelAboutMousePressed
        printArea.setVisible(false);
        resetColor(subPanelHome);
        resetColor(subPanelCustomers);
        resetColor(subPanelAccounts);
        setColor(subPanelAbout);
        panelHome.setVisible(false);
        panelCustomers.setVisible(false);
        panelAccounts.setVisible(false);
        panelAbout.setVisible(true);
    }//GEN-LAST:event_subPanelAboutMousePressed

    private void jLabel10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MousePressed
        notInformed = true;
        String id = JOptionPane.showInputDialog(new JFrame(), "Enter Customer Id ");

        boolean isFound = findId(id);

        if (id.equals("")) {
            JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
        } else if (isFound) {
            payBill(id);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jLabel10MousePressed

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
        String id = JOptionPane.showInputDialog(new JFrame(), "Enter Customer Id ");
        notInformed = true;
        boolean isFound = findId(id);

        if (id.equals("")) {
            JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
        } else if (isFound) {
            payBill(id);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jLabel4MousePressed

    private void jPanel3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MousePressed
        String id = JOptionPane.showInputDialog(new JFrame(), "Enter Customer Id ");
        notInformed = true;
        boolean isFound = findId(id);

        if (id.equals("")) {
            JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
        } else if (isFound) {
            payBill(id);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPanel3MousePressed

    private void jLabel11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MousePressed
        notInformed = true;
        if (verifyPassword()) {
            DataZone dz = new DataZone();
            dz.fillCustomersTable();
            dz.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Invalid Password!\nPlease try again!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jLabel11MousePressed

    private void jPanel16MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel16MousePressed
        new RegisterCustomer().setVisible(true);
    }//GEN-LAST:event_jPanel16MousePressed

    private void jPanel6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MousePressed
        String id = JOptionPane.showInputDialog(new JFrame(), "Enter Id:");
        notInformed = true;
        boolean isFound = findId(id);

        if (id.equals("")) {
            JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
        } else if (isFound) {
            SearchResult sr = new SearchResult();
            sr.fillCustomersData(id);
            sr.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPanel6MousePressed

    private void jPanel15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel15MousePressed
        notInformed = true;
        String id = JOptionPane.showInputDialog(new JFrame(), "Enter Id ");

        boolean isFound = findId(id);

        if (id.equals("")) {
            JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
        } else if (isFound) {
            ModifyCustomer mc = new ModifyCustomer();
            mc.fillCustomersData(id);
            mc.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPanel15MousePressed

    private void jPanel5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel5MousePressed
        String id = JOptionPane.showInputDialog(new JFrame(), "Enter Id ");
        notInformed = true;
        boolean isFound = findId(id);

        double usCost = new DataZone().getUsageCost(id);
        
        if (id.equals("")) {
            JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
        } else if (isFound && (paymentDone(id) || usCost == 0)) {
            boolean isRemoved = removeCustomer(id);
            if (isRemoved) {
                addToCustomerRemovedHistory(id, cusFullName);
            }
        } else if (isFound) {
            JOptionPane.showMessageDialog(new JFrame(), "Customer with < "+ id +" > id has to pay his bill first!", "Illegal Attempt", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPanel5MousePressed

    private void jPanel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MousePressed
        notInformed = true;
        if (verifyPassword()) {
            DataZone dz = new DataZone();
            dz.fillCustomersTable();
            dz.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Invalid Password!\nPlease try again!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPanel4MousePressed

    private void jPanel8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MousePressed
        notInformed = true;
        if (verifyPassword()) {
            String userName = JOptionPane.showInputDialog(new JFrame(), "Enter Username:");

            boolean isFound = findUsername(userName);

            if (userName.equals("")) {
                JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
            } else if (isFound) {
                AccountSearchResult asr = new AccountSearchResult();
                asr.fillAgentsData(userName);
                asr.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Incorrect Password!\nPlease try again!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jPanel8MousePressed

    private void jPanel19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel19MousePressed
        notInformed = true;
        if (verifyPassword()) {
            new RegisterAgent().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Incorrect Password!\nPlease try again!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jPanel19MousePressed

    private void jPanel20MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel20MousePressed
        notInformed = true;
        if (verifyPassword()) {
            String userName = JOptionPane.showInputDialog(new JFrame(), "Enter Username:");

            boolean isFound = findUsername(userName);

            if (userName.equals("")) {
                JOptionPane.showMessageDialog(new JFrame(), "Id field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
            } else if (isFound) {
                ModifyAgent ma = new ModifyAgent();
                ma.fillAgentsData(userName);
                ma.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "This id is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Incorrect Password!\nPlease try again!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jPanel20MousePressed

    private void deletePanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deletePanelMousePressed
        notInformed = true;
        if (verifyPassword()) {
            String userName = JOptionPane.showInputDialog(new JFrame(), "Enter username ");

            if (userName.equals(ag_username)) {
                System.out.println("error username");
                JOptionPane.showMessageDialog(new JFrame(), "You cannot delete your own account from here!\nPlease contact another agent!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean isFound = findUsername(userName);

                if (userName.equals("")) {
                    JOptionPane.showMessageDialog(new JFrame(), "Username field is empty!\nPlease Try again! ", "LoginError", JOptionPane.ERROR_MESSAGE);
                } else if (isFound) {
                    removeAgent(userName);
                    addToAgentRemovedHistory(userName, cusFullName);
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "This username is not registered!\nPlease try again!", "LoginError", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Incorrect Password!\nPlease try again!", "Authentication Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_deletePanelMousePressed

    private void jLabel17MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MousePressed
        notInformed = true;
        History h = new History();
        h.fillHistoryTable();
        h.setVisible(true);
    }//GEN-LAST:event_jLabel17MousePressed

    private void jPanel9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel9MousePressed
        notInformed = true;
        History h = new History();
        h.fillHistoryTable();
        h.setVisible(true);
    }//GEN-LAST:event_jPanel9MousePressed

    private void jLabel8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel8MousePressed
        notInformed = true;
        History h = new History();
        h.fillHistoryTable();
        h.setVisible(true);
    }//GEN-LAST:event_jLabel8MousePressed

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Main;
    private javax.swing.JPanel deletePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelAbout;
    private javax.swing.JLabel labelAccounts;
    private javax.swing.JLabel labelCustomers;
    private javax.swing.JLabel labelHome;
    private javax.swing.JPanel panelAbout;
    private javax.swing.JPanel panelAbout1;
    private javax.swing.JPanel panelAccounts;
    private javax.swing.JPanel panelCustomers;
    private javax.swing.JPanel panelHome;
    private javax.swing.JTextArea printArea;
    private javax.swing.JPanel subPanelAbout;
    private javax.swing.JPanel subPanelAccounts;
    private javax.swing.JPanel subPanelCustomers;
    private javax.swing.JPanel subPanelHome;
    // End of variables declaration//GEN-END:variables
}
