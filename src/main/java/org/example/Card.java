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

    /*
    protected Image getImg(){
        return img;
    }
    */
}
