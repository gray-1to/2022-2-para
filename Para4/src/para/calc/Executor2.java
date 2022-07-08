/*
学籍番号：20B30100
氏名：伊藤悠馬
*/
package para.calc;

import javafx.scene.control.*;
import javafx.application.Platform;

public class Executor2 extends ExecutorBase implements Executor{
  Label label;
  MyThread last;
  public Executor2(Label label){
    super();
    this.label = label;
  }

  public void writeState(String state){
    System.err.println(Thread.currentThread().getName());//hint
    System.out.print(state);
        label.setText(state);
    Platform.runLater(()->{
        // label.setText(state);
        System.err.println(Thread.currentThread().getName());//hint
      });
  }

  synchronized public String operation(String data){
    MyThread th = new MyThread(data, last);
    last = th;
    last.start();
    return result;
  }

  class MyThread extends Thread{
    MyThread last;
    String data;
    MyThread(String data, MyThread last){
      this.data = data;
      this.last = last;
    }

    public void run(){
      if(last != null){
        try{
          last.join();
        }catch(InterruptedException ex){
        }
      }
      init(data);

      boolean isSuccess=true;
      while(isSuccess && s.hasNext()){
        isSuccess = onestep();
      }
      // 最終的な結果を表示する（このままだとエラーが発生するので修正が必要）
      // label.setText("<<  "+result+"  >>");
      
      try{
        Thread.sleep(2000);
      }catch(InterruptedException ex){
      }
    }
  }
}