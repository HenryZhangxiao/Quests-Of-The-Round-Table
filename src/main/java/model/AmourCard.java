package model;

public class AmourCard extends Card{

    protected byte bp, freeBids;

    public AmourCard(){
        name = "Amour";
        deck = "Adventure";
        id = 32;
        bp = 10;
        freeBids = 1;
    }

    protected void setBP(byte _bp){
        bp = _bp;
    }

    protected void setFreeBids(byte _freeBids){
        freeBids = _freeBids;
    }

    public byte getBP(){
        return bp;
    }

    protected byte getFreeBids(){
        return freeBids;
    }
}
