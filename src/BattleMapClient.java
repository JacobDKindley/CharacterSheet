import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Medium creatures are 64x64 pixels.
 */
public class BattleMapClient implements Runnable{
    StyledDocument chat;
    token selectedToken=null;
    static Socket serverSocket;
    static Scanner serverIn;
    static PrintStream serverOut;
    String whisperReply, filePath,tokenPath ="Files\\Tokens\\", battleMapPath ="Files\\Battle Maps\\";
    Style redText,speakerText,d4Text,d6Text,d8Text,d10Text,d12Text,d20Text,sumText;
    String clickCommand="Move Token";
    static boolean secretRolling=false,host=false,allowPopup=true,battleMapLock=false;
    JPopupMenu tokenPopupMenu,battleMapPopupMenu;
    static BufferedImage clientPrep,serverCommitted,fogOfWarPrep,fogOfWarCommitted;
    int initialDrawX,initialDrawY;  //Used for drawing circle and square so the program knows where the click draw originated from and determine where to place it
    Color drawColor=Color.black;    //Color used when drawing
    static ArrayList<InitiativeMarker> initiativeMarkers = new ArrayList<>();


    public BattleMapClient(boolean host){
        BattleMapClient.host=host;
        filePath=client.filePath;

        try {
            Files.createDirectories(Paths.get(filePath+tokenPath));
            Files.createDirectories(Paths.get(filePath+battleMapPath));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error2 connecting to server:\n"+e);
        }



        //Setup client.gui



        //Setup chat functionality

        chat = client.gui.chatTextPane.getStyledDocument();

        //Add listener for enter and shift enter so that it submits text and creates new lines accordingly
        InputMap chatInput= client.gui.chatInput.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        chatInput.put(enter, "SUBMIT");
        chatInput.put(shiftEnter, "ADD LINE");
        ActionMap chatAction = client.gui.chatInput.getActionMap();
        chatAction.put("SUBMIT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    submitChat(client.gui.chatInput.getText());
                    client.gui.chatInput.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        chatAction.put("ADD LINE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                client.gui.chatInput.append("\n");
            }
        });


        //Setup right click menu

        battleMapPopupMenu = new JPopupMenu();

        JMenuItem move = new JMenuItem("Move Token");
        AbstractAction setClickCommand = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickCommand=e.getActionCommand();
            }
        };
        move.addActionListener(setClickCommand);
        battleMapPopupMenu.add(move);

        JMenu draw = new JMenu("Draw");
        battleMapPopupMenu.add(draw);

        JMenuItem freeHand = new JMenuItem("Free Hand");
        freeHand.addActionListener(setClickCommand);
        draw.add(freeHand);
        JMenuItem drawSquare = new JMenuItem("Draw Square");
        drawSquare.addActionListener(setClickCommand);
        draw.add(drawSquare);
        JMenuItem drawCircle = new JMenuItem("Draw Circle");
        drawCircle.addActionListener(setClickCommand);
        draw.add(drawCircle);
        JMenuItem drawCone = new JMenuItem("Draw Cone");
        drawCone.addActionListener(setClickCommand);
        draw.add(drawCone);
        JMenuItem erase = new JMenuItem("Erase");
        erase.addActionListener(setClickCommand);
        draw.add(erase);




        //Setup menubar


        if(host) {
            JMenuBar menuBar = client.frame.getJMenuBar();

            JMenu token = new JMenu("Tokens");
            menuBar.add(token);

            JMenu dmTools = new JMenu("DM Tools");
            menuBar.add(dmTools);

            JMenuItem hidebattleMap = new JMenuItem("Hide Map");
            hidebattleMap.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    serverOut.println("LOCK_MAP:");
                }
            });
            dmTools.add(hidebattleMap);

            JMenuItem clearInitiative = new JMenuItem("Clear Initiative");
            clearInitiative.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    serverOut.println("INIT_CLEAR:");
                }
            });
            dmTools.add(clearInitiative);

            JMenuItem addInitiative = new JMenuItem("Monster Initiative");
            addInitiative.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog("Enter Monster Name:");
                    int bonus=-10;
                    while (bonus==-10){
                        try {
                            bonus=Integer.parseInt(JOptionPane.showInputDialog("Enter INIT bonus:"));
                        }catch (Exception ex){}
                    }
                    int roll=(int)(1+Math.random()*20);
                    serverOut.println("ROLL:"+secretRolling+":"+name+" Init::::::"+roll+" + :"+bonus+" = "+(roll+bonus));
                    serverOut.println("INITIATIVE:"+(roll+bonus)+":"+name+":?:?:?");
                }
            });
            dmTools.add(addInitiative);

            JMenuItem clearDrawings = new JMenuItem("Clear Drawings");
            dmTools.add(clearDrawings);
            clearDrawings.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    serverOut.println("DRAW_CLEAR:");
                }
            });
            JMenuItem clearFog = new JMenuItem("Clear Fog");
            clearFog.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    serverOut.println("FOG_CLEAR:");
                }
            });
            dmTools.add(clearFog);

            JMenuItem tokenMenu = new JMenuItem("Tokens");
            tokenMenu.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JFileChooser fc = new JFileChooser(tokenPath);
                    if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
                    String name = JOptionPane.showInputDialog("Enter Token Name:");
                    Point p = client.gui.battleMapScroller.getViewport().getViewPosition();
                    File f=fc.getSelectedFile();
                    if(f.exists())serverOut.println("ADD_TOKEN:" + name + ":" + f.getPath().substring(f.getPath().indexOf(tokenPath)) + ":" + (p.x + 128) + ":" + (p.y + 128));

                }
            });
            token.add(tokenMenu);

            JMenuItem battleMapMenu = new JMenuItem("Battle Maps");
            battleMapMenu.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    JFileChooser fc = new JFileChooser(battleMapPath);
                    if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
                    File f=fc.getSelectedFile();
                    if(f.exists())serverOut.println("SELECT_MAP:" + f.getPath().substring(f.getPath().indexOf(battleMapPath)));
                }
            });
            token.add(battleMapMenu);



            //Add extra token DM options to right click menu

            ActionListener conditionListener = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    serverOut.println("TOKEN_CONDITION:"+client.battleMap.tokens.indexOf(selectedToken)+":"+e.getActionCommand());
                }
            };

            tokenPopupMenu = new JPopupMenu();
            JMenu conditions = new JMenu("Conditions");
            tokenPopupMenu.add(conditions);

            JMenuItem blind = new JMenuItem("Blind");
            conditions.add(blind);
            blind.addActionListener(conditionListener);
            JMenuItem charmed = new JMenuItem("Charmed");
            conditions.add(charmed);
            charmed.addActionListener(conditionListener);
            JMenuItem concentrating = new JMenuItem("Concen.");
            conditions.add(concentrating);
            concentrating.addActionListener(conditionListener);
            JMenuItem deaf = new JMenuItem("Deaf");
            conditions.add(deaf);
            deaf.addActionListener(conditionListener);
            JMenuItem frightened = new JMenuItem("Feared");
            conditions.add(frightened);
            frightened.addActionListener(conditionListener);
            JMenuItem grappled = new JMenuItem("Grappled");
            conditions.add(grappled);
            grappled.addActionListener(conditionListener);
            JMenuItem invisible = new JMenuItem("Invisible");
            conditions.add(invisible);
            invisible.addActionListener(conditionListener);
            JMenuItem paralyzed = new JMenuItem("Paralyzed");
            conditions.add(paralyzed);
            paralyzed.addActionListener(conditionListener);
            JMenuItem petrified = new JMenuItem("Petrified");
            conditions.add(petrified);
            petrified.addActionListener(conditionListener);
            JMenuItem poisoned = new JMenuItem("Poisoned");
            conditions.add(poisoned);
            poisoned.addActionListener(conditionListener);
            JMenuItem prone = new JMenuItem("Prone");
            conditions.add(prone);
            prone.addActionListener(conditionListener);
            JMenuItem restrained = new JMenuItem("Restrained");
            conditions.add(restrained);
            restrained.addActionListener(conditionListener);
            JMenuItem stunned = new JMenuItem("Stunned");
            conditions.add(stunned);
            stunned.addActionListener(conditionListener);

            JMenu other = new JMenu("Other");
            tokenPopupMenu.add(other);

            JMenuItem size = new JMenuItem("Change Size");
            other.add(size);
            size.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String[] options = {"Medium","Large","Huge","Gargantuan"};
                    int size=JOptionPane.showOptionDialog(null,"Change Size","Size Selector",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,null);
                    if(!(size==JOptionPane.CLOSED_OPTION))serverOut.println("TOKEN_SIZE:"+client.battleMap.tokens.indexOf(selectedToken)+":"+size);
                }
            });

            JMenuItem name = new JMenuItem("Change Name");
            other.add(name);
            name.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = JOptionPane.showInputDialog("Enter new name");
                    if(name!=null)serverOut.println("TOKEN_NAME:"+client.battleMap.tokens.indexOf(selectedToken)+":"+name);
                }
            });

            JMenuItem remove = new JMenuItem("Remove");
            other.add(remove);
            remove.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    serverOut.println("TOKEN_REMOVE:"+client.battleMap.tokens.indexOf(selectedToken));
                }
            });

            //Add fog of war option to DM right click on map

            JMenu fogOfWar= new JMenu("Fog of War");
            battleMapPopupMenu.add(fogOfWar);

            JMenuItem fogAdd = new JMenuItem("Add Fog");
            fogAdd.addActionListener(setClickCommand);
            fogOfWar.add(fogAdd);
            JMenuItem fogRemove = new JMenuItem("Remove Fog");
            fogRemove.addActionListener(setClickCommand);
            fogOfWar.add(fogRemove);

        }


        //Setup battle map mouse listeners


        client.battleMap.addMouseListener(new MouseAdapter() {



            /**
             * Finds a token at the position given by the parameters
             * @param e mousevent used to find position of the mouse.
             * @return token at the position, or null if no token exists
             */
            public token findToken(MouseEvent e){
                for(int i=0;i<client.battleMap.tokens.size();i++){
                    token t=client.battleMap.tokens.get(i);
                    if(!t.editable)continue;
                    if(Math.abs(t.x+t.size*32-e.getX())<t.size*32 && Math.abs(t.y+t.size*32-e.getY())<t.size*32){   //checks if token is being clicked
                        return t;
                    }
                }
                return null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(battleMapLock && !host)return;
                if(SwingUtilities.isRightMouseButton(e)){
                    clientPrep = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                    fogOfWarPrep = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                    initialDrawX=e.getX();
                    initialDrawY=e.getY();
                    client.battleMap.repaint();
                    if(!allowPopup)return;   //if moving a token and right click is pressed, no popup should be generated
                    selectedToken=findToken(e);
                    if(selectedToken==null || !host)battleMapPopupMenu.show(client.battleMap,e.getX(),e.getY());
                    else tokenPopupMenu.show(client.battleMap,e.getX(),e.getY());
                    return;
                }
                switch (clickCommand) {
                    case "Move Token":
                        selectedToken = findToken(e);
                        if (selectedToken != null) {
                            serverOut.println("TOKEN_LOCK:" + client.battleMap.tokens.indexOf(selectedToken));
                            allowPopup=false;
                        }
                        break;
                    case "Add Fog":
                    case "Free Hand":
                    case "Draw Square":
                    case "Draw Circle":
                    case "Draw Cone":
                        initialDrawX=e.getX();
                        initialDrawY=e.getY();
                        allowPopup=false;
                        break;
                    case "Erase":
                        serverOut.println("DRAW_ERASE:"+e.getX()+":"+e.getY());
                        break;
                    case "Remove Fog":
                        serverOut.println("FOG_ERASE:"+e.getX()+":"+e.getY());
                        break;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(battleMapLock && !host)return;
                if(SwingUtilities.isRightMouseButton(e))return;
                switch (clickCommand) {
                    case "Move Token":
                        if(selectedToken!=null) {
                            int tileX,tileY,index;
                            index=client.battleMap.tokens.indexOf(selectedToken);
                            tileX=(int) Math.round((double)(e.getX()-selectedToken.size*32)/64.0);
                            tileY=(int) Math.round((double)(e.getY()-selectedToken.size*32)/64.0);
                            if(tileX<0)tileX=0;
                            else if(tileX*64>=client.battleMap.getWidth())tileX=client.battleMap.getWidth()/64-1;
                            if(tileY<0)tileY=0;
                            else if(tileY*64>=client.battleMap.getHeight())tileY=client.battleMap.getHeight()/64-1;

                            serverOut.println("TOKEN_RELEASED:"+index+","+tileX+","+tileY);

                            selectedToken = null;
                            allowPopup=true;
                        }
                        break;
                    case "Free Hand":
                    case "Draw Square":
                    case "Draw Circle":
                    case "Draw Cone":
                        allowPopup=true;

                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(clientPrep,"gif",baos);
                            byte[] bytes = baos.toByteArray();
                            serverOut.println("DRAW_COMMIT:"+ Arrays.toString(bytes));
                        } catch (Exception e1) {
                            System.out.print(e1);
                        }

                        clientPrep=new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                        break;
                    case "Erase":
                    case "Remove Fog":
                        allowPopup=true;
                        break;
                    case "Add Fog":
                        allowPopup=true;

                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(fogOfWarPrep,"gif",baos);
                            byte[] bytes = baos.toByteArray();
                            serverOut.println("FOG_COMMIT:"+ Arrays.toString(bytes));
                        } catch (Exception e1) {
                            System.out.print(e1);
                        }

                        fogOfWarPrep=new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                        break;
                }

            }



        });
        client.battleMap.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(battleMapLock && !host)return;
                if(SwingUtilities.isLeftMouseButton(e)) {
                    switch (clickCommand) {
                        case "Move Token":
                            if (selectedToken != null) {
                                int x = e.getX() - selectedToken.size * 32, y = e.getY() - selectedToken.size * 32, index = client.battleMap.tokens.indexOf(selectedToken);
                                serverOut.println("TOKEN_DRAG:" + index + "," + x + "," + y);
                            }
                            break;
                        case "Free Hand":
                            Graphics2D g = (Graphics2D)clientPrep.getGraphics();
                            g.setColor(drawColor);
                            g.setStroke(new BasicStroke(3));
                            g.drawLine(e.getX(), e.getY(), initialDrawX, initialDrawY);
                            initialDrawX=e.getX();
                            initialDrawY=e.getY();
                            g.dispose();
                            client.battleMap.repaint();
                            break;
                        case "Draw Square":
                            clientPrep = new BufferedImage(client.battleMap.getWidth(), client.battleMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            g = (Graphics2D) clientPrep.getGraphics();
                            int size = 2 * Math.max(Math.abs(e.getX() - initialDrawX), Math.abs(e.getY() - initialDrawY));
                            g.setColor(drawColor);
                            g.setStroke(new BasicStroke(3));
                            g.drawRect(initialDrawX - size / 2, initialDrawY - size / 2, size, size);
                            g.dispose();
                            client.battleMap.repaint();
                            break;
                        case "Draw Circle":
                            clientPrep = new BufferedImage(client.battleMap.getWidth(), client.battleMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            g = (Graphics2D) clientPrep.getGraphics();
                            g.setStroke(new BasicStroke(3));
                            size = 2 * Math.max(Math.abs(e.getX() - initialDrawX), Math.abs(e.getY() - initialDrawY));
                            g.setColor(drawColor);
                            g.drawOval(initialDrawX - size / 2, initialDrawY - size / 2, size, size);
                            g.dispose();
                            client.battleMap.repaint();
                            break;
                        case "Draw Cone":
                            int[] Xs= new int[3], Ys=new int[3];
                            int dX=Math.abs(initialDrawX-e.getX()),dY=Math.abs(initialDrawY-e.getY());
                            Xs[0]=initialDrawX;
                            Ys[0]=initialDrawY;
                            if(e.getX()>initialDrawX){
                                if(e.getY()>initialDrawY){      //Quadrant 4
                                    Xs[1]=initialDrawX+dX+dY/2;
                                    Ys[1]=initialDrawY+dY-dX/2;
                                    Xs[2]=initialDrawX+dX-dY/2;
                                    Ys[2]=initialDrawY+dY+dX/2;
                                }else{                          //Quadrant 1
                                    Xs[1]=initialDrawX+dX-dY/2;
                                    Ys[1]=initialDrawY-dY-dX/2;
                                    Xs[2]=initialDrawX+dX+dY/2;
                                    Ys[2]=initialDrawY-dY+dX/2;
                                }
                            }else{
                                if(e.getY()>initialDrawY){      //Quadrant 3
                                    Xs[1]=initialDrawX-dX-dY/2;
                                    Ys[1]=initialDrawY+dY-dX/2;
                                    Xs[2]=initialDrawX-dX+dY/2;
                                    Ys[2]=initialDrawY+dY+dX/2;
                                }else{                          //Quadrant 2

                                    Xs[1]=initialDrawX-dX+dY/2;
                                    Ys[1]=initialDrawY-dY-dX/2;
                                    Xs[2]=initialDrawX-dX-dY/2;
                                    Ys[2]=initialDrawY-dY+dX/2;
                                }
                            }

                            clientPrep = new BufferedImage(client.battleMap.getWidth(), client.battleMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            g = (Graphics2D) clientPrep.getGraphics();
                            g.setStroke(new BasicStroke(3));
                            g.setColor(drawColor);
                            g.drawPolygon(Xs, Ys, 3);
                            g.drawString(""+(int)(5*Math.sqrt(dX*dX+dY*dY)/64),initialDrawX,initialDrawY);
                            g.dispose();
                            client.battleMap.repaint();
                            break;
                        case "Erase":
                            serverOut.println("DRAW_ERASE:"+e.getX()+":"+e.getY());
                            break;
                        case "Add Fog":
                            fogOfWarPrep = new BufferedImage(client.battleMap.getWidth(), client.battleMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            g = (Graphics2D) fogOfWarPrep.getGraphics();
                            int x=Math.min(initialDrawX,e.getX()),y=Math.min(initialDrawY,e.getY()),width=Math.abs(initialDrawX-e.getX()),height=Math.abs(initialDrawY-e.getY());
                            g.setColor(Color.GRAY);
                            g.fillRect(x,y, width,height);
                            g.dispose();
                            client.battleMap.repaint();
                            break;
                        case "Remove Fog":
                            serverOut.println("FOG_ERASE:"+e.getX()+":"+e.getY());
                            break;
                    }
                }
            }
        });

        //Setup usable texts

        redText= chat.addStyle("red",null);
        speakerText = chat.addStyle("speaker",null);
        d4Text= chat.addStyle("d4",null);
        d6Text= chat.addStyle("d6",null);
        d8Text= chat.addStyle("d8",null);
        d10Text= chat.addStyle("d10",null);
        d12Text= chat.addStyle("d12",null);
        d20Text= chat.addStyle("d20",null);
        sumText= chat.addStyle("sum",null);

        StyleConstants.setForeground(redText,Color.red);
        StyleConstants.setForeground(speakerText,Color.BLACK);
        StyleConstants.setForeground(d4Text,Color.orange);
        StyleConstants.setForeground(d6Text,Color.blue);
        StyleConstants.setForeground(d8Text,Color.red);
        StyleConstants.setForeground(d10Text,Color.magenta);
        StyleConstants.setForeground(d12Text,Color.green);
        StyleConstants.setForeground(d20Text,Color.black);
        StyleConstants.setForeground(sumText,Color.gray);




    }



    public void submitChat(String input) throws BadLocationException {
        if(input==null || input.equals(""))return;
        //if there was a command entered instead of a message
        if(input.charAt(0)=='/'){
            int i= input.indexOf(" ");
            if(i==-1)i=input.length();
            String command = input.substring(1,i).toLowerCase();
            if(i!=input.length())input=input.substring(i+1);
            try {
                switch (command) {
                    case "w":
                    case "whisper":
                    case "msg":
                    case "message": // /w "TARGET" message
                        input = input.substring(1);
                        String target = input.substring(0, input.indexOf("\""));
                        input = input.substring(input.indexOf("\"") + 1).trim();
                        serverOut.println("PM:" + target + ":" + convertToUnprintable(input));
                        break;
                    case "r":
                    case "reply":
                        serverOut.println("PM:" + whisperReply + ":" + convertToUnprintable(input));
                        break;
                    case "secret":
                        secretRolling=true;
                        break;
                    case "public":
                        secretRolling=false;
                        break;
                    default:    //For unknown commands
                        chat.insertString(chat.getLength(), "Unknown command. Usable commands are:\n/w \"name\" message - pm someone\n/r message - reply to pm\n" +
                                "/secret - only DM can see rolls\n/public - everyone can see rolls\n", null);
                        break;
                }
            }catch (Exception e){chat.insertString(chat.getLength(),"Error: "+e+"\n",redText);}
            return;

        }


        serverOut.println("CHAT:"+convertToUnprintable(input));

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            if(host)serverSocket = new Socket("localhost", 43594);
            else{
                String ip=null;
                while (ip==null)ip=JOptionPane.showInputDialog("Enter IP Address");
                serverSocket = new Socket(ip, 43594);    //creates a connection to the server
            }
            serverIn = new Scanner(serverSocket.getInputStream());  //creates a scanner that listens to input from server
            serverOut = new PrintStream(serverSocket.getOutputStream());    //creates a stream that sends information to the server

            while (serverIn.hasNextLine()){
                String in=serverIn.nextLine();
                String command=in.substring(0,in.indexOf(":"));
                in=in.substring(in.indexOf(":")+1);
                switch (command){
                    case "ROLL":            //ROLL:SPEAKER:COLOR:COMMAND:d4:d6:d8:d10:d12:d20:bonus = sum (Dropped #)
                        String speaker=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        int color=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        StyleConstants.setForeground(speakerText,new Color(color));
                        String rollCommand = in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String d4=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String d6=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String d8=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String d10=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String d12=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String d20=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        chat.insertString(chat.getLength(),speaker+" "+rollCommand+": ",speakerText);
                        chat.insertString(chat.getLength(),d4,d4Text);
                        chat.insertString(chat.getLength(),d6,d6Text);
                        chat.insertString(chat.getLength(),d8,d8Text);
                        chat.insertString(chat.getLength(),d10,d10Text);
                        chat.insertString(chat.getLength(),d12,d12Text);
                        chat.insertString(chat.getLength(),d20,d20Text);
                        chat.insertString(chat.getLength(),in+"\n",sumText);
                        break;
                    case "TOKEN_REMOVE":    //TOKEN_LOCK:index
                        int index=Integer.parseInt(in);
                        client.battleMap.tokens.remove(index);
                        client.battleMap.repaint();
                        break;
                    case "TOKEN_LOCK":      //TOKEN_LOCK:index
                        index=Integer.parseInt(in);
                        client.battleMap.tokens.get(index).editable=false;
                        break;
                    case "TOKEN_RELEASED":  //TOKEN_RELEASED:index,tileX,tileY
                        index=Integer.parseInt(in.substring(0,in.indexOf(",")));
                        in=in.substring(in.indexOf(",")+1);
                        int tileX=Integer.parseInt(in.substring(0,in.indexOf(",")));
                        in=in.substring(in.indexOf(",")+1);
                        int tileY=Integer.parseInt(in);
                        client.battleMap.tokens.get(index).tileX=tileX;
                        client.battleMap.tokens.get(index).tileY=tileY;
                        client.battleMap.tokens.get(index).x=tileX*64;
                        client.battleMap.tokens.get(index).y=tileY*64;
                        client.battleMap.tokens.get(index).editable=true;
                        client.battleMap.repaint();
                        break;
                    case "TOKEN_DRAG":  //TOKEN_DRAG:index,x,y
                        index=Integer.parseInt(in.substring(0,in.indexOf(",")));
                        in=in.substring(in.indexOf(",")+1);
                        int x=Integer.parseInt(in.substring(0,in.indexOf(",")));
                        in=in.substring(in.indexOf(",")+1);
                        int y=Integer.parseInt(in);
                        client.battleMap.tokens.get(index).x=x;
                        client.battleMap.tokens.get(index).y=y;
                        client.battleMap.repaint();
                        break;
                    case "SELECT_MAP":  //SELECT_battleMap:path
                        try {
                            client.battleMap.setMap(in);
                            clientPrep = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                            BufferedImage temp = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = (Graphics2D) temp.getGraphics();
                            g.drawImage(serverCommitted,0,0,null);
                            g.dispose();
                            serverCommitted = temp;
                            fogOfWarPrep= new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                            temp = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                            g = (Graphics2D) temp.getGraphics();
                            g.drawImage(fogOfWarCommitted,0,0,null);
                            g.dispose();
                            fogOfWarCommitted = temp;
                            client.battleMap.repaint();
                        }catch (Exception e){
                            serverOut.println("GET_IMAGE:"+ in);
                            serverOut.println("SELF:SELECT_MAP:"+in);
                        }
                        break;
                    case "ADD_TOKEN": //ADD_TOKEN:name:path:x:Y
                        String name=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String path=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        x=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        y=Integer.parseInt(in);

                        try {
                            client.battleMap.tokens.add(new token(path,name,x,y));
                            client.battleMap.repaint();
                        }catch (Exception e){
                            serverOut.println("GET_IMAGE:"+ path);
                            serverOut.println("SELF:ADD_TOKEN:"+name+":"+path+":"+x+":"+y);
                        }
                        break;
                    case "PM":          //PM:SPEAKER whispers:COLOR:message
                        whisperReply=in.substring(0,in.indexOf("whispers")-1);
                    case "CHAT":        //CHAT:SPEAKER:COLOR:message  used to recieve global and private messages
                        try {
                            speaker=in.substring(0,in.indexOf(":"));
                            in=in.substring(in.indexOf(":")+1);
                            color=Integer.parseInt(in.substring(0,in.indexOf(":")));
                            in=convertFromUnprintable(in.substring(in.indexOf(":")+1));
                            StyleConstants.setForeground(speakerText,new Color(color));
                            chat.insertString(chat.getLength(),speaker+":",speakerText);
                            chat.insertString(chat.getLength(),convertFromUnprintable(in)+"\n",null);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "PME":     //PME:TARGET    occurs when PMing someone who doesn't exist
                        chat.insertString(chat.getLength(),"Error:"+in+" does not exist.\n",redText);
                        break;
                    case "LOGIN":   //LOGIN:        used to setup username
                        serverOut.println("LOGIN:"+JOptionPane.showInputDialog("Enter name:"));
                        changeColor();
                        serverOut.println("COLOR:"+ drawColor.getRGB());
                        break;
                    case "GET_IMAGE":       //GET_IMAGE:path:bytes
                        path = in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        byte[] bytes=reconstructBytes(in);
                        String directoryPath=filePath+path.substring(0,path.lastIndexOf("\\")+1);
                        if(!(new File(directoryPath).exists()))Files.createDirectories(Paths.get(directoryPath));
                        FileOutputStream fos = new FileOutputStream(filePath+path);
                        fos.write(bytes);
                        fos.close();
                        break;
                    case "TOKEN_LOGIN":     //TOKEN_LOGIN:index:name:x:y:tileX:tileY:editable:size:condition1:condition2:etc...
                        index=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        name = in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        x= Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        y= Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        tileX=Integer.parseInt( in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        tileY= Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        boolean editable= Boolean.parseBoolean(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        int size = Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        client.battleMap.tokens.get(index).update(name,x,y,tileX,tileY,editable,size,in);
                        client.battleMap.repaint();
                        break;
                    case "CHECK_FOR_IMAGE":     //CHECK_FOR_IMAGE:path:path:path:etc...
                        //Checks if images are on computer. If not send requests to download them
                        while(in.contains(":")){
                            path = in.substring(0,in.indexOf(":"));
                            in=in.substring(in.indexOf(":")+1);
                            File f = new File(filePath+path);
                            if(f.exists())continue;
                            serverOut.println("GET_IMAGE:"+path);
                        }
                        serverOut.println("CHECK_COMPLETE:");
                        break;
                    case "TOKEN_CONDITION":     //TOKEN_CONDITION:index:condition
                        index=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        client.battleMap.tokens.get(index).setCondition(in);
                        client.battleMap.repaint();
                        break;
                    case "TOKEN_SIZE":          //TOKEN_SIZE:index:size
                        index=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        client.battleMap.tokens.get(index).changeSize(Integer.parseInt(in));
                        client.battleMap.repaint();
                        break;
                    case "TOKEN_NAME":          //TOKEN_NAME:index:name
                        index=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        client.battleMap.tokens.get(index).setName(in);
                        client.battleMap.repaint();
                        break;
                    case "DRAW_COMMIT":         //DRAW_COMMIT:bytes
                        bytes = reconstructBytes(in);
                        InputStream inputStream = new ByteArrayInputStream(bytes);
                        BufferedImage temp = ImageIO.read(inputStream);
                        serverCommitted.getGraphics().drawImage(temp,0,0,client.battleMap);
                        client.battleMap.repaint();
                        break;
                    case "DRAW_ERASE":          //DRAW_ERASE:mouesX:mouseY
                        x=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        y=Integer.parseInt(in);
                        Graphics2D g = (Graphics2D) serverCommitted.getGraphics();
                        g.setBackground(new Color(0,0,0,0));
                        g.clearRect(x-8,y-8,16,16);


                        g.dispose();
                        client.battleMap.repaint();
                        break;
                    case "DRAW_CLEAR":          //DRAW_CLEAR
                        serverCommitted = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                        client.battleMap.repaint();
                        break;
                    case "FOG_COMMIT":          //FOG_COMMIT:bytes
                        bytes = reconstructBytes(in);
                        inputStream = new ByteArrayInputStream(bytes);
                        temp = ImageIO.read(inputStream);
                        fogOfWarCommitted.getGraphics().drawImage(temp,0,0,client.battleMap);
                        client.battleMap.repaint();
                        break;
                    case "FOG_ERASE":           //FOG_ERASE:x:y
                        x=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        y=Integer.parseInt(in);
                        g = (Graphics2D) fogOfWarCommitted.getGraphics();
                        g.setBackground(new Color(0,0,0,0));
                        g.clearRect(x-32,y-32,64,64);

                        g.dispose();
                        client.battleMap.repaint();
                        break;
                    case "FOG_CLEAR":           //FOG_CLEAR:
                        fogOfWarCommitted = new BufferedImage(client.battleMap.getWidth(),client.battleMap.getHeight(),BufferedImage.TYPE_INT_ARGB);
                        client.battleMap.repaint();
                        break;
                    case "LOCK_MAP":            //LOCK_battleMap:
                        battleMapLock= !battleMapLock;
                        if(client.battleMap!=null)client.battleMap.repaint();
                        break;
                    case "INITIATIVE":      //INITIATIVE:roll:name:maxHP:currHP:tempHP
                        int roll=Integer.parseInt(in.substring(0,in.indexOf(":")));
                        in=in.substring(in.indexOf(":")+1);
                        name=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String maxHP=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        String currHP=in.substring(0,in.indexOf(":"));
                        String tempHP=in.substring(in.indexOf(":")+1);
                        InitiativeMarker marker = new InitiativeMarker(roll,name,maxHP,currHP,tempHP);
                        int i;
                        for(i=0;i<initiativeMarkers.size();i++){
                            if(marker.name.getText().equals(initiativeMarkers.get(i).name.getText())){
                                initiativeMarkers.remove(i);
                                i--;
                            }else if(marker.compareTo(initiativeMarkers.get(i))>0)break;
                        }
                        initiativeMarkers.add(i,marker);
                        client.gui.combatTracker.removeAll();
                        for(InitiativeMarker im:initiativeMarkers)client.gui.combatTracker.add(im.pane);
                        break;
                    case "INIT_UPDATE":     //INIT_UPDATE:name:currHP:tempHP
                        name=in.substring(0,in.indexOf(":"));
                        in=in.substring(in.indexOf(":")+1);
                        currHP=in.substring(0,in.indexOf(":"));
                        tempHP=in.substring(in.indexOf(":")+1);
                        for(InitiativeMarker im:initiativeMarkers){
                            if(im.name.getText().equals(name)){
                                im.currHP.setText(currHP);
                                im.tempHP.setText(tempHP);
                            }
                        }
                        break;
                    case "INIT_CLEAR":      //INIT_CLEAR:
                        initiativeMarkers = new ArrayList<>();
                        client.gui.combatTracker.removeAll();
                        break;
                }
            }
        }catch (Exception e){
           JOptionPane.showMessageDialog(null,"Error connecting to server:\n"+e);
            client.gui.battleMapScroller.setViewportView(client.gui.connectionPane);
            try {
                serverOut=null;
                serverIn=null;
                serverSocket.close();
                serverSocket=null;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public byte[] reconstructBytes(String in){
        long start=System.currentTimeMillis();
        String[] strings = in.replace("[","").replace("]","").split(", ");
        byte[] reconstructed= new byte[strings.length];
        for(int i=0;i<reconstructed.length;i++){
            reconstructed[i]=Byte.parseByte(strings[i]);
        }
        long end=System.currentTimeMillis();
        System.out.println(end-start);
        return reconstructed;

    }

    public void changeColor(){
        Color temp=null;
        while (temp==null){
            temp=JColorChooser.showDialog(null,"Choose Color",drawColor);
        }
        drawColor=temp;

    }

    public String convertToUnprintable(String in){
        while (in.contains("\n")){
            in=in.substring(0,in.indexOf("\n"))+"~"+in.substring(in.indexOf("\n")+1);
        }
        return in;
    }

    public String convertFromUnprintable(String in){
        while (in.contains("~")){
            in=in.substring(0,in.indexOf("~"))+"\n"+in.substring(in.indexOf("~")+1);
        }
        return in;
    }
}
