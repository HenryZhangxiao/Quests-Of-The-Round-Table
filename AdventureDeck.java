import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdventureDeck implements AdventureDeckFactory{
    ArrayList<Card> weapons;
    ArrayList<Card> foes;
    ArrayList<Card> tests;
    ArrayList<Card> allies;

    public AdventureDeck(){
        weapons = new ArrayList<>();
        foes = new ArrayList<>();
        tests = new ArrayList<>();
        allies = new ArrayList<>();
    }

    @Override
    public Card drawCard() {
        return null;
    }

    @Override
    public void discardCard(Card c) {

    }

    @Override
    public void reshuffle(ArrayList<Card> arr) {

    }

    @Override
    public void initializeCards() {
        String[] weaponNames = {
                "Excalibur","Excalibur",
                "Lance","Lance","Lance","Lance","Lance","Lance",
                "Battle-ax", "Battle-ax","Battle-ax","Battle-ax","Battle-ax","Battle-ax","Battle-ax","Battle-ax",
                "Sword","Sword","Sword","Sword","Sword","Sword","Sword","Sword",
                "Sword","Sword","Sword","Sword","Sword","Sword","Sword","Sword",
                "Horse","Horse","Horse","Horse","Horse","Horse","Horse","Horse","Horse","Horse","Horse",
                "Dagger","Dagger","Dagger","Dagger","Dagger","Dagger"};
        Byte[] weaponBP = {
                30,30, //Excalibur
                20,20,20,20,20,20, //Lance
                15,15,15,15,15,15,15,15, //Battle-ax
                10,10,10,10,10,10,10,10, //Sword
                10,10,10,10,10,10,10,10, //Sword
                10,10,10,10,10,10,10,10,10,10, //Horse
                5,5,5,5,5,5 //Dagger
        };

        for(int i = 0; i < weaponNames.length; i++ ){
            weapons.add(new WeaponCard(weaponNames[i], weaponBP[i]));
        }


    }
}
