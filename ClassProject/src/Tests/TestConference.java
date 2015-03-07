/**
 * For Tests
 */
package Tests;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import model.BusinessRuleException;
import model.Conference;
import model.Paper;
import model.Recommendation;
import model.Review;
import model.User;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christopher Barrett
 * Tests the conference methods
 */
public class TestConference 
{
	/**
	 * Static Constants for Use
	 */
	private static final int PID = 1;
	private static final int AID = 123;
	private static final String FILE = "howaboutit.txt";
	private static final String ABST = "its a thing";
	private static final String TITLE = "Thing";
	private static final int PCID = 64; //from the user db a Program Chair
	private static final int SPCID = 56;
	private static final int REVID1 = 17;
	private static final int REVID2 = 21;
	private static final int REVID3 = 29;
	
	/**
	 * objects
	 */
	private static Conference con;
	private static Paper paper;
	
	
	/**
	 * Chris Barrett
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception 
	{
		con = new Conference();
		con.startTest();
		paper = new Paper(PID, AID, TITLE, ABST, FILE);
	}
	
	/**
	 * Adds the specified paper to the conference before each test.
	 * 
	 * @author Erik Tedder
	 * @throws BusinessRuleException
	 */
	@Before
	public void beforeEach() throws BusinessRuleException {
		con.login(AID);
		
		con.addPaper(TITLE, ABST, FILE);
		
		con.logout();
	}
	
	/**
	 * Chris Barrett
	 * after each class do the following
	 */
	@After
	public void breakDown()
	{
		con.logout();
		//Erik Tedder - added method to remove all papers/reviews/recommendation and reset
		//paper id counter
		con.endTest(); 
	}
	
	/**
	 * Chris Barrett
	 * after the tests are done do the following
	 */
	@AfterClass
	public static void breakOut()
	{
		con.endTesting();
		con.saveConference();
		
	}
	
	/**
	 * @author Erik Tedder 
	 * Test method for {@link model.Conference#login(int)}.
	 */
	@Test
	public void testLoginFail() {
		con.login(9999999);
		
		assertTrue("Current user should be null", con.getCurrentUser() == null);
	}

	/**
	 * @author Erik Tedder 
	 * Test method for {@link model.Conference#login(int)}.
	 */
	@Test
	public void testLoginSuccessful() {
		con.login(67);
		
		assertTrue("Current user should be null", con.getCurrentUser().getID() == 67);
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getUser(int)}.
	 */
	@Test
	public void testGetUser() 
	{
		User u = con.getUser(PCID);
		
		assertEquals("The get user works if the user collected returns the correct name", "Patricia Diaz", u.toString());
	}

	/**
	 * @author Chris Barrett
	 * @edited Erik Tedder
	 * Test method for {@link model.Conference#addPaper(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws BusinessRuleException 
	 */
	@Test
	public void testAddPaper() throws BusinessRuleException 
	{
		con.login(AID);
		
		con.addPaper(TITLE, ABST, FILE);
		
		//Erik Tedder - revised assertion to check assertTrue instead of assertEqual
		assertTrue("if the papers are equal", con.getPaper(PID).equals(paper));
	}
	
	/**
	 * Method to ensure BusinessRuleException is properly thrown.
	 * 
	 * @author Erik Tedder
	 * Test method for {@link model.Conference#addPaper(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws BusinessRuleException 
	 */
	@Test(expected = BusinessRuleException.class) 
	public void testAddPaperTooManySubmissions() throws BusinessRuleException 
	{
		con.login(AID);
		
		con.addPaper(TITLE, ABST, FILE);
		con.addPaper("title 1", "some abstract", "text1.txt");
		con.addPaper("title 2", "some abstract", "text2.txt");
		con.addPaper("title 3", "some abstract", "text3.txt");
		con.addPaper("title 4", "some abstract", "text4.txt");
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#assignSpc(int, int)}.
	 * @throws BusinessRuleException 
	 */
	@Test
	public void testAssignSpc() throws BusinessRuleException 
	{
		con.login(PCID);
		
		con.assignSpc(SPCID, PID);
		
		assertEquals("if the assignment is correct than equals true", SPCID, con.getSPCforPaper(PID).getID());
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link model.Conference#assignSpc(int, int)}.
	 * @throws BusinessRuleException 
	 */
	@Test(expected = BusinessRuleException.class)
	public void testAssignSpcToOwn() throws BusinessRuleException 
	{
		con.login(SPCID);
		
		con.addPaper("title", "abstract", "txt.txt"); //should be paper id 2
		
		con.logout();
		
		con.login(PCID);
		
		con.assignSpc(SPCID, 2);
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link model.Conference#assignSpc(int, int)}.
	 * @throws BusinessRuleException 
	 */
	@Test(expected = BusinessRuleException.class)
	public void testAssignSpcPastLimit() throws BusinessRuleException 
	{
		//login as random author
		con.login(31);

		con.addPaper("some paper 1", "some abstract", "txt1.txt"); //should be id 2
		con.addPaper("some paper 2", "some abstract", "txt2.txt"); //id 3
		con.addPaper("some paper 3", "some abstract", "txt3.txt"); //id 4
		con.addPaper("some paper 4", "some abstract", "txt4.txt"); //id 5

		con.logout();
		
		con.login(PCID);
		
		con.assignSpc(SPCID, PID);
		con.assignSpc(SPCID, 2);
		con.assignSpc(SPCID, 3);
		con.assignSpc(SPCID, 4);
		con.assignSpc(SPCID, 5);
	}

	/**
	 * @author Chris Barrett
	 * @edited Erik Tedder
	 * Test method for {@link model.Conference#assignReviewerToPaper(int, int)}.
	 * @throws BusinessRuleException 
	 */
	@Test
	public void testAssignReviewerToPaper() throws BusinessRuleException 
	{
		con.login(SPCID);
		
		con.assignReviewerToPaper(REVID1, PID);
		con.assignReviewerToPaper(REVID2, PID);
		con.assignReviewerToPaper(REVID3, PID);
		
		//Erik Tedder - revised from assertEqual to assertTrue, added 2 other reviewer checks
		assertTrue("If the paper exists in the list of papers for that reviewer", 
				paper.equals(con.getReviewerList(REVID1).get(0)));
		assertTrue("If the paper exists in the list of papers for that reviewer", 
				paper.equals(con.getReviewerList(REVID2).get(0)));
		assertTrue("If the paper exists in the list of papers for that reviewer", 
				paper.equals(con.getReviewerList(REVID3).get(0)));
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link model.Conference#assignReviewerToPaper(int, int)}.
	 * @throws BusinessRuleException 
	 */
	@Test(expected = BusinessRuleException.class)
	public void testAssignReviewerToPaperPastLimit() throws BusinessRuleException 
	{
		//login as random author
		con.login(31);
		
		con.addPaper("some paper 1", "some abstract", "txt1.txt"); //should be id 2
		con.addPaper("some paper 2", "some abstract", "txt2.txt"); //id 3
		con.addPaper("some paper 3", "some abstract", "txt3.txt"); //id 4
		con.addPaper("some paper 4", "some abstract", "txt4.txt"); //id 5
		
		con.logout();
		
		con.login(SPCID);		
		
		con.assignReviewerToPaper(REVID1, PID);
		con.assignReviewerToPaper(REVID1, 2);
		con.assignReviewerToPaper(REVID1, 3);
		con.assignReviewerToPaper(REVID1, 4);
		con.assignReviewerToPaper(REVID1, 5);
	}

	/**
	 * Test to ensure exception is thrown is assigning a review to their own paper.
	 * 
	 * @author Erik Tedder
	 * Test method for {@link model.Conference#assignReviewerToPaper(int, int)}.
	 * @throws BusinessRuleException 
	 */
	@Test(expected = BusinessRuleException.class)
	public void testAssignReviewerToPaperTheirOwn() throws BusinessRuleException 
	{
		con.login(REVID1);
		con.addPaper("reviewer 1 paper", "reviewer 1 abstract", "somefile.txt");
		int id = con.getPapersByAuthor(REVID1).get(0).getId();
		
		con.logout();
		
		con.login(SPCID);
		
		con.assignReviewerToPaper(REVID1, id);
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#submitReview(int, model.Review)}.
	 * @throws BusinessRuleException 
	 */
	@Test
	public void testSubmitReview() throws BusinessRuleException 
	{
		con.login(SPCID);
		
		con.assignReviewerToPaper(REVID1, PID);
		con.assignReviewerToPaper(REVID2, PID);
		con.assignReviewerToPaper(REVID3, PID);
		
		con.logout();
		
		con.login(REVID1);
		
		Review rev = new Review();
		rev.setScore(4);
		rev.setComment("sucks");
		
		con.submitReview(PID, rev);
		
		con.logout();
		
		con.login(REVID2);
		
		Review rev1 = new Review();
		rev.setScore(4);
		rev.setComment("sucks");
		
		con.submitReview(PID, rev1);
		
		con.logout();
		
		con.login(REVID3);
		
		Review rev2 = new Review();
		rev.setScore(4);
		rev.setComment("sucks");
		
		con.submitReview(PID, rev2);
		
		assertEquals("I hope that things work correctly sort of cheated", rev.getComment(), con.getReviewsForPaper(PID).get(0).getComment());
		assertEquals("I hope that things work correctly sort of cheated", rev1.getComment(), con.getReviewsForPaper(PID).get(1).getComment());
		assertEquals("I hope that things work correctly sort of cheated", rev2.getComment(), con.getReviewsForPaper(PID).get(2).getComment());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#spcSubmitRecommendation(int, model.Recommendation)}.
	 * @throws BusinessRuleException 
	 */
	@Test
	public void testSpcSubmitRecommendation() throws BusinessRuleException 
	{		
		con.login(PCID);
		
		con.assignSpc(SPCID, PID);
		
		con.logout();
		
		con.login(SPCID);
		
		Recommendation rec = new Recommendation();
		rec.setState(3);
		rec.setRationale("success");
		
		con.spcSubmitRecommendation(PID, rec);
		
		
		assertEquals("should work right?", rec.toString(), con.getRecommendationForPaper(PID).toString());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getAllPapers()}.
	 */
	@Test
	public void testGetAllPapers() 
	{
		assertTrue("", con.getAllPapers().size() > 0);
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getPapersByAuthor(int)}.
	 */
	@Test
	public void testGetPapersByAuthor() 
	{
		assertEquals("one paper should exist from that author", 1,con.getPapersByAuthor(AID).size());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getCurrentUser()}.
	 */
	@Test
	public void testGetCurrentUser() 
	{
		con.login(PCID);
		
		assertEquals("Should be right",PCID, con.getCurrentUser().myID);
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#submitDecision(int, int)}.
	 */
	@Test
	public void testSubmitDecisionAccepted() {
		con.login(PCID);
		
		con.submitDecision(PID, 1);
		
		assertEquals("This is correct", "Accepted", con.getPaperDecision(PID));
	}
	
	/**
	 * Erik Tedder
	 * Test method for {@link model.Conference#submitDecision(int, int)}.
	 */
	@Test
	public void testSubmitDecisionRejected() 
	{
		con.login(PCID);
		
		con.submitDecision(PID, 2);
		
		assertEquals("This is correct", "Rejected", con.getPaperDecision(PID));
	}
	
	/**
	 * Erik Tedder
	 * Test method for {@link model.Conference#submitDecision(int, int)}.
	 */
	@Test
	public void testSubmitDecisionInvalid() 
	{
		con.login(PCID);
		
		con.submitDecision(PID, 9);
		
		assertEquals("This is correct", "Being Reviewed", con.getPaperDecision(PID));
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getUserByRole(int)}.
	 */
	@Test
	public void testGetUserByRole() 
	{
		assertEquals("If 1 PC exist", 1,con.getUserByRole(1).size() );
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getPapersBySpc(int)}.
	 * @throws BusinessRuleException 
	 */
	@Test
	public void testGetPapersBySpc() throws BusinessRuleException
	{		
		con.assignSpc(SPCID, PID);
		
		assertEquals("If 1 paper exists for this SPC", 1, con.getPapersBySpc(SPCID).size());
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#removePaper(model.Paper)}.
	 */
	@Test
	public void testRemovePaper() 
	{
		con.login(AID);
		
		con.removePaper(paper);
		
		assertNull("I am not sure if it is null or DNE", con.getPaper(PID));
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#getDaysLeft()}.
	 */
	@Test
	public void testGetDaysLeft()
	{
		long today = new GregorianCalendar().getTimeInMillis();
		int daysLeft = (int) (con.getDeadline().getTimeInMillis() - today) / (1000 * 60 * 60 * 24);
		
		assertEquals("If they equal then this should work", daysLeft,con.getDaysLeft());
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Conference#changeUserRole(int, int)}.
	 */
	@Test
	public void testChangeUserRole() 
	{
		con.changeUserRole(AID, 4);
		assertEquals("If the change occured than this should be correct", 4, con.getUser(AID).getRole());
	}
}
