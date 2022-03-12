package org.example;

import java.util.ArrayList;
import java.util.Arrays;

public class Quest {
    private QuestCard questCard;
    private int turnPlayerID;
    private int numPlayers;

    private int questDrawerPID = -5;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    private int sponsorPID;
    protected ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    protected ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated

    private Card[][] stages;
    private Card[] playerCards;

    public Quest(QuestCard _questCard, int _questDrawerPID, int _numPlayers){
        questCard = _questCard;
        questDrawerPID = _questDrawerPID;
        numPlayers = _numPlayers;

        sponsorPID = -5;
        stages = new Card[questCard.getStages()][];

        outPIDs = new ArrayList<>();
        inPIDs = new ArrayList<>();
    }

    private int getNextPID(int currentPID){
        if(currentPID == numPlayers - 1){
            return 0;
        }
        else{
            return currentPID + 1;
        }
    }

    private void goToNextTurn(){
        turnPlayerID = getNextPID(turnPlayerID);
    }

    public void drawn() {
        ServerMessage questStartMsg = new ServerMessage(NetworkMsgType.QUEST_BEGIN, NetworkMessage.pack(questDrawerPID, questCard.id));
        NetworkServer.get().sendNetMessageToAllPlayers(questStartMsg);

        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY, NetworkMessage.pack(questCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);

        goToNextTurn();
    }

    //happens if player who drew quest doesn't sponsor it, goes around table
    public void sponsoring() {
        //player who drew the quest sponsored it already
        if(sponsorPID != -5){
            participating();
        }

        else{
            //ask current player if they want to sponsor
            ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(questCard.id));
            NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
        }

        goToNextTurn();
    }

    //players choose if they want to join the quest
    //once it gets to the sponsor, then everyone has opted in or out, sponsor picks cards for quest
    public void participating() {
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(sponsorPID,questCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);

        goToNextTurn();
    }

    //player who sponsored the quest now picks cards for quest
    public void staging(){

        //Henry stuff to validate sponsor's selection
        //This boolean basically takes the selected cards and checks if its valid (incremental order)
        //I don't know how you want to use this yet, maybe wrap this in a while loop to continue
        //to prompt while false (??)
        boolean isValidSelection = isValidSelection(stages);



        // I think the best approach (or at least one I know would work) would be to have the validation locally when the cards
        // are picked, then depending on if they're a valid selection, change a flag in the onCardPickingQuery (or whatever its name)
        // then in onCardPickingQuery in Game, it will either call quest.staging() again, or it will update Quest's stages variable
        // and call quest.battling()
        // when the selection is valid it would also have to onTurnChange() so that the game will be calling quest.battling() with
        // the next player's id as its arg (don't want the sponsor to battle)
        //Todo onQuestSponsorQuery will be called in game when any client responds to a query with a valid selection, or by pressing the decline button
        // the boolean declined will be true if they pressed the declined button, where it should ask the next player. This can be done here or in Game's
        // onQuestSponsorQuery function.

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
                    if(Arrays.asList(questCard.getSpecialFoes()).contains((stages[currentStage][0]).getName()) || questCard.getSpecialFoes()[0].equals("All")){
                        foeBP = ((FoeCard)stages[currentStage][0]).getAlt_bp();
                    }
                    else{
                        foeBP = ((FoeCard)stages[currentStage][0]).getBP();
                    }

                    for(int i = 1; i < stages[currentStage].length; ++i){
                        foeBP += ((WeaponCard)stages[currentStage][i]).getBP();
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
                        // ServerMessage stageResultMsg = new ServerMessage(NetworkMsgType.QUEST_STAGE_RESULT,NetworkMessage.pack(questCard.id, false, stageCards, playerCards));
                        // NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(stageResultMsg);

                    }
                }
            }
        }
        else{
            //turn has gone around table and back to player
            //TODO: message that tells final results to all players
            // Needs the winner's playerID, the winning player's cardsID array, and the entire cards from the sponsor.
            // NetworkMessage finalResultMsg = new ServerMessage(NetworkMsgType.QUEST_FINAL_RESULT,NetworkMessage.pack(winnerID, sponsorCards, winningPlayerCards));
            // NetworkServer.get().sendNetMessageToAllPlayers(finalResultMsg);
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
                    cardBP = currentCard.getBP();
                }
                else{
                    //TODO: Add the rest of the card types later
                }

                currentStageBPTotal += cardBP;
            }
            stageBPTotals[i] = currentStageBPTotal;
        }

        // Check to see if the stage BPs are in incremental order
        for(int i=0; i < stageBPTotals.length; i++) {
            int lastStageBP = 0;

            if(stageBPTotals[i] > lastStageBP){ //This is good. Valid selection. At least for this stage
                lastStageBP = stageBPTotals[i];
                continue;
            }
            return false; //We missed the if statement which checks validity so return false
        }
        return true; //If we got here there was no problem with the selection
    }


    public void setStages(Card[][] _stageCards){
        stages = _stageCards;
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
}
