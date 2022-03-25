package model;

import gui.EventStoryView;
import network.*;

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
    private Tournament tournament = null;

    private boolean kingsRecognition = false;

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

    public Quest getQuest(){
       return quest;
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
        ServerMessage msg = new ServerMessage(NetworkMsgType.UPDATE_HAND, NetworkMessage.pack(plyID,cardIDs));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onUpdateAllies(int plyID, int[] cardIDs) {
        getPlayerByID(plyID).setAllies(cardIDs);
        NetworkServer.get().sendNetMessageToAllPlayers(new ServerMessage(NetworkMsgType.UPDATE_ALLIES, NetworkMessage.pack(plyID,cardIDs)));
    }

    @Override
    public void onUpdateAmour(int plyID, int cardID) {
        getPlayerByID(plyID).setAmour((AmourCard) Card.getCardByID(cardID));
        NetworkServer.get().sendNetMessageToAllPlayers(new ServerMessage(NetworkMsgType.UPDATE_AMOUR, NetworkMessage.pack(plyID,cardID)));
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
        //if(plyID != turnPlayerID){
        //    System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
        //    return;
        //}

        getPlayerByID(plyID).hand.remove(Card.getCardByID(cardID));
        deck.discards.add(Card.getCardByID(cardID));

        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DISCARD,NetworkMessage.pack(plyID, cardID));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);
    }

    @Override
    public void onCardDiscardX(int plyID, int[] cardIDs) {
        getPlayerByID(plyID).discardCardsFromHand(cardIDs);
        deck.discardCards(Card.getCardsFromIDArray(cardIDs));

        NetworkServer.get().sendNetMessageToAllPlayers(new ServerMessage(NetworkMsgType.CARD_DISCARD_X,NetworkMessage.pack(plyID,cardIDs)));
    }

    @Override
    public void onStoryDrawCard(int plyID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        //Start a quest/event/tournament here
        //Card c = storyDeck.drawCard();
        //For Testing. CardIDs are in Card
        Card c = Card.getCardByID(51);

        ServerMessage msg = new ServerMessage(NetworkMsgType.STORY_CARD_DRAW,NetworkMessage.pack(plyID,c.id));
        NetworkServer.get().sendNetMessageToAllPlayers(msg);

        if(c instanceof QuestCard){
            //Is a quest card
            quest = new Quest((QuestCard) c, plyID, _players.size(), kingsRecognition);
            System.out.println("player " + plyID + " has drawn a quest");
            quest.drawn();

            kingsRecognition = false;
        }
        else if(c instanceof EventCard){

            ServerMessage msg1 = new ServerMessage(NetworkMsgType.EVENT_BEGIN,NetworkMessage.pack(plyID,c.getID()));
            NetworkServer.get().sendNetMessageToAllPlayers(msg1);

            //Is an event card
            switch(c.getName()){
                case "Chivalrous Deed":
                    int minShields = 100;
                    for(int i = 0; i < _players.size(); ++i){
                        int numShields = _players.get(i).getShields();
                        if(numShields < minShields){
                            minShields = numShields;
                        }
                    }

                    //gives 3 shields to each player who ties for lowest number of shields
                    for(Player p : _players) {
                        int shields = getPlayerByID(p.getPlayerNum()).getShields();
                        shields += 3;
                        getPlayerByID(p.getPlayerNum()).setShields(shields);

                        ServerMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(p.getPlayerNum(),shields));
                        NetworkServer.get().sendNetMessageToAllPlayers(shieldMsg);
                    }
                    return;

                case "Pox":
                    //all players but drawer lose a shield
                    // TODO: fix bug where drawer loses shields
                    for(Player p : _players) {
                        int shields = 0;
                        if(p.getPlayerNum() != plyID) {
                            shields = getPlayerByID(p.getPlayerNum()).getShields();
                            if(shields > 0) {
                                shields--;
                            }
                            getPlayerByID(p.getPlayerNum()).setShields(shields);
                        }

                        ServerMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(p.getPlayerNum(),shields));
                        NetworkServer.get().sendNetMessageToAllPlayers(shieldMsg);
                    }
                    return;

                case "Plague":
                    //drawer loses 2 shields if possible
                    int shields = getPlayerByID(plyID).getShields();
                    shields -= 2;
                    if(shields < 0) {
                        shields = 0;
                    }
                    getPlayerByID(plyID).setShields(shields);

                    ServerMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(plyID,shields));
                    NetworkServer.get().sendNetMessageToAllPlayers(shieldMsg);
                    return;

                case "King's Recognition":
                    kingsRecognition = true;
                    return;

                case "Queen's Favor":
                    //All players are tied for lowest rank, so all draw 2 cards
                    for(Player p : _players){
                        Card[] cards = deck.drawCardX(2);
                        ServerMessage msg2 = new ServerMessage(NetworkMsgType.CARD_DRAW_X, NetworkMessage.pack(p.getPlayerNum(), Card.getCardIDsFromArray(cards)));
                        NetworkServer.get().sendNetMessageToAllPlayers(msg2);
                    }
                    return;

                case "Court Called to Camelot":
                    //Add all cards to discard pile and then clear them
                    for(Player p: _players){
                        for(int i = 0; i < p.getAllies().size(); i++)
                            deck.discardCard(p.getAllies().get(i));
                        p.getAllies().clear();
                    }

                    //Sending -1 as plyID will clear all allies of all players on the local instance.
                    NetworkServer.get().sendNetMessageToAllPlayers(new ServerMessage(NetworkMsgType.CLEAR_ALLIES,NetworkMessage.pack(-1)));
                    return;

                case "King's Call to Arms":
                    //Handled clientside.
                    return;

                case "Prosperity Throughout the Realm":
                    //All players draw 2 cards
                    for(Player p : _players){
                        Card[] cards = deck.drawCardX(2);
                        ServerMessage msg3 = new ServerMessage(NetworkMsgType.CARD_DRAW_X, NetworkMessage.pack(p.getPlayerNum(), Card.getCardIDsFromArray(cards)));
                        NetworkServer.get().sendNetMessageToAllPlayers(msg3);
                    }
                    return;
            }

            //todo drawing card are here
            /*

            //The card drawing message should be sent to all players, regardless if they themselves are drawing a card. The one who
            //is drawing a card is packed as the first argument in the obj array. eg plyID in the following messages.

            //Drawing One Card. plyID is who is drawing/getting the card.
            ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW, NetworkMessage.pack(plyID, drawAdvCard().getID()));
            NetworkServer.get().sendNetMessageToAllPlayers(msg);

            //Drawing multiple cards. plyID is who is drawing/getting all the cards.
            Card[] cards = deck.drawCardX(2);
            ServerMessage msg2 = new ServerMessage(NetworkMsgType.CARD_DRAW_X, NetworkMessage.pack(plyID, Card.getCardIDsFromArray(cards)));
            NetworkServer.get().sendNetMessageToAllPlayers(msg);

            //Showing card to client. The select cards for Kings call to arms is already handled serverside
            ServerMessage msg3 = new ServerMessage(NetworkMsgType.EVENT_BEGIN,NetworkMessage.pack(plyID,c.getID()));
            NetworkServer.get().sendNetMessageToAllPlayers(msg3);


             */


        }

        else if(c instanceof TournamentCard){
            tournament = new Tournament((TournamentCard) c, plyID, _players.size());
            System.out.println("Player " + plyID + " has drawn a tournament");
            tournament.drawn();
        }

        //Adds it into the discard pile.
        storyDeck.discardCard(c);
    }

    @Override
    public void onQuestSponsorQuery(int plyID, boolean declined, int[][] questCards) {
        //Called when a player responds to a query to sponsor the quest. If declined is true, then questCards will be null.
        System.out.println("player " + plyID + " asked to sponsor");
        System.out.println("quest's player id is " + quest.getTurnPlayerID());
        if(!declined){
            int numCardsUsed = 0;

            Card[][] stageCards = new Card[questCards.length][];
            for(int i = 0; i < questCards.length; ++i){
                Card[] stage = new Card[questCards[i].length];

                for(int j = 0; j < questCards[i].length; ++j){
                    stage[j] = (Card.getCardByID(questCards[i][j]));
                    numCardsUsed++;
                }
                stageCards[i] = stage;
            }

            //sponsor draws n = (#cards used to sponsor) + (#stages in quest) cards
            Card[] cards = deck.drawCardX(numCardsUsed + questCards.length);
            ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW_X, NetworkMessage.pack(plyID, Card.getCardIDsFromArray(cards)));
            NetworkServer.get().sendNetMessageToAllPlayers(msg);

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
                quest.battleOrTest();   //previously participating
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
        System.out.println("in on participate GAME " + turnPlayerID);
        //Called when a player responds to a participation query. If declined is true, cards will be null.
        if(declined){
            quest.addOutPID(plyID);
            System.out.println("declined participation");
        }
        else{
            quest.addInPID(plyID);

            Card[] playerCards = new Card[cards.length];
            for(int i = 0; i < cards.length; ++i){
                playerCards[i] = Card.getCardByID(cards[i]);
            }
            quest.setPlayerCards(plyID, playerCards);
            System.out.println("accepted participation");
        }

        quest.battling();   // no need to check for participants or if it's the last turn, all handled in Quest
    }

    @Override
    public void onTournamentParticipationQuery(int plyID, boolean declined, int[] cardIDs) {
        System.out.println("Player " + plyID + " asked to participate in tournament");
        System.out.println("Tournament's player id is " + tournament.getTurnPlayerID());
        // Player chose to enter the tournament with a provided hand cardIDs
        if(!declined){
            tournament.addInPID(plyID);
            Card[] participantHand = new Card[cardIDs.length];
            for(int i = 0; i < cardIDs.length; i++){
                participantHand[i] = Card.getCardByID(cardIDs[i]);
            }
            tournament.setPlayerCards(plyID, participantHand);
            System.out.println("Player " + plyID + " accepted participation in the tournament");

            tournament.goToNextTurn();
            tournament.participating();   //previously participating
            //}

        }
        else{ // Player declined to enter the tournament
            tournament.addOutPID(plyID);
            System.out.println("Declined participation");
            // If the next person to query is the drawer, we have gone full circle, and it's time to start the tournament
            if(tournament.getNextPID(tournament.getTurnPlayerID()) == tournament.getTournamentDrawerPID()){
                System.out.println("We are done querying for tournament participation. Onto the tournament.");
                tournament.battling();
            }
            else{ // We still need to query the remaining players for participation
                System.out.println("Still more to query for participation");
                tournament.goToNextTurn();
                tournament.participating();
            }

        }
    }

    public void onTestBidQuery(int plyID, boolean declined, int bid, int[] cardIDs) {
        System.out.println("Player " + plyID + " asked for a bid");
        // Player chose to enter the tournament with a provided hand cardIDs
        if(!declined){
            quest.getTest().addInPID(plyID);
            quest.getTest().setCurrentBid(bid);

            System.out.println("Player " + plyID + " placed bid " + bid + " for the test");

            quest.getTest().goToNextTurn();
            quest.getTest().bidding();   //previously participating
        }
        else{ // Player declined to bid
            quest.getTest().addOutPID(plyID);
            System.out.println(plyID + " declined test participation");
            // If there are still more than 1 bidders, keep querying
            if(quest.getTest().getInPIDs().size() > 1){
                System.out.println("Still more eager bidders. Keep querying");
                quest.getTest().goToNextTurn();
                quest.getTest().bidding();   //previously participating
            }
            else{ // We have a bid winner.
                System.out.println("We have a bid winner.");
                quest.getTest().testResolution();
            }

        }
    }

    public void checkForWinner(){
        ArrayList<Player> winningPlayers = new ArrayList<>();
        for(Player p: _players){
            if(p.getShields() >= 5){ // 5 is amount to win
                winningPlayers.add(p);
            }
        }

        //Tournament tournament = new Tournament(null, null, winningPlayers.size()); //TODO: need to add tweaks to Tournament class
        //TODO: so that it'll work with no TournamentCard, have to immediately opt out non winningPlayers, and display winner at the end
        //tournament.drawn();

        //TODO: will have to be called after every successful Quest, and every Event that gives Shields
    }
}
