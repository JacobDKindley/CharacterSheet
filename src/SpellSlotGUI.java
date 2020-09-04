import javax.swing.*;

public class SpellSlotGUI {
    private JCheckBox lvl1_1;
    private JCheckBox lvl1_2;
    private JCheckBox lvl1_3;
    private JCheckBox lvl1_4;
    private JCheckBox lvl2_1;
    private JCheckBox lvl2_2;
    private JCheckBox lvl2_3;
    private JCheckBox lvl3_1;
    private JCheckBox lvl3_2;
    private JCheckBox lvl3_3;
    private JCheckBox lvl4_1;
    private JCheckBox lvl4_2;
    private JCheckBox lvl4_3;
    private JCheckBox lvl5_1;
    private JCheckBox lvl5_2;
    private JCheckBox lvl5_3;
    private JCheckBox lvl6_1;
    private JCheckBox lvl6_2;
    private JCheckBox lvl7_1;
    private JCheckBox lvl7_2;
    private JCheckBox lvl8_1;
    private JCheckBox lvl9_1;
    private JPanel pane;

    public void reset(){
        lvl1_1.setSelected(false);
        lvl1_2.setSelected(false);
        lvl1_3.setSelected(false);
        lvl1_4.setSelected(false);
        lvl2_1.setSelected(false);
        lvl2_2.setSelected(false);
        lvl2_3.setSelected(false);
        lvl3_1.setSelected(false);
        lvl3_2.setSelected(false);
        lvl3_3.setSelected(false);
        lvl4_1.setSelected(false);
        lvl4_2.setSelected(false);
        lvl4_3.setSelected(false);
        lvl5_1.setSelected(false);
        lvl5_2.setSelected(false);
        lvl5_3.setSelected(false);
        lvl6_1.setSelected(false);
        lvl6_2.setSelected(false);
        lvl7_1.setSelected(false);
        lvl7_2.setSelected(false);
        lvl8_1.setSelected(false);
        lvl9_1.setSelected(false);

    }

    public void setupSpellSlots(){
        int lvl=client.pc.casterLevel;
        if(lvl==0){
            pane.setVisible(false);
            return;
        }
        pane.setVisible(true);
        if(lvl>0){
            lvl1_1.setVisible(true);
            lvl1_2.setVisible(true);
        }
        if(lvl>1){
            lvl1_3.setVisible(true);
        }
        if(lvl>2){
            lvl1_4.setVisible(true);
            lvl2_1.setVisible(true);
            lvl2_2.setVisible(true);
        }
        if(lvl>3){
            lvl2_3.setVisible(true);
        }
        if(lvl>4){
            lvl3_1.setVisible(true);
            lvl3_2.setVisible(true);
        }
        if(lvl>5){
            lvl3_3.setVisible(true);
        }
        if(lvl>6){
            lvl4_1.setVisible(true);
        }
        if(lvl>7){
            lvl4_2.setVisible(true);
        }
        if(lvl>8){
            lvl4_3.setVisible(true);
            lvl5_1.setVisible(true);
        }
        if(lvl>9){
            lvl5_2.setVisible(true);
        }
        if(lvl>10){
            lvl6_1.setVisible(true);
        }
        if(lvl>12){
            lvl7_1.setVisible(true);
        }
        if(lvl>14){
            lvl8_1.setVisible(true);
        }
        if(lvl>16){
            lvl9_1.setVisible(true);
        }
        if(lvl>17){
            lvl5_3.setVisible(true);
        }
        if(lvl>18){
            lvl6_2.setVisible(true);
        }
        if(lvl>19){
            lvl7_2.setVisible(true);
        }
    }
}
