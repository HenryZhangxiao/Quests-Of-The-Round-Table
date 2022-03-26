package gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import network.LocalGameManager;

public class QuestResultView {

    private final int winnerID;

    public QuestResultView(int winner) {

        winnerID = winner;

        Platform.runLater(this::setup);
    }

    private void setup() {
        Stage stage = new Stage();
        VBox root = new VBox();

        Label result = new Label();
        if (winnerID == -1) {
            result.setText("Sorry, no one won this quest");
        } else if (LocalGameManager.get().getLocalPlayer().getPlayerNum() == winnerID) {
            result.setText("Congratulations! You won the quest");
        } else {
            result.setText(LocalGameManager.get().getPlayerByID(winnerID).getPlayerName() + " won this quest");
        }

        Button btn = new Button("OK");
        btn.setOnAction(actionEvent -> {
            // If you drew this story card, your turn ends
            //TODO: only change turn if you drew the story card
            //if (LocalGameManager.get().isMyTurn()) {
            //    LocalGameManager.get().finishTurn();
            //}

            stage.close();
        });

        root.getChildren().addAll(result, btn);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 200, 100);
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> e.consume());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(View.get().getScene().getWindow());

        stage.showAndWait();
    }
}
