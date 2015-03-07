package model;

/**
 * @author Steven Bradley
 *
 */
public class User {
	/**
	 * The unigue ID for this user.
	 */
	public int myID;
	/**
	 * The role of this user.
	 * PC = 1
	 * SPC = 2
	 * Reviwer = 4
	 */
	private int myRole;
	/**
	 * The first name of the user.
	 */
	private String myFirstName;
	/**
	 * The last name of the user.
	 */
	private String myLastName;
	/**
	 * The users email.
	 */
	private String myEmail;

	/**
	 * The papers that the user has authored and submitted to the conference.
	 */
	
	public int spc_pc;
	
	/** Creates a new user with the following fields.
	 * @param id 
	 * @param role
	 * @param first
	 * @param last
	 * @param email
	 */
	public User(int id, int role, String first, String last, String email) {
		myID = id;	
		myFirstName = first;
		myLastName = last;
		myEmail = email;
		myRole = role;
		
		
	}
	
	public int getID() {
		return myID;
	}
	
	public int getRole() {
		return myRole;
	}
	
	public String getFirstName() {
		return myFirstName;
	}
	
	public String getLastName() {
		return myLastName;
	}
	
	public String getEmail() {
		return myEmail;
	}
	
	public void setRole(int theRole) {
		myRole = theRole;
	}
	
	@Override
	public String toString() {
		return myFirstName + " " + myLastName;
	}
	

}
