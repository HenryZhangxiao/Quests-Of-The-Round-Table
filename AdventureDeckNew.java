public class AdventureDeck extends Deck{

    private void dealCardsToPlayers(ArrayList<Player> players){
        for(int i = 0; i < players.size(); ++i){
            for(int j = 0; j < 12; ++j){    //dealt 12 Cards at start of game
                players.get(i).hand.add(drawCard());
            }
        }
    }
}
