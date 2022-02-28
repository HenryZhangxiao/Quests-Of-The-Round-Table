package org.example;

import javafx.scene.image.Image;

import java.io.File;

public abstract class WeaponCard extends Card{

    protected byte bp; //Battle points

    protected void setBP(byte _bp){
        bp = _bp;
    }

    protected byte getBP(){
        return bp;
    }

}
