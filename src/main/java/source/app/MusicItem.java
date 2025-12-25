package source.app;

import source.IO;
import source.connection.MyClient;

import java.io.File;
import java.util.ArrayList;

public class MusicItem {
    private String user;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String duration;
    private File mp3File;
    private File xmlFile;
    private IO io;

    public MusicItem(File mp3File, String user) {
        this.user = user;
        this.title = "";
        this.artist = "";
        this.album = "";
        this.genre = "";
        this.duration = "";
        this.mp3File = mp3File;
        this.xmlFile = null;
        this.io = IO.getInstance();
        genAttributesFromXML();
    }

    public void genAttributesFromXML() {
        String xmlName = mp3File.getName().replace(".mp3", ".xml");
        this.xmlFile = new File(user+"_data",  xmlName);
        registerOrGenXML();
    }

    private void registerOrGenXML() {
        if (!xmlFile.exists()) {
            this.xmlFile = io.genXMLFromMP3(mp3File, user);

        }

        ArrayList<String> lst = io.getXMLAttributes(xmlFile);
        title = lst.get(0);
        artist = lst.get(1);
        album = lst.get(2);
        genre = lst.get(3);
        duration = lst.get(4);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mp3File.getName());
        sb.append("\n    Title: " + title + "\n");
        sb.append("    Artist: " + artist + "\n");
        sb.append("    Album: " + album + "\n");
        sb.append("    Genre: " + genre + "\n");
        sb.append("    Duration: " + duration + "\n");
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getDuration() {
        return duration;
    }

    public String getMP3FileName() {
        return mp3File.getName();
    }

    public void remove(MyClient client) {
        this.mp3File.delete();
        this.xmlFile.delete();
        client.deleteFile(user, mp3File.getName());
        client.deleteFile(user, xmlFile.getName());
    }
}
