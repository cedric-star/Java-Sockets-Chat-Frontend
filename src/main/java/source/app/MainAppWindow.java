package source.app;

import source.IO;
import source.connection.MyClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainAppWindow extends JFrame {
    private JPanel panel1;
    private JList musicList;
    private JButton addNewSong;
    private JButton changeWeb;

    private String username;
    private MyClient client;

    public MainAppWindow(String username) {
        this.username = username;
        client = new MyClient(this);

        setContentPane(panel1);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        setVisible(true);

        addNewSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(panel1);
                File file = chooser.getSelectedFile();
                client.sendFile(username, file);

                IO.saveFile(username, file);
            }
        });
        changeWeb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private JPanel genMusicDisplay() {
        return null;
    }
}
