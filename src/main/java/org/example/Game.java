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

    private enum Phase {
        defaultPhase, sponsoring, questing
    }


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

        int turnTaker = -5;     //no reason why I keep using -5, just a non valid player ID

        int questDrawerPID = -5;    //keep track of who drew the quest so looking for sponsor will only loop through players once
        int sponsorPID = -5;
        Phase phase = Phase.defaultPhase;

        while (!stopThread){
            //Will only run a single time during a turn due to turnTaker
            if(gameStarted && turnTaker != turnPlayerID){

                turnTaker = turnPlayerID;


                if(phase == Phase.defaultPhase){
                    Card storyCard = storyDeck.drawCard();
                    _cardsOnBoard.add(storyCard);
                    System.out.println(storyCard.getName());

                    //sleep needed or else message will cause error in View, since buttons not instantiated
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //TODO: create new message that will display drawn story card to all players, takes in card id
                    ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW,NetworkMessage.pack(turnPlayerID, 1));
                    NetworkServer.get().sendNetMessage(msg);

                    phase = Phase.sponsoring;   // will now go through players until sponsor chosen, won't draw story cards
                                                // on their 'turns' since different phase

                    //ask current player if they want to sponsor
                    //TODO: create new message that will enable a button to be pressed for current player to choose to sponsor
                    //ServerMessage ...

                    //TODO: when sponsor button is pressed, will need to send back info so game loop knows who sponsored, and not to keep asking for sponsorship from other players
                    //when this happens, phase which be set to questing
                }

                //happens if
                if(phase == Phase.sponsoring){
                    //went around table and nobody decided to sponsor
                    if(turnPlayerID == sponsorPID){
                        //TODO: discard story card for cardsOnBoard, so probably a new message
                        _cardsOnBoard.clear();
                    }
                    else{
                        //ask current player if they want to sponsor,
                        //TODO: same message as above, line 79
                        //ServerMessage ...

                    }
                }

                if(phase == Phase.questing){
                    //TBD
                }


            }
        }


    }

    //region Helpers
    public Player getPlayerByID(int plyID){
        for(Player p: _players){
            if(p.getPlayerNum() == plyID)
                return p;
        }
        return null;
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
        NetworkServer.get().sendNetMessage(msg);



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
        NetworkServer.get().sendNetMessage(msg);
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
        NetworkServer.get().sendNetMessage(msg);
    }

    @Override
    public void onDrawCard(int plyID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        Card c = deck.drawCard();
        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW,NetworkMessage.pack(plyID, c.id));
        NetworkServer.get().sendNetMessage(msg);
    }

    @Override
    public void onCardDiscard(int plyID, int cardID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        deck.discards.add(Card.getCardByID(cardID));

        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DISCARD,NetworkMessage.pack(plyID, cardID));
        NetworkServer.get().sendNetMessage(msg);
    }

    @Override
    public void onQuestSponsorQuery(int plyID, boolean declined, int[][] questCards) {
        //Called when a player responds to a query to sponsor the quest. If declined is true, then questCards will be null.


    }

    @Override
    public void onQuestParticipateQuery(int plyID, boolean declined, int[] cards) {
        //Called when a player responds to a participation query. If declined is true, cards will be null.


    }


}
