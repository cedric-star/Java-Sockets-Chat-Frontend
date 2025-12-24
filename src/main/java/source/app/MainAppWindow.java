package source.app;

import source.IO;
import source.connection.MyClient;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class MainAppWindow extends JFrame {
    private JPanel panel1;
    private JList<MusicItem> musicList;
    private JButton addNewSong;
    private JButton changeWeb;
    private JScrollPane scroller;

    private String username;
    private MyClient client;
    private DefaultListModel<MusicItem> listModel;
    private IO io;

    public MainAppWindow(String username) {
        this.io = IO.getInstance();
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
                File mp3file = chooser.getSelectedFile();

                File newFile = io.saveFile(username, mp3file);
                MusicItem newItem = new MusicItem(newFile, username);
                listModel.addElement(newItem);

                client.sendFile(username, mp3file);

            }
        });
        changeWeb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    //aufruf einmalig beim initiieren des frames
    // Vereinfachte Version in MainAppWindow:
    private void genMusicDisplay() {
        ArrayList<File> files = io.readAllMP3(this.username);
        listModel = new DefaultListModel<>();
        musicList.setModel(listModel);

        //einmal für jedes listenelement rendern
        musicList.setCellRenderer(new ListCellRenderer<MusicItem>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends MusicItem> list,
                                                          MusicItem value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {

                JPanel panel = new JPanel(new BorderLayout());

                // Hier verwenden wir getTitle() vom MusicItem
                JTextArea textArea = new JTextArea(value.toString());
                textArea.setEditable(false);

                JPanel btnPanel = new JPanel(new BorderLayout());
                JButton playBtn = new JButton("Edit");
                JButton delBtn = new JButton("Delete");

                btnPanel.add(playBtn, BorderLayout.NORTH);
                btnPanel.add(delBtn, BorderLayout.SOUTH);

                panel.add(textArea, BorderLayout.CENTER);
                panel.add(btnPanel, BorderLayout.EAST);

                return panel;
            }
        });

        for (File file : files) {
            MusicItem item = new MusicItem(file, username);
            listModel.addElement(item);
        }

    }

    public String getUsername() {return username;}

}
