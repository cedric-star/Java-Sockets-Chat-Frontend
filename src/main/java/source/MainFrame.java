package source;

import source.chat.MyChat;
import source.serializer.MyThread;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    JButton newTFrame = new JButton("Start Local Editor");
    JButton newChat = new JButton("Start Chat");
    JTextField userName = new JTextField(30);

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setGUI();
        setVisible(true);
    }

    private void setGUI() {
        setTitle("MyÜbungen");
        setSize(600, 600);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(newTFrame);
        buttonPanel.add(newChat);
        buttonPanel.add(userName);

        newTFrame.addActionListener(e -> genTFrame());
        newChat.addActionListener(e -> genChat());
        userName.addActionListener(e -> genChat());

        add(buttonPanel);
    }

    private void genTFrame() {
        MyThread t = new MyThread();
        t.start();
    }

    private void genChat() {
        if (!userName.getText().isEmpty()) {
            new MyChat(userName.getText());
            userName.setText("");
        }

    }



}
