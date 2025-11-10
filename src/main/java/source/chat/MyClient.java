package source.chat;

import java.io.*;
import java.net.Socket;
import org.json.*;

public class MyClient {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private final MyChat myChat; // Direkter Verweis auf die MyChat Instanz
    private ListenerThread listener;
    private Thread listenerThread;

    public MyClient(MyChat chat) { // MyChat im Konstruktor übergeben
        this.myChat = chat;
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

    public void setChat(String inp) {
        // Direkter Aufruf auf MyChat
        if (myChat != null) {
            JSONArray ar = new JSONArray(inp);
            StringBuilder sb = new StringBuilder();

            ar.forEach((item) -> {
                JSONObject jo = (JSONObject) item;
                sb.append("[");
                String s = (myChat.getUser().equals(jo.getString("user"))) ? "Ich" : jo.getString("user");
                sb.append(s);
                sb.append("]: ");
                sb.append(jo.getString("msg").trim());
                sb.append(System.lineSeparator());
            });

            myChat.updateChatDisplay(sb.toString());
        }
    }

    private void startListening() {
        listener = new ListenerThread(this);
        listenerThread = new Thread(listener);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendMessage(String msg) {
        JSONObject jo = new JSONObject();
        jo.put("msg", msg);
        jo.put("user",myChat.getUser());
        try {
            out.writeUTF(jo.toString());
            out.flush();
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