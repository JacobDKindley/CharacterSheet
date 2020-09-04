import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class token {
    BufferedImage image;
    int x,y,tileX,tileY;
    int size;   //1=medium-tiny, 2=large,3=huge,4=gargantuan
    String name="",path="";
    boolean editable=true,visible=true;
    ArrayList<String> conditions = new ArrayList<>();

    public token(String path,String name,int x,int y) throws IOException {
        tileX=x/64;
        tileY=y/64;
        this.x=tileX*64;
        this.y=tileY*64;
        this.path=path;
        this.name=name;
        File f = new File(client.filePath+path);
        image = ImageIO.read(f);
        size=image.getHeight()/64;
    }

    public void update(String name,int x,int y,int tileX,int tileY,boolean editable,int size,String conditions){
        this.name=name;
        this.x=x;
        this.y=y;
        this.tileX=tileX;
        this.tileY=tileY;
        this.editable=editable;
        if(this.size!=size)changeSize(size-1);
        while (conditions.contains(":")){
            setCondition(conditions.substring(0,conditions.indexOf(":")));
            conditions=conditions.substring(conditions.indexOf(":")+1);
        }
        setCondition(conditions);

    }

    public void setCondition(String condition){
        if(conditions.contains(condition)){
            removeCondition(condition);
            return;
        }
        if(condition.equals("Invisible") && !BattleMapClient.host)visible=false;
        conditions.add(condition);
    }

    public void removeCondition(String condition){
        conditions.remove(condition);
        if(condition.equals("Invisible") && !BattleMapClient.host)visible=true;
    }

    public void setName(String name){
        this.name=name;
    }

    public void changeSize(int size){
        this.size=size+1;

        BufferedImage modified;
        if(size==0){       //Medium
            modified = new BufferedImage(64,64,image.getType());

            Graphics2D graphics2D = modified.createGraphics();
            graphics2D.drawImage(image,0,0,64,64,null);
            graphics2D.dispose();
        }else if(size==1){     //Large
            modified = new BufferedImage(128,128,image.getType());

            Graphics2D graphics2D = modified.createGraphics();
            graphics2D.drawImage(image,0,0,128,128,null);
            graphics2D.dispose();
        }else if(size==2){     //Huge
            modified = new BufferedImage(192,192,image.getType());

            Graphics2D graphics2D = modified.createGraphics();
            graphics2D.drawImage(image,0,0,192,192,null);
            graphics2D.dispose();
        }else{              //Gargantuan
            modified = new BufferedImage(256,256,image.getType());

            Graphics2D graphics2D = modified.createGraphics();
            graphics2D.drawImage(image,0,0,256,256,null);
            graphics2D.dispose();
        }

        image=modified;
    }
    public String toString(){
        StringBuilder temp= new StringBuilder();
        for(int i=0;i<conditions.size()-1;i++){
            temp.append(conditions.get(i)).append(":");
        }
        if(conditions.size()>0)temp.append(conditions.get(conditions.size()-1));
        return (name+":"+x+":"+y+":"+tileX+":"+tileY+":"+editable+":"+size+":"+temp);
    }
}
