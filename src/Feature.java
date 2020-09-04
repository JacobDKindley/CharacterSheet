import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Feature {
    JPanel pane;
    private JButton button;
    String title,description,source;
    int lvl;

    public Feature(String title,String description,String source,int lvl){
        this.title=title;
        this.description=description;
        this.source=source;
        this.lvl=lvl;
        button.setText(source+" "+lvl+": "+title);
        pane.setMaximumSize(new Dimension(pane.getMaximumSize().width,pane.getPreferredSize().height));

        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FeaturePopup fp = new FeaturePopup(title,description);
                JFrame frame = new JFrame(title);
                frame.setContentPane(fp.pane);
                frame.setAlwaysOnTop(true);
                frame.pack();
                frame.setSize(new Dimension(400,450));
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}
