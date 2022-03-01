package org.example;

import java.util.ArrayList;

public class Player{

    private int playerNum;
    private String playerName;
    public ArrayList<Card> hand;
    public ArrayList<Card> board;  //amours, weapons, etc in front of them

    public Player(int ID, String name){
        playerName = name;
        playerNum = ID;
        hand = new ArrayList<>();
        board = new ArrayList<>();
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
        //todo
    }

    public void addCardByID(int cardID){
        //todo need a way to lookup a card by ID and get back a card object before adding it to the hand.
    }

    protected void discardCardFromHand(int cardID){
        //Card card = hand.get(index);
        //hand.remove(card);
        //card.discard();

        //todo this needs to be by cardID, not by index in the hand.
    }

    protected void discardCardsFromHand(Integer[] indices){
        //for(int i = 0; i < indices.length; ++i){
        //    discardCardFromHand(indices[i]);
        //}
        //todo same as above
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
