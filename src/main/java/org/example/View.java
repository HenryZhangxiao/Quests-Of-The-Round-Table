package org.example;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;

public class View extends Pane {

    //private Game model;
    private ImageView storyDiscard;
    private ImageView advDiscard;
    //private Group hand;
    private Button storyDeck;
    private Button advDeck;

    public View () {

        setWidth(1280);
        setHeight(720);

        storyDiscard = new ImageView();
        storyDiscard.setX(getWidth()/2-110);
        storyDiscard.setY(getHeight()/3);
        storyDiscard.setFitWidth(100);
        storyDiscard.setFitHeight(140);
        storyDiscard.setPreserveRatio(true);
        getChildren().add(storyDiscard);

        advDiscard = new ImageView();
        advDiscard.setX(getWidth()/2+10);
        advDiscard.setY(getHeight()/3);
        advDiscard.setFitWidth(100);
        advDiscard.setFitHeight(140);
        advDiscard.setPreserveRatio(true);
        getChildren().add(advDiscard);

        storyDeck = new Button("Draw story card");
        storyDeck.relocate(storyDiscard.getX()-120, storyDiscard.getY());
        storyDeck.setPrefSize(100,140);
        getChildren().add(storyDeck);

        storyDeck.setOnAction(e -> {
            //Draw story card within game model, update hand to reflect
            //Displays a test card in the discard pile for now
            Image newImg = new Image(new File("src/resources/Horse.jpg").toURI().toString());
            storyDiscard.setImage(newImg);
        });

        advDeck = new Button("Draw adventure card");
        advDeck.relocate(advDiscard.getX()+120, advDiscard.getY());
        advDeck.setPrefSize(100,140);
        getChildren().add(advDeck);

        advDeck.setOnAction(e -> {
            //Draw adventure card within game model, update hand to reflect
            //Displays a test card in the discard pile for now
            Image newImg = new Image(new File("src/resources/Sword.jpg").toURI().toString());
            advDiscard.setImage(newImg);
        });

    }

}
