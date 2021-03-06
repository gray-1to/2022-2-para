package para;
import para.graphic.shape.*;
import para.graphic.target.*;

/** 同心円を描画するデモ */
public class Main04{
  /** メインメソッド
   * @param args このプログラムでは値は使用されません
   */
  public static void main(String[] args){
    // ShapeManager sm = new ShapeManager();
    ShapeManager sm = new OrderedShapeManager();
    Target target;
    target = new JavaFXTarget("OrderedDisplay");
    // target = new TextTarget(System.out);
    target.init();
    target.clear();
    target.flush();
    while(true){
      for(int i=55;i<256;i=i+50){
        Shape s = new Circle(i,100,100,i,
                             new Attribute(255-i, 255-i, i, true));
        sm.add(s);
        target.draw(sm);
        target.flush();
        try{
          Thread.sleep(300);
        }catch(InterruptedException e){
        }
      }
      target.clear();
      sm.clear();
    }
  }
}
