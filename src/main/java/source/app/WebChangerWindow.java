package source.app;

import source.IO;
import source.connection.MyClient;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class WebChangerWindow extends JFrame {
    MyClient client;
    String user;

    /*Color mainTextColor;
    Color backgroundColor;
    Color tableHeadTextColor;
    Color tableRowTextColor;
    Color tableHeadBackgroundColor;
    Color tableRowBackgroundColor;*/

    JScrollPane scroller;
    JPanel content;
    JColorChooser mainTextColorChooser;
    JColorChooser backgroundColorChooser;
    JColorChooser tableHeadTextColorChooser;
    JColorChooser tableRowTextColorChooser;
    JColorChooser tableHeadBackgroundColorChooser;
    JColorChooser tableRowBackgroundColorChooser;
    JPanel panel1;
    private IO io;

    public WebChangerWindow(MyClient client, String user) {
        this.io = IO.getInstance();
        this.user = user;
        this.client = client;
        setGui();

        setContentPane(panel1);
        setVisible(true);
    }

    private void setGui() {
        panel1 = new JPanel(new BorderLayout());

        JLabel head = new JLabel("Choose your Design!!!");
        head.setFont(head.getFont().deriveFont(34f));
        panel1.add(head, BorderLayout.NORTH);


        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        mainTextColorChooser = new JColorChooser();
        content.add(getChooserPanel("set main text color", mainTextColorChooser, "mainTextColor"));

        backgroundColorChooser = new JColorChooser();
        content.add(getChooserPanel("set background color", backgroundColorChooser, "backgroundColor"));

        tableHeadTextColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table header text color", tableHeadTextColorChooser, "tableHeadTextColor"));

        tableRowTextColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table row text color", tableRowTextColorChooser, "tableRowTextColor"));

        tableHeadBackgroundColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table hieader background color", tableHeadBackgroundColorChooser, "tableHeadBackgroundColor"));

        tableRowBackgroundColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table row background color", tableRowBackgroundColorChooser, "tableRowBackgroundColor"));

        scroller = new JScrollPane(content);
        panel1.add(scroller, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
    }

    private JPanel getChooserPanel(String title, JColorChooser jcc, String attributeName) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(24f));
        JButton expand = new JButton("toggle expand view");
        expand.addActionListener(e -> {
            if (center.isVisible()) {
                center.setVisible(false);
            } else {
                center.setVisible(true);
            }
        });

        JPanel top = new JPanel(new FlowLayout());
        top.add(expand);
        top.add(label);

        panel.add(top, BorderLayout.NORTH);

        JButton btn = new JButton("set color");
        btn.addActionListener(e -> {
            Color c = jcc.getColor();
            String temp = Integer.toHexString(c.getRGB());
            System.out.println(temp);
            String hex = "#"+temp.substring(temp.length()-6);
            System.out.println(hex);

            File xml = io.setStyleAttribute(user, attributeName, hex);
            client.sendFile(user, xml);
        });

        center.add(jcc);
        center.add(btn);
        center.setVisible(false);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
}
