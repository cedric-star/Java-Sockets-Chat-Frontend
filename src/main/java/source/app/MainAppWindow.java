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
    private JButton addNewSong;
    private JButton changeWeb;
    private JScrollPane scroller;
    private JPanel contentPanel;

    private String username;
    private MyClient client;
    private DefaultListModel<MusicItem> listModel;
    private IO io;

    public MainAppWindow(String username) {
        this.io = IO.getInstance();
        this.username = username;
        client = new MyClient(this);

        setGui();
        setContentPane(panel1);
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

    private void setGui() {
        JPanel bottomBtnPanel = new JPanel(new FlowLayout());
        bottomBtnPanel.add(addNewSong = new JButton("Add New Song"), BorderLayout.SOUTH);
        bottomBtnPanel.add(changeWeb = new JButton("Change Web"), BorderLayout.SOUTH);

        genMusicDisplay();
        scroller = new JScrollPane(contentPanel);

        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());

        panel1.add(bottomBtnPanel, BorderLayout.SOUTH);
        panel1.add(scroller, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
    }

    //aufruf einmalig beim initiieren des frames
    // Vereinfachte Version in MainAppWindow:
    private void genMusicDisplay() {
        ArrayList<File> files = io.readAllMP3(this.username);
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        for (File file : files) {
            MusicItem item = new MusicItem(file, username);

            JPanel itemPanel = new JPanel(new BorderLayout());


            JPanel attributePanel = new JPanel();
            attributePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            attributePanel.add(new JLabel(item.getMP3FileName()+ " | "));


            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            titlePanel.add(new JLabel("Title: "));
            JTextField titleField = new JTextField(item.getTitle());
            titlePanel.add(titleField);
            attributePanel.add(titlePanel);

            JPanel genrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            genrePanel.add(new JLabel("Genre: "));
            JTextField genreField = new JTextField(item.getGenre());
            genrePanel.add(genreField);
            attributePanel.add(genrePanel);


            itemPanel.add(attributePanel, BorderLayout.WEST);


            JButton delBtn = new JButton("Löschen");
            delBtn.addActionListener(e -> {
                contentPanel.remove(itemPanel);
                contentPanel.revalidate();
                contentPanel.repaint();

            });

            JButton editBtn = new JButton("Edit");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(delBtn);
            buttonPanel.add(editBtn);

            itemPanel.add(buttonPanel, BorderLayout.EAST);
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            contentPanel.add(itemPanel);


        }
    }

    public String getUsername() {return username;}

}
