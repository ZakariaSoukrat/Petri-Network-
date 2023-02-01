/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pneditor.editor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import org.pneditor.editor.actions.*;
import org.pneditor.editor.actions.algorithms.BoundednessAction;
import org.pneditor.editor.actions.draw.ArcSelectToolAction;
import org.pneditor.editor.actions.draw.PlaceSelectToolAction;
import org.pneditor.editor.actions.draw.SelectionSelectToolAction;
import org.pneditor.editor.actions.draw.TokenSelectToolAction;
import org.pneditor.editor.actions.draw.TransitionSelectToolAction;
import org.pneditor.editor.actions.edit.CopyAction;
import org.pneditor.editor.actions.edit.CutAction;
import org.pneditor.editor.actions.edit.DeleteAction;
import org.pneditor.editor.actions.edit.PasteAction;
import org.pneditor.editor.actions.edit.RedoAction;
import org.pneditor.editor.actions.edit.SelectAllAction;
import org.pneditor.editor.actions.edit.UndoAction;
import org.pneditor.editor.actions.element.SetArcInhibitoryAction;
import org.pneditor.editor.actions.element.SetArcMultiplicityAction;
import org.pneditor.editor.actions.element.SetArcRegularAction;
import org.pneditor.editor.actions.element.SetArcResetAction;
import org.pneditor.editor.actions.element.SetLabelAction;
import org.pneditor.editor.actions.element.SetTokensAction;
import org.pneditor.editor.actions.file.ExportAction;
import org.pneditor.editor.actions.file.ImportAction;
import org.pneditor.editor.actions.file.NewFileAction;
import org.pneditor.editor.actions.file.OpenFileAction;
import org.pneditor.editor.actions.file.QuitAction;
import org.pneditor.editor.actions.file.SaveAction;
import org.pneditor.editor.actions.file.SaveFileAsAction;
import org.pneditor.editor.canvas.*;
import org.pneditor.editor.filechooser.EpsFileType;
import org.pneditor.editor.filechooser.FileType;
import org.pneditor.editor.filechooser.FileTypeException;
import org.pneditor.editor.filechooser.PflowFileType;
import org.pneditor.editor.filechooser.PngFileType;
import org.pneditor.editor.filechooser.ViptoolPnmlFileType;
import org.pneditor.editor.gpetrinet.GraphicArc;
import org.pneditor.editor.gpetrinet.GraphicElement;
import org.pneditor.editor.gpetrinet.GraphicPetriNet;
import org.pneditor.petrinet.PetriNetInterface;
import org.pneditor.util.GraphicsTools;
import org.pneditor.PNEConstantsConfiguration;

/**
 * This class is the main point of the application.
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public final class Root implements WindowListener, ListSelectionListener, SelectionChangedListener {

	private static final String APP_NAME = "PNEditor";
	private static final String APP_VERSION = "0.71";

	public Root(final String[] varargs) {
		loadPreferences();
		this.selection.setSelectionChangedListener(this);

		setupMainFrame();
		this.mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setupFrameIcons();

		if (varargs.length == 1) {
			final String filename = varargs[0];
			final File file = new File(filename);
			final FileType fileType = FileType.getAcceptingFileType(file, FileType.getAllFileTypes());
			try {
				final GraphicPetriNet petriNet = fileType.load(file, getCurrentModel());
				this.setGraphicPetriNet(petriNet);
				this.setCurrentFile(file); // TODO: make it DRY with OpenFileAction
				this.setModified(false);
				this.setCurrentDirectory(file.getParentFile());
				this.canvas.repaint();
			} catch (FileTypeException ex) {
				Logger.getLogger(Root.class.getName()).log(Level.INFO, null, ex);
			}
		}
	}

	private static final String CURRENT_DIRECTORY = "current_directory";

	private void loadPreferences() {
		final Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		setCurrentDirectory(new File(preferences.get(CURRENT_DIRECTORY, System.getProperty("user.home"))));
	}

	private void savePreferences() {
		final Preferences preferences = Preferences.userNodeForPackage(this.getClass());
		preferences.put(CURRENT_DIRECTORY, getCurrentDirectory().toString());
	}

	// Undo manager - per application
	private final UndoAction undo = new UndoAction(this);
	private final RedoAction redo = new RedoAction(this);
	private final UndoManager undoManager = new UndoManager(this, this.undo, this.redo);

	public UndoManager getUndoManager() {
		return this.undoManager;
	}

	// Current directory - per application
	private File currentDirectory;

	public File getCurrentDirectory() {
		return this.currentDirectory;
	}

	public void setCurrentDirectory(final File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}

	// Main frame - per application
	private final MainFrame mainFrame = new MainFrame(getNewWindowTitle());

	public Frame getParentFrame() {
		return this.mainFrame;
	}

	// Document - per tab
	private GraphicPetriNet graphicPetriNet = new GraphicPetriNet();

	public GraphicPetriNet getGraphicPetriNet() {
		return this.graphicPetriNet;
	}

	public void setGraphicPetriNet(final GraphicPetriNet graphicPetriNet) {
		this.graphicPetriNet = graphicPetriNet;
		getUndoManager().eraseAll();
		refreshAll();
	}

	public void updateGraphicPetriNet(final GraphicPetriNet newGraphicPetriNet) {
		this.graphicPetriNet = newGraphicPetriNet;
		refreshAll();
	}

	public PetriNetInterface getPetriNet() {
		return this.graphicPetriNet.getPetriNet();
	}

	private String model = "initial";

	public String getCurrentModel() {
		return this.model;
	}

	public void setCurrentModel(final String model) {
		this.model = model;
		this.mainFrame.setTitle(getNewWindowTitle());
		
		for (final JMenuItem modelItem : this.modelsList) {
			if (modelItem.getName().equals(getCurrentModel())) {
				modelItem.setEnabled(false);
			} else {
				modelItem.setEnabled(true);
			}
		}
	}

	// Clicked element - per tab
	private GraphicElement clickedElement;

	public GraphicElement getClickedElement() {
		return this.clickedElement;
	}

	public void setClickedElement(final GraphicElement clickedElement) {
		this.clickedElement = clickedElement;
		enableOnlyPossibleActions();
	}

	// Selection - per tab
	private final Selection selection = new Selection();

	public Selection getSelection() {
		return this.selection;
	}

	@Override
	public void selectionChanged() {
		enableOnlyPossibleActions();
	}

	// Selection + clicked element
	public Set<GraphicElement> getSelectedElementsWithClickedElement() {
		final Set<GraphicElement> selectedElements = new HashSet<>();
		selectedElements.addAll(getSelection().getElements());
		if (getClickedElement() != null) {
			selectedElements.add(getClickedElement());
		}
		return selectedElements;
	}

	// List editor - per tab

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		enableOnlyPossibleActions();
		repaintCanvas();
	}

	// per tab
	public void selectToolSelect() {
		this.select.setSelected(true);
		this.canvas.activeCursor = Cursor.getDefaultCursor();
		this.canvas.setCursor(this.canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedToolSelect() {
		return this.select.isSelected();
	}

	public void selectToolPlace() {
		this.place.setSelected(true);
		
		this.canvas.activeCursor = GraphicsTools.getCursor(PNEConstantsConfiguration.PLACE, new Point(16, 16));
		this.canvas.setCursor(this.canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedToolPlace() {
		return this.place.isSelected();
	}

	public void selectToolTransition() {
		this.transition.setSelected(true);
		this.canvas.activeCursor = GraphicsTools.getCursor(PNEConstantsConfiguration.TRANSITION, new Point(16, 16));
		this.canvas.setCursor(this.canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedToolTransition() {
		return this.transition.isSelected();
	}

	public void selectToolArc() {
		this.arc.setSelected(true);
		this.canvas.activeCursor = GraphicsTools.getCursor(PNEConstantsConfiguration.ARC, new Point(0, 0));
		this.canvas.setCursor(this.canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedToolArc() {
		return this.arc.isSelected();
	}

	public void selectToolToken() {
		this.token.setSelected(true);
		this.canvas.activeCursor = GraphicsTools.getCursor(PNEConstantsConfiguration.TOKEN_OR_FIRE, new Point(16, 0));
		this.canvas.setCursor(this.canvas.activeCursor);
		repaintCanvas();
	}

	public boolean isSelectedToolToken() {
		return this.token.isSelected();
	}

	// per tab
	private final Canvas canvas = new Canvas(this);
	private final DrawingBoard drawingBoard = new DrawingBoard(this.canvas);

	private JPopupMenu placePopup;
	private JPopupMenu transitionPopup;
	private JPopupMenu arcPopup;
	private JPopupMenu canvasPopup;

	public JPopupMenu getPlacePopup() {
		return this.placePopup;
	}
	
	private void setupPlacePopup() {
		this.placePopup = new JPopupMenu();
		this.placePopup.add(this.setLabel);
		this.placePopup.add(this.setTokens);
		this.placePopup.addSeparator();
		this.placePopup.add(this.cutAction);
		this.placePopup.add(this.copyAction);
		this.placePopup.add(this.delete);
	}

	public JPopupMenu getTransitionPopup() {
		return this.transitionPopup;
	}
	
	private void setupTransitionPopup() {
		this.transitionPopup = new JPopupMenu();
		this.transitionPopup.add(this.setLabel);
		this.transitionPopup.addSeparator();
		this.transitionPopup.add(this.cutAction);
		this.transitionPopup.add(this.copyAction);
		this.transitionPopup.add(this.delete);
	}

	public JPopupMenu getArcPopup() {
		return this.arcPopup;
	}
	
	private void setupArcPopup() {
		this.arcPopup = new JPopupMenu();
		this.arcPopup.add(this.setArcMultiplicity);
		this.arcPopup.add(this.setArcRegular);
		this.arcPopup.add(this.setArcInhibitory);
		this.arcPopup.add(this.setArcReset);

		this.arcPopup.add(this.delete);
	}

	public JPopupMenu getCanvasPopup() {
		return this.canvasPopup;
	}
	
	private void setupCanvasPopup() {
		this.canvasPopup = new JPopupMenu();
		this.canvasPopup.add(this.pasteAction);
	}

	// per application
	private JToggleButton select;
	private JToggleButton place;
	private JToggleButton transition;
	private JToggleButton arc;
	private JToggleButton token;
	
	private Action setLabel;
	private Action setTokens;
	private Action setArcMultiplicity;
	private Action setArcInhibitory;
	private Action setArcReset;
	private Action setArcRegular;
	
	private Action delete;
	private Action cutAction;
	private Action copyAction;
	private Action pasteAction;
	private Action selectAllAction;
	
	private final Set<JMenuItem> modelsList = new HashSet<>();

	public void refreshAll() {
		this.canvas.repaint();
		enableOnlyPossibleActions();
	}

	public void repaintCanvas() {
		this.canvas.repaint();
	}

	private void enableOnlyPossibleActions() {
		final boolean isDeletable = this.clickedElement != null || !this.selection.isEmpty();
		final boolean isCutable = isDeletable;
		final boolean isCopyable = isCutable;
		final boolean isPastable = !this.clipboard.isEmpty();
		final boolean isArc = this.clickedElement != null && !this.clickedElement.isNode();
		final boolean isTransition = this.clickedElement != null && this.clickedElement.isTransition();
		final boolean isPlace = this.clickedElement != null && this.clickedElement.isPlace();
		final boolean isSourceAPlace = isArc && ((GraphicArc) this.clickedElement).getArc().isSourceAPlace();
		final boolean isReset = isArc && ((GraphicArc) this.clickedElement).getArc().isReset();


		this.cutAction.setEnabled(isCutable);
		this.copyAction.setEnabled(isCopyable);
		this.pasteAction.setEnabled(isPastable);
		this.selectAllAction.setEnabled(true);
		this.delete.setEnabled(isDeletable);
		this.setArcMultiplicity.setEnabled(!isReset);
		this.setArcInhibitory.setEnabled(isSourceAPlace);
		this.setArcReset.setEnabled(isSourceAPlace);
		this.setArcRegular.setEnabled(isArc);
		this.setTokens.setEnabled(isPlace);
		this.setLabel.setEnabled(isPlace || isTransition);
		this.undo.setEnabled(getUndoManager().isUndoable());
		this.redo.setEnabled(getUndoManager().isRedoable());

	}

	@Override
	public void windowClosed(final WindowEvent e) {
	}

	@Override
	public void windowIconified(final WindowEvent e) {
	}

	@Override
	public void windowDeiconified(final WindowEvent e) {
	}

	@Override
	public void windowActivated(final WindowEvent e) {
	}

	@Override
	public void windowDeactivated(final WindowEvent e) {
	}

	@Override
	public void windowOpened(final WindowEvent e) {
	}

	@Override
	public void windowClosing(final WindowEvent e) {
		quitApplication();
	}

	/**
	 * Terminates the application
	 */
	public void quitApplication() {
		if (!this.isModified()) {
			quitNow();
		}
		this.mainFrame.setState(Frame.NORMAL);
		this.mainFrame.setVisible(true);
		final int answer = JOptionPane.showOptionDialog(this.getParentFrame(),
				"Any unsaved changes will be lost. Really quit?", "Quit", JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE, null, new String[] { "Quit", "Cancel" }, "Cancel");
		if (answer == JOptionPane.YES_OPTION) {
			quitNow();
		}
	}

	private void quitNow() {
		savePreferences();
		System.exit(0);
	}

	private final JToolBar toolBar = new JToolBar();

	private void setupFrameIcons() {
		final List<Image> icons = new LinkedList<>();
		icons.add(GraphicsTools.getBufferedImage(PNEConstantsConfiguration.ICON16));
		icons.add(GraphicsTools.getBufferedImage(PNEConstantsConfiguration.ICON32));
		icons.add(GraphicsTools.getBufferedImage(PNEConstantsConfiguration.ICON48));
		this.mainFrame.setIconImages(icons);
	}

	private JMenu setupAndGetFileMenu() {
		
	 	final List<FileType> openSaveFiletypes = new LinkedList<>();
		openSaveFiletypes.add(new PflowFileType());
		
		final List<FileType> importFiletypes = new LinkedList<>();
		importFiletypes.add(new ViptoolPnmlFileType());
		
		final List<FileType> exportFiletypes = new LinkedList<>();
		exportFiletypes.add(new ViptoolPnmlFileType());
		exportFiletypes.add(new EpsFileType());
		exportFiletypes.add(new PngFileType());
		
		final Action newFile = new NewFileAction(this);
		final Action openFile = new OpenFileAction(this, openSaveFiletypes);
		final Action saveFile = new SaveAction(this, openSaveFiletypes);
		final Action saveFileAs = new SaveFileAsAction(this, openSaveFiletypes);
		final Action importFile = new ImportAction(this, importFiletypes);
		final Action exportFile = new ExportAction(this, exportFiletypes);
		final Action quit = new QuitAction(this);
		
		this.toolBar.add(newFile);
		this.toolBar.add(openFile);
		this.toolBar.add(saveFile);
		this.toolBar.add(importFile);
		this.toolBar.add(exportFile);
		this.toolBar.addSeparator();

		final JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		fileMenu.add(newFile);
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		fileMenu.add(saveFileAs);
		fileMenu.add(importFile);
		fileMenu.add(exportFile);
		fileMenu.addSeparator();
		fileMenu.add(quit);
		
		return fileMenu;
	}
	
	private JMenu setupAndGetElementMenu() {
		this.setLabel = new SetLabelAction(this);
		this.setTokens = new SetTokensAction(this);
		this.setArcMultiplicity = new SetArcMultiplicityAction(this);
		this.setArcInhibitory = new SetArcInhibitoryAction(this);
		this.setArcRegular = new SetArcRegularAction(this);
		this.setArcReset = new SetArcResetAction(this);
		
		final JMenu elementMenu = new JMenu("PetriNet");
		elementMenu.setMnemonic('P');
		
		elementMenu.add(this.setLabel);
		elementMenu.addSeparator();
		elementMenu.add(this.setTokens);
		elementMenu.addSeparator();
		elementMenu.add(this.setArcMultiplicity);
		elementMenu.add(this.setArcInhibitory);
		elementMenu.add(this.setArcReset);
		elementMenu.add(this.setArcRegular);
		
		return elementMenu;
	}
	
	private JMenu setupAndGetEditMenu() {
		this.delete = new DeleteAction(this);
		this.cutAction = new CutAction(this);
		this.copyAction = new CopyAction(this);
		this.pasteAction = new PasteAction(this);
		this.selectAllAction = new SelectAllAction(this);
		
		this.toolBar.add(this.cutAction);
		this.toolBar.add(this.copyAction);
		this.toolBar.add(this.pasteAction);
		this.toolBar.addSeparator();

		this.toolBar.add(this.undo);
		this.toolBar.add(this.redo);
		this.toolBar.add(this.delete);
		
		final JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		
		editMenu.add(this.undo);
		editMenu.add(this.redo);
		editMenu.addSeparator();
		editMenu.add(this.cutAction);
		editMenu.add(this.copyAction);
		editMenu.add(this.pasteAction);
		editMenu.add(this.selectAllAction);
		editMenu.add(this.delete);
		
		return editMenu;
	}
	
	private JMenu setupAndGetDrawMenu() {

		final Action selectToolSelectionAction = new SelectionSelectToolAction(this);
		final Action selectToolPlaceAction = new PlaceSelectToolAction(this);
		final Action selectToolTransitionAction = new TransitionSelectToolAction(this);
		final Action selectToolArcAction = new ArcSelectToolAction(this);
		final Action selectToolTokenAction = new TokenSelectToolAction(this);

		this.select = new JToggleButton(selectToolSelectionAction);
		this.select.setSelected(true);
		this.place = new JToggleButton(selectToolPlaceAction);
		this.transition = new JToggleButton(selectToolTransitionAction);
		this.arc = new JToggleButton(selectToolArcAction);
		this.token = new JToggleButton(selectToolTokenAction);

		this.select.setText("");
		this.place.setText("");
		this.transition.setText("");
		this.arc.setText("");
		this.token.setText("");

		this.toolBar.addSeparator();
		this.toolBar.add(this.select);
		this.toolBar.add(this.place);
		this.toolBar.add(this.transition);
		this.toolBar.add(this.arc);
		this.toolBar.add(this.token);
		this.toolBar.addSeparator();
		
		final ButtonGroup drawGroup = new ButtonGroup();
		drawGroup.add(this.select);
		drawGroup.add(this.place);
		drawGroup.add(this.transition);
		drawGroup.add(this.arc);
		drawGroup.add(this.token);
		
		final JMenu drawMenu = new JMenu("Draw");
		drawMenu.setMnemonic('D');
		
		drawMenu.add(selectToolSelectionAction);
		drawMenu.addSeparator();
		drawMenu.add(selectToolPlaceAction);
		drawMenu.add(selectToolTransitionAction);
		drawMenu.add(selectToolArcAction);
		drawMenu.add(selectToolTokenAction);
		
		return drawMenu;

	}
	
	private JMenu setupAndGetChangeMenu() {
		final JMenu changeMenu = new JMenu("Change model");
		changeMenu.setMnemonic('C');
		String dirName = "src/org/pneditor/petrinet/adapters";
		final File folder = new File(dirName);
		if (folder != null) {
			for (final File modelPath : folder.listFiles()) {	
				  Path path = modelPath.toPath();
				  final String modelString = path.getName(path.getNameCount() - 1).toFile().getName();
			      final JMenuItem modelItem = changeMenu.add(new ChangeModelAction(this,modelString));
				modelItem.setName(modelString);
				if (modelString.equals(getCurrentModel())) {
					modelItem.setEnabled(false);
				}
				this.modelsList.add(modelItem);
			}
		}
		return changeMenu;
	}
	
	private void setupMainFrame() {
		this.toolBar.setFloatable(false);

		final JMenuBar menuBar = new JMenuBar();
		this.mainFrame.setJMenuBar(menuBar);

		// asus 2012 algorithms menu
		final JMenu algorithmsMenu = new JMenu("Algorithms");
		algorithmsMenu.setMnemonic('A');

		// asus 2012 algorithms submenu items
		algorithmsMenu.add(new BoundednessAction(this));

		final JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new AboutAction(this));
		
		menuBar.add(setupAndGetFileMenu());
		menuBar.add(setupAndGetEditMenu());
		menuBar.add(setupAndGetDrawMenu());
		menuBar.add(setupAndGetElementMenu());
		menuBar.add(algorithmsMenu);
		menuBar.add(setupAndGetChangeMenu());
		menuBar.add(helpMenu);

		setupArcPopup();
		setupCanvasPopup();
		setupPlacePopup();
		setupTransitionPopup();

		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		splitPane.setDividerSize(6);
		splitPane.setOneTouchExpandable(true);
		splitPane.setRightComponent(this.drawingBoard);
		splitPane.setDividerLocation(120);

		this.mainFrame.add(splitPane, BorderLayout.CENTER);
		this.mainFrame.add(this.toolBar, BorderLayout.NORTH);

		this.mainFrame.addWindowListener(this);
		this.mainFrame.setLocation(50, 50);
		this.mainFrame.setSize(700, 450);
		this.mainFrame.setVisible(true);
	}

	private LocalClipboard clipboard = new LocalClipboard();

	public LocalClipboard getClipboard() {
		return this.clipboard;
	}

	public void updateClipboard() {
		final Set<GraphicElement> elementsCopied = this.clipboard.getContents();
		this.clipboard = new LocalClipboard(getCurrentModel());
		this.clipboard.setContents(elementsCopied);
	}

	private boolean isModified;

	public boolean isModified() {
		return this.isModified;
	}

	public void setModified(final boolean isModified) {
		this.isModified = isModified;
		this.mainFrame.setTitle(getNewWindowTitle());
	}

	private String getNewWindowTitle() {
		String windowTitle = "";
		if (getCurrentFile() != null) {
			windowTitle += getCurrentFile().getName();
		} else {
			windowTitle += "Untitled";
		}
		if (isModified()) {
			windowTitle += " [modified]";
		}
		windowTitle += " - " + getAppShortName() + " - " + (this.model != null ? this.model : "initial"); // FIXME: why null at
																								// the start?
		return windowTitle;
	}

	private File currentFile;

	public File getCurrentFile() {
		return this.currentFile;
	}

	public void setCurrentFile(final File currentFile) {
		this.currentFile = currentFile;
		this.mainFrame.setTitle(getNewWindowTitle());
	}

	public String getAppShortName() {
		return APP_NAME;
	}

	public String getAppLongName() {
		return APP_NAME + ", version " + APP_VERSION;
	}

	public DrawingBoard getDrawingBoard() {
		return this.drawingBoard;
	}
	
	/**
	 * Scans all classloaders for the current thread for loaded jars, and then scans
	 * each jar for the package name in question, listing all classes directly under
	 * the package name in question. Assumes directory structure in jar file and class
	 * package naming follow java conventions (i.e. com.example.test.MyTest would be in
	 * /com/example/test/MyTest.class)
	 */
	public Collection<Class> getClassesForPackage(String packageName) throws Exception {
	  String packagePath = packageName.replace(".", "/");
	  ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	  Set<URL> jarUrls = new HashSet<URL>();

	  while (classLoader != null) {
	    if (classLoader instanceof URLClassLoader)
	      for (URL url : ((URLClassLoader) classLoader).getURLs())
	        if (url.getFile().endsWith(".jar"))  // may want better way to detect jar files
	          jarUrls.add(url);

	    classLoader = classLoader.getParent();
	  }

	  Set<Class> classes = new HashSet<Class>();

	  for (URL url : jarUrls) {
	    JarInputStream stream = new JarInputStream(url.openStream()); // may want better way to open url connections
	    JarEntry entry = stream.getNextJarEntry();

	    while (entry != null) {
	      String name = entry.getName();
	      int i = name.lastIndexOf("/");

	      if (i > 0 && name.endsWith(".class") && name.substring(0, i).equals(packagePath)) 
	        classes.add(Class.forName(name.substring(0, name.length() - 6).replace("/", ".")));

	      entry = stream.getNextJarEntry();
	    }

	    stream.close();
	  }

	  return classes;
	}

}
