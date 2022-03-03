package org.example;

public abstract class Card {

    protected String name; //Name of card
    protected float x_cord, y_cord; //x and y coordinates for the GUI
    //protected Image img; Need to complete
    protected String deck;
    protected Integer id;   //used for getCardByID()


    protected void discard(){
        // send message to server to add this card to discards of deck
        //deck.discards.add(this);

        //TODO: alter x and y coords so no longer on GUI?
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
        }
        return new ThievesFoe(); //Should never be reached
    }

    /*
    protected Image getImg(){
        return img;
    }
    */
}
