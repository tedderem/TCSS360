package model;

/**
 * An enumerator class to aid in the implementation of the observer/observable for the 
 * Conference project.
 * 
 * @author Erik Tedder
 * @date 5/25/2014
 */
public enum ConfChangeType {
	/** Enum type for a user logging in successfully. */
	LOGIN_SUCCESSFUL,
	
	/** Enum type for a fail user login. */
	LOGIN_FAIL,
	
	/** Enum type for a user logging out. */
	LOGOUT,
	
	/** Enum type for paper added to conference. */
	PAPER_ADDED,
	
	/** Enum type for paper removed from conference. */
	PAPER_REMOVED,
	
	/** Enum type for a paper being reviewed. */
	REVIEW_ADDED,
	
	/** Enum type for a reviewer being assigned to a paper. */
	REVIEWER_ASSIGNED,
	
	/** Enum type for a subprogram chair being assigned to a paper. */
	SPC_ASSIGNED,
	
	/** Enum type for when a decision is made on a paper. */
	DECISION_MADE
	
	//Other change types that may be necessary later on.

}
