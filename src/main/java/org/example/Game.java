package org.example;

import java.util.ArrayList;

//This class will only be ran on the server. All actions by the players will come in via event listeners and any responses must be sent
//out via a ServerMessage obj with NetworkServer.get().sendNetMessage
public class Game extends Thread implements ServerEventListener {

    //Singleton
    private static Game game;

    public static Game get(){
        if(game == null)
            game = new Game();
        return game;
    }

    private Deck deck;
    private Deck storyDeck;

    private ArrayList<Player> _players;
    private ArrayList<Card> _cardsOnBoard; //For later
    private int turnPlayerID = 0;

    private boolean gameStarted = false;
    
    private Quest quest = null;

    private volatile boolean stopThread = false;

    private Game(){
        _players = new ArrayList<Player>();
        deck = new AdventureDeck();
        storyDeck = new StoryDeck();
        _cardsOnBoard = new ArrayList<>();
        deck.initializeCards();
        storyDeck.initializeCards();
        NetworkServer.get().addListener(this);
    }

    @Override
    public void run() {

        /*while (!stopThread){

        }*/
    }

    //region Helpers
    public Player getPlayerByID(int plyID){
        for(Player p: _players){
            if(p.getPlayerNum() == plyID)
                return p;
        }
        return null;
    }

    public Card drawAdvCard(){
        return deck.drawCard();
    }

    public Card drawStoryCard(){
        return storyDeck.drawCard();
    }


    //endregion

    public void close(){
        stopThread = true;
        this.interrupt();

    }

    @Override
    public void onPlayerConnect(int plyID, String playerName) {
        Player p = new Player(plyID,playerName);

        for(int i = 0; i < 8; i++) {
            p.addCard(deck.drawCard());
        }

        _players.add(p);


        ServerMessage msg = new ServerMessage(NetworkMsgType.UPDATE_PLAYERLIST,NetworkMessage.pack(plyID,playerName,p.getHandCardIDs()));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);



    }

    @Override
    public void onPlayerDisconnect(int plyID, String playerName) {
        int x = -1;
        for (int i = 0; i < _players.size(); i++) {
            if(_players.get(i).getPlayerNum() == plyID){
                x = i;

                //Safety check to make sure the turn isnt the disconnecting player's
                if(plyID == turnPlayerID){
                    if(i+1 < _players.size()){
                        turnPlayerID = _players.get(i+1).getPlayerNum();
                    }
                    else
                        turnPlayerID = _players.get(0).getPlayerNum();
                }
            }
        }

        if(x != -1)
            _players.remove(x);

        System.out.println("SERVER: Player " + playerName + " disconnected. ID: " + String.valueOf(plyID));
    }

    @Override
    public void onGameStart() {
        gameStarted = true;

        ServerMessage msg = new ServerMessage(NetworkMsgType.START_GAME,null);
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onTurnChange(int idOfPlayer) {
        if(turnPlayerID != idOfPlayer){
            return;
        }

        //Goes to next player in list.
        for(int i = 0; i < _players.size(); i++){
            if(_players.get(i).getPlayerNum() == idOfPlayer){
                if(i+1 < _players.size()){
                    turnPlayerID = _players.get(i+1).getPlayerNum();
                }
                else{
                    turnPlayerID = _players.get(0).getPlayerNum();
                }
            }
        }

        //Sends new turnID to all clients
        ServerMessage msg = new ServerMessage(NetworkMsgType.TURN_CHANGE,NetworkMessage.pack(turnPlayerID));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onUpdateHand(int plyID, int[] cardIDs) {
        getPlayerByID(plyID).hand.clear();
        for(int i = 0; i < cardIDs.length; i++) {
            getPlayerByID(plyID).addCardByID(cardIDs[i]);
        }
        ServerMessage msg = new ServerMessage(NetworkMsgType.UPDATE_HAND,NetworkMessage.pack(plyID,cardIDs));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onDrawCard(int plyID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        Card c = deck.drawCard();
        getPlayerByID(plyID).hand.add(c);
        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW,NetworkMessage.pack(plyID, c.id));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onDrawCardX(int plyID, int amountOfCards) {
        //For Drawing multiple cards
        int cardIDs[] = new int[amountOfCards];
        for(int i = 0; i < amountOfCards; i++){
            Card c = deck.drawCard();
            getPlayerByID(plyID).hand.add(c);
            cardIDs[i] = c.id;
        }
        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW_X,NetworkMessage.pack(plyID,cardIDs));
    }

    @Override
    public void onCardDiscard(int plyID, int cardID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        getPlayerByID(plyID).hand.remove(Card.getCardByID(cardID));
        deck.discards.add(Card.getCardByID(cardID));

        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DISCARD,NetworkMessage.pack(plyID, cardID));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onStoryDrawCard(int plyID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        //Start a quest/event/tournament here
        Card c = storyDeck.drawCard();

        ServerMessage msg = new ServerMessage(NetworkMsgType.STORY_CARD_DRAW,NetworkMessage.pack(plyID,c.id));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);

        if(c instanceof QuestCard){
            //Is a quest card
            quest = new Quest((QuestCard) c, plyID, _players.size());
            System.out.println("player " + plyID + " has drawn a quest");
            quest.drawn();
        }

    }

    @Override
    public void onQuestSponsorQuery(int plyID, boolean declined, int[][] questCards) {
        //Called when a player responds to a query to sponsor the quest. If declined is true, then questCards will be null.
        System.out.println("player " + plyID + " asked to sponsor");
        System.out.println("quest's player id is " + quest.getTurnPlayerID());
        if(!declined){

            Card[][] stageCards = new Card[questCards.length][];
            for(int i = 0; i < questCards.length; ++i){
                Card[] stage = new Card[questCards[i].length];

                for(int j = 0; j < questCards[i].length; ++j){
                    stage[j] = (Card.getCardByID(questCards[i][j]));
                }
                stageCards[i] = stage;
            }

            //if sponsor card selection is valid, then go ahead, otherwise redo sponsoring
            //Todo fix this. It validates clientside for now, but theres something up when sending cards over
            //if(Quest.isValidSelection(stageCards, quest.getQuestCard())){
            //    //invalid selection
            //    System.out.println("SERVER: invalid selection from " + String.valueOf(plyID));
            //    quest.sponsoring();
            //}
            //else{
                //valid selection
                quest.setSponsorPID(plyID);
                quest.setStages(stageCards);

                System.out.println("valid selection, to next turn");

                quest.goToNextTurn();
                quest.participating();
            //}

        }
        //next player is the one who drew the quest, meaning no one sponsored
        else if(quest.getNextPID(quest.getTurnPlayerID()) == quest.getQuestDrawerPID()){
            System.out.println("quest over because quest drawer is next: " + quest.getQuestDrawerPID());
            quest = null;
        }
        else{
            //they declined but there are other players left who may sponsor
            System.out.println("still more to sponsor");
            quest.goToNextTurn();
            quest.sponsoring();
        }
    }

    @Override
    public void onQuestParticipateQuery(int plyID, boolean declined, int[] cards) {
        //Called when a player responds to a participation query. If declined is true, cards will be null.
        if(declined){
            quest.addOutPID(plyID);
        }
        else{
            quest.addInPID(plyID);

            Card[] playerCards = new Card[cards.length];
            for(int i = 0; i < cards.length; ++i){
                playerCards[i] = Card.getCardByID(cards[i]);
            }
            quest.setPlayerCards(playerCards);
        }

        //next player is the one who sponsored
        if(quest.getNextPID(quest.getTurnPlayerID()) == quest.getSponsorPID()){
            //no one has chosen to participate in the quest
            if(quest.getInPIDs().isEmpty()){
                quest = null;
            }
            else{
                //at least someone has sponsored, go on to battling
                quest.battling();
            }
        }
        //still more people to choose to participate
        else{
            quest.participating();
        }


    }


}
