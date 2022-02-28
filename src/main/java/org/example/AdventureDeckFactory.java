package org.example;

import java.util.ArrayList;

public interface AdventureDeckFactory {
    Card drawCard();
    void discardCard(Card c);
    void reshuffle(ArrayList<Card> arr);
    void initializeCards();
}
