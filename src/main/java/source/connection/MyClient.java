package source.connection;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import source.app.MainAppWindow;

public class MyClient {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private final MainAppWindow mainWindow; // Direkter Verweis auf die MyChat Instanz
    private ListenerThread listener;
    private Thread listenerThread;

    public MyClient(MainAppWindow win) { // MyChat im Konstruktor übergeben
        this.mainWindow = win;
        startConnection("localhost", 16969);
    }

    private void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());

            startListening();

        } catch (IOException e) {
            System.err.println("Could not connect to " + ip + ":" + port + "\n" + e.getMessage());
        }
    }

    private void sendFile(String fileName) {

    }


    private void startListening() {
        listener = new ListenerThread(this);
        listenerThread = new Thread(listener);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendFile(String user, File file) {
        try {
            out.writeUTF(user);
            out.writeUTF(file.getName());
            out.writeLong(file.length());
            out.write(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            System.err.println("Fehler beim Senden: " + e.getMessage());
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