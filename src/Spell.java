import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Spell{
    private JButton button;
    JPanel pane;
    JCheckBox checkBox;
    SpellInfo info;

    public Spell(SpellInfo info){
        this.info=info;
        button.setText("Level "+info.lvl+": "+info.name);
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame(info.name);
                SpellPopup sp = new SpellPopup(info);
                frame.setContentPane(sp.pane);
                frame.pack();
                frame.setSize(new Dimension(400,450));
                frame.setVisible(true);
            }
        });
        pane.setMaximumSize(new Dimension(pane.getMaximumSize().width,pane.getPreferredSize().height));
    }


}
