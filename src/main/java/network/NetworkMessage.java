package network;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkMessage implements Serializable {

    //-1 is server. <= 0 is a client
    public int playerID = -1;

    public NetworkMsgType messageType = NetworkMsgType.UNKNOWN;

    ArrayList<Object> _objects;

    public NetworkMessage(int senderID, NetworkMsgType type,ArrayList<Object> _objs){
        playerID = senderID;
        messageType = type;
        _objects = _objs;
    }

    public static ArrayList<Object> pack(Object... args){
        ArrayList<Object> o = new ArrayList<>();
        for(Object i : args){
            o.add(i);
        }
        return o;
    }

}

