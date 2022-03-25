package gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.*;
import network.*;

import java.util.ArrayList;

public class QuestSponsorView {

    // Quest info
    private int stagesSelected;

    // Window size
    private final int width;
    private final int height;

    // Lists of cards
    ArrayList<Card> hand;
    ArrayList<ArrayList<Card>> selectedCards;

    // For drawing each card
    ArrayList<ImageView> handViews;
    ArrayList<ArrayList<ImageView>> selectionViews;

    // Hand and Selection backgrounds
    Rectangle handRect;
    Rectangle selectionRect;

    Button yesBtn;

    private Group selectionCardGroup;
    private Group handCardGroup;

    private final QuestCard questCard;

    public QuestSponsorView(QuestCard aQuestCard) {
        questCard = aQuestCard;
        stagesSelected = 0;
        width = 960;
        height = 350 + 115 * questCard.getStages();

        hand = new ArrayList<>(LocalGameManager.get().getLocalPlayer().hand);
        selectedCards = new ArrayList<>();
        for (int i = 0; i < questCard.getStages(); ++i) {
            selectedCards.add(new ArrayList<>());
        }
        handViews = new ArrayList<>();
        selectionViews = new ArrayList<>();

        Platform.runLater(this::setup);

    }

    private void updateCards() {
        // Clear all drawn cards
        handCardGroup.getChildren().clear();
        selectionCardGroup.getChildren().clear();

        // Enable accept button if enough foes selected
        yesBtn.setDisable(stagesSelected != questCard.getStages());


        //Drawing all selected cards
        for(int i = 0; i < selectedCards.size(); i++){
            for (int j = 0; j < selectedCards.get(i).size(); j++) {
                ImageView aCard = new ImageView();
                aCard.setFitWidth(75);
                aCard.setFitHeight(105);
                aCard.setPreserveRatio(true);
                aCard.setX(selectionRect.getX() + 10 + j*85);
                aCard.setY(selectionRect.getY() + 10 + i*115);
                aCard.setImage(View.get().getAdvCards());
                aCard.setViewport(View.getAdvCard(selectedCards.get(i).get(j).getID()));
                selectionCardGroup.getChildren().add(aCard);

                int finalI = i;
                int finalJ = j;
                aCard.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        // If unselecting a foe or test, remove that card and all weapons it had + all foes/tests and weapons after it
                        if (selectedCards.get(finalI).get(finalJ) instanceof FoeCard || selectedCards.get(finalI).get(finalJ) instanceof TestCard) {
                            while (stagesSelected > finalI) {
                                int counter = 0;
                                // removes all cards from the last stage
                                while (counter < selectedCards.get(stagesSelected-1).size()) {
                                    hand.add(selectedCards.get(stagesSelected-1).get(counter));
                                    selectedCards.get(stagesSelected-1).remove(counter);
                                }
                                stagesSelected--;
                            }
                        }
                        // If unselecting a weapon, only remove that weapon
                        else {
                            hand.add(selectedCards.get(finalI).get(finalJ));
                            selectedCards.get(finalI).remove(finalJ);
                        }

                        //Once moving a card, reupdate all the cards.
                        updateCards();
                    }
                });
            }
        }

        //Drawing all cards in hand.
        for(int i = 0; i < hand.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(handRect.getX() + 10 + (i%8)*110);
            aCard.setY(handRect.getY() + 10 + Math.floorDiv(i,8)*150);
            aCard.setImage(View.get().getAdvCards());
            aCard.setViewport(View.getAdvCard(hand.get(i).getID()));
            handCardGroup.getChildren().add(aCard);

            //TODO: allow tests to be selected

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    System.out.println(stagesSelected);
                    if(hand.get(finalI).getID() <= 6){      // selected a weapon
                        if (validateWeapon(hand.get(finalI))) {     // ensures no duplicates and foes are always picked first
                            selectedCards.get(stagesSelected-1).add(hand.get(finalI));
                            hand.remove(finalI);
                        }
                    } else if (hand.get(finalI).getID() <= 21 && stagesSelected < questCard.getStages()) {    // selected a foe or test and stages are not full
                        if (hand.get(finalI).getID() > 17) {    // selected a test
                            for (int j = 0; j < stagesSelected; ++j) {
                                int prevCardID = selectedCards.get(j).get(0).getID();
                                // If a test was already selected, don't allow this new test
                                if (prevCardID > 17 && prevCardID < 22)
                                    return;
                            }
                        }

                        selectedCards.get(stagesSelected).add(hand.get(finalI));
                        hand.remove(finalI);
                        stagesSelected++;
                    }

                    updateCards();
                }
            });
        }
    }

    private void setup() {
        Stage stage = new Stage();
        Group root = new Group();

        // Quest information
        VBox labels = new VBox(10);
        labels.setPrefSize(340, 190);
        labels.relocate(0,20);
        labels.setAlignment(Pos.CENTER);

        Label lblMain = new Label("A new Quest is available!\n\n" + questCard.getName() + "\nWill you sponsor the " + questCard.getStages() + " Stages?");
        lblMain.setFont(new Font("Arial", 20));
        lblMain.setTextAlignment(TextAlignment.CENTER);

        StringBuilder details = new StringBuilder("Select 1 foe or 1 test for each stage\nEach foe may have weapons");
        if (questCard.getSpecialFoes().length != 0) {
            details.append("\nAlt BPs: ");
            details.append(questCard.getSpecialFoes()[0]);
            for (int i = 1; i < questCard.getSpecialFoes().length; ++i)
                details.append(", ").append(questCard.getSpecialFoes()[i]);
        }

        Label lblDetails = new Label(details.toString());
        lblDetails.setTextAlignment(TextAlignment.CENTER);

        Label lblError = new Label("Each stage must have more Battle Points than the last");
        lblError.setTextFill(Color.RED);
        lblError.setVisible(false);
        lblError.setTextAlignment(TextAlignment.CENTER);

        labels.getChildren().addAll(lblMain,lblDetails,lblError);

        // yes and no buttons
        Button noBtn = new Button("Decline");
        noBtn.setPrefSize(110, 40);
        noBtn.relocate(40, 210);
        noBtn.setOnAction(actionEvent -> {
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(true));
            NetworkManager.get().sendNetMessageToServer(msg);
            stage.close();
        });

        yesBtn = new Button("Accept");
        yesBtn.setPrefSize(110, 40);
        yesBtn.relocate(190,210);
        yesBtn.setDisable(true);
        yesBtn.setOnAction(actionEvent -> {
            Card[][] valCards = new Card[questCard.getStages()][];
            int[][] sendCards = new int[questCard.getStages()][];
            ArrayList<Card> discardedCards = new ArrayList<>();
            for (int i = 0; i < questCard.getStages(); i++){
                sendCards[i] = new int[selectedCards.get(i).size()];
                valCards[i] = new Card[selectedCards.get(i).size()];
                for (int j = 0; j < selectedCards.get(i).size(); j++) {
                    sendCards[i][j] = selectedCards.get(i).get(j).getID();
                    valCards[i][j] = selectedCards.get(i).get(j);
                    discardedCards.add(selectedCards.get(i).get(j));
                }
            }

            if(!Quest.isValidSelection(valCards,questCard)){
                System.out.println("CLIENT: Invalid Sponsor Selection");
                lblError.setVisible(true);
                return;
            }
            else
                System.out.println("CLIENT: Valid Sponsor Selection");

            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(false,sendCards));
            NetworkManager.get().sendNetMessageToServer(msg);

            NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.CARD_DISCARD_X,NetworkMessage.pack(Card.getCardIDsFromArrayList(discardedCards))));


            LocalGameManager.get().getLocalPlayer().hand = hand;
            View.get().update();

            stage.close();
        });

        // Area for selected cards
        Group selectionGroup = new Group();

        selectionRect = new Rectangle(340, 10, 605, (115 * questCard.getStages()) + 10);
        selectionRect.setFill(Color.PALEVIOLETRED);
        selectionRect.setStroke(Color.SADDLEBROWN);
        selectionRect.setArcWidth(30);
        selectionRect.setArcHeight(20);

        selectionCardGroup = new Group();

        selectionGroup.getChildren().addAll(selectionRect,selectionCardGroup);

        // Area cards in hand
        Group handGroup = new Group();

        handRect = new Rectangle(35, height-320, 890, 310);
        handRect.setFill(Color.DARKGRAY);
        handRect.setStroke(Color.SADDLEBROWN);
        handRect.setArcWidth(30);
        handRect.setArcHeight(20);

        handCardGroup = new Group();

        handGroup.getChildren().addAll(handRect,handCardGroup);

        updateCards();  // to initially show cards in hand

        root.getChildren().addAll(labels,noBtn,yesBtn,selectionGroup,handGroup);
        Scene scene = new Scene(root,width,height);
        stage.setScene(scene);
        stage.setTitle((LocalGameManager.get().getLocalPlayer().getPlayerNum() + 1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName());
        stage.showAndWait();
    }

    private boolean validateWeapon(Card c) {
        if (stagesSelected < 1) return false;
        if (selectedCards.get(stagesSelected-1).get(0) instanceof TestCard) return false;
        for (Card x : selectedCards.get(stagesSelected-1)) {
            if (c.getID() == x.getID()) {
                return false;
            }
        }
        return true;
    }

}
