package model;//DOES NOT CURRENTLY ACCOUNT FOR THE MERLIN ALLY CARD, conditional bp or conditional free bids

import java.util.ArrayList;

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

    public static int getBPForAllies(ArrayList<AllyCard> allyCards, QuestCard questCard, AmourCard amourCard){
        int bp = 0;
        if(amourCard != null)
            bp += amourCard.bp;

        for(AllyCard c: allyCards){
            if(c instanceof SirGawainAlly){
                if(questCard instanceof GreenKnightQuest)
                    bp += 20;
                else
                    bp += 10;
            }
            else if(c instanceof KingPellinoreAlly){
                bp += 10;
            }
            else if(c instanceof SirPercivalAlly){
                if(questCard instanceof HolyGrailQuest)
                    bp += 20;
                else
                    bp += 5;
            }
            else if(c instanceof SirTristanAlly){
                boolean hasIseult = false;
                for(AllyCard x: allyCards){
                    if(x instanceof QueenIseultAlly)
                        hasIseult = true;
                }
                if(hasIseult)
                    bp += 20;
                else
                    bp += 10;
            }
            else if(c instanceof KingArthurAlly){
                bp += 10;
            }
            else if(c instanceof SirLancelotAlly){
                if(questCard instanceof QueensHonorQuest)
                    bp += 25;
                else
                    bp += 15;
            }
            else if(c instanceof SirGalahadAlly){
                bp += 15;
            }
        }

        return bp;
    }

    public static int getBidsForAllies(ArrayList<AllyCard> allyCards, QuestCard questCard, AmourCard amourCard){
        int bids = 0;

        if(amourCard != null)
            bids += 1;

        for(AllyCard c: allyCards) {
            if (c instanceof KingPellinoreAlly) {
                if(questCard instanceof QuestingBeastQuest){
                    bids += 4;
                }
            }
            else if (c instanceof KingArthurAlly) {
                bids += 2;
            }
            else if (c instanceof QueenGuinevereAlly) {
                bids += 3;
            }
            else if (c instanceof QueenIseultAlly) {
                boolean hasTristan = false;
                for(AllyCard x: allyCards){
                    if(x instanceof SirTristanAlly)
                        hasTristan = true;
                }
                if(hasTristan)
                    bids += 4;
                else
                    bids += 2;
            }

        }

        return bids;
    }
}


