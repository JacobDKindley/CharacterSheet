import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI {
    JTextField nameField;
    private JButton shortRestButton;
    private JButton longRestButton;
    JLabel classLabel;
    JLabel raceLabel;
    JLabel strScoreLabel;
    JLabel dexScoreLabel;
    JLabel conScoreLabel;
    JLabel intScoreLabel;
    JLabel wisScoreLabel;
    JLabel chaScoreLabel;
    JPanel pane;
    JTextField acField;
    JLabel initiativeLabel;
    JLabel speedLabel;
    JLabel maxHpLabel;
    JTextField currentHP;
    JTextField tempHP;
    JLabel totalHdLabel;
    JTextField usedHdTextField;
    JTextPane defenseTextPane;
    JTextPane chatTextPane;
    JTextArea chatInput;
    private JButton allSpellSearchButton;
    private JButton spellKnownSearchButton;
    private JButton d10Button;
    private JButton d12Button;
    private JButton d20Button;
    private JButton d4Button;
    private JButton d6Button;
    private JButton d8Button;
    JCheckBox plus5Checkbox;
    JCheckBox minus5Checkbox;
    JCheckBox plus2Checkbox;
    JCheckBox minus2Checkbox;
    JCheckBox advCheckBox;
    JCheckBox disadvCheckBox;
    JTextField rollModTextField;
    JLabel passivePerceptionLabel;
    JLabel passiveInsightLabel;
    JLabel passiveInvestigationLabel;
    JLabel sensesLabel;
    JTextPane proficiencyTextPane;
    private Roller deathSaveRoller;
    private Roller strCheckRoller;
    JLabel strModLabel;
    private Roller dexCheckRoller;
    private Roller conCheckRoller;
    private Roller intCheckRoller;
    private Roller wisCheckRoller;
    private Roller chaCheckRoller;
    JLabel dexModLabel;
    JLabel conModLabel;
    JLabel intModLabel;
    JLabel wisModLabel;
    JLabel chaModLabel;
    private Roller initiativeRoller;
    JPanel featurePane;
    Skill acrobatics;
    Skill animalHandling;
    Skill arcana;
    Skill athletics;
    Skill deception;
    Skill history;
    Skill insight;
    Skill intimidation;
    Skill investigation;
    Skill medicine;
    Skill nature;
    Skill perception;
    Skill performance;
    Skill persuasion;
    Skill religion;
    Skill sleightOfHand;
    Skill stealth;
    Skill survival;
    Save strSave;
    Save dexSave;
    Save conSave;
    Save intSave;
    Save wisSave;
    Save chaSave;
    JPanel abilityPane;
    JPanel allSpellPane;
    JPanel spellPane;
    JLabel proficiencyLabel;
    JPanel weaponActionPane;
    JPanel spellActionPane;
    JPanel otherActionPane;
    JButton addWeaponAction;
    JButton addSpellAction;
    JButton addOtherAction;
    private JButton itemAddButton;
    private JButton itemRemoveButton;
    JPanel invenPane;
    JLabel maxWeightLabel;
    JLabel currentWeightLabel;
    private JButton removeWeaponActionButton;
    private JButton removeSpellActionButton;
    private JButton removeOtherActionButton;
    SpellSlotGUI spellSlotGUI;
    JLabel backgroundLabel;
    JTextField gpTextField;
    JTextField epTextField;
    JTextField spTextField;
    JTextField cpTextField;
    JTextField ppTextField;
    PactMagicGUI pactMagicGUI;
    JScrollPane battleMapScroller;
    private JButton connectToServerButton;
    private JButton hostServerButton;
    JPanel connectionPane;
    private JLabel d10Amount;
    private JLabel d12Amount;
    private JLabel d20Amount;
    private JLabel d8Amount;
    private JLabel d6Amount;
    private JLabel d4Amount;
    JPanel combatTracker;
    JPanel mapPane;
    private JTabbedPane tabbedPane1;
    private JPanel statPane;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JPanel featureContainer;
    private JPanel actionContainer;
    private JPanel actionPane;
    private JPanel inventoryContainer;
    private JPanel spellContainer;
    private JPanel allSpellContainer;

    DocumentListener doc = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            update();
        }
    };

    boolean rollStarted=false;
    long time;

    AbstractAction spellSearch = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog frame = new JDialog();
            SpellSearchPopup ssp = new SpellSearchPopup();
            frame.setContentPane(ssp.pane);
            frame.pack();
            frame.setSize(new Dimension(600,800));
            frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            ssp.searchButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    client.spellFilters=ssp.getFilters();
                    client.spellNameSearch=ssp.nameField.getText();
                    client.updateGUI();
                    frame.dispose();
                }
            });

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    client.spellFilters=ssp.getFilters();
                    client.spellNameSearch=ssp.nameField.getText();
                    client.updateGUI();
                    super.windowClosing(e);
                }
            });

            frame.setVisible(true);
        }
    };

    public GUI() {
        advCheckBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(disadvCheckBox.isSelected())disadvCheckBox.setSelected(false);
            }
        });
        disadvCheckBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(advCheckBox.isSelected())advCheckBox.setSelected(false);
            }
        });
        allSpellSearchButton.addActionListener(spellSearch);
        spellKnownSearchButton.addActionListener(spellSearch);
        itemAddButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(client.filePath+"Files\\Items\\");
                if(fc.showOpenDialog(null)!=JFileChooser.APPROVE_OPTION)return;
                if(fc.getSelectedFile().exists())client.pc.parseItem(fc.getSelectedFile());
            }
        });
        itemRemoveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] names= new String[client.pc.items.size()];
                for(int i=0;i< names.length;i++){
                    names[i]=client.pc.items.get(i).name;
                }
                JComboBox<String> list = new JComboBox<>(names);
                JOptionPane.showMessageDialog(null, list, "Remove Item", JOptionPane.PLAIN_MESSAGE);
                if(list.getSelectedIndex()==-1)return;
                client.pc.items.remove(list.getSelectedIndex());
                client.updateGUI();
            }
        });

        addWeaponAction.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.pc.weaponActions.add(new ActionPane());
                client.updateGUI();
            }
        });

        addSpellAction.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.pc.spellActions.add(new ActionPane());
                client.updateGUI();
            }
        });

        addOtherAction.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.pc.otherActions.add(new ActionPane());
                client.updateGUI();
            }
        });

        removeWeaponActionButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] names= new String[client.pc.weaponActions.size()];
                for(int i=0;i< names.length;i++){
                    names[i]=client.pc.weaponActions.get(i).name;
                }
                JComboBox<String> list = new JComboBox<>(names);
                JOptionPane.showMessageDialog(null, list, "Remove Weapon Action", JOptionPane.PLAIN_MESSAGE);
                if(list.getSelectedIndex()==-1)return;
                client.pc.weaponActions.remove(list.getSelectedIndex());
                client.updateGUI();
            }
        });

        removeSpellActionButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] names= new String[client.pc.spellActions.size()];
                for(int i=0;i< names.length;i++){
                    names[i]=client.pc.spellActions.get(i).name;
                }
                JComboBox<String> list = new JComboBox<>(names);
                JOptionPane.showMessageDialog(null, list, "Remove Spell Action", JOptionPane.PLAIN_MESSAGE);
                if(list.getSelectedIndex()==-1)return;
                client.pc.spellActions.remove(list.getSelectedIndex());
                client.updateGUI();
            }
        });

        removeOtherActionButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] names= new String[client.pc.otherActions.size()];
                for(int i=0;i< names.length;i++){
                    names[i]=client.pc.otherActions.get(i).name;
                }
                JComboBox<String> list = new JComboBox<>(names);
                JOptionPane.showMessageDialog(null, list, "Remove Other Action", JOptionPane.PLAIN_MESSAGE);
                if(list.getSelectedIndex()==-1)return;
                client.pc.otherActions.remove(list.getSelectedIndex());
                client.updateGUI();
            }
        });

        shortRestButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.pc.shortRest();
            }
        });

        longRestButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.pc.longRest();
            }
        });

        connectToServerButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.battleMapClient = new BattleMapClient(false);
                Thread t1= new Thread(client.battleMapClient);
                t1.start();
                battleMapScroller.setViewportView(client.battleMap);
                client.gui.pane.repaint();
                client.gui.pane.revalidate();
            }
        });

        hostServerButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread serverThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BattleMapServer.initialize();
                    }
                });
                serverThread.start();
                client.battleMapClient = new BattleMapClient(true);
                Thread t1= new Thread(client.battleMapClient);
                t1.start();
                battleMapScroller.setViewportView(client.battleMap);
                client.gui.pane.repaint();
                client.gui.pane.revalidate();
            }
        });

        ((DefaultCaret)(chatTextPane.getCaret())).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        d20Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=Integer.parseInt(d20Amount.getText());
                d20Amount.setText(Integer.toString(i+1));
                if(!rollStarted){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                    t.start();
                }
                else time=System.currentTimeMillis();
            }
        });

        d12Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=Integer.parseInt(d12Amount.getText());
                d12Amount.setText(Integer.toString(i+1));
                if(!rollStarted){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                    t.start();
                }
                else time=System.currentTimeMillis();
            }
        });

        d10Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=Integer.parseInt(d10Amount.getText());
                d10Amount.setText(Integer.toString(i+1));
                if(!rollStarted){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                    t.start();
                }
                else time=System.currentTimeMillis();
            }
        });

        d8Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=Integer.parseInt(d8Amount.getText());
                d8Amount.setText(Integer.toString(i+1));
                if(!rollStarted){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                    t.start();
                }
                else time=System.currentTimeMillis();
            }
        });

        d6Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=Integer.parseInt(d6Amount.getText());
                d6Amount.setText(Integer.toString(i+1));
                if(!rollStarted){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                    t.start();
                }
                else time=System.currentTimeMillis();
            }
        });

        d4Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=Integer.parseInt(d4Amount.getText());
                d4Amount.setText(Integer.toString(i+1));
                if(!rollStarted){
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startTimer();
                        }
                    });
                    t.start();
                }
                else time=System.currentTimeMillis();
            }
        });

        currentHP.getDocument().addDocumentListener(doc);
        tempHP.getDocument().addDocumentListener(doc);


    }

    private void createUIComponents() {
        acrobatics = new Skill("Acrobatics","DEX");
        animalHandling = new Skill("Animal Handling","WIS");
        arcana = new Skill("Arcana","INT");
        athletics = new Skill("Athletics","STR");
        deception = new Skill("Deception","CHA");
        history = new Skill("History","INT");
        insight = new Skill("Insight","WIS");
        intimidation = new Skill("Intimidation","CHA");
        investigation = new Skill("Investigation","INT");
        medicine = new Skill("Medicine","WIS");
        nature = new Skill("Nature","INT");
        perception = new Skill("Perception","WIS");
        performance = new Skill("Performance","CHA");
        persuasion = new Skill("Persuasion","CHA");
        religion = new Skill("Religion","INT");
        sleightOfHand = new Skill("Sleight of Hand","DEX");
        stealth = new Skill("Stealth","DEX");
        survival = new Skill("Survival","WIS");
        strSave = new Save("STR Save","STR");
        dexSave = new Save("DEX Save","DEX");
        conSave = new Save("CON Save","CON");
        intSave = new Save("INT Save","INT");
        wisSave = new Save("WIS Save","WIS");
        chaSave = new Save("CHA Save","CHA");
        deathSaveRoller= new Roller("Death");
        strCheckRoller = new Roller("STR Check");
        dexCheckRoller = new Roller("DEX Check");
        conCheckRoller = new Roller("CON Check");
        intCheckRoller = new Roller("INT Check");
        wisCheckRoller = new Roller("WIS Check");
        chaCheckRoller = new Roller("CHA Check");
        initiativeRoller = new Roller("INIT");

        featurePane = new JPanel();
        featurePane.setLayout(new BoxLayout(featurePane,BoxLayout.Y_AXIS));

        abilityPane = new JPanel();
        abilityPane.setLayout(new BoxLayout(abilityPane,BoxLayout.Y_AXIS));

        allSpellPane = new JPanel();
        allSpellPane.setLayout(new BoxLayout(allSpellPane,BoxLayout.Y_AXIS));

        spellPane = new JPanel();
        spellPane.setLayout(new BoxLayout(spellPane,BoxLayout.Y_AXIS));

        weaponActionPane = new JPanel();
        weaponActionPane.setLayout(new GridLayout(0,1));

        spellActionPane = new JPanel();
        spellActionPane.setLayout(new GridLayout(0,1));

        otherActionPane = new JPanel();
        otherActionPane.setLayout(new GridLayout(0,1));

        invenPane = new JPanel();
        invenPane.setLayout(new BoxLayout(invenPane,BoxLayout.PAGE_AXIS));

        combatTracker = new JPanel();
        combatTracker.setLayout(new GridLayout(0,1));
    }

    public void startTimer() {
        rollStarted=true;
        time=System.currentTimeMillis();
        while ((System.currentTimeMillis() < time + 1000)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int[] dice = new int[6];
        dice[0]=Integer.parseInt(d4Amount.getText());
        dice[1]=Integer.parseInt(d6Amount.getText());
        dice[2]=Integer.parseInt(d8Amount.getText());
        dice[3]=Integer.parseInt(d10Amount.getText());
        dice[4]=Integer.parseInt(d12Amount.getText());
        dice[5]=Integer.parseInt(d20Amount.getText());


        client.roll("rolled",dice,0);

        d4Amount.setText("0");
        d6Amount.setText("0");
        d8Amount.setText("0");
        d10Amount.setText("0");
        d12Amount.setText("0");
        d20Amount.setText("0");

        rollStarted=false;
    }

    public void update(){
        if(BattleMapClient.serverOut!=null)BattleMapClient.serverOut.println("INIT_UPDATE:"+nameField.getText()+":"+currentHP.getText()+":"+tempHP.getText());
    }
}
