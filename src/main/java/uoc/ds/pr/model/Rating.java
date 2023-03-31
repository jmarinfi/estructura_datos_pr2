package uoc.ds.pr.model;

import uoc.ds.pr.SportEvents4Club;

public class Rating {
    private SportEvents4Club.Rating rating;
    private String message;
    private Player player;
    private SportEvent sportEvent;

    public Rating(SportEvents4Club.Rating rating, String message, Player user, SportEvent sportEvent) {
        this.rating = rating;
        this.message = message;
        this.player = user;
        this.sportEvent = sportEvent;
    }

    public SportEvents4Club.Rating rating() {

        return this.rating;
    }

    public Player getPlayer() {
        return this.player;
    }

    public SportEvent getSportEvent() {
        return sportEvent;
    }

}
