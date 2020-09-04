import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class LevelUpPopup {
    JButton artificerButton;
    JButton barbarianButton;
    JButton bardButton;
    JButton bloodHunterButton;
    JButton clericButton;
    JButton druidButton;
    JButton fighterButton;
    JButton monkButton;
    JButton paladinButton;
    JButton rangerButton;
    JButton rogueButton;
    JButton sorcererButton;
    JButton warlockButton;
    JButton wizardButton;
    JPanel pane;
    static Character pc = client.pc;
    public LevelUpPopup(){
        JDialog levelDialog = new JDialog();
        int lvl=pc.totalLevel();
        //Check multiclass rules
        if(lvl!=0) {
            if (pc.strScore< 13) {
                if(pc.levels[1]==0)barbarianButton.setEnabled(false);
                if(pc.levels[8]==0)paladinButton.setEnabled(false);
                if (pc.dexScore< 13 && pc.levels[6]==0) fighterButton.setEnabled(false);
            }
            if (pc.dexScore < 13) {
                if(pc.levels[7]==0)monkButton.setEnabled(false);
                if(pc.levels[9]==0)rangerButton.setEnabled(false);
                if(pc.levels[10]==0)rogueButton.setEnabled(false);
            }
            if (pc.intScore < 13) {
                if(pc.levels[0]==0)artificerButton.setEnabled(false);
                if(pc.levels[13]==0)wizardButton.setEnabled(false);
                if(pc.levels[3]==0 && pc.strScore<13 && pc.dexScore<13)bloodHunterButton.setEnabled(false);
            }
            if (pc.wisScore < 13) {
                if(pc.levels[4]==0)clericButton.setEnabled(false);
                if(pc.levels[5]==0)druidButton.setEnabled(false);
                if(pc.levels[7]==0)monkButton.setEnabled(false);
                if(pc.levels[9]==0)rangerButton.setEnabled(false);
            }
            if (pc.chaScore < 13) {
                if(pc.levels[2]==0)bardButton.setEnabled(false);
                if(pc.levels[11]==0) sorcererButton.setEnabled(false);
                if(pc.levels[12]==0)warlockButton.setEnabled(false);
                if(pc.levels[8]==0)paladinButton.setEnabled(false);
            }
        }

        //Artificer goes here
        artificerButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(!confirmClass("Artificer"))return;
                    pc.levels[0]++;
                    updateClassLabel("Artificer",pc.levels[0]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Artificer\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[0]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Artificer", pc.levels[0],in);
                        s=in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Artificer:\n"+ex);}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        barbarianButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(!confirmClass("Barbarian"))return;
                    pc.levels[1]++;
                    updateClassLabel("Barbarian",pc.levels[1]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Barbarian\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[1]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Barbarian", pc.levels[1],in);
                        s=in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Barbarian");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        bardButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Bard"))return;
                    pc.levels[2]++;
                    updateClassLabel("Bard",pc.levels[2]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Bard\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[2]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Bard", pc.levels[2],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Bard");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        //Blood Hunter goes here

        clericButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Cleric"))return;
                    pc.levels[4]++;
                    updateClassLabel("Cleric",pc.levels[4]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Cleric\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[4]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Cleric", pc.levels[4],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Cleric");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        druidButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Druid"))return;
                    pc.levels[5]++;
                    updateClassLabel("Druid",pc.levels[5]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Druid\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[5]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Druid", pc.levels[5],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Druid");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        fighterButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Fighter"))return;
                    pc.levels[6]++;
                    updateClassLabel("Fighter",pc.levels[6]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Fighter\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[6]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Fighter", pc.levels[6],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Fighter");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        monkButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Monk"))return;
                    pc.levels[7]++;
                    updateClassLabel("Monk",pc.levels[7]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Monk\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[7]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Monk", pc.levels[7],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Monk");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        paladinButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Paladin"))return;
                    pc.levels[8]++;
                    updateClassLabel("Paladin",pc.levels[8]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Paladin\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[8]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Paladin", pc.levels[8],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Paladin");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        rangerButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Ranger"))return;
                    pc.levels[9]++;
                    updateClassLabel("Ranger",pc.levels[9]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Ranger\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[9]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Ranger", pc.levels[9],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Ranger:\n"+ex);}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        rogueButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Rogue"))return;
                    pc.levels[10]++;
                    updateClassLabel("Rogue",pc.levels[10]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Rogue\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[10]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Rogue", pc.levels[10],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Rogue");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        sorcererButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Sorcerer"))return;
                    pc.levels[11]++;
                    updateClassLabel("Sorcerer",pc.levels[11]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Sorcerer\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[11]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Sorcerer", pc.levels[11],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Sorcerer");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        warlockButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Warlock"))return;
                    pc.levels[12]++;
                    updateClassLabel("Warlock",pc.levels[12]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Warlock\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[12]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Warlock", pc.levels[12],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Warlock");}
                endLevelUp();
                levelDialog.dispose();
            }
        });

        wizardButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e)  {
                try {
                    if(!confirmClass("Wizard"))return;
                    pc.levels[13]++;
                    updateClassLabel("Wizard",pc.levels[13]);

                    BufferedReader in = new BufferedReader(new FileReader("Files\\Classes\\Wizard\\Base.txt"));
                    String s = in.readLine();
                    while (!s.equals("level"+pc.levels[13]))s=in.readLine();
                    s=in.readLine();
                    while (!s.equals("00")) {
                        pc.parseFeature(s, "Wizard", pc.levels[13],in);
                        s = in.readLine();
                    }

                }catch (Exception ex){System.out.println("Error leveling Wizard");}
                endLevelUp();
                levelDialog.dispose();
            }
        });




        levelDialog.setContentPane(pane);
        levelDialog.pack();
        levelDialog.setSize(new Dimension(650,800));
        levelDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        levelDialog.setVisible(true);
    }

    public void endLevelUp(){
        for(int i=0;i<pc.laterFeatures.size();i++){
            String s=pc.laterFeatures.get(i);
            int lvl = Integer.parseInt(s.substring(0,s.indexOf(":")));
            if(pc.totalLevel()==lvl){
                s=s.substring(s.indexOf(":")+1);
                String source =s.substring(0,s.indexOf(":"));
                s=s.substring(s.indexOf(":")+1);
                try {
                    pc.parseFeature(s, source, lvl, null);
                }catch (Exception e){}
                pc.laterFeatures.remove(i);
                i--;
            }
        }

        if(pc.totalLevel()>16)pc.proficiency=6;
        else if(pc.totalLevel()>12)pc.proficiency=5;
        else if(pc.totalLevel()>8)pc.proficiency=4;
        else if(pc.totalLevel()>4)pc.proficiency=3;
        else pc.proficiency=2;

        //Determine Spell Slots
        pc.casterLevel=0;
        pc.casterLevel+=pc.levels[2];
        pc.casterLevel+=pc.levels[4];
        pc.casterLevel+=pc.levels[5];
        pc.casterLevel+=pc.levels[11];
        pc.casterLevel+=pc.levels[13];
        if(pc.casterLevel==0 && pc.levels[0]>0 && pc.levels[8]==0 && pc.levels[9]==0 && !pc.subclasses.toString().contains("Eldritch Knight") && !pc.subclasses.toString().contains("Arcane Trickster"))pc.casterLevel+=Math.ceil((pc.levels[0])/2.0);
        else pc.casterLevel+=Math.floor((pc.levels[0])/2.0);
        if(pc.casterLevel==0 && pc.levels[8]>0 && pc.levels[9]==0 && !pc.subclasses.toString().contains("Eldritch Knight") && !pc.subclasses.toString().contains("Arcane Trickster"))pc.casterLevel+=Math.ceil((pc.levels[8])/2.0);
        else pc.casterLevel+=Math.floor((pc.levels[8])/2.0);

        if(pc.casterLevel==0 && pc.levels[9]>0 && !pc.subclasses.toString().contains("Eldritch Knight") && !pc.subclasses.toString().contains("Arcane Trickster"))pc.casterLevel+=Math.ceil((pc.levels[9])/2.0);
        else pc.casterLevel+=Math.floor((pc.levels[9])/2.0);

        if(pc.subclasses.toString().contains("Eldritch Knight") && !pc.subclasses.toString().contains("Arcane Trickster")){
            if(pc.casterLevel==0)pc.casterLevel+=Math.ceil((pc.levels[6])/3.0);
            else pc.casterLevel+=Math.floor((pc.levels[6])/3.0);
        }

        if(pc.subclasses.toString().contains("Arcane Trickster")){
            if(pc.casterLevel==0)pc.casterLevel+=Math.ceil((pc.levels[10])/3.0);
            else pc.casterLevel+=Math.floor((pc.levels[10])/3.0);
        }

        System.out.println(pc.casterLevel);
        for(ActionPane ap:pc.weaponActions)ap.parseCode();
        for(ActionPane ap:pc.spellActions)ap.parseCode();
        for(ActionPane ap:pc.otherActions)ap.parseCode();
        int inc = 0;
        while (inc == 0) {
            try {
                inc = Integer.parseInt(JOptionPane.showInputDialog("Enter Health gained"));
            } catch (Exception e) {
            }
        }
        client.gui.maxHpLabel.setText(Integer.toString(Integer.parseInt(client.gui.maxHpLabel.getText()) + inc));
        client.gui.totalHdLabel.setText(Integer.toString(Integer.parseInt(client.gui.totalHdLabel.getText()) + 1));


        client.updateGUI();
        client.pc.longRest();
    }

    public void updateClassLabel(String Class,int lvl){
        if(pc.totalLevel()==1)client.gui.classLabel.setText(Class+" "+lvl);
        else if (lvl==1) client.gui.classLabel.setText(client.gui.classLabel.getText()+"/ "+Class+" "+lvl);
        else{
            String oldLabel=client.gui.classLabel.getText();
            String updatedLabel=oldLabel.substring(0,oldLabel.indexOf(Class))+Class+" "+lvl;
            oldLabel=oldLabel.substring(oldLabel.indexOf(Class));
            if(oldLabel.contains("/"))updatedLabel+=oldLabel.substring(oldLabel.indexOf("/"));
            client.gui.classLabel.setText(updatedLabel);
        }
    }

    /**
     * Has the user confirm their selection in class and provides them with a list detailing all the features
     * @param Class the class chosen by the player
     * @return whether it is confirmed or not
     * @throws Exception IO exceptions from reading files
     */
    public boolean confirmClass(String Class) throws Exception {
        ConfirmPopup cp = new ConfirmPopup("Files\\Classes\\"+Class+"\\");
        return cp.choice;
    }
}
