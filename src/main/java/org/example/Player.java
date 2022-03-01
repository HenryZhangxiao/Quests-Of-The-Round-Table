package org.example;

import java.util.ArrayList;

public class Player{

    private int playerNum;
    private String playerName;
    public ArrayList<Card> hand;

    public Player(int ID, String name){
        playerName = name;
        playerNum = ID;
        hand = new ArrayList<>();
    }

    public void addCard(Card c){
        hand.add(c);
    }

    protected Card getCardByID(int id){ //TODO will have to change to follow order of single Image with all cards
        switch(id){
            case 1:
                return new ExcaliburWeapon();
            //break;
            case 2:
                return new LanceWeapon();
            //break;
            case 3:
                return new BattleAxWeapon();
            //break;
            case 4:
                return new SwordWeapon();
            //break;
            case 5:
                return new HorseWeapon();
            //break;
            case 6:
                return new DaggerWeapon();
            //break;
            case 7:
                return new DragonFoe();
            //break;
            case 8:
                return new GiantFoe();
            //break;
            case 9:
                return new MordredFoe();
            //break;
            case 10:
                return new GreenKnightFoe();
            //break;
            case 11:
                return new BlackKnightFoe();
            //break;
            case 12:
                return new EvilKnightFoe();
            //break;
            case 13:
                return new SaxonKnightFoe();
            //break;
            case 14:
                return new RobberKnightFoe();
            //break;
            case 15:
                return new SaxonsFoe();
            //break;
            case 16:
                return new BoarFoe();
            //break;
            case 17:
                return new ThievesFoe();
            //break;
        }
        return new ThievesFoe(); //Should never be reached
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
        hand.add(getCardByID(cardID));
    }

    protected void discardCardFromHand(int cardID){
        Card card = getCardByID(cardID);
        hand.remove(card);
        card.discard();
    }

    protected void discardCardsFromHand(Integer[] cardIDs){
        for(int i = 0; i < cardIDs.length; ++i){
            discardCardFromHand(cardIDs[i]);
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
