package uoc.ds.pr.model;

public class Enrollment {
    Player player;
    boolean isSubstitute;

    public Enrollment(Player player, boolean isSubstitute) {
        this.player = player;
        this.isSubstitute = isSubstitute;
    }

    public Player getPlayer() {
        return player;
    }
}
