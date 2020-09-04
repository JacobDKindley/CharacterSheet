import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConfirmPopup {
    JPanel pane;
    JTextPane textPane;
    JButton cancelButton;
    JButton confirmButton;
    private JLabel label;
    private JScrollPane scroller;
    boolean choice = false;
    Style titles,descriptions,fileName;


    public ConfirmPopup(String path) throws Exception {
        StyledDocument doc=textPane.getStyledDocument();
        titles = doc.addStyle("",null);
        descriptions = doc.addStyle("",null);
        fileName=doc.addStyle("",null);
        StyleConstants.setFontFamily(titles,"Garamond");
        StyleConstants.setFontFamily(descriptions,"Garamond");
        StyleConstants.setFontFamily(fileName,"Garamond");
        StyleConstants.setForeground(titles, Color.red);
        StyleConstants.setForeground(descriptions,Color.black);
        StyleConstants.setForeground(fileName,Color.magenta);
        StyleConstants.setFontSize(titles,24);
        StyleConstants.setBold(titles,true);
        StyleConstants.setFontSize(descriptions,14);
        StyleConstants.setFontSize(fileName,32);
        StyleConstants.setBold(fileName,true);

        File f = new File(path);
        File[] files = f.listFiles();
        label.setText("Confirm Choice: "+f.getName());
        //Parsing one file vs a folder
        if(files==null){
            BufferedReader in = new BufferedReader(new FileReader(f));
            doc.insertString(doc.getLength(), f.getName().substring(0, f.getName().indexOf(".txt")) + "\n", fileName);
            String s = in.readLine();
            while (s != null) {
                parse(s, doc);
                s = in.readLine();
            }
        }else {


            //Present base features first
            File base = new File(path + "Base.txt");
            if (base.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(base));
                doc.insertString(doc.getLength(), base.getName().substring(0, base.getName().indexOf(".txt")) + "\n", fileName);
                String s = in.readLine();
                while (s != null) {
                    parse(s, doc);
                    s = in.readLine();
                }
            }

            //Present subrace/class features
            for (File file : files) {
                if (file.getName().contains("Base.txt")) continue;
                BufferedReader in = new BufferedReader(new FileReader(file));
                doc.insertString(doc.getLength(), file.getName().substring(0, file.getName().indexOf(".txt")) + "\n", fileName);
                String s = in.readLine();
                while (s != null) {
                    parse(s, doc);
                    s = in.readLine();
                }


            }
        }

        JDialog dialog = new JDialog();
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setSize(new Dimension(800,900));
        textPane.setCaretPosition(0);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        confirmButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choice=true;
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choice=false;
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    public void parse(String s,StyledDocument doc) throws BadLocationException {
        if (s.startsWith("LVL1:")) s = s.substring(s.indexOf(":") + 1);
        if (s.startsWith("F:")) {
            s = s.substring(s.indexOf("*") + 1);
            doc.insertString(doc.getLength(), s.substring(0, s.indexOf("*")) + "\n", titles);
            s = s.substring(s.indexOf("*") + 1);
            doc.insertString(doc.getLength(), client.convertFromGrave(s) + "\n", descriptions);
        } else if (s.startsWith("P:")) {
            doc.insertString(doc.getLength(), "Gain proficiency:\n", titles);
            doc.insertString(doc.getLength(), s.substring(s.indexOf(":") + 1) + "\n", descriptions);
        } else if (s.startsWith("PC:")) {
            s = s.substring(s.indexOf(":") + 1);
            s = s.substring(0, s.length() - 1);
            s = s.replace("/", ", ");
            doc.insertString(doc.getLength(), "Choose proficiency:\n", titles);
            doc.insertString(doc.getLength(), s + "\n", descriptions);
        } else if (s.startsWith("STR:")) {
            doc.insertString(doc.getLength(), "Gain " + s.substring(s.indexOf(":") + 1) + " STR\n", titles);
        } else if (s.startsWith("DEX:")) {
            doc.insertString(doc.getLength(), "Gain " + s.substring(s.indexOf(":") + 1) + " DEX\n", titles);
        } else if (s.startsWith("CON:")) {
            doc.insertString(doc.getLength(), "Gain " + s.substring(s.indexOf(":") + 1) + " CON\n", titles);
        } else if (s.startsWith("INT:")) {
            doc.insertString(doc.getLength(), "Gain " + s.substring(s.indexOf(":") + 1) + " INT\n", titles);
        } else if (s.startsWith("WIS:")) {
            doc.insertString(doc.getLength(), "Gain " + s.substring(s.indexOf(":") + 1) + " WIS\n", titles);
        } else if (s.startsWith("CHA:")) {
            doc.insertString(doc.getLength(), "Gain " + s.substring(s.indexOf(":") + 1) + " CHA\n", titles);
        } else if (s.startsWith("level")) {
            doc.insertString(doc.getLength(), "Level " + s.substring(5) + "\n", titles);
        } else if (s.startsWith("Choose Subclass")) {
            doc.insertString(doc.getLength(), "Choose Subclass \n", titles);
        } else if (s.startsWith("Sub")) {
            doc.insertString(doc.getLength(), "Subclass Feature\n", titles);
        } else if (s.startsWith("ASI")) {
            doc.insertString(doc.getLength(), "ASI\n", titles);
        }else if (s.startsWith("ASC")){
            s = s.substring(s.indexOf(":") + 1);
            s = s.substring(0, s.length() - 1);
            s = s.replace("/", ", ");
            doc.insertString(doc.getLength(), "Choose ASI:\n", titles);
            doc.insertString(doc.getLength(), s + "\n", descriptions);
        }else if (s.startsWith("VASC")){
            doc.insertString(doc.getLength(), "Choose 2 different ASIs:\n", titles);
        }else if(s.startsWith("LC")){
            doc.insertString(doc.getLength(), "Learn a language:\n", titles);
        }else if(s.startsWith("GAMING_SET")){
            doc.insertString(doc.getLength(), "Gain proficiency in a gaming set.\n", titles);
        }else if (s.startsWith("PREREQ")){
            doc.insertString(doc.getLength(), "Prerequisite(s):+"+s.substring(s.indexOf(":")+1)+"\n", titles);
        }
    }
}
