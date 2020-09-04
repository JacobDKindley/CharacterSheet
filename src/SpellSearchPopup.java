import javax.swing.*;
import java.awt.event.ActionEvent;

public class SpellSearchPopup {
    JTextField nameField;
    JPanel pane;
    JButton searchButton;
    private JCheckBox abjurationCheckBox;
    private JCheckBox conjurationCheckBox;
    private JCheckBox divinationCheckBox;
    private JCheckBox enchantmentCheckBox;
    private JCheckBox artificerCheckBox;
    private JCheckBox evocationCheckBox;
    private JCheckBox illusionCheckBox;
    private JCheckBox necromancyCheckBox;
    private JCheckBox transmutationCheckBox;
    private JCheckBox concentrationCheckBox;
    private JCheckBox ritualCheckBox;
    private JCheckBox bardCheckBox;
    private JCheckBox clericCheckBox;
    private JCheckBox druidCheckBox;
    private JCheckBox paladinCheckBox;
    private JCheckBox rangerCheckBox;
    private JCheckBox sorcererCheckBox;
    private JCheckBox warlockCheckBox;
    private JCheckBox wizardCheckBox;
    private JCheckBox cantripCheckBox;
    private JCheckBox level1CheckBox;
    private JCheckBox level2CheckBox;
    private JCheckBox level3CheckBox;
    private JCheckBox level4CheckBox;
    private JCheckBox level5CheckBox;
    private JCheckBox level6CheckBox;
    private JCheckBox level7CheckBox;
    private JCheckBox level8CheckBox;
    private JCheckBox level9CheckBox;

        public String[] getFilters(){
        String[] out = {"","",""};
        if(abjurationCheckBox.isSelected())out[0]+="Abjuration ";
        if(conjurationCheckBox.isSelected())out[0]+="Conjuration ";
        if(divinationCheckBox.isSelected())out[0]+="Divination ";
        if(enchantmentCheckBox.isSelected())out[0]+="Enchantment ";
        if(evocationCheckBox.isSelected())out[0]+="Evocation ";
        if(illusionCheckBox.isSelected())out[0]+="Illusion ";
        if(necromancyCheckBox.isSelected())out[0]+="Necromancy ";
        if(transmutationCheckBox.isSelected())out[0]+="Transmutation ";
        if(concentrationCheckBox.isSelected())out[0]+="Concentration ";
        if(ritualCheckBox.isSelected())out[0]+="Ritual ";
        if(artificerCheckBox.isSelected())out[1]+="Artificer ";
        if(bardCheckBox.isSelected())out[1]+="Bard ";
        if(clericCheckBox.isSelected())out[1]+="Cleric ";
        if(druidCheckBox.isSelected())out[1]+="Druid ";
        if(paladinCheckBox.isSelected())out[1]+="Paladin ";
        if(rangerCheckBox.isSelected())out[1]+="Ranger ";
        if(sorcererCheckBox.isSelected())out[1]+="Sorcerer ";
        if(warlockCheckBox.isSelected())out[1]+="Warlock ";
        if(wizardCheckBox.isSelected())out[1]+="Wizard ";
        if(cantripCheckBox.isSelected())out[2]+="Cantrip ";
        if(level1CheckBox.isSelected())out[2]+="1 ";
        if(level2CheckBox.isSelected())out[2]+="2 ";
        if(level3CheckBox.isSelected())out[2]+="3 ";
        if(level4CheckBox.isSelected())out[2]+="4 ";
        if(level5CheckBox.isSelected())out[2]+="5 ";
        if(level6CheckBox.isSelected())out[2]+="6 ";
        if(level7CheckBox.isSelected())out[2]+="7 ";
        if(level8CheckBox.isSelected())out[2]+="8 ";
        if(level9CheckBox.isSelected())out[2]+="9 ";

        return out;
    }
}
