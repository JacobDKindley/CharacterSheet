import javax.swing.*;
import java.awt.event.ActionEvent;

public class Roller {
    private JPanel panel;
    JButton button;
    int[] dice = {0,0,0,0,0,1};     //d4s,d6s,d8s,d10s,d12s,d20s
    int bonus;
    String command="";
    AbstractAction action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(command.toLowerCase().contains("make") && command.toLowerCase().contains("save"))client.roll(command,dice,bonus+8);
            else client.roll(command,dice,bonus);
        }
    };

    public Roller(String command){
        this.command=command;
        if(command.toLowerCase().contains("make") && command.toLowerCase().contains("save")){
            dice = new int[6];
        }
        button.addActionListener(action);
    }

    public Roller(String command,int[] dice){
        this.command=command;
        this.dice=dice;
        button.addActionListener(action);
    }





}
