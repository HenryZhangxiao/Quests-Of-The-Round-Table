package gui;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

public class TournamentParticipationView {

    //Window sizes
    private int width = 1000;
    private int height = 1000;

    //List of cards
    ArrayList<Card> hand;
    ArrayList<Card> selectedCards;

    //Image of all cards
    Image advCards;

    //For drawing each card
    ArrayList<ImageView> handViews;
    ArrayList<ImageView> selectionViews;

    //Rectangles to show each area
    Rectangle selectArea;
    Rectangle handArea;

    //Labels to show the total BP of the selection and for an error
    Label totalBPLabel;
    Label errorLabel;

    //Groups of each ImageView that contains only the card ImageViews.
    Group selectionCardGroup = new Group();
    Group handCardGroup = new Group();

    public TournamentParticipationView() {
        //Gets the hand of the local player
        hand = new ArrayList<>(LocalGameManager.get().getLocalPlayer().hand);
        selectedCards = new ArrayList<>();
        handViews = new ArrayList<>();
        selectionViews = new ArrayList<>();

        //Loads the image
        advCards = View.get().getAdvCards();

        //To prevent JavaFX from yelling about threads.
        Platform.runLater(this::setup);
    }

    private void setup() {
        Stage stage = new Stage();
        Group root = new Group();

        stage.setOnCloseRequest(e -> e.consume());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(View.get().getScene().getWindow());

        //The areas for the selected cards and the cards in the hand
        selectArea = new Rectangle(55,220,890,180);
        handArea = new Rectangle(55,height-400,890,310);

        Font largeFont = new Font("Arial", 20);

        Group selectionGroup = new Group();
        Label selectionLabel = new Label("Select Weapon, Ally, or Amour cards for the tournament!");
        selectionLabel.setFont(largeFont);
        selectionLabel.setLayoutX(20);
        selectionLabel.setLayoutY(50);
        selectionGroup.getChildren().add(selectionLabel);

        totalBPLabel = new Label("You have selected 0 card(s) for a total BP value of 0");
        totalBPLabel.setFont(largeFont);
        totalBPLabel.setLayoutX(20);
        totalBPLabel.setLayoutY(100);
        selectionGroup.getChildren().add(totalBPLabel);


        selectArea.setFill(Color.SKYBLUE);
        selectArea.setStroke(Color.SADDLEBROWN);
        selectArea.setArcWidth(30);
        selectArea.setArcHeight(20);
        selectionGroup.getChildren().add(selectArea);

        //Error label to notify that two cards of the same type cant be added. Set to invisible by default
        errorLabel = new Label("You cannot add two cards of the same type, play any foe cards, or play any test cards!");
        errorLabel.setFont(largeFont);
        errorLabel.setLayoutX(selectArea.getX());
        errorLabel.setLayoutY(selectArea.getY() + selectArea.getHeight() + 20);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        selectionGroup.getChildren().add(errorLabel);

        selectionGroup.getChildren().add(selectionCardGroup);

        //Hand Area
        Group handGroup = new Group();
        handArea.setFill(Color.DARKGRAY);
        handArea.setStroke(Color.SADDLEBROWN);
        handArea.setArcWidth(30);
        handArea.setArcHeight(20);
        handGroup.getChildren().add(handArea);

        handGroup.getChildren().add(handCardGroup);

        //Decline button
        Button declineButton = new Button();
        declineButton.setLayoutX(selectArea.getX());
        declineButton.setLayoutY(height - 500);
        declineButton.setMinWidth(100);
        declineButton.setMinHeight(40);
        declineButton.setText("Decline");
        declineButton.setOnAction(e -> {
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TOURNAMENT_PARTICIPATION_QUERY, NetworkMessage.pack(true));
            NetworkManager.get().sendNetMessageToServer(msg);
            stage.close();
        });
        
        //Accept button
        Button acceptButton = new Button();
        acceptButton.setLayoutX(handArea.getX() + handArea.getWidth() - 100);
        acceptButton.setLayoutY(height - 500);
        acceptButton.setMinWidth(100);
        acceptButton.setMinHeight(40);
        acceptButton.setText("Battle!");
        acceptButton.setOnAction(e -> {
            int[] cardsToSend = new int[selectedCards.size()];
            for (int i = 0; i < selectedCards.size(); i++){
                cardsToSend[i] = selectedCards.get(i).getID();
            }

            if (!selectedCards.isEmpty()) {
                // TEMPORARY DISCARDING
                // TODO: discarding should be done only if there is more than one player participating
                int[] cardIDs = new int[hand.size()];
                for(int i = 0; i < hand.size(); i++){
                    cardIDs[i] = hand.get(i).getID();
                }

                LocalClientMessage msg2 = new LocalClientMessage(NetworkMsgType.CARD_DISCARD_X, NetworkMessage.pack(Card.getCardIDsFromArrayList(selectedCards)));
                NetworkManager.get().sendNetMessageToServer(msg2);

                LocalGameManager.get().getLocalPlayer().hand = hand;
                View.get().update();
            }
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TOURNAMENT_PARTICIPATION_QUERY,NetworkMessage.pack(false,cardsToSend));
            NetworkManager.get().sendNetMessageToServer(msg);

            stage.close();
        });
        
        root.getChildren().addAll(selectionGroup, handGroup, declineButton, acceptButton);
        
        updateCards();

        Scene scene = new Scene(root,width,height);
        stage.setScene(scene);
        stage.setTitle("(" + (LocalGameManager.get().getLocalPlayer().getPlayerNum() + 1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName() + ") A Tournament begins!");
        stage.showAndWait();
    }

    private void updateCards() {
        //Clears both groups.
        selectionCardGroup.getChildren().clear();
        handCardGroup.getChildren().clear();

        //Calculates the BP of all selected cards
        int bpVal = 0;
        //todo clean this up
        for(Card c: selectedCards){
            if(c instanceof FoeCard){
                bpVal += ((FoeCard) c).getBP();
            }
            else if(c instanceof WeaponCard){
                bpVal += ((WeaponCard) c).getBP();
            }
            else if(c instanceof AllyCard){
                bpVal += ((AllyCard) c).getBP();
            }
            else if(c instanceof AmourCard){
                bpVal += ((AmourCard) c).getBP();
            }
        }

        //totalBPLabel.setText("You have selected " + selectedCards.size() + " card(s) for a total BP value of " + bpVal);
        totalBPLabel.setText("Rank BP: " + String.valueOf(LocalGameManager.get().getLocalPlayer().getBattlePoints()) +"\n" +
                "Selected weapon(s), Amour, and Allies BP: " + String.valueOf(bpVal) +"\n" +
                "Total BP: " + String.valueOf(LocalGameManager.get().getLocalPlayer().getBattlePoints() + bpVal));

        //Drawing the selected cards
        for(int i = 0; i < selectedCards.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(selectArea.getX() + 10 + (i%8)*110);
            aCard.setY(selectArea.getY() + 10 + Math.floorDiv(i,8)*150);
            aCard.setImage(advCards);
            aCard.setViewport(View.getAdvCard(selectedCards.get(i).getID()));
            selectionCardGroup.getChildren().add(aCard);

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    hand.add(selectedCards.get(finalI));
                    selectedCards.remove(finalI);
                    //Once moving a card, reupdate all the cards.
                    updateCards();
                    errorLabel.setVisible(false);
                }
            });
        }

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
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    //if(hand.get(finalI) instanceof AmourCard){
                    //    LocalGameManager.get().getLocalPlayer().setAmour((AmourCard) hand.get(finalI));
                    //    hand.remove(finalI);
                    //    updateCards();
                    //    NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_AMOUR,NetworkMessage.pack(hand.get(finalI).getID())));
                    //    return;
                    //}

                    if(!validateCardSelection(hand.get(finalI))){
                        //Shows an error label because two of the same cards are selected.
                        errorLabel.setVisible(true);
                        return;
                    }
                    selectedCards.add(hand.get(finalI));
                    hand.remove(finalI);
                    updateCards();
                    errorLabel.setVisible(false);
                }
            });
        }
    }

    public boolean validateCardSelection(Card c){
        //Simply checks to see if card is already in selection or is a foe card.
        if(c instanceof FoeCard || c instanceof TestCard)
            return false;

        for (Card x: selectedCards) {
            if(c.getID() == x.getID())
                return false;
        }
        return true;
    }
}
