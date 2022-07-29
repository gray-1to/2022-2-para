// 学籍番号：20B30100
// 氏名：伊藤悠馬
package para;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ObservableValue;

import para.graphic.target.*;
import para.graphic.shape.*;
import para.graphic.parser.*;


/**
 * スライダにより残像効果が変わるプログラム
 */
public class Main14 extends Application {
  final Thread thread;
  final JavaFXCanvasTarget jfc;
  final TargetDelayFilter filter;
  final Target target;
  final ShapeManager sm;
  volatile int value;
  Slider slider = new Slider(0, 1.0, 0.2);
  volatile float slider_value = 0.2f;
  final ShapeManager after_circle_sm;
  // volatile int circle_x;

  public Main14() {
    sm = new OrderedShapeManager();
    jfc = new JavaFXCanvasTarget(360, 280);
    after_circle_sm = new OrderedShapeManager();

    // filter = new TargetDelayFilter(jfc, "donothing.cl", "DoNothing");
    filter = new TargetDelayFilter(jfc, "delay.cl", "Delay");
    target = new TargetRecorder("recorddelay", filter);
    target.init();
    target.clear();
    sm.add(new Camera(0, 20, 20));
    thread = new Thread(new Runnable() {
      public void run() {
        int i=0;
        while (true) {
          // System.out.println(Thread.currentThread().getName());

          // synchronized(slider){
            // sm.put(new Circle(30, (int)(slider.getValue()*320), 200, 60, new Attribute(255, 0, 0, true)));
            sm.put(new Circle(30, i, 200, 60, new Attribute(255, 0, 0, true)));
            i=(i+10)%320;
            target.draw(sm);
            // sm.put(new Circle(31, (int)(slider.getValue()*320), 200, 60, new Attribute(0, 0, 255, true)));
            // target.draw(sm);            
          // }

          target.flush();
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
          }
        }
      }
    });
  }

  public void start(Stage stage) {
    BorderPane pane = new BorderPane();
    pane.setCenter(jfc);
    pane.setBottom(slider);
    slider.valueProperty().addListener(
        (ObservableValue<? extends Number> ov,
            Number old_val, Number new_val) -> {
          // System.out.println(Thread.currentThread().getName());
          slider_value = (float) slider.getValue();
          filter.setParameter(slider_value);
        });
    Scene scene = new Scene(pane);
    stage.setTitle("DelaySlider");
    stage.setScene(scene);
    stage.setOnCloseRequest(ev -> {
      System.exit(0);
    });
    stage.sizeToScene();
    stage.show();
    thread.start();
  }
}
