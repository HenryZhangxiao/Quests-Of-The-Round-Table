package model;

public class RankCard extends Card{

    protected byte bp;

    public RankCard(String _name, byte _bp){
        name = _name;
        bp = _bp;
    }

    protected void setBp(byte _bp){
        bp = _bp;
    }

    protected byte getBp(){
        return bp;
    }
}
