package gui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Stage;
import model.*;
import network.*;

import java.io.File;
import java.util.ArrayList;

public class QuestParticipationView {

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
    Rectangle amourArea;

    //Labels to show the total BP of the selection and for an error
    Label totalBPLabel;
    Label errorLabel;
    Label amourLabel;

    //Groups of each ImageView that contains only the card ImageViews.
    Group selectionCardGroup = new Group();
    Group handCardGroup = new Group();

    //Shows the decline button or not. It wont show the decline button if on a seperate stage of the quest.
    private boolean showDeclineButton = false;

    private QuestCard questCard;
    private Card[] stageCards;
    private Button merlinButton;

    public QuestParticipationView(boolean showDeclineButton, int questID, Card[] stageCards){
        //Gets the hand of the local player
        hand = new ArrayList<>(LocalGameManager.get().getLocalPlayer().hand);
        selectedCards = new ArrayList<>();
        handViews = new ArrayList<>();
        selectionViews = new ArrayList<>();

        //Loads the image
        advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());

        this.showDeclineButton = showDeclineButton;
        this.questCard = (QuestCard) Card.getCardByID(questID);
        this.stageCards = stageCards;

        //To prevent JavaFX from yelling about threads.
        Platform.runLater(this::setup);

    }

    //This is called to redraw all the cards when a change is made.
    private void updateCards(){
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
        }
        //Calculates all BP bonuses for any allies or amours the player has in play
        int allyBP = AllyCard.getBPForAllies(LocalGameManager.get().getLocalPlayer().getAllies(),questCard,LocalGameManager.get().getLocalPlayer().getAmour());

        //totalBPLabel.setText("You have selected " + String.valueOf(selectedCards.size()) + " card(s) for a total BP value of " + String.valueOf(bpVal));
        totalBPLabel.setText("Rank BP: " + String.valueOf(LocalGameManager.get().getLocalPlayer().getBattlePoints()) +"\n" +
                "Allies in play BP: " + String.valueOf(allyBP) + "\n" +
                "Selected weapon(s) BP: " + String.valueOf(bpVal) +"\n" +
                "Total BP: " + String.valueOf(LocalGameManager.get().getLocalPlayer().getBattlePoints() + bpVal + allyBP));


        //Enables Merlin button if in allies
        boolean enableMerlin = false;
        for(Card c: LocalGameManager.get().getLocalPlayer().getAllies()){
            if(c instanceof MerlinAlly)
                enableMerlin = true;
        }
        merlinButton.setDisable(!enableMerlin);

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
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if(hand.get(finalI) instanceof AmourCard){
                        LocalGameManager.get().getLocalPlayer().setAmour((AmourCard) hand.get(finalI));
                        LocalGameManager.get().getLocalPlayer().hand.remove(hand.get(finalI));
                        hand.remove(finalI);
                        Platform.runLater(() -> {
                            View.get().update();}
                        );
                        NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_AMOUR,NetworkMessage.pack(hand.get(finalI).getID())));
                        updateCards();
                        return;
                    }
                    else if(hand.get(finalI) instanceof AllyCard){
                        LocalGameManager.get().getLocalPlayer().addAlly((AllyCard) hand.get(finalI));
                        LocalGameManager.get().getLocalPlayer().hand.remove(hand.get(finalI));
                        Platform.runLater(() -> {
                            View.get().update();}
                        );
                        hand.remove(finalI);
                        NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.UPDATE_ALLIES,NetworkMessage.pack(LocalGameManager.get().getLocalPlayer().getAllyCardIDs())));
                        updateCards();
                        return;
                    }

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

    //Initial setup for the window.
    private void setup(){
        Stage stage = new Stage();
        //Everything is added to this main group.
        Group mainGroup = new Group();

        //The areas for the selected cards and the cards in the hand
        selectArea = new Rectangle(55,200,720,180);
        handArea = new Rectangle(55,height-400,890,310);
        amourArea = new Rectangle(800,200,130,180);

        Font largeFont = new Font("Arial", 20);

        merlinButton = new Button("Use Merlin");
        merlinButton.setTooltip(new Tooltip("Use Merlin to preview this stage's cards!"));
        merlinButton.setLayoutX(amourArea.getX());
        merlinButton.setLayoutY(100);
        merlinButton.setMinWidth(100);
        merlinButton.setMinHeight(40);
        merlinButton.setOnAction(e -> {
            PreviewStageView v = new PreviewStageView(Card.getCardListFromCardArray(stageCards),questCard);
            LocalGameManager.get().setUsedMerlin(true);
            merlinButton.setDisable(true);
        });
        mainGroup.getChildren().add(merlinButton);

        //Selected Cards subgroup
        Group selectionGroup = new Group();
        //TODO: add info about stage number and number of cards in the stage
        Label selectionLabel = new Label("Select cards for the stage of the quest!");
        selectionLabel.setFont(largeFont);
        selectionLabel.setLayoutX(20);
        selectionLabel.setLayoutY(50);
        selectionGroup.getChildren().add(selectionLabel);

        totalBPLabel = new Label("You have selected 0 card(s) for a total BP value of 0");
        totalBPLabel.setFont(largeFont);
        totalBPLabel.setLayoutX(selectArea.getX());
        totalBPLabel.setLayoutY(100);
        selectionGroup.getChildren().add(totalBPLabel);


        selectArea.setFill(Color.DARKBLUE);
        selectArea.setStroke(Color.SADDLEBROWN);
        selectArea.setArcWidth(30);
        selectArea.setArcHeight(20);
        selectionGroup.getChildren().add(selectArea);

        //Error label to notify that two cards of the same type cant be added. Set to invisible by default
        errorLabel = new Label("You cannot add two cards of the same type or play any foe cards!");
        errorLabel.setFont(largeFont);
        errorLabel.setLayoutX(selectArea.getX());
        errorLabel.setLayoutY(selectArea.getY() + selectArea.getHeight() + 10);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        selectionGroup.getChildren().add(errorLabel);

        selectionGroup.getChildren().add(selectionCardGroup);
        mainGroup.getChildren().add(selectionGroup);

        //Amour Card Area
        amourArea.setFill(Color.YELLOW);
        amourArea.setStroke(Color.SADDLEBROWN);
        amourArea.setArcWidth(30);
        amourArea.setArcHeight(20);
        mainGroup.getChildren().add(amourArea);

        amourLabel = new Label("No Amour\n Selected");
        amourLabel.setLayoutX(amourArea.getX() + 20);
        amourLabel.setLayoutY(amourArea.getY() + 10);
        amourLabel.setFont(largeFont);
        mainGroup.getChildren().add(amourLabel);


        //Hand Area
        Group handGroup = new Group();
        handArea.setFill(Color.DARKGRAY);
        handArea.setStroke(Color.SADDLEBROWN);
        handArea.setArcWidth(30);
        handArea.setArcHeight(20);
        handGroup.getChildren().add(handArea);

        handGroup.getChildren().add(handCardGroup);
        mainGroup.getChildren().add(handGroup);

        //Decline to participate button
        if(showDeclineButton) {
            Button declineButton = new Button();
            declineButton.setLayoutX(selectArea.getX());
            declineButton.setLayoutY(height - 500);
            declineButton.setMinWidth(100);
            declineButton.setMinHeight(40);
            declineButton.setText("Decline");

            declineButton.setOnAction(e -> {
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(true));
                NetworkManager.get().sendNetMessageToServer(msg);
                stage.close();
            });

            mainGroup.getChildren().add(declineButton);
        }

        //Accept button
        Button acceptButton = new Button();
        acceptButton.setLayoutX(handArea.getX() + handArea.getWidth() - 100);
        acceptButton.setLayoutY(height - 500);
        acceptButton.setMinWidth(100);
        acceptButton.setMinHeight(40);
        acceptButton.setText("Battle!");

        acceptButton.setOnAction(e -> {

            int[] sendCards = new int[selectedCards.size()];
            for (int i = 0; i < selectedCards.size(); i++){
                sendCards[i] = selectedCards.get(i).getID();
            }

            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY,NetworkMessage.pack(false,sendCards));
            NetworkManager.get().sendNetMessageToServer(msg);

            int[] cardIDs = new int[hand.size()];
            for(int i = 0; i < hand.size(); i++){
                cardIDs[i] = hand.get(i).getID();
            }

            //LocalClientMessage msg2 = new LocalClientMessage(NetworkMsgType.UPDATE_HAND,NetworkMessage.pack(cardIDs));
            //NetworkManager.get().sendNetMessageToServer(msg2);

            if(selectedCards.size() != 0) {
                //For discarding
                LocalClientMessage msg3 = new LocalClientMessage(NetworkMsgType.CARD_DISCARD_X, NetworkMessage.pack(Card.getCardIDsFromArrayList(selectedCards)));
                NetworkManager.get().sendNetMessageToServer(msg3);
            }

            LocalGameManager.get().getLocalPlayer().hand = hand;
            View.get().update();

            stage.close();
        });

        mainGroup.getChildren().add(acceptButton);


        //Puts everything together
        Scene s1 = new Scene(mainGroup,width,height);

        //Draws all the cards
        updateCards();

        //Opens the window and waits.
        stage.setScene(s1);
        stage.setTitle((LocalGameManager.get().getLocalPlayer().getPlayerNum() + 1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName());
        stage.showAndWait();

    }

    public boolean validateCardSelection(Card c){
        //Simply checks to see if card is already in selection or is a foe card.
        if(c instanceof FoeCard)
            return false;

        for (Card x: selectedCards) {
            if(c.getID() == x.getID())
                return false;
        }
        return true;
    }

}
