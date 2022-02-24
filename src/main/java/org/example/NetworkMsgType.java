package org.example;

public enum NetworkMsgType {
    UNKNOWN, //Should never be this
    HEARTBEAT, //Sends every few seconds to the server if no message was sent in the last x seconds
    CONNECT, //Initial Connection. Sent with an ID to be assigned.
    DISCONNECT, //Sent on a disconnect
    TEST_MESSAGE //For Testing


    //GAME_NEW_ROUND, Example message types
    //GAME_PLAYER_CARDDRAW

}
