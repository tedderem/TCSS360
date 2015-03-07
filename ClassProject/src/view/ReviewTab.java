package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import model.Conference;
import model.Paper;
import model.Review;
import model.User;

/**
 * JPanel which will act as the tab for the Author view.
 * 
 * @author Erik Tedder
 * @date 6/2/2014
 */
@SuppressWarnings("serial")
public class ReviewTab extends JPanel {
	
	/** The current conference. */
	private Conference myConference;
	/** The center panel for adding new items. */
	private JPanel myCenterPanel;
	/** The text area for adding a review. */
	private JTextArea myTextArea;

	/**
	 * Constructor of the AuthorTab.
	 * 
	 * @param theConference The conference of this tab.
	 */
	public ReviewTab(final Conference theConference) {
		super();
		
		myTextArea = new JTextArea();
		
		setBackground(new java.awt.Color(255, 255, 255));
		
		myConference = theConference;

		setLayout(new BorderLayout());
		add(new JLabel("Your Papers To Review (Click to View)", SwingConstants.CENTER), BorderLayout.NORTH);
		JPanel innerCenterPanel = new JPanel();
		innerCenterPanel.setBackground(new java.awt.Color(255, 255, 255));
		myCenterPanel = new JPanel();
		myCenterPanel.setLayout(new BoxLayout(myCenterPanel, BoxLayout.Y_AXIS));
		myCenterPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		
		myCenterPanel.setBackground(new java.awt.Color(255, 255, 255));
		
		innerCenterPanel.add(myCenterPanel);
		add(innerCenterPanel, BorderLayout.CENTER);
		
		updateDisplay();
	}

	/**
	 * Method which updates the display of current papers to an author.
	 */
	public void updateDisplay() {
		if (myConference.getCurrentUser() != null) {
			myCenterPanel.removeAll();
			
			User user = myConference.getCurrentUser();
			ArrayList<Paper> papers = (ArrayList<Paper>) myConference.getReviewerList(user.getID());
			
			if (papers.size() == 0) {
				myCenterPanel.add(new JLabel("No papers requiring review", SwingConstants.CENTER));
			}
			
			for (final Paper p : papers) {
				JPanel panel = new JPanel();
				panel.setBackground(new Color(255, 255, 255));
				
				JLabel label = new JLabel(p.getTitle());
				label.setPreferredSize(new Dimension(200, 35));
				
				panel.add(label);
				
				JButton viewButton = new JButton("View Paper");
				viewButton.setAlignmentX(CENTER_ALIGNMENT);
				viewButton.addActionListener(new ActionListener() {					
					public void actionPerformed(final ActionEvent e) {
						String fileName = "Papers/" + p.getAuthorID() + "/" + p.getFile();
						//Attempts to load the file using the user's default program
						try {
							Desktop.getDesktop().open(new File(fileName));
						} catch (final IOException e1) {
							JOptionPane.showMessageDialog(null, "Error loading selected paper.");
						}
					}
				});
				panel.add(viewButton);
				
				JButton reviewButton = new JButton("Review Paper");
				reviewButton.setAlignmentX(CENTER_ALIGNMENT);
				reviewButton.addActionListener(new ActionListener() {					
					public void actionPerformed(final ActionEvent e) {
						ArrayList<Review> rev = (ArrayList<Review>) myConference.getReviewsForPaper(p.getId());
						
						for (Review r : rev) {
							if (r.getReviewerID() == myConference.getCurrentUser().getID() && r.getScore() == 0) {
								new ReviewDialog(p);
							} else if (r.getReviewerID() == myConference.getCurrentUser().getID() && r.getScore() != 0){
								String format = "<html><p style=\"width: 255px;\">";
								String response = format + "You have already reviewed this "
										+ "paper, giving it a score of " + r.getScore() 
										+ "<br><br>Review Comment: " + r.getComment();
								JOptionPane.showMessageDialog(null, response);
							}
						}
						
						
					}
				});
				panel.add(reviewButton);
				
				myCenterPanel.add(panel);
				myCenterPanel.add(Box.createRigidArea(new Dimension(0,5)));
			}		
			
		}
	}
	
	/**
	 * Inner JDialog class that is responsible for showing the contents of a paper.
	 * 
	 * @author Erik Tedder	
	 */
	public class ReviewDialog extends JDialog {
		
		/** The paper being reviewed. */
		private Paper myPaper;
		
		/** A list of JSliders, for the ability to calculate the average. */
		private ArrayList<JSlider> mySliders;
		
		/**
		 * Constructor of a new PaperDialog.
		 * 
		 * @param thePaper The paper to have contents displayed.
		 */
		public ReviewDialog(final Paper thePaper) {
			super();
			setTitle("Review Paper " + thePaper.getTitle());
			
			mySliders = new ArrayList<JSlider>();
			
			myPaper = thePaper;			
			
			initDialog();
			pack();
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
		}

		/**
		 * Initialize the dialog window.
		 */
		private void initDialog() {
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			
			String[] reviewQs = {"<html>Can the content be directly applied by<br>classroom "
					+ "instructors or curriculum designers?", "<html>Does the work appeal to a broad "
							+ "readership <br>interested in engineering education or <br>is it "
							+ "narrowly specialized?", "Does the work address a significant problem?", 
							"<html>Does the author build upon relevant references and<br>bodies of knowledge?", 
							"<html>If a teaching intervention is reported, is it<br> adequately evaluated "
							+ "in terms of its impact<br>on learning in actual use?", 
							"<html>Does the author use methods appropriate to the<br>goals, both for the "
							+ "instructional intervention and the<br>evaluation of impact on learning?", 
							"<html>Did the author provide sufficient detail to replicate<br>and evaluate?", 
							"Is the paper clearly and carefully written?", 
							"<html>Does the paper adhere to accepted standards of <br>style, usage, and composition?"};
			
			add(panel);
			panel.add(new JLabel("Reviewing Paper: " + myPaper.getTitle(), SwingConstants.CENTER), BorderLayout.NORTH);
			
			JPanel centerPanel = new JPanel();
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
			
			Component separator = Box.createRigidArea(new Dimension(0, 10));		
			
			centerPanel.add(separator);
			
			JPanel reviewPanel = new JPanel(new GridLayout(9,2));
			
			for (String s : reviewQs) {
				reviewPanel.add(new JLabel(s, SwingConstants.CENTER));
				reviewPanel.add(createSliders(s));
			}
			centerPanel.add(reviewPanel);
			centerPanel.add(separator);
			
			JPanel comment = new JPanel();
			comment.add(new JLabel("Paper Comments:"));
			myTextArea.setLineWrap(true);
	        myTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	        myTextArea.setPreferredSize(new Dimension(450, 125));
			comment.add(myTextArea);
			
			centerPanel.add(comment);
			
			panel.add(centerPanel, BorderLayout.CENTER);
			
			//Button to just close the panel out
			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					int avg = 0;
					for (JSlider s : mySliders) {
						avg += s.getValue();
					}
					avg = avg / mySliders.size();
					
					String comment = myTextArea.getText();
					myTextArea.setText("");
					
					Review rev = new Review(myConference.getCurrentUser().getID(), avg, comment);
					myConference.submitReview(myPaper.getId(), rev);
					JOptionPane.showMessageDialog(null, "Your review has been added.");
					dispose();
				}
			});
			
			okButton.setAlignmentX(CENTER_ALIGNMENT);
			
			bottomPanel.add(okButton);
			
			centerPanel.add(bottomPanel);
		}
		
		/**
		 * Method which creates a new JSlider.
		 * 
		 * @param question The question to be associated with this slider.
		 * @return The constructed JSlider.
		 */
		private JPanel createSliders(final String question) {
			JPanel panel = new JPanel();
			JSlider slider = new JSlider(1, 5, 3);
			
			
			slider.setMajorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			
			mySliders.add(slider);
			panel.add(slider);
			
			return panel;
		}
	}
}
