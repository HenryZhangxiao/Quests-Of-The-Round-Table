public class FoeCard extends Card{

    protected byte bp, altBP;
    protected boolean special;

    public FoeCard(String _name, byte _bp, boolean _special){
        name = _name;
        bp = _bp;
        altBP = _bp; //Assign same value to both BP holders
        special = _special;
    }

    public FoeCard(String _name, byte _bp, byte _alt_bp, boolean _special){
        name = _name;
        bp = _bp;
        altBP = _alt_bp;
        special = _special;
    }

    protected void setBP(byte _bp){
        bp = _bp;
    }

    protected void setAlt_bp(byte _alt_bp){
        altBP = _alt_bp;
    }

    protected void setSpecial(boolean bool){
        special = bool;
    }

    protected byte getBP(){
        return bp;
    }

    protected byte getAlt_bp(){
        return altBP;
    }

    protected boolean getSpecial(){
        return special;
    }


}
