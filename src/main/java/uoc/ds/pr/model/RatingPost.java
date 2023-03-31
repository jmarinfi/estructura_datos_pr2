package uoc.ds.pr.model;

import uoc.ds.pr.SportEvents4Club;

public class RatingPost extends Post {

    private SportEvents4Club.Rating rating;

    public RatingPost(Player player, SportEvent sportEvent, SportEvents4Club.Rating rating) {
        this.player = player;
        this.sportEvent = sportEvent;
        this.rating = rating;
        this.action = "rating";
    }

    public SportEvents4Club.Rating getRating() {
        return rating;
    }

    public void setRating(SportEvents4Club.Rating rating) {
        this.rating = rating;
    }

    @Override
    public String message() {
        return "{'player': '" + player.getId() + "', 'sportEvent': '" + sportEvent.getEventId() + "', 'rating': '" + rating.name() + "', 'action': '" + action + "'}";
    }
}
