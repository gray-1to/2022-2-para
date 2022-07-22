// 学籍番号：20B30100
// 氏名：伊藤悠馬
package para;

import java.util.Scanner;
import java.util.stream.IntStream;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;

import para.graphic.parser.MainParser;
import para.graphic.shape.Attribute;
import para.graphic.shape.CollisionChecker;
import para.graphic.shape.CollisionCheckerParallel2;
import para.graphic.shape.Digit;
import para.graphic.shape.MathUtil;
import para.graphic.shape.OrderedShapeManager;
import para.graphic.shape.Rectangle;
import para.graphic.shape.Shape;
import para.graphic.shape.ShapeManager;
import para.graphic.shape.Vec2;
import para.graphic.target.JavaFXTarget;


import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Main11 {
  // final JavaFXTarget jf = new JavaFXTarget("Main11", 1840, 960);
  final JavaFXTarget jf = new JavaFXTarget("Main11", 1840, 960, 1840, 990);
  final MainParser ps;
  final ShapeManager sm, wall, scoreboard, deadwall;
  Attribute wallattr = new Attribute(250, 230, 200, true, 0, 0, 0);
  Thread thread;
  final static String data = "shape 10 Circle 80 60 20 Attribute Color 225 105 0 Fill true\n" +
      "shape 11 Circle 1760 60 20 Attribute Color 225 105 0 Fill true\n" +
      "shape 12 Circle 80 900 20 Attribute Color 225 105 0 Fill true\n" +
      "shape 13 Circle 1760 900 20 Attribute Color 225 105 0 Fill true\n";
  Vec2 pos;
  Vec2 vel;
  volatile int bpos;
  final String selector;
  volatile int score;
  volatile int mybar_x;
  volatile int mybar_y;

  public Main11(String selector) {
    this.selector = selector;
    sm = new OrderedShapeManager();
    wall = new OrderedShapeManager();
    scoreboard = new OrderedShapeManager();
    deadwall = new OrderedShapeManager();
    ps = new MainParser(jf, sm);
  }

  public void init(){
    sm.clear();
    wall.clear();
    scoreboard.clear();
    deadwall.clear();
    ps.parse(new Scanner(data));

    wall.add(new Rectangle(0, 0, 0, 1840, 20, wallattr));
    wall.add(new Rectangle(1, 0, 0, 20, 960, wallattr));
    wall.add(new Rectangle(2, 1820, 0, 20, 960, wallattr));
    deadwall.add(new Rectangle(3, 0, 940, 1840, 20, wallattr));
    // 操作対象のバー
    wall.add(new Rectangle(10,790, 800, 240, 20, wallattr));
    mybar_x = 790;
    mybar_y = 800;
    bpos = 150;
    pos = new Vec2(200, 250);
    // vel = new Vec2(16 * 10, 61 * 10);
    vel = new Vec2(4, 61 / 4.0f);
    vel.mul(0.1f);
    score = 0;
  }

  public void start(String args0, int level) {
    IntStream.range(0, level * 3 * level * 6).forEach(n -> {
      int x = n % (level * 6);
      int y = n / (level * 6);
      sm.add(new Rectangle(20 + n,
        30 + x * 1780 / level / 6, 30 + y * 450 / level / 3,
        1780 / level / 6 - 5, 450 / level / 3 -5,
        new Attribute(250, 100, 250, true, 0, 0, 0)));
    });
    Spinner<Integer> spinner = new Spinner<Integer>(1,4,1);
    spinner.setPrefHeight(30);
    Button button = new Button("start");
    button.setPrefHeight(30);
    button.setPrefWidth(100);
    button.setOnAction(ev->{
      this.init();
      this.start(args0, spinner.getValue());
      });
    HBox hbox = new HBox();
    hbox.getChildren().add(spinner);
    hbox.getChildren().add(button);
    hbox.relocate(920, 960);
    System.out.println(hbox.getAlignment());
    jf.addContents(hbox);
    jf.show();
    CollisionChecker ccp;
    switch(selector){
    case "SINGLE":
    ccp = new CollisionCheckerParallel2(false);
    break;
    case "PARALLEL":
    ccp = new CollisionCheckerParallel2(true);
    break;
    case "POOL":
    ccp = new CollisionCheckerParallel3(20);
    break;
    default:
    ccp = new CollisionCheckerParallel2(false);
    }
    thread = new Thread(new Runnable() {
      public void run() {
        int j = 0;
        float time;
        float[] stime = new float[] { 1.0f };
        float[] wtime = new float[] { 1.0f };
        float[] dtime = new float[] { 1.0f };
        long startTimeMS = System.currentTimeMillis();
        long count = -1;
        boolean ball_alive = true;
        while (ball_alive) {
          count++;
          if (count == 100) {
            long endTimeMS = System.currentTimeMillis();
            System.out.println((endTimeMS - startTimeMS) + "msec");
            // System.exit(0);
          }
          j = (j + 2) % 120;
          jf.clear();
          jf.drawCircle(1000, (int) pos.data[0], (int) pos.data[1], 5,
              new Attribute(0, 0, 0, true, 0, 0, 0));
          jf.draw(sm);
          jf.draw(wall);
          jf.draw(deadwall);
          jf.draw(scoreboard);
          jf.flush();
          time = 1.0f;
          while (0 < time) {
            stime[0] = time;
            wtime[0] = time;
            dtime[0] = time;
            Vec2 tmpspos = new Vec2(pos);
            Vec2 tmpsvel = new Vec2(vel);
            Vec2 tmpwpos = new Vec2(pos);
            Vec2 tmpwvel = new Vec2(vel);
            synchronized (sm) {
              Shape s = ccp.check(sm, tmpspos, tmpsvel, stime);
              Shape w = ccp.check(wall, tmpwpos, tmpwvel, wtime);
              Shape d = ccp.check(deadwall, tmpwpos, tmpwvel, dtime);
              if (s != null) {
                // System.out.println("shape removed : " + s.getID());
                sm.remove(s);
                score++;
                pos = tmpspos;
                vel = tmpsvel;
                time = stime[0];
              } else if (w != null) {
                // System.out.println("wall touched");
                pos = tmpwpos;
                vel = tmpwvel;
                time = wtime[0];
              } else if (d != null) {
                System.out.println("deadwall touched");
                ball_alive = false;
                time = 0;
              } else {
                // System.out.println("no collision");
                pos = MathUtil.plus(pos, MathUtil.times(vel, time));
                time = 0;
              }
            }

          }
        }
      }
    });
    thread.start();
    new Thread(() -> {
      while (true) {
        for (int i = 0; i < 5; i++) {
          scoreboard.remove(20 * i);
        }
        scoreboard.add(new Digit(0, 850, 750, 20, score / 10000, new Attribute(100, 100, 100, true, 0, 0, 0)));
        scoreboard.add(new Digit(20, 900, 750, 20, score / 1000 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
        scoreboard.add(new Digit(40, 950, 750, 20, score / 100 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
        scoreboard.add(new Digit(60, 1000, 750, 20, score / 10 % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
        scoreboard.add(new Digit(80, 1050, 750, 20, score % 10, new Attribute(100, 100, 100, true, 0, 0, 0)));
        jf.draw(scoreboard);
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

    jf.addKeyPressHandler(ev -> {
      if(ev.getCode() == KeyCode.F){
        wall.remove(10);
        mybar_x = mybar_x - 20;
        wall.add(new Rectangle(10,mybar_x, 800, 240, 20, wallattr));
      }
      else if(ev.getCode() == KeyCode.J){
        wall.remove(10);
        mybar_x = mybar_x + 20;
        wall.add(new Rectangle(10,mybar_x, 800, 240, 20, wallattr));
      }
    });

  }

  public void ready(String args0){
    Spinner<Integer> spinner = new Spinner<Integer>(1,4,1);
    spinner.setPrefHeight(30);
    Button button = new Button("start");
    button.setPrefHeight(30);
    button.setPrefWidth(100);
    button.setOnAction(ev->{
      this.start(args0, spinner.getValue());
      });
    HBox hbox = new HBox();
    hbox.getChildren().add(spinner);
    hbox.getChildren().add(button);
    hbox.relocate(920, 960);
    System.out.println(hbox.getAlignment());
    jf.addContents(hbox);
    jf.init();
  }

  public static void main(String[] args) {
    Main11 main11 = new Main11(args[0]);
    main11.init();
    main11.ready(args[0]);
  }
}
