// 学籍番号：20B30100
// 氏名：伊藤悠馬
package para;

import java.io.*;
import java.net.*;
import para.graphic.shape.*;
import para.graphic.target.*;
import para.graphic.parser.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/** カメラを起動し、サーバに送るデモ
 */
public class Main08{
  // final Target target;
  final static int PORTNO = 30000;
  final static int MAXCONNECTION = 3;
  /** メインメソッド
   * @param args args[0]は相手先のホスト
   */
  public static void main(String[] args){
    ShapeManager to_server_sm = new ShapeManager();
    final ShapeManager from_server_sm = new OrderedShapeManager();
    to_server_sm.put(new Camera(0,0,0));
    try(Socket s = new Socket(args[0],PORTNO)){
      PrintStream to_server_ps = new PrintStream(s.getOutputStream());
      Target server_target = new TextTarget(to_server_ps);
      //クライアント側の表示ウィンドウ用
      BufferedReader text_from_server = new BufferedReader(new InputStreamReader(s.getInputStream()));
      Target client_target = new JavaFXTarget("Client", 320 * MAXCONNECTION, 240);
      MainParser parser = new MainParser(client_target, from_server_sm);
      server_target.init();
      client_target.init();
      server_target.clear();
      client_target.clear();
      //クライアント側の表示更新
      // //クライアント側の動画情報の取得・保存
      new Thread(() -> {
        while(true){
          parser.parse(new Scanner(text_from_server));
          client_target.clear();
          client_target.draw(from_server_sm);
          client_target.flush();
          try{
            Thread.sleep(100);
          }catch(InterruptedException ex){
          }
        }
      }).start();

      while(true){
        server_target.draw(to_server_sm);
        server_target.flush();
        try{
          Thread.sleep(100);
        }catch(InterruptedException ex){
        }
        if(to_server_ps.checkError()){
          break;
        }
      }
    }catch(IOException ex){
      System.err.println(ex);
    }
  }
}
