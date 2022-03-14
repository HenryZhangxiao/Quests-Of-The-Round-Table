package network;

import java.util.ArrayList;

public class ServerMessage extends NetworkMessage {
    public ServerMessage(NetworkMsgType type, ArrayList<Object> _objs) {
        super(-1, type, _objs);
    }
}
