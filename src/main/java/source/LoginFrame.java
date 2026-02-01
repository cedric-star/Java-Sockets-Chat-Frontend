package source;

import javax.swing.*;
import java.awt.*;

import source.app.MainAppWindow;

public class LoginFrame extends JFrame {
    JButton start = new JButton("Start App");
    JTextField userName = new JTextField(30);

    /**
     * Anmeldefenster, der Nutzer wird ausschließlich über seinen Namen identifiziert.
     * Dieses Fenster wird beim Starten der App zu Beginn angezeigt.
     */
    public LoginFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setGUI();
        setVisible(true);
    }

    /**
     * Basis Komponenten des Login definieren.
     * Wenn der Nutzer existiert werden seine Dateien angezeigt, wenn nicht, wird er neu erstellt.
     */
    private void setGUI() {
        setTitle("Login/Register");
        setSize(600, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(userName);
        panel.add(start);

        start.addActionListener(e -> startApp());
        userName.addActionListener(e -> startApp());

        add(panel);
    }

    /**
     * Leeren Nutzernamen verhinder.
     * Bei Anmeldung/Registrierung wird Eingabe zurückgesetzt, falls ein weiterer Nutzer die App
     * verwenden möchte.
     */
    private void startApp() {
        if (!userName.getText().isEmpty()) {
            new MainAppWindow(userName.getText());
            userName.setText("");
        }
        //setVisible(false);
    }
}
