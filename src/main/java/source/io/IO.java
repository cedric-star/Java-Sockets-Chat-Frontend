package source.io;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

    /**
     * Diese Klasse ist als Singleton implementiert.
     * @return
     */
    public static IO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IO();
        }
        return INSTANCE;
    }

    /**
     * Datei im korrekten user Ordner speichern.
     * Wird aufgerufen, wenn ein neuer Song gespeichert wird.
     * @param user
     * @param f
     * @return
     */
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

        updateUserXML(user);
        return newFile;
    }

    /**
     * Datei im korrekten user Ordner speichern.
     * Aufruf kommt aus Socket ByteStream.
     * @param user
     * @param fileName
     * @param content
     * @return
     */
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

        updateUserXML(user);
        return newFile;
    }

    /**
     * @param user
     * @return Passende XML-Datei für jeweiligen user.
     */
    public synchronized File getUserXMLFile(String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        return new File(baseDir, user + "_music.xml");
    }

    /**
     * Wenn Metadaten einer MP3-Datei bearbeitet worden, wird die XML aktualisiert.
     * Dabei werden die style und order Attribute auf default gesetzt, wenn es noch keine Datei gibt,
     * oder auf die aus der existierenden XML-Datei ausgelesen und eingefüft.
     * @param user
     */
    private synchronized void updateUserXML(String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        //akzeptiere nur mp3 Dateien
        File[] mp3Files = baseDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        });

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("files");
            rootElement.setAttribute("user", user);

            String sortby = "title";
            String sortorder = "ascending";
            String c1 = "#0000ff"; //blau
            String c2 = "#020202"; //weiß
            String c3 = "#ffffff"; //weiß
            String c4 = "#000000"; //schwarz
            String c5 = "#00ff00"; //grün
            String c6 = "#00ffff"; //hellblau

            //attribute auslesen, wenn vorhanden
            File xml2 = getUserXMLFile(user);
            System.out.println("testtest: "+xml2.exists());
            if (xml2.exists()) {
                //mit xpath weil hier realisiert wurde, dass es einfacher als getElementByTagName() ist
                XPathFactory xpf = XPathFactory.newInstance();
                XPath xpath = xpf.newXPath();

                Document myDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml2);

                String byIn = xpath.evaluate("/files/@sortby", myDoc);
                sortby = byIn.isEmpty() ? sortby : byIn;

                String orderIn = xpath.evaluate("/files/@sortorder", myDoc);
                sortorder = orderIn.isEmpty() ? sortorder : orderIn;

                String in1 = xpath.evaluate("/files/style/@mainTextColor", myDoc);
                c1 = in1.isEmpty() ? c1 : in1;
                String in2 = xpath.evaluate("/files/style/@backgroundColor", myDoc);
                c2 = in2.isEmpty() ? c2 : in2;
                String in3 = xpath.evaluate("/files/style/@tableHeadTextColor", myDoc);
                c3 = in3.isEmpty() ? c3 : in3;
                String in4 = xpath.evaluate("/files/style/@tableRowTextColor", myDoc);
                c4 = in4.isEmpty() ? c4 : in4;
                String in5 = xpath.evaluate("/files/style/@tableHeadBackgroundColor", myDoc);
                c5 = in5.isEmpty() ? c5 : in5;
                String in6 = xpath.evaluate("/files/style/@tableRowBackgroundColor", myDoc);
                c6 = in6.isEmpty() ? c6 : in6;
            }

            rootElement.setAttribute("sortby", sortby);
            rootElement.setAttribute("sortorder", sortorder);
            doc.appendChild(rootElement);

            Element style = doc.createElement("style");
            style = doc.createElement("style");
            style.setAttribute("mainTextColor", c1);
            style.setAttribute("backgroundColor", c2);
            style.setAttribute("tableHeadTextColor", c3);
            style.setAttribute("tableRowTextColor", c4);
            style.setAttribute("tableHeadBackgroundColor", c5);
            style.setAttribute("tableRowBackgroundColor", c6);
            rootElement.appendChild(style);

            //mp3 dateien auslesen und speichern
            for (File mp3File : mp3Files) {
                Element mp3Element = createMP3Element(doc, mp3File);
                if (mp3Element != null) {
                    rootElement.appendChild(mp3Element);
                }
            }

            File xml = getUserXMLFile(user);
            saveXML(xml, doc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Eine MP3-Datei speichert als XML-Element (tag) die jeweiligen Elemente Titel, Artist, ...
     * Die Metadaten der MP3-Datei werden ausgelesen und anschließend in die XML-Datei geschrieben.
     * @param doc
     * @param mp3file
     * @return
     */
    private synchronized Element createMP3Element(Document doc, File mp3file) {
        AudioFile audioFile;
        String filename = mp3file.getName();

        try {
            audioFile = AudioFileIO.read(mp3file);
            AudioFile.logger.setLevel(Level.OFF);
        } catch (Exception e) {
            System.err.println("Error reading audio file: " + mp3file.getAbsolutePath() + "\n" + e.getMessage());
            return null;
        }

        Element mp3Element = doc.createElement("mp3");
        mp3Element.setAttribute("filename", filename);

        Element filenameElement = doc.createElement("filename");
        filenameElement.appendChild(doc.createTextNode(filename));
        mp3Element.appendChild(filenameElement);

        Element titleElement = doc.createElement("title");
        try {
            String title = audioFile.getTag().getFirst(FieldKey.TITLE);
            titleElement.appendChild(doc.createTextNode(title != null && !title.isEmpty() ? title : "no_title"));
        } catch (Exception e) {
            titleElement.appendChild(doc.createTextNode("no_title"));
        }
        mp3Element.appendChild(titleElement);

        Element artistElement = doc.createElement("artist");
        try {
            String artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
            artistElement.appendChild(doc.createTextNode(artist != null && !artist.isEmpty() ? artist : "no_artist"));
        } catch (Exception e) {
            artistElement.appendChild(doc.createTextNode("no_artist"));
        }
        mp3Element.appendChild(artistElement);

        Element albumElement = doc.createElement("album");
        try {
            String album = audioFile.getTag().getFirst(FieldKey.ALBUM);
            albumElement.appendChild(doc.createTextNode(album != null && !album.isEmpty() ? album : "no_album"));
        } catch (Exception e) {
            albumElement.appendChild(doc.createTextNode("no_album"));
        }
        mp3Element.appendChild(albumElement);

        Element genreElement = doc.createElement("genre");
        try {
            String genre = audioFile.getTag().getFirst(FieldKey.GENRE);
            genreElement.appendChild(doc.createTextNode(genre != null && !genre.isEmpty() ? genre : "no_genre"));
        } catch (Exception e) {
            genreElement.appendChild(doc.createTextNode("no_genre"));
        }
        mp3Element.appendChild(genreElement);

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

    /**
     * Löscht lokal die jeweilige Datei.
     * @param user
     * @param fileName
     */
    public synchronized void deleteFile(String user, String fileName) {
        File baseDir = new File(user+"_data");
        File mp3File = new File(baseDir, fileName);

        if (mp3File.exists() && mp3File.delete()) updateUserXML(user);
    }

    /**
     * Setzt Attribute für /files/@... in der XML-Datei.
     * @param user
     * @param attributeName
     * @param value
     * @return
     */
    public synchronized File setFilesAttribute(String user, String attributeName, String value) {
        File xml = getUserXMLFile(user);

        try {
            System.out.println("setting attribute:"+attributeName+"="+value);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);

            NodeList nodeL = doc.getElementsByTagName("files");

            if (nodeL.getLength() > 0) {
                Element elem = (Element) nodeL.item(0);
                elem.setAttribute(attributeName, value);
            }
            saveXML(xml, doc);
        } catch (Exception e) {System.err.println(e);};
        return xml;
    }

    /**
     * Setzt Attribute für /files/style/@... in der XML-Datei.
     * @param user
     * @param attributeName
     * @param value
     * @return
     */
    public synchronized File setStyleAttribute(String user, String attributeName, String value) {
        File xml = getUserXMLFile(user);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);

            NodeList styleL = doc.getElementsByTagName("style");

            if (styleL.getLength() > 0) {
                Element style = (Element) styleL.item(0);
                style.setAttribute(attributeName, value);
            }
            saveXML(xml, doc);
        } catch (Exception e) {System.err.println(e);};
        return xml;
    }

    /**
     * Hilfsmethode zum schreiben einer XML-Datei aus einem File Objekt.
     * @param xml
     * @param doc
     * @throws Exception
     */
    private synchronized void saveXML(File xml, Document doc) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xml);
        transformer.transform(source, result);
    }

    /**
     * Liest die XML-Datei aus, um für jeden Song alle Elemente zu erhalten.
     * Nutzt eine Verschachtelte ArrayList: welcher Song > welche Attribute für diesen Song.
     * @param user
     * @return
     */
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

    /**
     * Nimmt eine Liste an Werten, um diese in den Header der MP3-Datei zu schreiben.
     * Wird genutz, um anschließend die Werte daraus zu lesen und in der XML-Datei zu speichern.
     * Diese Methode setzt aber nur die MP3-Metadaten.
     * @param user
     * @param vals
     */
    public synchronized void updateMP3XMLAttributes(String user, ArrayList<String> vals) {
        AudioFile audioFile = null;
        String filename = vals.get(0);
        File baseDir = new File(user+"_data");
        File mp3file = new File (baseDir, filename);

        try {
            audioFile = AudioFileIO.read(mp3file);
            AudioFile.logger.setLevel(Level.WARNING);

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

    /**
     * Hilfsmethode die den Inhalt eines XML-Tags zurückgibt.
     * @param parent
     * @param tagName
     * @return
     */
    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
}