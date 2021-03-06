package model;

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

    private static final int NUM_KINGS_RECOGNITION = 2;
    private static final int NUM_QUEENS_FAVOR = 2;
    private static final int NUM_COURT_TO_CAMELOT = 2;
    private static final int NUM_POX = 1;
    private static final int NUM_PLAGUE = 1;
    private static final int NUM_CHIVALROUS_DEED = 1;
    private static final int NUM_PROSPERITY = 1;
    private static final int NUM_KINGS_CALL_TO_ARMS = 1;

    private static final int NUM_CAMELOT = 1;
    private static final int NUM_ORNKEY = 1;
    private static final int NUM_TINTAGEL = 1;
    private static final int NUM_YORK = 1;

    protected void initializeCards() {
        cards.clear();

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

        for(int i = 0; i < NUM_KINGS_RECOGNITION; ++i){
            cards.add(new KingsRecognitionEvent());
        }
        for(int i = 0; i < NUM_QUEENS_FAVOR; ++i){
            cards.add(new QueensFavorEvent());
        }
        for(int i = 0; i < NUM_COURT_TO_CAMELOT; ++i){
            cards.add(new CourtToCamelotEvent());
        }
        for(int i = 0; i < NUM_POX; ++i){
            cards.add(new PoxEvent());
        }
        for(int i = 0; i < NUM_PLAGUE; ++i){
            cards.add(new PlagueEvent());
        }
        for(int i = 0; i < NUM_CHIVALROUS_DEED; ++i){
            cards.add(new ChivalrousDeedEvent());
        }
        for(int i = 0; i < NUM_PROSPERITY; ++i){
            cards.add(new ProsperityEvent());
        }
        for(int i = 0; i < NUM_KINGS_CALL_TO_ARMS; ++i){
            cards.add(new KingsCallToArmsEvent());
        }

        for(int i = 0; i < NUM_CAMELOT; ++i){
            cards.add(new CamelotTournament());
        }
        for(int i = 0; i < NUM_ORNKEY; ++i){
            cards.add(new OrkneyTournament());
        }
        for(int i = 0; i < NUM_TINTAGEL; ++i){
            cards.add(new TintagelTournament());
        }
        for(int i = 0; i < NUM_YORK; ++i){
            cards.add(new YorkTournament());
        }

        shuffle();
    }

    @Override
    protected void rigCards() {
        // Rigged For a 2 stage Quest, then a tournament, then an event
        cards.clear();


        //Adding


        for (int i = 0; i < NUM_HOLY_GRAIL; ++i) {
            cards.add(new HolyGrailQuest());
        }
        for (int i = 0; i < NUM_GREEN_KNIGHT; ++i) {
            cards.add(new GreenKnightQuest());
        }
        for (int i = 0; i < NUM_QUESTING_BEAST; ++i) {
            cards.add(new QuestingBeastQuest());
        }
        for (int i = 0; i < NUM_QUEENS_HONOR; ++i) {
            cards.add(new QueensHonorQuest());
        }
        for (int i = 0; i < NUM_FAIR_MAIDEN; ++i) {
            cards.add(new FairMaidenQuest());
        }
        for (int i = 0; i < NUM_ENCHANTED_FOREST; ++i) {
            cards.add(new EnchantedForestQuest());
        }
        for (int i = 0; i < NUM_ARTHURS_ENEMIES; ++i) {
            cards.add(new ArthursEnemiesQuest());
        }
        for (int i = 0; i < NUM_SLAY_THE_DRAGON; ++i) {
            cards.add(new SlayTheDragonQuest());
        }
        for (int i = 0; i < NUM_BOAR_HUNT; ++i) {
            cards.add(new BoarHuntQuest());
        }

        for (int i = 0; i < NUM_SAXON_RAIDERS - 1; ++i) {
            cards.add(new SaxonRaidersQuest());
        }



        for (int i = 0; i < NUM_KINGS_RECOGNITION; ++i) {
            cards.add(new KingsRecognitionEvent());
        }

        for (int i = 0; i < NUM_QUEENS_FAVOR - 1; ++i) {
            cards.add(new QueensFavorEvent());
        }

        for (int i = 0; i < NUM_COURT_TO_CAMELOT; ++i) {
            cards.add(new CourtToCamelotEvent());
        }
        for (int i = 0; i < NUM_POX; ++i) {
            cards.add(new PoxEvent());
        }
        for (int i = 0; i < NUM_PLAGUE; ++i) {
            cards.add(new PlagueEvent());
        }
        for (int i = 0; i < NUM_CHIVALROUS_DEED; ++i) {
            cards.add(new ChivalrousDeedEvent());
        }
        for (int i = 0; i < NUM_PROSPERITY; ++i) {
            cards.add(new ProsperityEvent());
        }
        for (int i = 0; i < NUM_KINGS_CALL_TO_ARMS; ++i) {
            cards.add(new KingsCallToArmsEvent());
        }


        for (int i = 0; i < NUM_CAMELOT; ++i) {
            cards.add(new CamelotTournament());
        }
        for (int i = 0; i < NUM_ORNKEY; ++i) {
            cards.add(new OrkneyTournament());
        }

        //Tintagel

        for (int i = 0; i < NUM_YORK; ++i) {
            cards.add(new YorkTournament());
        }

        shuffle();

        for (int i = 0; i < 1; ++i) {
            cards.add(0, new SaxonRaidersQuest());
        }

        for (int i = 0; i < NUM_TINTAGEL; ++i) {
            cards.add(1, new TintagelTournament());
        }

        for (int i = 0; i < 1; ++i) {
            cards.add(2,new QueensFavorEvent());
        }


    }

}
