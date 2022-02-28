package org.example;

import java.util.ArrayList;

public class LocalClientMessage extends NetworkMessage{
    public LocalClientMessage(NetworkMsgType type, ArrayList<Object> _objs) {
        super(NetworkManager.get().getLocalPlayerID(), type, _objs);
    }
}
