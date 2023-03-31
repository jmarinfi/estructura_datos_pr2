package uoc.ds.pr.model;

import uoc.ds.pr.SportEvents4Club;

public abstract class Post {

    protected Player player;
    protected SportEvent sportEvent;
    protected String action;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    abstract public String message();
}
