package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import model.Conference;
import model.Paper;
import model.Recommendation;
import model.Review;

/**
 * Inner-class for a new JDialog window to display the necessary information to give a 
 * paper a recommendation.
 * 
 * @author Erik Tedder
 */
@SuppressWarnings("serial")
public class DecisionDialog extends JDialog {

	/** The list of users to display. */
	private Conference myConference;

	/** The paper needing the recommendation. */
	private Paper myPaper;

	/**
	 * Constructor for a new RecommendDialog window. 
	 * 
	 * @param theConference The Conference the paper is in.
	 * @param thePaper The paper needing a recommendation.
	 */
	public DecisionDialog(final Conference theConference, final Paper thePaper) {
		super();
		setTitle("Paper Conference Acceptance View");

		myConference = theConference;
		myPaper = thePaper;
		
		initDialog();

		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		JOptionPane.showMessageDialog(this, "This paper is currently listed as " 
				+ myConference.getPaperDecision(myPaper.getId()));
	}

	/**
	 * Method for initializing and constructing the view of this Dialog window.
	 */
	private void initDialog() {
		add(new JLabel("<html><u>Paper Acceptance Display", SwingConstants.CENTER), BorderLayout.NORTH);
		
		ArrayList<Review> reviews = (ArrayList<Review>) myConference.getReviewsForPaper(myPaper.getId());
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		JPanel paperPanel = new JPanel();
		JPanel innerPaper = new JPanel();
		innerPaper.setLayout(new BoxLayout(innerPaper, BoxLayout.Y_AXIS));
		String paperInfo = myPaper.getTitle() + " by " + myConference.getUser(myPaper.getAuthorID());
		
		innerPaper.add(new JLabel(paperInfo));
		paperPanel.add(innerPaper);
		paperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		centerPanel.add(paperPanel);
		
		centerPanel.add(Box.createVerticalStrut(8));
		
		JLabel recLabel = new JLabel("<html><i>Please consider the reviews below when making your decision:", SwingConstants.CENTER);
		recLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(recLabel);
		centerPanel.add(Box.createVerticalStrut(10));
				
		//////////////////////////////////////////////
		
		Recommendation recommend = myConference.getRecommendationForPaper(myPaper.getId());
		
		JPanel recPanel = new JPanel();
		recPanel.setLayout(new BoxLayout(recPanel, BoxLayout.Y_AXIS));
		
		JLabel recommendLabel = new JLabel("Recommendation by " + myConference.getSPCforPaper(myPaper.getId()) + ":");
		recommendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(recommendLabel);
		
		String recScoreString = "Score : ";
		if (recommend.getState() != 0) {
			recScoreString += recommend.getState();
		} else {
			recScoreString += "not yet recommended";
		}
		
		JLabel recommendation = new JLabel(recScoreString);
		recommendation.setAlignmentX(Component.LEFT_ALIGNMENT);
		recPanel.add(Box.createHorizontalStrut(20));
		recPanel.add(recommendation);
		recPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel recommendPanel = new JPanel();
		recommendPanel.setLayout(new BoxLayout(recommendPanel, BoxLayout.X_AXIS));
		recommendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel recCommentLabel = new JLabel("Comment : ");
		recCommentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		recommendPanel.add(recCommentLabel);
		JTextArea recText = new JTextArea();
		recText.setLineWrap(true);
		recText.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		recText.setEditable(false);
		recText.setBackground(getBackground());
		recText.setText(recommend.getRationale());
		
		JScrollPane recPane = new JScrollPane(recText);
		recPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		recPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		recPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		recPane.setPreferredSize(new Dimension(75, 50));
		
		recommendPanel.add(recPane);
		recommendPanel.add(Box.createHorizontalStrut(5));
		recPanel.add(recommendPanel);
		
		centerPanel.add(recPanel);		
		centerPanel.add(Box.createVerticalStrut(8));
		
		/////////////////
		
		int reviewNum = 1;
		
		for (Review r : reviews) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			JLabel label = new JLabel("Review " + reviewNum +" by " + myConference.getUser(r.getReviewerID()) + ":");
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			centerPanel.add(label);
			reviewNum++;			
			
			String scoreString = "Score : ";
			if (r.getScore() != 0) {
				scoreString += r.getScore();
			} else {
				scoreString += "not yet reviewed";
			}
			
			JLabel score = new JLabel(scoreString);
			score.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(Box.createHorizontalStrut(20));
			panel.add(score);
			panel.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			JPanel comment = new JPanel();
			comment.setLayout(new BoxLayout(comment, BoxLayout.X_AXIS));
			comment.setAlignmentX(Component.LEFT_ALIGNMENT);
			JLabel commentLabel = new JLabel("Comment : ");
			commentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			comment.add(commentLabel);
			JTextArea commentText = new JTextArea();
			commentText.setLineWrap(true);
			commentText.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			commentText.setEditable(false);
			commentText.setBackground(getBackground());
			commentText.setText(r.getComment());
			
			JScrollPane commentPane = new JScrollPane(commentText);
			commentPane.setAlignmentX(Component.LEFT_ALIGNMENT);
			commentPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			commentPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			commentPane.setPreferredSize(new Dimension(75, 50));
			
			comment.add(commentPane);
			comment.add(Box.createHorizontalStrut(5));
			panel.add(comment);
			
			centerPanel.add(panel);		
			centerPanel.add(Box.createVerticalStrut(8));
		}
		
		final String[] decisions = {"Accept", "Reject"};
		final JComboBox<String> stateBox = new JComboBox<String>(decisions);
		JPanel decisionsPanel = new JPanel();
		decisionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		decisionsPanel.add(new JLabel("Paper Decision: "));
		decisionsPanel.add(stateBox);		
		
		centerPanel.add(decisionsPanel);
		
		JPanel buttonPanel = new JPanel();
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				myConference.submitDecision(myPaper.getId(), stateBox.getSelectedIndex() + 1);
				String s = "You have ";
				if (stateBox.getSelectedIndex() + 1 == 1) {
					s += "accepted this paper to the conference.";
				} else {
					s += "rejected this paper from the conference.";
				}
				JOptionPane.showMessageDialog(null, s);
				dispose();
			}
		});
		buttonPanel.add(submitButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPanel.add(cancelButton);
		//centerPanel.add(buttonPanel);
		
		add(centerPanel);
		add(buttonPanel, BorderLayout.SOUTH);
	}
}