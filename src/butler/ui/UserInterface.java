// @author A0097802Y

package butler.ui;

import java.util.logging.Logger;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.joda.time.DateTime;

import net.java.balloontip.BalloonTip;
import butler.logic.state.DisplayListType;
import butler.logic.state.DisplayListChangedEvent;
import butler.logic.state.DisplayMode;
import butler.logic.state.DisplayModeChangedEvent;
import butler.logic.state.DisplayStateListener;
import butler.logic.Logic;
import butler.common.LoggerPreset;
import butler.common.Task;
import butler.common.TimeSpanHelper;
import butler.logic.command.Action;
import butler.logic.command.Result;

public class UserInterface implements DisplayStateListener, NativeKeyListener {

	//Program Labels Messages
	private static final String EMPTY_NORMAL_MESSAGE = "Normal Tasks Page: Empty";
	private static final String EMPTY_FLOATING_MESSAGE = "Floating Tasks Page: Empty";
	private static final String EMPTY_DEADLINE_MESSAGE = "Deadline Tasks Page: Empty";
	private static final String SEARCH_MESSAGE = "Search term(s) : ";
	private static final String SEARCH_EMPTY_MESSAGE = "There is no task with \"";
	private static final String LINE_BREAK = "<br>&emsp;&emsp;";
	private static final String EXIT_PROGRAM = "exit";

	//Program Variables
	boolean listCmdMultipleDay = false;
	boolean isCtrlPressed = false;
	boolean isPgDownPressed = false;
	boolean isPgUpPressed = false;
	boolean isSearchCmd = false;

	private JFrame frame;
	private JTextField textField;

	private int deadlinePage = 1;
	private int normalTaskPage = 1;
	private int floatingPage = 1;
	private int charPerLine = 100;
	private int taskbarHeight = 45;
	private int padding = 5;
	private int searchCharacterLength = 60;
	private int searchCharacterFeedbackLength = 40;
	private int normalPaneFullSize = 5;
	private int deadlinePaneFullSize = 2;
	private int floatingPaneFullSize = 4;
	private int requiredLength = 50;
	private int increment = 50;

	private List<Task> deadlineTasks;
	private List<Task> normalTasks;
	private List<Task> floatingTasks;

	private JList<String> floatingList = new JList<String>();
	private JList<String> deadlineList = new JList<String>();
	private JList<String> normalList = new JList<String>();

	private JLabel lblFeedback = new JLabel(" ");
	private JLabel lblDeadlinePage = new JLabel(" ");
	private JLabel lblNormalTaskPage = new JLabel(" ");
	private JLabel lblFloatingPage = new JLabel(" ");
	private JLabel lblListAndSearch = new JLabel(" ");
	private JLabel lblLegend = new JLabel(" ");
	private JLabel lblHelp = new JLabel(" ");

	private static final Logger Log = LoggerPreset.getLogger();

	private Logic logic;

	private BalloonTip floatingBalloonTip = new BalloonTip(lblFloatingPage, "");
	private BalloonTip deadlineBalloonTip = new BalloonTip(lblDeadlinePage, "");
	private BalloonTip normalBalloonTip = new BalloonTip(lblNormalTaskPage, "");

	private DefaultListModel<String> normalModel = new DefaultListModel<String>();
	private DefaultListModel<String> floatingModel = new DefaultListModel<String>();
	private DefaultListModel<String> deadlineModel = new DefaultListModel<String>();

	private JScrollPane normalListPane;
	private JScrollPane deadlineListPane;
	private JScrollPane floatingListPane;

	/**
	 *User Interface top level of abstraction
	 *
	 *@param logic is the logic component which will interact with UI
	 */
	public UserInterface(Logic logic) {

		linkLogicToUI(logic);
		initializeGUI();
		initializeJNativeHook();
		listTodayTasks(logic);
		initializeSystemTrayIcon();
	}

	/**
	 *This operation lists the tasks for today
	 */
	private void listTodayTasks(Logic logic) {
		logic.listTasks(TimeSpanHelper.createDaySpan(new Date()));
		Log.info("UI begin listing of today's task");
	}

	/**
	 *This operation handles the interaction between logic and UserInterface
	 */
	private void linkLogicToUI(Logic logic) {
		this.logic = logic;
		logic.addDisplayStateListener(this);
		Log.info("Added Display State Listener");
	}

	/**
	 *Highest level of Abstraction for implementing JNativeHook
	 */
	private void initializeJNativeHook() {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				initGlobalListener();
			}
		});
	}

	/**
	 *Actual implementation of JNativeHook
	 */
	private void initGlobalListener() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			Log.severe("JNativeHook not registered.");
		}

		GlobalScreen.getInstance().addNativeKeyListener(this);
	}

	/**
	 *This operation detects Page Up and Page Down buttons released
	 */
	public void nativeKeyReleased(NativeKeyEvent e) {

		if (e.getKeyCode() == NativeKeyEvent.VK_PAGE_DOWN) {
			releasePageDown();
		}

		if (e.getKeyCode() == NativeKeyEvent.VK_PAGE_UP) {
			releasePageUp();
		}
	}

	/**
	 *This operation detects Ctrl button pressed
	 */
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VK_CONTROL) {
			pressCtrl();
		}
	}

	/**
	 *This operation maximize the application 
	 *when ctrl button is pressed and page up button is released  
	 */
	private void releasePageUp() {

		isPgUpPressed = true;

		if (isPgUpPressed && isCtrlPressed) {
			displayUI();
			isPgUpPressed = false;
			isCtrlPressed = false;
		}
	}

	/**
	 *This operation minimize the application 
	 *when ctrl button is pressed and page down button is released  
	 */
	private void releasePageDown() {

		isPgDownPressed = true;

		if (isPgDownPressed && isCtrlPressed) {
			hideUI();
			isPgDownPressed = false;
			isCtrlPressed = false;
		}
	}

	/**
	 *This operation is mandatory for the implementation of JNativeHook
	 */
	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {}

	/**
	 *This operation minimizes the program
	 */
	private void hideUI() {
		frame.setVisible(false);
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

	private void pressCtrl() {
		isCtrlPressed = true;
	}

	/**
	 *GUI top level of abstraction
	 */
	private void initializeGUI() {

		JFrame.setDefaultLookAndFeelDecorated(true);
		initializeGUIComponents();
		setupLabels();
		addAllListeners();
		processTextField();
		addComponentsToPane(frame.getContentPane());
		setupFrame();
		Log.info("Finishing initializing GUI");
	}

	/**
	 *Top level abstraction to handle the listeners of the jList
	 */
	private void addAllListeners() {
		addDeadlineListListeners();
		addFloatingListListeners();
		addNormalListListeners();
		Log.info("Added listeners to all list");
	}

	/**
	 *Top level abstraction to setup the different labels
	 */
	private void setupLabels() {
		setupLblHelp();
		setupLblLegend();
		setupLblFeedback();
	}

	/**
	 *Normal List Top level listener abstraction
	 */
	private void addNormalListListeners() {
		normalListAddFocusListener();
		normalListAddKeyListener();
		normalListAddListSelectionListener();	
	}

	/**
	 *Floating List Top level listener abstraction
	 */
	private void addFloatingListListeners() {
		floatingListAddFocusListener();
		floatingListAddListSelectionListener();
		floatingAddKeyListener();
	}

	/**
	 *Deadline list top level abstraction
	 */
	private void addDeadlineListListeners() {
		deadlineListAddFocusListener();
		deadlineListAddListSelectionListener();
		deadlineAddKeyListener();
	}

	/**
	 *This operation handles the normal list selection listener
	 */
	private void normalListAddListSelectionListener() {
		normalList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				if (!arg0.getValueIsAdjusting()
						&& normalList.getSelectedIndex() != -1) {

					int index = normalList.getSelectedIndex() + (normalTaskPage - 1) * normalPaneFullSize;

					normalBalloonTip.setCloseButton(null);

					String taskContent = "";

					SimpleDateFormat firstPartDayFormat = new SimpleDateFormat(
							"EEE dd.MMM.yy HH:mm");

					SimpleDateFormat secondPartDayFormat = new SimpleDateFormat(
							"HH:mm");

					String task = "";
					String date = "";

					if (normalTasks.get(index).isFullDay()) {

						date = "[whole day] <br>";
						task =  normalTasks.get(index).getName();

					} else {

						date = "["
								+ firstPartDayFormat.format(
										normalTasks.get(index).getStartTime())
										+ " to "
										+ secondPartDayFormat.format(
												normalTasks.get(index).getEndTime()) 
												+ "] <br>";
						task =  normalTasks.get(index).getName();
					}

					StringTokenizer taskStrTokenizer = new StringTokenizer(task);

					while (taskContent.length() < requiredLength
							&& taskStrTokenizer.hasMoreTokens() != false) {

						String token = taskStrTokenizer.nextToken();
						if ((taskContent.concat(token).length()) < requiredLength
								|| taskContent.length() == 0) {
							taskContent = taskContent.concat(token + " ");
						} else {
							taskContent = taskContent.concat("<br>" + token
									+ " ");
							requiredLength += increment;
						}
					}

					taskContent = date + task;

					setupBalloonProperties(normalBalloonTip, normalList, taskContent, 33, 0 );
				}
			}
		});
	}

	/**
	 *This operation handles the normal list key listener
	 */
	private void normalListAddKeyListener() {
		normalList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				int normalTaskSize = getNormalTaskSize();

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					pressLeftKeyNormalList(normalTaskSize);
				}

				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					pressRightKeyNormalList(normalTaskSize);
				}
			}

			//press right arrow key to go next page
			private void pressRightKeyNormalList(int normalTaskSize) {
				if (++normalTaskPage <= normalTaskSize) {
					normalBalloonTip.setVisible(false);
					lblNormalTaskPage.setText("Normal Tasks Page: "
							+ normalTaskPage + " of " + normalTaskSize);

					populateTxtPnNormal();

				} else {
					--normalTaskPage;
				}
			}

			//press left arrow key to go previous page
			private void pressLeftKeyNormalList(int normalTaskSize ) {
				if (--normalTaskPage > 0
						&& normalTaskPage <= normalTaskSize) {
					normalBalloonTip.setVisible(false);
					lblNormalTaskPage.setText("Normal Tasks Page: "
							+ normalTaskPage + " of " + normalTaskSize);

					populateTxtPnNormal();

				} else {
					++normalTaskPage;
				}
			}

			private int getNormalTaskSize() {
				int normalTaskSize = normalTasks.size() % normalPaneFullSize;

				if (normalTaskSize != 0) {
					normalTaskSize = normalTasks.size() / normalPaneFullSize + 1;
				} else {
					normalTaskSize = normalTasks.size() / normalPaneFullSize;
				}

				return normalTaskSize;
			}
		});
	}

	/**
	 *This operation handles normal list focus listener
	 */
	private void normalListAddFocusListener() {

		normalList.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent arg0) {
				gainListFocus(normalList,normalBalloonTip );
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				lostListFocus(normalList, normalBalloonTip );
			}
		});
	}

	public void show(boolean b) {
		frame.setVisible(b);
	}

	/**
	 *This operation handles the text field
	 */
	private void processTextField() {
		textField = new JTextField();

		TextFieldLengthChecker textFieldLengthChecker = new TextFieldLengthChecker();
		textField.setDocument(textFieldLengthChecker);

		textField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg) {

				lblFeedback.setText(logic.getTipMessage(textField.getText()));

				if (textField.getText().length() > 0
						&& arg.getKeyCode() == KeyEvent.VK_ENTER) {

					try {
						
						if (textField.getText().equalsIgnoreCase(EXIT_PROGRAM)){
							System.exit(0);
						}
						
						Result result = logic.executeCommand(textField.getText());

						if (result.getActionPerformed() == Action.ADD) {
							String addedTextFeedback = result.getFirstResult().getName() + " is added.";
							if (addedTextFeedback.length() < 50) {
								lblFeedback.setText(result.getFirstResult()
										.getName() + " is added.");
							} else {
								lblFeedback.setText(result.getFirstResult()
										.getName().substring(0, 50)
										+ "... is added.");
							}
						} else if (result.getActionPerformed() == Action.DELETE) {

							String deletedTextFeedback = result
									.getFirstResult().getName()
									+ " is deleted.";

							if (deletedTextFeedback.length() < 50) {
								lblFeedback.setText(result.getFirstResult()
										.getName() + " is deleted.");
							} else {
								lblFeedback.setText(result.getFirstResult()
										.getName().substring(0, 50)
										+ "... is deleted.");
							}

						} 

						textField.setText("");

					} catch (Exception ex) {
						lblFeedback.setText(ex.getMessage());
					}
				}

				lblHelp.setText(logic.getGuideMessage(textField.getText()));

				if (arg.getKeyCode() == KeyEvent.VK_SPACE) {
					try {
						String str = logic.autofill(textField.getText());
						int currPosition = textField.getCaretPosition();
						textField.setText(str);
						textField.setCaretPosition(currPosition);
					} catch (Exception ex) {
						lblFeedback.setText(ex.getMessage());
					}
				}
			}
		});
	}

	/**
	 *This operation handles the list gaining focus and balloontip becoming invisible
	 */
	private void gainListFocus(JList<String> list, BalloonTip balloonTip){
		list.clearSelection();
		list.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
		balloonTip.setVisible(false);
	}

	/**
	 *This operation handles the list losing focus and balloontip becoming invisible
	 */
	private void lostListFocus(JList<String> list, BalloonTip balloonTip){
		list.clearSelection();
		list.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		balloonTip.setVisible(false);
	}

	/**
	 *This operation handles the floating list key listener
	 */
	private void floatingAddKeyListener() {
		floatingList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				int floatingTaskSize = floatingTasks.size() % floatingPaneFullSize;

				if (floatingTaskSize != 0) {
					floatingTaskSize = floatingTasks.size() / floatingPaneFullSize + 1;
				} else {
					floatingTaskSize = floatingTasks.size() / floatingPaneFullSize;
				}

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					if (--floatingPage > 0 && floatingPage <= floatingTaskSize) {
						floatingBalloonTip.setVisible(false);
						lblFloatingPage.setText("Floating Tasks Page: "
								+ floatingPage + " of " + floatingTaskSize);

						beginProcessTextPaneFloating();

					} else {
						++floatingPage;
					}
				}

				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (++floatingPage <= floatingTaskSize) {
						floatingBalloonTip.setVisible(false);
						lblFloatingPage.setText("Floating Tasks Page: "
								+ floatingPage + " of " + floatingTaskSize);

						beginProcessTextPaneFloating();

					} else {
						--floatingPage;
					}
				}
			}
		});
	}

	/**
	 *This operation handles the floating list list selection listener
	 */
	private void floatingListAddListSelectionListener() {
		floatingList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()
						&& floatingList.getSelectedIndex() != -1) {
					int index = floatingList.getSelectedIndex()
							+ (floatingPage - 1) * floatingPaneFullSize;

					floatingBalloonTip.setCloseButton(null);

					String taskContent = "";

					StringTokenizer taskStrTokenizer = new StringTokenizer(
							floatingTasks.get(index).getName());
					 
					while (taskContent.length() < requiredLength
							&& taskStrTokenizer.hasMoreTokens() != false) {

						String token = taskStrTokenizer.nextToken();
						if ((taskContent.concat(token).length()) < requiredLength
								|| taskContent.length() == 0) {
							taskContent = taskContent.concat(token + " ");
						} else {
							taskContent = taskContent.concat("<br>" + token
									+ " ");
							requiredLength += increment;
						}
					}

					setupBalloonProperties(floatingBalloonTip, floatingList, taskContent, 18, 365 );
				}
			}
		});
	}
	
	/**
	 *This operation handles the floating list focus listener
	 */
	private void floatingListAddFocusListener() {

		floatingList.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent arg0) {
				gainListFocus(floatingList,floatingBalloonTip );
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				lostListFocus(floatingList, floatingBalloonTip );
			}
		});
	}

	/**
	 *This operation handles the deadline list key listener
	 */
	private void deadlineAddKeyListener() {
		deadlineList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				int deadlineTaskSize = deadlineTasks.size() % deadlinePaneFullSize;

				if (deadlineTaskSize != 0) {
					deadlineTaskSize = deadlineTasks.size() / deadlinePaneFullSize + 1;
				} else {
					deadlineTaskSize = deadlineTasks.size() / deadlinePaneFullSize;
				}

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {

					if (--deadlinePage > 0 && deadlinePage <= deadlineTaskSize) {
						deadlineBalloonTip.setVisible(false);
						lblDeadlinePage.setText("Deadline Tasks Page: "
								+ deadlinePage + " of " + deadlineTaskSize);
						populateTxtPnDeadline();

					} else {
						++deadlinePage;
					}
				}

				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (++deadlinePage <= deadlineTaskSize) {
						deadlineBalloonTip.setVisible(false);
						lblDeadlinePage.setText("Deadline Tasks Page: "
								+ deadlinePage + " of " + deadlineTaskSize);

						populateTxtPnDeadline();

					} else {
						--deadlinePage;
					}
				}
			}
		});
	}

	/**
	 *This operation handles the deadline list selection listener
	 */
	private void deadlineListAddListSelectionListener() {
		deadlineList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()
						&& deadlineList.getSelectedIndex() != -1) {
					int index = deadlineList.getSelectedIndex()
							+ (deadlinePage - 1) * deadlinePaneFullSize;

					deadlineBalloonTip.setCloseButton(null);

					SimpleDateFormat format = new SimpleDateFormat(
							"EEE dd.MMM.yy HH:mm");

					String taskContent = "";
					String date = "["
							+ format.format(
									deadlineTasks.get(index).getEndTime()) 
									+ "] <br>";
					String task = deadlineTasks.get(index).getName();

					StringTokenizer taskStrTokenizer = new StringTokenizer(task);

					while (taskContent.length() < requiredLength
							&& taskStrTokenizer.hasMoreTokens() != false) {

						String token = taskStrTokenizer.nextToken();
						if ((taskContent.concat(token).length()) < requiredLength
								|| taskContent.length() == 0) {
							taskContent = taskContent.concat(token + " ");
						} else {
							taskContent = taskContent.concat("<br>" + token
									+ " ");
							requiredLength += increment;
						}
					}

					taskContent = date + task;

					setupBalloonProperties(deadlineBalloonTip, deadlineList, taskContent, 30, 225 );
				}
			}
		});	
	}

	/**
	 *This operation handles the deadline list focus listener
	 */
	private void deadlineListAddFocusListener() {
		deadlineList.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent arg0) {
				gainListFocus(deadlineList, deadlineBalloonTip);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				lostListFocus(deadlineList, deadlineBalloonTip);
			}
		});
	}

	/**
	 *This operation handles the properties of the balloon
	 */
	protected void setupBalloonProperties(BalloonTip balloonTip, JList<String> list,
			String taskContent, int multiplier, int addition) {

		balloonTip.setPadding(padding);
		balloonTip.setTextContents("<html> " + taskContent + "</html>");
		balloonTip.setVisible(true);
		balloonTip.setLocation(deadlineBalloonTip.getX(),
				(list.getSelectedIndex() + 1) * multiplier + addition);
		balloonTip.getStyle().flipY(false);
	}

	/**
	 *This operation handles the properties of frame
	 */
	private void setupFrame() {

		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		addFrameWindowFocusListener();
		setupFrameLocation();
	}

	/**
	 *This operation sets the program at the 
	 *bottom right hand corner of screen
	 */
	private void setupFrameLocation() {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		int Xcoordinate = (int) rect.getMaxX() - frame.getWidth();
		int Ycoordinate = (int) rect.getMaxY() - frame.getHeight() - taskbarHeight;
		frame.setLocation(Xcoordinate, Ycoordinate);
	}

	/**
	 *This operation processes the visual of 
	 *feedback label
	 */
	private void setupLblFeedback() {
		lblFeedback.setForeground(Color.red);
		lblFeedback.setFont(new Font("Times New Roman", Font.ITALIC, 12));
	}

	/**
	 *This operation puts the focus on the textField 
	 *for the user to type immediately when the program runs 
	 */
	private void addFrameWindowFocusListener() {
		frame.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent arg0) {

				textField.requestFocusInWindow();
			}

			//mandatory method for WindowFocusListener
			public void windowLostFocus(WindowEvent arg0) {}
		});
	}

	/**
	 *This operation handles the text in Legend label
	 */
	private void setupLblLegend() {
		lblLegend.setText("<html>Legend: <font color=red>Task Overdue</font>-"
				+ "<font color=black>Task Ongoing</font>-"
				+ "<font color=green>Task Finished</font></html>");
	}

	/**
	 *This operation handles the text in Help label
	 */
	private void setupLblHelp() {
		lblHelp.setText(logic.getGuideMessage(""));
	}

	/**
	 *This operation handles the components
	 *that need to be initialized
	 */
	private void initializeGUIComponents() {

		frame = new JFrame("Task Butler");

		deadlineTasks = new ArrayList<Task>();
		floatingTasks = new ArrayList<Task>();
		normalTasks = new ArrayList<Task>();

		normalBalloonTip.setVisible(false);
		deadlineBalloonTip.setVisible(false);
		floatingBalloonTip.setVisible(false);

		normalListPane = new JScrollPane(normalList);
		deadlineListPane = new JScrollPane(deadlineList);
		floatingListPane = new JScrollPane(floatingList);

		floatingListPane.setHorizontalScrollBar(null);
		deadlineListPane.setHorizontalScrollBar(null);
		normalListPane.setHorizontalScrollBar(null);
	}

	/**
	 *Highest level of abstraction for System Tray Icon 
	 */
	private void initializeSystemTrayIcon() {
		TrayIconForSystemTray trayIconForSystemTray = new TrayIconForSystemTray(frame);
	}

	/**
	 *The operation handles the layout 
	 *of all the components 
	 */
	private void addComponentsToPane(Container contentPane) {

		contentPane.setLayout(new GridBagLayout());

		GridBagConstraints gBC = new GridBagConstraints();
		gBC.fill = GridBagConstraints.HORIZONTAL;
		gBC.insets = new Insets(5, 5, 5, 5);

		gBC.gridx = 0;
		gBC.gridy = 0;
		contentPane.add(lblListAndSearch, gBC);

		gBC.gridx = 0;
		gBC.gridy = 1;
		contentPane.add(lblNormalTaskPage, gBC);

		gBC.gridx = 0;
		gBC.gridy = 2;
		gBC.weightx = 1.71;
		gBC.weighty = 1.71;
		gBC.fill = GridBagConstraints.BOTH;
		normalListPane.setPreferredSize(new Dimension(400, 190));
		contentPane.add(normalListPane, gBC);

		gBC.gridx = 0;
		gBC.gridy = 3;
		gBC.weightx = 0;
		gBC.weighty = 0;
		gBC.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(lblDeadlinePage, gBC);

		gBC.gridx = 0;
		gBC.gridy = 4;
		gBC.weightx = 1;
		gBC.weighty = 1;
		gBC.fill = GridBagConstraints.BOTH;
		deadlineListPane.setPreferredSize(new Dimension(400, 85));
		contentPane.add(deadlineListPane, gBC);

		gBC.gridx = 0;
		gBC.gridy = 5;
		gBC.weightx = 0;
		gBC.weighty = 0;
		gBC.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(lblFloatingPage, gBC);

		gBC.gridx = 0;
		gBC.gridy = 6;
		gBC.weightx = 1;
		gBC.weighty = 1;
		gBC.fill = GridBagConstraints.BOTH;
		floatingListPane.setPreferredSize(new Dimension(400, 85));
		contentPane.add(floatingListPane, gBC);

		gBC.gridx = 0;
		gBC.gridy = 7;
		gBC.weightx = 0;
		gBC.weighty = 0;
		gBC.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(lblFeedback, gBC);

		gBC.gridx = 0;
		gBC.gridy = 8;
		contentPane.add(textField, gBC);

		gBC.gridx = 0;
		gBC.gridy = 9;
		gBC.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(lblLegend, gBC);

		gBC.gridx = 0;
		gBC.gridy = 10;
		gBC.gridheight = 1;
		gBC.fill = GridBagConstraints.BOTH;
		lblHelp.setPreferredSize(new Dimension(400, 65));
		contentPane.add(lblHelp, gBC);

	}

	/**
	 *This operation handles the processing of floating list text pane
	 */
	private void beginProcessTextPaneFloating() {

		if (floatingTasks.size() > 0) {

			floatingPage--;
			floatingModel.removeAllElements();

			populateFloatingPane();

			floatingList.setModel(floatingModel);
			floatingPage++;
		}
	}
	
	/**
	 *This operation handles the populating of floating list text pane
	 */
	private void populateFloatingPane() {
		try {
			for (int i = floatingPage * floatingPaneFullSize; i < (floatingPage + 1) * floatingPaneFullSize; i++) {

				if (floatingTasks.get(i).getName().length() < charPerLine) {
					floatingModel.addElement("F" + (i + 1) + ". "
							+ floatingTasks.get(i).getName());
				} else {
					floatingModel.addElement("F"
							+ (i + 1)
							+ ". "
							+ floatingTasks.get(i).getName()
							.substring(0, charPerLine) + "...");
				}
			}
		} catch (Exception e) {
			Log.severe("An error occured in populating floating tasks.");
		}
	}

	/**
	 *This operation returns the floatingModel to be used in junit testing
	 *@return floatingModel which holds floating tasks
	 */
	public DefaultListModel<String> getFloatingModel() {
		return floatingModel;
	}

	/**
	 *This operation returns the normalModel to be used in junit testing
	 *@return normalModel which holds normal tasks
	 */
	public DefaultListModel<String> getNormalModel() {
		return normalModel;
	}

	/**
	 *This operation returns the deadlineModel to be used in junit testing
	 *@return deadlineModel which holds deadline tasks
	 */
	public DefaultListModel<String> getDeadlineModel() {
		return deadlineModel;
	}

	private void populateTxtPnNormal() {

		if (normalTasks.size() > 0) {

			normalTaskPage--;
			normalModel.removeAllElements();

			try {
				for (int i = normalTaskPage * normalPaneFullSize; i < (normalTaskPage + 1) * normalPaneFullSize; i++) {

					String task = "";

					if (listCmdMultipleDay) {

						if (normalTasks.get(i).isFullDay()) {

							SimpleDateFormat format = new SimpleDateFormat(
									"EEE dd.MMM.yy");

							task = "<html>T"
									+ (i + 1)
									+ ". ["
									+ format.format(normalTasks.get(i)
											.getStartTime()) + "] [whole day]"
											+ LINE_BREAK + normalTasks.get(i).getName();

						} else {

							SimpleDateFormat multipleDaysFormat = new SimpleDateFormat(
									"EEE dd.MMM.yy HH:mm");

							SimpleDateFormat normalDayFormat = new SimpleDateFormat(
									"HH:mm");

							task = "<html>T"
									+ (i + 1)
									+ ". ["
									+ multipleDaysFormat.format(normalTasks
											.get(i).getStartTime())
											+ " - "
											+ normalDayFormat.format(normalTasks.get(i)
													.getEndTime()) + "]" + LINE_BREAK
													+ normalTasks.get(i).getName();
						}
					}else{
						
						if (normalTasks.get(i).isFullDay()) {

							if (isSearchCmd){

								SimpleDateFormat format = new SimpleDateFormat(
										"EEE dd.MMM.yy");

								task = "<html>T"
										+ (i + 1)
										+ ". ["
										+ format.format(normalTasks.get(i)
												.getStartTime()) + "] [whole day]"
												+ LINE_BREAK + normalTasks.get(i).getName();	

							} else {

								task = "<html>T"
										+ (i + 1)
										+ ". [whole day]" + LINE_BREAK + 
										normalTasks.get(i).getName();	
							}

						} else {

							if (isSearchCmd){

								SimpleDateFormat multipleDaysFormat = new SimpleDateFormat(
										"EEE dd.MMM.yy HH:mm");

								SimpleDateFormat normalDayFormat = new SimpleDateFormat(
										"HH:mm");

								task = "<html>T"
										+ (i + 1)
										+ ". ["
										+ multipleDaysFormat.format(normalTasks
												.get(i).getStartTime())
												+ " - "
												+ normalDayFormat.format(normalTasks.get(i)
														.getEndTime()) + "]" + LINE_BREAK
														+ normalTasks.get(i).getName();
							}else{
								SimpleDateFormat normalDayFormat = new SimpleDateFormat(
										"HH:mm");

								task = "<html>T"
										+ (i + 1)
										+ ". ["
										+ normalDayFormat.format(normalTasks.get(i)
												.getStartTime())
												+ " - "
												+ normalDayFormat.format(normalTasks.get(i)
														.getEndTime()) + "]" + LINE_BREAK
														+ normalTasks.get(i).getName();
							}
						}
					}

					if (task.length() < charPerLine) {
						normalModel.addElement(task);

					} else {
						normalModel.addElement(task.substring(0,
								charPerLine) + "...");
					}
				}
			} catch (Exception e) {

			}
			normalList.setModel(normalModel);
			normalTaskPage++;
			normalList.setCellRenderer(new ListRenderer(normalTasks,
					normalTaskPage, normalPaneFullSize));
		}
	}

	private void populateTxtPnDeadline() {

		if (deadlineTasks.size() > 0) {
			deadlinePage--;
			deadlineModel.removeAllElements();

			try {

				for (int i = deadlinePage * deadlinePaneFullSize; i < (deadlinePage + 1) * deadlinePaneFullSize; i++) {

					SimpleDateFormat format = new SimpleDateFormat(
							"EEE dd.MMM.yy HH:mm");

					String task = "<html>D" + (i + 1) + ". ["
							+ format.format(deadlineTasks.get(i).getEndTime())
							+ "]" + LINE_BREAK + deadlineTasks.get(i).getName() + "</html>";

					if (task.length() < charPerLine) {
						deadlineModel.addElement(task);
					} else {
						deadlineModel.addElement(task.substring(0, charPerLine)
								+ "...");
					}
				}
			} catch (Exception e) {
				Log.severe("An error occured in populating deadline tasks.");
			}
			deadlineList.setModel(deadlineModel);
			deadlinePage++;
			deadlineList.setCellRenderer(new ListRenderer(deadlineTasks,
					deadlinePage, deadlinePaneFullSize));
		}
	}

	/**
	 *Top level abstraction for DisplayModeChange
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void modeChanged(DisplayModeChangedEvent evt) {

		if (evt.getModeChanged() == DisplayMode.LIST) {
			processListDisplayMode(evt);
		} else if (evt.getModeChanged() == DisplayMode.SEARCH) {
			processSearchDisplayMode(evt);
		}
	}

	/**
	 *Top level abstraction for Search Display Mode
	 */
	private void processSearchDisplayMode(DisplayModeChangedEvent evt) {
		String searchFeedback = SEARCH_MESSAGE + evt.getSearchTerms();
		setupLabelForSearch(searchFeedback, evt);
		setuplblFeedbackWithSearch(searchFeedback, evt);
		isSearchCmd = true;
	}

	/**
	 *This operation handles the content of feedback label
	 *when Search command is used
	 */
	private void setuplblFeedbackWithSearch(String searchFeedback, DisplayModeChangedEvent evt) {
		if (normalTasks.size() == 0 && deadlineTasks.size() == 0
				&& floatingTasks.size() == 0) {

			searchFeedback = SEARCH_EMPTY_MESSAGE + evt.getSearchTerms() + "\"";

			if (searchFeedback.length() < searchCharacterFeedbackLength) {
				lblFeedback.setText( SEARCH_EMPTY_MESSAGE + evt.getSearchTerms() + "\"");
			} else {
				lblFeedback.setText( SEARCH_EMPTY_MESSAGE + evt.getSearchTerms().substring(0, searchCharacterFeedbackLength) + "...\".");
			}
		}
	}

	/**
	 *This operation handles the content of lblListAndSearch label
	 *when Search command is used
	 */
	private void setupLabelForSearch(String searchFeedback, DisplayModeChangedEvent evt) {
		if (searchFeedback.length() < searchCharacterLength) {
			lblListAndSearch.setText(SEARCH_MESSAGE
					+ evt.getSearchTerms());
		} else {
			lblListAndSearch.setText(SEARCH_MESSAGE
					+ evt.getSearchTerms().substring(0, searchCharacterLength) + "...");
		}

	}

	/**
	 *Top level abstraction for List Display Mode
	 */
	private void processListDisplayMode(DisplayModeChangedEvent evt) {

		if (evt.getTimeSpan().isFullDayInterval()) {
			setupLabelForListFullDay(evt);
		} else {
			setupLabelForListMultipleDay(evt);
		}

		//list command checks if the input span across multiple days
		listCmdMultipleDay = evt.getTimeSpan().isSpanningMultipleDays();

	}

	/**
	 *This operation handles the content of lblListAndSearch
	 *when the List command spans across multiple days
	 */
	private void setupLabelForListMultipleDay(DisplayModeChangedEvent evt) {

		SimpleDateFormat startTimeFormat = new SimpleDateFormat("EEE dd.MMM.yy");

		DateTime endTime = new DateTime(evt.getTimeSpan().getEndTime());
		endTime = endTime.minusSeconds(1);

		lblListAndSearch.setText("You are viewing ["
				+ startTimeFormat.format(evt.getTimeSpan().getStartTime())
				+ "] to ["
				+ endTime.dayOfWeek().getAsShortText() + " " + endTime.dayOfMonth().get() + "." 
				+ endTime.monthOfYear().get() + "." + endTime.year().get() + "]");
	}

	/**
	 *This operation handles the content of lblListAndSearch
	 *when the List command list a day's task
	 */
	private void setupLabelForListFullDay(DisplayModeChangedEvent evt) {
		SimpleDateFormat startTimeFormat = new SimpleDateFormat("EEE dd.MMM.yy");
		lblListAndSearch.setText("You are viewing ["
				+ startTimeFormat.format(evt.getTimeSpan().getStartTime())+ "]");
	}

	/**
	 *Top Level abstraction of DisplayListChanged event
	 */
	@Override
	public void listChanged(DisplayListChangedEvent evt) {

		if (evt.getListChanged() == DisplayListType.DEADLINE) {

			handleDeadlineList(evt);

		} else if (evt.getListChanged() == DisplayListType.FLOATING) {

			handleFloatingList(evt);

		} else {

			handleNormalDisplayList(evt);
		}
	}

	/**
	 *Top level of abstraction for handling normal list
	 */
	private void handleNormalDisplayList(DisplayListChangedEvent evt) {

		normalTaskPage = 1;
		normalTasks = evt.getDisplayList();

		if (normalTasks.size() == 0) {

			setupEmptyLblNormalTaskPage();

		} else {

			setupLblNormalTaskPage();

		}

		populateTxtPnNormal();
	}

	/**
	 *This operation handles the content of lblNormalTaskPage
	 *when normalTasks is empty
	 */
	private void setupEmptyLblNormalTaskPage() {
		lblNormalTaskPage.setText(EMPTY_NORMAL_MESSAGE);
		normalModel.clear();
	}

	/**
	 *This operation handles the content of lblNormalTaskPage
	 *depending on size of normalTasks
	 */
	private void setupLblNormalTaskPage() {

		int normalTaskSize = normalTasks.size() % normalPaneFullSize;

		if (normalTaskSize != 0) {
			normalTaskSize = normalTasks.size() / normalPaneFullSize + 1;
		} else {
			normalTaskSize = normalTasks.size() / normalPaneFullSize;
		}

		lblNormalTaskPage.setText("Normal Tasks Page: "
				+ normalTaskPage + " of " + normalTaskSize);
	}

	/**
	 *Top level of abstraction for handling floating list
	 */
	private void handleFloatingList(DisplayListChangedEvent evt) {

		floatingPage = 1;
		floatingTasks = evt.getDisplayList();

		if (floatingTasks.size() == 0) {

			setupEmptyLblFloatingPage();

		} else {

			setupLblFLoatingTaskPage();
		}

		beginProcessTextPaneFloating();
	}

	/**
	 *This operation handles the content of lblFloatingPage
	 *depending on size of floatingTasks
	 */
	private void setupLblFLoatingTaskPage() {

		int floatingTaskSize = floatingTasks.size() % floatingPaneFullSize;

		if (floatingTaskSize != 0) {
			floatingTaskSize = floatingTasks.size() / floatingPaneFullSize + 1;
		} else {
			floatingTaskSize = floatingTasks.size() / floatingPaneFullSize;
		}

		lblFloatingPage.setText("Floating Tasks Page: " + floatingPage
				+ " of " + floatingTaskSize);
	}

	/**
	 *This operation handles the content of lblFloatingPage
	 *when floatingTasks is empty
	 */
	private void setupEmptyLblFloatingPage() {
		lblFloatingPage.setText(EMPTY_FLOATING_MESSAGE);
		floatingModel.clear();
	}

	/**
	 *Top level of abstraction for handling deadline list
	 */
	private void handleDeadlineList(DisplayListChangedEvent evt) {

		deadlinePage = 1;

		deadlineTasks = evt.getDisplayList();

		if (deadlineTasks.size() == 0) {
			setupEmptyLblDeadlinePage();

		} else {
			setupLblDeadlinePage();
		}

		populateTxtPnDeadline();
	}

	/**
	 *This operation handles the content of lblDeadlinePage
	 *depending on size of deadlineTasks
	 */
	private void setupLblDeadlinePage() {

		int deadLineTaskSize = deadlineTasks.size() % deadlinePaneFullSize;

		if (deadLineTaskSize != 0)
			deadLineTaskSize = deadlineTasks.size() / deadlinePaneFullSize + 1;
		else
			deadLineTaskSize = deadlineTasks.size() / deadlinePaneFullSize;

		lblDeadlinePage.setText("Deadline Tasks Page: " + deadlinePage
				+ " of " + deadLineTaskSize);
	}

	/**
	 *This operation handles the content of lblDeadlinePage
	 *when deadlineTasks is empty
	 */
	private void setupEmptyLblDeadlinePage() {
		lblDeadlinePage.setText(EMPTY_DEADLINE_MESSAGE);
		deadlineModel.clear();
	}
}


