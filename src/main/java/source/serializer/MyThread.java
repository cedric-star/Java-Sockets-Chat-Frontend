package source.serializer;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class MyThread extends Thread{
    MyFrame frame;
    
    private CountDownLatch watcher = new CountDownLatch(1);

    @Override
    public void run() {
        //UI im EDT (Event Dispatch Thread) starten
        SwingUtilities.invokeLater(() -> {
            frame = new MyFrame();
            watcher.countDown();//zählt runter wenn aufgerufen/UI fertig ist
        });

        //Logik im eigentlichen Thread
        System.out.println("Hallo, ich bin ein gestarteter Thread: "+getName());

        try {
            watcher.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(isAlive()) {
            try {
                Thread.sleep(2000);
                System.out.println(getName()+": \n"+frame.getText());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
