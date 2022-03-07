package org.example;

import java.util.ArrayList;

public abstract class QuestCard extends Card{

    protected byte stages;
    protected String[] specialFoes;

    public QuestCard(String _name, byte _stages, String[] _specialFoes, Integer _id){
        name = _name;
        stages = _stages;
        specialFoes = _specialFoes;
        id = _id;
    }

    protected void setStages(byte _stages){
        stages = _stages;
    }

    protected void setSpecialFoe(String[] _foes){
        specialFoes = _foes;
    }

    protected byte getStages(){
        return stages;
    }

    protected String[] getSpecialFoes(){
        return specialFoes;
    }
}
