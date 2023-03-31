package uoc.ds.pr;

import edu.uoc.ds.adt.nonlinear.Dictionary;
import edu.uoc.ds.adt.nonlinear.DictionaryAVLImpl;
import edu.uoc.ds.adt.nonlinear.HashTable;
import edu.uoc.ds.adt.nonlinear.graphs.*;
import edu.uoc.ds.adt.sequential.LinkedList;
import edu.uoc.ds.adt.sequential.List;
import edu.uoc.ds.traversal.Iterator;

import java.time.LocalDate;
import java.util.PriorityQueue;
import java.util.Queue;

import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.*;
import uoc.ds.pr.util.OrderedVector;

public class SportEvents4ClubImpl implements SportEvents4Club {

    public static final int MAX_NUM_BEST_SPORT_EVENTS_BY_RATING = 10;

    private Dictionary<String, Player> players;
    private Dictionary<String, OrganizingEntity> organizingEntities;
    private Queue<File> files;
    private int totalFiles;
    private int rejectedFiles;
    private Dictionary<String, SportEvent> sportEvents;
    private Player mostActivePlayer;
    private OrderedVector<SportEvent> bestSportEventsByRating;
    private Role[] roles;
    private int numRoles;
    private Dictionary<String, Worker> workers;
    private OrderedVector<OrganizingEntity> best5OrganizingEntities;
    private SportEvent bestSportEventByAttenders;
    private DirectedGraph<Player, String> socialNetwork;

    public SportEvents4ClubImpl() {
        players = new DictionaryAVLImpl<>();
        organizingEntities = new HashTable<>(MAX_NUM_ORGANIZING_ENTITIES);
        files = new PriorityQueue<>();
        totalFiles = 0;
        rejectedFiles = 0;
        sportEvents = new DictionaryAVLImpl<>();
        mostActivePlayer = null;
        bestSportEventsByRating = new OrderedVector<>(MAX_NUM_BEST_SPORT_EVENTS_BY_RATING, SportEvent.CMP_V);
        roles = new Role[MAX_ROLES];
        numRoles = 0;
        workers = new HashTable<>();
        best5OrganizingEntities = new OrderedVector<>(MAX_ORGANIZING_ENTITIES_WITH_MORE_ATTENDERS, (oe1, oe2) -> Integer.compare(oe1.numAttenders(), oe2.numAttenders()));
        bestSportEventByAttenders = null;
        socialNetwork = new DirectedGraphImpl<>();
    }

    @Override
    public void addPlayer(String id, String name, String surname, LocalDate dateOfBirth) {
        Player p = getPlayer(id);
        if (p != null) {
            p.setName(name);
            p.setSurname(surname);
            p.setBirthday(dateOfBirth);
        } else {
            p = new Player(id, name, surname, dateOfBirth);
            players.put(p.getId(), p);
        }
    }

    @Override
    public void addOrganizingEntity(String id, String name, String description) {
        OrganizingEntity oe = getOrganizingEntity(id);
        if (oe != null) {
            oe.setName(name);
            oe.setDescription(description);
        } else {
            oe = new OrganizingEntity(id, name, description);
            organizingEntities.put(oe.getOrganizationId(), oe);
        }
    }

    @Override
    public void addFile(String id, String eventId, String orgId, String description, Type type, byte resources, int max, LocalDate startDate, LocalDate endDate) throws OrganizingEntityNotFoundException {
        OrganizingEntity oe = getOrganizingEntity(orgId);
        if (oe == null) {
            throw new OrganizingEntityNotFoundException();
        }
        files.add(new File(id, eventId, description, type, startDate, endDate, resources, max, oe));
        totalFiles++;
    }

    @Override
    public File updateFile(Status status, LocalDate date, String description) throws NoFilesException {
        File file = files.poll();
        if (file == null) {
            throw new NoFilesException();
        }
        file.update(status, date, description);
        if (file.isEnabled()) {
            SportEvent sportEvent = file.newSportEvent();
            sportEvents.put(sportEvent.getEventId(), sportEvent);
        } else {
            rejectedFiles++;
        }
        return file;
    }

    @Override
    public void signUpEvent(String playerId, String eventId) throws PlayerNotFoundException, SportEventNotFoundException, LimitExceededException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        player.addEvent(sportEvent);
        if (!sportEvent.isFull()) {
            sportEvent.addEnrollment(player);
        } else {
            sportEvent.addSubstitute(player);
            throw new LimitExceededException();
        }
        updateMostActivePlayer(player);
        player.addPost(new SignUpPost(player, sportEvent));
    }

    private void updateMostActivePlayer(Player player) {
        if (mostActivePlayer == null) {
            mostActivePlayer = player;
        } else if (player.numSportEvents() > mostActivePlayer.numSportEvents()) {
            mostActivePlayer = player;
        }
    }

    @Override
    public double getRejectedFiles() {
        return (double) rejectedFiles / (double) totalFiles;
    }

    @Override
    public Iterator<SportEvent> getSportEventsByOrganizingEntity(String organizationId) throws NoSportEventsException {
        OrganizingEntity organizingEntity = getOrganizingEntity(organizationId);
        if (organizingEntity == null || !organizingEntity.hasActivities()) {
            throw new NoSportEventsException();
        }
        return organizingEntity.sportEvents();
    }

    @Override
    public Iterator<SportEvent> getAllEvents() throws NoSportEventsException {
        Iterator<SportEvent> it = sportEvents.values();
        if (!it.hasNext()) {
            throw new NoSportEventsException();
        }
        return it;
    }

    @Override
    public Iterator<SportEvent> getEventsByPlayer(String playerId) throws NoSportEventsException {
        Player player = getPlayer(playerId);
        if (player == null || !player.hasEvents()) {
            throw new NoSportEventsException();
        }
        return player.getEvents();
    }

    @Override
    public void addRating(String playerId, String eventId, Rating rating, String message) throws SportEventNotFoundException, PlayerNotFoundException, PlayerNotInSportEventException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        if (!player.isInSportEvent(eventId)) {
            throw new PlayerNotInSportEventException();
        }
        sportEvent.addRating(rating, message, player);
        updateBestSportEvents(sportEvent);
        player.addRating(rating, message, sportEvent);
        player.addPost(new RatingPost(player, sportEvent, rating));
    }

    private void updateBestSportEvents(SportEvent sportEvent) {
        bestSportEventsByRating.delete(sportEvent);
        bestSportEventsByRating.update(sportEvent);
    }

    @Override
    public Iterator<uoc.ds.pr.model.Rating> getRatingsByEvent(String eventId) throws SportEventNotFoundException, NoRatingsException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        if (!sportEvent.hasRatings()) {
            throw new NoRatingsException();
        }
        return sportEvent.ratings();
    }

    @Override
    public Player mostActivePlayer() throws PlayerNotFoundException {
        if (mostActivePlayer == null) {
            throw new PlayerNotFoundException();
        }
        return mostActivePlayer;
    }

    @Override
    public SportEvent bestSportEvent() throws SportEventNotFoundException {
        if (bestSportEventsByRating.isEmpty()) {
            throw new SportEventNotFoundException();
        }
        return bestSportEventsByRating.elementAt(0);
    }
    
    @Override
    public void addRole(String roleId, String description) {
        Role role = getRole(roleId);
        if (role != null) {
            role.setDescription(description);
        } else {
            roles[numRoles] = new Role(roleId, description);
            numRoles++;
        }
    }

    @Override
    public void addWorker(String dni, String name, String surname, LocalDate birthDay, String roleId) {
        Worker worker = getWorker(dni);
        Role role = getRole(roleId);
        if (worker != null) {
            worker.setName(name);
            worker.setSurname(surname);
            worker.setBirthday(birthDay);
            Role oldRole = worker.getRole();
            if (!oldRole.equals(role)) {
                oldRole.deleteWorker(worker);
                worker.setRole(role);
                role.addWorker(worker);
            }
        } else {
            worker = new Worker(dni, name, surname, birthDay, role);
            workers.put(worker.getDni(), worker);
            role.addWorker(worker);
        }
    }

    @Override
    public void assignWorker(String dni, String eventId) throws WorkerNotFoundException, WorkerAlreadyAssignedException, SportEventNotFoundException {
        SportEvent sportEvent = getSportEvent(eventId);
        Worker worker = getWorker(dni);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        if (worker == null) {
            throw new WorkerNotFoundException();
        }
        if (sportEvent.hasWorker(worker)) {
            throw new WorkerAlreadyAssignedException();
        }
        sportEvent.addWorker(worker);
    }

    @Override
    public Iterator<Worker> getWorkersBySportEvent(String eventId) throws SportEventNotFoundException, NoWorkersException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        Iterator<Worker> workers = sportEvent.getWorkers();
        if (!workers.hasNext()) {
            throw new NoWorkersException();
        }
        return workers;
    }

    @Override
    public Iterator<Worker> getWorkersByRole(String roleId) throws NoWorkersException {
        Role role = getRole(roleId);
        Iterator<Worker> workers = role.getWorkers();
        if (!workers.hasNext()) {
            throw new NoWorkersException();
        }
        return workers;
    }

    @Override
    public Level getLevel(String playerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        return player.getLevel();
    }

    @Override
    public Iterator<Enrollment> getSubstitutes(String eventId) throws SportEventNotFoundException, NoSubstitutesException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        Iterator<Enrollment> substitutes = sportEvent.getSubstitutes();
        if (!substitutes.hasNext()) {
            throw new NoSubstitutesException();
        }
        return substitutes;
    }

    @Override
    public void addAttender(String phone, String name, String eventId) throws AttenderAlreadyExistsException, SportEventNotFoundException, LimitExceededException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        Attender attender = new Attender(phone, name);
        if (sportEvent.hasAttendee(attender)) {
            throw new AttenderAlreadyExistsException();
        }
        if (sportEvent.isFull()) {
            throw new LimitExceededException();
        }
        sportEvent.addAttendee(attender);
        OrganizingEntity organization = sportEvent.getOrganizingEntity();
        organization.incrementAttendees(1);
        best5OrganizingEntities.delete(organization);
        best5OrganizingEntities.update(organization);
        if (bestSportEventByAttenders == null || bestSportEventByAttenders.numAttenders() < sportEvent.numAttenders()) {
            bestSportEventByAttenders = sportEvent;
        }
    }

    @Override
    public Attender getAttender(String phone, String sportEventId) throws SportEventNotFoundException, AttenderNotFoundException {
        SportEvent sportEvent = getSportEvent(sportEventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        Attender attender = sportEvent.getAttendee(phone);
        if (attender == null) {
            throw new AttenderNotFoundException();
        }
        return attender;
    }

    @Override
    public Iterator<Attender> getAttenders(String eventId) throws SportEventNotFoundException, NoAttendersException {
        SportEvent sportEvent = getSportEvent(eventId);
        if (sportEvent == null) {
            throw new SportEventNotFoundException();
        }
        Iterator<Attender> attendees = sportEvent.getAttendees();
        if (!attendees.hasNext()) {
            throw new NoAttendersException();
        }
        return attendees;
    }

    @Override
    public Iterator<OrganizingEntity> best5OrganizingEntities() throws NoAttendersException {
        if (organizingEntities.isEmpty() || sportEvents.isEmpty() || best5OrganizingEntities.isEmpty()) {
            throw new NoAttendersException();
        }
        return best5OrganizingEntities.values();
    }

    @Override
    public SportEvent bestSportEventByAttenders() throws NoSportEventsException {
        if (sportEvents.isEmpty() || bestSportEventByAttenders == null) {
            throw new NoSportEventsException();
        }
        return bestSportEventByAttenders;
    }

    @Override
    public void addFollower(String playerId, String playerFollowerId) throws PlayerNotFoundException {
        Player player = getPlayer(playerId);
        Player playerFollower = getPlayer(playerFollowerId);
        if (player == null || playerFollower == null) {
            throw new PlayerNotFoundException();
        }
        Vertex<Player> playerVertex = socialNetwork.getVertex(player);
        Vertex<Player> playerFollowerVertex = socialNetwork.getVertex(playerFollower);
        if (playerVertex == null) {
            playerVertex = socialNetwork.newVertex(player);
        }
        if (playerFollowerVertex == null) {
            playerFollowerVertex = socialNetwork.newVertex(playerFollower);
        }
        socialNetwork.newEdge(playerVertex, playerFollowerVertex);
        player.setNumFollowers(player.getNumFollowers() + 1);
        playerFollower.setNumFollowings(playerFollower.getNumFollowings() + 1);
    }

    @Override
    public Iterator<Player> getFollowers(String playerId) throws PlayerNotFoundException, NoFollowersException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        if (player.getNumFollowers() == 0) {
            throw new NoFollowersException();
        }
        Vertex<Player> vPlayer = socialNetwork.getVertex(player);
        Iterator<Edge<String, Player>> edgeIterator = socialNetwork.edgesWithSource(vPlayer);
        List<Player> listFollowers = new LinkedList<>();
        while (edgeIterator.hasNext()) {
            DirectedEdge<String, Player> edge = (DirectedEdge<String, Player>) edgeIterator.next();
            Player pl = edge.getVertexDst().getValue();
            listFollowers.insertEnd(pl);
        }
        return listFollowers.values();
    }

    @Override
    public Iterator<Player> getFollowings(String playerId) throws PlayerNotFoundException, NoFollowingException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        if (player.getNumFollowings() == 0) {
            throw new NoFollowingException();
        }
        Vertex<Player> vPlayer = socialNetwork.getVertex(player);
        Iterator<Edge<String, Player>> edgeIterator = socialNetwork.edgedWithDestA(vPlayer);
        List<Player> listFollowings = new LinkedList<>();
        while (edgeIterator.hasNext()) {
            DirectedEdge<String, Player> edge = (DirectedEdge<String, Player>) edgeIterator.next();
            Player pl = edge.getVertexSrc().getValue();
            listFollowings.insertEnd(pl);
        }
        return listFollowings.values();
    }

    @Override
    public Iterator<Player> recommendations(String playerId) throws PlayerNotFoundException, NoFollowersException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        if (player.getNumFollowers() == 0) {
            throw new NoFollowersException();
        }
        List<Player> recommendations = new LinkedList<>();
        Vertex<Player> playerVertex = socialNetwork.getVertex(player);
        Iterator<Edge<String, Player>> followersEdgeIterator = socialNetwork.edgesWithSource(playerVertex);
        while (followersEdgeIterator.hasNext()) {
            DirectedEdge<String, Player> followerEdge = (DirectedEdge<String, Player>) followersEdgeIterator.next();
            Vertex<Player> followerVertex = followerEdge.getVertexDst();
            Iterator<Edge<String, Player>> recommendationsEdgeIterator = socialNetwork.edgesWithSource(followerVertex);
            while (recommendationsEdgeIterator.hasNext()) {
                DirectedEdge<String, Player> recommendationEdge = (DirectedEdge<String, Player>) recommendationsEdgeIterator.next();
                Vertex<Player> recommendationVertex = recommendationEdge.getVertexDst();
                if (!recommendationVertex.getValue().equals(player) && !isFollowing(recommendationVertex, player, socialNetwork) && !isIn(recommendationVertex.getValue(), recommendations)) {
                    recommendations.insertEnd(recommendationVertex.getValue());
                }
            }
        }
        return recommendations.values();
    }

    private boolean isFollowing(Vertex<Player> recommendationVertex, Player player, DirectedGraph<Player, String> socialNetwork) {
        Iterator<Edge<String, Player>> followingEdgeIterator = socialNetwork.edgedWithDestA(recommendationVertex);
        boolean isFollowing = false;
        while (followingEdgeIterator.hasNext() && !isFollowing) {
            DirectedEdge<String, Player> followingEdge = (DirectedEdge<String, Player>) followingEdgeIterator.next();
            isFollowing = followingEdge.getVertexSrc().getValue().equals(player);
        }
        return isFollowing;
    }

    private boolean isIn(Player recommendation, List<Player> res) {
        Iterator<Player> values = res.values();
        boolean found = false;
        while (values.hasNext() && !found) {
            Player player = values.next();
            if (player.equals(recommendation)) {
                found = true;
            }
        }
        return found;
    }

    @Override
    public Iterator<Post> getPosts(String playerId) throws PlayerNotFoundException, NoPostsException {
        Player player = getPlayer(playerId);
        if (player == null) {
            throw new PlayerNotFoundException();
        }
        List<Post> posts = new LinkedList<>();
        try {
            Iterator<Player> followings = getFollowings(playerId);
            while (followings.hasNext()) {
                Player following = followings.next();
                Iterator<Post> postsFollowing = following.getPosts();
                if (!postsFollowing.hasNext()) {
                    throw new NoPostsException();
                }
                while (postsFollowing.hasNext()) {
                    Post post = postsFollowing.next();
                    posts.insertEnd(post);
                }
            }

        } catch (NoFollowingException e) {
            throw new NoPostsException();
        }
        return posts.values();
    }

    @Override
    public int numPlayers() {
        return players.size();
    }

    @Override
    public int numOrganizingEntities() {
        return organizingEntities.size();
    }

    @Override
    public int numFiles() {
        return totalFiles;
    }

    @Override
    public int numRejectedFiles() {
        return rejectedFiles;
    }

    @Override
    public int numPendingFiles() {
        return files.size();
    }

    @Override
    public int numSportEvents() {
        return sportEvents.size();
    }

    @Override
    public int numSportEventsByPlayer(String playerId) {
        Player player = getPlayer(playerId);
        if (player == null) {
            return 0;
        }
        return player.numEvents();
    }

    @Override
    public int numPlayersBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        if (sportEvent == null) {
            return 0;
        }
        return sportEvent.numPlayers();
    }

    @Override
    public int numSportEventsByOrganizingEntity(String orgId) {
        OrganizingEntity organizingEntity = getOrganizingEntity(orgId);
        if (organizingEntity == null) {
            return 0;
        }
        return organizingEntity.numEvents();
    }

    @Override
    public int numSubstitutesBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        if (sportEvent == null) {
            return 0;
        }
        return sportEvent.getNumSubstitutes();
    }

    @Override
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    @Override
    public SportEvent getSportEvent(String eventId) {
        return sportEvents.get(eventId);
    }

    @Override
    public OrganizingEntity getOrganizingEntity(String id) {
        return organizingEntities.get(id);
    }

    @Override
    public File currentFile() {
        return files.peek();
    }

    @Override
    public int numRoles() {
        return numRoles;
    }

    @Override
    public Role getRole(String roleId) {
        for (Role role: roles) {
            if (role == null) {
                return null;
            } else if (role.getRoleId().equals(roleId)) {
                return role;
            }
        }
        return null;
    }

    @Override
    public int numWorkers() {
        return workers.size();
    }

    @Override
    public Worker getWorker(String dni) {
        return workers.get(dni);
    }

    @Override
    public int numWorkersByRole(String roleId) {
        Role role = getRole(roleId);
        if (role == null) {
            return 0;
        }
        return role.numWorkers();
    }

    @Override
    public int numWorkersBySportEvent(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        if (sportEvent == null) {
            return 0;
        }
        return sportEvent.numWorkers();
    }

    @Override
    public int numRatings(String playerId) {
        Player player = getPlayer(playerId);
        if (player == null) {
            return 0;
        }
        return player.getNumRatings();
    }

    @Override
    public int numAttenders(String sportEventId) {
        SportEvent sportEvent = getSportEvent(sportEventId);
        if (sportEvent == null) {
            return 0;
        }
        return getSportEvent(sportEventId).numAttenders();
    }

    @Override
    public int numFollowers(String playerId) {
        Player player = getPlayer(playerId);
        if (player == null) {
            return 0;
        }
        return player.getNumFollowers();
    }

    @Override
    public int numFollowings(String playerId) {
        Player player = getPlayer(playerId);
        if (player == null) {
            return 0;
        }
        return player.getNumFollowings();
    }
}
