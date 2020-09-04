import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


// Attack code: name/properties/command/stat/proficient/bonus;dice/stat/bonus/proficient/damageType;dice/stat/bonus/proficient/damageType
public class ActionPane {
    JPanel pane;
    JButton button;

    String name,properties,mainCode;
    ArrayList<String> codes = new ArrayList<>();

    public ActionPane(){
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ActionPopup(ActionPane.this);
            }
        });
    }

    public ActionPane(String name,String properties,String code){
        this.name=name;
        this.properties=properties;
        while (code.contains(";")){
            codes.add(code.substring(0,code.indexOf(";")));
            code=code.substring(code.indexOf(";")+1);
        }
        if(code.length()!=0)codes.add(code);
        mainCode=codes.remove(0);
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setText(name);
        parseCode();
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ActionPopup(ActionPane.this);
            }
        });
    }

    public void parseCode(){
        pane.removeAll();
        pane.add(button);

        //Parse the main roller
        String temp,stat;
        int bonus;
        try {
            temp = mainCode;
            String command = temp.substring(0, temp.indexOf("|")).trim();
            temp = temp.substring(temp.indexOf("|") + 1);
            stat = temp.substring(0, temp.indexOf("|")).trim();
            temp = temp.substring(temp.indexOf("|") + 1);
            boolean proficient=true;
            try{
                proficient=Boolean.parseBoolean(temp.substring(0,temp.indexOf("|")));
                temp=temp.substring(temp.indexOf("|")+1);
            }catch (Exception e){}
            bonus = 0;
            try {
                bonus = Integer.parseInt(temp.trim());
            } catch (Exception e) {
            }
            Roller mainRoller = new Roller(command);
            mainRoller.bonus = ((proficient) ? client.pc.proficiency:0) + bonus + statBonus(stat);
            mainRoller.button.setToolTipText(command);
            pane.add(mainRoller.button);
        }catch (Exception e){}

        if(codes.size()==0){
            client.updateGUI();
            return;
        }
        //Parse the damage rollers
        for(int i=0;i<codes.size();i++){
            try {
                temp = codes.get(i);
                String dice = temp.substring(0, temp.indexOf("|")).trim();
                temp = temp.substring(temp.indexOf("|") + 1);
                stat = temp.substring(0, temp.indexOf("|")).trim();
                temp = temp.substring(temp.indexOf("|") + 1);
                bonus = 0;
                try {
                    bonus = Integer.parseInt(temp.substring(0, temp.indexOf("|")).trim());
                } catch (Exception e) {
                }
                temp = temp.substring(temp.indexOf("|") + 1);
                boolean proficient =false;
                try{
                    proficient=Boolean.parseBoolean(temp.substring(0,temp.indexOf("|")));
                    temp=temp.substring(temp.indexOf("|")+1);
                }catch (Exception e){}

                String damageType = temp.trim();
                int[] d = new int[6];
                for (int j = 0; j < d.length - 1; j++) {
                    d[j] = Integer.parseInt(dice.substring(0, dice.indexOf(",")));
                    dice = dice.substring(dice.indexOf(",") + 1);
                }
                d[5] = Integer.parseInt(dice);
                Roller roll = new Roller(damageType, d);
                roll.bonus = bonus + ((proficient) ? client.pc.proficiency:0)+ statBonus(stat);
                roll.button.setIcon(new ImageIcon(client.filePath+"Files\\Resources\\Blood.png"));
                roll.button.setSelectedIcon(new ImageIcon(client.filePath+"Files\\Resources\\BloodSelected.png"));
                roll.button.setPressedIcon(new ImageIcon(client.filePath+"Files\\Resources\\BloodSelected.png"));

                String tooltip = "";
                if (d[0] > 0) tooltip += d[0] + "d4 + ";
                if (d[1] > 0) tooltip += d[1] + "d6 + ";
                if (d[2] > 0) tooltip += d[2] + "d8 + ";
                if (d[3] > 0) tooltip += d[3] + "d10 + ";
                if (d[4] > 0) tooltip += d[4] + "d12 + ";
                if (d[5] > 0) tooltip += d[5] + "d20 + ";
                if (tooltip.length() > 0) tooltip = tooltip.substring(0, tooltip.length() - 2) + damageType;
                roll.button.setToolTipText(tooltip);
                pane.add(roll.button);
            }catch (Exception e){}
        }
        client.updateGUI();
    }

    public int statBonus(String stat){
        int out=0;
        switch (stat){
            case "STR":
                out=client.pc.STR;
                break;
            case "DEX":
                out=client.pc.DEX;
                break;
            case "CON":
                out=client.pc.CON;
                break;
            case "INT":
                out=client.pc.INT;
                break;
            case "WIS":
                out=client.pc.WIS;
                break;
            case "CHA":
                out=client.pc.CHA;
                break;
            case "FINESSE":
                out=Math.max(client.pc.STR,client.pc.DEX);
                break;
            case "SPELL":
                out=Math.max(client.pc.INT,client.pc.WIS);
                out=Math.max(out,client.pc.CHA);
                break;
        }
        return out;
    }
}
