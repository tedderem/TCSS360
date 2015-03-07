/**
 * 
 */
package model;

/**
 * An exception for the case of a Business Rule being violated for the TCSS 360 team project.
 * 
 * @author Erik Tedder
 * @date 6/5/2014
 */
@SuppressWarnings("serial")
public class BusinessRuleException extends Exception {

	/**
	 * Constructor of a new BusinessRuleException with no message provided.
	 */
	public BusinessRuleException() {
		super();
	}

	/**
	 * Constructor of a new BusinessRuleException with a given message parameter.
	 * 
	 * @param theMessage The message accompanying this exception being thrown.
	 */
	public BusinessRuleException(final String theMessage) {
		super(theMessage);
	}
}
