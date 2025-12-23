package source;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class IO {

    public static synchronized void saveFile(String user, File f) {
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
    }

    public static synchronized void saveFile(String user, String fileName, byte[] content) {
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
    }

    public static synchronized ArrayList<File> readAllMP3(String user) {
        File baseDir = new File(user+"_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        File[] files = baseDir.listFiles();
        System.out.println(files.toString());
        return new ArrayList<File>(Arrays.asList(files));
    }

    public static synchronized File genXMLFromMP3(File mp3file) {
        //hier mp3 auslesen und irgendwie metadaten in xml umwandeln, dann speichern mit saveFile
        return null;
    }
}
