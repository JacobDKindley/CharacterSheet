import javax.swing.*;
import java.awt.event.ActionEvent;

public class SpellInfo implements Comparable<SpellInfo>{
    String name,castTime,duration,components,description,tags,range;
    int lvl;

    Spell knownSpell, preparedSpell;

    public SpellInfo(String name,int lvl,String castTime,String duration,String range,String components,String description,String tags,String code){
        this.name=name;
        this.lvl=lvl;
        this.castTime=castTime;
        this.duration=duration;
        this.range=range;
        this.components=components;
        this.description=description;
        this.tags=tags;
        knownSpell = new Spell(this);
        knownSpell.checkBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!knownSpell.checkBox.isSelected()){
                    client.pc.yourSpells.remove(SpellInfo.this);
                    client.pc.preparedSpells.remove(SpellInfo.this);
                }else{
                    int i;
                    for(i=0;i<client.pc.yourSpells.size();i++){
                        if(SpellInfo.this.compareTo(client.pc.yourSpells.get(i))<0)break;
                    }
                    client.pc.yourSpells.add(i,SpellInfo.this);
                }
                client.updateGUI();
            }
        });
        preparedSpell = new Spell(this);
        preparedSpell.checkBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!preparedSpell.checkBox.isSelected()){
                    client.pc.preparedSpells.remove(SpellInfo.this);
                    int i;
                    for(i=0;i<client.pc.yourSpells.size();i++){
                        if(SpellInfo.this.compareTo(client.pc.yourSpells.get(i))<0)break;
                    }
                    client.pc.yourSpells.add(i,SpellInfo.this);
                    if(code!=null) {
                        for(i=0 ;i<client.pc.spellActions.size();i++){
                            if(client.pc.spellActions.get(i).name.equals(name))break;
                        }
                        client.pc.spellActions.remove(i);
                    }
                }else{
                    client.pc.yourSpells.remove(SpellInfo.this);
                    int i;
                    for(i=0;i<client.pc.preparedSpells.size();i++){
                        if(SpellInfo.this.compareTo(client.pc.preparedSpells.get(i))<0)break;
                    }
                    client.pc.preparedSpells.add(i,SpellInfo.this);
                    if(code!=null)client.pc.spellActions.add(new ActionPane(name,"",code));
                }
                client.updateGUI();
            }
        });
    }

    @Override
    public int compareTo(SpellInfo other) {
        if(lvl> other.lvl)return 1;
        else if(lvl< other.lvl)return -1;
        return name.compareTo(other.name);
    }

}
