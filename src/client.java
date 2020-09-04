import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;

//TODO: change eldritch invocations and Pact Boons to FOLDER_SELECTION style, add requirement information to confirm popups, AT for amount of cantrips for each class
public class client {
    static GUI gui;
    static Character pc;
    static ArrayList<SpellInfo> allSpells = new ArrayList<>(460);
    static String[] spellFilters = {"","",""};  //{Schools,classes,levels}
    static String spellNameSearch="",filePath="";
    static JFrame frame;
    static BattleMapClient battleMapClient;
    static BattleMap battleMap = new BattleMap();

    public static void main(String[] Args){
        new client();
    }

    public client(){

        try {
            String filePath = new File(client.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            client.filePath=filePath.substring(0,filePath.lastIndexOf("\\")+1);
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error getting filePath\n"+e);
        }

        frame = new JFrame("Character Sheet");
        gui = new GUI();
        pc = new Character();
        frame.setContentPane(gui.pane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int res=JOptionPane.showConfirmDialog(null,"Would you like to save before exiting?");

                if(res==JOptionPane.YES_OPTION){
                    JFileChooser fc = new JFileChooser();
                    fc.setCurrentDirectory(new File(filePath+"Files\\Characters"));
                    fc.showSaveDialog(null);
                    if(fc.getSelectedFile()!=null && fc.getSelectedFile().exists())save(fc.getSelectedFile().getPath());
                }
                super.windowClosing(e);
            }
        });


        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        menuBar.setVisible(true);

        JMenu character = new JMenu("Character");
        menuBar.add(character);

        JMenuItem levelUp = new JMenuItem("Level Up");
        levelUp.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pc.levelUp();
            }
        });
        character.add(levelUp);

        JMenu file = new JMenu("File");
        menuBar.add(file);
        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save(filePath+"Files\\Characters\\Autosave_"+gui.nameField.getText()+".txt");
                JFileChooser fc = new JFileChooser(filePath+"Files\\Characters");
                if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
                File f = fc.getSelectedFile();
                if (f == null || !f.exists()) return;
                load(f.getAbsolutePath());
            }
        });
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(filePath+"Files\\Characters"));
                fc.showSaveDialog(null);
                if(fc.getSelectedFile()!=null && fc.getSelectedFile().exists())save(fc.getSelectedFile().getPath());
            }
        });
        JMenuItem newCharacter = new JMenuItem("New Character");
        newCharacter.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save(filePath+"Files\\Characters\\Autosave_"+gui.nameField.getText()+".txt");
                newCharacter();
            }
        });
        file.add(load);
        file.add(save);
        file.add(newCharacter);

        frame.setVisible(true);
        prepareAllSpells();
        updateGUI();
    }

    public static void roll(String command,int[] dice,int bonus){
        try{
            bonus+=Integer.parseInt(gui.rollModTextField.getText());
        }catch (Exception e){}
        gui.rollModTextField.setText("0");
        if(gui.plus2Checkbox.isSelected()){
            gui.plus2Checkbox.setSelected(false);
            bonus+=2;
        }
        if(gui.plus5Checkbox.isSelected()){
            gui.plus5Checkbox.setSelected(false);
            bonus+=5;
        }
        if(gui.minus2Checkbox.isSelected()){
            gui.minus2Checkbox.setSelected(false);
            bonus-=2;
        }
        if(gui.minus5Checkbox.isSelected()){
            gui.minus5Checkbox.setSelected(false);
            bonus-=5;
        }

        if(gui.advCheckBox.isSelected() || gui.disadvCheckBox.isSelected()){
            StringBuilder roll1= new StringBuilder(),roll2 = new StringBuilder();
            int total1=bonus,total2=bonus;

            //d4s
            for(int i=0;i< dice[0];i++){
                int r=(int)(1+Math.random()*4),r2=(int)(1+Math.random()*4);
                total1+=r;
                total2+=r2;
                roll1.append(r).append(" + ");
                roll2.append(r2).append(" + ");
            }
            roll1.append(":");
            roll2.append(":");

            //d6s
            for(int i=0;i< dice[1];i++){
                int r=(int)(1+Math.random()*6),r2=(int)(1+Math.random()*6);
                total1+=r;
                total2+=r2;
                roll1.append(r).append(" + ");
                roll2.append(r2).append(" + ");
            }
            roll1.append(":");
            roll2.append(":");

            //d8s
            for(int i=0;i< dice[2];i++){
                int r=(int)(1+Math.random()*8),r2=(int)(1+Math.random()*8);
                total1+=r;
                total2+=r2;
                roll1.append(r).append(" + ");
                roll2.append(r2).append(" + ");
            }
            roll1.append(":");
            roll2.append(":");

            //d10s
            for(int i=0;i< dice[3];i++){
                int r=(int)(1+Math.random()*10),r2=(int)(1+Math.random()*10);
                total1+=r;
                total2+=r2;
                roll1.append(r).append(" + ");
                roll2.append(r2).append(" + ");;
            }
            roll1.append(":");
            roll2.append(":");

            //d12s
            for(int i=0;i< dice[4];i++){
                int r=(int)(1+Math.random()*12),r2=(int)(1+Math.random()*12);
                total1+=r;
                total2+=r2;
                roll1.append(r).append(" + ");
                roll2.append(r2).append(" + ");
            }
            roll1.append(":");
            roll2.append(":");

            //d20s
            for(int i=0;i< dice[5];i++){
                int r=(int)(1+Math.random()*20),r2=(int)(1+Math.random()*20);
                total1+=r;
                total2+=r2;
                roll1.append(r).append(" + ");
                roll2.append(r2).append(" + ");
            }
            roll1.append(":").append(bonus).append(" = ").append(total1).append(" (Dropped ").append(total2).append(")");
            roll2.append(":").append(bonus).append(" = ").append(total2).append(" (Dropped ").append(total1).append(")");
            if(BattleMapClient.serverOut!=null) {
                if (gui.advCheckBox.isSelected()) {
                    if (total1 > total2) {
                        BattleMapClient.serverOut.println("ROLL:" + BattleMapClient.secretRolling + ":" + command + ":" + roll1);
                        if(command.equals("INIT"))BattleMapClient.serverOut.println("INITIATIVE:"+total1+":"+client.gui.nameField.getText()+":"+client.gui.maxHpLabel.getText()+":"+client.gui.currentHP.getText()+":"+client.gui.tempHP.getText());
                    } else {
                        BattleMapClient.serverOut.println("ROLL:" + BattleMapClient.secretRolling + ":" + command + ":" + roll2);
                        if(command.equals("INIT"))BattleMapClient.serverOut.println("INITIATIVE:"+total2+":"+client.gui.nameField.getText()+":"+client.gui.maxHpLabel.getText()+":"+client.gui.currentHP.getText()+":"+client.gui.tempHP.getText());
                    }
                    gui.advCheckBox.setSelected(false);
                } else {
                    if (total1 < total2) {
                        BattleMapClient.serverOut.println("ROLL:" + BattleMapClient.secretRolling + ":" + command + ":" + roll1);
                        if(command.equals("INIT"))BattleMapClient.serverOut.println("INITIATIVE:"+total1+":"+client.gui.nameField.getText()+":"+client.gui.maxHpLabel.getText()+":"+client.gui.currentHP.getText()+":"+client.gui.tempHP.getText());
                    } else {
                        BattleMapClient.serverOut.println("ROLL:" + BattleMapClient.secretRolling + ":" + command + ":" + roll2);
                        if (command.equals("INIT")) BattleMapClient.serverOut.println("INITIATIVE:" + total2 + ":" + client.gui.nameField.getText() + ":" + client.gui.maxHpLabel.getText() + ":" + client.gui.currentHP.getText() + ":" + client.gui.tempHP.getText());
                    }
                    gui.disadvCheckBox.setSelected(false);
                }
            }else{
                if (gui.advCheckBox.isSelected()) {
                    if (total1 > total2) {
                        JOptionPane.showMessageDialog(null,command+": "+roll1.toString().replace(":",""));
                    } else {
                        JOptionPane.showMessageDialog(null,command+": "+roll2.toString().replace(":",""));
                    }
                    gui.advCheckBox.setSelected(false);
                } else {
                    if (total1 < total2) {
                        JOptionPane.showMessageDialog(null,command+": "+roll1.toString().replace(":",""));
                    } else {
                        JOptionPane.showMessageDialog(null,command+": "+roll2.toString().replace(":",""));
                    }
                    gui.disadvCheckBox.setSelected(false);
                }


            }
        }else{
            StringBuilder rolls= new StringBuilder();
            int total=bonus;

            //d4s
            for(int i=0;i< dice[0];i++){
                int r=(int)(1+Math.random()*4);
                total+=r;
                rolls.append(r).append(" + ");
            }
            rolls.append(":");

            //d6s
            for(int i=0;i< dice[1];i++){
                int r=(int)(1+Math.random()*6);
                total+=r;
                rolls.append(r).append(" + ");
            }
            rolls.append(":");

            //d8s
            for(int i=0;i< dice[2];i++){
                int r=(int)(1+Math.random()*8);
                total+=r;
                rolls.append(r).append(" + ");
            }
            rolls.append(":");

            //d10s
            for(int i=0;i< dice[3];i++){
                int r=(int)(1+Math.random()*10);
                total+=r;
                rolls.append(r).append(" + ");
            }
            rolls.append(":");

            //d12s
            for(int i=0;i< dice[4];i++){
                int r=(int)(1+Math.random()*12);
                total+=r;
                rolls.append(r).append(" + ");
            }
            rolls.append(":");

            //d20s
            for(int i=0;i< dice[5];i++){
                int r=(int)(1+Math.random()*20);
                total+=r;
                rolls.append(r).append(" + ");
            }
            rolls.append(":").append(bonus).append(" = ").append(total);
            if(BattleMapClient.serverOut!=null) {
                BattleMapClient.serverOut.println("ROLL:" + BattleMapClient.secretRolling + ":" + command + ":" + rolls);
                if(command.equals("INIT"))BattleMapClient.serverOut.println("INITIATIVE:"+total+":"+client.gui.nameField.getText()+":"+client.gui.maxHpLabel.getText()+":"+client.gui.currentHP.getText()+":"+client.gui.tempHP.getText());
            }else{
                JOptionPane.showMessageDialog(null,command+": "+rolls.toString().replace(":",""));

            }
        }
    }

    public void prepareAllSpells()  {
        try {
            File dir = new File(filePath+"Files\\Spells");
            File[] files = dir.listFiles();
            for (File f : files) {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String name=in.readLine();
                int lvl =Integer.parseInt(in.readLine());
                String castTime=in.readLine();
                String duration=in.readLine();
                String range=in.readLine();
                String components=in.readLine();
                String description=client.convertFromGrave(in.readLine());
                String tags= in.readLine();
                String code=in.readLine();
                SpellInfo temp=new SpellInfo(name,lvl,castTime,duration,range,components,description,tags,code);
                int i=0;
                for(i=0;i<allSpells.size();i++){
                    if(temp.compareTo(allSpells.get(i))<0)break;
                }
                allSpells.add(i,temp);
            }
        }catch (Exception e){System.out.println("Error preparing all spells:\n"+e);}

    }

    public static void updateGUI(){
        //set stats

        pc.updateModifiers();
        gui.strScoreLabel.setText(""+pc.strScore);
        gui.dexScoreLabel.setText(""+pc.dexScore);
        gui.conScoreLabel.setText(""+pc.conScore);
        gui.intScoreLabel.setText(""+pc.intScore);
        gui.wisScoreLabel.setText(""+pc.wisScore);
        gui.chaScoreLabel.setText(""+pc.chaScore);
        gui.strModLabel.setText(""+pc.STR);
        gui.dexModLabel.setText(""+pc.DEX);
        gui.conModLabel.setText(""+pc.CON);
        gui.intModLabel.setText(""+pc.INT);
        gui.wisModLabel.setText(""+pc.WIS);
        gui.chaModLabel.setText(""+pc.CHA);
        gui.proficiencyLabel.setText(""+pc.proficiency);
        gui.initiativeLabel.setText(Integer.toString(pc.DEX+pc.initBonus));

        //set senses
        gui.passivePerceptionLabel.setText("Passive Perception: "+(10+pc.passivePerceptionBonus+pc.skills[11].calculateTotal()));
        gui.passiveInsightLabel.setText("Passive Insight: "+(10+pc.passiveInsightBonus+pc.skills[6].calculateTotal()));
        gui.passiveInvestigationLabel.setText("Passive Investigation: "+(10+pc.passiveInvestigationBonus+pc.skills[6].calculateTotal()));
        gui.sensesLabel.setText(pc.additionalSenses);
        //set defenses
        gui.defenseTextPane.setText(pc.defenses);

        //set skill and save labels
        for (Skill s:pc.skills)s.updateLabel();
        for(Save s:pc.saves)s.updateLabel();
        //setup feature pane
        gui.featurePane.removeAll();
        for(Feature f:pc.features)gui.featurePane.add(f.pane);

        gui.abilityPane.removeAll();
        for(Ability a:pc.abilities)gui.abilityPane.add(a.pane);
        for(AbilityText a:pc.abilityTexts)gui.abilityPane.add(a.pane);

        gui.allSpellPane.removeAll();

        for(SpellInfo s: allSpells){
            if (!spellFilter(s)) continue;
            gui.allSpellPane.add(s.knownSpell.pane);
        }

        gui.spellPane.removeAll();
        for(SpellInfo s:pc.preparedSpells){
            if (!spellFilter(s)) continue;
            gui.spellPane.add(s.preparedSpell.pane);

        }

        for(SpellInfo s:pc.yourSpells){
            if (!spellFilter(s)) continue;
            gui.spellPane.add(s.preparedSpell.pane);
        }

        //Setup Actions
        gui.weaponActionPane.removeAll();
        for(ActionPane ap:pc.weaponActions)gui.weaponActionPane.add(ap.pane);

        gui.spellActionPane.removeAll();
        for(ActionPane ap:pc.spellActions)gui.spellActionPane.add(ap.pane);
        gui.spellSlotGUI.setupSpellSlots();
        gui.pactMagicGUI.setupPactMagic();

        gui.otherActionPane.removeAll();
        for(ActionPane ap:pc.otherActions)gui.otherActionPane.add(ap.pane);

        //Setup inventory

        gui.invenPane.removeAll();
        double weight=0;
        for(Item it:pc.items){
            JButton button = new JButton(it.name);
            button.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ItemPopup(it);

                }
            });
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            gui.invenPane.add(button);
            weight+=it.weight;
        }
        gui.currentWeightLabel.setText(""+weight);
        gui.maxWeightLabel.setText("/ "+(15*pc.strScore));


        gui.pane.revalidate();
        gui.pane.repaint();


    }

    /**
     * Returns true if the spell fits the filters, false otherwise
     * @param s the spell to check
     * @return whether the spell fits the filters
     */
    private static boolean spellFilter(SpellInfo s) {
        if(!s.name.toLowerCase().contains(spellNameSearch.toLowerCase()))return false;

        filterLoop:
        for (String spellFilter : spellFilters) {
            if(spellFilter.equals(""))continue;
            String filter = spellFilter.toLowerCase();
            while (filter.contains(" ")) {
                String f = filter.substring(0, filter.indexOf(" "));
                if (s.tags.toLowerCase().contains(f)) continue filterLoop;
                filter = filter.substring(filter.indexOf(" ") + 1);
            }
            return false;
        }
        return true;
    }

    public void load(String path){
        try {

            BufferedReader in = new BufferedReader(new FileReader(path));

            gui.nameField.setText(in.readLine());
            gui.classLabel.setText(in.readLine());
            gui.raceLabel.setText(in.readLine());
            gui.backgroundLabel.setText(in.readLine());
            gui.acField.setText(in.readLine());
            gui.speedLabel.setText(in.readLine());
            gui.maxHpLabel.setText(in.readLine());
            gui.currentHP.setText(in.readLine());
            gui.tempHP.setText(in.readLine());
            gui.totalHdLabel.setText(in.readLine());
            gui.usedHdTextField.setText(in.readLine());
            gui.cpTextField.setText(in.readLine());
            gui.spTextField.setText(in.readLine());
            gui.epTextField.setText(in.readLine());
            gui.gpTextField.setText(in.readLine());
            gui.ppTextField.setText(in.readLine());
            pc.strScore=Integer.parseInt(in.readLine());
            pc.dexScore=Integer.parseInt(in.readLine());
            pc.conScore=Integer.parseInt(in.readLine());
            pc.intScore=Integer.parseInt(in.readLine());
            pc.wisScore=Integer.parseInt(in.readLine());
            pc.chaScore=Integer.parseInt(in.readLine());
            pc.proficiency=Integer.parseInt(in.readLine());
            pc.defenses=convertFromGrave(in.readLine());
            pc.additionalSenses=in.readLine();
            pc.armorProficiency=in.readLine();
            pc.weaponProficiency=in.readLine();
            pc.otherProficiency=in.readLine();
            pc.languages=in.readLine();
            pc.passivePerceptionBonus=Integer.parseInt(in.readLine());
            pc.passiveInsightBonus=Integer.parseInt(in.readLine());
            pc.passiveInvestigationBonus=Integer.parseInt(in.readLine());
            pc.initBonus=Integer.parseInt(in.readLine());
            pc.casterLevel=Integer.parseInt(in.readLine());
            pc.jackOfAllTrades=Boolean.parseBoolean(in.readLine());

            //Classes and subclasses
            for(int i=0;i<pc.levels.length;i++)pc.levels[i]=Integer.parseInt(in.readLine());
            String s=in.readLine();
            pc.subclasses = new ArrayList<>();
            while (!s.equals("00")){
                pc.subclasses.add(s);
                s=in.readLine();
            }

            //Skills and Saves
            for(Skill skill:pc.skills){
                skill.label.setIcon(new ImageIcon(filePath+"Files\\Resources\\unProficient.png"));
                skill.proficient=false;
                skill.expertise=false;
                s=in.readLine();
                if(Boolean.parseBoolean(s.substring(0,s.indexOf("|"))))skill.setProficient();
                s=s.substring(s.indexOf("|")+1);
                if(Boolean.parseBoolean(s.substring(0,s.indexOf("|"))))skill.setExpertise();
                s=s.substring(s.indexOf("|")+1);
                skill.misc=Integer.parseInt(s.substring(0,s.indexOf("|")));
                s=s.substring(s.indexOf("|")+1);
                skill.override=Integer.parseInt(s);
            }
            for(Save save:pc.saves){
                save.label.setIcon(new ImageIcon(filePath+"Files\\Resources\\unProficient.png"));
                save.proficient=false;
                s=in.readLine();
                if(Boolean.parseBoolean(s.substring(0,s.indexOf("|"))))save.setProficient();
                s=s.substring(s.indexOf("|")+1);
                save.misc=Integer.parseInt(s.substring(0,s.indexOf("|")));
                s=s.substring(s.indexOf("|")+1);
                save.override=Integer.parseInt(s);
            }

            s=in.readLine();
            pc.abilities = new ArrayList<>();
            while (!s.equals("00")){
                String title=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                int max=Integer.parseInt(s.substring(0,s.indexOf("|")));
                s=s.substring(s.indexOf("|")+1);
                String recharge=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                int priority = Integer.parseInt(s);
                Ability tempAbility = new Ability(title,max,recharge,priority);
                int i;
                for(i=0;i<pc.abilities.size();i++){
                    if(tempAbility.compareTo(pc.abilities.get(i))>0){
                        break ;
                    }
                }
                pc.abilities.add(i,tempAbility);
                s=in.readLine();
            }

            s=in.readLine();
            pc.abilityTexts = new ArrayList<>();
            while (!s.equals("00")){
                String title=s.substring(0,s.indexOf("|"));
                s=s.substring(0,s.indexOf("|"));
                String description=s.substring(0,s.indexOf("|"));
                s=s.substring(0,s.indexOf("|"));
                int priority=Integer.parseInt(s);
                AbilityText tempAbilityText = new AbilityText(title,description,priority);
                int i;
                for(i=0;i<pc.abilityTexts.size();i++){
                    if(tempAbilityText.compareTo(pc.abilityTexts.get(i))>0){
                        break ;
                    }
                }
                pc.abilityTexts.add(i,tempAbilityText);
                s=in.readLine();
            }

            s=in.readLine();
            pc.features=new ArrayList<>();
            while (!s.equals("00")){
                String title=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                String description = convertFromGrave(s.substring(0,s.indexOf("|")));
                s=s.substring(s.indexOf("|")+1);
                String source = s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                int lvl=Integer.parseInt(s);
                pc.features.add(new Feature(title,description,source,lvl));
                s=in.readLine();
            }
            s=in.readLine();
            pc.laterFeatures = new ArrayList<>();
            while (!s.equals("00")){
                pc.laterFeatures.add(s);
                s=in.readLine();
            }

            //Reset all spells to unprepared, and unknown
            for(SpellInfo spell:allSpells){
                if(spell.preparedSpell.checkBox.isSelected())spell.preparedSpell.checkBox.doClick();
                if(spell.knownSpell.checkBox.isSelected())spell.knownSpell.checkBox.doClick();
            }
            //Prepared spells and Known spells
            s=in.readLine();
            pc.preparedSpells= new ArrayList<>();
            while (!s.equals("00")){
                SpellInfo spell = allSpells.get(Integer.parseInt(s));
                spell.knownSpell.checkBox.doClick();
                spell.preparedSpell.checkBox.doClick();
                s=in.readLine();
            }

            s=in.readLine();
            pc.yourSpells = new ArrayList<>();
            while (!s.equals("00")){
                SpellInfo spell = allSpells.get(Integer.parseInt(s));
                spell.knownSpell.checkBox.doClick();
                s=in.readLine();
            }

            //Actions

            s=in.readLine();
            pc.weaponActions = new ArrayList<>();
            while (!s.equals("00")){
                String name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                String properties =s.substring(0,s.indexOf("|"));
                String code=s.substring(s.indexOf("|")+1);
                pc.weaponActions.add(new ActionPane(name,properties,code));
                s=in.readLine();
            }

            //Must load spell actions after spells so the actions added actions are removed and not duplicated
            s=in.readLine();
            pc.spellActions = new ArrayList<>();
            while (!s.equals("00")){
                String name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                String properties =s.substring(0,s.indexOf("|"));
                String code=s.substring(s.indexOf("|")+1);
                pc.spellActions.add(new ActionPane(name,properties,code));
                s=in.readLine();
            }

            s=in.readLine();
            pc.otherActions = new ArrayList<>();
            while (!s.equals("00")){
                String name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                String properties =s.substring(0,s.indexOf("|"));
                String code=s.substring(s.indexOf("|")+1);
                pc.otherActions.add(new ActionPane(name,properties,code));
                s=in.readLine();
            }

            s=in.readLine();
            pc.items = new ArrayList<>();
            while (!s.equals("00")){
                Item it = new Item();
                it.name=s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                it.description = s.substring(0,s.indexOf("|"));
                s=s.substring(0,s.indexOf("|"));
                it.value = s.substring(0,s.indexOf("|"));
                s=s.substring(s.indexOf("|")+1);
                it.weight = Double.parseDouble(s);
                pc.items.add(it);
                s=in.readLine();
            }


        } catch (Exception e){}
        updateGUI();

    }

    public void save(String path){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));

            //Stats and basic character info
            out.write(gui.nameField.getText()+"\r\n");
            out.write(gui.classLabel.getText()+"\r\n");
            out.write(gui.raceLabel.getText()+"\r\n");
            out.write(gui.backgroundLabel.getText()+"\r\n");
            out.write(gui.acField.getText()+"\r\n");
            out.write(gui.speedLabel.getText()+"\r\n");
            out.write(gui.maxHpLabel.getText()+"\r\n");
            out.write(gui.currentHP.getText()+"\r\n");
            out.write(gui.tempHP.getText()+"\r\n");
            out.write(gui.totalHdLabel.getText()+"\r\n");
            out.write(gui.usedHdTextField.getText()+"\r\n");
            out.write(gui.cpTextField.getText()+"\r\n");
            out.write(gui.spTextField.getText()+"\r\n");
            out.write(gui.epTextField.getText()+"\r\n");
            out.write(gui.gpTextField.getText()+"\r\n");
            out.write(gui.ppTextField.getText()+"\r\n");
            out.write(pc.strScore+"\r\n");
            out.write(pc.dexScore+"\r\n");
            out.write(pc.conScore+"\r\n");
            out.write(pc.intScore+"\r\n");
            out.write(pc.wisScore+"\r\n");
            out.write(pc.chaScore+"\r\n");
            out.write(pc.proficiency+"\r\n");
            out.write(convertToGrave(pc.defenses)+"\r\n");
            out.write(pc.additionalSenses+"\r\n");
            out.write(pc.armorProficiency+"\r\n");
            out.write(pc.weaponProficiency+"\r\n");
            out.write(pc.otherProficiency+"\r\n");
            out.write(pc.languages+"\r\n");
            out.write(pc.passivePerceptionBonus+"\r\n");
            out.write(pc.passiveInsightBonus+"\r\n");
            out.write(pc.passiveInvestigationBonus+"\r\n");
            out.write(pc.initBonus+"\r\n");
            out.write(pc.casterLevel+"\r\n");
            out.write(pc.jackOfAllTrades+"\r\n");

            //Classes and subclasses
            for(int i:pc.levels)out.write(i+"\r\n");
            for(String s:pc.subclasses)out.write(s+"\r\n");
            out.write("00\r\n");


            //Skills and saves
            for(Skill s:pc.skills)out.write(s.proficient+"|"+s.expertise+"|"+s.misc+"|"+s.override+"\r\n");

            for(Save s:pc.saves)out.write(s.proficient+"|"+s.misc+"|"+s.override+"\r\n");


            //Abilities and ability Texts
            for(Ability a:pc.abilities)out.write(a.name.getText()+"|"+a.originalMax+"|"+a.recharge.getText()+"|"+a.priority+"\r\n");
            out.write("00\r\n");
            for(AbilityText at:pc.abilityTexts)out.write(at.title+"|"+at.label.getText()+"|"+at.priority+"\r\n");
            out.write("00\r\n");


            //Features
            for(Feature f: pc.features)out.write(f.title+"|"+convertToGrave(f.description)+"|"+f.source+"|"+f.lvl+"\r\n");
            out.write("00\r\n");

            //Later Features
            for(String s:pc.laterFeatures)out.write(s+"\r\n");
            out.write("00\r\n");

            //Spells
            for(SpellInfo spell: pc.preparedSpells)out.write(allSpells.indexOf(spell)+"\r\n");
            out.write("00\r\n");
            for(SpellInfo spell: pc.yourSpells)out.write(allSpells.indexOf(spell)+"\r\n");
            out.write("00\r\n");

            //Actions

            for(ActionPane ap:pc.weaponActions){
                StringBuilder code= new StringBuilder(ap.mainCode + ";");
                for(String c:ap.codes) code.append(c).append(";");
                out.write(ap.name+"|"+ap.properties+"|"+code+"\r\n");
            }
            out.write("00\r\n");

            for(ActionPane ap:pc.spellActions){
                StringBuilder code= new StringBuilder(ap.mainCode + ";");
                for(String c:ap.codes) code.append(c).append(";");
                out.write(ap.name+"|"+ap.properties+"|"+code+"\r\n");
            }
            out.write("00\r\n");

            for(ActionPane ap:pc.otherActions){
                StringBuilder code= new StringBuilder(ap.mainCode + ";");
                for(String c:ap.codes) code.append(c).append(";");
                out.write(ap.name+"|"+ap.properties+"|"+code+"\r\n");
            }
            out.write("00\r\n");

            //Items

            for(Item it: pc.items){
                out.write(it.name+"|"+it.description+"|"+it.value+"|"+it.weight);
            }
            out.write("00\r\n");


            out.close();


        }catch (Exception e){System.out.println("Error while saving:\n"+e);}

    }

    public void newCharacter(){
        load(filePath+"Files\\Resources\\blank.txt");
        pc.selectRace();
        pc.setStats();
        pc.selectBackground();
        pc.levelUp();
    }
    public static String convertFromGrave(String s){
        StringBuilder out= new StringBuilder();
        while (s.contains("~")){
            out.append(s, 0, s.indexOf("~")).append("\n");
            s=s.substring(s.indexOf("~")+1);
        }
        out.append(s);
        return out.toString();
    }

    public static String convertToGrave(String s){
        StringBuilder out= new StringBuilder();
        while (s.contains("\n")){
            out.append(s, 0, s.indexOf("\n")).append("~");
            s=s.substring(s.indexOf("\n")+1);
        }
        out.append(s);
        return out.toString();
    }
}
