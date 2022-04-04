package network;

import gui.*;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;
import java.util.Optional;

public class LocalGameManager implements ClientEventListener{

    //Singleton
    public static LocalGameManager localGameManager;

    private ArrayList<Player> _players;

    private Player localPlayer;

    private int turnID = -1;
    private ArrayList<Card> _cardsOnBoard;

    private ArrayList<Card> _adventurePile;
    private ArrayList<Card> _storyPile;

    private boolean gameStarted = false;
    private int connectedPlayerCount = 0;

    private boolean usedMerlin = false;

    private boolean canDrawStory = true;

    private Stage playAgainStage;

    private LocalGameManager(){
        _players = new ArrayList<>();
        _cardsOnBoard = new ArrayList<>();
        _adventurePile = new ArrayList<>();
        _storyPile = new ArrayList<>();
        NetworkManager.get().addListener(this);
    }

    public static LocalGameManager get(){
        if(localGameManager == null)
            localGameManager = new LocalGameManager();
        return localGameManager;
    }


    //region Helpers
    public Player getPlayerByID(int plyID){
        for(Player p: _players){
            if(p.getPlayerNum() == plyID)
                return p;
        }
        return null;
    }

    public Player getLocalPlayer(){
        if(localPlayer != null)
            return localPlayer;
        else {
            for (int i = 0; i < _players.size(); i++) {
                if (_players.get(i).getPlayerNum() == NetworkManager.get().getLocalPlayerID()) {
                    return _players.get(i);
                }
            }
            return null;
        }
    }

    public boolean isMyTurn(){
        if (localPlayer != null)
            return localPlayer.getPlayerNum() == turnID;
        else
            return false;
    }

    public int getTurnID(){
        return turnID;
    }

    public void finishTurn(){
        if(!isMyTurn() || localPlayer.hand.size() > 12)
            return;

        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TURN_CHANGE,null);
        NetworkManager.get().sendNetMessageToServer(msg);
        canDrawStory = true;
    }

    public String[] getAllPlayerNames(){
        String[] x = new String[_players.size()];
        for (int i = 0; i < _players.size(); i++){
            x[i] = _players.get(i).getPlayerName();
        }
        return x;
    }

    public ArrayList<Player> getPlayers(){
        return _players;
    }

    public boolean isGameStarted(){return gameStarted;}

    public int getConnectedPlayerCount(){return connectedPlayerCount;}

    public ArrayList<Card> getAdvPile() {return _adventurePile;}

    public ArrayList<Card> getStoryPile() {return _storyPile;}

    public boolean getUsedMerlin() {return usedMerlin;}
    public void setUsedMerlin(boolean used) {usedMerlin = used;}

    public void startGame(boolean riggedGame){
        if(!NetworkManager.get().isHost())
            return;
        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.START_GAME, NetworkMessage.pack(riggedGame));
        NetworkManager.get().sendNetMessageToServer(msg);
    }

    public void drawCard() {
        if(!isMyTurn())
            return;
        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.CARD_DRAW, null);
        NetworkManager.get().sendNetMessageToServer(msg);
    }

    public void discardCard(int handIndex) {
        if(!isMyTurn())
            return;

        ArrayList<Object> objs = new ArrayList<>();
        objs.add(localPlayer.getHandCardIDs()[handIndex]);

        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.CARD_DISCARD, objs);
        NetworkManager.get().sendNetMessageToServer(msg);
    }

    public void drawStory() {
        if(!isMyTurn() || !canDrawStory)
            return;
        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.STORY_CARD_DRAW, null);
        NetworkManager.get().sendNetMessageToServer(msg);
        canDrawStory = false;
    }

    //endregion

    @Override
    public void onPlayerConnect(int plyID, String playerName, int[] cardIDs) {
        Player p = new Player(plyID,playerName);

        p.addCardsByIDs(cardIDs);

        _players.add(p);

        connectedPlayerCount++;

        if (plyID == NetworkManager.get().getLocalPlayerID())
            localPlayer = p;
    }

    @Override
    public void onPlayerDisconnect(int plyID, String playerName) {
        int x = -1;
        for (int i = 0; i < _players.size(); i++) {
            if(_players.get(i).getPlayerNum() == plyID){
                x = i;
            }
        }

        if(x != -1)
            _players.remove(x);

        connectedPlayerCount--;

        System.out.println("CLIENT: Player " + playerName + " disconnected. ID: " + String.valueOf(plyID));
    }

    @Override
    public void onStartGame() {
        gameStarted = true;
        turnID = 0;
    }

    @Override
    public void onTurnChange(int idOfPlayer) {
        turnID = idOfPlayer;
        Platform.runLater(() -> {
            View.get().enableTurnButtons();
            View.get().update();
        });

    }

    @Override
    public void onUpdateHand(int plyID, int[] cardIDs) {
        getPlayerByID(plyID).hand.clear();
        for(int i = 0; i < cardIDs.length; i++) {
            getPlayerByID(plyID).addCardByID(cardIDs[i]);
        }
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onUpdateShields(int plyID, int shieldCount) {
        getPlayerByID(plyID).setShields(shieldCount);
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onUpdateAllies(int plyID, int[] cardIDs) {
        getPlayerByID(plyID).setAllies(cardIDs);
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onUpdateAmour(int plyID, int cardID) {
        // clears amours for all players eg end of quest.
        if(plyID == -1){
            for(Player p : _players)
                p.setAmour(null);
        }
        else
            getPlayerByID(plyID).setAmour((AmourCard) Card.getCardByID(cardID));

        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onClearAllies(int plyID) {
        if(plyID == -1){
            for(Player p : _players)
                p.getAllies().clear();
        }
        else
            getPlayerByID(plyID).getAllies().clear();

        Platform.runLater(() -> View.get().update());

    }

    @Override
    public void onDrawCard(int plyID, int cardID) {
        getPlayerByID(plyID).addCardByID(cardID);
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onDrawCardX(int plyID, int[] cardIDs) {
        for(int i = 0; i < cardIDs.length; i++) {
            getPlayerByID(plyID).addCardByID(cardIDs[i]);
        }
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onCardDiscard(int plyID, int cardID) {
        getPlayerByID(plyID).discardCardFromHand(cardID);

        _adventurePile.add(Card.getCardByID(cardID));
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onCardDiscardX(int plyID, int[] cardIDs, boolean runForPlyID) {
        //If we aren't running it for the plyID, then ignore. Useful if local player already discarded cards.
        if(!runForPlyID && plyID == getLocalPlayer().getPlayerNum())
            return;

        if(cardIDs == null){
            System.out.println("CLIENT: CardDiscardX cardids null");
            return;
        }

        if(cardIDs.length == 0){
            System.out.println("CLIENT: CardDiscardX cardids empty");
            return;
        }

        getPlayerByID(plyID).discardCardsFromHand(cardIDs);
        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onStoryDrawCard(int plyID, int cardID) {
        //Called when a player has drawn a story card.
        _storyPile.add(Card.getCardByID(cardID));

        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onQuestBegin(int plyID, int questCardID) {
        //Called when a quest card has been played.
        View.get().logMsg(getPlayerByID(plyID).getPlayerName() + " drew a Quest card!");

    }

    @Override
    public void onQuestSponsorQuery(int questCardID) {
        //called when asking the local player if they would like to sponsor the quest.
        QuestCard c = (QuestCard) Card.getCardByID(questCardID);
        QuestSponsorView q = new QuestSponsorView(c);

    }

    @Override
    public void onQuestParticipateQuery(int sponsorPlyID, int questID, int[] stageCardIDs) {
        //Called when the sponsor has chosen their cards and is asking the local player if they would like to participate.
        Card[] cards = Card.getCardsFromIDArray(stageCardIDs);
        QuestParticipationView q = new QuestParticipationView(true, questID,cards);

    }

    @Override
    public void onQuestStageResult(int questCardID, boolean wonStage, int[] stageCardsIDs, int[] playerCardsIDs) {
        //TODO Show result of stage.
        // wonStage will be true if they won the stage
        if(wonStage) {
            System.out.println("CLIENT: You won the stage.");
            View.get().logMsg("You won the stage.");
        }
        else {
            System.out.println("CLIENT: You lost the stage.");
            View.get().logMsg("You lost the stage.");
        }

        Platform.runLater(() -> View.get().update());
    }

    @Override
    public void onQuestFinalResult(int[] winnerIDs, int[][] sponsorCards) {
        //Called when the quest is over and shows the winning results. sponsorCards is separated by stage. eg: sponsorCards[0] will get stage 1's cards.
        System.out.println("CLIENT: The Quest has ended.");

        if (winnerIDs.length != 0) {
            StringBuilder ss = new StringBuilder(getPlayerByID(winnerIDs[0]).getPlayerName());
            for (int i = 1; i < winnerIDs.length; i++) {
                ss.append(", ").append(getPlayerByID(winnerIDs[i]).getPlayerName());
            }
            View.get().logMsg("The winners of the Quest are: " + ss);
        } else {
            View.get().logMsg("No one completed the Quest.");
        }
        usedMerlin = false;
        View.get().enableEndTurnButton();
    }

    @Override
    public void onEventStoryBegin(int plyID, int eventCardID) {
        EventStoryView e = new EventStoryView(plyID,eventCardID);
        View.get().enableEndTurnButton();
    }

    @Override
    public void onMordredDiscard(int cardUserID, int removedCardPlayerID, int allyCardID) {
        //Simple popup to notify players that Mordred has been used.
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Mordred Attacks!");
            a.setHeaderText("Mordred has removed an Ally from play!");
            a.setContentText(getPlayerByID(cardUserID).getPlayerName() + " used Mordred to remove " + getPlayerByID(removedCardPlayerID).getPlayerName() + "'s " + Card.getCardByID(allyCardID).getName() + " from play!");
            a.showAndWait();
        });
    }

    @Override
    public void onTournamentBegin(int drawerID, int tournamentCardID) {
        System.out.println("CLIENT: Tournament begins");
        View.get().logMsg(getPlayerByID(drawerID).getPlayerName() + " drew a Tournament card!");

    }

    @Override
    public void onTournamentParticipationQuery(int tournamentCardID) {
        //called when asking the local player if they would like to participate in the tournament
        TournamentCard c = (TournamentCard) Card.getCardByID(tournamentCardID);
        // TODO: GUI for participation
        TournamentParticipationView trnmtView = new TournamentParticipationView();

    }

    @Override
    public void onTournamentTie(int tournamentCardID) {
        View.get().logMsg("This Tournament ended in a tie! A new tie-breaking tournament will be played by the winners.");
    }

    @Override
    public void onTournamentFinalResult(int[] winnerIDs) {
        //Called when the tournament is over and shows the winning results (winnerID is [-1] for no winner)
        System.out.println("CLIENT: The Tournament has ended. winner ids are: " + winnerIDs);
        // TODO: GUI for results of tournament (maybe show shields won?)
        if (winnerIDs[0] != -1) {
            StringBuilder ss = new StringBuilder(getPlayerByID(winnerIDs[0]).getPlayerName());
            for (int i = 1; i < winnerIDs.length; i++) {
                ss.append(", ").append(getPlayerByID(winnerIDs[i]).getPlayerName());
            }
            View.get().logMsg("The winners of this Tournament are: " + ss);
        }

        View.get().enableEndTurnButton();
    }

    @Override
    public void onTestBegin(int drawerID, int testID) {

    }

    @Override
    public void onTestBidQuery(int testID, int questID, int currentBid) {
        TestBiddingView testView = new TestBiddingView(questID, testID, currentBid+1);
    }

    @Override
    public void onTestFinalResult(int testID, int winnerID, int currentBid) {
        View.get().logMsg(getPlayerByID(winnerID).getPlayerName() + " bid " + currentBid + " cards and won this stage.");
    }

    @Override
    public void onGameFinalResult(int[] winnerIDs) {
        if(winnerIDs == null || winnerIDs.length == 0)
            return;

        System.out.println("CLIENT: the winner(s) are: ");
        for(int i = 0; i < winnerIDs.length; ++i){
            System.out.println(winnerIDs[i]);
        }

        Platform.runLater(() -> {
            int width = 400;
            int height = 300;

            playAgainStage = new Stage();
            Group mainGroup = new Group();
            playAgainStage.setTitle("Victory!!");

            Label winnerLabel = new Label();
            winnerLabel.setLayoutY(10);
            winnerLabel.setLayoutX(5);
            mainGroup.getChildren().add(winnerLabel);

            if(winnerIDs.length == 1) {
                winnerLabel.setText(getPlayerByID(winnerIDs[0]).getPlayerName() + " has won the game! Congratulations!");
            }
            else{
                String s = "";
                for(int i = 0; i < winnerIDs.length;i++){
                    if(i == winnerIDs.length - 1)
                        s += "and " + getPlayerByID(winnerIDs[i]).getPlayerName();
                    else {
                        if(winnerIDs.length == 2)
                            s += getPlayerByID(winnerIDs[i]).getPlayerName() + " ";
                        else
                            s += getPlayerByID(winnerIDs[i]).getPlayerName() + ", ";
                    }
                }
                winnerLabel.setText("Congratulations to " + s + " for winning the game!");
            }

            Label playAgainLabel = new Label();
            playAgainLabel.setLayoutY(height - 50);
            playAgainLabel.setLayoutX(10);
            mainGroup.getChildren().add(playAgainLabel);

            if(NetworkManager.get().isHost()) {
                playAgainLabel.setText("Would you like to play again?");

                Button playAgainButton = new Button("Play Again?");
                playAgainButton.setLayoutX(width/2 + 10);
                playAgainButton.setLayoutY(height - 50);
                playAgainButton.setMinWidth(width/2 - 20);
                playAgainButton.setMinHeight(40);
                playAgainButton.setOnAction(e -> {
                    NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.GAME_RESET, NetworkMessage.pack(true)));
                    playAgainStage.close();
                });
                mainGroup.getChildren().add(playAgainButton);

                Button exitButton = new Button("Exit Game");
                exitButton.setLayoutX(10);
                exitButton.setLayoutY(height - 50);
                exitButton.setMinWidth(width/2 - 20);
                exitButton.setMinHeight(40);
                exitButton.setOnAction(e -> {
                    NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.GAME_RESET, NetworkMessage.pack(false)));
                    playAgainStage.close();
                });
                mainGroup.getChildren().add(exitButton);

            }
            else{
                playAgainLabel.setText("Waiting for host to exit or play again...");
            }


            //Puts everything together
            Scene s1 = new Scene(mainGroup,width,height);

            playAgainStage.setOnCloseRequest(e -> e.consume());
            playAgainStage.setResizable(false);
            playAgainStage.initModality(Modality.WINDOW_MODAL);
            playAgainStage.initOwner(View.get().getScene().getWindow());

            //Opens the window and waits.
            playAgainStage.setScene(s1);
            playAgainStage.showAndWait();


        });
    }

    @Override
    public void onGameReset(boolean playAgain) {
        Platform.runLater(() -> {
            if(playAgainStage != null)
                playAgainStage.close();

            if(!playAgain){
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Closing the Game.");
                a.setHeaderText("Ending the Game.");
                a.setContentText("Host has decided not to play again. Goodbye!");
                a.showAndWait().ifPresent(r -> {
                    if(r == ButtonType.OK || r == ButtonType.CLOSE || r == ButtonType.APPLY){
                        Platform.exit();
                    }
                });

                return;
            }


        for(Player p: _players)
            p.resetPlayer();

        _adventurePile.clear();
        _storyPile.clear();

        canDrawStory = true;

        NetworkManager.get().sendNetMessageToServer(new LocalClientMessage(NetworkMsgType.CARD_DRAW_X,NetworkMessage.pack(12)));

        View.get().update();

        });

        View.get().clearLog();
    }
}
