package para.graphic.shape;
import para.graphic.target.Target;

/** 
 * 円
 */
public class Blank extends Shape{
  /** 中心のx座標 */
  private int x;
  /** 中心のy座標 */
  private int y;
  /** 半径 */
  private int r;
  /** 属性 */
  private Attribute attr;

  /** 空白を生成する
   *  @param id 識別子
   *  @param x  中心のx座標
   *  @param y  中心のy座標
   */
  public Blank(int id, int x, int y){
    super(id, x, x, y, y);
    this.x =x;
    this.y = y;
    this.attr = null;
  }

  /** 属性を取得する */
  @Override
  public Attribute getAttribute(){
    return attr;
  }

  /** この円を出力する
   *  @param target 出力先
   */
  @Override
  public void draw(Target target){
  }

  public int getX(){
    return x;
  }

  public int getY(){
    return y;
  }
}
