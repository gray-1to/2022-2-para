package para;

import java.util.Scanner;
import java.util.stream.IntStream;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;

public class Game03 extends GameFrame{
  volatile Thread thread;
  final ShapeManager sm, wall, board;
  Vec2 pos;
  Vec2 vel;
  int bpos;
  static final int WIDTH=320;
  static final int HEIGHT=660;
  
  public Game03(){
    super(new JavaFXCanvasTarget(WIDTH, HEIGHT));
    title="BreakOut";
    sm = new OrderedShapeManager();
    wall = new OrderedShapeManager();
    board = new ShapeManager();
    Attribute wallattr = new Attribute(250,230,200,true,0,0,0);
    wall.add(new Rectangle(0, 0, 0, 320, 20, wallattr));
    wall.add(new Rectangle(1, 0, 0, 20, 300, wallattr));
    wall.add(new Rectangle(2, 300,0, 20, 300, wallattr));
    wall.add(new Rectangle(3, 0,281, 320, 20, wallattr));
    //    wall.add(new Rectangle(3, 0,281, 120, 20, wallattr));
    //    wall.add(new Rectangle(4, 200,281, 120, 20, wallattr));
  }

  public void gamestart(int v){
    if(thread != null){
      return;
    }
    sm.clear();
    IntStream.range(0,65*30).forEach(n->{
        int x = n%65;
        int y = n/65;
        sm.add(new Rectangle(10+n,30+x*4,50+y*4,3,3,
                             new Attribute(250,100,250,true,0,0,0)));
      });
    thread = new Thread(()->{
        pos = new Vec2(200,130);
        vel = new Vec2(2,8);
        bpos = 150;
        Attribute attr = new Attribute(150,150,150,true);
        board.put(new Camera(0, 0, 320,attr));
        board.put(new Rectangle(15000, bpos-40,225,80,10,attr));
        canvas.draw(board);
        canvas.draw(sm);
        float time;
        float[] btime = new float[]{1.0f};
        float[] stime = new float[]{1.0f};
        float[] wtime = new float[]{1.0f};

        while(true){
          try{
            Thread.sleep(80);
          }catch(InterruptedException ex){
          }
          if((lefton ==1 || righton ==1)){
            bpos =bpos-8*lefton+8*righton;
            if(bpos<35){
              bpos = 35;
            }else if(285<bpos){
              bpos =285;
            }
            board.put(new Rectangle(15000, bpos-40,225,80,10,attr));
          }
          CollisionChecker ccp = new CollisionCheckerParallel2(true);
          canvas.clear();
          canvas.draw(board);
          canvas.drawCircle(10000,(int)pos.data[0],(int)pos.data[1],5,
                          new Attribute(0,0,0,true,0,0,0));
          canvas.draw(sm);
          canvas.draw(wall);
          canvas.flush();
          time =1.0f;
          while(0<time){
            btime[0] = time;
            stime[0] = time;
            wtime[0] = time;
            Vec2 tmpbpos = new Vec2(pos);
            Vec2 tmpbvel = new Vec2(vel);
            Vec2 tmpspos = new Vec2(pos);
            Vec2 tmpsvel = new Vec2(vel);
            Vec2 tmpwpos = new Vec2(pos);
            Vec2 tmpwvel = new Vec2(vel);
            Shape b=ccp.check(board, tmpbpos, tmpbvel, btime);
            Shape s=ccp.check(sm, tmpspos, tmpsvel, stime);
            Shape w=ccp.check(wall, tmpwpos, tmpwvel, wtime);
            if( b != null && 
                (s == null || stime[0]<btime[0]) &&
                (w == null || wtime[0]<btime[0])){
              pos = tmpbpos;
              vel = tmpbvel;
              time = btime[0];
            }else if(s != null){
              sm.remove(s);
              pos = tmpspos;
              vel = tmpsvel;
              time = stime[0];
            }else if(w != null){
              pos = tmpwpos;
              vel = tmpwvel;
              time = wtime[0];
            }else{
              pos = MathUtil.plus(pos, MathUtil.times(vel,time));
              time = 0;
            }
          }
        }
      });
    thread.start();
  }
}
