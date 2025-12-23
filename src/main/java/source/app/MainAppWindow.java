package source.app;

import source.IO;
import source.connection.MyClient;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class MainAppWindow extends JFrame {
    private JPanel panel1;
    private JList musicList;
    private JButton addNewSong;
    private JButton changeWeb;
    private JScrollPane scroller;

    private String username;
    private MyClient client;
    private DefaultListModel<String> listModel;

    public MainAppWindow(String username) {
        this.username = username;
        client = new MyClient(this);




        setContentPane(panel1);



        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);

        genMusicDisplay();

        setVisible(true);

        addNewSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("mp3 file", "mp3");
                chooser.setFileFilter(filter);

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

    //aufruf einmalig beim initiieren des frames
    private void genMusicDisplay() {
        ArrayList<File> files = IO.readAllMP3(this.username);
        System.out.println("test: length from mp3lsit"+files.size());

        listModel = new DefaultListModel<>();

        for  (File file : files) {
            listModel.addElement(file.getName());
        }

        musicList.setModel(listModel);

    }

    public String getUsername() {return username;}
}
