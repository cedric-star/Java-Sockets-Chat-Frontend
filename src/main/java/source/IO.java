package source;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class IO {

    private static IO INSTANCE;
    private IO() {}
    private String user;

    public static IO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IO();
        }
        return INSTANCE;
    }

    public void setUser(String user) {this.user = user;}
    public String getUser() {return this.user;}

    public synchronized File saveFile(String user, File f) {
        System.out.println("\nSaving File: "+f.getName());

        File baseDir = new File((user+"_data"));
        if (!baseDir.exists()) baseDir.mkdirs();

        File newFile = new File(baseDir, f.getName());

        try {
            Files.copy(f.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("File saved: "+newFile.getAbsolutePath());

        return newFile;
    }

    public synchronized File saveFile(String user, String fileName, byte[] content) {
        System.out.println("\nSaving File: "+fileName);

        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();


        File newFile = new File(baseDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(content);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("File saved: "+newFile.getAbsolutePath());

        return newFile;
    }

    public synchronized ArrayList<File> readAllMP3(String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        File[] files = baseDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        });
        System.out.println(files.toString());
        ArrayList<File> mp3s = new ArrayList<File>(Arrays.asList(files));

        for (File mp3 : mp3s) {
            genXMLFromMP3(mp3, user);
        }
        return mp3s;
    }


    public synchronized ArrayList<String> getXMLAttributes(File xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        ArrayList<String> attr = new ArrayList<>();
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xml);

            XPathFactory xpfactory = XPathFactory.newInstance();
            XPath xpath = xpfactory.newXPath();
            String base = "/file/data/";

            attr.add(xpath.evaluate(base+"title", document));
            attr.add(xpath.evaluate(base+"artist", document));
            attr.add(xpath.evaluate(base+"album", document));
            attr.add(xpath.evaluate(base+"genre", document));
            attr.add(xpath.evaluate(base+"duration", document));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return attr;
    }

    public synchronized File genXMLFromMP3(File mp3file,  String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        String newName = mp3file.getName();
        newName = newName.replace(".mp3", ".xml");

        File xml = new  File(baseDir, newName);
        StringBuilder sb = getXMLContent(mp3file, user);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(xml));
            bw.write(sb.toString());
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return xml;
    }

    private synchronized StringBuilder getXMLContent(File mp3file, String user) {
        AudioFile audioFile;

        try {
            audioFile = AudioFileIO.read(mp3file);
            audioFile.logger.setLevel(Level.WARNING);
        } catch (Exception e) {
            System.err.println("error, while reading audio file: "+mp3file.getAbsolutePath()+"\n"+e.getMessage());
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<file>\n");
        sb.append("    <user>"+user+"</user>\n");
        sb.append("    <data>\n");

        String title;
        try {
            title = audioFile.getTag().getFirst(FieldKey.TITLE);
        } catch (Exception e) {
            title = "no_title";
        }
        sb.append("        <title>"+title+"</title>\n");

        String artist;
        try {
            artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
        } catch (Exception e) {
            artist = "no_artist";
        }
        sb.append("        <artist>"+artist+"</artist>\n");


        String album;
        try {
            album = audioFile.getTag().getFirst(FieldKey.ALBUM);
        } catch (Exception e) {
            album = "no_album";
        }
        sb.append("        <album>"+album+"</album>\n");

        String genre;
        try {
            genre = audioFile.getTag().getFirst(FieldKey.GENRE);
        } catch (Exception e) {
            genre = "no_genre";
        }
        sb.append("        <genre>"+genre+"</genre>\n");

        String duration;
        try {
            duration = Integer.toString(audioFile.getAudioHeader().getTrackLength())+"s";
        } catch (Exception e) {
            duration = "couldnt find duration";
        }
        sb.append("        <duration>"+duration+"</duration>\n");
        sb.append("    </data>\n");
        sb.append("</file>");


        return sb;
    }
}