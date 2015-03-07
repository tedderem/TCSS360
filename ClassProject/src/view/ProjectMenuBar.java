package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import model.Conference;

/**
 * The JMenuBar class for the project. 
 * 
 * @author Erik Tedder
 * @date 5/24/2014
 */
@SuppressWarnings("serial")
public class ProjectMenuBar extends JMenuBar {

	private JMenu myFileMenu;
	private JMenu myHelpMenu;
	private JMenuItem myExitMenuItem;
	private JMenuItem myHelpMenuItem;
	private JMenuItem myLogoutMenuItem;
	
	private Conference myConference;
	private JFrame myFrame;
	
	/**
	 * No-argument constructor of a new ProjectMenuBar.
	 */
	public ProjectMenuBar(final JFrame theFrame, final Conference theConference) {
		super();
		
		myFileMenu = new JMenu("File");
		myHelpMenu = new JMenu("Help");
		myExitMenuItem = new JMenuItem("Exit");
		myHelpMenuItem = new JMenuItem("Help");
		myLogoutMenuItem = new JMenuItem("Logout");
		
		
		myConference = theConference;
		myFrame = theFrame;
		
		initMenus();
		createActions();
	}	

	/**
	 * Method which creates the menus for the menu bar and initializes the menu items.
	 */
	private void initMenus() {
		add(myFileMenu);
		add(myHelpMenu);
		
		myFileMenu.add(myLogoutMenuItem);
		myFileMenu.addSeparator();
		myFileMenu.add(myExitMenuItem);
		
		myHelpMenu.add(myHelpMenuItem);		
	}	

	/**
	 * Method to create the actions for the various menu bar menu items.
	 */
	private void createActions() {
		myExitMenuItem.addActionListener(new ActionListener() {			
			public void actionPerformed(final ActionEvent e) {
				myConference.saveConference();
				System.exit(0);
			}
		});
		
		myLogoutMenuItem.addActionListener(new ActionListener() {			
			public void actionPerformed(final ActionEvent e) {
				myConference.logout();
			}
		});
		
		myHelpMenuItem.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(myFrame, "Pay no attention to that man "
						+ "behind the curtain");
			}
		});
	}

}
