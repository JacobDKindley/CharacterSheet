import javax.swing.*;

public class PactMagicGUI {
    private JPanel pane;
    private JCheckBox slot1;
    private JCheckBox slot2;
    private JCheckBox slot3;
    private JCheckBox slot4;
    private JLabel levelLabel;

    public void reset(){
        slot1.setSelected(false);
        slot2.setSelected(false);
        slot3.setSelected(false);
        slot4.setSelected(false);
    }

    public void setupPactMagic(){
        int lvl=client.pc.levels[12];
        if(lvl==0){
            pane.setVisible(false);
            return;
        }
        pane.setVisible(true);
        if(lvl>0){
            slot1.setVisible(true);
            levelLabel.setText("Level 1");
        }
        if(lvl>1){
            slot2.setVisible(true);
        }
        if(lvl>2)levelLabel.setText("Level 2");
        if(lvl>4)levelLabel.setText("Level 3");
        if(lvl>6)levelLabel.setText("Level 4");
        if(lvl>8)levelLabel.setText("Level 5");
        if(lvl>10)slot3.setVisible(true);
        if(lvl>16)slot4.setVisible(true);
    }
}
