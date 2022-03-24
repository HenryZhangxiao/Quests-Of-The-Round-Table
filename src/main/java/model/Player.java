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
    private AmourCard amourCard;
    private ArrayList<AllyCard> allies;

    public Player(int ID, String name){
        playerName = name;
        playerNum = ID;
        hand = new ArrayList<>();
        numShields = 0;
        battlePoints = 5;//Default for rank squire
        allies = new ArrayList<>();

        //For testing allies
        //for(int i = 0; i < 10; i++)
        //    allies.add((AllyCard) Card.getCardByID(22 + (int)(Math.random() * ((31 - 22) + 1))));

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

    public int[] getAllyCardIDs(){
        int[] a = new int[allies.size()];
        for(int i = 0; i < allies.size(); i++){
            a[i] = allies.get(i).id;
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

    public void discardCardsFromHand(int[] cardIDs){
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

    public void setAmour(AmourCard card){
        amourCard = card;
    }

    public AmourCard getAmour(){
        return amourCard;
    }

    public ArrayList<AllyCard> getAllies(){
        return allies;
    }

    public void setAllies(int[] cardIDs){
        allies.clear();
        for(int i = 0; i < cardIDs.length; i++){
            allies.add((AllyCard) Card.getCardByID(cardIDs[i]));
        }
    }

    public void addAlly(AllyCard card){
        allies.add(card);
    }

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
