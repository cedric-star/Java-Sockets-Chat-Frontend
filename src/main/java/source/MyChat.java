package source;

import javax.swing.*;
import java.awt.*;

public class MyChat extends JFrame {
    MyClient client;
    JTextArea chatDisplay = new JTextArea();
    JTextField messageInput = new JTextField();
    JButton send = new JButton("Send");
    String user;

    public MyChat(String user) {
        this.user = user;
        setLayout(new BorderLayout());
        setGUI();
        setVisible(true);

        // MyClient bekommt MyChat Instanz übergeben
        client = new MyClient(this);
    }

    private void setGUI() {
        setTitle("MyChat");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatDisplay.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatDisplay);

        JPanel inputPanel = new JPanel(new BorderLayout());

        messageInput.addActionListener(e -> sendMsg());
        send.addActionListener(e -> sendMsg());

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(send, BorderLayout.EAST);

        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMsg() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            messageInput.setText("");
        }
    }

    //wird von MyClient aufgerufen
    public void updateChatDisplay(String completeChat) {
        //Im UI-Thread ausführen
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatDisplay.setText(completeChat);
                chatDisplay.setCaretPosition(chatDisplay.getDocument().getLength());
            }
        });
    }

    public String getUser() {
        return user;
    }

    @Override
    public void dispose() {
        client.stopConnection();
        super.dispose();
    }
}