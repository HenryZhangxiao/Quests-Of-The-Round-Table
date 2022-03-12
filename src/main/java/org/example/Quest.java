package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Quest {
    private static QuestCard questCard;
    private int turnPlayerID;
    private int numPlayers;

    private int questDrawerPID = -5;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    private int sponsorPID;
    protected ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    protected ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated
    private int winnerID;

    private Card[][] stageCards;
    private Card[] playerCards;

    public Quest(QuestCard _questCard, int _questDrawerPID, int _numPlayers){
        questCard = _questCard;
        questDrawerPID = _questDrawerPID;
        turnPlayerID = questDrawerPID;
        numPlayers = _numPlayers;

        sponsorPID = -5;
        stageCards = new Card[questCard.getStages()][];

        outPIDs = new ArrayList<>();
        inPIDs = new ArrayList<>();
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
        turnPlayerID = getNextPID(turnPlayerID);
    }

    public void drawn() {
        ServerMessage questStartMsg = new ServerMessage(NetworkMsgType.QUEST_BEGIN, NetworkMessage.pack(questDrawerPID, questCard.id));
        NetworkServer.get().sendNetMessageToAllPlayers(questStartMsg);

        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY, NetworkMessage.pack(questCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
    }

    //happens if player who drew quest doesn't sponsor it, goes around table
    public void sponsoring() {
        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(questCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
    }

    //players choose if they want to join the quest
    //once it gets to the sponsor, then everyone has opted in or out, sponsor picks cards for quest
    public void participating() {
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(sponsorPID,questCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);

        goToNextTurn();
    }

    //players who opted in pick weapons to fight foes, etc
    public void battling() {
        //the turn player is not the sponsor (not all players have fought)
        if(turnPlayerID != sponsorPID){

            //loops through all stages
            for(int currentStage = 0; currentStage < questCard.getStages(); ++currentStage){

                //current player is still in the quest
                if(inPIDs.contains(turnPlayerID)){

                    //add up that player's battle points
                    int playerBP = 5;   //5 is points from being a squire
                    for(int i = 0; i < playerCards.length; ++i){
                        playerBP += ((WeaponCard)playerCards[i]).getBP();
                    }

                    //add up current foe's battle points
                    //assumes the first card in a stage will be the foe
                    int foeBP;
                    if(Arrays.asList(questCard.getSpecialFoes()).contains((stageCards[currentStage][0]).getName()) || questCard.getSpecialFoes()[0].equals("All")){
                        foeBP = ((FoeCard)stageCards[currentStage][0]).getAlt_bp();
                    }
                    else{
                        foeBP = ((FoeCard)stageCards[currentStage][0]).getBP();
                    }

                    for(int i = 1; i < stageCards[currentStage].length; ++i){
                        foeBP += ((WeaponCard)stageCards[currentStage][i]).getBP();
                    }

                    if(playerBP > foeBP){
                        // player wins, draws an Adventure card for winning
                        ServerMessage drawCardMsg = new ServerMessage(NetworkMsgType.CARD_DRAW,NetworkMessage.pack(turnPlayerID, Game.get().drawAdvCard().id));
                        NetworkServer.get().sendNetMessageToAllPlayers(drawCardMsg);

                        // message that will tell player that they won the fight.
                        //  will likely also clear the weapon cards from the board that that player used

                        ServerMessage stageResultMsg = new ServerMessage(NetworkMsgType.QUEST_STAGE_RESULT,NetworkMessage.pack(questCard.id, true, stageCards, playerCards));
                        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(stageResultMsg);

                        //beat foe of last stage, get shields
                        if(currentStage == questCard.getStages()){
                            // message that will update shields for all local versions of that player
                            int shields = Game.get().getPlayerByID(turnPlayerID).getShields();
                            shields += ((int) questCard.stages);
                            Game.get().getPlayerByID(turnPlayerID).setShields(shields);
                            NetworkMessage shieldMsg = new ServerMessage(NetworkMsgType.UPDATE_SHIELDS,NetworkMessage.pack(turnPlayerID,shields));
                        }
                    }
                    else{
                        // player loses, pid removed from inPIDS, put in outPIDS
                        inPIDs.removeAll(Arrays.asList(turnPlayerID)); //to add
                        outPIDs.add(turnPlayerID);
                        // message that will tell player that they lost the fight, could be same message that they won the fight
                        //  but with an input flag set to a different value.
                        //  will likely also clear the weapon cards from that player
                        //Todo: false since the player lost. Needs the current stages cards and players cards set to arrays above.
                        ServerMessage stageResultMsg = new ServerMessage(NetworkMsgType.QUEST_STAGE_RESULT,NetworkMessage.pack(questCard.id, false, stageCards, playerCards));
                        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(stageResultMsg);

                    }
                }
            }
        }
        else{
            //turn has gone around table and back to player
             ServerMessage finalResultMsg = new ServerMessage(NetworkMsgType.QUEST_FINAL_RESULT,NetworkMessage.pack(winnerID, stageCards));
             NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);
        }

        goToNextTurn();
    }

    public static boolean isValidSelection(Card[][] stages){
        int[] stageBPTotals = new int[stages.length];

        for(int i=0; i < stages.length; i++){ //Loop through each stage
            int currentStageBPTotal = 0;

            for(int j=0; j < stages[i].length; j++){ //Loop through the cards of each stage
                int cardBP = 0;

                if(stages[i][j] instanceof WeaponCard){
                    WeaponCard currentCard = (WeaponCard) stages[i][j];
                    cardBP = currentCard.getBP();
                }
                else if(stages[i][j] instanceof FoeCard){
                    FoeCard currentCard = (FoeCard) stages[i][j];
                    if(Arrays.asList(questCard.getSpecialFoes()).contains(currentCard.getName()) || (questCard.getSpecialFoes().length != 0 && questCard.getSpecialFoes()[0].equals("All"))){
                        cardBP = currentCard.getAlt_bp();
                    } else{
                        cardBP = currentCard.getBP();
                    }
                }
                else{
                    //TODO: Add the rest of the card types later
                }

                currentStageBPTotal += cardBP;
            }

            stageBPTotals[i] = currentStageBPTotal;
        }

        // Check to see if the stage BPs are in incremental order
        int lastStageBP = 0;
        for(int i=0; i < stageBPTotals.length; i++) {
            if(stageBPTotals[i] > lastStageBP){ //This is good. Valid selection. At least for this stage
                lastStageBP = stageBPTotals[i];
                continue;
            }

            return false; //We missed the if statement which checks validity so return false
        }

        return true; //If we got here there was no problem with the selection
    }


    public void setStages(Card[][] _stageCards){
        stageCards = _stageCards;
    }

    public void setPlayerCards(Card[] _playerCards){
        playerCards = _playerCards;
    }


    public void setSponsorPID(int sponsorPID) {
        this.sponsorPID = sponsorPID;
    }

    public int getSponsorPID(){ return sponsorPID;}

    public int getQuestDrawerPID(){ return questDrawerPID;}

    public ArrayList<Integer> getInPIDs() {
        return inPIDs;
    }

    public void addOutPID(int pid){ outPIDs.add(pid);}

    public void addInPID(int pid){ inPIDs.add(pid);}

    public int getTurnPlayerID() {
        return turnPlayerID;
    }
}
