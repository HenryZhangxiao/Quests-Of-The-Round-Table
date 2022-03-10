package org.example;

import java.util.ArrayList;

public class Quest {
    private QuestCard questCard;

    private int questDrawerPID = -5;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    private int sponsorPID;
    protected ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    protected ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated

    private Card[][] stages;

    private enum Phase {
        drawn, sponsoring, participating, battling
    }
    Phase phase;

    public Quest(QuestCard _questCard, int _questDrawerPID){
        questCard = _questCard;
        questDrawerPID = _questDrawerPID;
        phase = Phase.drawn;

        sponsorPID = -5;
        stages = new Card[questCard.getStages()][];
    }

    public void execute(int turnPlayerID){
        if(phase == Phase.drawn){

            ServerMessage questStartMsg = new ServerMessage(NetworkMsgType.QUEST_BEGIN,NetworkMessage.pack(questDrawerPID, questCard.id));
            NetworkServer.get().sendNetMessageToAllPlayers(questStartMsg);

            //ask current player if they want to sponsor
            ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(questCard.id));
            NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);


            //NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg();
            //TODO:in onQuestSponsorQuery(), LocalGameManager do stuff to un-disable a button of something to allow player
            // to choose to sponsor. The button(or whatever the input method) will send a onQuestSponsorQuery() ServerMessage
            // through NetworkServer so Game's Quest can set sponsorPID

            phase = Phase.sponsoring;   // will now go through players until sponsor chosen, won't draw story cards
                                            // on their 'turns' since different phase
        }

        //happens if player who drew quest doesn't sponsor it, goes around table
        else if(phase == Phase.sponsoring){
            //player who drew the quest sponsored it
            if(sponsorPID != -5){
                phase = Phase.participating;
            }

            else{
                //ask current player if they want to sponsor
                ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(questCard.id));
                NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
            }
        }

        //players choose if they want to join the quest
        //once it gets to the sponsor, then everyone has opted in or out, sponsor picks cards for quest
        else if(phase == Phase.participating) {
            //player who sponsored the quest now picks cards for quest
            if(turnPlayerID == sponsorPID){
                //TODO: picking of cards from hand

                //Henry stuff to validate sponsor's selection

                phase = Phase.battling;
            }
            else{
                ServerMessage sponsorQuery = new ServerMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(questCard.id));
                NetworkServer.get().getPlayerByID(turnPlayerID).sendNetMsg(sponsorQuery);
            }
        }

        //players who opted in pick weapons to fight foes, etc
        else if(phase == Phase.battling){

            //loops through all stages
            for(int currentStage = 0; currentStage < questCard.getStages(); ++currentStage){

                //current player is still in the quest
                if(inPIDs.contains(turnPlayerID)){

                    //current player picks 0+ weapons from their hand
                    //TODO: send message to clients so that player of given ID can pick cards from their hand
                    // Remember: no duplicate weapons can be picked, so whenever pick loop through already picked and compare names
                    // will likely also do something to display the weapons picked to that player

                    WeaponCard[] weapons = new WeaponCard[0];   //To be properly filled with weapons from above functionality

                    //add up that player's battle points
                    int playerBP = 5;   //5 is points from being a squire
                    for(int i = 0; i < weapons.length; ++i){
                        playerBP += weapons[i].getBP();
                    }

                    //add up current foe's battle points
                    //assumes the first card in a stage will be the foe
                    int foeBP = ((FoeCard)stages[currentStage][0]).getBP();
                    for(int i = 1; i < stages[currentStage].length; ++i){
                        foeBP += ((WeaponCard)stages[currentStage][i]).getBP();
                    }

                    if(playerBP > foeBP){
                        // player wins, draws an Adventure card for winning
                        //TODO: needs to draw an adventure card, not sure how we'll do that since there's no Deck in Quest

                        // TODO: message that will tell player that they won the fight.
                        //  will likely also clear the weapon cards from the board that that player used

                        //beat foe of last stage, get shields
                        if(currentStage == questCard.getStages()){
                            //TODO: message that will update shields for all local versions of that player
                            // takes a number equal to the number of stages as number of shields to be given
                            // using .giveShields() function in Player
                        }
                    }
                    else{
                        // player loses, pid removed from inPIDS, put in outPIDS
                        inPIDs.remove(turnPlayerID);
                        outPIDs.add(turnPlayerID);
                        // TODO: message that will tell player that they lost the fight, could be same message that they won the fight
                        //  but with an input flag set to a different value.
                        //  will likely also clear the weapon cards from that player
                    }
                }
            }



        }
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

    public void addInPID(int pid){ outPIDs.add(pid);}
}
