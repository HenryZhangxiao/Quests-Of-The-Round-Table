package model;

public class EventCard extends Card{

    protected String description;

    public EventCard(String _name, String _description){
        name = _name;
        description = _description;
    }

    protected void setDescription(String _description){
        description = _description;
    }

    protected String getDescription(){
        return description;
    }
}
