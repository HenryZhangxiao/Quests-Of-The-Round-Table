package network;

import model.Game;

import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkManager extends Thread {

    //Singleton Instance
    private static NetworkManager networkManger;

    //Listeners
    private ArrayList<ClientEventListener> _listeners;

    //A list of messages that have been received from the server.
    private BlockingQueue<NetworkMessage> _messagesReceived;

    //Port Number
    public final int PORT = 2005;

    //Is hosting the server/running the game
    private boolean isHost = false;

    //Local Player
    private NetworkClient localPlayer;


    private volatile boolean stopThread = false;

    private NetworkManager(){
        _messagesReceived = new LinkedBlockingQueue<NetworkMessage>();
        _listeners = new ArrayList<ClientEventListener>();

    }

    //Singleton
    public static NetworkManager get(){
        if(networkManger == null)
            networkManger = new NetworkManager();
        return networkManger;
    }

    //Joining a game by IP. Also called after creating a game.
    public boolean joinGame(String IP, String playerName){
        try {
            LocalGameManager.get();
            Socket s = new Socket(IP,PORT);
            localPlayer = new NetworkClient(s,-1,true);
            localPlayer.start();
            localPlayer.setPlayerName(playerName);

            this.start();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean createGame(String playerName){
        isHost = true;
        NetworkServer.get().start();
        Game.get();
        //Todo: Reenable this?
        //Game.get().start();

        return joinGame("localhost", playerName);
    }

    public void run(){
        while (!stopThread) {
            NetworkMessage msg;
            while ((msg = _messagesReceived.poll()) != null){
                ArrayList<Object> _objs = msg._objects;
                handleMessage(msg, _objs);
            }
        }
    }

    //These are received from the server.
    public void handleMessage(NetworkMessage msg,ArrayList<Object> _objs){
        switch (msg.messageType){
            case UNKNOWN:
                System.out.println("Unknown Message Received. Objs: " + _objs.toString());
            case HEARTBEAT:
                //Todo
                break;
            case CONNECT:
                //Gets PlayerID from server
                localPlayer.setPlayerId((int)_objs.get(0));
                //Sends back a name to use
                LocalClientMessage m = new LocalClientMessage(NetworkMsgType.CONNECT,NetworkMessage.pack(localPlayer.getPlayerName()));
                localPlayer.sendNetMsg(m);

                System.out.println("CLIENT: Set localID to " + _objs.get(0).toString());
                break;
            case DISCONNECT:
                //Todo
                break;
            case START_GAME:
                for (ClientEventListener l: _listeners) {
                    l.onStartGame();
                }
                break;
            case UPDATE_PLAYERLIST:
                for (ClientEventListener l: _listeners) {
                    l.onPlayerConnect((int)_objs.get(0),(String) _objs.get(1), (int[])_objs.get(2));
                }
                break;
            case UPDATE_HAND:
                for (ClientEventListener l: _listeners) {
                    l.onUpdateHand((int)_objs.get(0),(int[])_objs.get(1));
                }
                break;
            case UPDATE_SHIELDS:
                for (ClientEventListener l: _listeners) {
                    l.onUpdateShields((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case UPDATE_ALLIES:
                for (ClientEventListener l: _listeners) {
                    l.onUpdateAllies((int)_objs.get(0),(int[])_objs.get(1));
                }
                break;
            case UPDATE_AMOUR:
                for (ClientEventListener l: _listeners) {
                    l.onUpdateAmour((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case CLEAR_ALLIES:
                for (ClientEventListener l: _listeners) {
                    l.onClearAllies((int)_objs.get(0));
                }
                break;
            case CARD_DRAW_X:
                for (ClientEventListener l: _listeners) {
                    l.onDrawCardX((int)_objs.get(0),(int[])_objs.get(1));
                }
                break;
            case CARD_DRAW:
                for (ClientEventListener l: _listeners) {
                    l.onDrawCard((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case CARD_DISCARD:
                for (ClientEventListener l: _listeners) {
                    l.onCardDiscard((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case CARD_DISCARD_X:
                for (ClientEventListener l: _listeners) {
                    l.onCardDiscardX((int)_objs.get(0),(int[])_objs.get(1));
                }
                break;
            case STORY_CARD_DRAW:
                for (ClientEventListener l: _listeners) {
                    l.onStoryDrawCard((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case TURN_CHANGE:
                for (ClientEventListener l: _listeners) {
                    l.onTurnChange((int)_objs.get(0));
                }
                break;

            case QUEST_BEGIN:
                for (ClientEventListener l: _listeners) {
                    l.onQuestBegin((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case QUEST_SPONSOR_QUERY:
                for (ClientEventListener l: _listeners) {
                    l.onQuestSponsorQuery((int)_objs.get(0));
                }
                break;

            case QUEST_PARTICIPATE_QUERY:
                for (ClientEventListener l: _listeners) {
                    l.onQuestParticipateQuery((int)_objs.get(0),(int)_objs.get(1),(int[])_objs.get(2));
                }
                break;
            case QUEST_STAGE_RESULT:
                for (ClientEventListener l: _listeners) {
                    l.onQuestStageResult((int)_objs.get(0),(boolean)_objs.get(1),(int[])_objs.get(2),(int[])_objs.get(3));
                }
                break;
            case QUEST_FINAL_RESULT:
                for (ClientEventListener l: _listeners) {
                    l.onQuestFinalResult((int)_objs.get(0),(int[][])_objs.get(1));
                }
                break;
            case EVENT_BEGIN:
                for (ClientEventListener l: _listeners) {
                    l.onEventStoryBegin((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case TOURNAMENT_BEGIN:
                for (ClientEventListener l: _listeners) {
                    l.onTournamentBegin((int)_objs.get(0),(int)_objs.get(1));
                }
                break;
            case TOURNAMENT_PARTICIPATION_QUERY:
                for (ClientEventListener l: _listeners) {
                    l.onTournamentParticipationQuery((int)_objs.get(0));
                }
                break;
            case TOURNAMENT_TIE:
                for (ClientEventListener l: _listeners) {
                    l.onTournamentTie((int)_objs.get(0));
                }
                break;
            case TOURNAMENT_FINAL_RESULT:
                for (ClientEventListener l: _listeners) {
                    l.onTournamentFinalResult((int[])_objs.get(0));
                }
                break;
            case TEST_MESSAGE:
                System.out.println("Got a message from: " + String.valueOf(msg.playerID));
                break;
            case TEST_BEGIN:
                for (ClientEventListener l: _listeners) {
                    l.onTestBegin((int)_objs.get(0),(int) _objs.get(1));
                }
                break;
            case TEST_BID_QUERY:
                for (ClientEventListener l: _listeners) {
                    l.onTestBidQuery((int)_objs.get(0),(int) _objs.get(1),(int) _objs.get(2));
                }
                break;
            case TEST_FINAL_RESULT:
                for (ClientEventListener l: _listeners) {
                    l.onTestFinalResult((int)_objs.get(0),(int) _objs.get(1),(int) _objs.get(2));
                }
                break;
            case GAME_FINAL_RESULT:
                for (ClientEventListener l: _listeners) {
                    l.onGameFinalResult((int[])_objs.get(0));
                }
                break;
            case MORDRED_DISCARD:
                for (ClientEventListener l: _listeners) {
                    l.onMordredDiscard((int)_objs.get(0),(int)_objs.get(1),(int)_objs.get(2));
                }
                break;
            default:
                System.out.println("Default Message Received.");
                break;
        }
    }

    public void addReceivedMsg(NetworkMessage msg){
        try {
            _messagesReceived.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startGame(){
        if(isHost){
            LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.START_GAME,null);
            sendNetMessageToServer(msg);
        }
    }

    //region Helpers/Getters/Setters
    public void sendNetMessageToServer(LocalClientMessage msg){
        localPlayer.sendNetMsg(msg);
    }
    public NetworkClient getLocalPlayer(){ return localPlayer; }
    public int getLocalPlayerID() {return localPlayer.getPlayerId(); }
    public static boolean isInstantiated(){
        return networkManger != null;
    }
    public boolean isHost() {return isHost; }

    //Listeners
    public void addListener(ClientEventListener l){
        _listeners.add(l);
    }
    public void removeListener(ClientEventListener l){
        _listeners.remove(l);
    }

    //endregion

    public void close(){
        stopThread = true;
        this.interrupt(); //Not sure if you need this?



        if(isHost){
            NetworkServer.get().close();
        }
        else{
            localPlayer.disconnect();
        }

        localPlayer.close();
    }


}
