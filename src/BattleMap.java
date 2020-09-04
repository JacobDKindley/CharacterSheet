import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BattleMap extends JPanel {
    BufferedImage map;
    ArrayList<token> tokens = new ArrayList<>();
    String path="Files\\Battle Maps\\Blank.png";

    public BattleMap(){
        map = null;
    }

    @Override
    public Dimension getPreferredSize(){
        if(map==null)return new Dimension(0,0);
        return new Dimension(map.getWidth(),map.getHeight());
    }

    @Override
    public Dimension getMaximumSize() {
        if(map==null)return new Dimension(0,0);
        return new Dimension(map.getWidth(),map.getHeight());
    }

    @Override
    public Dimension getMinimumSize() {
        if(map==null)return new Dimension(0,0);
        return new Dimension(map.getWidth(),map.getHeight());
    }

    public void setMap(String path) throws IOException {
        File f = new File(client.filePath+path);
        map = ImageIO.read(f);
        this.path=path;
        this.revalidate();
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(BattleMapClient.battleMapLock && !BattleMapClient.host){
            if(map!=null)g.fillRect(0,0,map.getWidth(),map.getHeight());
            return;
        }
        if(map!=null){
            g.drawImage(map,0,0,this);
        }

        g.drawImage(BattleMapClient.clientPrep,0,0,this);
        g.drawImage(BattleMapClient.serverCommitted,0,0,this);

        g.setColor(Color.RED);
        for(token t:tokens){
            if(!t.visible)continue;
            g.drawImage(t.image,t.x,t.y,this);
            int deltaX=Math.abs((int)Math.round((double)t.x/64.0)-t.tileX), deltaY=Math.abs((int)Math.round((double)t.y/64.0)-t.tileY);
            for(int i=0;i<t.conditions.size();i++){
                g.drawString(t.conditions.get(i),t.x,t.y+(i+1)*12);
            }
            if(deltaX!=0 || deltaY!=0){
                int temp=deltaX;
                if(deltaY>deltaX)temp=deltaY;
                g.drawString("Distance: "+temp,t.x+t.size*32-32,t.y-12);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float).3));
                g2d.drawImage(t.image,t.tileX*64,t.tileY*64,this);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)1));


            }
        }
        for(token t:tokens){
            if(!t.visible)continue;
            g.drawString(t.name,t.x+t.size*32-3*t.name.length(),t.y);
        }

        if(BattleMapClient.host){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float).7));
            g2d.drawImage(BattleMapClient.fogOfWarCommitted,0,0,this);
            g2d.drawImage(BattleMapClient.fogOfWarPrep,0,0,this);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float)1));
        }else{
            g.drawImage(BattleMapClient.fogOfWarCommitted,0,0,this);
        }
    }

}
