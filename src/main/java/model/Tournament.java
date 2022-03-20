package model;

import network.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Tournament {

    private TournamentCard tournamentCard;
    private int turnPlayerID;
    private int numPlayers;
    private int numVictoryShields;
    private int round;
    private int tournamentDrawerPID;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    protected ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    protected ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated

    private Card[][] playerCards;

    public Tournament(TournamentCard _tournamentCard, int _tournamentDrawerPID, int _numPlayers){
        tournamentCard = _tournamentCard;
        tournamentDrawerPID = _tournamentDrawerPID;
        turnPlayerID = _tournamentDrawerPID;
        numPlayers = _numPlayers;

        playerCards = new Card[numPlayers][];

        outPIDs = new ArrayList<>();
        inPIDs = new ArrayList<>();

        round = 0;

        numVictoryShields = tournamentCard.getBonusShields() + numPlayers;
    }

    public void drawn() {
        ServerMessage questStartMsg = new ServerMessage(NetworkMsgType.QUEST_BEGIN, NetworkMessage.pack(tournamentDrawerPID, tournamentCard.id));
        NetworkServer.get().sendNetMessageToAllPlayers(questStartMsg);

        //ask current player if they want to sponsor
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY, NetworkMessage.pack(tournamentCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
    }

    //players choose if they want to join the quest
    //once it gets to the sponsor, then everyone has opted in or out, sponsor picks cards for quest
    public void participating() {
        System.out.println("in participating TOURNAMENT " + turnPlayerID);
        //if(turnPlayerID != sponsorPID && !outPIDs.contains(turnPlayerID)) {
        ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(tournamentDrawerPID, tournamentCard.id));
        NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
        //}
    }

    public void battling() {
        System.out.println("in battling TOURNAMENT "  + turnPlayerID);
        round++;
        int highestPlayerBP = 0;

        // ONLY ONE PERSON SO THAT PERSON WON
        if(inPIDs.size() == 1){
            //TODO: Network message for the only winner inPIDs.get(0)
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
                    addInPID(currentPIDToCheck);
                }
                // There is a current tie so add the tied 'winner'
                else if(playerBP == highestPlayerBP){
                    addInPID(currentPIDToCheck);
                }
                // The current hand to check is lower than the current highest so add to outPIDs
                else{
                    addOutPID(currentPIDToCheck);
                }
            }

            // By here, we are done, and we should know who the winner(s) is/are
            // If it's the second go-around, and we have another tie, everybody wins
            if(round == 2 && inPIDs.size() >= 2){
                numVictoryShields = numPlayers;
                //TODO: All players in inPIDs win
                //TODO: Networking messages to award shields to each player in inPIDs
            }
            // If there is a tie, and it's not the second round, we need to repeat the tournament
            if(inPIDs.size() >= 2){
                //TODO: Network message to alert players that there was a tie amongst inPIDs
            }
            // Otherwise, no tie so send winning messages to inPIDs
            else{
                for(int i = 0; i < inPIDs.size(); i++){
                    //TODO: Networking to send messages to each winner?
                    //TODO: Also to send messages to draw
                }
            }
        }
    }

    public boolean isValidSelection(Card[] stages){
        ArrayList<Card> weapons = new ArrayList<>();
        AmourCard amour = null;

        for(int i = 0; i < stages.length; i++){
            if(stages[i] instanceof WeaponCard){
                WeaponCard currentCard = (WeaponCard) stages[i];
                if(weapons.contains(currentCard)){
                    return false;
                }
                weapons.add(currentCard);
            }
            else if(stages[i] instanceof AmourCard){
                AmourCard currentCard = (AmourCard) stages[i];
                if(amour != null){
                    return false;
                }
                amour = currentCard;
            }
        }
        // No duplicate weapons or more than one amour
        return true;
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

}
