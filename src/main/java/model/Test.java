package model;

import network.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    private TestCard testCard;
    private int turnPlayerID;
    private int numPlayers;

    private int testDrawerPID = -5;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    protected ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    protected ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated

    private int currentStage;
    private int highestBid;
    private int currentBid;

    private int[][] playerCards;


    public Test(TestCard _testCard, int _testDrawerPID, int _numPlayers){
        testCard = _testCard;
        testDrawerPID = _testDrawerPID;
        turnPlayerID = _testDrawerPID;
        numPlayers = _numPlayers;

        outPIDs = new ArrayList<>();
        inPIDs = new ArrayList<>();
        playerCards = new int[numPlayers][];

        currentStage = 0;
        // Init current/highest bid to min-1 to allow min bid on first query (only bids higher than current bid are accepted locally)
        highestBid = testCard.getMinimumBid() - 1;
        currentBid = testCard.getMinimumBid() - 1;
    }


    protected int getNextPID(int currentPID){
        int index = inPIDs.indexOf(currentPID);

        if(index == inPIDs.size() - 1){
            return inPIDs.get(0);
        }
        else{
            return inPIDs.get(index+1);
        }
    }

    protected void goToNextTurn(){
        System.out.println("turnPlayerID going from " + turnPlayerID + " to " + getNextPID(turnPlayerID));
        turnPlayerID = getNextPID(turnPlayerID);
    }

    public void drawn() {
        ServerMessage testStartMsg = new ServerMessage(NetworkMsgType.TEST_BEGIN, NetworkMessage.pack(testDrawerPID, testCard.id));
        NetworkServer.get().sendNetMessageToAllPlayers(testStartMsg);

        // Prompt current player for a bid
        ServerMessage bidQuery = new ServerMessage(NetworkMsgType.TEST_BID_QUERY, NetworkMessage.pack(testCard.id, Game.get().getQuest().getQuestCard().getID(),currentBid));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(bidQuery);
    }
/*
    //happens if player who drew quest doesn't sponsor it, goes around table
    public void sponsoring() {
        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(testCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
    }
*/
    //players choose if they want to join the quest
    //once it gets to the sponsor, then everyone has opted in or out, sponsor picks cards for quest
    public void bidding() {
        System.out.println("in bidding TEST " + turnPlayerID);
        //if(turnPlayerID != sponsorPID && !outPIDs.contains(turnPlayerID)) {
        ServerMessage bidQuery = new ServerMessage(NetworkMsgType.TEST_BID_QUERY, NetworkMessage.pack(testCard.id, Game.get().getQuest().getQuestCard().getID(), currentBid));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(bidQuery);
    }

    // Players who still need to provide a higher bid
    public void testResolution() {
        System.out.println("in Test Resolution");
        /*
        for(AllyCard ally: allies){
            numFreeBids += ally.getBidsForAllies(ally, Game.get().getQuest().getQuestCard().getID(), Game.get().getPlayerByID(inPIDs.get(0)).getAmour());
        }
        */

        // The winner needs to discard cards
        ServerMessage discardMsg = new ServerMessage(NetworkMsgType.CARD_DISCARD_X,NetworkMessage.pack(inPIDs.get(0), playerCards[inPIDs.get(0)],true));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(discardMsg);


        // Tell everyone that the quest has been won by inPIDs.get(0)
        ServerMessage testOverMsg = new ServerMessage(NetworkMsgType.TEST_FINAL_RESULT,NetworkMessage.pack(testCard.getID(),inPIDs.get(0), highestBid));
        NetworkServer.get().sendNetMessageToAllPlayers(testOverMsg);

    }

    public boolean isBiddingDone(){
        return inPIDs.size() == 1;
    }

    public boolean isValidBid(int plyID, int bid){
        ArrayList<AllyCard> allies = Game.get().getPlayerByID(plyID).getAllies();
        int numFreeBids = AllyCard.getBidsForAllies(allies, Game.get().getQuest().getQuestCard(), Game.get().getPlayerByID(plyID).getAmour());

        return bid + numFreeBids > currentBid && bid + numFreeBids > testCard.getMinimumBid();
    }

    public void setCurrentBid(int bid) {
        this.currentBid = bid;
    }

    public void setPlayerCards(int pid, int[] _playerCards){
        playerCards[pid] = _playerCards;
    }

    public int getTestDrawerPID(){ return testDrawerPID;}

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

    public int getHighestBid() {
        return highestBid;
    }

    public int getTurnPlayerID() {
        return turnPlayerID;
    }

    public TestCard getTestCard(){return testCard;}

    public int getCurrentStage() {
        return currentStage;
    }
}
