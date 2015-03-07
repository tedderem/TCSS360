package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import model.BusinessRuleException;
import model.Conference;
import model.Paper;
import model.Review;
import model.User;

/**
 * JScrollPane which will act as the tab for the subprogram chair view.
 * 
 * @author Erik Tedder
 * @date 6/2/2014
 */
@SuppressWarnings("serial")
public class SPCTab extends JScrollPane {

	/** The Current conference. */
	private Conference myConference;
	/** The table which will display all papers still needing assignments */
	private JTable myUnassignedTable;
	/** The table which will display all papers that have been assigned. */
	private JTable myAssignedTable;

	/**
	 * Constructor for a new SPCTab with the given conference.
	 * 
	 * @param theConference The current conference.
	 */
	public SPCTab(final Conference theConference) {
		super();

		myConference = theConference;
		myUnassignedTable = new JTable();
		myAssignedTable = new JTable();

		myUnassignedTable.setDragEnabled(false);
		myUnassignedTable.getTableHeader().setReorderingAllowed(false);
		myUnassignedTable.getTableHeader().setResizingAllowed(false);
		
		myAssignedTable.setDragEnabled(false);
		myAssignedTable.getTableHeader().setReorderingAllowed(false);
		myAssignedTable.getTableHeader().setResizingAllowed(false);

		initClass();
	}

	/**
	 * Method which initializes and constructs the layout for this tab.
	 */
	private void initClass() {
		JPanel pcPanel = new JPanel();
		JPanel innerPcPanel = new JPanel();
		innerPcPanel.setBackground(new java.awt.Color(255, 255, 255));
		innerPcPanel.setLayout(new BoxLayout(innerPcPanel, BoxLayout.Y_AXIS));

		JLabel unassignedLabel = new JLabel("Papers Needing Reviewers");
		unassignedLabel.setAlignmentX(CENTER_ALIGNMENT);
		JScrollPane unassignedScrollPane = new JScrollPane();
		unassignedScrollPane.setPreferredSize(new Dimension(600, 125));

		setBackground(new java.awt.Color(255, 255, 255));
		setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		pcPanel.setBackground(new java.awt.Color(255, 255, 255));

		myUnassignedTable.setShowHorizontalLines(false);
		myUnassignedTable.setShowVerticalLines(false);
		unassignedScrollPane.setViewportView(myUnassignedTable);

		unassignedLabel.setToolTipText("From this table you will be able to look over any "
				+ "papers that have need to have reviewers assigned to them. ");

		myUnassignedTable.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Integer paperID = (Integer) myUnassignedTable.getValueAt(myUnassignedTable.getSelectedRow(), 0);
					
					new AssignDialog(myConference.getUserByRole(4),	myConference.getPaper(paperID));
				}
			}
		});

		innerPcPanel.add(unassignedLabel);
		innerPcPanel.add(unassignedScrollPane);
		innerPcPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		
		JLabel assignedLabel = new JLabel("Papers In Review");
		assignedLabel.setAlignmentX(CENTER_ALIGNMENT);
		JScrollPane assignedScrollPane = new JScrollPane();
		assignedScrollPane.setPreferredSize(new Dimension(600, 125));

		myAssignedTable.setShowHorizontalLines(false);
		myAssignedTable.setShowVerticalLines(false);
		assignedScrollPane.setViewportView(myAssignedTable);

		assignedLabel.setToolTipText("From this table you will be able to look over any "
				+ "papers that have completed, or are under-going, their review process. ");

		myAssignedTable.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Integer paperID = (Integer) myAssignedTable.getValueAt(myAssignedTable.getSelectedRow(), 0);
					
					ArrayList<Review> reviews = (ArrayList<Review>) myConference.getReviewsForPaper(paperID);
					//flag to dictate being able to make a recommendation
					boolean canRecommend = true; 
					for (Review r : reviews) {
						if (r.getScore() == 0)
							canRecommend = false;
					}
					
					if (canRecommend) {
						new SeeReviewsDialog(myConference, myConference.getPaper(paperID));
					} else {
						JOptionPane.showMessageDialog(null, "Cannot submit a recommendation "
								+ "until all reviews have been posted.");
					}
				}
			}
		});

		innerPcPanel.add(assignedLabel);
		innerPcPanel.add(assignedScrollPane);
		innerPcPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		
		JButton assignSPC = new JButton("New Reviewer");
		assignSPC.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				new ReviewDialog(myConference.getUserByRole(0));
			}
		});
		assignSPC.setAlignmentX(CENTER_ALIGNMENT);
		innerPcPanel.add(assignSPC);

		pcPanel.add(innerPcPanel);
		setViewportView(pcPanel);
	}

	/**
	 * Updates the tables within the SPC tab.
	 */
	public void updateTables() {
		ArrayList<Paper> allPapers = (ArrayList<Paper>) myConference.getPapersBySpc(myConference.getCurrentUser().getID());
		ArrayList<Paper> unassignedPapers = new ArrayList<Paper>();
		ArrayList<Paper> assignedPapers = new ArrayList<Paper>();
		
		for (Paper p : allPapers) {
			if (myConference.getReviewsForPaper(p.getId()).isEmpty()) {
				unassignedPapers.add(p);
			} else {
				assignedPapers.add(p);
			}
		}
		
		
		myUnassignedTable.setModel(new UnassignedTableModel(unassignedPapers));	
		myAssignedTable.setModel(new AssignedTableModel(assignedPapers));
	}

	/**
	 * Inner-class for a new JDialog window to display the necessary information to assign
	 * three reviewers to a paper.
	 * 
	 * @author Erik Tedder
	 */
	private class AssignDialog extends JDialog {

		/** The list of users to display. */
		private ArrayList<User> myUsers;
		
		/** The paper needing the reviewers. */
		private Paper myPaper;

		/**
		 * Constructor for a new AssignDialog window. 
		 * 
		 * @param theUsers The users able to be assigned to a paper.
		 * @param thePaper The paper needing reviewers.
		 */
		public AssignDialog(final List<User> theUsers, final Paper thePaper) {
			super();
			setTitle("Assign a subprogram chair");

			myUsers = (ArrayList<User>) theUsers;
			myPaper = thePaper;

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
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

			final User[] nameArray = myUsers.toArray(new User[0]);

			final JComboBox<User> list = new JComboBox<User>(nameArray);
			final JComboBox<User> list2 = new JComboBox<User>(nameArray);
			final JComboBox<User> list3 = new JComboBox<User>(nameArray);
			
			JPanel rev1 = new JPanel();
			rev1.add(new JLabel("Reviewer 1:"));
			rev1.add(list);
			panel.add(rev1);
			
			JPanel rev2 = new JPanel();
			rev2.add(new JLabel("Reviewer 2:"));
			rev2.add(list2);
			panel.add(rev2);
			
			JPanel rev3 = new JPanel();
			rev3.add(new JLabel("Reviewer 3:"));
			rev3.add(list3);
			panel.add(rev3);
			
			add(new JLabel("Select a Reviewer to be assigned:", SwingConstants.CENTER), BorderLayout.NORTH);
			add(panel, BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel();
			JButton set = new JButton("Assign to Paper");
			set.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to "
							+ "assign reviewers:\n\n" + nameArray[list.getSelectedIndex()] 
							+ "\n" + nameArray[list2.getSelectedIndex()] + "\n" 
							+ nameArray[list3.getSelectedIndex()] + "\n\nto Paper " 
							+ myPaper.getTitle(), "Assign To Paper", 
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION){
						if (nameArray[list.getSelectedIndex()].getID() !=  nameArray[list2.getSelectedIndex()].getID() 
								&& nameArray[list.getSelectedIndex()].getID() !=  nameArray[list3.getSelectedIndex()].getID() 
								&& nameArray[list2.getSelectedIndex()].getID() !=  nameArray[list3.getSelectedIndex()].getID()) {
							try {
								myConference.assignReviewerToPaper(nameArray[list.getSelectedIndex()].getID(), myPaper.getId());
								myConference.assignReviewerToPaper(nameArray[list2.getSelectedIndex()].getID(), myPaper.getId());
								myConference.assignReviewerToPaper(nameArray[list3.getSelectedIndex()].getID(), myPaper.getId());
							} catch (BusinessRuleException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
							} finally {							
								dispose();
							}
						} else {
							JOptionPane.showMessageDialog(null, "Cannot assign the same reviewer twice to a paper. Try again.");
						}
						
					}
				}
			});
			buttonPanel.add(set);
			buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			buttonPanel.add(cancel);

			add(buttonPanel, BorderLayout.SOUTH);
		}
	}

	/**
	 * Inner-class of a new Review Dialog. Dialog will display all the necessary information
	 * for assigning a user's role as a reviewer.
	 * 
	 * @author Erik Tedder
	 */
	private class ReviewDialog extends JDialog {

		/** ArrayList of all possible Users */
		private ArrayList<User> myUsers;

		/**
		 * Constructor of a new ReviewDialog class with the given list of users.
		 * 
		 * @param theUsers The list of users available to promote.
		 */
		public ReviewDialog(final List<User> theUsers) {
			super();
			setTitle("Assign a Reviewer");

			myUsers = (ArrayList<User>) theUsers;

			initDialog();

			pack();
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
		}

		/**
		 * Method which initializes the layout and elements of this ReviewDialog.
		 */
		private void initDialog() {
			JPanel panel = new JPanel();

			final User[] nameArray = myUsers.toArray(new User[0]);

			final JComboBox<User> list = new JComboBox<User>(nameArray);
			panel.add(list);
			add(new JLabel("Select a user to be set to a reviewer:", SwingConstants.CENTER), BorderLayout.NORTH);
			add(panel, BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel();
			JButton set = new JButton("Change Role");
			set.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to "
							+ "change " + nameArray[list.getSelectedIndex()] + "'s role to "
							+ "reviwer?", "Change User's Role", 
							JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION){
						myConference.changeUserRole(nameArray[list.getSelectedIndex()].getID(), 4);
						dispose();
					}
				}
			});
			buttonPanel.add(set);
			buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			buttonPanel.add(cancel);

			add(buttonPanel, BorderLayout.SOUTH);
		}
	}

	/**
	 * Inner-class for a new TableModel. Contains the information of the papers and displays
	 * it accordingly in the appropriate JTable.
	 * 
	 * @author Erik Tedder
	 *
	 */
	private class UnassignedTableModel extends AbstractTableModel {

		/** The names of the columns of the table. */
		private String[] columnNames = {"ID", "Title", "Author Name", "Reviewer 1", 
										"Reviewer 2", "Reviewer 3"};
		/** The ArrayList of papers to get displayed. */
		private ArrayList<Paper> myPaperList;

		/**
		 * Constructor of a new UnassignedTableModel with a given list of papers.
		 * 
		 * @param arrayList The papers to be displayed.
		 */
		public UnassignedTableModel(final List<Paper> arrayList) {
			myPaperList = (ArrayList<Paper>) arrayList;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getRowCount() {
			return myPaperList.size();
		}

		/**
		 * {@inheritDoc}
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getColumnName(int column) {
			return columnNames[column];
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			
			switch (columnIndex) {
			case 0:
				ret = (Object) myPaperList.get(rowIndex).getId();
				break;
			case 1:
				ret = (Object) myPaperList.get(rowIndex).getTitle();
				break;
			case 2:
				User author = myConference.getUser(myPaperList.get(rowIndex).getAuthorID());
				ret = (Object) author.getFirstName() + " " + author.getLastName();
				break;	
			case 3:
				ret = (Object) "N/A";
				break;
			case 4:
				ret = (Object) "N/A";
				break;
			case 5:
				ret = (Object) "N/A";
				break;
			}

			return ret;
		}

	}
	
	/**
	 * Inner-class for a new TableModel. Contains the information of the papers and displays
	 * it accordingly in the appropriate JTable.
	 * 
	 * @author Erik Tedder
	 *
	 */
	private class AssignedTableModel extends AbstractTableModel {

		private String[] columnNames = {"ID", "Title", "Author Name", "Review 1", 
										"Review 2", "Review 3"};
		private ArrayList<Paper> myPaperList;

		public AssignedTableModel(final ArrayList<Paper> arrayList) {
			myPaperList = (ArrayList<Paper>) arrayList;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getRowCount() {
			return myPaperList.size();
		}

		/**
		 * {@inheritDoc}
		 */
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getColumnName(int column) {
			return columnNames[column];
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			List<Review> reviews = myConference.getReviewsForPaper(myPaperList.get(rowIndex).getId());

			switch (columnIndex) {
			case 0:
				ret = (Object) myPaperList.get(rowIndex).getId();
				break;
			case 1:
				ret = (Object) myPaperList.get(rowIndex).getTitle();
				break;
			case 2:
				User author = myConference.getUser(myPaperList.get(rowIndex).getAuthorID());
				ret = (Object) author;
				break;	
			case 3:
				if (reviews.get(0).getScore() != 0) {
					ret = (Object) reviews.get(0).getScore();
				} else {
					User rev = myConference.getUser(reviews.get(0).getReviewerID());
					ret = (Object) rev;
				}
				break;
			case 4:
				if (reviews.get(1).getScore() != 0) {
					ret = (Object) reviews.get(1).getScore();
				} else {
					User rev = myConference.getUser(reviews.get(1).getReviewerID());
					ret = (Object) rev;
				}
				break;
			case 5:
				if (reviews.get(2).getScore() != 0) {
					ret = (Object) reviews.get(2).getScore();
				} else {
					User rev = myConference.getUser(reviews.get(2).getReviewerID());
					ret = (Object) rev;
				}
				break;
			}

			return ret;
		}

	}


}
