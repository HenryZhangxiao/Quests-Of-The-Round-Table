package org.example;

import java.util.ArrayList;

public class LocalGameManager implements ClientEventListener{

    //Singleton
    public static LocalGameManager localGameManager;

    private ArrayList<Player> _players;

    private Player localPlayer;

    private int turnID = -1;
    private ArrayList<Card> _cardsOnBoard;

    private ArrayList<Card> _discardPile;

    private boolean gameStarted = false;
    private int connectedPlayerCount = 0;


    private LocalGameManager(){
        _players = new ArrayList<Player>();
        _cardsOnBoard = new ArrayList<Card>();
        _discardPile = new ArrayList<Card>();
        NetworkManager.get().addListener(this);
    }

    public static LocalGameManager get(){
        if(localGameManager == null)
            localGameManager = new LocalGameManager();
        return localGameManager;
    }


    //region Helpers
    private Player getPlayerByID(int plyID){
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

    public void finishTurn(){
        if(!isMyTurn())
            return;

        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TURN_CHANGE,null);
        NetworkManager.get().sendNetMessageToServer(msg);
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

    public ArrayList<Card> getDiscardPile() {return _discardPile;}

    public void startGame(){
        if(!NetworkManager.get().isHost())
            return;
        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.START_GAME, null);
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
        if(!isMyTurn())
            return;
        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.STORY_CARD_DRAW, null);
        NetworkManager.get().sendNetMessageToServer(msg);
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
        View.get().update();
    }

    @Override
    public void onUpdateHand(int plyID, int[] cardIDs) {
        getPlayerByID(plyID).hand.clear();
        for(int i = 0; i < cardIDs.length; i++) {
            getPlayerByID(plyID).addCardByID(cardIDs[i]);
        }
        View.get().update();
    }

    @Override
    public void onUpdateShields(int plyID, int shieldCount) {
        getPlayerByID(plyID).setShields(shieldCount);
        View.get().update();
    }

    @Override
    public void onDrawCard(int plyID, int cardID) {
        getPlayerByID(plyID).addCardByID(cardID);
        View.get().update();
    }

    @Override
    public void onDrawCardX(int plyID, int[] cardIDs) {
        for(int i = 0; i < cardIDs.length; i++) {
            getPlayerByID(plyID).addCardByID(cardIDs[i]);
        }
        View.get().update();
    }

    @Override
    public void onCardDiscard(int plyID, int cardID) {
        getPlayerByID(plyID).discardCardFromHand(cardID);

        _discardPile.add(Card.getCardByID(cardID));
        View.get().update();
    }

    @Override
    public void onStoryDrawCard(int plyID, int cardID) {
        //Called when a player has drawn a story card.

        View.get().update();
    }

    @Override
    public void onQuestBegin(int plyID, int questCardID) {
        //Called when a quest card has been played.


    }

    @Override
    public void onQuestSponsorQuery(int questCardID) {
        //called when asking the local player if they would like to sponsor the quest.
        QuestCard c = (QuestCard) Card.getCardByID(questCardID);
        QuestSponsorView q = new QuestSponsorView(c);

    }

    @Override
    public void onQuestParticipateQuery(int sponsorPlyID, int questID) {
        //Called when the sponsor has chosen their cards and is asking the local player if they would like to participate.
        QuestParticipationView q = new QuestParticipationView(true);

    }

    @Override
    public void onQuestStageResult(int questCardID, boolean wonStage, int[] stageCardsIDs, int[] playerCardsIDs) {
        //TODO Show result of stage.
        // wonStage will be true if they won the stage
        if(wonStage)
            System.out.println("CLIENT: You won the stage.");
        else
            System.out.println("CLIENT: You lost the stage.");

    }

    @Override
    public void onQuestFinalResult(int winnerID, int[][] sponsorCards) {
        //Called when the quest is over and shows the winning results. sponsorCards is separated by stage. eg: sponsorCards[0] will get stage 1's cards.
        System.out.println("CLIENT: The Quest has ended.");
    }
}
