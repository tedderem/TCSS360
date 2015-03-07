/**
 * From Tests
 */
package Tests;

import static org.junit.Assert.*;
import model.Review;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Christopher Barrett
 * Tests the Review methods
 */
public class TestReview 
{
	/**
	 * Constants
	 */
	private static final int REVID = 21;
	private static final int SCORE = 4;
	private static final String COM = "comments";
	
	/**
	 * Objects
	 */
	private Review rev;
	private Review rev2;
	
	/**
	 * Chris Barrett
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		rev = new Review();
		rev2 = new Review(REVID, SCORE, COM);
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Review#getReviewerID()}.
	 */
	@Test
	public void testGetReviewerID() 
	{
		assertEquals("The get id works", REVID, rev2.getReviewerID());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Review#setScore(int)}.
	 */
	@Test
	public void testSetScore() 
	{
		rev.setScore(SCORE);
		
		assertEquals("The set/get score works", SCORE, rev.getScore());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Review#setComment(java.lang.String)}.
	 */
	@Test
	public void testSetComment() 
	{
		rev.setComment(COM);
		
		assertEquals("The set/get Comment works", COM, rev.getComment());
	}
}
