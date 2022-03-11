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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class View extends Pane {

    //Singleton
    public static View view;

    private final static int CARD_SRC_W = 200;
    private final static int CARD_SRC_H = 280;
    private final static int CARD_W = 100;
    private final static int CARD_H = 140;
    private final static int MARGIN = 10;
    private final static int HAND_COLS = 8;
    private final static int HAND_ROWS = 2;

    private Image advCards;
    private Image storyCards;
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

        advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());
        storyCards = new Image(new File("src/resources/storyComposite.jpg").toURI().toString());
    }

    public Button getStoryDeck() {
        return storyDeck;
    }

    public Image getAdvCards() {
        return advCards;
    }

    public Image getStoryCards() {
        return storyCards;
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
        endTurn.setDisable(!LocalGameManager.get().isMyTurn());

        int[] cardIDs = LocalGameManager.get().getLocalPlayer().getHandCardIDs();
        // index for ImageView in local arraylist cards
        for (int index = 0; index < 16; ++index) {
            Rectangle2D viewport;
            if (index < cardIDs.length) {
                viewport = getAdvCard(cardIDs[index]);
            } else {
                viewport = getAdvCard(0);
            }
            cards.get(index).setViewport(viewport);
        }

        ArrayList<Card> discardPile = LocalGameManager.get().getDiscardPile();
        if (!discardPile.isEmpty())
            advDiscard.setViewport(getAdvCard(discardPile.get(discardPile.size()-1).getID()));
    }

    private void gameViewInit() {
        Image advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());
        Image storyCards = new Image(new File("src/resources/storyComposite.jpg").toURI().toString());

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
            int finalI = i;
            card.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    //discard
                    LocalGameManager.get().discardCard(finalI);
                } else {
                    //play card
                }
            });

            card.setImage(advCards);
            card.setViewport(getAdvCard(0));
        }

        getChildren().add(hand);

        storyDeck = new Button("Draw story card");
        storyDeck.relocate(720, 240);
        storyDeck.setPrefSize(100,140);
        storyDeck.setOnAction(e -> LocalGameManager.get().drawStory());
        getChildren().add(storyDeck);

        storyDiscard = new ImageView();
        storyDiscard.setX(storyDeck.getLayoutX() + 110);
        storyDiscard.setY(storyDeck.getLayoutY());
        storyDiscard.setFitWidth(100);
        storyDiscard.setFitHeight(140);
        storyDiscard.setPreserveRatio(true);
        storyDiscard.setImage(storyCards);
        storyDiscard.setViewport(getStoryCard(0));
        getChildren().add(storyDiscard);

        advDiscard = new ImageView();
        advDiscard.setX(storyDeck.getLayoutX() + 220);
        advDiscard.setY(storyDeck.getLayoutY());
        advDiscard.setFitWidth(100);
        advDiscard.setFitHeight(140);
        advDiscard.setPreserveRatio(true);
        advDiscard.setImage(advCards);
        advDiscard.setViewport(getAdvCard(0));
        getChildren().add(advDiscard);

        advDeck = new Button("Draw\nadventure card");
        advDeck.relocate(storyDeck.getLayoutX() + 330, advDiscard.getY());
        advDeck.setPrefSize(100,140);
        advDeck.setTextAlignment(TextAlignment.CENTER);
        advDeck.setOnAction(e -> LocalGameManager.get().drawCard());
        getChildren().add(advDeck);

        endTurn = new Button("End Turn");
        endTurn.relocate(storyDeck.getLayoutX() + 440, storyDeck.getLayoutY() + 110);
        endTurn.setPrefSize(100,30);
        endTurn.setDisable(true);
        endTurn.setOnAction(e -> LocalGameManager.get().finishTurn());
        getChildren().add(endTurn);

        Label localPly = new Label(LocalGameManager.get().getLocalPlayer().getPlayerName());
        localPly.relocate(20, getHeight()-50);
        getChildren().add(localPly);
    }

    public Rectangle2D getAdvCard(int id) {
        return new Rectangle2D(((id-1)%8)*200,Math.floorDiv(id-1,8)*280,200,280);
    }

    public Rectangle2D getStoryCard(int id) {
        return new Rectangle2D(((id-18)%6)*200,Math.floorDiv(id-18,6)*280,200,280);
    }

}
