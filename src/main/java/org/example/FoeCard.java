package org.example;

public abstract class FoeCard extends Card{

    protected byte bp, altBP;
    protected boolean special;


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
