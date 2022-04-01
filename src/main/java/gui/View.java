package gui;

import javafx.concurrent.Task;
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
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.AllyCard;
import model.AmourCard;
import model.MordredFoe;
import network.*;
import model.Card;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

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
    private ArrayList<ImageView> allies;
    private Button storyDeck;
    private Button advDeck;
    private Button endTurn;
    private Label localPly;
    private Label errorLabel;
    private Label turnLabel;
    private Font errorFont;


    public static View get() {
        if (view == null)
            view = new View();
        return view;
    }

    private View () {

        setWidth(1280);
        setHeight(720);

        advCards = new Image(new File("src/main/resources/advComposite.jpg").toURI().toString());
        storyCards = new Image(new File("src/main/resources/storyComposite.jpg").toURI().toString());
        //advCards = new Image("/advComposite.jpg");
        //storyCards = new Image("/storyComposite.jpg");
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
        startBtn.setOnAction(e -> LocalGameManager.get().startGame(false));

        Button rigBtn = new Button("Start Demo Game");
        rigBtn.setVisible(false);
        rigBtn.setOnAction(e -> {
            LocalGameManager.get().startGame(true);
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(lbl,startBtn,rigBtn);
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
            if (NetworkManager.get().isHost()) {
                startBtn.setVisible(true);
                rigBtn.setVisible(true);
            }
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

    public void enableTurnButtons(){
        storyDeck.setDisable(!LocalGameManager.get().isMyTurn());
        endTurn.setDisable(true);
    }

    public void enableEndTurnButton(){
        endTurn.setDisable(!LocalGameManager.get().isMyTurn());
    }

    public void update() {
        //if(!LocalGameManager.get().isMyTurn()) {
            //endTurn.setDisable(!LocalGameManager.get().isMyTurn());

            advDeck.setDisable(!LocalGameManager.get().isMyTurn());
        //}

        if(LocalGameManager.get().isMyTurn())
            turnLabel.setText("It is your turn.");
        else
            turnLabel.setText("It is currently " + LocalGameManager.get().getPlayerByID(LocalGameManager.get().getTurnID()).getPlayerName() + "'s turn.");

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

        int[] allyIDs = LocalGameManager.get().getLocalPlayer().getAllyCardIDs();
        for (int index = 0; index < 10; ++index) {
            Rectangle2D viewport;
            if (index < allyIDs.length) {
                viewport = getAdvCard(allyIDs[index]);
                allies.get(index).setVisible(true);
            } else {
                viewport = getAdvCard(0);
                allies.get(index).setVisible(false);
            }
            allies.get(index).setViewport(viewport);
        }

        ArrayList<Card> advPile = LocalGameManager.get().getAdvPile();
        if (!advPile.isEmpty())
            advDiscard.setViewport(getAdvCard(advPile.get(advPile.size()-1).getID()));

        ArrayList<Card> storyPile = LocalGameManager.get().getStoryPile();
        if (!storyPile.isEmpty())
            storyDiscard.setViewport(getStoryCard(storyPile.get(storyPile.size()-1).getID()));

        // Update shields
        localPly.setText((LocalGameManager.get().getLocalPlayer().getPlayerNum()+1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName() + "\n Shields: " + LocalGameManager.get().getLocalPlayer().getShields());
    }

    private void gameViewInit() {
        //Image advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());
        //Image storyCards = new Image(new File("src/resources/storyComposite.jpg").toURI().toString());

        Group hand = new Group();
        Group allyGroup = new Group();

        Rectangle handArea = new Rectangle(getWidth()-900, getHeight()-320, 890, 310);
        handArea.setFill(Color.DARKGRAY);
        handArea.setStroke(Color.SADDLEBROWN);
        handArea.setArcWidth(30);
        handArea.setArcHeight(20);
        hand.getChildren().add(handArea);

        cards = new ArrayList<>();
        allies = new ArrayList<>();

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
                } else if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if(LocalGameManager.get().getLocalPlayer().hand.get(finalI) instanceof AllyCard){
                        LocalGameManager.get().getLocalPlayer().addAlly((AllyCard) LocalGameManager.get().getLocalPlayer().hand.get(finalI));
                        LocalGameManager.get().getLocalPlayer().hand.remove(finalI);

                        NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_ALLIES, NetworkMessage.pack(LocalGameManager.get().getLocalPlayer().getAllyCardIDs())));
                    }
                    else if(LocalGameManager.get().getLocalPlayer().hand.get(finalI) instanceof AmourCard){
                        if(LocalGameManager.get().getLocalPlayer().getAmour() != null)
                            return;

                        //Playing the amour card.
                        LocalGameManager.get().getLocalPlayer().setAmour((AmourCard) LocalGameManager.get().getLocalPlayer().hand.get(finalI));
                        LocalGameManager.get().getLocalPlayer().hand.remove(finalI);

                        NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_AMOUR, NetworkMessage.pack(LocalGameManager.get().getLocalPlayer().getAmour().getID())));
                    }
                    else if(LocalGameManager.get().getLocalPlayer().hand.get(finalI) instanceof MordredFoe){
                        //Mordred's selection view
                        MordredView m = new MordredView(finalI);
                    }
                }
            });

            card.setImage(advCards);
            card.setViewport(getAdvCard(0));
        }

        getChildren().add(hand);

        //Ally drawing area
        Rectangle allyArea = new Rectangle(30, 30, 1110, 160);
        allyArea.setFill(Color.LIGHTBLUE);
        allyArea.setStroke(Color.SADDLEBROWN);
        allyArea.setArcWidth(30);
        allyArea.setArcHeight(20);
        allyGroup.getChildren().add(allyArea);

        Label allyLabel = new Label("Allies in play: ");
        allyLabel.setLayoutX(allyArea.getX());
        allyLabel.setLayoutY(allyArea.getY() - 20);
        allyGroup.getChildren().add(allyLabel);

        for(int i = 0; i < 10; i++){
            ImageView card = new ImageView();
            card.setFitWidth(100);
            card.setFitHeight(140);
            card.setPreserveRatio(true);
            card.setX(allyArea.getX() + 10 + (i%10)*110);
            card.setY(allyArea.getY() + 10 + Math.floorDiv(i,10)*150);
            allies.add(card);
            allyGroup.getChildren().add(card);

            //add event handling for discarding
            int finalI = i;
            card.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    //discard
                    //LocalGameManager.get().discardCard(finalI);
                } else {
                    //play card
                }
            });

            card.setImage(advCards);
            card.setViewport(getAdvCard(0));
        }

        getChildren().add(allyGroup);

        storyDeck = new Button("Draw story card");
        storyDeck.relocate(700, 240);
        storyDeck.setPrefSize(100,140);
        storyDeck.setDisable(true);
        storyDeck.setOnAction(e -> {
            storyDeck.setDisable(true);
            //endTurn.setDisable(false);
            LocalGameManager.get().drawStory();

        });
        getChildren().add(storyDeck);


        Rectangle storyDiscardArea = new Rectangle(storyDeck.getLayoutX() + 105, storyDeck.getLayoutY() - 5, 110, 150);
        storyDiscardArea.setFill(new Color(0.4f,0,0,0.3f));
        storyDiscardArea.setStroke(Color.SADDLEBROWN);
        storyDiscardArea.setArcWidth(30);
        storyDiscardArea.setArcHeight(20);
        getChildren().add(storyDiscardArea);

        Label storyDiscardLabel = new Label("Story Discard Pile");
        storyDiscardLabel.setLayoutX(storyDiscardArea.getX() + 5);
        storyDiscardLabel.setLayoutY(storyDiscardArea.getY() - 20);
        getChildren().add(storyDiscardLabel);

        storyDiscard = new ImageView();
        storyDiscard.setX(storyDiscardArea.getX() + 5);
        storyDiscard.setY(storyDiscardArea.getY() + 5);
        storyDiscard.setFitWidth(100);
        storyDiscard.setFitHeight(140);
        storyDiscard.setPreserveRatio(true);
        storyDiscard.setImage(storyCards);
        storyDiscard.setViewport(getStoryCard(0));
        getChildren().add(storyDiscard);


        Rectangle advDiscardArea = new Rectangle(storyDeck.getLayoutX() + 220, storyDeck.getLayoutY() - 5, 110, 150);
        advDiscardArea.setFill(new Color(0.4f,0,0,0.3f));
        advDiscardArea.setStroke(Color.SADDLEBROWN);
        advDiscardArea.setArcWidth(30);
        advDiscardArea.setArcHeight(20);
        getChildren().add(advDiscardArea);

        Label advDiscardLabel = new Label("Adventure Discard Pile");
        advDiscardLabel.setLayoutX(advDiscardArea.getX() - 3);
        advDiscardLabel.setLayoutY(advDiscardArea.getY() - 20);
        getChildren().add(advDiscardLabel);

        advDiscard = new ImageView();
        advDiscard.setX(advDiscardArea.getX() + 5);
        advDiscard.setY(advDiscardArea.getY() + 5);
        advDiscard.setFitWidth(100);
        advDiscard.setFitHeight(140);
        advDiscard.setPreserveRatio(true);
        advDiscard.setImage(advCards);
        advDiscard.setViewport(getAdvCard(0));
        getChildren().add(advDiscard);

        advDeck = new Button("Draw\nadventure card");
        advDeck.relocate(storyDeck.getLayoutX() + 340, advDiscard.getY());
        advDeck.setPrefSize(100,140);
        advDeck.setTextAlignment(TextAlignment.CENTER);
        advDeck.setOnAction(e -> LocalGameManager.get().drawCard());
        //Disables drawing adventure cards
        //getChildren().add(advDeck);

        endTurn = new Button("End Turn");
        endTurn.relocate(storyDeck.getLayoutX() + 450, storyDeck.getLayoutY() + 110);
        endTurn.setPrefSize(100,30);
        endTurn.setDisable(true);
        endTurn.setOnAction(e -> {
            if(LocalGameManager.get().getLocalPlayer().hand.size() > 12){
                errorLabel.setVisible(true);
                return;
            }

            errorLabel.setVisible(false);
            LocalGameManager.get().finishTurn();
        });
        getChildren().add(endTurn);

        localPly = new Label((LocalGameManager.get().getLocalPlayer().getPlayerNum()+1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName() + "\n Shields: " + LocalGameManager.get().getLocalPlayer().getShields());
        localPly.relocate(20, getHeight()-50);
        getChildren().add(localPly);

        turnLabel = new Label();
        turnLabel.relocate(allyArea.getX(),allyArea.getY() + allyArea.getHeight() + 10);
        getChildren().add(turnLabel);

        errorLabel = new Label("You have too many cards in your hand. Use RMB to discard some.");
        errorLabel.relocate(allyArea.getX(), allyArea.getY() + allyArea.getHeight() + 30);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        getChildren().add(errorLabel);

        enableTurnButtons();
    }

    public static Rectangle2D getAdvCard(int id) {
        return new Rectangle2D(((id-1)%8)*200,Math.floorDiv(id-1,8)*280,200,280);
    }

    public static Rectangle2D getStoryCard(int id) {
        return new Rectangle2D(((id-33)%6)*200,Math.floorDiv(id-33,6)*280,200,280);
    }

}
