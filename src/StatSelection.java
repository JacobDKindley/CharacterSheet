import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class StatSelection {
    private JTextField strField;
    private JTextField dexField;
    private JTextField conField;
    private JTextField intField;
    private JTextField wisField;
    private JTextField chaField;
    private JLabel strMod;
     JLabel strTotal;
    private JLabel dexMod;
     JLabel dexTotal;
    private JLabel conMod;
     JLabel conTotal;
    private JLabel intMod;
     JLabel intTotal;
    private JLabel wisMod;
     JLabel wisTotal;
    private JLabel chaMod;
     JLabel chaTotal;
    JButton finalizeButton;
    JPanel pane;

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
    public StatSelection(Character pc){
        strTotal.setText(Integer.toString(pc.strScore));
        dexTotal.setText(Integer.toString(pc.dexScore));
        conTotal.setText(Integer.toString(pc.conScore));
        intTotal.setText(Integer.toString(pc.intScore));
        wisTotal.setText(Integer.toString(pc.wisScore));
        chaTotal.setText(Integer.toString(pc.chaScore));
        strMod.setText(Integer.toString(pc.strScore-10));
        dexMod.setText(Integer.toString(pc.dexScore-10));
        conMod.setText(Integer.toString(pc.conScore-10));
        intMod.setText(Integer.toString(pc.intScore-10));
        wisMod.setText(Integer.toString(pc.wisScore-10));
        chaMod.setText(Integer.toString(pc.chaScore-10));
        strField.getDocument().addDocumentListener(doc);
        dexField.getDocument().addDocumentListener(doc);
        conField.getDocument().addDocumentListener(doc);
        intField.getDocument().addDocumentListener(doc);
        wisField.getDocument().addDocumentListener(doc);
        chaField.getDocument().addDocumentListener(doc);

    }

    public void update(){
        try{
            int str=Integer.parseInt(strField.getText());
            if(str>18)strTotal.setText(Integer.toString(18+Integer.parseInt(strMod.getText())));
            else if(str<3)strTotal.setText(Integer.toString(3+Integer.parseInt(strMod.getText())));
            else strTotal.setText(""+(Integer.parseInt(strMod.getText())+str));
        }catch (Exception e){}
        try{
            int dex=Integer.parseInt(dexField.getText());
            if(dex>18)dexTotal.setText(Integer.toString(18+Integer.parseInt(dexMod.getText())));
            else if(dex<3)dexTotal.setText(Integer.toString(3+Integer.parseInt(dexMod.getText())));
            else dexTotal.setText(""+(Integer.parseInt(dexMod.getText())+dex));
        }catch (Exception e){}
        try{
            int con=Integer.parseInt(conField.getText());
            if(con>18)conTotal.setText(Integer.toString(18+Integer.parseInt(conMod.getText())));
            else if(con<3)conTotal.setText(Integer.toString(3+Integer.parseInt(conMod.getText())));
            else conTotal.setText(""+(Integer.parseInt(conMod.getText())+con));
        }catch (Exception e){}
        try{
            int inte=Integer.parseInt(intField.getText());
            if(inte>18)intTotal.setText(Integer.toString(18+Integer.parseInt(intMod.getText())));
            else if(inte<3)intTotal.setText(Integer.toString(3+Integer.parseInt(intMod.getText())));
            else intTotal.setText(""+(Integer.parseInt(intMod.getText())+inte));
        }catch (Exception e){}
        try{
            int wis=Integer.parseInt(wisField.getText());
            if(wis>18)wisTotal.setText(Integer.toString(18+Integer.parseInt(wisMod.getText())));
            else if(wis<3)wisTotal.setText(Integer.toString(3+Integer.parseInt(wisMod.getText())));
            else wisTotal.setText(""+(Integer.parseInt(wisMod.getText())+wis));
        }catch (Exception e){}
        try{
            int cha=Integer.parseInt(chaField.getText());
            if(cha>18)chaTotal.setText(Integer.toString(18+Integer.parseInt(chaMod.getText())));
            else if(cha<3)chaTotal.setText(Integer.toString(3+Integer.parseInt(chaMod.getText())));
            else chaTotal.setText(""+(Integer.parseInt(chaMod.getText())+cha));
        }catch (Exception e){}
    }
}
