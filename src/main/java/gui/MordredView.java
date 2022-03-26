package gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Card;
import model.MordredFoe;
import model.Player;
import network.*;

import java.io.File;

public class MordredView {

    public int width = 1120;
    public int height = 60;

    Image advCards;

    Group mainGroup;

    Rectangle selectedRectangle;
    int selectedPlayer = -1;
    int selectedCard = -1;

    Button okButton;

    public MordredView(int handIndex){
        //Loads the image
        advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());

        mainGroup = new Group();



        setup();
    }

    public void update(){

    }

    public void setup(){


        Stage stage = new Stage();

        int playerNum = LocalGameManager.get().getPlayers().size();

        Player[] players = new Player[playerNum - 1];
        int x = 0;
        for(int i = 0; i < playerNum; i++){
            if(LocalGameManager.get().getPlayers().get(i).getPlayerNum() != LocalGameManager.get().getLocalPlayer().getPlayerNum()){
                players[x] = LocalGameManager.get().getPlayers().get(i);
                x++;
            }
        }

        height += 240 + (180 * (players.length - 1));

        Label infoLabel = new Label("Select a player's ally to remove: ");
        infoLabel.setLayoutX(10);
        infoLabel.setLayoutY(10);
        mainGroup.getChildren().add(infoLabel);

        for(int i = 0; i < players.length; i++){
            Group g = new Group();

            Rectangle r = new Rectangle(10, 60 + (180 * i), width - 20, 160);
            r.setFill(Color.LIGHTBLUE);
            r.setStroke(Color.SADDLEBROWN);
            r.setArcWidth(30);
            r.setArcHeight(20);
            g.getChildren().add(r);

            Label nameL = new Label(players[i].getPlayerName() + "'s Allies: ");
            nameL.setLayoutX(r.getX());
            nameL.setLayoutY(r.getY() - 20);
            g.getChildren().add(nameL);

            Label noAlliesLabel = new Label(players[i].getPlayerName() + " has no Allies in play.");
            noAlliesLabel.setLayoutX(r.getX() + 30);
            noAlliesLabel.setLayoutY(r.getY() + r.getHeight()/2 - 10);
            noAlliesLabel.setVisible(players[i].getAllies().size() == 0);
            g.getChildren().add(noAlliesLabel);

            for(int k = 0; k < players[i].getAllies().size(); k++){
                ImageView img = new ImageView();
                img.setFitWidth(100);
                img.setFitHeight(140);
                img.setPreserveRatio(true);
                img.setX(r.getX() + 5 + (k * 110));
                img.setY(r.getY() + 10);
                img.setImage(advCards);
                img.setViewport(View.getAdvCard(players[i].getAllies().get(k).getID()));
                g.getChildren().add(img);

                int finalK = k;
                int finalI = i;
                img.setOnMouseClicked(e -> {
                    if(e.getButton() == MouseButton.PRIMARY){
                        selectedRectangle.setX(img.getX() - 5);
                        selectedRectangle.setY(img.getY() - 5);
                        selectedRectangle.setVisible(true);
                        selectedPlayer = players[finalI].getPlayerNum();
                        selectedCard = players[finalI].getAllies().get(finalK).getID();
                        okButton.setDisable(false);
                    }
                });
            }

            mainGroup.getChildren().add(g);
        }

        selectedRectangle = new Rectangle(0,0,110, 150);
        selectedRectangle.setStroke(Color.SADDLEBROWN);
        selectedRectangle.setFill(new Color(0.8f,0,0,0.4f));
        selectedRectangle.setArcWidth(30);
        selectedRectangle.setArcHeight(20);
        selectedRectangle.setVisible(false);
        mainGroup.getChildren().add(selectedRectangle);

        Button cancelButton = new Button("Cancel");
        cancelButton.setMinWidth( width/2 - 10);
        cancelButton.setMinHeight(40);
        cancelButton.setLayoutX(5);
        cancelButton.setLayoutY(height - 50);
        cancelButton.setOnAction(e -> {
            stage.close();
        });
        mainGroup.getChildren().add(cancelButton);

        okButton = new Button("OK");
        okButton.setMinWidth(width / 2 - 10);
        okButton.setMinHeight(40);
        okButton.setLayoutX(width / 2 + 5);
        okButton.setLayoutY(height - 50);
        okButton.setDisable(true);
        okButton.setOnAction(e -> {

            NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.MORDRED_DISCARD, NetworkMessage.pack(selectedPlayer,selectedCard)));

            stage.close();
        });
        mainGroup.getChildren().add(okButton);

        //Puts everything together
        Scene s1 = new Scene(mainGroup,width,height);

        //Draws all the cards
        update();

        stage.setOnCloseRequest(e -> e.consume());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(View.get().getScene().getWindow());

        //Opens the window and waits.
        stage.setScene(s1);
        stage.setTitle("(" +(LocalGameManager.get().getLocalPlayer().getPlayerNum() + 1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName() + ") Use Mordred to remove an opponent's ally!");
        stage.showAndWait();
    }
}
