import javax.swing.*;
import java.awt.event.ActionEvent;

public class Skill {
    String name,stat;
    boolean proficient,expertise;
    int misc,override;
    private JPanel pane;
    private Roller roller;
    JLabel label;

    public Skill(String name,String stat){
        this.name=name;
        this.stat=stat;
        updateLabel();
    }

    public void setProficient(){
        proficient=true;
        label.setIcon(new ImageIcon(client.filePath+"Files\\Resources\\Proficient.png"));
        updateLabel();
    }

    public void setExpertise(){
        expertise=true;
        label.setIcon(new ImageIcon(client.filePath+"Files\\Resources\\Expertise.png"));
        updateLabel();
    }
    public void updateLabel(){
        label.setText(calculateTotal()+" "+name);
    }

    public int calculateTotal(){
        if(client.pc==null)return 0;
        if(override!=0)return override;
        int total=0;
        switch (stat){
            case "STR":
                total+=client.pc.STR;
                break;
            case "DEX":
                total+=client.pc.DEX;
                break;
            case "CON":
                total+=client.pc.CON;
                break;
            case "INT":
                total+=client.pc.INT;
                break;
            case "WIS":
                total+=client.pc.WIS;
                break;
            case "CHA":
                total+=client.pc.CHA;
                break;
        }
        total+=misc;
        if(proficient)total+=client.pc.proficiency;
        else if(client.pc.jackOfAllTrades)total+=client.pc.proficiency/2;
        if(expertise)total+=client.pc.proficiency;
        roller.bonus=total;
        return total;
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
        roller = new Roller(name);
    }
}
