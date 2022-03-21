package gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;

import java.io.File;
import java.util.ArrayList;

public class PreviewStageView {

    private ArrayList<Card> stageCards;
    private QuestCard questCard;
    private Image advCards;

    public PreviewStageView(ArrayList<Card> stageCards, QuestCard questCard){
        this.stageCards = stageCards;
        this.questCard = questCard;

        advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());


        Platform.runLater(() -> setup());
    }

    public void setup(){

        Stage stage = new Stage();
        Group root = new Group();

        Rectangle cardArea = new Rectangle(0,20, Math.max(stageCards.size(),2) * 120 , 150);

        int bp = Quest.getBPForStage(Card.getCardArrayFromCardList(stageCards),questCard);

        Label l = new Label("Preview of current stage cards. Total BP is: " + String.valueOf(bp));
        root.getChildren().add(l);


        for(int i = 0; i < stageCards.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(cardArea.getX() + 10 + (i%8)*110);
            aCard.setY(cardArea.getY() + 10);
            aCard.setImage(advCards);
            aCard.setViewport(View.getAdvCard(stageCards.get(i).getID()));
            root.getChildren().add(aCard);
        }

        Button okBtn = new Button("OK");
        okBtn.setLayoutX(10);
        okBtn.setLayoutY(180);
        okBtn.setMinWidth(cardArea.getWidth() + 20);
        okBtn.setMinHeight(20);
        okBtn.setOnAction(e -> {
            stage.close();
        });

        root.getChildren().add(okBtn);

        Scene scene = new Scene(root, cardArea.getWidth() + 40, 220);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.showAndWait();

    }
}
