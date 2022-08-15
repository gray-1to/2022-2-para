//学籍番号:20B30100
//氏名:伊藤悠馬
package para.game;

import java.net.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.AbstractQueue;
import java.util.concurrent.ArrayBlockingQueue;
import para.graphic.parser.MainParser;
import para.graphic.target.Target;
import para.graphic.target.TextTarget;
import para.graphic.shape.ShapeManager;

public class GameServerFrame extends Thread{
  public static final int PORTNO=30001;
  public final AbstractQueue<GameInputThread> queue;
  private final GameTextTarget[] useroutput;
  public final ArrayList<GameInputThread> array; 

  private final int maxconnection;
  private int users;
  private ServerSocket ss=null;

  private boolean game_enable = false;
  
  public GameServerFrame(int maxconnection){
    this.maxconnection = maxconnection;
    queue = new ArrayBlockingQueue<GameInputThread>(maxconnection);
    useroutput = new GameTextTarget[maxconnection];
    array = new ArrayList<GameInputThread>(maxconnection);
    for(int i=0; i<maxconnection; i++){
      useroutput[i] = null;
    }
    users = 0;
  }

  public void init() throws IOException{
    ss = new ServerSocket(PORTNO);
  }

  public void welcome(){
    start();
  }
  
  public void run(){
    loop();
  }
  
  private void loop(){
    while(true){
      System.out.println(users);
      Socket s;
      synchronized(this){
        users++;
        if(users>2){
          game_enable = true;
        }
        while(maxconnection<users){ //ckeck why needed?
          try{
            wait();
          }catch(InterruptedException ex){
          }
        }
        game_enable = false;
      }
      try{
        s = ss.accept();
        int id;
        synchronized(this){
          id = decideUserID();
          useroutput[id] = new GameTextTarget(s.getOutputStream());
          InputStream is = s.getInputStream();
          GameInputThread th = new GameInputThread(is, id, this);
          array.add(th);
          queue.add(th);
        }
      }catch(IOException ex){
        System.err.print(ex);
      }
    }
  }

  public synchronized GameTextTarget getUserOutput(int id){
    return useroutput[id];
  }

  private synchronized int decideUserID(){
    for(int i=0;i<maxconnection;i++){
      if(useroutput[i]==null){
        return i;
      }
    }
    return -1;
  }
  
  public synchronized void removeUser(int num){
    users--;
    useroutput[num]=null;
    for(GameInputThread th : array){
      if(th.userID == num){
        array.remove(th);
        break;
      }
    }
    notifyAll();
  }

  public synchronized int getUserCounter(){
    return users;
  }

  public synchronized boolean getGameEnable(){
    return  game_enable;
  }
}
