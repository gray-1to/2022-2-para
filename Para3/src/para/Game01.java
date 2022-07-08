package para;

import java.util.Scanner;
import java.util.Random;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;

/** モグラたたきゲームの雛形
 */
public class Game01 extends GameFrame{
  final static int WIDTH=400;
  final static int HEIGHT=700;
  final int MCOUNT=9;
  final int XCOUNT=3;
  Target inputside;
  Target outputside;
  Thread thread;
  ShapeManager osm;
  ShapeManager ism;
  private long prev; 
  Random rand;
  private int last;
  private  int[] slot;
  Attribute mogattr;
  private int score;
  private boolean continueState;
  
  public Game01(){
    super(new JavaFXCanvasTarget(WIDTH, HEIGHT));
    canvas.init();
    title = "Mole";
    outputside = canvas;
    inputside = canvas;
    osm = new ShapeManager();
    ism = new ShapeManager();
    rand = new Random(System.currentTimeMillis());
    slot = new int[MCOUNT]; 
    mogattr = new Attribute(158,118,38);
  }

  public void gamestart(int v){
    if(thread != null){
      return;
    }
    score = 0;
    continueState = true;
    thread = new Thread(()->{
        Attribute attr = new Attribute(250,80,80);
        while(continueState){
          try{
            Thread.sleep(80);
          }catch(InterruptedException ex){
          }
          SynchronizedPoint p = xy.copy();
          if(prev != p.getTime()){
            prev = p.getTime();
            ism.put(new Circle(v, (int)p.getXY()[0], (int)p.getXY()[1],
                               20, attr));
            Shape s = InsideChecker.check(osm,
                                          new Vec2(p.getXY()[0], p.getXY()[1]));
            if(s != null){
              slot[(s.getID()-10)/10]=0;
              System.out.println(p.getXY()[0]+" "+p.getXY()[1]+" "+p.getTime());
              score = score + 1;
            }
          }else if(300 < System.currentTimeMillis()-prev){
            ism.remove(v);
          }
          if(score>4){
            continueState = false;
          }
          inputside.clear();
          ism.put(new Digit(100, 250, 350, 30, score%10,  new Attribute(200,200,200)));
          ism.put(new Digit(101, 180, 350, 30, (score%100)/10,  new Attribute(200,200,200)));
          ism.put(new Digit(102, 110, 350, 30, score/100,  new Attribute(200,200,200)));
          mole();
          inputside.draw(ism);
          inputside.flush();
        }
        inputside.clear();
        ism.put(new Digit(100, 250, 350, 30, score%10,  new Attribute(200,200,200)));
        ism.put(new Digit(101, 180, 350, 30, (score%100)/10,  new Attribute(200,200,200)));
        ism.put(new Digit(102, 110, 350, 30, score/100,  new Attribute(200,200,200)));
        mole();
        inputside.draw(ism);
        inputside.flush();
        System.out.println("FINISH!!!");
      });
    thread.start();
  }

  private void mole(){
    int appear = rand.nextInt(40);
    if(30<appear){
      int head = rand.nextInt(MCOUNT-1)+1;
      int duration = rand.nextInt(600)+200;

      if(slot[(last+head)%MCOUNT] <=0){
        slot[(last+head)%MCOUNT] = duration;
      }
    }
    for(int i=0;i<MCOUNT;i++){
      slot[i]=slot[i]-10;
    }
    for(int i=0;i<MCOUNT;i++){
      if(0<slot[i]){
        // osm.put(new Circle(10+i*10, (i%XCOUNT)*130+60, (i/XCOUNT)*130+60,
        //                    slot[i]/25,mogattr));
        Garden.setMole(10+i*10, (i%XCOUNT)*130+60, (i/XCOUNT)*130+60,
                      slot[i]/25,osm);
      }else{
        // osm.remove(10+i*10);
        Garden.removeMole(10+i*10, osm);
      }
    }
    inputside.draw(osm);
  }
}
