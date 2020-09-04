import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleMapServer implements Runnable{
    Socket socket;
    static PrintStream[] clients = new PrintStream[16];
    static String[] names = new String[16];
    static Color[] colors = new Color[16];
    static int dmID=0;

    public static void initialize(){
        Arrays.fill(colors, Color.black);
        try {
            ServerSocket serverSocket = new ServerSocket(43594);
            ExecutorService pool = Executors.newFixedThreadPool(16);

            while (true){
                pool.execute(new BattleMapServer(serverSocket.accept()));
            }


        }catch (Exception e){System.out.println("Something went wrong:\n"+e);}
    }

    public BattleMapServer(Socket socket){
        this.socket=socket;
        System.out.println(socket.getInetAddress().getHostAddress());
    }

    public int nextID(){
        for(int i=0;i<clients.length;i++){
            if(clients[i]==null)return i;
        }
        return -1;
    }

    public int indexOfName(String name){
        for(int i=0;i<names.length;i++){
            if(name.equals(names[i]))return i;
        }
        return -1;
    }

    @Override
    public void run() {
        int ID=nextID();

        try{
            Scanner clientIn = new Scanner(socket.getInputStream());
            PrintStream serverOut = new PrintStream(socket.getOutputStream());
            serverOut.println("LOGIN:");
            clients[ID]=serverOut;
            while (clientIn.hasNextLine()){
                String in = clientIn.nextLine();
                String command= in.substring(0,in.indexOf(":"));
                in=in.substring(in.indexOf(":")+1);
                //parse input
                switch (command){
                    case "ROLL":            //ROLL:secret:COMMAND:d4:d6:d8:d10:d12:d20:bonus=sum(dropped #)
                        boolean secret = Boolean.parseBoolean(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        if(secret){
                            clients[dmID].println(command+":"+names[ID]+" secretly:"+Color.gray.getRGB()+":"+in);
                            if(ID==dmID)continue;
                            serverOut.println(command+":"+names[ID]+" secretly:"+Color.gray.getRGB()+":"+in);
                        }else {
                            for (PrintStream client : clients) {
                                if (client == null) continue;
                                client.println(command + ":" + names[ID] + ":" + colors[ID].getRGB() + ":" + in);
                            }
                        }
                        break;
                    case "INIT_CLEAR":      //INIT_CLEAR:
                    case "INIT_UPDATE":     //INIT_UPDATE:name:currHP:tempHP
                    case "INITIATIVE":      //INITIATIVE:roll:name:maxHP:currHP:tempHP
                    case "LOCK_MAP":        //LOCK_MAP
                    case "FOG_CLEAR":       //FOG_CLEAR
                    case "FOG_COMMIT":      //FOG_COMMIT:imageInfo
                    case "FOG_ERASE":       //FOG_ERASE:mouseX:mouseY:lastX:lastY
                    case "DRAW_CLEAR":      //DRAW_CLEAR:
                    case "DRAW_ERASE":      //DRAW_ERASE:mouseX:mouseY:lastX:lastY
                    case "DRAW_COMMIT":     //DRAW_COMMIT:imageinfo
                    case "TOKEN_NAME":      //TOKEN_NAME:index:name
                    case "TOKEN_SIZE":      //TOKEN_SIZE:index:size
                    case "TOKEN_CONDITION": //TOKEN_CONDITION:index:condition
                    case "TOKEN_REMOVE":    //TOKEN_REMOVE:index
                    case "TOKEN_LOCK":      //TOKEN_LOCK:index
                    case "TOKEN_RELEASED":  //TOKEN_RELEASED:index,tileX,tileY
                    case "TOKEN_DRAG":      //TOKEN_DRAG:index,x,y
                    case "SELECT_MAP":      //SELECT_MAP:path
                    case "ADD_TOKEN":       //ADD_TOKEN:name:path:x:y
                        for(PrintStream client: clients){
                            if(client==null)continue;
                            client.println(command+":"+in);
                        }
                        break;
                    case "CHAT":            //CHAT:message
                        for (PrintStream client : clients) {
                            if (client == null) continue;
                            client.println("CHAT:" + names[ID] + ":" + colors[ID].getRGB() + ":" + in);
                        }
                        break;
                    case "PM":              //PM:TARGET:message
                        String target=in.substring(0,in.indexOf(":"));
                        int index=indexOfName(target);
                        if(index==-1){
                            serverOut.println("PME:"+target);
                            break;
                        }
                        clients[index].println("PM:"+names[ID]+" whispers:"+colors[ID].getRGB()+":"+in.substring(in.indexOf(":")+1));
                        serverOut.println("CHAT:You whisper to "+target+":"+colors[index].getRGB()+":"+in.substring(in.indexOf(":")+1));
                        break;
                    case "LOGIN":           //LOGIN:name
                        names[ID]=in;
                        if(ID!=dmID)serverOut.println("LOCK_MAP:");
                        for (PrintStream client : clients) {
                            if (client == null) continue;
                            client.println("CHAT:System:" + Color.blue.getRGB() + ": "+in+" has entered the server.");
                        }

                        //Make a list of files that are needed. Send list to client. Client will send requests and finally a complete message.Server will have to parse the requests then be able to read the complete message and create the tokens.
                        StringBuilder needed= new StringBuilder();
                        for(token t:client.battleMap.tokens){
                            if(!needed.toString().contains(t.path)) needed.append(t.path).append(":");
                        }
                        needed.append(client.battleMap.path).append(":");

                        serverOut.println("CHECK_FOR_IMAGE:"+needed);


                        break;
                    case "COLOR":           //COLOR:RGBcode
                        colors[ID] = new Color(Integer.parseInt(in));
                        break;
                    case "GET_IMAGE":       //GET_IMAGE:path
                        String path=in;
                        File f = new File(path);
                        FileInputStream fis = new FileInputStream(f);
                        serverOut.println("GET_IMAGE:"+path+":"+ Arrays.toString(fis.readAllBytes()));
                        fis.close();
                        break;
                    case "CHECK_COMPLETE":  //CHECK_COMPLETE:
                        //used during login to say all needed pictures are loaded and the board can be created
                        for(int i=0;i<client.battleMap.tokens.size();i++){
                            serverOut.println("ADD_TOKEN:"+client.battleMap.tokens.get(i).name+":"+client.battleMap.tokens.get(i).path+":0:0");
                            serverOut.println("TOKEN_LOGIN:"+i+":"+client.battleMap.tokens.get(i).toString());
                        }
                        serverOut.println("SELECT_MAP:"+client.battleMap.path);


                        //send copy of drawings of serverCommitted Images
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(BattleMapClient.serverCommitted,"gif",baos);
                            byte[] bytes = baos.toByteArray();
                            serverOut.println("DRAW_COMMIT:"+ Arrays.toString(bytes));
                            baos = new ByteArrayOutputStream();
                            ImageIO.write(BattleMapClient.fogOfWarCommitted,"gif",baos);
                            bytes = baos.toByteArray();
                            serverOut.println("FOG_COMMIT:"+ Arrays.toString(bytes));
                        } catch (Exception e1) {
                            System.out.print(e1);
                        }
                        if(ID!=dmID && !BattleMapClient.battleMapLock)serverOut.println("LOCK_MAP:");   //used to hide map until fog and drawings and tokens have been added
                        for(InitiativeMarker im:BattleMapClient.initiativeMarkers)serverOut.println("INITIATIVE:"+im.roll+":"+im.name.getText()+":"+im.maxHP.getText()+":"+im.currHP.getText()+":"+im.tempHP);
                        break;
                    case "SELF":        //SELF:COMMAND:...
                        serverOut.println(in);
                        break;

                }

            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error\n"+e);}

        clients[ID]=null;
        for (PrintStream client : clients) {
            if (client == null) continue;
            client.println("CHAT:System:" + Color.blue.getRGB() + ": "+names[ID]+" has left the server.");
        }
        colors[ID]=null;
        names[ID]=null;


    }
}
