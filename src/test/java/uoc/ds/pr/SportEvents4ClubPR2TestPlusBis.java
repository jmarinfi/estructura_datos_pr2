package uoc.ds.pr;

import edu.uoc.ds.traversal.Iterator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uoc.ds.pr.exceptions.*;
import uoc.ds.pr.model.OrganizingEntity;
import uoc.ds.pr.model.Player;
import uoc.ds.pr.model.Post;
import uoc.ds.pr.model.SportEvent;

import static uoc.ds.pr.util.DateUtils.createLocalDate;

import java.time.LocalDate;

public class SportEvents4ClubPR2TestPlusBis extends SportEvents4ClubPR2TestPlus {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    public void initialState() {
        super.initialState();
    }

    @Test
    public void updateFileTestBis() throws DSException {

        initialState();

        Assert.assertEquals(1, this.sportEvents4Club.numPendingFiles());

        sportEvents4Club.updateFile(SportEvents4Club.Status.DISABLED, LocalDate.now(), "Rejected");

        Assert.assertEquals(0, this.sportEvents4Club.numPendingFiles());
        Assert.assertThrows(NoFilesException.class, () -> sportEvents4Club.updateFile(SportEvents4Club.Status.ENABLED, LocalDate.now(), "Approved"));
    }

    @Test
    public void signUpEventTestBis() throws DSException {

        initialState();

        Assert.assertEquals(3, this.sportEvents4Club.numPlayersBySportEvent("EV-1103"));
        Assert.assertThrows(PlayerNotFoundException.class, () -> this.sportEvents4Club.signUpEvent("FakePlayer", "EV-1103"));
        Assert.assertThrows(SportEventNotFoundException.class, () -> this.sportEvents4Club.signUpEvent("idPlayer11", "FakeEvent"));

        this.sportEvents4Club.addFile("F-999", "EV-9999", "ORG-8", "Test", SportEvents4Club.Type.MICRO, (byte) 1, 3, createLocalDate("20-11-2022"), createLocalDate("21-11-2022"));

        Assert.assertEquals(2, this.sportEvents4Club.numPendingFiles());
        Assert.assertEquals("F-999", this.sportEvents4Club.currentFile().getFileId());

        this.sportEvents4Club.updateFile(SportEvents4Club.Status.ENABLED, LocalDate.now(), "Approved");
        SportEvent sportEvent = this.sportEvents4Club.getSportEvent("EV-9999");

        Assert.assertEquals("EV-9999", sportEvent.getEventId());

        this.sportEvents4Club.signUpEvent("idPlayer4", sportEvent.getEventId());
        this.sportEvents4Club.signUpEvent("idPlayer11", sportEvent.getEventId());
        this.sportEvents4Club.signUpEvent("idPlayer14", sportEvent.getEventId());

        Assert.assertThrows(LimitExceededException.class, () -> this.sportEvents4Club.signUpEvent("idPlayer2", sportEvent.getEventId()));
        Assert.assertEquals(4, this.sportEvents4Club.numPlayersBySportEvent(sportEvent.getEventId()));
        Assert.assertEquals(1, this.sportEvents4Club.numSubstitutesBySportEvent(sportEvent.getEventId()));
    }

    @Test
    public void getSportEventsByPlayerTestBis() throws DSException {

        initialState();

        Assert.assertThrows(NoSportEventsException.class, () -> this.sportEvents4Club.getEventsByPlayer("FakePlayer"));
        Assert.assertEquals("idPlayer11", this.sportEvents4Club.getPlayer("idPlayer11").getId());
        Assert.assertThrows(NoSportEventsException.class, () -> this.sportEvents4Club.getEventsByPlayer("idPlayer11"));
    }

    @Test
    public void addRatingTestBis() throws DSException {

        initialState();

        SportEvent sportEvent = this.sportEvents4Club.getSportEvent("EV-1101");
        Assert.assertEquals("EV-1101", sportEvent.getEventId());

        Player player = this.sportEvents4Club.getPlayer("idPlayer11");
        Assert.assertEquals("idPlayer11", player.getId());

        Assert.assertThrows(SportEventNotFoundException.class, () -> this.sportEvents4Club.addRating("idPlayer11", "FakeEvent", SportEvents4Club.Rating.FIVE, "Great"));
        Assert.assertThrows(PlayerNotFoundException.class, () -> this.sportEvents4Club.addRating("FakePlayer", "EV-1101", SportEvents4Club.Rating.FIVE, "Great"));
        Assert.assertThrows(PlayerNotInSportEventException.class, () -> this.sportEvents4Club.addRating("idPlayer11", "EV-1101", SportEvents4Club.Rating.FIVE, "Great"));
    }

    @Test
    public void best5OrganizingEntitiesTestBis() throws DSException {

        initialState();

        Iterator< OrganizingEntity> entities = this.sportEvents4Club.best5OrganizingEntities();
        OrganizingEntity org1 = entities.next();
        OrganizingEntity org2 = entities.next();
        OrganizingEntity org3 = entities.next();

        Assert.assertFalse(entities.hasNext());
        Assert.assertTrue(org1.numAttenders() > org2.numAttenders());
        Assert.assertTrue(org1.numAttenders() > org3.numAttenders());
        Assert.assertTrue(org2.numAttenders() > org3.numAttenders());
    }

    @Test
    public void bestSportEventByAttendersTestBis() throws DSException {

        initialState();

        SportEvent sportEvent1 = this.sportEvents4Club.bestSportEventByAttenders();
        Assert.assertEquals("EV-1101", sportEvent1.getEventId());
        Assert.assertEquals(13, sportEvent1.numAttenders());

        SportEvent sportEvent2 = this.sportEvents4Club.getSportEvent("EV-1103");
        Assert.assertEquals(6, sportEvent2.numAttenders());

        this.sportEvents4Club.addAttender("Phone7", "Attender7", "EV-1103");
        this.sportEvents4Club.addAttender("Phone8", "Attender8", "EV-1103");
        this.sportEvents4Club.addAttender("Phone9", "Attender9", "EV-1103");
        this.sportEvents4Club.addAttender("Phone10", "Attender10", "EV-1103");
        this.sportEvents4Club.addAttender("Phone11", "Attender11", "EV-1103");
        this.sportEvents4Club.addAttender("Phone12", "Attender12", "EV-1103");
        this.sportEvents4Club.addAttender("Phone13", "Attender13", "EV-1103");
        this.sportEvents4Club.addAttender("Phone14", "Attender14", "EV-1103");

        Assert.assertSame(sportEvent2, this.sportEvents4Club.bestSportEventByAttenders());
        Assert.assertEquals(14, sportEvent2.numAttenders());
    }

    @Test
    public void followersFollowingsTestBis() throws DSException {

        initialState();

        Player player2 = this.sportEvents4Club.getPlayer("idPlayer2");
        Player player4 = this.sportEvents4Club.getPlayer("idPlayer4");
        this.sportEvents4Club.addFollower("idPlayer4", "idPlayer2");
        Assert.assertEquals(1, player4.getNumFollowers());
        Assert.assertEquals(3, player2.getNumFollowings());

        Iterator<Player> followersPlayer4 = this.sportEvents4Club.getFollowers("idPlayer4");
        Iterator<Player> followingsPlayer2 = this.sportEvents4Club.getFollowings("idPlayer2");

        boolean found = false;
        while (followersPlayer4.hasNext() && !found) {
            Player follower = followersPlayer4.next();
            if (follower.equals(player2)) {
                found = true;
            }
        }
        Assert.assertTrue(found);

        found = false;
        while (followingsPlayer2.hasNext() && !found) {
            Player following = followingsPlayer2.next();
            if (following.equals(player4)) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void recommendationTestBis() throws DSException {
        initialState();

        Player player2 = this.sportEvents4Club.getPlayer("idPlayer2");
        Iterator<Player> recommendationsPlayer5 = this.sportEvents4Club.recommendations("idPlayer5");

        boolean found = false;

        while (recommendationsPlayer5.hasNext() && !found) {
            Player recommendation = recommendationsPlayer5.next();
            if (recommendation.equals(player2)) {
                found = true;
            }
        }
        Assert.assertFalse(found);

        this.sportEvents4Club.addFollower("idPlayer5", "idPlayer1");
        recommendationsPlayer5 = this.sportEvents4Club.recommendations("idPlayer5");

        while (recommendationsPlayer5.hasNext() && !found) {
            Player recommendation = recommendationsPlayer5.next();
            if (recommendation.equals(player2)) {
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void getPostsTestBis() throws DSException {

        initialState();

        Iterator<Post> postsPlayer10 = this.sportEvents4Club.getPosts("idPlayer10");
        Assert.assertEquals("{'player': 'idPlayer5', 'sportEvent': 'EV-1101', 'action': 'signup'}", postsPlayer10.next().message());
        Assert.assertFalse(postsPlayer10.hasNext());

        this.sportEvents4Club.signUpEvent("idPlayer5", "EV-1103");
        this.sportEvents4Club.addRating("idPlayer5", "EV-1101", SportEvents4Club.Rating.FIVE, "Great");

        postsPlayer10 = this.sportEvents4Club.getPosts("idPlayer10");
        Assert.assertEquals("{'player': 'idPlayer5', 'sportEvent': 'EV-1101', 'action': 'signup'}", postsPlayer10.next().message());
        Assert.assertEquals("{'player': 'idPlayer5', 'sportEvent': 'EV-1103', 'action': 'signup'}", postsPlayer10.next().message());
        Assert.assertEquals("{'player': 'idPlayer5', 'sportEvent': 'EV-1101', 'rating': 'FIVE', 'action': 'rating'}", postsPlayer10.next().message());
        Assert.assertFalse(postsPlayer10.hasNext());
    }
}
