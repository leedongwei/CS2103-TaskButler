// @author A0097802Y

package butler.ui;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import butler.common.LoggerPreset;

public class TrayIconForSystemTray {
	
	private SystemTray sysTray;
	private PopupMenu menu = new PopupMenu();
	private MenuItem aboutMenuItem;
	private MenuItem showAppMenuItem;
	private MenuItem exitMenuItem;
	private TrayIcon trayIcon;
	private Image taskButlerImage;
	private Frame frame;
	
	private static final String ABOUT_MESSAGE = buildAboutMessage();
	private static final String msgMenuAbout = "About Task Butler";
	private static final String msgMenuShowApp = "Show Application";
	private static final String msgMenuExitApp = "Exit Application";
	
	private static final Logger Log = LoggerPreset.getLogger();
	
	/**
	 *Highest level of Abstraction for the overall tray icon
	 */
	public TrayIconForSystemTray(Frame frame){
		
		assert frame != null;
		
		// check to see if system tray is supported on OS
				if (SystemTray.isSupported()) {
					
					sysTray = SystemTray.getSystemTray();
					this.frame = frame;
					
					Log.info("Begin tray icon operations");
					processTrayIcon();
					initializeMenuItems();
					populateTrayIconMenu();
					addAboutMenuItemListener();
					addShowAppMenuItemListener();
					addExitAppMenuItemListener();
				}
	}
	
	/**
	 *Highest level of Abstraction for the tray icon itself
	 */
	private void processTrayIcon() {
		retrieveTrayIconImage();
		createTrayIcon();
		addTrayIconListener();
		addTrayIconToTray();
	}

	/**
	 *Add tray icon to system tray
	 */
	private void addTrayIconToTray() {
				try {
					sysTray.add(trayIcon);
					Log.info("Add tray icon to system tray");
				} catch (AWTException e) {
					Log.severe("Cannot add tray icon to system tray");
				}
	}

	/**
	 *This operation adds listener to the tray icon
	 *to maximize the program
	 */
	private void addTrayIconListener() {
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					displayUI();
					Log.info("Icon clicked to maximise program");
				}
			}
		});
	}

	/**
	 *This operation adds listener to "Exit Application" Menu Item 
	 *to exit program
	 */
	private void addExitAppMenuItemListener() {

		// add action listener to the exit item in the popup menu
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}

			private void exitProgram() {
				Log.info("Exit program");
				System.exit(0);
			}
		});
	}

	/**
	 *This operation adds listener to "Show Application" Menu Item 
	 *to maximize program
	 */
	private void addShowAppMenuItemListener() {

		showAppMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Log.info("Show Application button clicked");
				displayUI();
			}
		});
	}
	
	/**
	 *This operation adds items to the menu
	 */
	private void populateTrayIconMenu() {
		menu.add(aboutMenuItem);
		menu.add(showAppMenuItem);
		menu.add(exitMenuItem);
		Log.info("Added menu items to menu");
	}

	/**
	 *This operation initialized the menu items
	 */
	private void initializeMenuItems() {
		aboutMenuItem = new MenuItem(msgMenuAbout);
		showAppMenuItem = new MenuItem(msgMenuShowApp);
		exitMenuItem = new MenuItem(msgMenuExitApp);
		Log.info("Initialized all menu items");
	}

	/**
	 *This operation retrieves the image for program system tray icon
	 */
	private void retrieveTrayIconImage() { 
		taskButlerImage = Toolkit.getDefaultToolkit().
				getImage(getClass().getResource("logo.png"));
		Log.info("Retrieved Tray Icon Image");
	}

	/**
	 *This operation creates tray icon with the image retrieved
	 */
	private void createTrayIcon() {
		trayIcon = new TrayIcon(taskButlerImage, "Task Butler", menu);
		Log.info("Created Tray Icon");
	}

	/**
	 *This operation returns message on "About Task Butler" menu item
	 */
	private static String buildAboutMessage() {

		StringBuilder message = new StringBuilder();

		message.append("CS2103 Aug 2013 Group f10-2j\n");
		message.append("Team Members:\n");
		message.append("Cheu Wee Loon (A0097836L)\n");
		message.append("Goh Jiaquan (A0097722X)\n");
		message.append("Koo Quan En Clement (A0097802Y)\n");
		message.append("Lee Dong Wei (A0085419X)\n\n");

		message.append("Acknowledgement:\n");
		message.append("This application includes third-party packages by\n");
		message.append("- Joda-Time: http://www.joda.org/joda-time/\n");
		message.append("- Balloon Tip: http://balloontip.java.net/\n");
		message.append("- JNativeHook: http://code.google.com/p/jnativehook/");

		return message.toString();
	}
	
	/**
	 *This operation adds listener to "About Task Butler" Menu Item 
	 *to show ABOUT_MESSAGE content
	 */
	private void addAboutMenuItemListener() {

		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showAboutMenuMsg();
				Log.info("ABOUT_MESSAGE shown");
			}

			private void showAboutMenuMsg() {
				JOptionPane.showMessageDialog(frame, ABOUT_MESSAGE,
						msgMenuAbout, JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	/**
	 *This operation maximizes the program
	 */
	private void displayUI() {

		// for cases if the user explicitly click minimize button
		if (frame.getState() == JFrame.ICONIFIED) {
			frame.setState(JFrame.NORMAL);
		}

		frame.setVisible(true);
	}
}
