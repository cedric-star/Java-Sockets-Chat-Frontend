package source.app;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import source.IO;
import source.connection.MyClient;

public class LibraryFrame extends BaseFrame {

    private JPanel panel1;
    private JButton addNewSong;
    private JButton changeWeb;
    private JButton showWeb;
    private JScrollPane scroller;
    private JPanel contentPanel;

    private String user;
    private MyClient client;
    private IO io;

    /**
     * Dieses Fenster zeigt die Songs an. Hierüber können die Attribute wie Titel auch
     * geändert werden.
     * @param user
     */
    public LibraryFrame(String user) {
        super("Song Library", "Songs for: "+user);
        this.user = user;
        this.io = IO.getInstance();
        this.client = new MyClient(this);

        setGui();
        refreshDisplay();

        addNewSong.addActionListener(e -> addSong());
        changeWeb.addActionListener(e -> {new WebChangerFrame(client, user);});
        showWeb.addActionListener(e -> showMP3Website());
    }

    /**
     * Anordnung und verschachteln der Komponenten.
     */
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

        wrapper.add(panel1, BorderLayout.CENTER);
    }

    /**
     * Diese Methode wird ausgeführt, wenn der Button gedrückt wird, mit dem ein weiterer
     * Song in die Bibliothek aufgenommen werden soll.
     */
    private void addSong() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("mp3 file", "mp3"));

        if (chooser.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
            File mp3file = chooser.getSelectedFile();
            io.saveFile(user, mp3file);
            client.sendFile(user, mp3file);
            refreshDisplay();
        }
    }

    /**
     * Das display listet alle Songs auf.
     * Mit dieser Methode wird das Display neu erstellt, und zeichnet es neu, wenn ein Song
     * hinzugefügt oder entfernt wurde.
     * Ausschließlich der Inhalt des scrollers wird dabei aktualisiert.
     */
    private void refreshDisplay() {
        contentPanel.removeAll();

        io.getAllMP3Data(user).forEach(mp3 -> {
            contentPanel.add(createSongPanel(mp3));
        });
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Diese Methode nimmt Daten zu einer MP3-Datei und erstellt Anzeige mit Eingabefeldern zum
     * Ändern der Metadaten.
     * @param mp3 Strings sind nacheinander die Informationen über eine MP3-Datei.
     *            Nacheinander: Dateiname, Titel, Artist, Album, Genre.
     * @return @return Gibt Panel zu einem Song zurück.
     */
    private JPanel createSongPanel(ArrayList<String> mp3) {
        JPanel itemPanel = new JPanel(new BorderLayout());

        //Damit einzelne Zeilen später nicht über Bildschirm verteilt sind.
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
            io.deleteFile(user, mp3.get(0));
            contentPanel.remove(itemPanel);
            contentPanel.revalidate();
            contentPanel.repaint();

            client.deleteFile(user, mp3.get(0));
            client.sendFile(user, new File (new File (user +"_data"), user +"_music.xml"));
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
            io.updateMP3XMLAttributes(user, newMP3);

            client.sendFile(user, new File (new File (user +"_data"),newMP3.get(0)));
            client.sendFile(user, new File (new File (user +"_data"), user +"_music.xml"));
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(deleteBtn);
        rightPanel.add(saveBtn);

        itemPanel.add(leftPanel, BorderLayout.CENTER);
        itemPanel.add(rightPanel, BorderLayout.EAST);

        return itemPanel;
    }

    /**
     * Für MyClient benötigt.
     * @return
     */
    public String getUser() {return user;}

    /**
     * Methode öffnet über den Desktop den Browser und lädt in einem
     * neuen Tab die HTML-Datei, für den jeweiligen Nutzer.
     */
    private void showMP3Website() {
        client.syncFiles(user);
        File baseDir = new File((user +"_data"));
        File html = new File(baseDir, user +"_index.html");

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