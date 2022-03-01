package org.example;

public interface ClientEventListener {
    public void onPlayerConnect(int plyID, String playerName, int[] cardIDs);
    public void onPlayerDisconnect(int plyID, String playerName);

    public void onStartGame();

    public void onTurnChange(int idOfPlayer);

    public void onDrawCard(int plyID, int cardID);
    public void onCardDiscard(int plyID, int cardID);

}
