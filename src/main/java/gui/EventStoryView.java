package gui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;
import network.*;

import java.io.File;
import java.util.ArrayList;

public class EventStoryView {

    //Window sizes
    private int width = 300;
    private int height = 500;

    //List of cards
    ArrayList<Card> hand;
    ArrayList<Card> selectedCards;

    //Images of all cards
    Image advCards;
    Image storyCards;

    public int drawerID;
    public EventCard eventCard;

    private boolean discardCards = false;

    //For safety checks.
    private boolean choosingFoes = true;
    private int foeCount = 0;

    //Groups of each ImageView that contains only the card ImageViews.
    Group selectionCardGroup = new Group();
    Group handCardGroup = new Group();

    //Rectangles to show each area
    Rectangle selectArea;
    Rectangle handArea;

    private Label errorLabel;

    public EventStoryView(int playerID, int eventCardID){

        drawerID = playerID;
        eventCard = (EventCard) Card.getCardByID(eventCardID);

        hand = LocalGameManager.get().getLocalPlayer().hand;
        selectedCards = new ArrayList<>();

        //Loads the image
        advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());
        storyCards = new Image(new File("src/resources/storyComposite.jpg").toURI().toString());

         if(eventCard instanceof KingsCallToArmsEvent){
            discardCards = true;
            width = 900;
            height = 800;

            //Checks to see if the user has any weapons.
            for(Card c: hand){
                if(c instanceof WeaponCard){
                    choosingFoes = false;
                }
            }

            //Checks to see how many foes the user has.
            if(choosingFoes){
                for(Card c: hand){
                    if(c instanceof FoeCard){
                        foeCount++;
                    }
                }
            }

         }


        Platform.runLater(() -> setup());

    }

    public void updateCards(){
        //Clears both groups.
        selectionCardGroup.getChildren().clear();
        handCardGroup.getChildren().clear();

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

    public void setup(){
        Stage stage = new Stage();
        Group mainGroup = new Group();

        Font largeFont = new Font("Arial", 20);

        if(!discardCards) {

            Label cardLabel = new Label("A new event has been drawn!");
            cardLabel.setLayoutX(0);
            cardLabel.setLayoutY(10);
            mainGroup.getChildren().add(cardLabel);


            ImageView eventCardView = new ImageView();

            eventCardView.setFitWidth(width - 10);
            eventCardView.setFitHeight(height - 100);
            eventCardView.setX(10);
            eventCardView.setY(40);

            eventCardView.setPreserveRatio(true);
            eventCardView.setImage(storyCards);
            Rectangle2D r = View.getStoryCard(eventCard.getID());
            eventCardView.setViewport(r);
            mainGroup.getChildren().add(eventCardView);

        }
        else{

            Group selectionGroup = new Group();

            //The areas for the selected cards and the cards in the hand
            selectArea = new Rectangle(5, 40,240,160);
            handArea = new Rectangle(5,400,width - 10,310);

            Label eventCardLabel = new Label("Event Card: ");
            eventCardLabel.setLayoutX(500);
            eventCardLabel.setLayoutY(20);
            mainGroup.getChildren().add(eventCardLabel);

            Rectangle eventCardArea = new Rectangle(500,40,210,300);
            eventCardArea.setFill(Color.DARKCYAN);
            eventCardArea.setStroke(Color.SADDLEBROWN);
            eventCardArea.setArcWidth(30);
            eventCardArea.setArcHeight(20);
            mainGroup.getChildren().add(eventCardArea);


            ImageView eventCardView = new ImageView();
            eventCardView.setFitWidth(200);
            eventCardView.setFitHeight(280);
            eventCardView.setX(505);
            eventCardView.setY(50);
            eventCardView.setPreserveRatio(true);
            eventCardView.setImage(storyCards);
            Rectangle2D r = View.getStoryCard(eventCard.getID());
            eventCardView.setViewport(r);
            mainGroup.getChildren().add(eventCardView);


            Label selectLabel = new Label();
            if(!choosingFoes)
                selectLabel.setText("Please choose one weapon to discard.");
            else
                selectLabel.setText("Please choose " + String.valueOf(Math.min(2,foeCount) + " foe card(s) to discard."));

            selectLabel.setLayoutY(20);
            mainGroup.getChildren().add(selectLabel);

            selectArea.setFill(Color.DARKBLUE);
            selectArea.setStroke(Color.SADDLEBROWN);
            selectArea.setArcWidth(30);
            selectArea.setArcHeight(20);
            selectionGroup.getChildren().add(selectArea);

            //Error label to notify that two cards of the same type cant be added. Set to invisible by default
            errorLabel = new Label();
            if(!choosingFoes)
                errorLabel.setText("You must select 1 weapon card to discard.");
            else
                errorLabel.setText("You must select " + Math.min(foeCount,2) + " foe card(s) to discard.");

            errorLabel.setFont(largeFont);
            errorLabel.setLayoutX(20);
            errorLabel.setLayoutY(250);
            errorLabel.setTextFill(Color.RED);
            errorLabel.setVisible(false);
            selectionGroup.getChildren().add(errorLabel);

            selectionGroup.getChildren().add(selectionCardGroup);
            mainGroup.getChildren().add(selectionGroup);


            //Hand Area
            Group handGroup = new Group();
            handArea.setFill(Color.DARKGRAY);
            handArea.setStroke(Color.SADDLEBROWN);
            handArea.setArcWidth(30);
            handArea.setArcHeight(20);
            handGroup.getChildren().add(handArea);

            handGroup.getChildren().add(handCardGroup);
            mainGroup.getChildren().add(handGroup);

            //Draws all the cards
            updateCards();
        }

        Button okButton = new Button();
        okButton.setFont(largeFont);
        okButton.setText("OK");
        okButton.setLayoutX(10);
        okButton.setLayoutY(height - 50);
        okButton.setMinWidth(width - 20);
        okButton.setMinHeight(30);
        okButton.setOnAction(e -> {
            if(discardCards){
                if(!choosingFoes){
                    if(selectedCards.isEmpty()) {
                        errorLabel.setVisible(true);
                        return;
                    }
                }
                else
                    if(selectedCards.size() < Math.min(foeCount,2)) {
                        errorLabel.setVisible(true);
                        return;
                    }
            }

            LocalGameManager.get().getLocalPlayer().hand = hand;
            View.get().update();

            //LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.UPDATE_HAND, NetworkMessage.pack(Card.getCardIDsFromArrayList(hand)));
            //NetworkManager.get().sendNetMessageToServer(msg);

            LocalClientMessage msg2 = new LocalClientMessage(NetworkMsgType.CARD_DISCARD_X, NetworkMessage.pack(Card.getCardIDsFromArrayList(selectedCards)));
            NetworkManager.get().sendNetMessageToServer(msg2);

            stage.close();
        });
        mainGroup.getChildren().add(okButton);


        //Puts everything together
        Scene s1 = new Scene(mainGroup,width,height);

        //Opens the window and waits.
        stage.setScene(s1);
        stage.setTitle(String.valueOf(LocalGameManager.get().getLocalPlayer().getPlayerNum()) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName());
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
    }

    private boolean validateCardSelection(Card c){
        if(!choosingFoes){
            if(!(c instanceof WeaponCard))
                return false;
            if(selectedCards.size() >= 1)
                return false;
        }
        else{
            if(!(c instanceof FoeCard))
                return false;
            if(selectedCards.size() >= 2)
                return false;
        }
        return true;
    }

}
