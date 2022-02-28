public class TournamentCard extends Card{

    protected byte bonusShields;

    public TournamentCard(String _name, byte _bonus){
        name = _name;
        bonusShields = _bonus;
    }

    protected void setBonusShields(byte _bonus){
        bonusShields = _bonus;
    }

    protected byte getBonusShields(){
        return bonusShields;
    }
}
