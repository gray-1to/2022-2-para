package para.graphic.shape;
import java.util.*;

/** 描画順序をidの順とする図形集合
 */
public class OrderedShapeManager extends ShapeManager{
  /** 集合内の図形を出力する．
   *  @param target 出力装置
   */
  public synchronized void draw(Target target){
    for(Shape s:data){
      s.draw(target); 
    }
  }
}
