import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ActionPopup {
    JTextField nameField;
    JTextField propertiesField;
    JPanel codePane;
    JPanel pane;
    private JButton addDamageRollButton;
    private JTextField mainRollField;

    private void createUIComponents() {
        codePane = new JPanel();
        codePane.setLayout(new GridLayout(0,1));
    }

    public ActionPopup(ActionPane ap){
        JFrame frame = new JFrame(ap.name);
        frame.setContentPane(pane);
        frame.pack();
        frame.setSize(new Dimension(400,500));
        frame.setAlwaysOnTop(true);
        nameField.setText(ap.name);
        propertiesField.setText(ap.properties);
        mainRollField.setText(ap.mainCode);

        ArrayList<JTextField> references = new ArrayList<>();
        for(int i=0;i<ap.codes.size();i++){
            JTextField field = new JTextField(ap.codes.get(i));
            references.add(field);
            codePane.add(field);
        }

        addDamageRollButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField field = new JTextField();
                references.add(field);
                codePane.add(field);
                pane.revalidate();
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ap.name= nameField.getText();
                ap.button.setText(ap.name);
                ap.properties= propertiesField.getText();
                ap.mainCode= mainRollField.getText();
                ap.codes = new ArrayList<>();
                for (JTextField reference : references) {
                    if(reference.getText().equals(""))continue;
                    ap.codes.add(reference.getText());
                }
                ap.parseCode();
            }
        });
        frame.setVisible(true);
    }
}
