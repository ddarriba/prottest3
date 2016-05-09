/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package es.uvigo.darwin.xprottest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.plaf.BorderUIResource;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;

import pal.alignment.Alignment;
import pal.tree.Tree;
import es.uvigo.darwin.prottest.ProtTest;
import es.uvigo.darwin.prottest.facade.ProtTestFacade;
import es.uvigo.darwin.prottest.facade.TreeFacade;
import es.uvigo.darwin.prottest.facade.TreeFacadeImpl;
import es.uvigo.darwin.prottest.global.options.ApplicationOptions;
import es.uvigo.darwin.prottest.model.Model;
import es.uvigo.darwin.prottest.util.ProtTestAlignment;
import es.uvigo.darwin.prottest.util.exception.AlignmentParseException;
import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;
import es.uvigo.darwin.prottest.util.factory.ProtTestFactory;
import es.uvigo.darwin.prottest.util.logging.ProtTestLogger;
import es.uvigo.darwin.prottest.util.printer.ProtTestFormattedOutput;
import es.uvigo.darwin.prottest.util.printer.ProtTestPrinter;
import es.uvigo.darwin.xprottest.analysis.FrequenciesView;
import es.uvigo.darwin.xprottest.analysis.TreeView;
import es.uvigo.darwin.xprottest.analysis.consensus.Consensus;
import es.uvigo.darwin.xprottest.compute.OptionsView;
import es.uvigo.darwin.xprottest.compute.RunningFrame;
import es.uvigo.darwin.xprottest.results.ErrorLogView;
import es.uvigo.darwin.xprottest.results.ResultsView;
import es.uvigo.darwin.xprottest.util.TextAreaAppender;

/**
 * The XProtTest main frame offers whole connection between the application
 * componentes providing the full functionality of ProtTest-HPC.
 * 
 * @author Diego Darriba
 */
public final class XProtTestView extends FrameView {

	/* Application colors */
	public static final Color NORMAL_COLOR = Color.BLACK;
	public static final Color CRITIC_COLOR = new Color(153, 0, 0);
	public static final Color DONE_COLOR = new Color(102, 102, 153);
	/* Settings */
	private int errorBehavior;
	/* General variables */
	private ResourceMap resourceMap;
	private Alignment alignment;
	private Tree tree;
	private ProtTestFacade prottestFacade;
	private boolean alignmentLoaded;
	private File alignmentFile;
	private boolean lnlCalculated;
	private Model[] models;
	private PrintWriter displayWriter;
	private Handler mainHandler;
	private PrintWriter lowLevelDisplayWriter;
	private Handler lowLevelHandler;
	/* WINDOWS */
	private ResultsView resultsView;
	private TreeView treeView;
	private Consensus consensusView;
	private FrequenciesView frequenciesView;
	private ErrorLogView errorLogView;

	public int getErrorBehavior() {
		return errorBehavior;
	}

	protected void setErrorBehavior(int errorBehavior) {
		this.errorBehavior = errorBehavior;
	}

	public ProtTestFacade getFacade() {
		return prottestFacade;
	}

	private void setAlignmentFile(File alignmentFile) {
		enableHandler();
		int numDecimals = 5;
		alignmentLoaded = (alignmentFile != null);
		if (alignmentLoaded) {
			lblDataFileStatus.setText(resourceMap.getString("msg-data-loaded")
					+ " " + alignmentFile.getName());
			lblDataFileStatus.setForeground(DONE_COLOR);
			this.alignmentFile = alignmentFile;
			showFrequenciesItem.setEnabled(true);
			double[] frequencies = ProtTestAlignment.getFrequencies(alignment);
			displayWriter.println("");
			displayWriter.println(resourceMap.getString("aa-frequencies"));
			displayWriter.println(ProtTestFormattedOutput.space(resourceMap
					.getString("aa-frequencies").length(), '-'));

			for (int i = 0; i < frequencies.length; i++) {
				displayWriter.println(ProtTestAlignment.charOfIndex(i)
						+ " - "
						+ ProtTestFormattedOutput.getDecimalString(
								frequencies[i], numDecimals));
			}
		} else {
			lblDataFileStatus.setText(resourceMap.getString("msg-no-data"));
			lblDataFileStatus.setForeground(CRITIC_COLOR);
			showFrequenciesItem.setEnabled(false);
		}
		frequenciesView = null;
		computeMenuItem.setEnabled(alignmentLoaded);
		resultsMenuItem.setEnabled(false);
		if (resultsView != null) {
			resultsView.dispose();
			resultsView = null;
		}
		showTreeMenuItem.setEnabled(false);
		if (treeView != null) {
			treeView.dispose();
			treeView = null;
		}
		averagingMenuItem.setEnabled(false);
		if (consensusView != null) {
			consensusView.dispose();
			consensusView = null;
		}
		disableHandler();
	}

	public PrintWriter getDisplayWriter() {
		return displayWriter;
	}

	public XProtTestView(SingleFrameApplication app, ProtTestFacade facade) {
		super(app);

		resourceMap = Application
				.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class)
				.getContext().getResourceMap(XProtTestView.class);

		this.errorBehavior = Application
				.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class)
				.getContext().getResourceMap(XProtTestApp.class)
				.getInteger("default-error-behavior");

		this.prottestFacade = facade;

		initComponents();

		Image image = new ImageIcon("icon.png").getImage();
	    getFrame().setIconImage(image);
	      
		displayWriter = new PrintWriter(new TextAreaAppender(mainTextArea));
		lowLevelDisplayWriter = new PrintWriter(new TextAreaAppender(
				phymlTextArea));

		mainHandler = ProtTestLogger.getDefaultLogger().addHandler(
				displayWriter);
		lowLevelHandler = ProtTestLogger.getDefaultLogger().addLowLevelHandler(
				lowLevelDisplayWriter);
		try {
			Handler logHandler = ProtTestFactory.getInstance()
					.createLogHandler();
			if (logHandler != null) {
				ProtTestLogger.getDefaultLogger().addHandler(logHandler);
			}
		} catch (IOException ex) {
			ProtTestLogger.getDefaultLogger().severeln(ex.getMessage());
		}
		// status bar initialization - message timeout, idle icon and busy
		// animation, etc

		int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
		messageTimer = new Timer(messageTimeout, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lblLikelihoodStatus.setText("");
			}
		});
		messageTimer.setRepeats(false);
		int busyAnimationRate = resourceMap
				.getInteger("StatusBar.busyAnimationRate");
		for (int i = 0; i < busyIcons.length; i++) {
			busyIcons[i] = resourceMap
					.getIcon("StatusBar.busyIcons[" + i + "]");
		}
		busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
				lblDataFileStatus.setIcon(busyIcons[busyIconIndex]);
			}
		});
		idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
		lblDataFileStatus.setIcon(idleIcon);

		TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
		taskMonitor
				.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

					@Override
					public void propertyChange(
							java.beans.PropertyChangeEvent evt) {
						String propertyName = evt.getPropertyName();
						if ("started".equals(propertyName)) {
							if (!busyIconTimer.isRunning()) {
								lblDataFileStatus.setIcon(busyIcons[0]);
								busyIconIndex = 0;
								busyIconTimer.start();
							}
						} else if ("done".equals(propertyName)) {
							busyIconTimer.stop();
							lblDataFileStatus.setIcon(idleIcon);
						} else if ("message".equals(propertyName)) {
							String text = (String) (evt.getNewValue());
							lblLikelihoodStatus.setText((text == null) ? ""
									: text);
							messageTimer.restart();
						} else if ("progress".equals(propertyName)) {
							int value = (Integer) (evt.getNewValue());
						}
					}
				});
		lblMoreInfo.setVisible(false);

		ProtTestPrinter.printHeader();
		disableHandler();
	}

	public void disableHandler() {
		mainHandler.setLevel(Level.OFF);
	}

	public void enableHandler() {
		mainHandler.setLevel(Level.INFO);
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = XProtTestApp.getApplication().getMainFrame();
			aboutBox = new XProtTestAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		aboutBox.setVisible(true);
	}

	@Action
	public void showCreditsBox() {
		if (creditsBox == null) {
			JFrame mainFrame = XProtTestApp.getApplication().getMainFrame();
			creditsBox = new CreditsBox(mainFrame);
			creditsBox.setLocationRelativeTo(mainFrame);
		}
		creditsBox.setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents() {

		mainPanel = new JPanel();
		mainTabbedPane = new JTabbedPane();
		mainScrollPane = new JScrollPane();
		mainTextArea = new JTextArea();
		phymlScrollPane = new JScrollPane();
		phymlTextArea = new JTextArea();
		statusPanel = new JPanel();
		lblDataFileStatus = new JLabel();
		lblLikelihoodStatus = new JLabel();
		lblMoreInfo = new JLabel();
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		loadAlignmentMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		saveConsoleMenuItem = new JMenuItem();
		printConsoleMenuItem = new JMenuItem();
		editMenu = new JMenu();
		editCopyMenuItem = new JMenuItem();
		editSelectAllMenuItem = new JMenuItem();
		preferencesMenuItem = new JMenuItem();
		analysisMenu = new JMenu();
		computeMenuItem = new JMenuItem();
		showFrequenciesItem = new JMenuItem();
		showTreeMenuItem = new JMenuItem();
		averagingMenuItem = new JMenuItem();
		resultsMenu = new JMenu();
		resultsMenuItem = new JMenuItem();
		errorMenuItem = new JMenuItem();
		helpMenu = new JMenu();
		manualMenuItem = new JMenuItem();
		homepageMenuItem = new JMenuItem();
		discgroupMenuItem = new JMenuItem();
		aboutMenuItem = new JMenuItem();

		org.jdesktop.application.ResourceMap appResourceMap = org.jdesktop.application.Application
				.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class)
				.getContext().getResourceMap(XProtTestView.class);

		mainPanel.setSize(new java.awt.Dimension(600, 860));
		mainPanel.setBorder(new BorderUIResource.EmptyBorderUIResource(
				new java.awt.Insets(20, 20, 20, 20)));
		mainPanel.setLocation(new java.awt.Point(10, -10));
		mainPanel.setVisible(true);
		mainPanel.setAutoscrolls(true);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(null);
		mainPanel.setName("mainPanel");

		mainScrollPane.setSize(590, 800);// new java.awt.Dimension(590, 600));
		mainScrollPane.setLocation(new java.awt.Point(20, 20));
		mainScrollPane.setVisible(true);
		mainScrollPane.setAutoscrolls(true);

		mainScrollPane.setName("mainScrollPane");
		// mainScrollPane.setPreferredSize(new java.awt.Dimension(460, 500));

		mainTextArea.setMargin(new Insets(5, 5, 5, 5));
		mainTextArea.setBackground(Color.white);
		mainTextArea.setEditable(false);
		mainTextArea.setSize(500, 800);// new java.awt.Dimension(15, 10));
		mainTextArea.setAutoscrolls(true);
		mainTextArea.setVisible(true);

		// mainTextArea.setColumns(15);
		// mainTextArea.setEditable(false);
		mainTextArea.setFont(XProtTestApp.FONT_CONSOLE);
		// mainTextArea.setRows(30);
		mainTextArea.setName("mainTextArea");
		// mainScrollPane.setViewportView(mainTextArea);
		mainScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainTextArea.setWrapStyleWord(true);
		mainTextArea.setLineWrap(true);

		phymlTextArea.setMargin(new Insets(5, 5, 5, 5));
		phymlTextArea.setBackground(Color.white);
		phymlTextArea.setEditable(false);
		phymlTextArea.setSize(500, 800);// new java.awt.Dimension(15, 10));
		phymlTextArea.setAutoscrolls(true);
		phymlTextArea.setVisible(true);
		phymlTextArea.setFont(XProtTestApp.FONT_CONSOLE);
		phymlTextArea.setName("phymlTextArea");
		phymlScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		phymlTextArea.setWrapStyleWord(true);
		phymlTextArea.setLineWrap(true);

		statusPanel.setPreferredSize(new java.awt.Dimension(592, 30));
		statusPanel.setBorder(new BorderUIResource.EtchedBorderUIResource(1,
				new java.awt.Color(182, 182, 182), new java.awt.Color(89, 89,
						89)));
		statusPanel.setLocation(new java.awt.Point(20, 630));
		statusPanel.setVisible(true);
		statusPanel.setLayout(new BorderLayout());
		statusPanel.setForeground(java.awt.Color.blue);
		statusPanel.setBackground(new java.awt.Color(220, 220, 220));
		// statusPanel.setFont(new java.awt.Font("Dialog", 0, 9));

		// statusPanel.setBorder(BorderFactory.createLineBorder(new
		// java.awt.Color(0, 0, 0)));
		statusPanel.setName("statusPanel");
		// statusPanel.setPreferredSize(new java.awt.Dimension(100, 200));

		lblDataFileStatus.setSize(new java.awt.Dimension(150, 40));
		lblDataFileStatus.setVisible(true);
		lblDataFileStatus.setForeground(CRITIC_COLOR);
		lblDataFileStatus.setHorizontalAlignment(JLabel.RIGHT);
		lblDataFileStatus.setFont(XProtTestApp.FONT_LABEL);

		java.util.ResourceBundle bundle = java.util.ResourceBundle
				.getBundle("es/uvigo/darwin/xprottest/resources/XProtTestView");
		lblDataFileStatus.setText(bundle.getString("msg-no-data"));
		lblDataFileStatus.setName("lblDataFileStatus");

		lblLikelihoodStatus.setSize(new java.awt.Dimension(270, 40));
		lblLikelihoodStatus.setVisible(true);
		lblLikelihoodStatus.setForeground(CRITIC_COLOR);
		lblLikelihoodStatus.setFont(XProtTestApp.FONT_LABEL);

		lblLikelihoodStatus.setText(bundle.getString("msg-no-lnl-calculated"));
		lblLikelihoodStatus.setName("lblLikelihoodStatus");

		// mainPanel.add(mainScrollPane, BorderLayout.CENTER);
		mainPanel.add(mainTabbedPane, BorderLayout.CENTER);
		mainTabbedPane.addTab("Main", mainScrollPane);
		mainTabbedPane.addTab("Phyml-log", phymlScrollPane);

		mainPanel.add(statusPanel, BorderLayout.PAGE_END);
		mainScrollPane.getViewport().add(mainTextArea);
		phymlScrollPane.getViewport().add(phymlTextArea);
		statusPanel.add(lblLikelihoodStatus, BorderLayout.LINE_START);
		statusPanel.add(lblDataFileStatus, BorderLayout.LINE_END);

		menuBar.setName("menuBar");

		ActionMap actionMap = org.jdesktop.application.Application
				.getInstance(es.uvigo.darwin.xprottest.XProtTestApp.class)
				.getContext().getActionMap(XProtTestView.class, this);
		fileMenu.setAction(actionMap.get("openDataFile"));
		fileMenu.setMnemonic('F');
		fileMenu.setText(bundle.getString("item-file"));
		fileMenu.setName("fileMenu");

		saveConsoleMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		saveConsoleMenuItem.setMnemonic('S');
		saveConsoleMenuItem.setText(bundle.getString("item-save-console"));
		saveConsoleMenuItem.setName("saveConsoleMenuItem");
		saveConsoleMenuItem
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveConsole(evt);
					}
				});
		printConsoleMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_P,
				java.awt.event.InputEvent.CTRL_MASK));
		printConsoleMenuItem.setMnemonic('P');
		printConsoleMenuItem.setText(bundle.getString("item-print-console"));
		printConsoleMenuItem.setName("printConsoleMenuItem");
		printConsoleMenuItem
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						printConsole(evt);
					}
				});
		loadAlignmentMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		loadAlignmentMenuItem.setMnemonic('L');
		loadAlignmentMenuItem.setText(bundle.getString("item-load-alignment"));
		loadAlignmentMenuItem.setName("loadAlignmentMenuItem");
		loadAlignmentMenuItem
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						openDataFile(evt);
					}
				});
		fileMenu.add(loadAlignmentMenuItem);
		fileMenu.add(saveConsoleMenuItem);
		fileMenu.add(printConsoleMenuItem);
		exitMenuItem.setAction(actionMap.get("quit"));
		exitMenuItem.setMnemonic('x');
		exitMenuItem.setName("menuFileExit");
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		editMenu.setMnemonic('E');
		editMenu.setText(appResourceMap.getString("menu-edit"));
		editMenu.setName("editMenu");

		editCopyMenuItem.setAction(actionMap.get("editCopy"));
		editCopyMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.CTRL_MASK));
		editCopyMenuItem.setMnemonic('c');
		editCopyMenuItem.setText(appResourceMap.getString("menu-copy"));
		editCopyMenuItem.setToolTipText(appResourceMap
				.getString("editCopyMenuItem.toolTipText"));
		editCopyMenuItem.setName("editCopyMenuItem");
		editMenu.add(editCopyMenuItem);

		editSelectAllMenuItem.setAction(actionMap.get("editSelectAll"));
		editSelectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_A,
				java.awt.event.InputEvent.CTRL_MASK));
		editSelectAllMenuItem.setMnemonic('a');
		editSelectAllMenuItem.setText(appResourceMap
				.getString("menu-selectAll"));
		editSelectAllMenuItem.setToolTipText(appResourceMap
				.getString("editSelectAllMenuItem.toolTipText"));
		editSelectAllMenuItem.setName("editSelectAllMenuItem");
		editMenu.add(editSelectAllMenuItem);

		preferencesMenuItem.setMnemonic('p');
		preferencesMenuItem.setText(appResourceMap
				.getString("menu-preferences"));
		preferencesMenuItem.setToolTipText(appResourceMap
				.getString("editPreferencesMenuItem.toolTipText"));
		preferencesMenuItem.setName("preferencesMenuItem");
		preferencesMenuItem
				.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						loadPreferencesView(evt);
					}
				});
		editMenu.add(preferencesMenuItem);

		menuBar.add(editMenu);

		analysisMenu.setMnemonic('A');
		analysisMenu.setText(bundle.getString("item-analysis"));
		analysisMenu.setName("analysisMenu");

		computeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_L,
				java.awt.event.InputEvent.CTRL_MASK));
		computeMenuItem.setMnemonic('c');
		computeMenuItem.setText(bundle.getString("item-compute-likelihood"));
		computeMenuItem.setToolTipText(appResourceMap
				.getString("computeMenuItem.toolTipText"));
		computeMenuItem.setEnabled(false);
		computeMenuItem.setName("computeMenuItem");
		computeMenuItem.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadOptionsView(evt);
			}
		});
		analysisMenu.add(computeMenuItem);

		showFrequenciesItem
				.setAction(actionMap.get("showAminoacidFrequencies"));
		showFrequenciesItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		showFrequenciesItem.setMnemonic('f');
		showFrequenciesItem.setText(bundle.getString("item-show-frequencies"));
		showFrequenciesItem.setToolTipText(appResourceMap
				.getString("showFrequenciesItem.toolTipText"));
		showFrequenciesItem.setEnabled(false);
		showFrequenciesItem.setName("showFrequenciesItem");
		analysisMenu.add(showFrequenciesItem);

		showTreeMenuItem.setAction(actionMap.get("showTree"));
		showTreeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_T,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		showTreeMenuItem.setMnemonic('t');
		showTreeMenuItem.setText(bundle.getString("item-show-tree"));
		showTreeMenuItem.setToolTipText(appResourceMap
				.getString("showTreeMenuItem.toolTipText"));
		showTreeMenuItem.setEnabled(false);
		showTreeMenuItem.setName("showTreeMenuItem");
		analysisMenu.add(showTreeMenuItem);

		averagingMenuItem.setAction(actionMap.get("showConsensus"));
		averagingMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		averagingMenuItem.setMnemonic('p');
		averagingMenuItem.setText(appResourceMap
				.getString("averagingMenuItem.text"));
		averagingMenuItem.setToolTipText(appResourceMap
				.getString("averagingMenuItem.toolTipText"));
		averagingMenuItem.setEnabled(false);
		averagingMenuItem.setName("averagingMenuItem");
		analysisMenu.add(averagingMenuItem);

		menuBar.add(analysisMenu);

		resultsMenu.setAction(actionMap.get("showResultsWindow"));
		resultsMenu.setMnemonic('S');
		resultsMenu.setText(bundle.getString("item-results"));
		resultsMenu.setName("resultsMenu");

		resultsMenuItem.setAction(actionMap.get("showResultsWindow"));
		resultsMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_R,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		resultsMenuItem.setMnemonic('r');
		resultsMenuItem.setText(bundle.getString("item-results-sub"));
		resultsMenuItem.setEnabled(false);
		resultsMenuItem.setName("resultsMenuItem");
		resultsMenuItem.setToolTipText(appResourceMap
				.getString("resultsMenuItem.toolTipText"));
		resultsMenu.add(resultsMenuItem);

		errorMenuItem.setAction(actionMap.get("showErrorLog"));
		errorMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		errorMenuItem.setMnemonic('e');
		errorMenuItem.setText(appResourceMap.getString("errorMenuItem.text"));
		errorMenuItem.setToolTipText(appResourceMap
				.getString("errorMenuItem.toolTipText"));
		errorMenuItem.setEnabled(false);
		errorMenuItem.setName("errorMenuItem");
		resultsMenu.add(errorMenuItem);

		menuBar.add(resultsMenu);

		helpMenu.setMnemonic('H');
		helpMenu.setText(bundle.getString("item-help"));
		helpMenu.setName("helpMenu");

		homepageMenuItem.setAction(actionMap.get("openHomepage"));
		homepageMenuItem.setMnemonic('p');
		homepageMenuItem.setText(appResourceMap.getString("menu-homepage"));
		homepageMenuItem.setName("homepageMenuItem");
		helpMenu.add(homepageMenuItem);
		
		manualMenuItem.setAction(actionMap.get("showManual"));
		manualMenuItem.setMnemonic('m');
		manualMenuItem.setText(appResourceMap.getString("menu-manual"));
		manualMenuItem.setName("manualMenuItem");
		helpMenu.add(manualMenuItem);
		
		discgroupMenuItem.setAction(actionMap.get("openDiscussionGroup"));
		discgroupMenuItem.setMnemonic('D');
		discgroupMenuItem.setText(appResourceMap.getString("menu-discussiongroup"));
		discgroupMenuItem.setName("discgroupMenuItem");
		helpMenu.add(discgroupMenuItem);
		
		aboutMenuItem.setAction(actionMap.get("showAboutBox"));
		aboutMenuItem.setText(bundle.getString("item-about-window"));
		aboutMenuItem.setName("aboutMenuItem");
		helpMenu.add(aboutMenuItem);

		creditsMenuItem.setAction(actionMap.get("showCreditsBox"));
		creditsMenuItem.setText(bundle.getString("item-credits-window"));
		creditsMenuItem.setName("creditsMenuItem");
		helpMenu.add(creditsMenuItem);

		menuBar.add(helpMenu);

		setComponent(mainPanel);
		setMenuBar(menuBar);

		this.getFrame().setLayout(new BorderLayout());

		this.getFrame().setLocation(new java.awt.Point(281, 80));
		this.getFrame().getContentPane().setLayout(new BorderLayout());
		this.getFrame().setTitle("ProtTest " + ProtTest.versionNumber);
		this.getFrame().setSize(new java.awt.Dimension(630, 695));
		this.getFrame().setResizable(true);
		this.getFrame().setContentPane(mainPanel);
	}

	private void openDataFile(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openDataFile
		FileDialog fc = new FileDialog(this.getFrame(),
				"Load amino-acid alignment", FileDialog.LOAD);
		fc.setDirectory(System.getProperty("user.dir"));
		fc.setVisible(true);

		String dataFileName = fc.getFile();

		if (dataFileName != null) {
			enableHandler();
			try {
				ProtTestPrinter.printPreAnalysisHeader();
				File f = new File(fc.getDirectory() + dataFileName);// fc.getSelectedFile();
				ProtTestPrinter.printFileData(f);
				if (alignmentFile == null
						|| !f.getAbsolutePath().equals(
								alignmentFile.getAbsolutePath())) {
					lblLikelihoodStatus.setText(resourceMap
							.getString("msg-no-lnl-calculated"));
					lblLikelihoodStatus.setForeground(CRITIC_COLOR);
					lnlCalculated = false;
				}
				alignment = prottestFacade.readAlignment(f.getAbsolutePath(),
						true);
				setAlignmentFile(f);
			} catch (AlignmentParseException ex) {
				displayWriter.println(ex.getMessage());
				setAlignmentFile(null);
			} catch (FileNotFoundException ex) {
				displayWriter.println(ex.getMessage());
				setAlignmentFile(null);
			} catch (IOException ex) {
				displayWriter.println(ex.getMessage());
				setAlignmentFile(null);
			}
			disableHandler();
		}
	}// GEN-LAST:event_openDataFile

	private void saveConsole(java.awt.event.ActionEvent evt) {
		String text = mainTextArea.getText();

		FileDialog fc = new FileDialog(this.getFrame(),
				"Save ProtTest console", FileDialog.SAVE);
		fc.setDirectory(System.getProperty("user.dir"));
		fc.setVisible(true);

		String dataFileName = fc.getFile();
		try {
			File f = new File(dataFileName);
			FileWriter fw = new FileWriter(f);
			fw.write(text);
			fw.close();
		} catch (IOException ex) {
			Logger.getLogger(XProtTestView.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private void printConsole(java.awt.event.ActionEvent evt) {
		try {
			mainTextArea.print();
		} catch (PrinterException ex) {
			Logger.getLogger(XProtTestView.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	private void loadOptionsView(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_loadOptionsView
		OptionsView op = new OptionsView(this, alignment,
				alignmentFile.getAbsolutePath());
		op.setVisible(true);

		enableItems(false);
	}// GEN-LAST:event_loadOptionsView

	private void loadPreferencesView(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_loadPreferencesView
		PreferencesView pv = new PreferencesView(this);
		pv.setVisible(true);

		enableItems(false);
	}// GEN-LAST:event_loadPreferencesView

	public void unloadOptionsView(boolean running) {
		if (!running) {
			enableItems(true);

		}
	}

	public void unloadPreferencesView() {
		enableItems(true);

	}

	// private JFileChooser createFileChooser(String title) {
	// JFileChooser fc = new JFileChooser();
	// fc.setDialogTitle(title);
	// fc.setFileFilter(fc.getAcceptAllFileFilter());
	// fc.setCurrentDirectory(new File("."));
	// return fc;
	// }

	@Action
	public void showAminoacidFrequencies() {
		if (alignment != null) {
			if (frequenciesView == null) {
				frequenciesView = new FrequenciesView(alignment);

			}
			frequenciesView.setVisible(true);

		}
	}

	private boolean correctDone;

	@Action
	public void showResultsWindow() {
		if (models != null) {
			if (resultsView == null) {
				resultsView = new ResultsView(this, alignment, models,
						correctDone);

			}
			resultsView.setVisible(true);

		}
	}

	private int numModels;
	private boolean taskRunning = false;

	public void computeLikelihood(int numModels, ApplicationOptions options) {

		enableHandler();

		ProtTestPrinter.printExecutionHeader(options);

		preferencesMenuItem.setEnabled(false);

		this.numModels = numModels;

		if (errorLogView != null) {
			ProtTestLogger.getDefaultLogger().removeHandler(
					errorLogView.getLogHandler());
			errorLogView.setVisible(false);
			errorLogView.dispose();
			errorLogView = null;

		}
		if (resultsView != null) {
			resultsView.setVisible(false);
			resultsView.dispose();
			resultsView = null;

		}
		if (treeView != null) {
			treeView.setVisible(false);
			treeView.dispose();
			treeView = null;

		}
		if (consensusView != null) {
			consensusView.setVisible(false);
			consensusView.dispose();
			consensusView = null;

		}
		errorLogView = new ErrorLogView();
		ProtTestLogger.getDefaultLogger().addHandler(
				errorLogView.getLogHandler());
		RunningFrame runningFrame = new RunningFrame(this, numModels);
		errorMenuItem.setEnabled(false);
		prottestFacade.addObserver(runningFrame);
		lblMoreInfo.setVisible(false);

		Task task = new ComputeLikelihoodTask(getApplication(), runningFrame,
				this, options);
		// get the application's context...
		ApplicationContext appC = Application.getInstance().getContext();
		// ...to get the TaskMonitor and TaskService
		TaskMonitor tM = appC.getTaskMonitor();
		TaskService tS = appC.getTaskService();

		// i.e. making the animated progressbar and busy icon visible
		tS.execute(task);
		taskRunning = true;

		enableItems(false);
		runningFrame.setVisible(true);

	}

	private void enableItems(boolean enable) {
		menuBar.setEnabled(enable);
		fileMenu.setEnabled(enable);
		editMenu.setEnabled(enable);
		analysisMenu.setEnabled(enable);
		resultsMenu.setEnabled(enable);
		helpMenu.setEnabled(enable);
	}

	protected void computationDone(boolean done, Model[] models) {

		preferencesMenuItem.setEnabled(true);

		if (done) {
			correctDone = (models.length == this.numModels);

			if (correctDone) {
				lblLikelihoodStatus.setText(resourceMap
						.getString("msg-lnl-calculated"));
				lblLikelihoodStatus.setForeground(DONE_COLOR);
				errorMenuItem.setEnabled(false);
				lblMoreInfo.setVisible(false);

				if (errorLogView != null) {
					errorLogView.setVisible(false);
					errorLogView.dispose();
					errorLogView = null;

				}
			} else {
				lblLikelihoodStatus.setText("Warning! "
						+ resourceMap.getString("models-not-complete"));
				lblLikelihoodStatus.setForeground(CRITIC_COLOR);
				displayWriter.println("");
				displayWriter.println(resourceMap
						.getString("models-not-complete"));
				displayWriter.println(resourceMap
						.getString("msg-see-error-log"));
				errorMenuItem.setEnabled(true);
				lblMoreInfo.setVisible(true);

			}
		} else {
			lblLikelihoodStatus.setText(resourceMap.getString("msg-lnl-error"));
			lblLikelihoodStatus.setForeground(CRITIC_COLOR);
			displayWriter.println(resourceMap.getString("msg-see-error-log"));
			errorMenuItem.setEnabled(true);

		}

		lnlCalculated = done;
		resultsMenuItem.setEnabled(done);
		showTreeMenuItem.setEnabled(done);
		averagingMenuItem.setEnabled(done);

		this.models = models;

		disableHandler();

	}

	public void computationInterrupted() {
		lblLikelihoodStatus.setText(resourceMap.getString("msg-lnl-error"));
		lblLikelihoodStatus.setForeground(CRITIC_COLOR);
		errorMenuItem.setEnabled(true);
		lblMoreInfo.setVisible(true);
		disableHandler();

	}

	public void unloadRunningView(RunningFrame rf) {
		enableItems(true);
		rf.setVisible(false);
		disableHandler();
	}

	private class ComputeLikelihoodTask extends Task<Object, Void> {

		private RunningFrame runningFrame;
		private XProtTestView mainFrame;
		private Model[] models;
		private ApplicationOptions options;

		ComputeLikelihoodTask(Application app, RunningFrame runningFrame,
				XProtTestView mainFrame, ApplicationOptions options) {
			// Runs on the EDT. Copy GUI state that
			// doInBackground() depends on from parameters
			// to ComputeLikelihoodTask fields, here.
			super(app);
			this.options = options;
			this.runningFrame = runningFrame;
			this.mainFrame = mainFrame;
			runningFrame.setTask(this);
		}

		@Override
		protected Object doInBackground() {
			// Your Task's code here. This method runs
			// on a background thread, so don't reference
			// the Swing GUI from here.
			// ProtTestPrinter printer = new ProtTestPrinter(getDisplayWriter(),
			// new PrintWriter(System.err));

			try {
				options.setAlignment(alignmentFile.getAbsolutePath());
				models = prottestFacade.startAnalysis(options);
			} catch (AlignmentParseException ex) {
				ProtTestLogger.severeln(ex.getMessage(), this.getClass());
			} catch (IOException ex) {
				ProtTestLogger.severeln(ex.getMessage(), this.getClass());
			} catch (ProtTestInternalException ex) {
				ProtTestLogger.severeln(ex.getMessage(), this.getClass());
			}

			runningFrame.finishedExecution();

			return null; // return your result
		}

		@Override
		protected void succeeded(Object result) {
			// Runs on the EDT. Update the GUI based on
			// the result computed by doInBackground().
			boolean done = models != null && models.length > 0;
			if (done) {
				try {
					for (Model model : models) {
						// throws ProtTestInternalException when Lk is not set
						model.getLk();
					}
				} catch (ProtTestInternalException e) {
					done = false;
				}
			}

			mainFrame.computationDone(done, models);
		}

		@Override
		protected void cancelled() {
			mainFrame.computationDone(false, null);
		}

		@Override
		protected void interrupted(InterruptedException ex) {
			mainFrame.computationInterrupted();
		}
	}

	@Action
	public void showTree() {
		TreeFacade treeFacade = new TreeFacadeImpl();

		if (models != null) {
			if (treeView == null) {
				treeView = new TreeView(this, treeFacade, tree, models);

			}
			treeView.setVisible(true);

		}
	}

	@Action
	public void showConsensus() {
		TreeFacade treeFacade = new TreeFacadeImpl();

		if (models != null) {
			if (consensusView == null) {
				consensusView = new Consensus(this, treeFacade, models,
						alignment);

			}
			consensusView.setVisible(true);

		}
	}

	@Action
	public void showErrorLog() {
		if (errorLogView != null) {
			errorLogView.setVisible(true);

		}
	}

	@Action
	public void editCopy() {
		mainTextArea.copy();

	}

	@Action
	public void editSelectAll() {
		mainTextArea.selectAll();

	}

	@Action
	public void showPreferences() {
	}

	@Action
	public void openDiscussionGroup() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				URI prottestURI = new URI(ProtTest.URL_DISCUSSION_GROUP);
				desktop.browse(prottestURI);
			} else {
				displayWriter.println("Cannot browse URL. Please open " + 
						ProtTest.URL_DISCUSSION_GROUP + " manually.");
			}
		} catch (Exception e) {
			displayWriter.println("Cannot open URL : " + e.getMessage());
		}
	}
	
	@Action
	public void openHomepage() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				URI prottestURI = new URI(ProtTest.URL_HOMEPAGE);
				desktop.browse(prottestURI);
			} else {
				displayWriter.println("Cannot browse URL. Please open " + 
						ProtTest.URL_HOMEPAGE + " manually.");
			}
		} catch (Exception e) {
			displayWriter.println("Cannot open URL : " + e.getMessage());
		}
	}
	
	@Action
	public void showManual() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				URI prottestURI = new URI(ProtTest.URL_MANUAL);
				desktop.browse(prottestURI);
			} else {
				displayWriter.println("Cannot browse URL. Please open " + 
						ProtTest.URL_MANUAL + " manually.");
			}
		} catch (Exception e) {
			displayWriter.println("Cannot open URL : " + e.getMessage());
		}
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JMenu fileMenu;
	private JMenuItem exitMenuItem;
	private JMenu analysisMenu;
	private JMenuItem averagingMenuItem;
	private JMenuItem computeMenuItem;
	private JMenuItem editCopyMenuItem;
	private JMenu editMenu;
	private JMenuItem editSelectAllMenuItem;
	private JMenuItem errorMenuItem;
	private JLabel lblDataFileStatus;
	private JLabel lblLikelihoodStatus;
	private JLabel lblMoreInfo;
	private JMenuItem loadAlignmentMenuItem;
	private JPanel mainPanel;
	private JTabbedPane mainTabbedPane;
	private JScrollPane phymlScrollPane;
	private JTextArea phymlTextArea;
	private JScrollPane mainScrollPane;
	private JTextArea mainTextArea;
	private JMenuItem homepageMenuItem;
	private JMenuItem manualMenuItem;
	private JMenuItem discgroupMenuItem;
	private JMenuBar menuBar;
	private JMenuItem preferencesMenuItem;
	private JMenu resultsMenu;
	private JMenuItem resultsMenuItem;
	private JMenuItem showFrequenciesItem;
	private JMenuItem showTreeMenuItem;
	private JMenu helpMenu;
	private JMenuItem aboutMenuItem;
	private JPanel statusPanel;
	// End of variables declaration//GEN-END:variables
	private JMenuItem saveConsoleMenuItem;
	private JMenuItem printConsoleMenuItem;
	private JMenuItem creditsMenuItem = new JMenuItem();
	private final Timer messageTimer;
	private final Timer busyIconTimer;
	private final Icon idleIcon;
	private final Icon[] busyIcons = new Icon[15];
	private int busyIconIndex = 0;
	private JDialog aboutBox;
	private JDialog creditsBox;

}
