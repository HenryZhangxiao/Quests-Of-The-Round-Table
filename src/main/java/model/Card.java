package model;

import java.util.ArrayList;
import java.util.Arrays;
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

    public String getName(){
        return name;
    }

    public int getID(){ return id; }

    public static Card getCardByID(int id){
        switch(id){
            case 1:
                return new ExcaliburWeapon();
            case 2:
                return new LanceWeapon();
            case 3:
                return new BattleAxWeapon();
            case 4:
                return new SwordWeapon();
            case 5:
                return new HorseWeapon();
            case 6:
                return new DaggerWeapon();
            case 7:
                return new DragonFoe();
            case 8:
                return new GiantFoe();
            case 9:
                return new MordredFoe();
            case 10:
                return new GreenKnightFoe();
            case 11:
                return new BlackKnightFoe();
            case 12:
                return new EvilKnightFoe();
            case 13:
                return new SaxonKnightFoe();
            case 14:
                return new RobberKnightFoe();
            case 15:
                return new SaxonsFoe();
            case 16:
                return new BoarFoe();
            case 17:
                return new ThievesFoe();
            case 18:
                return new TestOfValorTest();
            case 19:
                return new TestOfTemptationTest();
            case 20:
                return new TestOfMorganLeFeyTest();
            case 21:
                return new TestOfTheQuestingBeastTest();
            case 22:
                return new SirGalahadAlly();
            case 23:
                return new SirLancelotAlly();
            case 24:
                return new KingArthurAlly();
            case 25:
                return new SirTristanAlly();
            case 26:
                return new KingPellinoreAlly();
            case 27:
                return new SirGawainAlly();
            case 28:
                return new SirPercivalAlly();
            case 29:
                return new QueenGuinevereAlly();
            case 30:
                return new QueenIseultAlly();
            case 31:
                return new MerlinAlly();
            case 32:
                return new AmourCard();
            case 33:
                return new EnchantedForestQuest();
            case 34:
                return new ArthursEnemiesQuest();
            case 35:
                return new SaxonRaidersQuest();
            case 36:
                return new BoarHuntQuest();
            case 37:
                return new QuestingBeastQuest();
            case 38:
                return new QueensHonorQuest();
            case 39:
                return new SlayTheDragonQuest();
            case 40:
                return new FairMaidenQuest();
            case 41:
                return new HolyGrailQuest();
            case 42:
                return new GreenKnightQuest();
            case 43:
                return new ChivalrousDeedEvent();
            case 44:
                return new PoxEvent();
            case 45:
                return new PlagueEvent();
            case 46:
                return new KingsRecognitionEvent();
            case 47:
                return new QueensFavorEvent();
            case 48:
                return new CourtToCamelotEvent();
            case 49:
                return new KingsCallToArmsEvent();
            case 50:
                return new ProsperityEvent();
            case 51:
                return new CamelotTournament();
            case 52:
                return new OrkneyTournament();
            case 53:
                return new TintagelTournament();
            case 54:
                return new YorkTournament();
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

    public static Card[] getCardsFromIDArray(int[] cardIDs){
        Card[] cards = new Card[cardIDs.length];
        for(int i = 0; i < cards.length; i++){
            cards[i] = Card.getCardByID(cardIDs[i]);
        }
        return cards;
    }

    public static ArrayList<Card> getCardListFromCardArray(Card[] cards){
        ArrayList<Card> list = new ArrayList<>();
        for(int i = 0; i < cards.length; i++)
            list.add(cards[i]);
        return list;
    }

    public static Card[] getCardArrayFromCardList(ArrayList<Card> cards){
        Card[] arry = new Card[cards.size()];
        for(int i = 0; i < cards.size();i++){
            arry[i] = cards.get(i);
        }
        return arry;
    }

    /*
    protected Image getImg(){
        return img;
    }
    */
}
