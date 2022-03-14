package model;

import network.NetworkServer;
import network.ServerMessage;

import java.util.ArrayList;

public class Player{

    private int playerNum;
    private String playerName;
    public ArrayList<Card> hand;
    private int numShields;
    private int battlePoints;

    public Player(int ID, String name){
        playerName = name;
        playerNum = ID;
        hand = new ArrayList<>();
        numShields = 0;
        battlePoints = 5;//Default for rank squire
    }

    public void addCard(Card c){
        hand.add(c);
    }

    public int[] getHandCardIDs(){
        int[] a = new int[hand.size()];
        for(int i = 0; i < hand.size(); i++){
            a[i] = hand.get(i).id;
        }
        return a;
    }

    public void addCardsByIDs(int[] cardIDs){
        for(int i = 0; i < cardIDs.length; ++i){
            addCardByID(cardIDs[i]);
        }
    }

    public void addCardByID(int cardID){
        hand.add(Card.getCardByID(cardID));
    }

    public void discardCardFromHand(int cardID){
        Card card = Card.getCardByID(cardID);
        hand.remove(card);
    }

    public void discardCardsFromHand(Integer[] cardIDs){
        for(int i = 0; i < cardIDs.length; ++i){
            discardCardFromHand(cardIDs[i]);
        }
    }

    public void sendNetMessage(ServerMessage msg){
        NetworkServer.get().getPlayerByID(playerNum).sendNetMsg(msg);
    }

    public void setShields(int x) {
        numShields = x;
    }
    public int getShields(){return numShields; }

    public int getBattlePoints(){return battlePoints;}

    public void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }

    public int getPlayerNum(){
        return playerNum;
    }

    public void setPlayerName(String name){
        this.playerName = name;
    }

    public String getPlayerName(){return playerName;}
}
