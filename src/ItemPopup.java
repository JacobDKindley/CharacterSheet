import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ItemPopup {
    private JTextPane descriptionTextPane;
    private JPanel pane;
    private JTextField weightTextField;
    private JTextField valueTextField;
    private JTextField nameTextField;

    public ItemPopup(Item item){
        descriptionTextPane.setText(item.description);
        nameTextField.setText(item.name);
        weightTextField.setText(""+item.weight);
        valueTextField.setText(item.value);
        JFrame frame = new JFrame();
        frame.setContentPane(pane);
        frame.pack();
        frame.setSize(new Dimension(450,500));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                item.name= nameTextField.getText();
                item.value=valueTextField.getText();
                try{
                    item.weight=Double.parseDouble(weightTextField.getText());
                }catch (Exception exception){}
            }
        });
        client.updateGUI();
        frame.setVisible(true);


    }
}
