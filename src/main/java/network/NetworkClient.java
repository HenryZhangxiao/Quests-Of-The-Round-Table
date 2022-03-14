package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkClient extends Thread{

    //The name that will represent the player
    private String playerName;

    //Internal ID for game management. Is shared over server/client.
    private int playerId = -1;

    //Sockets and streams
    private Socket sock;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    //Set when connected to server. False when disconnected.
    private boolean connected = false;

    private boolean isLocalPlayer = false;

    //Used to stop internal Thread
    private volatile boolean stopThread = false;

    public NetworkClient(Socket socket, int plyID, boolean isLocalPly){
        isLocalPlayer = isLocalPly;
        playerId = plyID;
        try {
            sock = socket;
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
            connected = true;
        }
        catch (Exception e){
            System.out.println("Exception: " + e.toString());
            connected = false;
        }
    }

    public void run(){
        while (!stopThread){
            if(!sock.isClosed()) {
                try {
                    NetworkMessage msg = (NetworkMessage) inStream.readObject();
                    if (isLocalPlayer)
                        NetworkManager.get().addReceivedMsg(msg);
                    else
                        NetworkServer.get().addReceivedMsg(msg);
                } catch (Exception e) {
                    if (isLocalPlayer)
                        System.out.println("CLIENT: Couldn't Read Network Message.");
                    else
                        System.out.println("SERVER: Couldn't Read Network Message from ID: " + String.valueOf(playerId));
                    System.out.println(e.toString());
                }
            }
        }
    }

    //Called when the server/client disconnects, notifying all other clients and the server
    public void disconnect() {
        if(connected) {
            NetworkMessage msg = new LocalClientMessage(NetworkMsgType.DISCONNECT, null);
            sendNetMsg(msg);
        }
    }
    
    public void sendNetMsg(NetworkMessage msg){
        try{
            outStream.writeObject(msg);
            outStream.flush();
            outStream.reset();
        }
        catch (Exception e){
            if(isLocalPlayer)
                System.out.println("CLIENT: Couldn't Write Network Message.");
            else
                System.out.println("SERVER: Couldn't Write Network Message.");

            System.out.println(e.toString());
        }
    }

    //Called on both server and client when program closes to close all sockets.
    public void close(){
        try {
            stopThread = true;
            this.interrupt();

            sock.close();
            inStream.close();
            outStream.close();
            connected = false;
        }
        catch (Exception e){
            System.out.println("Exception: " + e.toString());
            connected = false;
        }
    }

    //region Getters/Setters
    public void setPlayerName(String name){
        playerName = name;
    }
    public String getPlayerName(){ return playerName; }

    public void setPlayerId(int plyID) { playerId = plyID; }
    public int getPlayerId(){ return playerId; }

    public boolean isConnected(){ return connected; }
    //endregion

}