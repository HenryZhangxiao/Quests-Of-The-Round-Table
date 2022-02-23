public class TestCard extends Card{

    protected byte minimumBid;

    public TestCard(String _name){
        name = _name;
        minimumBid = 3;
    }

    public TestCard(String _name, byte _minBid){
        name = _name;
        minimumBid = _minBid;
    }

}
