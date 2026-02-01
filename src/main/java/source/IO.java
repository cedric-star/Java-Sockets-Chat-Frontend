package source;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

    //save mp3
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

        // XML nach dem Speichern der MP3 aktualisieren
        updateUserXML(user);

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

        // XML nach speichern der MP3 aktualisieren
        updateUserXML(user);

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

        // Wenn keine MP3s gefunden wurden, leere Liste zurückgeben
        if (files == null) {
            return new ArrayList<>();
        }

        System.out.println("Found " + files.length + " MP3 files");
        ArrayList<File> mp3s = new ArrayList<File>(Arrays.asList(files));

        // XML für alle MP3s generieren/aktualisieren
        updateUserXML(user);

        return mp3s;
    }

    public synchronized ArrayList<String> getXMLAttributes(File xml, String mp3FileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        ArrayList<String> attr = new ArrayList<>();

        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xml);

            XPathFactory xpfactory = XPathFactory.newInstance();
            XPath xpath = xpfactory.newXPath();
            String base = "/files/mp3[@filename='" + mp3FileName + "']/";

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

    public synchronized File getUserXMLFile(String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        return new File(baseDir, user + "_music.xml");
    }

    private synchronized void updateUserXML(String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        // Alle MP3-Dateien im Verzeichnis finden
        File[] mp3Files = baseDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        });

        if (mp3Files == null || mp3Files.length == 0) {
            System.out.println("No MP3 files found for user: " + user);
            deleteFile(user, (user+"_music.xml"));
            return;
        }

        try {
            // XML-Dokument erstellen
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Wurzelelement erstellen
            Element rootElement = doc.createElement("files");
            rootElement.setAttribute("user", user);
            rootElement.setAttribute("sortby", "artist");
            rootElement.setAttribute("sortorder", "descending");
            doc.appendChild(rootElement);

            // Für jede MP3-Datei ein Element hinzufügen
            for (File mp3File : mp3Files) {
                Element mp3Element = createMP3Element(doc, mp3File, user);
                if (mp3Element != null) {
                    rootElement.appendChild(mp3Element);
                }
            }

            Element style = doc.createElement("style");
            style.setAttribute("mainTextColor", "#0000ff");
            style.setAttribute("backgroundColor", "#111111");
            style.setAttribute("tableHeadTextColor", "#ffffff");
            style.setAttribute("tableRowTextColor", "#000000");
            style.setAttribute("tableHeadBackgroundColor", "#00ff00");
            style.setAttribute("tableRowBackgroundColor", "#ffffff");
            rootElement.appendChild(style);

            // XML in Datei schreiben
            File xmlFile = getUserXMLFile(user);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println("XML file updated: " + xmlFile.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error updating XML for user " + user + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private synchronized Element createMP3Element(Document doc, File mp3file, String user) {
        AudioFile audioFile;
        String filename = mp3file.getName();

        try {
            audioFile = AudioFileIO.read(mp3file);
            AudioFile.logger.setLevel(Level.WARNING);
        } catch (Exception e) {
            System.err.println("Error reading audio file: " + mp3file.getAbsolutePath() + "\n" + e.getMessage());
            return null;
        }

        // MP3-Element erstellen
        Element mp3Element = doc.createElement("mp3");
        mp3Element.setAttribute("filename", filename);

        // Filename-Element
        Element filenameElement = doc.createElement("filename");
        filenameElement.appendChild(doc.createTextNode(filename));
        mp3Element.appendChild(filenameElement);

        // Title-Element
        Element titleElement = doc.createElement("title");
        try {
            String title = audioFile.getTag().getFirst(FieldKey.TITLE);
            titleElement.appendChild(doc.createTextNode(title != null && !title.isEmpty() ? title : "no_title"));
        } catch (Exception e) {
            titleElement.appendChild(doc.createTextNode("no_title"));
        }
        mp3Element.appendChild(titleElement);

        // Artist-Element
        Element artistElement = doc.createElement("artist");
        try {
            String artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
            artistElement.appendChild(doc.createTextNode(artist != null && !artist.isEmpty() ? artist : "no_artist"));
        } catch (Exception e) {
            artistElement.appendChild(doc.createTextNode("no_artist"));
        }
        mp3Element.appendChild(artistElement);

        // Album-Element
        Element albumElement = doc.createElement("album");
        try {
            String album = audioFile.getTag().getFirst(FieldKey.ALBUM);
            albumElement.appendChild(doc.createTextNode(album != null && !album.isEmpty() ? album : "no_album"));
        } catch (Exception e) {
            albumElement.appendChild(doc.createTextNode("no_album"));
        }
        mp3Element.appendChild(albumElement);

        // Genre-Element
        Element genreElement = doc.createElement("genre");
        try {
            String genre = audioFile.getTag().getFirst(FieldKey.GENRE);
            genreElement.appendChild(doc.createTextNode(genre != null && !genre.isEmpty() ? genre : "no_genre"));
        } catch (Exception e) {
            genreElement.appendChild(doc.createTextNode("no_genre"));
        }
        mp3Element.appendChild(genreElement);

        // Duration-Element
        Element durationElement = doc.createElement("duration");
        try {
            String duration = Integer.toString(audioFile.getAudioHeader().getTrackLength()) + "s";
            durationElement.appendChild(doc.createTextNode(duration));
        } catch (Exception e) {
            durationElement.appendChild(doc.createTextNode("couldnt_find_duration"));
        }
        mp3Element.appendChild(durationElement);

        return mp3Element;
    }

    // Methode zum Löschen einer MP3 und Aktualisieren der XML
    public synchronized void deleteFile(String user, String fileName) {
        File baseDir = new File(user+"_data");
        File mp3File = new File(baseDir, fileName);

        if (mp3File.exists() && mp3File.delete()) updateUserXML(user);
    }

    public synchronized File setStyleAttribute(String user, String attributeName, String value) {
        File xml = getUserXMLFile(user);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);
            Element root = doc.getDocumentElement();
            root.normalize();


            NodeList styleL = doc.getElementsByTagName("style");
            if (styleL.getLength() > 0) {
                Element style = (Element) styleL.item(0);
                style.setAttribute(attributeName, value);
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xml);
            transformer.transform(source, result);

        } catch (Exception e) {System.err.println(e);};

        return getUserXMLFile(user);
    }

    // Methode zum Abrufen aller MP3-Daten aus der XML
    public synchronized ArrayList<ArrayList<String>> getAllMP3Data(String user) {
        ArrayList<ArrayList<String>> allData = new ArrayList<>();
        File xmlFile = getUserXMLFile(user);

        if (!xmlFile.exists()) {
            return allData;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            NodeList mp3Nodes = document.getElementsByTagName("mp3");

            for (int i = 0; i < mp3Nodes.getLength(); i++) {
                Element mp3Element = (Element) mp3Nodes.item(i);
                ArrayList<String> mp3Data = new ArrayList<>();

                mp3Data.add(mp3Element.getAttribute("filename"));
                mp3Data.add(getElementText(mp3Element, "title"));
                mp3Data.add(getElementText(mp3Element, "artist"));
                mp3Data.add(getElementText(mp3Element, "album"));
                mp3Data.add(getElementText(mp3Element, "genre"));
                mp3Data.add(getElementText(mp3Element, "duration"));

                allData.add(mp3Data);
            }

        } catch (Exception e) {
            System.err.println("Error reading XML file: " + e.getMessage());
        }

        return allData;
    }

    public synchronized void updateMP3XMLAttributes(String user, ArrayList<String> vals) {
        AudioFile audioFile = null;
        String filename = vals.get(0);
        File baseDir = new File(user+"_data");
        File mp3file = new File (baseDir, filename);

        try {
            audioFile = AudioFileIO.read(mp3file);
            audioFile.logger.setLevel(Level.WARNING);

            Tag tag = audioFile.getTagOrCreateAndSetDefault();

            tag.setField(FieldKey.TITLE, vals.get(1));
            tag.setField(FieldKey.ARTIST, vals.get(2));
            tag.setField(FieldKey.ALBUM, vals.get(3));
            tag.setField(FieldKey.GENRE, vals.get(4));

            audioFile.commit();
        } catch (Exception e) {
            System.err.println("Error reading audio file: " + mp3file.getAbsolutePath() + "\n" + e.getMessage());
        }


        updateUserXML(user);
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }


}