package source.jframes;

import source.io.IO;
import source.connection.MyClient;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class WebChangerFrame extends BaseFrame {
    MyClient client;
    String user;

    JScrollPane scroller;
    JPanel content;
    JColorChooser mainTextColorChooser;
    JColorChooser backgroundColorChooser;
    JColorChooser tableHeadTextColorChooser;
    JColorChooser tableRowTextColorChooser;
    JColorChooser tableHeadBackgroundColorChooser;
    JColorChooser tableRowBackgroundColorChooser;
    private IO io;

    public WebChangerFrame(MyClient client, String user) {
        super("Change Web", "Change the look of your own Website");
        this.io = IO.getInstance();
        this.user = user;
        this.client = client;

        setGui();
    }

    private void setGui() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel chooseSorting = getSortingPanel();
        content.add(chooseSorting);

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

        wrapper.add(content, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
    }

    private JPanel getSortingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel label = new JLabel("set sorting by attribute");
        label.setFont(label.getFont().deriveFont(24f));
        panel.add(label, BorderLayout.NORTH);


        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JComboBox sortBy = new JComboBox();
        sortBy.addItem("title");
        sortBy.addItem("artist");
        sortBy.addItem("album");
        sortBy.addItem("genre");
        sortBy.addItem("duration");
        orderPanel.add(sortBy);


        JCheckBox cb = new JCheckBox();
        cb.setSelected(true);
        cb.setText("ascending?");
        orderPanel.add(cb);
        panel.add(orderPanel, BorderLayout.CENTER);

        JButton btn = new JButton("set order");
        btn.addActionListener(e -> {
            String attr = sortBy.getSelectedItem().toString();
            System.out.println("orderby: "+attr);

            String order = cb.isSelected() ? "ascending" : "descending";
            System.out.println("order: "+order);

            io.setFilesAttribute(user, "sortby", attr);

            File xml2 = io.setFilesAttribute(user, "sortorder", order);

            client.sendFile(user, xml2);
        });
        panel.add(btn, BorderLayout.EAST);

        return panel;
    }


    private JPanel getChooserPanel(String title, JColorChooser jcc, String attributeName) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(20f));
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
