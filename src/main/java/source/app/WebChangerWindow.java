package source.app;

import source.connection.MyClient;

import javax.swing.*;
import java.awt.*;

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

    public WebChangerWindow(MyClient client, String user) {
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
        content.add(getChooserPanel("set main text color", mainTextColorChooser));

        backgroundColorChooser = new JColorChooser();
        content.add(getChooserPanel("set background color", backgroundColorChooser));

        tableHeadTextColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table header text color", tableHeadTextColorChooser));


        tableRowTextColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table row text color", tableRowTextColorChooser));

        tableHeadBackgroundColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table hieader background color", tableHeadBackgroundColorChooser));

        tableRowBackgroundColorChooser = new JColorChooser();
        content.add(getChooserPanel("set table row background color", tableRowBackgroundColorChooser));

        scroller = new JScrollPane(content);
        panel1.add(scroller, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
    }

    private JPanel getChooserPanel(String title, JColorChooser jcc) {
        JPanel panel = new JPanel(new BorderLayout());

        // Schwarze, dicke Border (3 Pixel dick)
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(24f));
        panel.add(label, BorderLayout.NORTH);

        panel.add(jcc, BorderLayout.CENTER);

        JButton btn = new JButton("set color");
        btn.addActionListener(e -> {
            Color c = jcc.getColor();
            String temp = Integer.toHexString(c.getRGB());
            System.out.println(temp);
            String hex = "#"+temp.substring(temp.length()-6);
            System.out.println(hex);
        });

        panel.add(btn, BorderLayout.EAST);
        return panel;
    }
}
