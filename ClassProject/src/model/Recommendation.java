package model;

/**
 * @author  Steven Bradley
 *
 */
public class Recommendation {
	// [5] strong accept [4] accept [3] neutral [2] reject [1] strong reject
	/**
	 * The numerical state of the recommendation.
	 */
	int state;
	/**
	 * The comments to explain the recommendation.
	 */
	String rationale;

	/**
	 * Creates a new recommendation with no zero state and no comments.
	 */
	public Recommendation() {
		state = 0;
	}

	/**
	 * @param state
	 */
	public void setState(int state) {
		if (state < 0 || state > 5)
			return;
		this.state = state;

	}
	
	/** 
	 * @param comments
	 */
	public void setRationale(String comments) {
		rationale = comments;
	}
	
	/**
	 * @return
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * @return
	 */
	public String getRationale() {
		return rationale;
	}
	
	/*(non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return state + "," + rationale;
	}
}
