package org.example;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Deck {
    String name;
    protected ArrayList<Card> cards;
    protected ArrayList<Card> discards;

    public Deck(){
        cards = new ArrayList<>();
        discards = new ArrayList<>();
    }

    protected void shuffle(){
        Collections.shuffle(cards);
    }

    protected void reshuffle(){
        if(cards.isEmpty()) {
            cards = discards;
            discards.clear();
            shuffle();
        }
    }

    protected Card drawCard(){
        if(cards.isEmpty())
            reshuffle();
        return cards.remove(0);
    }

    protected String getName(){
        return name;
    }

    protected abstract void initializeCards();


}
