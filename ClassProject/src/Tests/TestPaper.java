/**
 * From Tests
 */
package Tests;

import static org.junit.Assert.*;
import model.Paper;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Christopher Barrett
 * Tests the paper methods
 */
public class TestPaper 
{
	/**
	 * Constants
	 */
	private static final int PID = 21;
	private static final int AID = 4;
	private static final String FILE = "Charlie";
	private static final String ABST = "Horner";
	private static final String TITLE = "CH@blah.edu";
	
	/**
	 * object
	 */
	private Paper paper;
	
	/**
	 * Chris Barrett
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		paper = new Paper(PID, AID, TITLE, ABST, FILE);
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Paper#getId()}.
	 */
	@Test
	public void testGetId() 
	{
		assertEquals("The get paper id works", PID, paper.getId());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Paper#getAuthorID()}.
	 */
	@Test
	public void testGetAuthorID() 
	{
		assertEquals("The get author id works", AID, paper.getAuthorID());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Paper#getTitle()}.
	 */
	@Test
	public void testGetTitle() 
	{
		assertEquals("The get title works", TITLE, paper.getTitle());
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Paper#getAbstract()}.
	 */
	@Test
	public void testGetAbstract() 
	{
		assertEquals("The get abstract works", ABST, paper.getAbstract());
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link mode.Paper#equals(other)}
	 */
	@Test
	public void testEqualsTrue() {
		Paper p = new Paper(PID, AID, TITLE, ABST, FILE);
		
		assertTrue(p.equals(paper));		
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link mode.Paper#equals(other)}
	 */
	@Test
	public void testEqualsItself() {
		assertTrue(paper.equals(paper));		
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link mode.Paper#equals(other)}
	 */
	@Test
	public void testDoesNotEqualNull() {
		assertFalse(paper.equals(null));		
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link mode.Paper#equals(other)}
	 */
	@Test
	public void testDoesNotEqual() {
		Paper p = new Paper(41, AID, TITLE, ABST, FILE);		
		
		assertFalse(paper.equals(p));	
		
		p = new Paper(PID, 0, TITLE, ABST, FILE);		
		
		assertFalse(paper.equals(p));
		
		p = new Paper(PID, AID, "asifj", ABST, FILE);		

		assertFalse(paper.equals(p));
		
		p = new Paper(PID, AID, TITLE, "asfasdf", FILE);		
		
		assertFalse(paper.equals(p));
		
		p = new Paper(PID, AID, TITLE, ABST, "asdfasdf");		
		
		assertFalse(paper.equals(p));
	}
	
	/**
	 * @author Erik Tedder
	 * Test method for {@link mode.Paper#equals(other)}
	 */
	@Test
	public void testDoesNotEqualOtherObject() {
		assertFalse(paper.equals("String"));		
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Paper#getFile()}.
	 */
	@Test
	public void testGetFile() 
	{
		assertEquals("The get file works", FILE, paper.getFile());
	}

}
