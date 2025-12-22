package source;

import source.app.MainAppWindow;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    JButton start = new JButton("Start App");
    JTextField userName = new JTextField(30);

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setGUI();
        setVisible(true);
    }

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

    private void startApp() {
        if (!userName.getText().isEmpty()) {
            new MainAppWindow(userName.getText());
            userName.setText("");
        }
        setVisible(false);
    }



}
