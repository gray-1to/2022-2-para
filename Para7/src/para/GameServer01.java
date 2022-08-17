//学籍番号：20B30100
//氏名：伊藤悠馬
package para;

import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

import para.graphic.shape.Rectangle;
import para.graphic.shape.Attribute;
import para.graphic.shape.ShapeManager;
import para.graphic.shape.OrderedShapeManager;
import para.graphic.shape.Vec2;
import para.graphic.shape.MathUtil;
import para.graphic.shape.Shape;
import para.graphic.shape.Circle;
import para.graphic.shape.Digit;
import para.graphic.shape.CollisionChecker;
import para.graphic.target.TranslateTarget;
import para.graphic.target.TranslationRule;
import para.game.GameServerFrame;
import para.game.GameInputThread;
import para.game.GameTextTarget;

public class GameServer01{
  final Attribute wallattr = new Attribute(250,230,200,true,0,0,0);
  final Attribute ballattr = new Attribute(250,120,120,true,0,0,0);
  final Attribute scoreattr = new Attribute(60,60,60,true,0,0,0);
  final int MAXCONNECTION=2;
  final GameServerFrame gsf;
  final ShapeManager[] userinput;
  final ShapeManager[] wall;
  final ShapeManager[] deadwall;
  final ShapeManager[] blocks;
  final ShapeManager[] ballandscore;
  final Vec2[] pos;
  final Vec2[] vel;
  final int[] score;
  final CollisionChecker checker;

  private volatile boolean all_game_finish = false;
  private volatile boolean[] each_game_finish = new boolean[MAXCONNECTION];

  private GameServer01(){
    checker = new CollisionChecker();
    gsf = new GameServerFrame(MAXCONNECTION);
    userinput = new ShapeManager[MAXCONNECTION];
    wall = new OrderedShapeManager[MAXCONNECTION];
    deadwall = new OrderedShapeManager[MAXCONNECTION];
    blocks = new OrderedShapeManager[MAXCONNECTION];
    ballandscore = new ShapeManager[MAXCONNECTION];
    pos = new Vec2[MAXCONNECTION];
    vel = new Vec2[MAXCONNECTION];
    score = new int[MAXCONNECTION];
    for(int i=0;i<userinput.length;i++){
      userinput[i] = new ShapeManager();
      ballandscore[i] = new ShapeManager();
      wall[i] = new OrderedShapeManager();
      deadwall[i] = new OrderedShapeManager();
      blocks[i] = new OrderedShapeManager();
      pos[i] = new Vec2(i*350+150,200);
      vel[i] = new Vec2(0,0);
      each_game_finish[i] = false;
    }
  }

  public void start(){
    try{
      gsf.init();
    }catch(IOException ex){
      System.err.println(ex);
    }
    gsf.welcome();

    int gs=0;
    while(true){
      // System.out.println(gsf.getGameEnable());
      // System.out.println(gsf.getUserCounter());
      // System.out.println(finish);
      // if(gsf.getGameEnable()){
        gs = (gs+1)%350;
        GameInputThread git = gsf.queue.poll();

        if(git != null){
          int id = git.getUserID();
          init(id);
          startReceiver(git);
        }
        try{
          Thread.sleep(100);
        }catch(InterruptedException ex){
        }
        for(int i=0;i<MAXCONNECTION;i++){
          GameTextTarget out = gsf.getUserOutput(i);
          if(out != null){
            calcForOneUser(i);
            ballandscore[i].put(new Circle(i*10000+1, (int)pos[i].data[0],
                                  (int)pos[i].data[1], 5, ballattr));
            putScore(i,score[i]);
            out.gamerstate(gs); //Gamerの状態をクライアントに伝える
            distributeOutput(out);
          }else{
            //終了後の初期化処理
            userinput[i] = new ShapeManager();
            ballandscore[i] = new ShapeManager();
            wall[i] = new OrderedShapeManager();
            deadwall[i] = new OrderedShapeManager();
            blocks[i] = new OrderedShapeManager();
            pos[i] = new Vec2(i*350+150,200);
            vel[i] = new Vec2(0,0);
            each_game_finish[i] = false;
          }
        }
        //全員が終了したかの判定
        all_game_finish = true;
        for(int i=0;i<MAXCONNECTION;i++){
          if(!each_game_finish[i]){
            all_game_finish = false;
          }
        }
        // 全員終了時の処理
        if(all_game_finish){
            int min_id = -1, min_score = 1000;
            for(int i=0;i<MAXCONNECTION;i++){
              if(score[i] <= min_score){
                min_score = score[i];
                min_id = i;
              }
            }
            gsf.finish(min_id);
            for(int i=0;i<MAXCONNECTION;i++){
              each_game_finish[i] = false;
            }
            all_game_finish = false;
        }
    }
  }
    
  private void startReceiver(GameInputThread git){
    int id = git.getUserID();
    git.init(new TranslateTarget(userinput[id],
                    new TranslationRule(id*10000,new Vec2(id*350,0))),
             new ShapeManager[]{userinput[id],wall[id],deadwall[id],
                                blocks[id],ballandscore[id]}
             );
    git.start();
  }

  private void init(int id){
    wall[id].add(new Rectangle(id*10000+5, id*350+0, 0, 320, 20, wallattr));
    wall[id].add(new Rectangle(id*10000+6, id*350+0, 0, 20, 300, wallattr));
    wall[id].add(new Rectangle(id*10000+7, id*350+300,0, 20, 300, wallattr));
    // wall[id].add(new Rectangle(id*10000+8, id*350+0,281, 320, 20, wallattr));
    deadwall[id].add(new Rectangle(id*10000+8, id*350+0,281, 320, 20, wallattr));
    IntStream.range(0,33*20).forEach(n->{
        int x = n%33;
        int y = n/33;
        blocks[id].add(new Rectangle(id*10000+n,id*350+30+x*8,30+y*8,6,6,
                             new Attribute(250,100,250,true,0,0,0)));
      });
    // pos[id] = new Vec2(id*350+150,200);
    pos[id] = new Vec2(id*350+150,150);
    vel[id] = new Vec2(4,-12);
    score[id] = 0;
  }
  
  private void calcForOneUser(int id){
    float[] btime = new float[]{1.0f};
    float[] stime = new float[]{1.0f};
    float[] wtime = new float[]{1.0f};
    float time = 1.0f;
    while(0<time){
      btime[0] = time;
      stime[0] = time;
      wtime[0] = time;
      Vec2 tmpbpos = new Vec2(pos[id]);
      Vec2 tmpbvel = new Vec2(vel[id]);
      Vec2 tmpspos = new Vec2(pos[id]);
      Vec2 tmpsvel = new Vec2(vel[id]);
      Vec2 tmpwpos = new Vec2(pos[id]);
      Vec2 tmpwvel = new Vec2(vel[id]);
      Shape b=checker.check(userinput[id], tmpbpos, tmpbvel, btime);
      Shape s=checker.check(blocks[id], tmpspos, tmpsvel, stime);
      Shape w=checker.check(wall[id], tmpwpos, tmpwvel, wtime);
      Shape deadw=checker.check(deadwall[id], tmpwpos, tmpwvel, wtime);
      if( b != null && 
          (s == null || stime[0]<btime[0]) &&
          (w == null || wtime[0]<btime[0])){
        pos[id] = tmpbpos;
        vel[id] = tmpbvel;
        //ボードの反射を乱数に
        float v_length = vel[id].length();
        double rand_rad = Math.toRadians(Math.random()*120-60);
        vel[id] = new Vec2((float)(v_length*Math.cos(rand_rad)),
                           (float)(v_length*Math.sin(rand_rad)));
        time = btime[0];
      }else if(s != null){
        blocks[id].remove(s); // block hit!
        score[id]++;
        pos[id] = tmpspos;
        vel[id] = tmpsvel;
        time = stime[0];
      }else if(w != null){
        pos[id] = tmpwpos;
        vel[id] = tmpwvel;
        time = wtime[0];
      }else if(deadw != null){
        //得点を100消費してやり直し
        if(score[id]>=100){
          score[id] -= 100;
          vel[id] = new Vec2(vel[id].data[0],-vel[id].data[1]);
        }else{
          each_game_finish[id] = true;
          vel[id] = new Vec2(0,0);
        }
      }else{
        pos[id] = MathUtil.plus(pos[id], MathUtil.times(vel[id],time));
        time = 0;
      }
    }
  }

  private void putScore(int id, int score){
    int one = score%10;
    int ten = (score/10)%10;
    int hundred = (score/100)%10;
    ballandscore[id].put(new Digit(id*10000+2,id*300+250,330,20,one,scoreattr));
    ballandscore[id].put(new Digit(id*10000+3,id*300+200,330,20,ten,scoreattr));
    ballandscore[id].put(new Digit(id*10000+4,id*300+150,330,20,hundred,scoreattr));
  }
  
  private void distributeOutput(GameTextTarget out){
    if(out == null){
      return;
    }
    out.clear();
    for(int i=0;i<MAXCONNECTION;i++){
      if(gsf.getUserOutput(i)!=null){
        out.draw(wall[i]);
        out.draw(deadwall[i]);
        out.draw(blocks[i]);
        out.draw(userinput[i]);
        out.draw(ballandscore[i]);
      }
    }
    out.flush();
  }

  public static void main(String[] args){
    GameServer01 gs = new GameServer01();
    gs.start();
  }
}
