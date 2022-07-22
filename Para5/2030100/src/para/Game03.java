// 学籍番号：20B30100
// 氏名：伊藤悠馬
package para;

import java.util.Scanner;
import java.util.stream.IntStream;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;
import para.game.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Game03 extends GameFrame{
  volatile Thread thread = null;
  final ShapeManager sm, wall, board, deadwall, scoreboard;
  Vec2 pos;
  Vec2 vel;
  int bpos;
  static final int WIDTH=1840;
  static final int HEIGHT=960;
  volatile int score;
  volatile int mybar_x;
  volatile int mybar_y;
  Attribute wallattr = new Attribute(250,230,200,true,0,0,0);
  ShapeManager camerasm;
  Stage stage;
  
  public Game03(){
    super(new JavaFXCanvasTarget(WIDTH, HEIGHT));
    title="BreakOut";
    sm = new OrderedShapeManager();
    wall = new OrderedShapeManager();
    board = new ShapeManager();
    deadwall = new OrderedShapeManager();
    scoreboard = new OrderedShapeManager();
    camerasm = new ShapeManager();
  }

  public void init(){
    sm.clear();
    wall.clear();
    board.clear();
    deadwall.clear();
    scoreboard.clear();

    wall.add(new Rectangle(0, 0, 0, 1840, 20, wallattr));
    wall.add(new Rectangle(1, 0, 0, 20, 960, wallattr));
    wall.add(new Rectangle(2, 1820, 0, 20, 960, wallattr));
    deadwall.add(new Rectangle(3, 0, 940, 1840, 20, wallattr));
    // 操作対象のバー
    // wall.add(new Rectangle(10,790, 800, 240, 20, wallattr));
    mybar_x = 790;
    mybar_y = 800;
    bpos = 150;
    pos = new Vec2(200, 250);
    // vel = new Vec2(16 * 10, 61 * 10);
    vel = new Vec2(4, 61 / 4.0f);
    vel.mul(0.1f);
    score = 0;
  }

  public void gamestart(int level){
    if(thread != null){
      return;
    }
    init();
    sm.clear();
    IntStream.range(0, level * 3 * level * 6).forEach(n -> {
      int x = n % (level * 6);
      int y = n / (level * 6);
      sm.add(new Rectangle(20 + n,
        30 + x * 1780 / level / 6, 30 + y * 450 / level / 3,
        1780 / level / 6 - 5, 450 / level / 3 -5,
        new Attribute(250, 100, 250, true, 0, 0, 0)));
    });
    CollisionChecker ccp;
    ccp = new CollisionCheckerParallel2(true);
    thread = new Thread(()->{
        pos = new Vec2(200,130);
        vel = new Vec2(2,8);
        bpos = 150;
        Attribute attr = new Attribute(150,150,150,true);
        camerasm.put(new Camera(0, 0, 0,attr));
        // board.put(new Rectangle(15000, bpos-40,225,80,10,attr));
        board.put(new Rectangle(10,mybar_x, mybar_y, 240, 20, wallattr));
        canvas.draw(camerasm);
        canvas.draw(sm);
        canvas.draw(board);
        float time;
        float[] btime = new float[]{1.0f};
        float[] stime = new float[]{1.0f};
        float[] wtime = new float[]{1.0f};
        float[] dtime = new float[] { 1.0f };
        boolean ball_alive = true;

        while(ball_alive){
          try{
            Thread.sleep(80);
          }catch(InterruptedException ex){
          }
          if(lefton==1){
            board.remove(10);
            mybar_x = mybar_x - 20;
            board.add(new Rectangle(10,mybar_x, mybar_y, 240, 20, wallattr));
          }
          else if(righton==1){
            board.remove(10);
            mybar_x = mybar_x + 20;
            board.add(new Rectangle(10,mybar_x, mybar_y, 240, 20, wallattr));
          }
          else if(upon==1){
            board.remove(10);
            mybar_y = mybar_y - 20;
            board.add(new Rectangle(10,mybar_x, mybar_y, 240, 20, wallattr));
          }
          else if(downon==1){
            board.remove(10);
            mybar_y = mybar_y + 20;
            board.add(new Rectangle(10,mybar_x, mybar_y, 240, 20, wallattr));
          }
          // CollisionChecker ccp = new CollisionCheckerParallel2(true);
          canvas.clear();
          canvas.draw(camerasm);
          canvas.drawCircle(10000,(int)pos.data[0],(int)pos.data[1],5,
                          new Attribute(0,0,0,true,0,0,0));
          canvas.draw(scoreboard);
          canvas.draw(sm);
          canvas.draw(wall);
          canvas.draw(deadwall);
          canvas.draw(board);
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
            Shape d = ccp.check(deadwall, tmpwpos, tmpwvel, dtime);
            if( b != null && 
                (s == null || stime[0]<btime[0]) &&
                (w == null || wtime[0]<btime[0])){
              pos = tmpbpos;
              vel = tmpbvel;
              time = btime[0];
            }else if(s != null){
              score = score + 1;
              sm.remove(s);
              pos = tmpspos;
              vel = tmpsvel;
              time = stime[0];
            }else if(w != null){
              pos = tmpwpos;
              vel = tmpwvel;
              time = wtime[0];
            } else if (d != null) {
              ball_alive = false;
              time = 0;
            }else{
              pos = MathUtil.plus(pos, MathUtil.times(vel,time));
              time = 0;
            }

            for (int i = 0; i < 5; i++) {
              scoreboard.remove(20 * i);
            }
            scoreboard.add(new Digit(0, 850, 750, 20, score / 10000, new Attribute(100, 100, 100, true, 0, 0, 0)));
            scoreboard.add(new Digit(20, 900, 750, 20, score / 1000 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
            scoreboard.add(new Digit(40, 950, 750, 20, score / 100 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
            scoreboard.add(new Digit(60, 1000, 750, 20, score / 10 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
            scoreboard.add(new Digit(80, 1050, 750, 20, score % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
            canvas.draw(scoreboard);
            try {
              Thread.sleep(10);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
        thread = null;
      });
    thread.start();
    // Thread scoreboard_thread = new Thread(() -> {
    //   while (ball_alive) {
    //     for (int i = 0; i < 5; i++) {
    //       scoreboard.remove(20 * i);
    //     }
    //     scoreboard.add(new Digit(0, 850, 750, 20, score / 10000, new Attribute(100, 100, 100, true, 0, 0, 0)));
    //     scoreboard.add(new Digit(20, 900, 750, 20, score / 1000 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
    //     scoreboard.add(new Digit(40, 950, 750, 20, score / 100 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
    //     scoreboard.add(new Digit(60, 1000, 750, 20, score / 10 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
    //     scoreboard.add(new Digit(80, 1050, 750, 20, score % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
    //     canvas.draw(scoreboard);
    //     try {
    //       Thread.sleep(10);
    //     } catch (InterruptedException e) {
    //       e.printStackTrace();
    //     }
    //   }
    // });
    // scoreboard_thread.start();
  }



  public static void main() {
    Game03 game03 = new Game03();
    game03.start(new Stage());
  }
}