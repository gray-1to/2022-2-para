// 20B30100
// 伊藤悠馬

package para.paint;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.canvas.*;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.beans.value.ObservableValue;

/**
 * JavaFX お絵描きアプリケーションのメインクラス
 */
public class Paint extends Application{
  Canvas canvas;
  GraphicsContext gc;
  /** 直前のポインタのx座標 */
  double oldx;
  /** 直前のポインタのy座標 */
  double oldy;
  /** 描画領域の大きさ */
  final int SIZE=600;
  Button clear;
  Button reverse;
  float r_slider_x=0/255;
  float g_slider_x=0/255;
  float b_slider_x=255/255;
  float opacity_slider_x=1;
  float width_slider_x=4;
  Shape rect;

  /**
   * お絵描きプログラムの準備をして、ウィンドウを開きます
   */
  public void start(Stage stage){
    Group group = new Group();
    canvas = new Canvas(SIZE,SIZE);
    gc = canvas.getGraphicsContext2D();
    drawShapes(gc);
    canvas.setOnMousePressed(ev->{
        oldx = ev.getX();
        oldy = ev.getY();
      });

    Slider sliderr = new Slider(0, 1, 0);
    Slider sliderg = new Slider(0, 1, 0);
    Slider sliderb = new Slider(0, 1, 1);
    Slider slider_opacity = new Slider(0, 1, 1);
    Slider slider_width = new Slider(0, 50, 4);

    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                            new EventHandler<MouseEvent>(){
                              public void handle(MouseEvent ev){
                                gc.strokeLine(oldx, oldy, ev.getX(), ev.getY());
                                oldx = ev.getX();
                                oldy = ev.getY();
                                r_slider_x = (float)sliderr.getValue();
                                g_slider_x = (float)sliderg.getValue();
                                b_slider_x = (float)sliderb.getValue();
                                opacity_slider_x = (float)slider_opacity.getValue();
                                width_slider_x = (float)slider_width.getValue();
                                gc.setStroke(new Color(r_slider_x, g_slider_x, b_slider_x, opacity_slider_x));
                                gc.setLineWidth(width_slider_x);
                              }
                            });
    clear = new Button("clear");
    clear.setOnAction(e->{
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0,SIZE,SIZE);});

    reverse = new Button("色反転");
    reverse.setOnAction(e->{
      sliderr.setValue(1 - r_slider_x);
      sliderg.setValue( 1 - g_slider_x);
      sliderb.setValue(1- b_slider_x);
    });

    BorderPane bp = new BorderPane();
    VBox vb = new VBox();
    HBox bv_clear = new HBox();
    sliderr.valueProperty().addListener((ObservableValue<? extends Number> ov,
                                          Number oldv, Number nv)->{
                                r_slider_x = (float)sliderr.getValue();
                                g_slider_x = (float)sliderg.getValue();
                                b_slider_x = (float)sliderb.getValue();
                                rect.setFill( new Color(r_slider_x, g_slider_x, b_slider_x, opacity_slider_x) );
      });
    sliderg.valueProperty().addListener((ObservableValue<? extends Number> ov,
                                          Number oldv, Number nv)->{
                                r_slider_x = (float)sliderr.getValue();
                                g_slider_x = (float)sliderg.getValue();
                                b_slider_x = (float)sliderb.getValue();
                                rect.setFill( new Color(r_slider_x, g_slider_x, b_slider_x, opacity_slider_x) );
      });
    sliderb.valueProperty().addListener((ObservableValue<? extends Number> ov,
                                          Number oldv, Number nv)->{
                                r_slider_x = (float)sliderr.getValue();
                                g_slider_x = (float)sliderg.getValue();
                                b_slider_x = (float)sliderb.getValue();
                                rect.setFill( new Color(r_slider_x, g_slider_x, b_slider_x, opacity_slider_x) );
      });
    vb.setAlignment(Pos.CENTER);
    vb.getChildren().add(sliderr);
    vb.getChildren().add(sliderg);
    vb.getChildren().add(sliderb);
    vb.getChildren().add(slider_opacity);
    vb.getChildren().add(slider_width);

    rect  = new Rectangle( 25 , 25 );
    rect.setFill( new Color(r_slider_x, g_slider_x, b_slider_x, opacity_slider_x) );
    bv_clear.getChildren().add(clear);
    bv_clear.getChildren().add(rect);

    vb.getChildren().add(bv_clear);
    vb.getChildren().add(reverse);
    bp.setTop(vb);
    bp.setCenter(canvas);
    Scene scene = new Scene(bp);
    stage.setScene(scene);
    stage.setTitle("JavaFX Draw");
    stage.show();
  }

  /**
   * 初期化メソッド、startメソッドの呼び出され方とは異なる呼び出され方をする。必要ならば定義する
   */
  public void init(){
  }

  /**
   * 図形を描きます。
   * 図形描画の実装サンプルです
   */
  private void drawShapes(GraphicsContext gc) {
    gc.setFill(Color.WHITE);
    gc.fillRect(0,0,SIZE,SIZE);
    gc.setFill(Color.GREEN);
    gc.setStroke(new Color(r_slider_x, g_slider_x, b_slider_x,opacity_slider_x));
    gc.setLineWidth(4);
    gc.strokeLine(40, 10, 10, 40);
    gc.fillOval(60, 10, 30, 30);
    gc.strokeOval(110, 10, 30, 30);
    gc.fillRoundRect(160, 10, 30, 30, 10, 10);
  }
}
