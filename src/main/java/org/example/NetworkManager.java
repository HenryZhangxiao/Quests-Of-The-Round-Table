package org.example;

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

    //Temporary
    private ArrayList<String> playerList;

    private boolean stopThread = false;

    private NetworkManager(){
        _messagesReceived = new LinkedBlockingQueue<NetworkMessage>();
        _listeners = new ArrayList<ClientEventListener>();
        playerList = new ArrayList<String>();
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
            Socket s = new Socket(IP,PORT);
            localPlayer = new NetworkClient(s,-1,true);
            localPlayer.start();

            playerList.add(playerName);
            for (ClientEventListener l: _listeners) {
                l.onPlayerListUpdate((String[]) playerList.toArray());
            }

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

    public void handleMessage(NetworkMessage msg,ArrayList<Object> _objs){
        switch (msg.messageType){
            case UNKNOWN:
                System.out.println("Unknown Message Received. Objs: " + _objs.toString());
            case HEARTBEAT:
                //Todo
                break;
            case CONNECT:
                localPlayer.setPlayerId((int)_objs.get(0));
                System.out.println("CLIENT: Set localID to " + _objs.get(0).toString());
                break;
            case DISCONNECT:
                //Todo
                break;
            case TEST_MESSAGE:
                System.out.println("Got a message from: " + String.valueOf(msg.playerID));
                break;
            case UPDATE_PLAYERSTATUS:


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

    //region Helpers/Getters/Setters
    public void sendNetMessage(NetworkMessage msg){
        localPlayer.sendNetMsg(msg);
    }
    public NetworkClient getLocalPlayer(){ return localPlayer; }
    public int getLocalPlayerID() {return localPlayer.getPlayerId(); }
    public static boolean isInstantiated(){
        return networkManger != null;
    }

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
