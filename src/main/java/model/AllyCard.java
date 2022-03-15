package model;//DOES NOT CURRENTLY ACCOUNT FOR THE MERLIN ALLY CARD, conditional bp or conditional free bids

public abstract class AllyCard extends Card{

    protected byte bp, freeBids;
/*
    public AllyCard(String _name, byte _bp, byte _freeBids){
        name = _name;
        bp = _bp;
        freeBids = _freeBids;
    }
*/
    protected void setBP(byte _bp){
        bp = _bp;
    }

    protected void setFreeBids(byte _freeBids){
        freeBids = _freeBids;
    }

    protected byte getBP(){
        return bp;
    }

    protected byte getFreeBids(){
        return freeBids;
    }
}


