import java.util.ArrayList;
import java.util.Collections;

public abstract class Deck {
    String name;
    ArrayList<Card> cards;
    Arraylist<Card> discards;

    protected void shuffle(){
        if(cards.isEmpty()) {
            cards = discards;
            discards.clear();
            Collections.shuffle(cards);
        }
    }

    protected Card drawCard(){
        return cards.remove(0);
    }

    protected String getName(){
        return name;
    }

    protected void setName(String _name){
        name = _name;
    }
}
