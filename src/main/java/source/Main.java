package source;

import javax.swing.*;

public class Main {

    /**
     * App soll mit dem Login Fenster starten.
     * @param args
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Couldn't load look and feel");
        }
        new LoginFrame();
    }
}