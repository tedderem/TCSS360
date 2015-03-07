package model;

//import java.util.ArrayList;
//import java.util.List;


/**
 * @author Steven Bradley
 * @version june 2 2014
 *
 */
public class Paper {
	/**
	 * The title of the paper.
	 */
	private String myTitle;
	/**
	 * A brief summary of the paper.
	 */
	private String myAbstract;
	/**
	 * The name of the paper file.
	 */
	private String myFile;
	/**
	 * The unique ID for the paper.
	 */
	private int myID;
	/**
	 * The unique ID for the author of the paper.
	 */
	private int myAuthorID;
	/**
	 * The unique ID for the subchair assigned to the paper.
	 */
	//private int mySubchair;
	/**
	 * The list of IDs for reviewers assigned to the paper.
	 */
	//private List<Integer> myReviewers;
	/**
	 * The list of reviews of the paper.
	 */
	//private List<Review> myReviews;
	
	// 0 - undecided, 1-accepted, 2-declined
	// please see Recommendation class;
	/**
	 * The recommendation made after the paper is reviewed.
	 */
	//private Recommendation recommendation;
	// 0 - undecided, 1-yes, 2-no
	/**
	 * Whether or not the paper has been accepted or rejected. A value of 0 denotes no decision,
	 * a value of 1 indicates paper has been accepted, and a value of 2 denotes the paper has
	 * been rejected from the Conference.
	 */
	//private int decision = 0;
	
	/** Creates a new paper with the folling fields.
	 * @param paperID
	 * @param authorID
	 * @param title
	 * @param anAbstract
	 * @param file
	 */
	public Paper(int paperID, int authorID, String title, String anAbstract, String file){
		myID = paperID;
		myAuthorID = authorID;
		myTitle = title;
		myAbstract = anAbstract;
		myFile = file;
//		myReviewers = new ArrayList<Integer>();
//		myReviews = new ArrayList<Review>();
//		recommendation = new Recommendation();
	}

	/** Assigns the given subchair id to the paper.
	 *  Checks to make sure the subchair is not the author of the paper.
	 * @param id
	 * @throws BusinessRuleException
	 */
//	public void assignSpc(int id) throws BusinessRuleException {
//		if (myAuthorID != id) {
//			mySubchair = id;
//		} else {
//			throw new BusinessRuleException("User cannot be a subprogram chair "
//					+ "to their own paper.");
//		}
//	}
	
	/** Assigns the given reviewer ID to the paper.
	 *  Checks to make sure the reviewer is not the author of the paper.
	 * @param id
	 * @throws BusinessRuleException 
	 */
//	public void assignReviewer(int id) throws BusinessRuleException {
//		if (myAuthorID != id) {
//			myReviewers.add(id);
//		} else {
//			throw new BusinessRuleException("User cannot be a reviewer to their own paper.");
//		}
//	}
//	
	/** Sets the recommendation for the paper.
	 * @param r
	 */
//	public void setRecommendation(Recommendation r){
//		recommendation = r;
//	}
	
	/** Gets the unique ID for the paper.
	 * @return the ID of the paper.
	 */
	public int getId(){
		return myID;
	}
	
	/** Gets the unique ID of the author.
	 * @return ID of the author.
	 */
	public int getAuthorID() {
		return myAuthorID;
	}
	
//	/** Gets the unique ID of the subchair.
//	 * @return ID of the subchair.
//	 */
//	public int getSubchairID() {
//		return mySubchair;
//	}
	
//	/** Gets the list of reviewer IDs for the paper.
//	 * @return List of reviewer IDs.
//	 */
//	public List<Integer> getReviewerList(){
//		return myReviewers;
//	}
//	
//	/** Gets the current status of the paper. (Accept/Reject/Undecided)
//	 * @return state of decision.
//	 */
//	public int getStatus(){
//		return recommendation.state;
//	}
	
//	/** Sets the decision for the paper. (Accept/Reject/Undecided)
//	 * @param decision
//	 */
//	public void setDecision(int decision){
//		this.decision = decision;
//	}
	
	/** Adds the given review to the list of the papers reviews.
	 * @param r the review to add to the paper.
	 */
//	public void submitReviewToPaper(Review r){
//		myReviews.add(r);
//	}
	
	public String getTitle() {
		return myTitle;
	}	
	
	public String getAbstract() {
		return myAbstract;
	}
	
	public String getFile() {
		return myFile;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @author Erik Tedder
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		
		if (other instanceof Paper) {
			Paper otherPaper = (Paper) other;
			
			if (otherPaper.myID == myID && otherPaper.myAbstract.equals(myAbstract) 
					&& otherPaper.myFile.equals(myFile) && otherPaper.myAuthorID == myAuthorID 
					&& otherPaper.myTitle.equals(myTitle))
				return true;
		}
		
		return false;
	}
	
//	public Recommendation getRec()
//	{
//		return recommendation;
//	}
//	
//	public boolean hasRev()
//	{
//		return myReviewers.size() != 0;
//	}
//	
//	public List<Review> getRev()
//	{
//		return myReviews;
//	}

//	public String toString() {
//		String output =  myID + "," + myAuthorID + "," + myTitle + "," + myAbstract + "," + myFile + "," + mySubchair
//				+ "~" + recommendation.toString() + "~";
//		
//		for (Review r : myReviews) {
//			output = output + r.toString() + "^";
//		}
//		
//		return output;
//	}
}
