package org.example;

public abstract class WeaponCard extends Card{

    protected byte bp; //Battle points

    protected void setBP(byte _bp){
        bp = _bp;
    }

    protected byte getBP(){
        return bp;
    }

}
