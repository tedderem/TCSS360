package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;

/**
 * Class which acts as a database assessor object of the peer-review process for
 * academic conferences.
 * 
 * Class was initially set up to accommodate text-file parsing, however the
 * writing out of said text-files became troublesome and overly complicated. To
 * fix this issue and keep with the business rules and user stories of project,
 * it was necessary to adapt the class to work with a SQLite database. Erik
 * Tedder edited the methods to work directly with accessing the database,
 * making it necessary to deprecate the previously used code. Anton Bardakhanov
 * made initial class implementation with official 12 user stories requirements
 * with hash_map, later on it was modified for database. All deprecated code
 * marked as //DEPRECATED.
 * 
 * @author Anton Bardakhanov, Steven Bradley, Erik Tedder (altered for database)
 */
public class Conference extends Observable {

	/** A fake, hard-coded deadline for when the conference is to be held. */
	public static final GregorianCalendar DEADLINE = new GregorianCalendar(
			2014, 5, 21);

	/** The currently logged in user of this conference. */
	private User currentUser;
	/** The connection to the database for accessing conference data. */
	private Connection c;

	//DEPRECATED class fields
	//private Map<Integer, User> my_users;
	//private Map<Integer, Paper> my_papers;
	// we going to parse paperId from text file;
	//int lastPaperId = 0;
	//int lastReviewId = 0;

	/**
	 * Constructor of a new Conference class. Initializes a new connection to the necessary 
	 * database containing the Conference's data. 
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 */
	public Conference() {	
		currentUser = null;
		//Erik Tedder
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/nunya.sqlite");
			c.setAutoCommit(true);
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		} 
		
		//my_users = new HashMap<Integer, User>();
		//my_papers = new HashMap<Integer, Paper>();
	}
	
	/**
	 * Simple method that should be called before any testing is done on this particular class.
	 * 
	 * @author Erik Tedder
	 */
	public void startTest() {
		try {
			c.setAutoCommit(false);
			c.createStatement().executeUpdate("DELETE FROM paper");
			c.createStatement().executeUpdate("DELETE FROM recommendation");
			c.createStatement().executeUpdate("DELETE FROM review");
			c.createStatement().executeUpdate("DELETE FROM sqlite_sequence");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that should be called when testing has been completed for this class. Rolls the
	 * database back to its original contents.
	 * 
	 * @author Erik Tedder
	 */
	public void endTesting() {
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("rollback");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that should be called when testing has been completed for this class. Rolls the
	 * database back to its original contents.
	 * 
	 * @author Erik Tedder
	 */
	public void endTest() {
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("DELETE FROM paper");
			stmt.executeUpdate("DELETE FROM recommendation");
			stmt.executeUpdate("DELETE FROM review");
			stmt.executeUpdate("DELETE FROM sqlite_sequence");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method which returns a user object which is associated with the given user id.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param theID The id of the user to be accessed from the database.
	 * @return The user associated with the passed user id.
	 */
	public User getUser(final int theID) {
		User u = null;
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE userID = '" + theID + "'");
			
			u = new User(rs.getInt("userID"), rs.getInt("roleID"), 
						rs.getString("firstName"), rs.getString("lastName"), 
						rs.getString("email"));
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return u;
	}
	
	/**
	 * Method which returns a paper based on the given paper id.
	 * 
	 * @author Steven Bradley, Erik Tedder (altered for database)
	 * @param theID The id of the paper.
	 * @return The paper wanting to be accessed.
	 */
	public Paper getPaper(final int theID) {
		Paper p = null;
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM paper WHERE paperID = '" + theID + "'");
			
			if (rs.next())
				p = new Paper(rs.getInt("paperID"), rs.getInt("userID"), rs.getString("title"),
					rs.getString("abstract"), rs.getString("fileName"));
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return p;
	}

	//DEPRECATED
//	public void addUser(User the_user) {
//		my_users.put(the_user.myID, the_user);
//		// myUserList.add(user);
//
//	}

	// US01. As an Author, I want to submit a manuscript to a conference.
	/**
	 * Method which submits and adds a paper to a Conference.
	 * 
	 * @author Steven Bradley, Erik Tedder (altered for database)
	 * @param title The title of the paper.
	 * @param theAbstract The abstract for the paper.
	 * @param fileName The file name for the paper.
	 * @throws BusinessRuleException exception when the added paper violates a business rule.
	 */
	public void addPaper(final String title, final String theAbstract, final String fileName) 
			throws BusinessRuleException {
			
		if (getPapersByAuthor(currentUser.getID()).size() < 4) {
			try {
				Statement stmt = c.createStatement();
				stmt.executeUpdate("INSERT INTO paper (userID, title, abstract, fileName) VALUES ('" + currentUser.getID() + "', '" 
						+ title + "', '" + theAbstract + "', '" + fileName + "')");

				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} 	
		} else {
			throw new BusinessRuleException("You may only have 4 papers submitted to a conference.");
		}

		setChanged();
		notifyObservers(ConfChangeType.PAPER_ADDED);
		
		//DEPRECATED
//		my_currentUser.submitPaper(my_papers.size() + 1);
//		my_papers.put(my_papers.size() + 1 , new Paper(my_papers.size() + 1, my_currentUser.getID(), title, Abstract, filename));
	}
	
	//DEPRECATED
//	// US01. As an Author, I want to submit a manuscript to a conference.
//	public void addPaper(Paper that) throws BusinessRuleException {
//		User cur = my_users.get(that.getAuthorID());
//		cur.submitPaper(that.getId());
//		my_papers.put(that.getId(), that);
//	}
	
	/**
	 * Method which updates the database to delete a paper and any parallel reviews or 
	 * recommendations.
	 * 
	 * @author Steven Bradley, Erik Tedder (altered for database) 
	 * @param thePaper the paper to be deleted.
	 */
	public void removePaper(final Paper thePaper) {
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("DELETE FROM paper WHERE paperID = '" + thePaper.getId() + "'");
			stmt.executeUpdate("DELETE FROM review WHERE paperID = '" + thePaper.getId() + "'");
			stmt.executeUpdate("DELETE FROM recommendation WHERE paperID = '" + thePaper.getId() + "'");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 		
		
		setChanged();
		notifyObservers(ConfChangeType.PAPER_REMOVED);
	}

	// US02. As a Program Chair I want to designate a Subprogram Chair for a
	// manuscript.
	/**
	 * A method which assigns a subprogram chair user to a given paper id.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param spcKey The user ID of the spc.
	 * @param paperID The paper id that needs a SPC assignment.
	 * @throws BusinessRuleException Business rule has been violated.
	 */
	public void assignSpc(final int spcKey, final int paperID) throws BusinessRuleException {
		if (getPapersBySpc(spcKey).size() < 4) { //ensure less than 4 papers assigned to spc
			try {
				if (getPaper(paperID).getAuthorID() != spcKey) { // spc != author
					Statement stmt = c.createStatement();			

					stmt.executeUpdate("INSERT INTO recommendation (paperId, userID) VALUES "
							+ "('" + paperID + "', '" + spcKey + "')");

					stmt.close();
				} else {
					throw new BusinessRuleException("A user cannot be the subproram chair for their own paper.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} 		

			setChanged();
			notifyObservers(ConfChangeType.SPC_ASSIGNED);
		} else {
			throw new BusinessRuleException("A subprogram chair may only have 4 papers assigned to them.");
		}
	}


	// US03. As a Subprogram Chair, I want to assign a paper to reviewers.
	/**
	 * Method which assigns a reviewer to a paper. 
	 * 
	 * @author Erik Tedder
	 * @param reviewerKey The user ID of the reviewer.
	 * @param paperID The paper ID of the paper. 
	 * @throws BusinessRuleException A business rule has been violated.
	 */
	public void assignReviewerToPaper(final int reviewerKey, final int paperID) 
			throws BusinessRuleException {
		if (getReviewerList(reviewerKey).size() < 4) {
			try {
				if (getPaper(paperID).getAuthorID() != reviewerKey) {
					Statement stmt = c.createStatement();
					stmt.executeUpdate("INSERT INTO review (paperId, userID) VALUES "
							+ "('" + paperID + "', '" + reviewerKey + "')");

					stmt.close();			

					setChanged();
					notifyObservers(ConfChangeType.REVIEWER_ASSIGNED);
				} else {
					throw new BusinessRuleException("Cannot assign a reviewer to his/her own paper.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} 		
		} else {
			throw new BusinessRuleException("Reviewer cannot have more than 4 papers assigned to them");
		}
	}

	// US04. As a Reviewer, I want to view a list of manuscripts to which I have
	// been assigned.
	/**
	 * Method which returns a list of papers that are assigned to a specified reviewer's user
	 * id.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param reviewerKey The user id of the reviewer.
	 * @return A list of papers that are associated with the given reviewer's user id.
	 */
	public List<Paper> getReviewerList(final int reviewerKey) {
		List<Paper> list = new ArrayList<Paper>();
		
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM review WHERE userID = '" + reviewerKey +"'");
			
			while (rs.next()) {
				list.add(getPaper(rs.getInt("paperID")));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 		
		
		return list;
	}

	/**
	 * Method to submit a new review to a paper.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param paperID The paper id of the paper.
	 * @param review The review to be added to the paper.
	 */
	public void submitReview(final int paperID, final Review review) {		
		try {
			Statement stmt = c.createStatement();
			
			stmt.executeUpdate("UPDATE review SET score = '" + review.getScore() 
					+ "', comment = '" + review.getComment() + "' WHERE userID = '" + currentUser.getID() 
					+ "' AND paperID = '" + paperID + "'");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		
		setChanged();
		notifyObservers(ConfChangeType.REVIEW_ADDED);
		
		//DEPRECATED
		//my_papers.get(paperId).submitReviewToPaper(review);
	}

	// US08. As a Subprogram Chair, I want to submit my recommendation for a
	// paper.
	/**
	 * Method which adds a subprogram chair's recommendation to a paper.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param paperID The id of the paper.
	 * @param r The recommendation to be added to the paper.
	 */
	public void spcSubmitRecommendation(final int paperID, final Recommendation r) {
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("UPDATE recommendation SET score = '" + r.getState() 
					+ "', comment = '" + r.getRationale() + "' WHERE userID = '" + currentUser.getID() 
					+ "' AND paperID = '" + paperID + "'");
			
			stmt.close();
			
			setChanged();
			notifyObservers(ConfChangeType.REVIEW_ADDED);
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		
//		DEPRECATED
//		if (my_papers.containsKey(paperID)) {
//			my_papers.get(paperID).setRecommendation(r);
//
//			setChanged();
//			notifyObservers(ConfChangeType.REVIEW_ADDED);
//		} else {
//			throw new NullPointerException("No such paper");
//		}
		
	}

	// US09. As a Program Chair, I want to view a list of all submitted
	// manuscripts and the acceptance status
	// US11. As a Program Chair, I want to see which papers are assigned to
	// which Subprogram chairs.
	// Please see US02
	/**
	 * Method which returns a list of papers that have been submitted to this Conference.
	 * 
	 * @author Anton Bardakhanov, Steven Bradley, Erik Tedder (altered for database)
	 * @return the list of all papers in the conference.
	 */
	public ArrayList<Paper> getAllPapers() {
		ArrayList<Paper> list = new ArrayList<Paper>();
		
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM paper");
			
			while (rs.next()) {
				list.add(new Paper(rs.getInt("paperID"), rs.getInt("userID"), rs.getString("title"),
						rs.getString("abstract"), rs.getString("fileName")));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		
		return list;		
		
		// DEPRECATED
		//return collection of papers, status can be obtained by getStatus()
		//return new ArrayList<Paper>(my_papers.values());
	}

	/**
	 * Returns the current logged in user. May not be necessary for later
	 * iterations of program, but considering using now for the sake of testing
	 * ID levels, etc.
	 * 
	 * @author Erik Tedder 
	 * @return myCurrentUser.
	 */
	public User getCurrentUser() {
		return currentUser;
	}

	/**
	 * Method which logs out the current user of the program.
	 * 
	 * @author Erik Tedder
	 */
	public void logout() {
		currentUser = null;

		setChanged();
		notifyObservers(ConfChangeType.LOGOUT);
	}

	// US10. As a Program Chair, I want to make an acceptance decision (yes or
	// no) on a submitted manuscript.
	/**
	 * A method which submits a decision to a given paper.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param paperID The id of the paper.
	 * @param decision The decision to be added to the paper (1 = accept, 2 = reject).
	 */
	public void submitDecision(final int paperID, final int decision) {		
		//Tests to ensure decision is of the right value.
		if (decision != 1 && decision != 2) {
			return;
		}
		
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("UPDATE paper SET decision = " + decision 
					+ " WHERE paperID = '" + paperID +"'");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
	
		setChanged();
		notifyObservers(ConfChangeType.DECISION_MADE);
		
		//DEPRECATED
		//my_papers.get(paperID).setDecision(decision);
	}
	
	/**
	 * Method which changes a user's role.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param theUserID The id of the user needing a role change.
	 * @param theRole The user's new role.
	 */
	public void changeUserRole(final int theUserID, final int theRole) {
		try {
			Statement stmt = c.createStatement();
			stmt.executeUpdate("UPDATE user SET roleID = " + theRole 
					+ " WHERE userID = '" + theUserID + "'");
			
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		
		//DEPRECATED
		//my_users.get(theUserID).setRole(theRole);		
	}

	/**
	 * Method which logs in a new user for the conference.
	 * 
	 * @author Erik Tedder
	 * @param theUser The user to be logged in.
	 */
	// US12. As a user, I want to log in.
	public void login(final int theUserID) {
		boolean userFound = false;

		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE userID = '" + theUserID + "'");
			
			if (rs.next()) {
				userFound = true;
				currentUser = new User(rs.getInt("userID"), rs.getInt("roleID"), 
						rs.getString("firstName"), rs.getString("lastName"), 
						rs.getString("email"));
				
				setChanged();
				notifyObservers(ConfChangeType.LOGIN_SUCCESSFUL);
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		
		
		if (!userFound) {
			setChanged();
			notifyObservers(ConfChangeType.LOGIN_FAIL);
		}
		
		//DEPRECATED (originally coded by Erik Tedder)
//		for (User u : myUserList) {
//			for (User u : my_users.values()) {
//				if (theUserID == u.getID()) {
//					userFound = true;
//					currentUser = u;
//
//					setChanged();
//					notifyObservers(ConfChangeType.LOGIN_SUCCESSFUL);
//				}
//			}
	}

	/**
	 * Method which returns a list of users of a given role.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param roleID The role to check users by.
	 * @return A list of users matching the roleID parameter.
	 */
	public List<User> getUserByRole(final int roleID){
		List<User> users = new ArrayList<User>();

		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE roleID = '" + roleID + "'");
			
			while (rs.next()) {
				users.add(new User(rs.getInt("userID"), rs.getInt("roleID"), 
						rs.getString("firstName"), rs.getString("lastName"), 
						rs.getString("email")));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		
		
		return users;
		
		//DEPRECATED
//		for(User u: my_users.values()){
//			if(u.getRole() == roleId)
//				users.add(u);
//		}
	}
	
	/**
	 * Method which gets and returns all the papers of a given user.
	 * 
	 * @author Steven Bradley, Erik Tedder (altered for database)
	 * @param authorID The user id of the author.
	 * @return the list of papers by the given author.
	 */
	public List<Paper> getPapersByAuthor(final int authorID){
		List<Paper> papers = new ArrayList<Paper>();

		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM paper WHERE userID = '" + authorID + "'");
			
			while (rs.next()) {
				papers.add(new Paper(rs.getInt("paperID"), rs.getInt("userID"), rs.getString("title"),
						rs.getString("abstract"), rs.getString("fileName")));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 	
		
		return papers;
		
		//DEPRECATED
//		for(Paper p: my_papers.values()){
//			if(p.getAuthorID() == authorId)
//				papers.add(p);
//		}
	}
	
	/**
	 * A method which returns a recommendation for a given paper.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param paperID The paper id to get the current recommendation for.
	 * @return The recommendation associated with this paper.
	 */
	public Recommendation getRecommendationForPaper(final int paperID) {
		Recommendation r = null;
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM recommendation WHERE paperID = '" + paperID + "'");
			
			if (rs.next()) {
				r = new Recommendation();
				r.setState(rs.getInt("score"));
				r.setRationale(rs.getString("comment"));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return r;
		
	}
	
	/**
	 * A method which returns a String representation of the current decision of a given paper.
	 * 
	 * @author Erik Tedder
	 * @param paperID The paper's id in which to receive the decision for.
	 * @return The String representation of a paper's decision.
	 */
	public String getPaperDecision(final int paperID) {
		String decisionStr = "Being Reviewed";
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM paper WHERE paperID = '" + paperID + "'");
			
			if (rs.next()) {
				int decision = rs.getInt("decision");

				if (decision == 1) {
					decisionStr = "Accepted";
				} if (decision == 2) {
					decisionStr = "Rejected";
				}
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return decisionStr;
		
	}
	
	/**
	 * Method which returns a the user that is assigned as the SPC for the given paper.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param paperID The id of a paper to get the SPC for.
	 * @return The user which is the SPC for this paper.
	 */
	public User getSPCforPaper(final int paperID) {
		User spc = null;
		
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM recommendation WHERE paperID = '" + paperID + "'");
			
			if (rs.next()) {
				int userID = rs.getInt("userID");
				rs = stmt.executeQuery("SELECT * FROM user WHERE userID = '" + userID + "'");
				
				spc = new User(rs.getInt("userID"), rs.getInt("roleID"), 
						rs.getString("firstName"), rs.getString("lastName"), rs.getString("email"));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return spc;
	}
	
	/**
	 * Method which returns a list of reviews for a given paper.
	 * 
	 * @author Erik Tedder
	 * @param paperID a paper's id to receive the reviews for.
	 * @return a list of reviews for the given paper id.
	 */
	public List<Review> getReviewsForPaper(final int paperID) {
		ArrayList<Review> list = new ArrayList<Review>();
		
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM review WHERE paperID = '" + paperID + "'");
			
			while (rs.next()) {
				list.add(new Review(rs.getInt("userID"), rs.getInt("score"), rs.getString("comment")));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return list;
	}
	
	/**
	 * Method which returns a list of papers that are associated with a given subprogram chair.
	 * 
	 * @author Anton Bardakhanov, Erik Tedder (altered for database)
	 * @param spcID the subprogram chair's id to get papers for.
	 * @return a list of papers associated with this spc.
	 */
	public List<Paper> getPapersBySpc(final int spcID){
		List<Paper> papers = new ArrayList<Paper>();
		try {
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM recommendation WHERE userID = '" + spcID + "'");
			
			while (rs.next()) {
				papers.add(getPaper(rs.getInt("paperID")));
			}
			
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return papers;
		
		//DEPRECATED
//		for(Paper p: my_papers.values()){
//			if(p.getSubchairID() == spcId)
//				papers.add(p);
//		}
	}

	/**
	 * Method which returns the number of days remaining until the conference deadline.
	 * 
	 * @author Erik Tedder
	 * @return the days left til deadline.
	 */
	public int getDaysLeft() {
		long todaysDate = new GregorianCalendar().getTimeInMillis();

		long daysLeft = (DEADLINE.getTimeInMillis() - todaysDate)
				/ (1000 * 60 * 60 * 24);

		return (int) daysLeft;
	}

	/**
	 * Getter method for the current deadline of the conference.
	 * 
	 * @author Erik Tedder
	 * @return the current deadline.
	 */
	public GregorianCalendar getDeadline() {
		return DEADLINE;
	}
	
	/**
	 * Method that should be called to ensure the proper "saving" and closing of the conference
	 * class, to ensure no data is lost. 
	 * 
	 * @author Erik Tedder
	 */
	public void saveConference() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
