package source.serializer;

import source.chat.MyClient;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyFrame extends JFrame {
    MyClient client;

    JTextArea editor = new JTextArea();
    JButton save = new JButton("Save");
    JButton load = new JButton("Load");
    JButton serialize = new JButton("Serialize");
    JButton deserialize = new JButton("Deserialize");
    JButton chooseFile = new JButton("Choose File");

    private String fileName = "./myDataTxt.txt";
    private String serFileName = "./myData.ser";

    public MyFrame() {
        setLayout(new BorderLayout());

        setGUI();
        setVisible(true);
    }

    private void setGUI() {
        setTitle("MySerializer");
        setSize(600, 400);

        //obere Zeile für Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(save);
        buttonPanel.add(load);
        buttonPanel.add(serialize);
        buttonPanel.add(deserialize);
        buttonPanel.add(chooseFile);


        //editor in scrollpane verpacken
        JScrollPane scrollPane = new JScrollPane(editor);

        //panel und editor zum frame hinzufügen
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        save.addActionListener(e -> {saveData();});
        load.addActionListener(e -> {loadData();});

        serialize.addActionListener(e -> {serializeEditor();});
        deserialize.addActionListener(e -> {deserializeEditor();});

        chooseFile.addActionListener(e -> {openFileDialog();});
    }

    private void saveData() {
        String myText = editor.getText();
        save2File(myText);
        System.out.println("wrote data!");
    }

    private void loadData() {
        String myData = readFromFile();
        editor.setText(myData);
        System.out.println("read data and printed on editor!");
    }

    private void serializeEditor() {
        try {
            FileOutputStream fos = new FileOutputStream(serFileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(editor);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            System.err.println("Fehler beim fos,ois");
        }
    }

    private void deserializeEditor() {
        try {
            FileInputStream fis = new FileInputStream(serFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);

            JTextArea myEditor = (JTextArea)ois.readObject();
            editor.setText(myEditor.getText());

            ois.close();
        } catch (Exception e) {
            System.err.println("Fehler beim deserialisieren");
        }
    }

    private void openFileDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(this);
        fileName = chooser.getSelectedFile().getAbsolutePath();

        loadData();//reload 2 show file contents on screen
    }

    //helferlein
    private void save2File(String s) {
        try (FileWriter fw = new FileWriter(fileName, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(s);

        } catch (Exception e) {
            System.err.println("Error while writing data to file\n"+e.getMessage());
        }
    }
    private String readFromFile() {
        StringBuilder sb = new StringBuilder();

        // Vereinfachte Version mit Reader für UTF-8
        try (FileReader fr = new FileReader(fileName, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(fr)) {

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

        } catch (Exception e) {
            System.err.println("Error while reading file\n" + e.getMessage());
        }

        return sb.toString();
    }
    public String getText() {
        return editor.getText();
    }
}
