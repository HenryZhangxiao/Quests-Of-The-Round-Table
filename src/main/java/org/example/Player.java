package org.example;

import java.util.ArrayList;

public class Player{

    private int playerNum;
    private String playerName;
    public ArrayList<Card> hand;
    private int numShields;

    public Player(int ID, String name){
        playerName = name;
        playerNum = ID;
        hand = new ArrayList<>();
        numShields = 0;
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

    protected void discardCardFromHand(int cardID){
        Card card = Card.getCardByID(cardID);
        hand.remove(card);
    }

    protected void discardCardsFromHand(Integer[] cardIDs){
        for(int i = 0; i < cardIDs.length; ++i){
            discardCardFromHand(cardIDs[i]);
        }
    }

    public void sendNetMessage(ServerMessage msg){
        NetworkServer.get().getPlayerByID(playerNum).sendNetMsg(msg);
    }

    public void giveShields(int x) {
        numShields += x;
    }

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
