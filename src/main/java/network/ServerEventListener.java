package network;

public interface ServerEventListener {

    void onPlayerConnect(int plyID, String playerName);
    void onPlayerDisconnect(int plyID, String playerName);

    void onGameStart();

    void onTurnChange(int idOfPlayer);

    void onUpdateHand(int plyID, int[] cardIDs);

    void onDrawCard(int plyID);
    void onDrawCardX(int plyID, int amountOfCards);
    void onCardDiscard(int plyID, int cardID);

    void onStoryDrawCard(int plyID);

    void onQuestSponsorQuery(int plyID, boolean declined, int[][] questCards);
    void onQuestParticipateQuery(int plyID, boolean declined, int[] cards);


}