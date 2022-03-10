package org.example;

public interface ServerEventListener {

    public void onPlayerConnect(int plyID, String playerName);
    public void onPlayerDisconnect(int plyID, String playerName);

    public void onGameStart();

    public void onTurnChange(int idOfPlayer);

    public void onDrawCard(int plyID);
    public void onCardDiscard(int plyID, int cardID);

    public void onStoryDrawCard(int plyID);

    public void onQuestSponsorQuery(int plyID, boolean declined, int[][] questCards);
    public void onQuestParticipateQuery(int plyID, boolean declined, int[] cards);


}
