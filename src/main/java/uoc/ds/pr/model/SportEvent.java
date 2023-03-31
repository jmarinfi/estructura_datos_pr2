package uoc.ds.pr.model;

import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.PriorityQueue;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.adt.sequential.Queue;
import edu.uoc.ds.adt.sequential.QueueArrayImpl;
import edu.uoc.ds.traversal.Iterator;
import uoc.ds.pr.SportEvents4Club;

import java.time.LocalDate;
import java.util.Comparator;

import static uoc.ds.pr.SportEvents4Club.MAX_NUM_ENROLLMENT;

public class SportEvent implements Comparable<SportEvent> {
    public static final Comparator<SportEvent> CMP_V = (se1, se2)->Double.compare(se1.rating(), se2.rating());
    public static final Comparator<String> CMP_K = String::compareTo;
    private String eventId;
    private String description;
    private SportEvents4Club.Type type;
    private LocalDate startDate;
    private LocalDate endDate;
    private int max;
    private File file;
    private List<Rating> ratings;
    private double sumRating;
    private Queue<Enrollment> enrollments;
    private Queue<Enrollment> substitutes;
    private Dictionary<String, Attender> attendees;
    private List<Worker> workers;

    public SportEvent(String eventId, String description, SportEvents4Club.Type type,
                      LocalDate startDate, LocalDate endDate, int max, File file) {
        setEventId(eventId);
        setDescription(description);
        setStartDate(startDate);
        setEndDate(endDate);
        setType(type);
        setMax(max);
        setFile(file);
        this.enrollments = new QueueArrayImpl<>(MAX_NUM_ENROLLMENT);
        this.ratings = new LinkedList<>();
        this.substitutes = new PriorityQueue<>((e1, e2) -> Integer.compare(e2.player.getLevel().getValue(), e1.player.getLevel().getValue()));
        this.attendees = new HashTable<>(file.getNum());
        this.workers = new LinkedList<>();
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportEvents4Club.Type getType() {
        return type;
    }

    public void setType(SportEvents4Club.Type type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    public double rating() {
        return (this.ratings.size()>0?(sumRating / this.ratings.size()):0);
    }

    public void addRating(SportEvents4Club.Rating rating, String message, Player player) {
        Rating newRating = new Rating(rating, message, player, this);
        ratings.insertEnd(newRating);
        sumRating+=rating.getValue();
    }

    public boolean hasRatings() {
        return ratings.size()>0;
    }

    public Iterator<Rating> ratings() {
        return ratings.values();
    }

    public void addEnrollment(Player player) {
        enrollments.add(new Enrollment(player, false));
    }

    public boolean is(String eventId) {
        return this.eventId.equals(eventId);
    }

    @Override
    public int compareTo(SportEvent se2) {
        return eventId.compareTo(se2.eventId);
    }

    public boolean isFull() {
        return (attendees.size() + enrollments.size()>=max);
    }

    public int numPlayers() {
        return enrollments.size() + substitutes.size();
    }

    public void addSubstitute(Player player) {
        substitutes.add(new Enrollment(player, true));
    }

    public int getNumSubstitutes() {
        return substitutes.size();
    }

    public Iterator<Enrollment> getEnrollments() {
        return enrollments.values();
    }

    public Iterator<Enrollment> getSubstitutes() {
        return substitutes.values();
    }

    public Iterator<Attender> getAttendees() {
        return attendees.values();
    }

    public boolean hasAttendee(Attender attender) {
        return attendees.containsKey(attender.getPhone());
    }

    public void addAttendee(Attender attender) {
        attendees.put(attender.getPhone(), attender);
    }

    public Attender getAttendee(String phone) {
        return attendees.get(phone);
    }

    public int numAttenders() {
        return attendees.size();
    }

    public Iterator<Worker> getWorkers() {
        return workers.values();
    }

    public void addWorker(Worker worker) {
        workers.insertEnd(worker);
    }

    public int numWorkers() {
        return workers.size();
    }

    public boolean hasWorker(Worker worker) {
        Iterator<Worker> it = workers.values();
        boolean found = false;
        while (!found && it.hasNext()) {
            Worker w = it.next();
            if (w.equals(worker)) {
                found = true;
            }
        }
        return found;
    }

    public OrganizingEntity getOrganizingEntity() {
        return file.getOrganization();
    }
}
