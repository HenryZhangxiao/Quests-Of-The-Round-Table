package org.example;

import java.util.ArrayList;

//This class will only be ran on the server. All actions by the players will come in via event listeners and any responses must be sent
//out via a ServerMessage obj with NetworkServer.get().sendNetMessage
public class Game extends Thread implements ServerEventListener {

    private Deck deck;

    private ArrayList<Player> _players;
    private int turnPlayerID = 0;

    private volatile boolean stopThread = false;

    public Game(){
        _players = new ArrayList<Player>();
    }

    @Override
    public void run() {
        while (!stopThread){
            //Game loop in here.
        }
    }

    //region Helpers
    private Player getPlayerByID(int plyID){
        for(Player p: _players){
            if(p.getPlayerNum() == plyID)
                return p;
        }
        System.out.println("GAME: Couldn't find player by ID: " + String.valueOf(plyID));
        return null;
    }

    //endregion

    public void close(){
        stopThread = true;
        this.interrupt();

    }

    @Override
    public void onPlayerConnect(int plyID, String playerName) {
        Player p = new Player(plyID,playerName);

        _players.add(p);
    }

    @Override
    public void onPlayerDisconnect(int plyID, String playerName) {
        int x = -1;
        for (int i = 0; i < _players.size(); i++) {
            if(_players.get(i).getPlayerNum() == plyID)
                x = i;
        }

        if(x != -1)
            _players.remove(x);

        System.out.println("Player " + playerName + " disconnected. ID: " + String.valueOf(plyID));
    }

    @Override
    public void onTurnChange(int idOfPlayer) {
        turnPlayerID = idOfPlayer;
    }

    @Override
    public void onDrawCard(int plyID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        Card c = deck.drawCard();
        ServerMessage msg = new ServerMessage(NetworkMsgType.CARD_DRAW,NetworkMessage.pack(plyID, c.id));
        NetworkServer.get().sendNetMessage(msg);
    }

    @Override
    public void onCardDiscard(int plyID, int cardID) {
        if(plyID != turnPlayerID){
            System.out.println("SERVER: Not Player " + String.valueOf(plyID) + " turn. Current TurnID is " + String.valueOf(turnPlayerID));
            return;
        }

        NetworkMessage msg = new ServerMessage(NetworkMsgType.CARD_DISCARD,NetworkMessage.pack(plyID, cardID));
        NetworkManager.get().sendNetMessage(msg);
    }


}
