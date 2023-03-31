package uoc.ds.pr.model;

import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.SportEvents4Club;

import java.time.LocalDate;

public class Player implements Comparable<Player> {
    private String id;
    private String name;
    private String surname;
    private List<SportEvent> events;
    private LocalDate birthday;
    private List<Rating> ratings;
    private SportEvents4Club.Level level;
    private int numFollowers;
    private int numFollowings;
    private List<Post> posts;


	public Player(String idUser, String name, String surname, LocalDate birthday) {
        this.setId(idUser);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.events = new LinkedList<>();
        this.ratings = new LinkedList<>();
        this.level = SportEvents4Club.Level.ROOKIE;
        this.numFollowers = 0;
        this.numFollowings = 0;
        this.posts = new LinkedList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public boolean is(String playerID) {
        return id.equals(playerID);
    }

    public void addEvent(SportEvent sportEvent) {
        events.insertEnd(sportEvent);
    }

    public int numEvents() {
        return events.size();
    }

    public boolean isInSportEvent(String eventId) {
        boolean found = false;
        SportEvent sportEvent = null;
        Iterator<SportEvent> it = getEvents();
        while (it.hasNext() && !found) {
            sportEvent = it.next();
            found = sportEvent.is(eventId);
        }
        return found;
    }

    public int numSportEvents() {
        return events.size();
    }

    public Iterator<SportEvent> getEvents() {
        return events.values();
    }

    public boolean hasEvents() {
        return this.events.size()>0;
    }

    public Iterator<Rating> getRatings() {
        return ratings.values();
    }

    public int getNumRatings() {
        return ratings.size();
    }

    public void addRating(SportEvents4Club.Rating rating, String message, SportEvent sportEvent) {
        Rating r = new Rating(rating, message, this, sportEvent);
        ratings.insertEnd(r);
        updateLevel();
    }

    public void setLevel(SportEvents4Club.Level level) {
        this.level = level;
    }

    public SportEvents4Club.Level getLevel() {
        return level;
    }

    public void updateLevel() {
        int numRatings = getNumRatings();
        if (numRatings < 2) {
            setLevel(SportEvents4Club.Level.ROOKIE);
        } else if (numRatings < 5) {
            setLevel(SportEvents4Club.Level.PRO);
        } else if (numRatings < 10) {
            setLevel(SportEvents4Club.Level.EXPERT);
        } else if (numRatings < 15) {
            setLevel(SportEvents4Club.Level.MASTER);
        } else {
            setLevel(SportEvents4Club.Level.LEGEND);
        }
    }

    public void setNumFollowers(int numFollowers) {
        this.numFollowers = numFollowers;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowings(int numFollowings) {
        this.numFollowings = numFollowings;
    }

    public int getNumFollowings() {
        return numFollowings;
    }

    public Iterator<Post> getPosts() {
        return posts.values();
    }

    public void addPost(Post post) {
        posts.insertEnd(post);
    }

    @Override
    public int compareTo(Player o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Player p)) {
            return false;
        }
        return id.equals(p.id);
    }
}
