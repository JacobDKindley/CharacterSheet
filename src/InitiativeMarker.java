import javax.swing.*;

public class InitiativeMarker implements Comparable<InitiativeMarker>{
    JLabel name;
    JLabel maxHP;
    JLabel currHP;
    JLabel tempHP;
    JPanel pane;
    private JLabel rollLabel;
    int roll;

    public InitiativeMarker(int roll, String name,String maxHP,String currHP,String tempHP){
        this.name.setText(name);
        this.maxHP.setText(maxHP);
        this.currHP.setText(currHP);
        this.tempHP.setText(tempHP);
        this.rollLabel.setText(""+roll);
        this.roll=roll;
    }

    @Override
    public int compareTo(InitiativeMarker other) {
        return roll-other.roll;
    }
}
