package org.example;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class View extends Pane {

    //Singleton
    public static View view;

    private ImageView storyDiscard;
    private ImageView advDiscard;
    private ArrayList<ImageView> cards;
    private Button storyDeck;
    private Button advDeck;
    private Button endTurn;

    public static View get() {
        if (view == null)
            view = new View();
        return view;
    }

    private View () {

        setWidth(1280);
        setHeight(720);

    }

    // Basically like join popup, there has to be a better way to do this tho
    public void doWaitPopup() {
        Stage waitPopup = new Stage();
        waitPopup.initModality(Modality.APPLICATION_MODAL);
        waitPopup.initStyle(StageStyle.UNDECORATED);

        Label lbl = new Label();
        lbl.setText("Waiting for more players to join");

        Button startBtn = new Button("Start Game");
        startBtn.setVisible(false);
        startBtn.setOnAction(e -> LocalGameManager.get().startGame());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(lbl,startBtn);
        layout.setAlignment(Pos.CENTER);
        Scene waitScene = new Scene(layout, 300, 250);
        waitPopup.setScene(waitScene);

        Task<Void> sleeper1 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    Thread.sleep(1000);
                    if (LocalGameManager.get().getConnectedPlayerCount() > 1)
                        break;
                }
                return null;
            }
        };

        Task<Void> sleeper2 = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (true) {
                    Thread.sleep(1000);
                    if (LocalGameManager.get().isGameStarted())
                        break;
                }
                return null;
            }
        };

        sleeper1.setOnSucceeded(workerStateEvent -> {
            if (NetworkManager.get().isHost())
                startBtn.setVisible(true);
            lbl.setText("Waiting for host to start game");
            new Thread(sleeper2).start();
        });

        sleeper2.setOnSucceeded(workerStateEvent -> {
            waitPopup.close();
            gameViewInit();
            update();
        });

        if (NetworkManager.get().isHost())
            new Thread(sleeper1).start();
        else {
            new Thread(sleeper2).start();
            lbl.setText("Waiting for host to start game");
        }
        waitPopup.showAndWait();
    }

    public void update() {
        System.out.println(LocalGameManager.get().getLocalPlayer().getPlayerName());
        System.out.println(LocalGameManager.get().isMyTurn());
        endTurn.setDisable(!LocalGameManager.get().isMyTurn());

        // index for ImageView in local arraylist cards
        int index = 0;
        for (int id: LocalGameManager.get().getLocalPlayer().getHandCardIDs()) {
            cards.get(index).setViewport(getAdvCard(id));
            index++;
        }

        advDiscard.setViewport(getAdvCard(new Random().nextInt(17)+1));

    }

    private void gameViewInit() {
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

        Image advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());
        advDiscard.setImage(advCards);
        advDiscard.setViewport(getAdvCard(0));

        //Image storyCards = new Image(new File("src/resources/storyComposite.jpg").toURI().toString());
        //storyDiscard.setImage(storyCards);
        //storyDiscard.setViewport(getStoryCard(0));

        storyDeck = new Button("Draw story card");
        storyDeck.relocate(storyDiscard.getX()-120, storyDiscard.getY());
        storyDeck.setPrefSize(100,140);
        getChildren().add(storyDeck);

        storyDeck.setOnAction(e -> {
            //model.drawStoryCard(); or smtg like that
            //update();
        });

        advDeck = new Button("Draw adventure card");
        advDeck.relocate(advDiscard.getX()+120, advDiscard.getY());
        advDeck.setPrefSize(100,140);
        getChildren().add(advDeck);

        //should there be 2 draw methods? (adv vs story)
        advDeck.setOnAction(e -> LocalGameManager.get().drawCard());

        Group hand = new Group();

        Rectangle handArea = new Rectangle(getWidth()-900, getHeight()-320, 890, 310);
        handArea.setFill(Color.DARKGRAY);
        handArea.setStroke(Color.SADDLEBROWN);
        handArea.setArcWidth(30);
        handArea.setArcHeight(20);
        hand.getChildren().add(handArea);

        cards = new ArrayList<>();

        for (int i = 0; i < 16; ++i) {
            ImageView card = new ImageView();
            card.setFitWidth(100);
            card.setFitHeight(140);
            card.setPreserveRatio(true);
            card.setX(handArea.getX() + 10 + (i%8)*110);
            card.setY(handArea.getY() + 10 + Math.floorDiv(i,8)*150);
            cards.add(card);
            hand.getChildren().add(card);

            //add event handling for discarding

            card.setImage(advCards);
            card.setViewport(getAdvCard(0));
        }

        getChildren().add(hand);

        endTurn = new Button("End Turn");
        endTurn.relocate(handArea.getX()+790, handArea.getY()-30);
        endTurn.setPrefSize(100,20);
        endTurn.setDisable(true);
        endTurn.setOnAction(e -> LocalGameManager.get().finishTurn());
        getChildren().add(endTurn);

        Label localPly = new Label(LocalGameManager.get().getLocalPlayer().getPlayerName());
        localPly.relocate(20, getHeight()-50);
        getChildren().add(localPly);
    }

    private Rectangle2D getAdvCard(int id) {
        return new Rectangle2D(((id-1)%8)*200,Math.floorDiv(id-1,8)*280,200,280);
    }

    //private Rectangle2D getStoryCard(int id) { }

}
