package org.example;

public interface ClientEventListener {
    public void onPlayerConnect(int plyID, String playerName, int[] cardIDs);
    public void onPlayerDisconnect(int plyID, String playerName);

    public void onStartGame();

    public void onTurnChange(int idOfPlayer);

    public void onUpdateHand(int plyID, int[] cardIDs);
    public void onUpdateShields(int plyID, int shieldCount);

    public void onDrawCard(int plyID, int cardID);
    public void onDrawCardX(int plyID, int[] cardIDs);
    public void onCardDiscard(int plyID, int cardID);

    public void onStoryDrawCard(int plyID, int cardID);

    public void onQuestBegin(int plyID, int questCardID);
    public void onQuestSponsorQuery(int questCardID);
    public void onQuestParticipateQuery(int sponsorPlyID, int questID);
    public void onQuestStageResult(int questCardID, boolean wonStage, int[] stageCardsIDs, int[] playerCardsIDs);
    public void onQuestFinalResult(int winnerID, int[][] sponsorCards, int[] playerCards);

}
