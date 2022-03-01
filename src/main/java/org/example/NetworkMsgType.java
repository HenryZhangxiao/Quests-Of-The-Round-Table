package org.example;

public enum NetworkMsgType {
    UNKNOWN, //Should never be this
    HEARTBEAT, //Sends every few seconds to the server if no message was sent in the last x seconds
    CONNECT, //Initial Connection. Server Sends ID to be assigned. Client replies with Name to use.
    DISCONNECT, //Sent on a disconnect
    UPDATE_PLAYERLIST, //Sent from server with ID,Name, and CardIDs
    START_GAME, //Send by host to start game.
    TEST_MESSAGE, //For Testing

    TURN_CHANGE, //[ID of whos turn it now is.]
    CARD_DRAW, //[ID of card drawn]
    CARD_DISCARD //[ID of card discarded]


    //GAME_NEW_ROUND, Example message types
    //GAME_PLAYER_CARDDRAW

}
