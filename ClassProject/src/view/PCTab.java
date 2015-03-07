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
import model.Recommendation;
import model.Review;
import model.User;

/**
 * JScrollPane which will act as the tab for the subprogram chair view.
 * 
 * @author Erik Tedder
 * @date 6/2/2014
 */
@SuppressWarnings("serial")
public class PCTab extends JScrollPane {
	
	/** The current conference. */
	private Conference myConference;
	/** The table of papers that still need a subprogram chair. */
	private JTable assignmentTable;
	/** The table of papers currently undergoing a review. */
	private JTable processTable;
	
	/**
	 * Constructor of a new PCTab with the given conference.
	 * 
	 * @param theConference The current conference.
	 */
	public PCTab(final Conference theConference) {
		super();
		
		myConference = theConference;
		assignmentTable = new JTable();
		processTable = new JTable();
		
		assignmentTable.setDragEnabled(false);
		assignmentTable.getTableHeader().setReorderingAllowed(false);
		assignmentTable.getTableHeader().setResizingAllowed(false);
		
		processTable.setDragEnabled(false);
		processTable.getTableHeader().setReorderingAllowed(false);
		processTable.getTableHeader().setResizingAllowed(false);
		
		initClass();
	}
	
	private void initClass() {
		JPanel pcPanel = new JPanel();
		JPanel innerPcPanel = new JPanel();
		innerPcPanel.setBackground(new java.awt.Color(255, 255, 255));
		innerPcPanel.setLayout(new BoxLayout(innerPcPanel, BoxLayout.Y_AXIS));
		
		JLabel completedLabel = new JLabel("Papers Needing Assignment");
		completedLabel.setAlignmentX(CENTER_ALIGNMENT);
		JScrollPane pcScrollPane = new JScrollPane();
		pcScrollPane.setPreferredSize(new Dimension(600, 125));
		
		setBackground(new java.awt.Color(255, 255, 255));
        setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pcPanel.setBackground(new java.awt.Color(255, 255, 255));

        assignmentTable.setModel(new UnassignedTableModel(myConference.getAllPapers()));
        assignmentTable.setShowHorizontalLines(false);
        assignmentTable.setShowVerticalLines(false);
        pcScrollPane.setViewportView(assignmentTable);

        completedLabel.setToolTipText("From this table you will be able to look over any "
        		+ "papers that need to be assigned to a subprogram chair.");
        
        assignmentTable.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Integer paperID = (Integer) assignmentTable.getValueAt(assignmentTable.getSelectedRow(), 0);
					
					new AssignDialog(myConference.getUserByRole(2),	myConference.getPaper(paperID));
				}
			}
		});
        
        JLabel assignedLabel = new JLabel("Papers In Review");
        assignedLabel.setAlignmentX(CENTER_ALIGNMENT);
		JScrollPane pcScrollPane1 = new JScrollPane();
		pcScrollPane1.setPreferredSize(new Dimension(600, 125));
		
		processTable.setShowHorizontalLines(false);
		processTable.setShowVerticalLines(false);
        pcScrollPane1.setViewportView(processTable);

        assignedLabel.setToolTipText("From this table you will be able to look over any "
        		+ "papers in the review process.");
        
        processTable.addMouseListener(new MouseAdapter() {			
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Integer paperID = (Integer) processTable.getValueAt(processTable.getSelectedRow(), 0);
					
					if (myConference.getRecommendationForPaper(paperID).getState() != 0) {
						new DecisionDialog(myConference, myConference.getPaper(paperID));
					} else {
						JOptionPane.showMessageDialog(null, "Cannot submit a decision "
								+ "until a recommendation have been posted.");
					}
				}
			}
		});
        
        innerPcPanel.add(completedLabel);
        innerPcPanel.add(pcScrollPane);
        innerPcPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        innerPcPanel.add(assignedLabel);
        innerPcPanel.add(pcScrollPane1);
        innerPcPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        JButton assignSPC = new JButton("New Subprogram Chair");
        assignSPC.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				new SPCDialog(myConference.getUserByRole(4));
			}
		});
        assignSPC.setAlignmentX(CENTER_ALIGNMENT);
        innerPcPanel.add(assignSPC);

        pcPanel.add(innerPcPanel);
        setViewportView(pcPanel);
	}
	
	/**
	 * Method which updates the current tables of the PC tab.
	 */
	public void updateTables() {
		ArrayList<Paper> allPapers = myConference.getAllPapers();
		ArrayList<Paper> unassignedPapers = new ArrayList<Paper>();
		ArrayList<Paper> assignedPapers = new ArrayList<Paper>();
		
		for (Paper p : allPapers) {
			if (myConference.getSPCforPaper(p.getId()) != null) {
				assignedPapers.add(p);
			} else {
				unassignedPapers.add(p);
			}
		}
		
		
		assignmentTable.setModel(new UnassignedTableModel(unassignedPapers));
		processTable.setModel(new AssignedTableModel(assignedPapers));
	}
	
	/**
	 * Inner-class for a JDialog which displays the information for assigning a new 
	 * subprogram chair.
	 * 
	 * @author Erik Tedder
	 */
	private class SPCDialog extends JDialog {
		
		/** The list of users who can be promoted to SPC. */
		private ArrayList<User> myUsers;
		
		/**
		 * Constructor of a new SPCDialog with the given list of users as a parameter.
		 * 
		 * @param theUsers The users who can be promoted to a subprogram chair.
		 */
		public SPCDialog(final List<User> theUsers) {
			super();
			setTitle("Assign a subprogram chair");
			
			myUsers = (ArrayList<User>) theUsers;
			
			initDialog();
						
			pack();
			setLocationRelativeTo(null);
			setResizable(false);
			setVisible(true);
		}

		/** 
		 * Initializes and constructs the tab's display.
		 */
		private void initDialog() {
			JPanel panel = new JPanel();
			
			final User[] nameArray = myUsers.toArray(new User[0]);
			
			final JComboBox<User> list = new JComboBox<User>(nameArray);
			panel.add(list);
			add(new JLabel("Select a user to be set to a subprogram chair:", SwingConstants.CENTER), BorderLayout.NORTH);
			add(panel, BorderLayout.CENTER);
			
			JPanel buttonPanel = new JPanel();
			JButton set = new JButton("Change Role");
			set.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to "
							+ "change " + nameArray[list.getSelectedIndex()] + "'s role to "
									+ "subprogram chair?", "Change User's Role", 
									JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION){
						myConference.changeUserRole(nameArray[list.getSelectedIndex()].getID(), 2);
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
	 * Inner-class which created a JDialog that allows for assigning a subprogram chair to a 
	 * paper.
	 * 
	 * @author Erik
	 */
	private class AssignDialog extends JDialog {
		
		/** The list of users able to be assigned. */
		private ArrayList<User> myUsers;
		
		/** The paper needing assignment. */
		private Paper myPaper;
		
		/**
		 * Constructor of a new AssignDialog class with the given list of users and a given 
		 * paper.
		 * 
		 * @param theUsers The users able to be assigned to the paper. 
		 * @param thePaper The paper needing a subprogram chair.
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
		 * Initializes and constructs the diplay for this class.
		 */
		private void initDialog() {
			JPanel panel = new JPanel();
			
			final User[] nameArray = myUsers.toArray(new User[0]);
			
			final JComboBox<User> list = new JComboBox<User>(nameArray);
			panel.add(list);
			add(new JLabel("Select a subprogram chair to be assigned:", SwingConstants.CENTER), BorderLayout.NORTH);
			add(panel, BorderLayout.CENTER);
			
			JPanel buttonPanel = new JPanel();
			JButton set = new JButton("Assign to Paper");
			set.addActionListener(new ActionListener() {				
				public void actionPerformed(ActionEvent e) {
					int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to "
							+ "assign " + nameArray[list.getSelectedIndex()] + " to Paper " + myPaper.getTitle(), "Assign To Paper", 
									JOptionPane.YES_NO_OPTION);
					if (n == JOptionPane.YES_OPTION){
						try {
							myConference.assignSpc(nameArray[list.getSelectedIndex()].getID(), myPaper.getId());
						} catch (BusinessRuleException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
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

		private String[] columnNames = {"ID", "Title", "Author Name", "SPC"};
		private ArrayList<Paper> myPaperList;

		public UnassignedTableModel(final ArrayList<Paper> arrayList) {
			myPaperList = (ArrayList<Paper>) arrayList;
		}

		public int getRowCount() {
			return myPaperList.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

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
				ret = (Object) "Not Assigned";						
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

		private String[] columnNames = {"ID", "Title", "Author Name", "SPC", "Review 1", 
										"Review 2", "Review 3"};
		private ArrayList<Paper> myPaperList;

		public AssignedTableModel(final ArrayList<Paper> arrayList) {
			myPaperList = (ArrayList<Paper>) arrayList;
		}

		public int getRowCount() {
			return myPaperList.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

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
				Recommendation r = myConference.getRecommendationForPaper(myPaperList.get(rowIndex).getId());
				
				if (r.getState() != 0) {
					ret = (Object) r.getState();
				} else {
					ret = (Object) myConference.getSPCforPaper((myPaperList.get(rowIndex).getId()));
				}						
				break;		
			case 4:
				if (reviews.size() != 0 && reviews.get(0).getScore() != 0) {
					ret = (Object) reviews.get(0).getScore();
				} else {
					ret = (Object) "None";
				}
				break;
			case 5:
				if (reviews.size() != 0 && reviews.get(1).getScore() != 0) {
					ret = (Object) reviews.get(1).getScore();
				} else {
					ret = (Object) "None";
				}
				break;
			case 6:
				if (reviews.size() != 0 && reviews.get(2).getScore() != 0) {
					ret = (Object) reviews.get(2).getScore();
				} else {
					ret = (Object) "None";
				}
				break;
			}

			return ret;
		}

	}

}
