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
    CARD_DISCARD, //[ID of card discarded]

    QUEST_BEGIN, //To Client [ID of quest card]
    QUEST_SPONSOR_QUERY, //To Client[ID of quest] - To Server [Declined boolean, Quest Data structure.]
    QUEST_PARTICIPATE_QUERY, //To Client[ID of quest, ID of player who sponsored it.] - To Server [declined boolean, Card Array of cards in quest]
    QUEST_RESULT, //The result of the quest. To client [The ID of the player who won, Sponsors cards, all participants cards]



    //GAME_NEW_ROUND, Example message types
    //GAME_PLAYER_CARDDRAW

}
