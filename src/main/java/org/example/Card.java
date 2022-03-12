package org.example;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Card {

    protected String name; //Name of card
    protected float x_cord, y_cord; //x and y coordinates for the GUI
    //protected Image img; Need to complete
    protected String deck;
    protected Integer id;   //used for getCardByID()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected void set_x_cord(float x){
        x_cord = x;
    }

    protected void set_y_cord(float y){
        y_cord = y;
    }

    protected void set_cords_arr(float[] arr){
        x_cord = arr[0];
        y_cord = arr[1];
    }

    protected void set_x_cord_floats(float x, float y){
        x_cord = x;
        y_cord = y;
    }

    protected void setName(String _name){
        name = _name;
    }

    protected void setID(int id_){
        id = id_;
    }

    protected float[] getCoordinates(){
        float[] arr = new float[2];
        arr[0] = x_cord;
        arr[1] = y_cord;
        return arr;
    }

    protected String getName(){
        return name;
    }

    protected int getID(){ return id; }

    public static Card getCardByID(int id){
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
            case 18:
                return new EnchantedForestQuest();
            case 19:
                return new ArthursEnemiesQuest();
            case 20:
                return new SaxonRaidersQuest();
            case 21:
                return new BoarHuntQuest();
            case 22:
                return new QuestingBeastQuest();
            case 23:
                return new QueensHonorQuest();
            case 24:
                return new SlayTheDragonQuest();
            case 25:
                return new FairMaidenQuest();
            case 26:
                return new HolyGrailQuest();
            case 27:
                return new GreenKnightQuest();

        }
        return new ThievesFoe(); //Should never be reached
    }

    public static int[] getCardIDsFromArrayList(ArrayList<Card> cards){
        int[] cardArry = new int[cards.size()];
        for(int i = 0; i < cards.size(); i++){
            cardArry[i] = cards.get(i).id;
        }
        return cardArry;
    }

    public static int[] getCardIDsFromArray(Card[] cards){
        int[] cardArry = new int[cards.length];
        for(int i = 0; i < cards.length; i++){
            cardArry[i] = cards[i].id;
        }
        return cardArry;
    }

    public static int[][] getStageCardIDsFromMDArray(Card[][] cards){
        int[][] cardArry = new int[cards.length][];
        for(int i = 0; i < cards.length; i++){
            cardArry[i] = getCardIDsFromArray(cards[i]);
        }
        return cardArry;
    }

    /*
    protected Image getImg(){
        return img;
    }
    */
}
