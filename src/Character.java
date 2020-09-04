import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Character {
    int STR,DEX,CON,INT,WIS,CHA,proficiency=2,strScore=10,dexScore=10,conScore=10,intScore=10,wisScore=10,chaScore=10;
    Skill[] skills = new Skill[24];
    Save[] saves = new Save[6];
    ArrayList<Feature> features = new ArrayList<>();
    ArrayList<Ability> abilities = new ArrayList<>();
    ArrayList<AbilityText> abilityTexts = new ArrayList<>();
    int[] levels = new int[14];
    ArrayList<String> subclasses = new ArrayList<>(),laterFeatures = new ArrayList<>();
    String armorProficiency="",weaponProficiency="",otherProficiency="",languages="";
    static String[] listOfLanguages={"Abyssal","Celestial","Common","Deep Speech","Draconic","Dwarvish","Elvish","Giant","Gnomish","Goblin","Halfling","Infernal","Orc","Primordial","Sylvan","Undercommon"};
    ArrayList<SpellInfo> yourSpells= new ArrayList<>(),preparedSpells = new ArrayList<>();
    ArrayList<ActionPane> weaponActions = new ArrayList<>(), spellActions = new ArrayList<>(), otherActions = new ArrayList<>();
    ArrayList<Item> items = new ArrayList<>();
    int passivePerceptionBonus,passiveInsightBonus,passiveInvestigationBonus,initBonus;
    String additionalSenses="", defenses="",chooseFolderChosen="";
    boolean jackOfAllTrades=false;
    int casterLevel=0;


    public Character(){
        skills[0] = client.gui.acrobatics;
        skills[1] = client.gui.animalHandling;
        skills[2] = client.gui.arcana;
        skills[3] = client.gui.athletics;
        skills[4] = client.gui.deception;
        skills[5] = client.gui.history;
        skills[6] = client.gui.insight;
        skills[7] = client.gui.intimidation;
        skills[8] = client.gui.investigation;
        skills[9] = client.gui.medicine;
        skills[10] = client.gui.nature;
        skills[11] = client.gui.perception;
        skills[12] = client.gui.performance;
        skills[13] = client.gui.persuasion;
        skills[14] = client.gui.religion;
        skills[15] = client.gui.sleightOfHand;
        skills[16] = client.gui.stealth;
        skills[17]= client.gui.survival;
        skills[18] = new Skill("STR Check","STR");
        skills[19] = new Skill("DEX Check","DEX");
        skills[20] = new Skill("CON Check","CON");
        skills[21] = new Skill("INT Check","INT");
        skills[22] = new Skill("WIS Check","WIS");
        skills[23] = new Skill("CHA Check","CHA");
        saves[0] = client.gui.strSave;
        saves[1] = client.gui.dexSave;
        saves[2] = client.gui.conSave;
        saves[3] = client.gui.intSave;
        saves[4] = client.gui.wisSave;
        saves[5] = client.gui.chaSave;
        Arrays.fill(levels,0);

    }

    public void setStats(){
        JDialog dialog = new JDialog();
        StatSelection ss = new StatSelection(this);
        dialog.setContentPane(ss.pane);
        dialog.pack();
        dialog.setSize(new Dimension(600,800));
        ss.finalizeButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                strScore=Integer.parseInt(ss.strTotal.getText());
                dexScore=Integer.parseInt(ss.dexTotal.getText());
                conScore=Integer.parseInt(ss.conTotal.getText());
                intScore=Integer.parseInt(ss.intTotal.getText());
                wisScore=Integer.parseInt(ss.wisTotal.getText());
                chaScore=Integer.parseInt(ss.chaTotal.getText());
                updateModifiers();
                client.updateGUI();
                dialog.dispose();
            }
        });
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);

    }

    public void updateModifiers(){
        STR=strScore/2-5;
        DEX=dexScore/2-5;
        CON=conScore/2-5;
        INT=intScore/2-5;
        WIS=wisScore/2-5;
        CHA=chaScore/2-5;
    }

    public void selectRace(){
        try {
            File[] races = new File(client.filePath+"Files\\Races").listFiles();
            String[] raceNames = new String[races.length];
            for (int i = 0; i < raceNames.length; i++) raceNames[i] = races[i].getName();
            JComboBox<String> list = new JComboBox<>(raceNames);
            ConfirmPopup cp;
            do{
                JOptionPane.showMessageDialog(null, list, "Race Selection", JOptionPane.PLAIN_MESSAGE);
                 cp = new ConfirmPopup(client.filePath+"Files\\Races\\"+list.getSelectedItem()+"\\");
            }while (!cp.choice);
            BufferedReader in = new BufferedReader(new FileReader(client.filePath+"Files\\Races\\"+list.getSelectedItem()+"\\Base.txt"));
            client.gui.raceLabel.setText((String)list.getSelectedItem());
            String s=in.readLine();
            while (s!=null){
                parseFeature(s,(String)list.getSelectedItem(),1,in);
                s=in.readLine();
            }
        }catch (Exception e){System.out.println("Error selecting race:\n"+e);}
    }

    public void selectBackground(){
        try {
            File[] files = new File(client.filePath+"Files\\Backgrounds").listFiles();
            String[] names = new String[files.length];
            for (int i = 0; i < names.length; i++) names[i] = files[i].getName().substring(0,files[i].getName().indexOf(".txt"));
            JComboBox<String> list = new JComboBox<>(names);
            ConfirmPopup cp;
            do{
                JOptionPane.showMessageDialog(null, list, "Background Selection", JOptionPane.PLAIN_MESSAGE);
                cp = new ConfirmPopup(client.filePath+"Files\\Backgrounds\\"+list.getSelectedItem()+".txt");
            }while (!cp.choice);
            BufferedReader in = new BufferedReader(new FileReader(client.filePath+"Files\\Backgrounds\\"+list.getSelectedItem()+".txt"));
            client.gui.backgroundLabel.setText((String)list.getSelectedItem());
            String s=in.readLine();
            while (s!=null){
                parseFeature(s,"Background",1,in);
                s=in.readLine();
            }
        }catch (Exception e){System.out.println("Error selecting background:\n"+e);}
    }

    public void levelUp(){
        new LevelUpPopup();
    }

    public void parseFeature(String s,String source,int lvl,BufferedReader in) throws IOException {
        if(!s.contains(":"))return;
        String command = s.substring(0, s.indexOf(":"));
        switch (command) {
            case "F":       //Adds a feature to the character
                s = s.substring(s.indexOf("*") + 1);
                String title = s.substring(0, s.indexOf("*"));
                s = s.substring(s.indexOf("*") + 1);
                String description = s;
                features.add(new Feature(title, client.convertFromGrave(description),source,lvl));
                break;
            case "P":       //Adds a proficiency
                proficiency(s.substring(2));
                break;
            case "PC":      //Choose between a list of proficiencies
                s=s.substring(3);
                ArrayList<String> options = new ArrayList<>();
                while (s.contains("/")){
                    options.add(s.substring(0,s.indexOf("/")));
                    s=s.substring(s.indexOf("/")+1);
                }
                String[] skimmed = skimKnownSkills(options.toArray(new String[0]));
                JComboBox<String> list = new JComboBox<>(skimmed);
                JOptionPane.showMessageDialog(null, list, "Skill Proficiencies", JOptionPane.PLAIN_MESSAGE);
                proficiency((String)list.getSelectedItem());
                break;
            case "A":       //Adds a new ability that has uses to a character
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                s=s.substring(s.indexOf("*")+1);
                int max = Integer.parseInt(s.substring(0,s.indexOf("/")));
                s=s.substring(s.indexOf("/")+1);
                String recharge = s.substring(0,s.indexOf("/"));
                s=s.substring(s.indexOf("/")+1);
                int priority=Integer.parseInt(s);
                Ability tempAbility = new Ability(title,max,recharge,priority);
                int i;
                for(i=0;i<abilities.size();i++){
                    if(tempAbility.compareTo(abilities.get(i))>0){
                        break ;
                    }
                }
                abilities.add(i,tempAbility);
                break;
            case "AT":      //Creates an ability that doesn't have uses
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                s=s.substring(s.indexOf("*")+1);
                description = s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                priority=Integer.parseInt(s);
                AbilityText tempAbilityText = new AbilityText(title,description,priority);
                i=0;
                for(i=0;i<abilityTexts.size();i++){
                    if(tempAbilityText.compareTo(abilityTexts.get(i))>0){
                        break ;
                    }
                }
                abilityTexts.add(i,tempAbilityText);
                break;
            case "AM":      //Increases the maximum of an ability by some increment
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                s=s.substring(s.indexOf("*")+1);
                int increment = Integer.parseInt(s);
                for(Ability a:abilities){
                    if(a.name.getText().equals(title)){
                        if(a.originalMax<0)a.originalMax-=6*increment;
                        else a.originalMax+=increment;
                        a.setMax();
                    }
                }
                break;
            case "ATM":     //Increases the maximum of an ability text by some increment
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                s=s.substring(s.indexOf("*")+1);
                increment = Integer.parseInt(s);
                for(AbilityText a:abilityTexts){
                    if(a.title.equals(title)){
                        String temp=a.label.getText();
                        int newMax=Integer.parseInt(temp.substring(temp.indexOf(": ")+2))+increment;
                        String updated = temp.substring(0,temp.indexOf(": "))+": "+newMax;
                        a.label.setText(updated);
                        break;
                    }
                }
                break;
            case "ASI":         //Grants players a choice between a feat and an ASI
                //Choose ability score to increase
                String[] stringArr = {"Feat","ASI"};
                list = new JComboBox<>(stringArr);
                JOptionPane.showMessageDialog(null, list, "ASI Selection", JOptionPane.PLAIN_MESSAGE);
                if(list.getSelectedIndex()==0)chooseFeat();
                else{
                    options = new ArrayList<>();
                    if(client.pc.strScore<20)options.add("STR");
                    if(client.pc.dexScore<20)options.add("DEX");
                    if(client.pc.conScore<20)options.add("CON");
                    if(client.pc.intScore<20)options.add("INT");
                    if(client.pc.wisScore<20)options.add("WIS");
                    if(client.pc.chaScore<20)options.add("CHA");
                    list = new JComboBox(options.toArray(new String[0]));
                    JOptionPane.showMessageDialog(null, list, "ASI Selection", JOptionPane.PLAIN_MESSAGE);
                    parseFeature(list.getSelectedItem()+":1",source,lvl,in);
                    options = new ArrayList<>();
                    if(client.pc.strScore<20)options.add("STR");
                    if(client.pc.dexScore<20)options.add("DEX");
                    if(client.pc.conScore<20)options.add("CON");
                    if(client.pc.intScore<20)options.add("INT");
                    if(client.pc.wisScore<20)options.add("WIS");
                    if(client.pc.chaScore<20)options.add("CHA");
                    list = new JComboBox(options.toArray(new String[0]));
                    JOptionPane.showMessageDialog(null, list, "ASI Selection", JOptionPane.PLAIN_MESSAGE);
                    parseFeature(list.getSelectedItem()+":1",source,lvl,in);

                }
                break;
            case "Choose Subclass":             //Choose subclass
                File dir = new File(client.filePath+"Files\\Classes\\"+source);
                File[] files = dir.listFiles();
                options = new ArrayList<>();
                for(File f:files){
                    if(!f.getName().contains("Base"))options.add(f.getName().substring(0,f.getName().indexOf(".")));
                }
                list = new JComboBox(options.toArray());
                JOptionPane.showMessageDialog(null, list, "Select Subclass", JOptionPane.PLAIN_MESSAGE);
                subclasses.add(client.filePath+"Files\\Classes\\" + source + "\\" + list.getSelectedItem() + ".txt");
            case "Sub":                         //Gain features of subclass at according level
                String path="";
                for(String s1:subclasses){
                    if(s1.contains(client.filePath+"Files\\Classes\\"+source))path=s1;
                }
                BufferedReader subIn = new BufferedReader(new FileReader(path));
                s=subIn.readLine();
                while (!s.equals("level"+lvl))s=subIn.readLine();
                s=subIn.readLine();
                while (!s.equals("00")){
                    parseFeature(s,source,lvl,subIn);
                    s=subIn.readLine();
                }
                break;
            case "RA":                      //Removes an ability or abilityText from the character
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                for(i=0;i<abilities.size();i++){
                    if(abilities.get(i).name.getText().equals(title)){
                        abilities.remove(i);
                        break;
                    }
                }
                for(i=0;i<abilityTexts.size();i++){
                    if(abilityTexts.get(i).title.equals(title)){
                        abilityTexts.remove(i);
                        break;
                    }
                }
                break;
            case "LVL1":                    //Flags the line as something you only get if you start with this class
                if(totalLevel()!=1)break;
                s=s.substring(s.indexOf(":")+1);
                parseFeature(s,source,lvl,in);
                break;
            case "STR":                     //Increases STR by some increment with a max of 20
                if(strScore<20)strScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "DEX":                     //Increases DEX by some increment with a max of 20
                if(dexScore<20)dexScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "CON":                     //Increases CON by some increment with a max of 20
                if(conScore<20)conScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "INT":                     //Increases INT by some increment with a max of 20
                if(intScore<20)intScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "WIS":                     //Increases WIS by some increment with a max of 20
                if(wisScore<20)wisScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "CHA":                     //Increases CHA by some increment with a max of 20
                if(chaScore<20)chaScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "STR_UNGUARD":             //Increases STR by some increment without a max
                strScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "DEX_UNGUARD":             //Increases DEX by some increment without a max
                dexScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "CON_UNGUARD":             //Increases CON by some increment without a max
                conScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "INT_UNGUARD":             //Increases INT by some increment without a max
                intScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "WIS_UNGUARD":             //Increases WIS by some increment without a max
                wisScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "CHA_UNGUARD":             //Increases CHA by some increment without a max
                chaScore+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "Choose Subrace":          //Choose subrace
                dir = new File(client.filePath+"Files\\Races\\"+source);
                files = dir.listFiles();
                options = new ArrayList<>();
                for(File f:files){
                    if(!f.getName().contains("Base"))options.add(f.getName().substring(0,f.getName().indexOf(".")));
                }
                list = new JComboBox(options.toArray());
                JOptionPane.showMessageDialog(null, list, "Select Subrace", JOptionPane.PLAIN_MESSAGE);
                File subrace = new File(client.filePath+"Files\\Races\\"+source+"\\"+list.getSelectedItem()+".txt");
                client.gui.raceLabel.setText((String)list.getSelectedItem());
                subIn = new BufferedReader(new FileReader(subrace));
                String s1=subIn.readLine();
                while (s1!=null){
                    parseFeature(s1,(String)list.getSelectedItem(),lvl,subIn);
                    s1= subIn.readLine();
                }
                break;
            case "L":       //Language
                String language = s.substring(s.indexOf(":")+1);
                if(!languages.contains(language))learnLanguage(language);
                break;
            case "LC":      //Language choice
                list=new JComboBox<>(skimLanguages());
                JOptionPane.showMessageDialog(null, list, "Language Choice", JOptionPane.PLAIN_MESSAGE);
                learnLanguage((String)list.getSelectedItem());
                break;
            case "EC":      //Expertise choice
                ArrayList<String> available = new ArrayList<>();
                for(Skill skill:skills)if(skill.proficient && !skill.expertise)available.add(skill.name);
                if(otherProficiency.contains("Thieves' Tools") && !otherProficiency.contains("Thieves' Tools(Expertise)"))available.add("Thieves' Tools");
                list = new JComboBox<>(available.toArray(new String[0]));
                JOptionPane.showMessageDialog(null, list, "Expertise Choice", JOptionPane.PLAIN_MESSAGE);
                gainExpertise((String)list.getSelectedItem());
                break;
            case "E":       //Gain expertise in a skill if proficient
                gainExpertise(s.substring(s.indexOf(":")+1));
                break;
            case "JACK":    //Gain jack of all trades ability
                jackOfAllTrades=true;
                break;
            case "P&E":     //Gain proficiency and expertise from a list of abilities
                s=s.substring(s.indexOf(":")+1);
                available=new ArrayList<>();
                while (s.contains("/")){
                    String name=s.substring(0,s.indexOf("/"));
                    for(Skill skill:skills)if(skill.name.equals(name) && !skill.expertise)available.add(name);
                    s=s.substring(s.indexOf("/")+1);
                }
                list = new JComboBox<>(available.toArray(new String[0]));
                JOptionPane.showMessageDialog(null, list, "Expertise Choice", JOptionPane.PLAIN_MESSAGE);
                proficiency((String)list.getSelectedItem());
                gainExpertise((String)list.getSelectedItem());
                break;
            case "S":       //Sets your speed
                client.gui.speedLabel.setText(s.substring(s.indexOf(":")+1));
                break;
            case "DA":      //Grants the draconic ancestry for dragonborn
                String[] dragons={"Black","Blue","Brass","Bronze","Copper","Gold","Green","Red","Silver","White"};
                list = new JComboBox<>(dragons);
                JOptionPane.showMessageDialog(null,list,"Draconic Ancestry",JOptionPane.PLAIN_MESSAGE);
                String breath="",save="",damage="",color=(String)list.getSelectedItem();
                if(list.getSelectedItem().equals("Black")){
                    breath="5' by 30' line";
                    save="DEX Save";
                    damage="Acid";
                }else if(list.getSelectedItem().equals("Blue")){
                    breath="5' by 30' line";
                    save="DEX Save";
                    damage="Lightning";
                }else if(list.getSelectedItem().equals("Brass")){
                    breath="5' by 30' line";
                    save="DEX Save";
                    damage="Fire";
                }else if(list.getSelectedItem().equals("Bronze")){
                    breath="5' by 30' line";
                    save="DEX Save";
                    damage="Lightning";
                }else if(list.getSelectedItem().equals("Copper")){
                    breath="5' by 30' line";
                    save="DEX Save";
                    damage="Acid";
                }else if(list.getSelectedItem().equals("Gold")){
                    breath="15' cone";
                    save="DEX Save";
                    damage="Fire";
                }else if(list.getSelectedItem().equals("Green")){
                    breath="15' cone";
                    save="CON Save";
                    damage="Poison";
                }else if(list.getSelectedItem().equals("Red")){
                    breath="15' cone";
                    save="DEX Save";
                    damage="Fire";
                }else if(list.getSelectedItem().equals("Silver")){
                    breath="15' cone";
                    save="CON Save";
                    damage="Cold";
                }else if(list.getSelectedItem().equals("White")){
                    breath="15' cone";
                    save="CON Save";
                    damage="Cold";
                }
                String bw = "F:*Breath Weapon*You can use your action to exhale destructive energy. Each creature in a "+breath+" must make a "+save+". The DC of this saving throw is 8 + your Constitution modifier + your proficiency bonus. A creature takes 2d6 "+damage+" damage on a failed save, and half as much damage on a successful one. The damage increase to 3d6 at 6th level, 4d6 at 11th, and 5d6 at 16th level. After using your breath weapon, you cannot use it again until you complete a short or long rest.";
                String dr = "F:*Damage Resistance*You have resistance to "+damage+" damage.";
                String resistance = "DEFENSE:"+damage+" resistance.";
                String code="Make "+save+"|CON|0;0,2,0,0,0,0||0|"+damage;
                client.gui.raceLabel.setText(color+" Dragonborn");
                parseFeature(bw,client.gui.raceLabel.getText(),1,in);
                parseFeature(dr,client.gui.raceLabel.getText(),1,in);
                parseFeature(resistance,client.gui.raceLabel.getText(),1,in);
                otherActions.add(new ActionPane("Breath Weapon","",code));
                break;
            case "PCI":     //Gain proficiency in a skill or save, if already proficient choose something from the rest of the list
                s=s.substring(s.indexOf(":")+1);
                String first=s.substring(0,s.indexOf("/"));
                for(Skill skill:skills)if(skill.name.equals(first) && !skill.proficient){
                    skill.setProficient();
                    return;
                }
                for(Save sa:saves)if(sa.name.equals(first) && !sa.proficient){
                    sa.setProficient();
                    return;
                }
                if(!client.gui.proficiencyTextPane.getText().contains(first)){
                    proficiency(first);
                    return;
                }
                parseFeature("PC:"+s.substring(s.indexOf("/")+1),source,lvl,in);
                break;
            case "ATC":
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                s=s.substring(s.indexOf("*")+1);
                for(AbilityText at:abilityTexts)if(at.title.equals(title)){
                    available=new ArrayList<>();
                    String original=at.label.getText();
                    String updated=original.substring(original.indexOf(":")+1).trim();
                    while (s.contains("/")){
                        if(!updated.contains(s.substring(0,s.indexOf("/"))))available.add(s.substring(0,s.indexOf("/")));
                        s=s.substring(s.indexOf("/")+1);
                    }
                    list= new JComboBox<>(available.toArray(new String[0]));
                    JOptionPane.showMessageDialog(null,list,title,JOptionPane.PLAIN_MESSAGE);
                    if(updated.equals(""))updated=(String)list.getSelectedItem();
                    else updated+=", "+list.getSelectedItem();
                    at.label.setText(original.substring(0,original.indexOf(":")+1)+" "+updated);

                }
                break;
            case "CHOICE":
                ArrayList<String> choices = new ArrayList<>();
                ArrayList<Integer> skips = new ArrayList<>();
                s=s.substring(s.indexOf(":")+1);
                while (s.contains("/")){
                    choices.add(s.substring(0,s.indexOf("/")));
                    s=s.substring(s.indexOf("/")+1);
                    skips.add(Integer.parseInt(s.substring(0,s.indexOf("/"))));
                    s=s.substring(s.indexOf("/")+1);
                }
                list = new JComboBox<>(choices.toArray(new String[0]));
                JOptionPane.showMessageDialog(null,list,"Choice",JOptionPane.PLAIN_MESSAGE);
                for(i=0;i<skips.get(list.getSelectedIndex());i++)in.readLine();
                break;
            case "ATI":
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                for(AbilityText at:abilityTexts)if(at.title.equals(title)){
                    String original=at.label.getText();
                    String updated=original.substring(original.indexOf(":")+1).trim();
                    if(updated.equals(""))updated=JOptionPane.showInputDialog(null,"Custom Enter:",title,JOptionPane.PLAIN_MESSAGE);
                    else updated+=", "+JOptionPane.showInputDialog(null,"Custom Enter:",title,JOptionPane.PLAIN_MESSAGE);
                    at.label.setText(original.substring(0,original.indexOf(":")+1)+" "+updated);
                }
                break;
            case "LCL"://Learn a language from a set List
                s=s.substring(s.indexOf(":")+1);
                available= new ArrayList<>();
                while (s.contains("/")){
                    if(!languages.contains(s.substring(0,s.indexOf("/"))))available.add(s.substring(0,s.indexOf("/")));
                    s=s.substring(s.indexOf("/")+1);
                }
                list = new JComboBox<>(available.toArray(new String[0]));
                JOptionPane.showMessageDialog(null,list,"Language Selection",JOptionPane.PLAIN_MESSAGE);
                learnLanguage((String)list.getSelectedItem());
                break;
            case "RT":      //Removes an item from an abilityText list. EX. if you want to remove an eldritch invocation chosen
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                for(AbilityText at:abilityTexts){
                    if(at.title.equals(title)){
                        String original = at.label.getText();
                        String temp=original.substring(original.indexOf(":")+1).trim();
                        available = new ArrayList<>();
                        while (temp.contains(",")){
                            available.add(temp.substring(0,temp.indexOf(",")));
                            temp=temp.substring(temp.indexOf(",")+2);
                        }
                        available.add(temp);
                        list = new JComboBox<>(available.toArray(new String[0]));
                        JOptionPane.showMessageDialog(null,list,title+" Removal Selection",JOptionPane.PLAIN_MESSAGE);
                        String toRemove=(String)list.getSelectedItem();
                        String updated=original.substring(original.indexOf(":")+1).trim();
                        if(updated.indexOf(toRemove)==0){
                            if(updated.length()==toRemove.length())updated="";
                            else updated=updated.substring(updated.indexOf(",")+2);
                        }else{
                            updated=updated.substring(0,updated.indexOf(toRemove)-2)+updated.substring(updated.indexOf(toRemove)+toRemove.length());
                        }
                        at.label.setText(original.substring(0,original.indexOf(":"))+": "+updated);
                        break;
                    }
                }
                break;
            case "SKIP":        //Used with CHOICE to skip lines so choices aren't stacked
                int skip=Integer.parseInt(s.substring(s.indexOf(":")+1));
                for(i=0;i<skip;i++)in.readLine();
                break;
            case "WEAPONACTION":
                s=s.substring(s.indexOf(":")+1);
                String name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                String properties=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                ActionPane ap = new ActionPane(name,properties,s);
                weaponActions.add(ap);
                break;
            case "SPELLACTION":
                s=s.substring(s.indexOf(":")+1);
                name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                properties=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                ap = new ActionPane(name,properties,s);
               spellActions.add(ap);
                break;
            case "OTHERACTION":
                s=s.substring(s.indexOf(":")+1);
                name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                properties=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                ap = new ActionPane(name,properties,s);
                otherActions.add(ap);
                break;
            case "SENSE":
                s=s.substring(s.indexOf(":")+1);
                if(additionalSenses.length()==0)additionalSenses=s;
                else additionalSenses+=", "+s;
                break;
            case "DEFENSE":
                s=s.substring(s.indexOf(":")+1);
                if(defenses.length()==0)defenses=s;
                else defenses+="\n"+s;
                break;
            case "KAL"://Kalishtar stuff
                String[] strArr = {"Insight","Intimidation","Performance","Persuasion"};
                list = new JComboBox<>(strArr);
                JOptionPane.showMessageDialog(null,list,"Psychic Glamour",JOptionPane.PLAIN_MESSAGE);
                String glam = "F:*Psychic Glamour*You have advantage on all ability checks you make with "+list.getSelectedItem()+".";
                parseFeature(glam,source,lvl,in);
                parseFeature("DEFENSE:Advantage on all "+list.getSelectedItem()+" ability checks.",source,lvl,in);
                break;
            case "VASC":
                String[] abilityScores={"STR","DEX","CON","INT","WIS","CHA"};
                list= new JComboBox<>(abilityScores);
                JOptionPane.showMessageDialog(null,list,"ASI Selection",JOptionPane.PLAIN_MESSAGE);
                parseFeature(list.getSelectedItem()+":1",source,lvl,in);
                list.removeItemAt(list.getSelectedIndex());
                JOptionPane.showMessageDialog(null,list,"ASI Selection",JOptionPane.PLAIN_MESSAGE);
                parseFeature(list.getSelectedItem()+":1",source,lvl,in);
                break;
            case "FEAT":
                chooseFeat();
                break;
            case "ASC":
                s=s.substring(s.indexOf(":")+1);
                options = new ArrayList<>();
                if(s.contains("STR") && strScore<20)options.add("STR");
                if(s.contains("DEX") && dexScore<20)options.add("DEX");
                if(s.contains("CON") && conScore<20)options.add("CON");
                if(s.contains("INT") && intScore<20)options.add("INT");
                if(s.contains("WIS") && wisScore<20)options.add("WIS");
                if(s.contains("CHA") && chaScore<20)options.add("CHA");
                list= new JComboBox<>(options.toArray(new String[0]));
                JOptionPane.showMessageDialog(null,list,"ASI Selection",JOptionPane.PLAIN_MESSAGE);
                parseFeature(list.getSelectedItem()+":1",source,lvl,in);
                break;
            case "SPEED_INCREASE":
                client.gui.speedLabel.setText(Integer.toString(Integer.parseInt(client.gui.speedLabel.getText())+Integer.parseInt(s.substring(s.indexOf(":")+1))));
                break;
            case "PASS_PERCEPTION":
                passivePerceptionBonus+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "PASS_INVESTIGATION":
                passiveInvestigationBonus+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "INIT_INCREASE":
                initBonus+=Integer.parseInt(s.substring(s.indexOf(":")+1));
                break;
            case "LATER_FEATURE":
                laterFeatures.add(s.substring(s.indexOf(":")+1));
                break;
            case "GAMING_SET":
                String input=JOptionPane.showInputDialog("Gain Proficiency in a Gaming Set:");
                proficiency(input);
                break;
            case "G":       //Gold increase
                try {
                    client.gui.gpTextField.setText(Integer.toString(Integer.parseInt(client.gui.gpTextField.getText()) + Integer.parseInt(s.substring(s.indexOf(":") + 1))));
                }catch (Exception e){}
                break;
            case "ITEM":
                Item it = new Item();
                s=s.substring(s.indexOf(":")+1);
                it.name=s;
                items.add(it);
                break;
            case "FOLDER_SELECTION":        //FOLDER_SELECTION:folder:options
                s=s.substring(s.indexOf(":")+1);
                String folder =s.substring(0,s.indexOf(":"));
                s=s.substring(s.indexOf(":")+1);
                chooseFromFolder(folder,s);
                break;
            case "ATU":                     //ATU:*title*text to update with
                s=s.substring(s.indexOf("*")+1);
                title=s.substring(0,s.indexOf("*"));
                String update = s.substring(s.indexOf("*")+1);
                for(AbilityText at:abilityTexts)if(at.title.equals(title)){
                    String original=at.label.getText();
                    String updated=original.substring(original.indexOf(":")+1).trim();
                    if(updated.equals(""))updated=update;
                    else updated+=", "+update;
                    at.label.setText(original.substring(0,original.indexOf(":")+1)+" "+updated);
                }
                break;
            case "FOLDER_SELECTION_DUPLICATE":      //removes a file from the selected list so it can be selected multiple times
                s=s.substring(s.indexOf(":")+1);
                chooseFolderChosen=chooseFolderChosen.substring(0,chooseFolderChosen.indexOf(s)-1)+chooseFolderChosen.substring(chooseFolderChosen.indexOf(s)+s.length()+1);
                break;

        }
        client.updateGUI();

    }

    public void parseItem(File itemFile) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(itemFile));
            Item item = new Item();
            items.add(item);
            item.name= itemFile.getName().substring(0,itemFile.getName().indexOf(".txt"));
            String s=in.readLine();
            while (s!=null){
                String command=s.substring(0,s.indexOf(":"));
                s=s.substring(s.indexOf(":")+1);
                switch (command){
                    case "VALUE":
                        item.value=s;
                        break;
                    case "WEIGHT":
                        item.weight=Double.parseDouble(s);
                        break;
                    case "DESCRIPTION":
                        item.description=client.convertFromGrave(s);
                        break;
                    case "WEAPONACTION":
                        String name=s.substring(0,s.indexOf("|"));
                        s=s.substring(s.indexOf("|")+1);
                        String properties=s.substring(0,s.indexOf("|"));
                        s=s.substring(s.indexOf("|")+1);
                        ActionPane ap = new ActionPane(name,properties,s);
                        weaponActions.add(ap);
                        break;
                }
                s=in.readLine();
            }
            client.updateGUI();
        }catch (Exception e){System.out.println("Error adding item:\n"+e);}
    }


    public int totalLevel(){
        int total=0;
        for(int i:levels)total+=i;
        return total;
    }

    public void gainExpertise(String name){
        if(name.equals("Thieves' Tools")){
            if(!otherProficiency.contains("Thieves' Tools"))return;
            otherProficiency = otherProficiency.substring(0,otherProficiency.indexOf("Thieves' Tools"))+"Thieves' Tools(Expertise)"+otherProficiency.substring(otherProficiency.indexOf("Thieves' Tools")+14);
            String original=client.gui.proficiencyTextPane.getText();
            String changed = original.substring(0,original.indexOf("\n\nOther\n"))+"\n\nOther\n"+otherProficiency+original.substring(original.indexOf("\n\nLanguages\n"));
            client.gui.proficiencyTextPane.setText(changed);
            return;
        }
        for(Skill s:skills)if(s.name.equals(name) && s.proficient)s.setExpertise();

    }

    public void learnLanguage(String language){
        if(languages.equals(""))languages+=language;
        else languages+=", "+language;
        String original=client.gui.proficiencyTextPane.getText();
        String changed = original.substring(0,original.indexOf("\n\nLanguages\n"))+"\n\nLanguages\n"+languages;
        client.gui.proficiencyTextPane.setText(changed);
    }

    /**
     * Adds a skill/save/tool/weapon/armor proficiency to the character
     * @param skill what proficiency to add
     */
    public void proficiency(String skill) {
        if(skill==null)return;
        for(Skill s:skills){
            if(s.name.equals(skill)) {
                s.setProficient();
                return;
            }
        }
        for(Save s:saves){
            if(s.name.equals(skill)){
                s.setProficient();
                return;
            }
        }

        //Check if it's armor/shield
        String original=client.gui.proficiencyTextPane.getText();
        if(skill.contains("Armor") || skill.contains("Shield")){
            if(armorProficiency.equals(""))armorProficiency+=skill;
            else armorProficiency+=", "+skill;
            String changed = "Armor\n"+armorProficiency+original.substring(original.indexOf("\n\nWeapons\n"));
            client.gui.proficiencyTextPane.setText(changed);
            return;
        }

        //Check if it's weapons
        switch (skill) {
            case "Club":
            case "Dagger":
            case "Greatclub":
            case "Handaxe":
            case "Javelin":
            case "Light Hammer":
            case "Mace":
            case "Quarterstaff":
            case "Sickle":
            case "Spear":
            case "Light Crossbow":
            case "Dart":
            case "Shortbow":
            case "Sling":
            case "Simple Weapons":
                if (weaponProficiency.contains("Simple Weapons")) return;
                if(weaponProficiency.equals(""))weaponProficiency+=skill;
                else weaponProficiency+=", "+skill;
                String changed = original.substring(0,original.indexOf("\n\nWeapons\n"))+"\n\nWeapons\n"+weaponProficiency+original.substring(original.indexOf("\n\nOther\n"));
                client.gui.proficiencyTextPane.setText(changed);
                return;
            case "Battleaxe":
            case "Flail":
            case "Glaive":
            case "Greataxe":
            case "Greatsword":
            case "Halberd":
            case "Lance":
            case "Longsword":
            case "Maul":
            case "Morningstar":
            case "Pike":
            case "Rapier":
            case "Scimitar":
            case "Shortsword":
            case "Trident":
            case "War Pick":
            case "Warhammer":
            case "Whip":
            case "Blowgun":
            case "Hand Crossbow":
            case "Heavy Crossbow":
            case "Longbow":
            case "Net":
            case "Martial Weapons":
                if (weaponProficiency.contains("Martial Weapons")) return;
                if(weaponProficiency.equals(""))weaponProficiency+=skill;
                else weaponProficiency+=", "+skill;
                changed = original.substring(0,original.indexOf("\n\nWeapons\n"))+"\n\nWeapons\n"+weaponProficiency+original.substring(original.indexOf("\n\nOther\n"));
                client.gui.proficiencyTextPane.setText(changed);
                return;
        }
        //Throw other stuff into the other section
        if(otherProficiency.equals(""))otherProficiency+=skill;
        else otherProficiency+=", "+skill;
        String changed = original.substring(0,original.indexOf("\n\nOther\n"))+"\n\nOther\n"+otherProficiency+original.substring(original.indexOf("\n\nLanguages\n"));
        client.gui.proficiencyTextPane.setText(changed);
    }

    public String[] skimKnownSkills(String[] skills) {
        ArrayList<String> temp = new ArrayList<>();
        outer:
        for(String name:skills){
            for(Skill s:this.skills){
                if(name.equals(s.name) && s.proficient)continue outer;
            }
            for(Save s:saves){
                if(name.equals(s.name) && s.proficient)continue outer;
            }
            if(client.gui.proficiencyTextPane.getText().contains(name))continue;
            temp.add(name);
        }
        return temp.toArray(new String[0]);
    }
    public String[] skimLanguages(){
        ArrayList<String> out = new ArrayList<>();
        for(String s:listOfLanguages)if(!languages.contains(s))out.add(s);
        return out.toArray(new String[0]);
    }

    public void chooseFromFolder(String folder,String options){
        try {
            File[] files = new File(client.filePath+"Files\\"+folder+"\\").listFiles();
            ArrayList<String> names = new ArrayList<>();
            if(options.length()==0)for (int i = 0; i < files.length; i++){
                if(!chooseFolderChosen.contains("|"+files[i].getName().substring(0,files[i].getName().indexOf(".txt"))+"|"))names.add(files[i].getName().substring(0,files[i].getName().indexOf(".txt")));
            }
            else{
                for (int i = 0; i < files.length; i++){
                    String name=files[i].getName().substring(0,files[i].getName().indexOf(".txt"));
                    if(options.contains(name) && !chooseFolderChosen.contains("|"+name+"|"))names.add(name);
                }
            }
            JComboBox<String> list = new JComboBox<>(names.toArray(new String[0]));
            ConfirmPopup cp;
            do{
                JOptionPane.showMessageDialog(null, list, folder+" Selection", JOptionPane.PLAIN_MESSAGE);
                cp = new ConfirmPopup(client.filePath+"Files\\"+folder+"\\"+list.getSelectedItem()+".txt");
            }while (!cp.choice);
            BufferedReader in = new BufferedReader(new FileReader(client.filePath+"Files\\"+folder+"\\"+list.getSelectedItem()+".txt"));
            chooseFolderChosen+="|"+list.getSelectedItem()+"|";
            String s=in.readLine();
            while (s!=null){
                parseFeature(s,folder,totalLevel(),in);
                s=in.readLine();
            }
        }catch (Exception e){System.out.println("Error selecting "+folder+":\n"+e);}
    }

    public void chooseFeat(){
        try {
            File[] files = new File(client.filePath+"Files\\Feats\\").listFiles();
            String[] names = new String[files.length];
            for (int i = 0; i < names.length; i++) names[i] = files[i].getName().substring(0,files[i].getName().indexOf(".txt"));
            JComboBox<String> list = new JComboBox<>(names);
            ConfirmPopup cp;
            do{
                JOptionPane.showMessageDialog(null, list, "Feat Selection", JOptionPane.PLAIN_MESSAGE);
                cp = new ConfirmPopup(client.filePath+"Files\\Feats\\"+list.getSelectedItem()+".txt");
            }while (!cp.choice);
            BufferedReader in = new BufferedReader(new FileReader(client.filePath+"Files\\Feats\\"+list.getSelectedItem()+".txt"));
            String s=in.readLine();
            while (s!=null){
                parseFeature(s,"Feat",totalLevel(),in);
                s=in.readLine();
            }
        }catch (Exception e){System.out.println("Error selecting feat:\n"+e);}

    }

    public void shortRest(){
        for(Ability a:abilities){
            if(a.recharge.getText().equals("Short Rest"))a.reset();
        }
        client.gui.pactMagicGUI.reset();
    }

    public void longRest(){
        client.gui.currentHP.setText(client.gui.maxHpLabel.getText());
        client.gui.tempHP.setText("0");

        for(Ability a:abilities){
            if(a.recharge.getText().equals("Long Rest"))a.reset();
        }
        client.gui.spellSlotGUI.reset();

        int newHdUsed=Integer.parseInt(client.gui.usedHdTextField.getText())-Integer.parseInt(client.gui.totalHdLabel.getText())/2;
        if(newHdUsed<0)client.gui.usedHdTextField.setText("0");
        else client.gui.usedHdTextField.setText(""+newHdUsed);

    }
}
