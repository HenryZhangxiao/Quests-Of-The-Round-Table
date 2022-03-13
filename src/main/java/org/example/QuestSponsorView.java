package org.example;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class QuestSponsorView {

    // Quest info
    private final String name;
    private final int numStages;
    private int foesSelected;

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
        name = questCard.name;
        numStages = questCard.stages;
        foesSelected = 0;
        width = 1000;
        height = 550 + 150 * numStages;

        hand = LocalGameManager.get().getLocalPlayer().hand;
        selectedCards = new ArrayList<>();
        for (int i = 0; i < numStages; ++i) {
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
        yesBtn.setDisable(foesSelected != numStages);


        //Drawing all selected cards
        for(int i = 0; i < selectedCards.size(); i++){
            for (int j = 0; j < selectedCards.get(i).size(); j++) {
                ImageView aCard = new ImageView();
                aCard.setFitWidth(100);
                aCard.setFitHeight(140);
                aCard.setPreserveRatio(true);
                aCard.setX(selectionRect.getX() + 10 + j*110);
                aCard.setY(selectionRect.getY() + 10 + i*150);
                aCard.setImage(View.get().getAdvCards());
                aCard.setViewport(View.get().getAdvCard(selectedCards.get(i).get(j).id));
                selectionCardGroup.getChildren().add(aCard);

                int finalI = i;
                int finalJ = j;
                aCard.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        if (selectedCards.get(finalI).get(finalJ) instanceof FoeCard)
                            foesSelected--;
                        hand.add(selectedCards.get(finalI).get(finalJ));
                        selectedCards.get(finalI).remove(finalJ);

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
            aCard.setViewport(View.get().getAdvCard(hand.get(i).id));
            handCardGroup.getChildren().add(aCard);

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    System.out.println(foesSelected);
                    if(hand.get(finalI).getID() <= 6){      // selected a weapon
                        if (validateWeapon(hand.get(finalI))) {     // ensures no duplicates and foes are always picked first
                            selectedCards.get(foesSelected-1).add(hand.get(finalI));
                            hand.remove(finalI);
                        }
                    } else if (hand.get(finalI).getID() <= 17 && foesSelected < numStages) {    // selected a foe and foes can still be selected
                        selectedCards.get(foesSelected).add(hand.get(finalI));
                        hand.remove(finalI);
                        foesSelected++;
                    }
                    updateCards();
                }
            });
        }
    }

    private void setup() {
        Stage stage = new Stage();
        Group root = new Group();

        //TODO: add info about alt BPs
        Label lblMain = new Label("A quest is available! " + name + "\nWould you like to sponsor it? (select " + numStages + " foes)");
        lblMain.relocate(20,20);

        Button noBtn = new Button("Decline");
        noBtn.relocate(20, 100);
        noBtn.setOnAction(actionEvent -> {
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(true));
            NetworkManager.get().sendNetMessageToServer(msg);
            stage.close();
        });

        Label lblError = new Label("The total BP of each stage must be greater than that of the previous stage");
        lblError.setTextFill(Color.RED);
        lblError.relocate(200, 100);
        lblError.setVisible(false);

        yesBtn = new Button("Accept");
        yesBtn.relocate(100,100);
        yesBtn.setDisable(true);
        yesBtn.setOnAction(actionEvent -> {
            Card[][] valCards = new Card[numStages][];
            int[][] sendCards = new int[numStages][];
            for (int i = 0; i < numStages; i++){
                sendCards[i] = new int[selectedCards.get(i).size()];
                valCards[i] = new Card[selectedCards.get(i).size()];
                for (int j = 0; j < selectedCards.get(i).size(); j++) {
                    sendCards[i][j] = selectedCards.get(i).get(j).getID();
                    valCards[i][j] = selectedCards.get(i).get(j);
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

            int[] cardIDs = new int[hand.size()];
            for(int i = 0; i < hand.size(); i++){
                cardIDs[i] = hand.get(i).id;
            }

            LocalClientMessage msg2 = new LocalClientMessage(NetworkMsgType.UPDATE_HAND,NetworkMessage.pack(cardIDs));
            NetworkManager.get().sendNetMessageToServer(msg2);

            LocalGameManager.get().getLocalPlayer().hand = hand;
            View.get().update();

            stage.close();
        });

        Group selectionGroup = new Group();

        selectionRect = new Rectangle(55, 200, 890, (150 * numStages) + 10);
        selectionRect.setFill(Color.PALEVIOLETRED);
        selectionRect.setStroke(Color.SADDLEBROWN);
        selectionRect.setArcWidth(30);
        selectionRect.setArcHeight(20);

        selectionCardGroup = new Group();

        selectionGroup.getChildren().addAll(selectionRect,selectionCardGroup);

        Group handGroup = new Group();

        handRect = new Rectangle(55, height-310, 890, 310);
        handRect.setFill(Color.DARKGRAY);
        handRect.setStroke(Color.SADDLEBROWN);
        handRect.setArcWidth(30);
        handRect.setArcHeight(20);

        handCardGroup = new Group();

        handGroup.getChildren().addAll(handRect,handCardGroup);

        updateCards();

        root.getChildren().addAll(lblMain,noBtn,yesBtn,lblError,selectionGroup,handGroup);
        Scene scene = new Scene(root,width,height);
        stage.setScene(scene);
        stage.setTitle(LocalGameManager.get().getLocalPlayer().getPlayerNum() + " " + LocalGameManager.get().getLocalPlayer().getPlayerName());
        stage.showAndWait();
    }

    private boolean validateWeapon(Card c) {
        if (foesSelected < 1) return false;
        for (Card x : selectedCards.get(foesSelected-1)) {
            if (c.getID() == x.getID()) {
                return false;
            }
        }
        return true;
    }

}
