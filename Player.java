public class Player {

    Integer playerNum;
    public ArrayList<Card> hand;
    public ArrayList<Card> board;  //amours, weapons, etc in front of them

    protected int drawCard(){
        //send request to server
    }

    protected discardCardFromHand(Integer index){
        Card card = hand.get(index);
        hand.remove(card);
        card.discard();

        //make sure other players see discarded cards
    }

    protected discardCardsFromHand(Integer[] indices){
        for(int i = 0; i < indices.length; ++i){
            discardCardFromHand(indices[i]);
        }
    }


}
