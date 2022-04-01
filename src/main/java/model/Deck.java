package model;

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

    protected void discardCard(Card c){
        discards.add(c);
    }

    protected void discardCards(Card[] cards){
        for(Card c: cards)
            discardCard(c);
    }

    protected Card[] drawCardX(int amountToDraw){
        Card[] cards = new Card[amountToDraw];
        for(int i = 0; i < amountToDraw; i++){
            cards[i] = drawCard();
        }
        return cards;
    }

    protected String getName(){
        return name;
    }

    protected abstract void initializeCards();

    protected abstract void rigCards();

    public void resetDeck(){
        discards.clear();
        cards.clear();
    }
}
