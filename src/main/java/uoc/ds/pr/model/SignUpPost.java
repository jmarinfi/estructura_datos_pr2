package uoc.ds.pr.model;

public class SignUpPost extends Post {

    public SignUpPost(Player player, SportEvent sportEvent) {
        this.player = player;
        this.sportEvent = sportEvent;
        this.action = "signup";
    }

    @Override
    public String message() {
        return "{'player': '" + player.getId() + "', 'sportEvent': '" + sportEvent.getEventId() + "', 'action': '" + action + "'}";
    }
}
