/*
学籍番号：20B30100
氏名：伊藤悠馬
*/
package para.graphic.shape;
import java.util.*;

/** 描画順序をidの順とする図形集合
 */
public class OrderedShapeManager extends ShapeManager{

    public OrderedShapeManager(){
        super(new TreeSet<Shape>( new Comparator<Shape>(){
            @Override
            public int compare(Shape o1, Shape o2){
                return o2.id - o1.id;
            }
        }));
    }
}
