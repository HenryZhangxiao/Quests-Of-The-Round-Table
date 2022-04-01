package model;

import network.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Tournament {

    private TournamentCard tournamentCard;
    private int turnPlayerID;
    private int numPlayers;
    private int numVictoryShields;
    private int numParticipants;
    private int round;
    private int tournamentDrawerPID;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    protected ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    protected ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated
    protected ArrayList<Integer> topBidderPIDs;
    private boolean drawerBidded;

    private Card[][] playerCards;

    protected boolean isTiebreaker;

    public Tournament(TournamentCard _tournamentCard, int _tournamentDrawerPID, int _numPlayers, boolean _isTiebreaker){
        tournamentCard = _tournamentCard;
        tournamentDrawerPID = _tournamentDrawerPID;
        turnPlayerID = _tournamentDrawerPID;
        numPlayers = _numPlayers;

        playerCards = new Card[numPlayers][];

        outPIDs = new ArrayList<>();
        inPIDs = new ArrayList<>();
        topBidderPIDs = new ArrayList<>();
        drawerBidded = false;

        round = 0;

        if(!_isTiebreaker){
            numVictoryShields = tournamentCard.getBonusShields() + numPlayers;
        }
        else{
            numVictoryShields = 0;
        }

        isTiebreaker = _isTiebreaker;
        System.out.println("isTiebreaker ctor: " + isTiebreaker);
    }

    protected int getNextPID(int currentPID){
        if(currentPID == numPlayers - 1) {
            return 0;
        }
        else {
            return currentPID + 1;
        }
    }

    protected void goToNextTurn(){
        System.out.println("turnPlayerID going from " + turnPlayerID + " to " + getNextPID(turnPlayerID));
        turnPlayerID = getNextPID(turnPlayerID);
    }

    public void drawn() {
        ServerMessage questStartMsg = new ServerMessage(NetworkMsgType.TOURNAMENT_BEGIN, NetworkMessage.pack(tournamentDrawerPID, tournamentCard.id));
        NetworkServer.get().sendNetMessageToAllPlayers(questStartMsg);

        // Ask current player if they want to participate
        ServerMessage participationQuery = new ServerMessage(NetworkMsgType.TOURNAMENT_PARTICIPATION_QUERY, NetworkMessage.pack(tournamentCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(participationQuery);
    }

    // Players choose if they want to join the tournament
    public void participating() {
        System.out.println("in participating TOURNAMENT " + turnPlayerID);

        // If we haven't gone full circle yet, otherwise go to battling()
        if(turnPlayerID == tournamentDrawerPID){
            System.out.println("to battle!");
            battling();
        }
        else if(!outPIDs.contains(turnPlayerID)) {
            System.out.println("a winner");
            ServerMessage participationQuery = new ServerMessage(NetworkMsgType.TOURNAMENT_PARTICIPATION_QUERY, NetworkMessage.pack(tournamentCard.id));
            NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(participationQuery);
        }
        else{
            System.out.println("not a winner, skip");
            goToNextTurn();
            participating();
        }
    }

    public void battling() {
        System.out.println("in battling TOURNAMENT "  + turnPlayerID);

        round++;
        if(round == 1){ //First round, assign numParticipants to opt in size
            numParticipants = inPIDs.size();;
        }
        int highestPlayerBP = 0;

        // NO PARTICIPANTS, END TOURNAMENT
        if (inPIDs.isEmpty()) {
            ServerMessage noWinMsg = new ServerMessage(NetworkMsgType.TOURNAMENT_FINAL_RESULT,NetworkMessage.pack(new int[] {-1}));
            NetworkServer.get().sendNetMessageToAllPlayers(noWinMsg);
        }
        // ONLY ONE PERSON SO THAT PERSON WON
        else if(inPIDs.size() == 1){
            int shields = Game.get().getPlayerByID(inPIDs.get(0)).getShields();
            numVictoryShields = numParticipants + tournamentCard.getBonusShields();
            shields += numVictoryShields;

            Game.get().getPlayerByID(inPIDs.get(0)).setShields(shields);

            ServerMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(inPIDs.get(0),shields));
            NetworkServer.get().sendNetMessageToAllPlayers(shieldMsg);

            ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.TOURNAMENT_FINAL_RESULT,NetworkMessage.pack(new int[] {inPIDs.get(0)}));
            NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);
        }
        else{ // More than one participant so calculate winner
            int playerBP;
            int currentPIDToCheck;

            // Compare each player who opted in
            for(int i = 0; i < inPIDs.size(); i++){
                currentPIDToCheck = inPIDs.get(i);
                playerBP = Game.get().getPlayerByID(currentPIDToCheck).getBattlePoints();   // Rank points

                // Adds up the BP of a player's selection
                for(int j = 0; j < playerCards[currentPIDToCheck].length; j++){
                    if (playerCards[currentPIDToCheck][j] instanceof WeaponCard){
                        WeaponCard currentCard = (WeaponCard) playerCards[currentPIDToCheck][j];
                        playerBP += currentCard.getBP();
                    }
                    else if(playerCards[currentPIDToCheck][j] instanceof AmourCard){
                        AmourCard currentCard = (AmourCard) playerCards[currentPIDToCheck][j];
                        playerBP += currentCard.getBP();
                    }
                    else if(playerCards[currentPIDToCheck][j] instanceof AllyCard) {
                        AllyCard currentCard = (AllyCard) playerCards[currentPIDToCheck][j];
                        playerBP += currentCard.getBP();
                    }
                    else{
                        // Catch all else statement so the first three statements are hard checked
                        System.out.println("Something went wrong with the Tournament card type checking");
                    }
                }
                // If this is the highest BP selection we have so far, we need to clear the arraylist
                // and add the current winner in
                if(playerBP > highestPlayerBP){
                    System.out.println(playerBP + " is higher than " + highestPlayerBP);
                    //addInPID(currentPIDToCheck);
                    topBidderPIDs.clear();
                    topBidderPIDs.add(currentPIDToCheck);
                    highestPlayerBP = playerBP;
                }
                // There is a current tie so add the tied 'winner'
                else if(playerBP == highestPlayerBP){
                    System.out.println(playerBP + " is tied with " + highestPlayerBP);
                    //addInPID(currentPIDToCheck);
                    topBidderPIDs.add(currentPIDToCheck);
                }
                // The current hand to check is lower than the current highest so add to outPIDs
                else{
                    System.out.println(playerBP + " is lower than " + highestPlayerBP);
                    //addOutPID(currentPIDToCheck);
                    outPIDs.add(currentPIDToCheck);
                }
            }

            // By here, we are done, and we should know who the winner(s) is/are
            // If it's the second go-around, and we have another tie, everybody wins
            if(round == 2 && topBidderPIDs.size() >= 2){
                System.out.println("tie in the second round");

                if(!isTiebreaker){
                    System.out.println("normal tournament ending");
                    //is a normal Tournament from a Card
                    numVictoryShields = numParticipants;
                    for(int i = 0; i < inPIDs.size(); i++){
                        int shields = Game.get().getPlayerByID(inPIDs.get(i)).getShields();
                        shields += numVictoryShields;

                        Game.get().getPlayerByID(inPIDs.get(i)).setShields(shields);

                        ServerMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(inPIDs.get(i),shields));
                        NetworkServer.get().sendNetMessageToAllPlayers(shieldMsg);
                    }

                    ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.TOURNAMENT_FINAL_RESULT,NetworkMessage.pack(topBidderPIDsToArray()));
                    NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);

                    Game.get().checkForWinner();
                }
                else{
                    System.out.println("tiebreaker tournament ending");
                    //is final tiebreaker Tournament
                    ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.GAME_FINAL_RESULT,NetworkMessage.pack(topBidderPIDsToArray()));
                    NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);
                }
            }
            // If there is a tie, and it's not the second round, we need to repeat the tournament
            else if(topBidderPIDs.size() >= 2){
                System.out.println("tie in the first round");
                ServerMessage tieMsg = new ServerMessage(NetworkMsgType.TOURNAMENT_TIE,NetworkMessage.pack(tournamentCard.getID()));
                NetworkServer.get().sendNetMessageToAllPlayers(tieMsg);

                restartTournament();

            }
            // Otherwise, no tie so send winning message to inPID
            else{
                if(!isTiebreaker){
                    System.out.println("normal tournament ending");
                    //is a normal Tournament from a Card
                    numVictoryShields = numParticipants + tournamentCard.getBonusShields();
                    System.out.println("winner, with " + numVictoryShields + " Victory Shields");

                    int shields = Game.get().getPlayerByID(topBidderPIDs.get(0)).getShields();
                    shields += numVictoryShields;
                    Game.get().getPlayerByID(topBidderPIDs.get(0)).setShields(shields);
                    ServerMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(topBidderPIDs.get(0),shields));
                    NetworkServer.get().sendNetMessageToAllPlayers(shieldMsg);

                    ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.TOURNAMENT_FINAL_RESULT,NetworkMessage.pack(topBidderPIDsToArray()));
                    NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);

                    //Clear all Amours in play
                    NetworkServer.get().sendNetMessageToAllPlayers(new ServerMessage(NetworkMsgType.UPDATE_AMOUR,NetworkMessage.pack(-1, -1)));

                    Game.get().checkForWinner();
                }
                else{
                    System.out.println("tiebreaker tournament ending");
                    //is final tiebreaker Tournament
                    ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.GAME_FINAL_RESULT,NetworkMessage.pack(topBidderPIDsToArray()));
                    NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);
                }
            }
        }
    }

    private void restartTournament() {
        // Move all participants who lost to outPIDs so only winners move on the next round
        for (Integer pid : inPIDs) {
            if (!topBidderPIDs.contains(pid))
                //addOutPID(pid);
                outPIDs.add(pid);
        }
        inPIDs.clear();
        topBidderPIDs.clear();
        drawerBidded = false;

        //goToNextTurn();

        if(!outPIDs.contains(turnPlayerID)) {
            System.out.println("restart: participation query");
            ServerMessage participationQuery = new ServerMessage(NetworkMsgType.TOURNAMENT_PARTICIPATION_QUERY, NetworkMessage.pack(tournamentCard.id));
            NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(participationQuery);
        }
        else{
            System.out.println("restart: skip turn, participating()");
            goToNextTurn();
            participating();
        }
    }

    public void setPlayerCards(int pid, Card[] _playerCards){
        playerCards[pid] = _playerCards;
    }

    public int getTournamentDrawerPID(){ return tournamentDrawerPID;}

    public ArrayList<Integer> getInPIDs() {
        return inPIDs;
    }

    public void addOutPID(int pid){
        if (inPIDs.contains(pid))
            inPIDs.removeAll(Arrays.asList(pid));
        if (!outPIDs.contains(pid) && !inPIDs.contains(pid))
            outPIDs.add(pid);
    }

    public void addInPID(int pid){
        if (!outPIDs.contains(pid) && !inPIDs.contains(pid))
            inPIDs.add(pid);
    }

    public TournamentCard getTournamentCard(){return tournamentCard;}

    public int getTurnPlayerID() {
        return turnPlayerID;
    }

    public boolean getDrawerBidded() {
        return drawerBidded;
    }

    public int getNumParticipants() {
        return numParticipants;
    }

    public void setDrawerBidded(boolean bid){
        drawerBidded = bid;
    }

    public void setNumParticipants(int _numParticipants){
        numParticipants = _numParticipants;
    }

    public int[] topBidderPIDsToArray(){
        int[] a = new int[topBidderPIDs.size()];
        int i = 0;
        for(Integer pid : topBidderPIDs){
            a[i] = pid;
            System.out.println("a[i]:" + a[i]);
            i++;

        }

        System.out.println("a:" + a);
        return a;
    }

    public void setOutPIDs(ArrayList<Integer> _outPIDs){
        outPIDs = _outPIDs;
    }
}
