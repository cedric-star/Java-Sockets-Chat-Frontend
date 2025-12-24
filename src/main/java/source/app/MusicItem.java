package source.app;

import source.IO;

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
    }

    private void genAttributesFromXML() {
        String xmlName = mp3File.getName().replace(".mp3", ".xml");
        this.xmlFile = new File(user+"_data",  xmlName);
        registerOrGenXML();

        title = "titl";
        artist = "art";
        album = "alb";
        genre = "gen";
        duration = "dur";
    }

    private void registerOrGenXML() {
        if (!xmlFile.exists()) {
            this.xmlFile = io.genXMLFromMP3(mp3File, user);
        } else {
            ArrayList<String> attributes = new ArrayList<String>();
            attributes.add(user);
            attributes.add(title);
            attributes.add(artist);
            attributes.add(album);
            attributes.add(genre);
            attributes.add(duration);

            io.setXMLAttributes(xmlFile, attributes);
        }

        //schauen ob xml mit name von mp3 existiert
        //wenn ja xml file setzen
        //mp3File.
        //this.xmlFile = IO.genXMLFromMP3(this.mp3File);


    }

}
