package source.connection;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import source.IO;
import source.app.LibraryFrame;

public class MyClient {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private final LibraryFrame mainWindow; // Direkter Verweis auf die MyChat Instanz
    private IO io;

    /**
     * Baut die Verbindung zum Server auf.
     * @param win
     */
    public MyClient(LibraryFrame win) { // MyChat im Konstruktor übergeben
        this.io = IO.getInstance();
        this.mainWindow = win;
        startConnection("localhost", 16969);
    }

    /**
     * Wird beim Starten der Verbindung einmalig ausgeführt und deklariert in und out.
     * Außerdem werden zunächst alle Dateien aus dem Backend geholt.
     * @param ip
     * @param port
     */
    private void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());

            syncFiles(mainWindow.getUser());

        } catch (IOException e) {
            System.err.println("Verbindung zu " + ip + ":" + port + "fehlgeschlagen:\n" + e.getMessage());
        }
    }

    /**
     * Sendet eine Datei eines Nutzers an das Backend. Dabei wird automatisch immer die Nutzer XML-Datei
     * mitgesendet, da diese alle wichtigen Metadaten enthält. (Es kann demnach auch zweimal hintereinander die
     * gleiche XML-Datei mit einem Methodenaufruf gesendet werden.)
     * @param user
     * @param file
     */
    public void sendFile(String user, File file) {
        try {
            out.writeByte(1);
            out.writeUTF(user);
            out.writeUTF(file.getName());
            out.writeLong(file.length());
            out.write(Files.readAllBytes(file.toPath()));

            File xmlFile = io.getUserXMLFile(user);
            out.writeByte(1);
            out.writeUTF(user);
            out.writeUTF(xmlFile.getName());
            out.writeLong(xmlFile.length());
            out.write(Files.readAllBytes(xmlFile.toPath()));
            out.flush();

        } catch (IOException e) {
            System.err.println("Fehler beim Senden:\n" + e.getMessage());
        }
    }

    /**
     * Sendet Nutzer und Dateiname ans Backend, dort wird passende Datei gefunden und gelöscht.
     * @param user
     * @param fileName
     */
    public void deleteFile(String user, String fileName) {
        try {
            out.writeByte(2);
            out.writeUTF(user);
            out.writeUTF(fileName);
            out.flush();
        } catch (IOException e) {
            System.err.println("Fehler beim löschen:\n" + e.getMessage());
        }
    }

    /**
     * Vom Backend sollen alle Dateien zum Nutzer gesendet werden, diese werden anschließen gespeichert.
     * @param user
     */
    public void syncFiles(String user) {
        try {
            out.writeByte(3);
            out.writeUTF(user);
            out.flush();

            int fileNum = in.readInt();
            for  (int i = 0; i < fileNum; i++) {
                String fileName = in.readUTF();
                Long fileLength = in.readLong();
                byte[] content = in.readNBytes(Math.toIntExact(fileLength));

                io.saveFile(user, fileName, content);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim synchronisieren mit den Server:\n" + e.getMessage());
        }
    }
}