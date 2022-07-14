// 学籍番号：20B30100
// 氏名：伊藤悠馬
package para;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import para.graphic.parser.MainParser;
import para.graphic.shape.Attribute;
import para.graphic.shape.OrderedShapeManager;
import para.graphic.shape.Rectangle;
import para.graphic.shape.ShapeManager;
import para.graphic.shape.Vec2;
import para.graphic.target.JavaFXTarget;
import para.graphic.target.Target;
import para.graphic.target.TextTarget;
import para.graphic.target.TranslateTarget;
import para.graphic.target.TranslationRule;

/**
 * クライアントからの通信を受けて描画するサーバプログラム。
 * 監視ポートは30000番
 */
public class Main09 {
  final public int PORTNO = 30000;
  final int MAXCONNECTION = 3;
  final Target target;
  final ShapeManager[] sms;
  final ServerSocket ss;
  ExecutorService exec; // added
  // Executor exec; // added
  volatile private boolean[] usedSms;
  TextTarget[] client_text_targets;

  /**
   * 受け付け用ソケットを開くこと、受信データの格納場所を用意すること
   * を行う
   */
  public Main09() {
    target = new JavaFXTarget("Server", 320 * MAXCONNECTION, 240);
    // target = new TextTarget(System.out);
    ServerSocket tmp = null;
    exec = Executors.newFixedThreadPool(MAXCONNECTION);
    try {
      tmp = new ServerSocket(PORTNO);
    } catch (IOException ex) {
      System.err.println(ex);
      System.exit(1);
    }
    ss = tmp;
    sms = new ShapeManager[MAXCONNECTION];
    for (int i = 0; i < MAXCONNECTION; i++) {
      sms[i] = new OrderedShapeManager();
    }
    usedSms = new boolean[MAXCONNECTION];
    for (int i = 0; i < MAXCONNECTION; i++) {
      usedSms[i] = false;
    }
    client_text_targets = new TextTarget[MAXCONNECTION];
    for(int i=0;i<MAXCONNECTION;i++){
      client_text_targets[i] = new TextTarget(System.out);
    }
  }

  /**
   * 受け付けたデータを表示するウィンドウの初期化とそこに受信データを表示するスレッドの開始
   */
  public void init() {
    target.init();
    target.clear();
    target.flush();
    for(int i=0;i<MAXCONNECTION;i++){
      client_text_targets[i].init();
      client_text_targets[i].clear();
      client_text_targets[i].flush();
    }
    new Thread(() -> {
      while (true) {
        target.clear();
        for(int i=0;i<MAXCONNECTION;i++){
          client_text_targets[i].clear();
        }
        for (ShapeManager sm : sms) {
          synchronized (sm) {
            target.draw(sm);
            for (int i=0;i<MAXCONNECTION;i++) {
              client_text_targets[i].draw(sm);
            }
          }
        }
        target.flush();
        for(TextTarget client_text_target :client_text_targets){
          client_text_target.flush();
        }

        try {
          Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
      }
    }).start();
  }

  /**
   * 受信の処理をする
   */
  public void start() {
    int i = 0;
    while (true) {
      try {
        Socket s = ss.accept();
        while (usedSms[i]) {
          // System.err.println(i + " is used");
          i = (i + 1) % MAXCONNECTION;
        }
        usedSms[i] = true;
        client_text_targets[i] = new TextTarget(s.getOutputStream());
        exec.execute(new MyThread(s, sms[i], i));
        i = (i + 1) % MAXCONNECTION;
      } catch (IOException ex) {
        System.err.print(ex);
      }
    }
  }

  class MyThread extends Thread {
    final Socket s;
    ShapeManager sm;
    int i;

    public MyThread(Socket s, ShapeManager sm, int i) {
      this.s = s;
      this.sm = sm;
      this.i = i;
    }

    public void run() {
      try {
        BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
        ShapeManager dummy = new ShapeManager();
        sm.clear();
        sm.put(new Rectangle(10000 * i, 320 * i, 0, 320, 240,
            new Attribute(0, 0, 0, true)));
        MainParser parser = new MainParser(new TranslateTarget(sm,
            new TranslationRule(10000 * i, new Vec2(320 * i, 0))),
            dummy);
        parser.parse(new Scanner(r));
        usedSms[i] = false;
      } catch (IOException ex) {
        System.err.print(ex);
      }
    }
  }

  public static void main(String[] args) {
    Main09 m = new Main09();
    m.init();
    m.start();
  }
}