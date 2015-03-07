package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
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
 * Inner-class for a new JDialog window to display the current reviews of a paper, and 
 * depending on the current user's role displays different information. If the current user is
 * a subprogram chair, then it displays the necessary information to submit a recommendation.
 * 
 * @author Erik Tedder
 */
@SuppressWarnings("serial")
public class SeeReviewsDialog extends JDialog {

	/** The list of users to display. */
	private Conference myConference;

	/** The paper needing the recommendation. */
	private Paper myPaper;
	
	/** Boolean value to denote whether this paper has been reviewed or not. */
	private boolean hasBeenRecommended;

	/**
	 * Constructor for a new RecommendDialog window. 
	 * 
	 * @param theConference The Conference the paper is in.
	 * @param thePaper The paper needing a recommendation.
	 */
	public SeeReviewsDialog(final Conference theConference, final Paper thePaper) {
		super();
		setTitle("Paper Recommendation View");

		myConference = theConference;
		myPaper = thePaper;
		hasBeenRecommended = myConference.getRecommendationForPaper(myPaper.getId()).getState() != 0;
		
		initDialog();

		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Method for initializing and constructing the view of this Dialog window.
	 */
	private void initDialog() {
		add(new JLabel("<html><u>Paper Recommendation Display", SwingConstants.CENTER), BorderLayout.NORTH);
		
		ArrayList<Review> reviews = (ArrayList<Review>) myConference.getReviewsForPaper(myPaper.getId());
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		JPanel paperPanel = new JPanel();
		JPanel innerPaper = new JPanel();
		innerPaper.setLayout(new BoxLayout(innerPaper, BoxLayout.Y_AXIS));
		String paperInfo = myPaper.getTitle() + " by " + myConference.getUser(myPaper.getAuthorID());
		
		JLabel viewLabel = new JLabel(" ", SwingConstants.CENTER);
		
		if (myConference.getCurrentUser().getRole() == 2 
				&& myConference.getCurrentUser().getID() != myPaper.getAuthorID()) {
			viewLabel.setText("<html><u><i>Click To Read");
			viewLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						String fileName = "Papers/" + myPaper.getAuthorID() + "/" + myPaper.getFile();
						//Attempts to load the file using the user's default program
						try {
							Desktop.getDesktop().open(new File(fileName));
						} catch (final IOException e1) {
							JOptionPane.showMessageDialog(null, "Error loading selected paper.");
						}
					}
				}
			});
		} else {
			viewLabel.setText("Paper has been " + myConference.getPaperDecision(myPaper.getId()));
		}
		innerPaper.add(new JLabel(paperInfo));
		innerPaper.add(viewLabel);
		paperPanel.add(innerPaper);
		paperPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		centerPanel.add(paperPanel);
		
		centerPanel.add(Box.createVerticalStrut(8));
		
		JLabel recLabel = new JLabel(" ", SwingConstants.CENTER);
		
		if (myConference.getCurrentUser().getRole() == 2 
				&& myConference.getCurrentUser().getID() != myPaper.getAuthorID()) {		
			recLabel.setText("<html><i>Please consider the reviews below when "
				+ "making your recommenation:");
		} else {
			recLabel.setText("<html><i>Below are the reviews for your submitted paper:");
		}
		recLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		centerPanel.add(recLabel);
		centerPanel.add(Box.createVerticalStrut(10));
		
		int reviewNum = 1;
		
		for (Review r : reviews) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			JLabel label = new JLabel();
			if (myConference.getCurrentUser().getRole() == 2 
					&& myConference.getCurrentUser().getID() != myPaper.getAuthorID()) {
				label.setText("Review " + reviewNum +" by " 
								+ myConference.getUser(r.getReviewerID()) + ":");
			} else {
				label.setText("Review " + reviewNum + ":");
			}
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
		
		if (myConference.getCurrentUser().getRole() == 2 
				&& myConference.getCurrentUser().getID() != myPaper.getAuthorID()) {
			final Integer[] nums = {1, 2, 3, 4, 5};
			final JComboBox<Integer> stateBox = new JComboBox<Integer>(nums);
			JPanel rationalePanel = new JPanel();
			rationalePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			rationalePanel.add(new JLabel("Recommendation Score: "));
			rationalePanel.add(stateBox);		

			if (hasBeenRecommended) {
				stateBox.setSelectedIndex(myConference.getRecommendationForPaper(myPaper.getId()).getState() - 1);
			}

			centerPanel.add(rationalePanel);

			JPanel textPanel = new JPanel();
			final JTextArea textArea = new JTextArea();
			textArea.setAlignmentX(Component.LEFT_ALIGNMENT);
			textArea.setLineWrap(true);
			textPanel.add(new JLabel("Statement: "));
			JScrollPane textPane = new JScrollPane(textArea);
			textPane.setAlignmentX(Component.LEFT_ALIGNMENT);
			textPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			textPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			textPane.setPreferredSize(new Dimension(350, 75));

			if (hasBeenRecommended) {
				textArea.setText(myConference.getRecommendationForPaper(myPaper.getId()).getRationale());
			}

			textPanel.add(textPane);
			textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			centerPanel.add(textPanel);

			JPanel buttonPanel = new JPanel();
			JButton submitButton = new JButton("Submit");
			submitButton.addActionListener(new ActionListener() {			
				public void actionPerformed(ActionEvent e) {
					Recommendation r = new Recommendation();
					String rationale = textArea.getText();
					int score = nums[stateBox.getSelectedIndex()];
					r.setRationale(rationale);
					r.setState(score);
					myConference.spcSubmitRecommendation(myPaper.getId(), r);

					JOptionPane.showMessageDialog(null, "Your Recommendation has been submitted.");
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
			add(buttonPanel, BorderLayout.SOUTH);
		} else {
			JPanel okPanel = new JPanel();
			JButton okButton = new JButton("Ok");
			okButton.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			okPanel.add(okButton);
			add(okPanel, BorderLayout.SOUTH);
		}
		
		add(centerPanel);
		
	}
}