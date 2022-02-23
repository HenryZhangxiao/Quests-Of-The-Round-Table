public class Card {

    protected String name; //Name of card
    protected float x_cord, y_cord; //x and y coordinates for the GUI
    protected String img; //Source of image (??)

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

    protected float[] getCoordinates(){
        float[] arr = new float[2];
        arr[0] = x_cord;
        arr[1] = y_cord;
        return arr;
    }

    protected String getName(){
        return name;
    }

    protected String getImg(){
        return img;
    }
}
