package source.app;

import source.IO;
import source.connection.MyClient;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainAppWindow extends JFrame {

    private JPanel panel1;
    private JButton addNewSong;
    private JButton changeWeb;
    private JButton showWeb;

    private JScrollPane scroller;
    private JPanel contentPanel;

    private String username;
    private MyClient client;
    private IO io;

    public MainAppWindow(String username) {
        this.username = username;
        this.io = IO.getInstance();
        this.client = new MyClient(this);

        setGui();
        refreshDisplay();

        setContentPane(panel1);
        setVisible(true);

        addNewSong.addActionListener(e -> addSong());
        changeWeb.addActionListener(e -> {new WebChangerWindow(client, username);});
        showWeb.addActionListener(e -> showMP3Website());
    }

    private void setGui() {
        panel1 = new JPanel(new BorderLayout());

        JPanel bottomBtnPanel = new JPanel(new FlowLayout());
        addNewSong = new JButton("Add New Song");
        changeWeb = new JButton("Change Web");
        showWeb = new JButton("View as Website");
        bottomBtnPanel.add(addNewSong);
        bottomBtnPanel.add(changeWeb);
        bottomBtnPanel.add(showWeb);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        scroller = new JScrollPane(contentPanel);

        panel1.add(scroller, BorderLayout.CENTER);
        panel1.add(bottomBtnPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
    }

    //wenn auf add new song geklickt
    private void addSong() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("mp3 file", "mp3"));

        if (chooser.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
            File mp3file = chooser.getSelectedFile();
            io.saveFile(username, mp3file);
            client.sendFile(username, mp3file);
            refreshDisplay();
        }
    }

    //updatet display immer (song anzeige)
    private void refreshDisplay() {
        contentPanel.removeAll();

        io.getAllMP3Data(username).forEach(mp3 -> {
            contentPanel.add(createSongPanel(mp3));
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    //baut aus einer mp3 (infos) ein panel, in forschleife für display verwendet
    private JPanel createSongPanel(ArrayList<String> mp3) {

        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        leftPanel.add(new JLabel("File: " + mp3.get(0)));

        JTextField titleField  = new JTextField(mp3.get(1));
        JTextField artistField = new JTextField(mp3.get(2));
        JTextField albumField  = new JTextField(mp3.get(3));
        JTextField genreField  = new JTextField(mp3.get(4));

        leftPanel.add(new JLabel("Title"));
        leftPanel.add(titleField);
        leftPanel.add(new JLabel("Artist"));
        leftPanel.add(artistField);
        leftPanel.add(new JLabel("Album"));
        leftPanel.add(albumField);
        leftPanel.add(new JLabel("Genre"));
        leftPanel.add(genreField);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> {
            io.deleteFile(username, mp3.get(0));
            contentPanel.remove(itemPanel);
            contentPanel.revalidate();
            contentPanel.repaint();

            client.deleteFile(username, mp3.get(0));
            client.sendFile(username, new File (new File (username+"_data"), username+"_music.xml"));
        });

        JButton saveBtn = new JButton("Save Edit");

        saveBtn.addActionListener(e -> {
            ArrayList<String> newMP3 = new ArrayList<String>();
            newMP3 = mp3;
            newMP3.set(1, titleField.getText());
            newMP3.set(2, artistField.getText());
            newMP3.set(3, albumField.getText());
            newMP3.set(4, genreField.getText());

            System.out.println("kkkkkkkkkk:"+newMP3.toString());
            io.updateMP3XMLAttributes(username, newMP3);

            client.sendFile(username, new File (new File (username+"_data"),newMP3.get(0)));
            client.sendFile(username, new File (new File (username+"_data"), username+"_music.xml"));
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(deleteBtn);
        rightPanel.add(saveBtn);

        itemPanel.add(leftPanel, BorderLayout.CENTER);
        itemPanel.add(rightPanel, BorderLayout.EAST);

        return itemPanel;
    }

    public String getUsername() {
        return username;
    }

    private void showMP3Website() {
        client.syncFiles(username);
        File baseDir = new File((username+"_data"));
        File html = new File(baseDir, username+"_index.html");

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(html.toURI());
            }
        } catch (Exception e2) {
            System.err.println(e2.getMessage());
        }
    }
}
//löschen von dateien wird glaube nicht korrekt synchronisiert
