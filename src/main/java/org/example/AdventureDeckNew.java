package org.example;

import java.util.ArrayList;

public class AdventureDeckNew extends Deck{

    //I couldn't figure out how to override the old Adventure Deck when committing


    protected void initializeCards(){
        //foes
        cards.add(new DragonFoe());
        for(int i = 0; i < 2; ++i){
            cards.add(new GiantFoe());
        }
        for(int i = 0; i < 4; ++i){
            cards.add(new MordredFoe());
        }
        for(int i = 0; i < 2; ++i){
            cards.add(new GreenKnightFoe());
        }
        for(int i = 0; i < 3; ++i){
            cards.add(new BlackKnightFoe());
        }
        for(int i = 0; i < 6; ++i){
            cards.add(new EvilKnightFoe());
        }
        for(int i = 0; i < 8; ++i){
            cards.add(new SaxonKnightFoe());
        }
        for(int i = 0; i < 7; ++i){
            cards.add(new RobberKnightFoe());
        }
        for(int i = 0; i < 5; ++i){
            cards.add(new SaxonsFoe());
        }
        for(int i = 0; i < 4; ++i){
            cards.add(new BoarFoe());
        }
        for(int i = 0; i < 8; ++i){
            cards.add(new ThievesFoe());
        }

        //weapons
        for(int i = 0; i < 2; ++i){
            cards.add(new ExcaliburWeapon());
        }
        for(int i = 0; i < 6; ++i){
            cards.add(new LanceWeapon());
        }
        for(int i = 0; i < 8; ++i){
            cards.add(new BattleAxWeapon());
        }
        for(int i = 0; i < 16; ++i){
            cards.add(new SwordWeapon());
        }
        for(int i = 0; i < 11; ++i){
            cards.add(new HorseWeapon());
        }
        for(int i = 0; i < 6; ++i){
            cards.add(new DaggerWeapon());
        }

        //allies

        //tests

        //amours


        shuffle();
    }

    private void dealCardsToPlayers(ArrayList<Player> players){
        for(int i = 0; i < players.size(); ++i){
            for(int j = 0; j < 12; ++j){    //dealt 12 Cards at start of game
                players.get(i).hand.add(drawCard());
            }
        }
    }


}
