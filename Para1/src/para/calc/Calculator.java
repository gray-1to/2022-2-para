// 20B30100
// 伊藤悠馬

package para.calc;

import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.geometry.Pos;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * JavaFX 電卓アプリケーションのメインクラス
 */
public class Calculator extends Application{
  Label input;
  Label output;
  StringBuilder buff;
  Executor ex;
  public Calculator(){
    input = new Label();
    output = new Label();
    buff = new StringBuilder();
    ex = new Executor1();
  }
  final String[] buttonname =  {"9","8","7","+",
                          "6","5","4","-",
                          "3","2","1","*",
                          "0",".",",","/"};

  

  public void start(Stage stage){
    VBox root = new VBox();
    root.setAlignment(Pos.TOP_CENTER);
    GridPane del_cal_box = new GridPane();
    GridPane key_box = new GridPane();
    GridPane grid = new GridPane();
    key_box.setAlignment(Pos.CENTER);
    Scene scene = new Scene(root, 200,300);
    Button[] buttons = new Button[16];
    Button buttoncal = new Button("=");
    buttoncal.setPrefHeight(56);
    buttoncal.setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        //calculate here
        String formula = input.getText();
        output.setText(ex.operation(formula));
      }
    });
    Button buttondel = new Button("<");
    buttondel.setPrefHeight(56);
    buttondel.setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        if(buff.length()>0){
          buff.deleteCharAt(buff.length()-1);
        }
        input.setText(buff.toString());
      }
    });
    StackPane stack = new StackPane();
    stack.getChildren().add(new Rectangle(140,30,Color.WHITE));
    stack.getChildren().add(input);
    root.getChildren().addAll(stack, output);
    for(int i=0;i<16;i++){
      buttons[i] = new Button(buttonname[i]);
      buttons[i].setPrefWidth(50);
      buttons[i].setPrefHeight(28);
    }
    buttons[0].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[0]);
        input.setText(buff.toString());
      }
    });
    buttons[1].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[1]);
        input.setText(buff.toString());
      }
    });
    buttons[2].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[2]);
        input.setText(buff.toString());
      }
    });
    buttons[3].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[3]);
        input.setText(buff.toString());
      }
    });
    buttons[4].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[4]);
        input.setText(buff.toString());
      }
    });
    buttons[5].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[5]);
        input.setText(buff.toString());
      }
    });
    buttons[6].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[6]);
        input.setText(buff.toString());
      }
    });
    buttons[7].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[7]);
        input.setText(buff.toString());
      }
    });
    buttons[8].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[8]);
        input.setText(buff.toString());
      }
    });
    buttons[9].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[9]);
        input.setText(buff.toString());
      }
    });
    buttons[10].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[10]);
        input.setText(buff.toString());
      }
    });
    buttons[11].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[11]);
        input.setText(buff.toString());
      }
    });
    buttons[12].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[12]);
        input.setText(buff.toString());
      }
    });
    buttons[13].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[13]);
        input.setText(buff.toString());
      }
    });
    buttons[14].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[14]);
        input.setText(buff.toString());
      }
    });
    buttons[15].setOnAction(new EventHandler<ActionEvent>(){
      public void handle(ActionEvent event){
        buff.append(buttonname[15]);
        input.setText(buff.toString());
      }
    });

    for(int i=0; i<16; i++){
      grid.add(buttons[i], i%4, (int)i/4);
    }
    del_cal_box.add(buttondel,0,0);
    del_cal_box.add(buttoncal,0,1);

    key_box.add(grid,0,0);
    key_box.add(del_cal_box,1,0);
    root.getChildren().add(key_box);

    stage.setWidth(200);
    stage.setHeight(200);
    stage.setScene(scene);
    stage.setTitle("JavaFX Calc");
    stage.show();
  }
}
