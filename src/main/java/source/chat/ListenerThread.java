package source.chat;

import java.io.IOException;

public class ListenerThread implements Runnable {
    private final MyClient clientRef;
    private volatile boolean running = true;

    public ListenerThread(MyClient clientRef) {
        this.clientRef = clientRef;
    }

    public void run() {
        try {
            while (running && !clientRef.getClientSocket().isClosed()) {
                String completeChat = clientRef.getIn().readUTF();
                clientRef.setChat(completeChat);
            }
        } catch (IOException e) {
            if (running) System.err.println("Fehler beim Hören: " + e.getMessage());
        }
    }

    public void close() {
        running = false;
    }
}
