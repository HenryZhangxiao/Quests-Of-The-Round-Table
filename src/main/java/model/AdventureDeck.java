package model;

import java.util.ArrayList;

public class AdventureDeck extends Deck{

    private static final int NUM_EXCALIBUR = 2;
    private static final int NUM_LANCE = 6;
    private static final int NUM_BATTLE_AX = 8;
    private static final int NUM_SWORD = 16;
    private static final int NUM_HORSE = 11;
    private static final int NUM_DAGGER = 6;
    private static final int NUM_DRAGON = 1;
    private static final int NUM_GIANT = 2;
    private static final int NUM_MORDRED = 4;
    private static final int NUM_GREEN_KNIGHT = 2;
    private static final int NUM_BLACK_KNIGHT = 3;
    private static final int NUM_EVIL_KNIGHT = 6;
    private static final int NUM_SAXON_KNIGHT = 8;
    private static final int NUM_ROBBER_KNIGHT = 7;
    private static final int NUM_SAXONS = 5;
    private static final int NUM_BOAR = 4;
    private static final int NUM_THIEVES = 8;

    private static final int NUM_TESTS = 2;

    private static final int NUM_AMOURS = 8;

    protected void initializeCards(){

        // Weapons
        for(int i = 0; i < NUM_EXCALIBUR; ++i){
            cards.add(new ExcaliburWeapon());
        }
        for(int i = 0; i < NUM_LANCE; ++i){
            cards.add(new LanceWeapon());
        }
        for(int i = 0; i < NUM_BATTLE_AX; ++i){
            cards.add(new BattleAxWeapon());
        }
        for(int i = 0; i < NUM_SWORD; ++i){
            cards.add(new SwordWeapon());
        }
        for(int i = 0; i < NUM_HORSE; ++i){
            cards.add(new HorseWeapon());
        }
        for(int i = 0; i < NUM_DAGGER; ++i){
            cards.add(new DaggerWeapon());
        }

        // Foes
        for(int i = 0; i < NUM_DRAGON; ++i){
            cards.add(new DragonFoe());
        }
        for(int i = 0; i < NUM_GIANT; ++i){
            cards.add(new GiantFoe());
        }
        for(int i = 0; i < NUM_MORDRED; ++i){
            cards.add(new MordredFoe());
        }
        for(int i = 0; i < NUM_GREEN_KNIGHT; ++i){
            cards.add(new GreenKnightFoe());
        }
        for(int i = 0; i < NUM_BLACK_KNIGHT; ++i){
            cards.add(new BlackKnightFoe());
        }
        for(int i = 0; i < NUM_EVIL_KNIGHT; ++i){
            cards.add(new EvilKnightFoe());
        }
        for(int i = 0; i < NUM_SAXON_KNIGHT; ++i){
            cards.add(new SaxonKnightFoe());
        }
        for(int i = 0; i < NUM_ROBBER_KNIGHT; ++i){
            cards.add(new RobberKnightFoe());
        }
        for(int i = 0; i < NUM_SAXONS; ++i){
            cards.add(new SaxonsFoe());
        }
        for(int i = 0; i < NUM_BOAR; ++i){
            cards.add(new BoarFoe());
        }
        for(int i = 0; i < NUM_THIEVES; ++i){
            cards.add(new ThievesFoe());
        }

        //allies
        cards.add(new SirGalahadAlly());
        cards.add(new SirLancelotAlly());
        cards.add(new KingArthurAlly());
        cards.add(new SirTristanAlly());
        cards.add(new KingPellinoreAlly());
        cards.add(new SirGawainAlly());
        cards.add(new SirPercivalAlly());
        cards.add(new QueenGuinevereAlly());
        cards.add(new QueenIseultAlly());
        cards.add(new MerlinAlly());

        //tests
        for(int i = 0; i < NUM_TESTS; ++i){
            cards.add(new TestOfMorganLeFeyTest());
            cards.add(new TestOfTemptationTest());
            cards.add(new TestOfValorTest());
            cards.add(new TestOfTheQuestingBeastTest());
        }

        //amours
        for(int i = 0; i < NUM_AMOURS; ++i){
            cards.add(new AmourCard());
        }

        shuffle();
    }

    @Override
    protected void rigCards() {
        //Todo rig cards?
        cards.clear();

        initializeCards();
    }

    private void dealCardsToPlayers(ArrayList<Player> players){
        for(int i = 0; i < players.size(); ++i){
            for(int j = 0; j < 12; ++j){    //dealt 12 Cards at start of game
                players.get(i).hand.add(drawCard());
            }
        }
    }


}
