package network;

interface ClientEventListener {
    void onPlayerConnect(int plyID, String playerName, int[] cardIDs);
    void onPlayerDisconnect(int plyID, String playerName);

    void onStartGame();

    void onTurnChange(int idOfPlayer);

    void onUpdateHand(int plyID, int[] cardIDs);
    void onUpdateShields(int plyID, int shieldCount);
    void onUpdateAllies(int plyID, int[] cardIDs);
    void onUpdateAmour(int plyID, int cardID);
    void onClearAllies(int plyID);

    void onDrawCard(int plyID, int cardID);
    void onDrawCardX(int plyID, int[] cardIDs);
    void onCardDiscard(int plyID, int cardID);
    void onCardDiscardX(int plyID, int[] cardIDs);

    void onStoryDrawCard(int plyID, int cardID);

    void onQuestBegin(int plyID, int questCardID);
    void onQuestSponsorQuery(int questCardID);
    void onQuestParticipateQuery(int sponsorPlyID, int questID, int[] stageCardIDs);
    void onQuestStageResult(int questCardID, boolean wonStage, int[] stageCardsIDs, int[] playerCardsIDs);
    void onQuestFinalResult(int winnerID, int[][] sponsorCards);

    void onEventStoryBegin(int drawerID, int eventCardID);

    void onTournamentBegin(int drawerID, int tournamentCardID);
    void onTournamentParticipationQuery(int tournamentCardID);
    void onTournamentTie(int tournamentCardID);
    void onTournamentFinalResult(int winnerID);

}
