package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.FileHandler;
import org.rc.vitruvius.model.VitruviusWorkingPane;
import org.rc.vitruvius.text.TextTranslator;
import org.rc.vitruvius.ui.actions.DisplayHelpAction;
import org.rc.vitruvius.ui.actions.EndDragAction;
import org.rc.vitruvius.ui.actions.GenerateGlyphyImageAction;
import org.rc.vitruvius.ui.actions.GenerateHtmlAction;
import org.rc.vitruvius.ui.actions.ImageClearAction;
import org.rc.vitruvius.ui.actions.ProgramExitAction;
import org.rc.vitruvius.ui.actions.TileSizeHandler;

import rcutil.swing.SavedWindowPositionJFrame;

/**
 * Main JFrame for the Vitruvius program. 
 * @author rcook
 *
 */
public class MainFrame extends SavedWindowPositionJFrame implements UserMessageListener
{
  public static void say(String msg) { System.out.println(msg); }
  
  Preferences applicationPreferences = null;
  
  // these are the two "working panes" -- one for drag and drop, one for the glyphy tool
  DragNDropPanel  dragNDropPanel  = null;
  GlyphyToolPanel glyphyToolPanel = null;
  JTabbedPane     tabbedPane      = null;

  // this is the text area that gets messages for the user; this class, as a UserMessageListener,
  // recieves the messages to put there.
  private JTextArea   messagesTextArea  = null;
  
  FileHandler fileHandler = null;
  
  // holds a reference to the current tabbed pane; will be either dragNDropPanel or glyphyToolPanel
  private VitruviusWorkingPane   currentWorkingPane       = null;
  public  VitruviusWorkingPane   getCurrentWorkingPane() { return currentWorkingPane; }
  public  void                   setCurrentWorkingPane(VitruviusWorkingPane pane) { currentWorkingPane = pane; }
  
//  TileSizeHandler tileSizeHandler = null;
  
  private static final long serialVersionUID = 1L;

  public MainFrame() 
  {
    TileSizeHandler.initialize(this);
    applicationPreferences = Preferences.userNodeForPackage(this.getClass());

    dragNDropPanel  = new DragNDropPanel(this, this, applicationPreferences);
    glyphyToolPanel = new GlyphyToolPanel(this, applicationPreferences);
    fileHandler = new FileHandler(this);
    GenerateGlyphyImageAction.getSingleton().updateReferences(this, glyphyToolPanel, new TextTranslator(this));
    
    try
    {
      URL resourceUrl = this.getClass().getResource("/icon/VitruvianMan.png");
      Image icon = Toolkit.getDefaultToolkit().getImage(resourceUrl);
      setIconImage(icon);
    }
    catch (Exception e) { e.printStackTrace(); }
    
  }
  
  @Override
  public void windowClosing(WindowEvent windowEvent)
  {
    // NOTE: The superclass already adds this class as a window event listener,
    // so we do not do it again here. We do call the superclass' method if they
    // have no unsaved changes or say they want to continue...
    if (unsavedChanges())
    {
//      say("windowClosing()");
      // Asks if they want to continue, and has a cancel option.
      int jOption = JOptionPane.showConfirmDialog(this, 
                                                  I18n.getString("unsavedChangesGenericMessage"),
                                                  I18n.getString("unsavedChangesDialogTitle"),
                                                  JOptionPane.YES_NO_OPTION
                                                  );
      if (jOption == JOptionPane.YES_OPTION)  
      {
        super.windowClosing(windowEvent);
        System.exit(0); 
      }
    }
  }
  
  /**
   * Create the UI for the program.
   */
  public void createDisplay()
  {
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    // numbers here represent default window position for first time program is run
    initializeSavedWindowPosition(applicationPreferences, 100, 100, 500, 400);
    
    Picture.checkImageFiles();    // outputs syserror messages if we have glyphy tool letters that are supposed to 
                                  // correspond to particular glyphs, but we have no file with the filename saved 
                                  // for that letter or the file doesn't exist.
    
    tabbedPane = new JTabbedPane();
 
    tabbedPane.addTab(I18n.getString("dragNDropTabbedPaneLabelText"),   dragNDropPanel);
    tabbedPane.addTab(I18n.getString("glyphyToolTabbedPaneLabelText"), glyphyToolPanel);
    
    messagesTextArea = new JTextArea(5, 80);
    messagesTextArea.setEditable(false);
    messagesTextArea.setFont(new Font("Arial", Font.BOLD, 14));    // new Font(Font.MONOSPACED, 20, Font.PLAIN);
    JScrollPane messagesScrollPane = new JScrollPane(messagesTextArea);
    
    add(tabbedPane, BorderLayout.CENTER);
    add(messagesScrollPane, BorderLayout.SOUTH);
    
    pack();
    
    JMenuBar menuBar = createMenuBar(this);
    setJMenuBar(menuBar);
    
    setWindowPosition();      // set position of the window on the screen.
    
    setCurrentWorkingPane(dragNDropPanel);
    
    tabbedPane.addChangeListener                // trap switch to other tabbed pane so we can
    (                                           // update the 'current working pane'
        new ChangeListener()
        {
          public void stateChanged(ChangeEvent event)
          {
            // set the working pane to the newly selected pane.
            Object source = event.getSource();
            JTabbedPane tabbedPane = (JTabbedPane)source;
            VitruviusWorkingPane workingPane = (VitruviusWorkingPane)tabbedPane.getSelectedComponent();
            currentWorkingPane = workingPane;
            currentWorkingPane.setGenerateImageActionStatus();
          }
        }
    );
    
    createKeyBindings();
  }
  
  private void createKeyBindings()
  {
    InputMap  tpInputMap  = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    ActionMap tpActionMap = tabbedPane.getActionMap();

//    KeyStroke controlO = KeyStroke.getKeyStroke("control O");
//    tabbedPaneInputMap.put(controlO, "Open");  //KeyStroke.getKeyStroke("control O")
//    tabbedPaneActionMap.put("Open", fileHandler.getOpenAction());
    
    setKeyBinding(tpInputMap, tpActionMap, "control O", "Open",     fileHandler.getOpenAction());
    setKeyBinding(tpInputMap, tpActionMap, "control S", "Save",     fileHandler.getSaveAction());
    setKeyBinding(tpInputMap, tpActionMap, "ESCAPE",    "exitDrag", new EndDragAction(dragNDropPanel));
    setKeyBinding(tpInputMap, tpActionMap, KeyEvent.VK_MINUS,       "decreaseTileSize", TileSizeHandler.getDecreaseAction());
    setKeyBinding(tpInputMap, tpActionMap, KeyEvent.VK_UNDERSCORE,  "decreaseTileSize", TileSizeHandler.getDecreaseAction());
    setKeyBinding(tpInputMap, tpActionMap, KeyEvent.VK_PLUS,        "increaseTileSize", TileSizeHandler.getIncreaseAction());
    setKeyBinding(tpInputMap, tpActionMap, KeyEvent.VK_EQUALS,      "increaseTileSize", TileSizeHandler.getIncreaseAction());
  }
  
  private void setKeyBinding(InputMap inputMap, ActionMap actionMap, int keyCode, String actionName, Action action)
  {
    KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, 0);
    setKeyBinding(inputMap, actionMap, keyStroke, actionName, action);
  }
  
  private void setKeyBinding(InputMap inputMap, ActionMap actionMap, String keyString, String actionName, Action action)
  {
    KeyStroke keyStroke = KeyStroke.getKeyStroke(keyString);
    setKeyBinding(inputMap, actionMap, keyStroke, actionName, action);
  }
  
  private void setKeyBinding(InputMap inputMap, ActionMap actionMap, KeyStroke keyStroke, String actionName, Action action)
  {
    inputMap.put(keyStroke, actionName);
    actionMap.put(actionName, action);
  }
  
  private JMenuBar createMenuBar(MainFrame mainFrame)
  {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu fileMenu = getI18nJMenu("fileMenuName", "fileMenuMnemonicKey");

    fileMenu.add(new JMenuItem(fileHandler.getOpenAction()));
    fileMenu.add(new JMenuItem(fileHandler.getSaveAction()));
    fileMenu.add(new JMenuItem(fileHandler.getSaveAsAction()));
    fileMenu.add(new JMenuItem(new ImageClearAction(this)));
    fileMenu.add(new JSeparator());
    fileMenu.add(new JMenuItem(new ProgramExitAction(mainFrame)));
    
    JMenu viewMenu = getI18nJMenu("viewMenuName", "viewMenuMnemonicKey");
    
    viewMenu.add(new JMenuItem(TileSizeHandler.getIncreaseAction()));
    viewMenu.add(new JMenuItem(TileSizeHandler.getDecreaseAction()));
    viewMenu.add(new JMenuItem(GenerateGlyphyImageAction.getSingleton()));
//    viewMenu.add(new JMenuItem(new GenerateImageAction(glyphyToolPanel, mainFrame, new TextTranslator(mainFrame))));
    
    JMenu htmlMenu = getI18nJMenu("htmlMenuName", "htmlMenuMnemonicKey");
    
    htmlMenu.add(new JMenuItem(new GenerateHtmlAction(this, GenerateHtmlAction.Target.FULL, I18n.getString("generateFullHtmlActionName"))));
    htmlMenu.add(new JMenuItem(new GenerateHtmlAction(this, GenerateHtmlAction.Target.FORUM, I18n.getString("generateForumHtmlActionName"))));
    
    JMenu helpMenu = getI18nJMenu("helpMenuName", "helpMenuMnemonicKey");
    helpMenu.add(new JMenuItem(new DisplayHelpAction(this, "VitruviusHelp.html", "displayHelpActionName", "displayHelpMnemonicKey")));
    helpMenu.add(new JMenuItem(new DisplayHelpAction(this, "VitruviusAbout.html", "displayAboutActionName", "displayAboutMnemonicKey")));
    
//    AbstractAction programHelpAction = null; // new ProgramHelpAction();
//    AbstractAction creditsAction = null; // new CreditsAction();
    
    menuBar.add(fileMenu);
    menuBar.add(viewMenu);
    menuBar.add(htmlMenu);
    menuBar.add(helpMenu);
    return menuBar;
  }

  public void decreaseTileSize() {  getCurrentWorkingPane().decreaseTileSize(); }
  public void increaseTileSize() {  getCurrentWorkingPane().increaseTileSize(); }
  public void clearCurrentPanel() { getCurrentWorkingPane().clearPanel(); }
  
  /**
   * Return a menu (not a menu item) where the menu name is accessed with the given
   * resources key, and the mnemonic is the character with the given resources key.
   * The mnemonic key may be null, in which case there is no keyboard access to invoke
   * the menu, it would have to be accessed with a mouse.
   * @param textKey
   * @param mnemonicKey
   * @return
   */
  private JMenu getI18nJMenu(String textKey, String mnemonicKey)  
  { 
    JMenu menu = new JMenu(I18n.getString(textKey));
    if (mnemonicKey != null)
    {
      String mnemonickeyChar = I18n.getString(mnemonicKey);
      menu.setMnemonic(mnemonickeyChar.charAt(0));
    }
    return menu;
  }  
  
  /**
   * Put the given message in the user messages area, and add a newline to the end of it.
   */
  public void addMessage(String message)    { messagesTextArea.append(message); messagesTextArea.append("\n"); }
  
  /**
   * Clear the user messages area.
   */
  public void clearMessages()               { messagesTextArea.setText(""); }
  
  /**
   * Return true/false indicating whether there are unsaved changes on either working pane (or both).
   * @return
   */
  public boolean unsavedChanges()
  {
    boolean result = false;
    result = dragNDropPanel.unsavedChanges();
    if (!result) { result = glyphyToolPanel.unsavedChanges(); }
    return result;
  }
  
}
