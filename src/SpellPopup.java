import javax.swing.*;

public class SpellPopup {
    JPanel pane;
    private JTextPane descriptionPane;
    private JLabel componentLabel;
    private JLabel rangeLabel;
    private JLabel durationLabel;
    private JLabel nameLabel;
    private JLabel tagLabel;
    private JLabel castTimeLabel;

    public SpellPopup(SpellInfo info){
        nameLabel.setText(info.name);
        castTimeLabel.setText("Cast Time: "+info.castTime);
        durationLabel.setText("Duration: "+info.duration);
        componentLabel.setText("Components: "+info.components);
        rangeLabel.setText("Range: "+info.range);
        descriptionPane.setText(info.description);
        tagLabel.setText(info.tags);
    }
}
