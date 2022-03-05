package org.example;

public class StoryDeck extends Deck{

    private static final int NUM_HOLY_GRAIL = 1;
    private static final int NUM_GREEN_KNIGHT = 1;
    private static final int NUM_QUESTING_BEAST = 1;
    private static final int NUM_QUEENS_HONOR = 1;
    private static final int NUM_FAIR_MAIDEN = 1;
    private static final int NUM_ENCHANTED_FOREST = 1;
    private static final int NUM_ARTHURS_ENEMIES = 2;
    private static final int NUM_SLAY_THE_DRAGON = 1;
    private static final int NUM_BOAR_HUNT = 2;
    private static final int NUM_SAXON_RAIDERS = 2;

    protected void initializeCards() {
        // Quests
        for(int i = 0; i < NUM_HOLY_GRAIL; ++i){
            cards.add(new HolyGrailQuest());
        }
        for(int i = 0; i < NUM_GREEN_KNIGHT; ++i){
            cards.add(new GreenKnightQuest());
        }
        for(int i = 0; i < NUM_QUESTING_BEAST; ++i){
            cards.add(new QuestingBeastQuest());
        }
        for(int i = 0; i < NUM_QUEENS_HONOR; ++i){
            cards.add(new QueensHonorQuest());
        }
        for(int i = 0; i < NUM_FAIR_MAIDEN; ++i){
            cards.add(new FairMaidenQuest());
        }
        for(int i = 0; i < NUM_ENCHANTED_FOREST; ++i){
            cards.add(new EnchantedForestQuest());
        }
        for(int i = 0; i < NUM_ARTHURS_ENEMIES; ++i){
            cards.add(new ArthursEnemiesQuest());
        }
        for(int i = 0; i < NUM_SLAY_THE_DRAGON; ++i){
            cards.add(new SlayTheDragonQuest());
        }
        for(int i = 0; i < NUM_BOAR_HUNT; ++i){
            cards.add(new BoarHuntQuest());
        }
        for(int i = 0; i < NUM_SAXON_RAIDERS; ++i){
            cards.add(new SaxonRaidersQuest());
        }

        shuffle();
    }
}
