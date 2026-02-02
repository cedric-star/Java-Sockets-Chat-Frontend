package source.app;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends BaseFrame {
    JButton start = new JButton("Start App");
    JTextField userName = new JTextField(30);

    /**
     * Anmeldefenster, der Nutzer wird ausschließlich über seinen Namen identifiziert.
     * Dieses Fenster wird beim Starten der App zu Beginn angezeigt.
     */
    public LoginFrame() {
        super("Login/Register", "Login or Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setGui();
    }

    /**
     * Basis Komponenten des Login definieren.
     * Wenn der Nutzer existiert werden seine Dateien angezeigt, wenn nicht, wird er neu erstellt.
     */
    private void setGui() {
        setSize(600, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(userName);
        panel.add(start);

        start.addActionListener(e -> startApp());
        userName.addActionListener(e -> startApp());

        wrapper.add(panel, BorderLayout.CENTER);
    }

    /**
     * Leeren Nutzernamen verhinder.
     * Bei Anmeldung/Registrierung wird Eingabe zurückgesetzt, falls ein weiterer Nutzer die App
     * verwenden möchte.
     */
    private void startApp() {
        if (!userName.getText().isEmpty()) {
            new LibraryFrame(userName.getText());
            userName.setText("");
        }
        //setVisible(false);
    }
}
