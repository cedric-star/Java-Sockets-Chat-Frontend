package source.app;

import javax.swing.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {
    String title;
    String head;

    JPanel wrapper;

    public BaseFrame(String title, String head) {
        this.title = title;
        this.head = head;

        this.wrapper = new JPanel(new BorderLayout());
        setGui();
    }

    private void setGui() {
        setTitle(title);

        JLabel header = new JLabel(head);
        header.setFont(header.getFont().deriveFont(34f));
        header.setHorizontalAlignment(JLabel.CENTER);

        JPanel footer = new  JPanel(new FlowLayout());
        footer.setName("footer");
        JLabel noC = new JLabel("© Cedric Wünsch");
        noC.setFont(noC.getFont().deriveFont(14f));
        footer.add(noC);

        JButton legal = new JButton("View Legal Information");
        legal.addActionListener(e -> {new LegalFrame();});
        footer.add(legal);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(footer, BorderLayout.SOUTH);

        add(wrapper);
        setVisible(true);
    }
}
