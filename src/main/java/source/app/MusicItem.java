package source.app;

import source.IO;

import java.io.File;

public class MusicItem {
    private String user;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String duration;
    private File mp3File;
    private File xmlFile;

    public MusicItem(File mp3File, String user) {
        this.user = user;
        this.title = "";
        this.artist = "";
        this.album = "";
        this.genre = "";
        this.duration = "";
        this.mp3File = mp3File;

        genAttributesFromXML();
    }

    private void genAttributesFromXML() {
        //use xmlfile
        // hier fehlt code

        title = "titl";
        artist = "art";
        album = "alb";
        genre = "gen";
        duration = "dur";
    }

    private void registerOrGenXML() {
        this.xmlFile = null;

        //schauen ob xml mit name von mp3 existiert
        //wenn ja xml file setzen
        //mp3File.
        this.xmlFile = IO.genXMLFromMP3(this.mp3File);


    }

}
