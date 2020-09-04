import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Ability implements Comparable<Ability>{
    JLabel name;
    private JButton minus;
    private JLabel current;
    private JLabel max;
    private JButton plus;
    JLabel recharge;
    JPanel pane;

    int priority,originalMax;

    public Ability(String title,int max,String recharge,int priority){
        name.setText(title);
        originalMax=max;
        setMax();
        this.recharge.setText(recharge);
        this.priority=priority;
        reset();
        minus.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!current.getText().equals("0"))current.setText(Integer.toString(Integer.parseInt(current.getText())-1));
            }
        });
        plus.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!current.getText().equals(Ability.this.max.getText().substring(2)))current.setText(Integer.toString(Integer.parseInt(current.getText())+1));
            }
        });
        pane.setMaximumSize(new Dimension(pane.getMaximumSize().width,pane.getPreferredSize().height));

    }

    /**
     * Some abilities are keyed off of a stat and an initial bonus. If original max is negative then
     * it is keyed off a stat and should be adjusted accordingly.
     */
    void setMax(){
        if(originalMax>0)max.setText(""+originalMax);
        else if(Math.abs(originalMax)%6==1)max.setText(Integer.toString(Integer.parseInt(client.gui.strModLabel.getText())+Math.abs(originalMax)/6));
        else if(Math.abs(originalMax)%6==2)max.setText(Integer.toString(Integer.parseInt(client.gui.dexModLabel.getText())+Math.abs(originalMax)/6));
        else if(Math.abs(originalMax)%6==3)max.setText(Integer.toString(Integer.parseInt(client.gui.conModLabel.getText())+Math.abs(originalMax)/6));
        else if(Math.abs(originalMax)%6==4)max.setText(Integer.toString(Integer.parseInt(client.gui.intModLabel.getText())+Math.abs(originalMax)/6));
        else if(Math.abs(originalMax)%6==5)max.setText(Integer.toString(Integer.parseInt(client.gui.wisModLabel.getText())+Math.abs(originalMax)/6));
        else if(Math.abs(originalMax)%6==0)max.setText(Integer.toString(Integer.parseInt(client.gui.chaModLabel.getText())+Math.abs(originalMax)/6-1));
        max.setText("/ "+max.getText());
    }

    public void reset(){
        current.setText(max.getText().substring(2));
    }

    /**
     * Determines if some other Ability has a higher priority than this Ability
     * @param o the other Ability
     * @return positive if other has higher priority
     */
    @Override
    public int compareTo(Ability o) {
        return o.priority-this.priority;
    }
}
