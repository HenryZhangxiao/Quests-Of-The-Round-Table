public class WeaponCard extends Card{

    protected byte bp; //Battle points

    public WeaponCard(String _name, byte _bp){
        name = _name;
        bp = _bp;
    }

    protected void setBP(byte _bp){
        bp = _bp;
    }

    protected void setName(String _name){
        name = _name;
    }

    protected byte getBP(){
        return bp;
    }

}
