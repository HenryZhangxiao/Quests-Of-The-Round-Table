package org.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Random;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var scene = new Scene(View.get(), View.get().getWidth(), View.get().getHeight());
        var label = new Label("This is the start of Quests game.");

        stage.setScene(scene);




        //Join/Create Game popup.
        doJoinPopup();
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //For quick testing
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TEST_MESSAGE,null);
                NetworkManager.get().sendNetMessage(msg);
            }
        });

        View.get().doWaitPopup();

        stage.setTitle("Quests of the Round Table");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    //Called when closed.
    @Override
    public void stop(){
        if(NetworkManager.isInstantiated())
            NetworkManager.get().close();
    }

    public void doJoinPopup(){
        Stage joinPopup = new Stage();
        joinPopup.initModality(Modality.APPLICATION_MODAL);
        joinPopup.setTitle("Start New Game or Join Game");
        joinPopup.initStyle(StageStyle.UNDECORATED);

        //Quick names for testing/default input
        String[] fakeNames = {"Joe","John","Carl","Trent","Sam"};

        Label label1 = new Label("Join Game or Start New Game");
        TextField ipField = new TextField("localhost");

        Label label2 = new Label("Enter your name:");
        //Random name from list
        TextField nameField = new TextField(fakeNames[new Random().nextInt(fakeNames.length)]);

        Button joinButton = new Button("Join Game");
        Button newGameButton = new Button("Start New Game");


        joinButton.setOnAction(e -> {
            label1.setText("Joining...");
            NetworkManager.get().joinGame(ipField.getText(),nameField.getText());
            joinPopup.close();
        });

        newGameButton.setOnAction(e -> {
            NetworkManager.get().createGame(nameField.getText());
            joinPopup.close();
        });


        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1, ipField, label2, nameField, joinButton, newGameButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);
        joinPopup.setScene(scene1);
        joinPopup.showAndWait();
    }

}