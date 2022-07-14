// 学籍番号：20B30100
// 氏名：伊藤悠馬
package para;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.MainParser;

import java.util.concurrent.*;

/** クライアントからの通信を受けて描画するサーバプログラム。
 * 監視ポートは30000番
 */
public class Main07{
  final public int PORTNO=30000;
  final int MAXCONNECTION=3;
  final Target target;
  final ShapeManager[] sms;
  final ServerSocket ss;
  ExecutorService exec; // added
  //Executor exec; // added
  volatile private boolean[] usedSms;

  /** 受け付け用ソケットを開くこと、受信データの格納場所を用意すること
   * を行う
   */
  public Main07(){
    target = new JavaFXTarget("Server", 320*MAXCONNECTION, 240);
    //target = new TextTarget(System.out);
    ServerSocket tmp=null;
    exec = Executors.newFixedThreadPool(MAXCONNECTION);
    try{
      tmp = new ServerSocket(PORTNO);
    }catch(IOException ex){
      System.err.println(ex);
      System.exit(1);
    }
    ss = tmp;
    sms = new ShapeManager[MAXCONNECTION];
    for(int i=0;i<MAXCONNECTION;i++){
      sms[i] = new OrderedShapeManager();
    }
    usedSms = new boolean[MAXCONNECTION];
    for(int i=0;i<MAXCONNECTION;i++){
      usedSms[i] = false;
    }
  }

  /** 受け付けたデータを表示するウィンドウの初期化とそこに受信データを表示するスレッドの開始
   */
  public void init(){
    target.init();
    target.clear();
    target.flush();
    new Thread(()->{
        while(true){
          target.clear();
          for(ShapeManager sm: sms){
            synchronized(sm){
              target.draw(sm);
            }
          }
          target.flush();
          try{
            Thread.sleep(100);
          }catch(InterruptedException ex){
          }
        }
    }).start();
  }

  /** 受信の処理をする
   */
  public void start(){
    int i=0;
    while(true){
      try{
        Socket s = ss.accept();
        while(usedSms[i]){
          // System.err.println(i + " is used");
          i=(i+1)%MAXCONNECTION;
        }
        usedSms[i] = true;
        exec.execute(new MyThread(s, sms[i], i));
        i=(i+1)%MAXCONNECTION;
      }catch(IOException ex){
        System.err.print(ex);
      }
    }
  }

  class MyThread extends Thread{
    final Socket s;
    ShapeManager sm;
    int i;

    public MyThread(Socket s, ShapeManager sm, int i){
      this.s = s;
      this.sm = sm;
      this.i = i;
    }

    public void run(){
      try{
        BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
        ShapeManager dummy = new ShapeManager();
        sm.clear();
        sm.put(new Rectangle(10000*i,320*i,0,320,240,
                                   new Attribute(0,0,0,true)));
        MainParser parser
          = new MainParser(new TranslateTarget(sm,
                            new TranslationRule(10000*i, new Vec2(320*i,0))),
                            dummy);
        parser.parse(new Scanner(r));
        usedSms[i] = false;
      }catch(IOException ex){
        System.err.print(ex);
      }
    }
  }

  public static void main(String[] args){
    Main07 m = new Main07();
    m.init();
    m.start();
  }
}