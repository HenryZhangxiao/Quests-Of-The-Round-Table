package org.example;

public class QuestCard extends Card{

    protected byte stages;
    protected String specialFoe;

    public QuestCard(String _name, byte _stages, String _specialFoe){
        name = _name;
        stages = _stages;
        specialFoe = _specialFoe;
    }

    protected void setStages(byte _stages){
        stages = _stages;
    }

    protected void setSpecialFoe(String _foe){
        specialFoe = _foe;
    }

    protected byte getStages(){
        return stages;
    }

    protected String getSpecialFoe(){
        return specialFoe;
    }
}
