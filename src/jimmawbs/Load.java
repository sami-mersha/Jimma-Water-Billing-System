package jimmawbs;

public class Load {
    public static void main(String[] args) {
        Splash s = new Splash();
        s.setVisible(true);
        Login m = new Login();
        m.setVisible(false);
        
        try {
            for (int i = 0; i <= 100; i++){
            Thread.sleep(30);
            s.labelPercent.setText(Integer.toString(i) + "%");
            s.progressBar.setValue(i);
            if(i == 100){
                s.setVisible(false);
                m.setVisible(true);
            }
        }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
}
