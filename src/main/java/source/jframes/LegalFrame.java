package source.jframes;

import javax.swing.*;
import java.awt.*;

public class LegalFrame extends BaseFrame{

    /**
     * Fenster für Impressum und Kontaktdaten.
     */
    public LegalFrame(){
        super("Legal Information", "Contact Details and Legal Information");
        setGui();
    }

    /**
     * Anordnung und verschachteln der Komponenten.
     */
    private void setGui() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setText(getText());

        wrapper.add(textPane, BorderLayout.CENTER);
        wrapper.add(new JLabel(""), BorderLayout.SOUTH);
    }

    /**
     * Text zusammenschreiben als String.
     * @return
     */
    private String getText() {
        StringBuilder sb = new StringBuilder();

        sb.append("Legal Information:");
        sb.append("\n");
        sb.append("Cedric Wünsch");
        sb.append("\n");
        sb.append("Hochschule Harz");
        sb.append("\n");
        sb.append("38855 Wernigerode");
        sb.append("\n");
        sb.append("\n");
        sb.append("This App was build as assignment for");
        sb.append("\n");
        sb.append("\"Rechnerkommunikation und Middleware\"");

        return sb.toString();
    }
}
