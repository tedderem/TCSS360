/**
 * Tests
 */
package Tests;

import static org.junit.Assert.*;
import model.Recommendation;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Christopher Barrett
 * Tests the Recommendation methods
 */
public class TestRec 
{
	/**
	 * Constants
	 */
	private static final int STATE = 2;
	private static final String COM = "sucks";
	
	/**
	 * Object
	 */
	private static Recommendation rec;
	
	/**
	 * Chris Barrett
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception 
	{
		rec = new Recommendation();
	}

	/**
	 * Chris Barrett
	 * Test method for {@link model.Recommendation#setState(int)}.
	 */
	@Test
	public void testSetState() 
	{
		rec.setState(STATE);
		
		assertEquals("The set/get state works", STATE, rec.getState());
	}

	/**
	 * Tests to ensure recommendation state does not get set when passed a value outside the
	 * appropriate state range.
	 * 
	 * Erik Tedder
	 * Test method for {@link model.Recommendation#setState(int)}.
	 */
	@Test
	public void testSetStateBelowThreshold() {
		rec.setState(-1);
		
		assertNotEquals("State should not set since out of the threshold", -1, rec.getState());
	}
	
	/**
	 * Tests to ensure recommendation state does not get set when passed a value outside the
	 * appropriate state range.
	 * 
	 * Erik Tedder
	 * Test method for {@link model.Recommendation#setState(int)}.
	 */
	@Test
	public void testSetStateAboveThreshold() {
		rec.setState(6);
		
		assertNotEquals("State should not set since out of the threshold", 6, rec.getState());
	}
	
	/**
	 * Chris Barrett
	 * Test method for {@link model.Recommendation#setRationale(java.lang.String)}.
	 */
	@Test
	public void testSetRationale() 
	{
		rec.setRationale(COM);
		
		assertEquals("The set/get rational works", COM, rec.getRationale());
	}
	

	/**
	 * Chris Barrett
	 * Test method for {@link model.Recommendation#toString()}.
	 */
	@Test
	public void testToString() 
	{
		assertEquals("The toString() works", "0,sucks", rec.toString());
	}

}
