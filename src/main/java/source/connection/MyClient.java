package source.connection;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

import source.IO;
import source.app.MainAppWindow;

public class MyClient {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private final MainAppWindow mainWindow; // Direkter Verweis auf die MyChat Instanz
    private ListenerThread listener;
    private Thread listenerThread;
    private IO io;

    public MyClient(MainAppWindow win) { // MyChat im Konstruktor übergeben
        this.io = IO.getInstance();
        this.mainWindow = win;
        startConnection("localhost", 16969);
    }

    private void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());

            syncFiles(mainWindow.getUsername());


        } catch (IOException e) {
            System.err.println("Could not connect to " + ip + ":" + port + "\n" + e.getMessage());
        }
    }

    private void startListening() {
        listener = new ListenerThread(this);
        listenerThread = new Thread(listener);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendFile(String user, File mp3file) {
        try {
            out.writeByte(1);
            out.writeUTF(user);
            out.writeUTF(mp3file.getName());
            out.writeLong(mp3file.length());
            out.write(Files.readAllBytes(mp3file.toPath()));

            File xmlFile = io.genXMLFromMP3(mp3file, user);
            out.writeByte(1);
            out.writeUTF(user);
            out.writeUTF(xmlFile.getName());
            out.writeLong(xmlFile.length());
            out.write(Files.readAllBytes(xmlFile.toPath()));
            out.flush();


        } catch (IOException e) {
            System.err.println("Fehler beim Senden: " + e.getMessage());
        }
    }

    public void deleteFile(String user, String fileName) {
        try {
            out.writeByte(2);
            out.writeUTF(user);
            out.writeUTF(fileName);
            out.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void syncFiles(String user) {
        try {
            out.writeByte(3);
            out.writeUTF(user);
            out.flush();


            int fileNum = in.readInt();
            ArrayList<File> files = new ArrayList<File>();
            for  (int i = 0; i < fileNum; i++) {
                String fileName = in.readUTF();
                Long fileLength = in.readLong();
                byte[] content = in.readNBytes(Math.toIntExact(fileLength));

                io.saveFile(user, fileName, content);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void stopConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (listener != null) listener.close();
            if (listenerThread != null) listenerThread.interrupt();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Socket geschlossen.");

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public DataInputStream getIn() {
        return in;
    }
}