package gui;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import network.*;

import java.io.File;
import java.util.ArrayList;

public class TestBiddingView {

    //get free bids from cards played

    //Window sizes
    private int width = 1000;
    private int height = 1000;

    //List of cards
    ArrayList<Card> hand;
    ArrayList<Card> bidCards;

    //Image of all cards
    Image advCards;

    //For drawing each card
    ArrayList<ImageView> handViews;
    ArrayList<ImageView> bidViews;

    //Rectangles to show each area
    Rectangle bidArea;
    Rectangle handArea;
    Rectangle amourArea;

    //Labels to show the total bid, errors, and free bid info
    Label totalBidLabel;
    Label errorLabel;
    Label amourLabel;

    // Button to bid
    Button acceptButton;

    //Groups of each ImageView that contains only the card ImageViews.
    Group bidCardGroup = new Group();
    Group handCardGroup = new Group();

    //Minimum bid (default or last bid+1)
    private int minBid;
    private QuestCard questCard;

    public TestBiddingView(int questCardID, int testCardID, int minBid) {
        //Gets the hand of the local player
        hand = new ArrayList<>(LocalGameManager.get().getLocalPlayer().hand);
        bidCards = new ArrayList<>();
        handViews = new ArrayList<>();
        bidViews = new ArrayList<>();

        //Loads the image
        advCards = View.get().getAdvCards();

        questCard = (QuestCard) Card.getCardByID(questCardID);
        TestCard testCard = (TestCard) Card.getCardByID(testCardID);
        //Set minimum bid
        this.minBid = minBid;

        //Run in JavaFX thread
        Platform.runLater(this::setup);
    }

    private void setup() {
        Stage stage = new Stage();
        //Everything is added to this main group.
        Group root = new Group();

        //The areas for the selected cards and the cards in the hand
        bidArea = new Rectangle(55,200,720,180);
        handArea = new Rectangle(55,height-400,890,310);
        amourArea = new Rectangle(800,200,130,180);

        Font largeFont = new Font("Arial", 20);


        //Bid Cards subgroup
        Group bidGroup = new Group();
        Label selectionLabel = new Label("Bid at least " + minBid + " cards for this stage of the quest! (RMB to bid/unbid, LMB to play an Ally or Amour)");
        selectionLabel.setFont(largeFont);
        selectionLabel.setLayoutX(20);
        selectionLabel.setLayoutY(50);
        bidGroup.getChildren().add(selectionLabel);

        totalBidLabel = new Label("You have 0 free bids from allies and amours in play\nYou have discarded 0 card(s) for a total bid of 0");
        totalBidLabel.setFont(largeFont);
        totalBidLabel.setLayoutX(bidArea.getX());
        totalBidLabel.setLayoutY(80);
        bidGroup.getChildren().add(totalBidLabel);

        bidArea.setFill(Color.GREEN);
        bidArea.setStroke(Color.SADDLEBROWN);
        bidArea.setArcWidth(30);
        bidArea.setArcHeight(20);
        bidGroup.getChildren().add(bidArea);

        //Error label to notify that two amours cant be added. Set to invisible by default
        errorLabel = new Label("You cannot add two amours!");
        errorLabel.setFont(largeFont);
        errorLabel.setLayoutX(bidArea.getX());
        errorLabel.setLayoutY(bidArea.getY() + bidArea.getHeight() + 10);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        bidGroup.getChildren().add(errorLabel);

        bidGroup.getChildren().add(bidCardGroup);

        //Amour Card Area
        amourArea.setFill(Color.YELLOW);
        amourArea.setStroke(Color.SADDLEBROWN);
        amourArea.setArcWidth(30);
        amourArea.setArcHeight(20);

        amourLabel = new Label("No Amour\n Selected");
        amourLabel.setLayoutX(amourArea.getX() + 20);
        amourLabel.setLayoutY(amourArea.getY() + 10);
        amourLabel.setFont(largeFont);

        //Hand Area
        Group handGroup = new Group();
        handArea.setFill(Color.DARKGRAY);
        handArea.setStroke(Color.SADDLEBROWN);
        handArea.setArcWidth(30);
        handArea.setArcHeight(20);
        handGroup.getChildren().add(handArea);

        handGroup.getChildren().add(handCardGroup);

        //Decline to participate button
        Button declineButton = new Button();
        declineButton.setLayoutX(bidArea.getX());
        declineButton.setLayoutY(height - 500);
        declineButton.setMinWidth(100);
        declineButton.setMinHeight(40);
        declineButton.setText("Fold");

        declineButton.setOnAction(e -> {
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TEST_BID_QUERY, NetworkMessage.pack(true));
            NetworkManager.get().sendNetMessageToServer(msg);
            stage.close();
        });

        //Accept button
        acceptButton = new Button();
        acceptButton.setLayoutX(handArea.getX() + handArea.getWidth() - 100);
        acceptButton.setLayoutY(height - 500);
        acceptButton.setMinWidth(100);
        acceptButton.setMinHeight(40);
        acceptButton.setText("Bid!");

        acceptButton.setOnAction(e -> {
            int freeBids = AllyCard.getBidsForAllies(LocalGameManager.get().getLocalPlayer().getAllies(),questCard,LocalGameManager.get().getLocalPlayer().getAmour());
            if (bidCards.size() + freeBids >= minBid) {
                int[] sendCards = new int[bidCards.size()];
                for (int i = 0; i < bidCards.size(); i++){
                    sendCards[i] = bidCards.get(i).getID();
                }

                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TEST_BID_QUERY,NetworkMessage.pack(false,bidCards.size() + freeBids,sendCards));
                NetworkManager.get().sendNetMessageToServer(msg);

                stage.close();
            }
        });

        // Add above elements to root Group
        root.getChildren().addAll(bidGroup,amourArea,amourLabel,handGroup,declineButton,acceptButton);

        //Draws all the cards
        updateCards();

        //Puts everything together
        Scene scene = new Scene(root,width,height);

        stage.setOnCloseRequest(e -> e.consume());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(View.get().getScene().getWindow());

        //Opens the window and waits.
        stage.setScene(scene);
        stage.setTitle((LocalGameManager.get().getLocalPlayer().getPlayerNum() + 1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName());
        stage.showAndWait();
    }

    private void updateCards() {
        //Clears both groups.
        bidCardGroup.getChildren().clear();
        handCardGroup.getChildren().clear();

        //Get free bids from allies and amours in play
        int freeBids = AllyCard.getBidsForAllies(LocalGameManager.get().getLocalPlayer().getAllies(),questCard,LocalGameManager.get().getLocalPlayer().getAmour());

        int numCardsBid = bidCards.size();

        totalBidLabel.setText("You have " + freeBids + " from allies and amours in play\nYou have discarded " +
                numCardsBid + " card(s) for a total bid of " + (freeBids + numCardsBid));

        // only set bid button to visible if bid is legal
        acceptButton.setDisable(freeBids + numCardsBid < minBid);

        //Drawing the selected cards
        for(int i = 0; i < bidCards.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(bidArea.getX() + 10 + (i%8)*110);
            aCard.setY(bidArea.getY() + 10 + Math.floorDiv(i,8)*150);
            aCard.setImage(advCards);
            aCard.setViewport(View.getAdvCard(bidCards.get(i).getID()));
            bidCardGroup.getChildren().add(aCard);

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    hand.add(bidCards.get(finalI));
                    bidCards.remove(finalI);
                    //Once moving a card, reupdate all the cards.
                    updateCards();
                    errorLabel.setVisible(false);
                }
            });
        }

        //Drawing Amour Card if set.
        if(LocalGameManager.get().getLocalPlayer().getAmour() != null){
            ImageView amourCard = new ImageView();
            amourCard.setFitWidth(100);
            amourCard.setFitHeight(140);
            amourCard.setPreserveRatio(true);
            amourCard.setX(amourArea.getX() + 15);
            amourCard.setY(amourArea.getY() + 10);
            amourCard.setImage(advCards);
            amourCard.setViewport(View.getAdvCard(LocalGameManager.get().getLocalPlayer().getAmour().getID()));
            handCardGroup.getChildren().add(amourCard);
            amourLabel.setVisible(false);
        }
        else
            amourLabel.setVisible(true);

        //Drawing all cards in hand.
        for(int i = 0; i < hand.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(handArea.getX() + 10 + (i%8)*110);
            aCard.setY(handArea.getY() + 10 + Math.floorDiv(i,8)*150);
            aCard.setImage(advCards);
            aCard.setViewport(View.getAdvCard(hand.get(i).getID()));
            handCardGroup.getChildren().add(aCard);

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) { // left click, play ally or amour
                    if (hand.get(finalI) instanceof AmourCard) {
                        //if an amour is already in play, don't let this amour get played
                        if (LocalGameManager.get().getLocalPlayer().getAmour() != null) {
                            errorLabel.setVisible(true);
                        } else {
                            //Update LGM, Server, and main view
                            LocalGameManager.get().getLocalPlayer().setAmour((AmourCard) hand.get(finalI));
                            LocalGameManager.get().getLocalPlayer().hand.remove(hand.get(finalI));
                            NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_AMOUR,NetworkMessage.pack(LocalGameManager.get().getLocalPlayer().getAmour().getID())));
                            View.get().update();

                            hand.remove(finalI);
                            updateCards();
                            return;
                        }
                    } else if (hand.get(finalI) instanceof AllyCard) {
                        // Update LGM, Server, and main view
                        LocalGameManager.get().getLocalPlayer().addAlly((AllyCard) hand.get(finalI));
                        LocalGameManager.get().getLocalPlayer().hand.remove(hand.get(finalI));
                        NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_ALLIES,NetworkMessage.pack(LocalGameManager.get().getLocalPlayer().getAllyCardIDs())));
                        View.get().update();

                        hand.remove(finalI);
                        updateCards();
                        return;
                    }
                } else {    // right click, bid card
                    bidCards.add(hand.get(finalI));
                    hand.remove(finalI);
                    updateCards();
                }
            });
        }
    }

}
