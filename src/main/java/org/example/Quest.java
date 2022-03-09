package org.example;

import java.util.ArrayList;

public class Quest {
    private QuestCard questCard;

    private int questDrawerPID = -5;    //keep track of who drew the quest so looking for sponsor will only loop through players once
    private int sponsorPID;
    private ArrayList<Integer> outPIDs;  //those who have opted out or have been eliminated
    private ArrayList<Integer> inPIDs;   //those who did not opt out and have not been eliminated

    private Card[][] stages;
    private int currentStage = 0;

    private enum Phase {
        drawn, sponsoring, participating, building, battling
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
            //TODO: is this actually necessary? I don't know if we actually need local version of the Quest
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_BEGIN,NetworkMessage.pack(turnPlayerID, questCard.id));
            NetworkManager.get().sendNetMessage(msg);

            //ask current player if they want to sponsor
            LocalClientMessage msg2 = new LocalClientMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(turnPlayerID));
            NetworkManager.get().sendNetMessage(msg2);
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
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_SPONSOR_QUERY,NetworkMessage.pack(turnPlayerID));
                NetworkManager.get().sendNetMessage(msg);
            }
        }

        //players choose if they want to join the quest
        else if(phase == Phase.participating){
            //went around table and nobody decided to participate
            if(turnPlayerID == sponsorPID){
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_RESULT,NetworkMessage.pack(turnPlayerID));
                NetworkManager.get().sendNetMessage(msg);
            }
            else {
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(turnPlayerID));
                NetworkManager.get().sendNetMessage(msg);
            }
        }

        //player who sponsored the quest now picks cards for quest
        else if(phase == Phase.building){
            //TODO: picking of cards from hand

            //Henry stuff to validate sponsor's selection
        }

        //players who opted in pick weapons to fight foes, etc
        else if(phase == Phase.battling){

            //current player is still in the quest
            if(inPIDs.contains(turnPlayerID)){

                //current player picks 0+ weapons from their hand
                //TODO: send message to clients so that player of given ID can pick cards from their hand
                // Remember: no duplicate weapons can be picked, so whenever pick loop through already picked and compare names
                // will likely also do something to display the weapons picked to that player

                WeaponCard[] weapons = new WeaponCard[0];   //To be properly filled with weapons from above functionality

                //add up that player's battle points
                int weaponPoints = 0;
                for(int i = 0; i < weapons.length; ++i){
                    weaponPoints += weapons[i].getBP();
                }

                int playerBP = 5 + weaponPoints;

                //assumes the first card in a stage will be the foe
                if(playerBP > ((WeaponCard)stages[currentStage][0]).getBP()){
                    // player wins, stay in inPIDS
                    // TODO: message that will tell player that they won the fight.
                    //  will likely also clear the weapon cards from that player

                    //beat foe of last stage, get shields
                    if(currentStage == questCard.getStages()){
                        //TODO: message that will update shields for all local versions of that player
                        // takes a number equal to the number of stages as number of shields to get
                        // using .giveShields() function
                    }
                }
                else{
                    // player loses, pid removed from inPIDS, put in outPIDS
                    // TODO: message that will tell player that they lost the fight, could be same message, with diff input.
                    //  will likely also clear the weapon cards from that player
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
