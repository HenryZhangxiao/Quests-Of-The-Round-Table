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


    public Test(TestCard _testCard, int _testDrawerPID, int _numPlayers){
        testCard = _testCard;
        testDrawerPID = _testDrawerPID;
        turnPlayerID = _testDrawerPID;
        numPlayers = _numPlayers;

        outPIDs = new ArrayList<>();
        inPIDs = new ArrayList<>();

        currentStage = 0;
        highestBid = testCard.getMinimumBid();
        currentBid = testCard.getMinimumBid();
    }


    protected int getNextPID(int currentPID){
        if(currentPID == numPlayers - 1){
            return 0;
        }
        else{
            return currentPID + 1;
        }
    }

    protected void goToNextTurn(){
        System.out.println("turnPlayerID going from " + turnPlayerID + " to " + getNextPID(turnPlayerID));
        turnPlayerID = getNextPID(turnPlayerID);
    }

    public void drawn() {
        ServerMessage questStartMsg = new ServerMessage(NetworkMsgType.QUEST_BEGIN, NetworkMessage.pack(testDrawerPID, testCard.id));
        NetworkServer.get().sendNetMessageToAllPlayers(questStartMsg);

        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY, NetworkMessage.pack(testCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
    }

    //happens if player who drew quest doesn't sponsor it, goes around table
    public void sponsoring() {
        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(testCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
    }

    //players choose if they want to join the quest
    //once it gets to the sponsor, then everyone has opted in or out, sponsor picks cards for quest
    public void participating() {
        System.out.println("in participating QUEST " + turnPlayerID);
        //if(turnPlayerID != sponsorPID && !outPIDs.contains(turnPlayerID)) {
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(inPIDs.get((0)), testCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
        //}
    }

    // Players who still need to provide a higher bid
    public void battling() {
        System.out.println("in battling TEST "  + turnPlayerID);

        //current player is still in the quest
        if(inPIDs.contains(turnPlayerID)){
            // We already performed a check using isValidBid, so we can just set highestBid
            highestBid = currentBid;
        }

        // If there are more than one bidder remaining, query for participation
        if (inPIDs.size() >= 2)
            //TODO: Query for participation
            //TODO: Querying for participation may have to be reworked (idk yet)
            participating();
        else {    // There is only one bidder remaining, so we have a winner
            //TODO: Network message to alert of winner.
            //ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.QUEST_FINAL_RESULT,NetworkMessage.pack(winnerID, Card.getStageCardIDsFromMDArray(stageCards)));
            //NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);

            //Clear all Amours in play
            NetworkServer.get().sendNetMessageToAllPlayers(new ServerMessage(NetworkMsgType.UPDATE_AMOUR,NetworkMessage.pack(-1, -1)));
        }
    }


    public boolean isBiddingDone(){
        return inPIDs.size() == 1;
    }

    public boolean isValidBid(int bid){
        return bid > currentBid && bid > testCard.getMinimumBid();
    }

    public void setCurrentBid(int bid) {
        this.currentBid = bid;
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
