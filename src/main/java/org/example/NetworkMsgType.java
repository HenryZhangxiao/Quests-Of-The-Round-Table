package org.example;

public enum NetworkMsgType {
    UNKNOWN, //Should never be this
    HEARTBEAT, //Sends every few seconds to the server if no message was sent in the last x seconds
    CONNECT, //Initial Connection. Server Sends ID to be assigned. Client replies with Name to use.
    DISCONNECT, //Sent on a disconnect
    TEST_MESSAGE, //For Testing
    UPDATE_PLAYERSTATUS //Sent from Server to all clients when a player joins/leaves.


    //GAME_NEW_ROUND, Example message types
    //GAME_PLAYER_CARDDRAW

}
