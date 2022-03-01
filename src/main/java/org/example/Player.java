package org.example;

import java.util.ArrayList;

public class Player {

    private int playerNum;
    private String playerName;
    public ArrayList<Card> hand;
    public ArrayList<Card> board;  //amours, weapons, etc in front of them

    public Player(int ID, String name){
        playerName = name;
        playerNum = ID;
    }

    protected int drawCard(){
        //TODO: send request to server
        return 0; //For now to stop any errors
    }

    protected void discardCardFromHand(Integer index){
        Card card = hand.get(index);
        hand.remove(card);
        card.discard();

        //TODO: make sure other players see discarded cards
    }

    protected void discardCardsFromHand(Integer[] indices){
        for(int i = 0; i < indices.length; ++i){
            discardCardFromHand(indices[i]);
        }
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
