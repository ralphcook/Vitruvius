package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.util.prefs.Preferences;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.VitruviusWorkingPane;
import org.rc.vitruvius.ui.actions.FileCloseAction;
import org.rc.vitruvius.ui.actions.FileOpenAction;
import org.rc.vitruvius.ui.actions.FileSaveAction;

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
  DragNDropPanel dragNDropPanel = null;
  GlyphyToolPanel glyphyToolPanel = null;

  // this is the text area that gets messages for the user; this class, as a UserMessageListener,
  // recieves the messages to put there.
  private JTextArea   messagesTextArea  = null;
  
  // holds a reference to the current tabbed pane
  private VitruviusWorkingPane   currentWorkingPane       = null;
  public  VitruviusWorkingPane   getCurrentWorkingPane() { return currentWorkingPane; }
  public  void                   setCurrentWorkingPane(VitruviusWorkingPane pane) { currentWorkingPane = pane; }
  
  private static final long serialVersionUID = 1L;

  public MainFrame() {}
  
  /**
   * Create the UI for the program.
   */
  public void createDisplay()
  {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    applicationPreferences = Preferences.userNodeForPackage(this.getClass());
    // numbers here represent default window position for first time program is run
    initializeSavedWindowPosition(applicationPreferences, 100, 100, 500, 400);
    
    Picture.checkImageFiles();    // outputs syserror messages if we have glyphy tool letters that are supposed to 
                                  // correspond to particular glyphs, but we have no file with the filename saved 
                                  // for that letter or the file doesn't exist.
    
    JMenuBar menuBar = createMenuBar();
    setJMenuBar(menuBar);
    
    JTabbedPane tabbedPane = new JTabbedPane();
    dragNDropPanel  = new DragNDropPanel(this, this, applicationPreferences);
    glyphyToolPanel = new GlyphyToolPanel(this);
 
    tabbedPane.addTab(I18n.getString("dragNDropTabbedPaneLabelText"),   dragNDropPanel);
    tabbedPane.addTab(I18n.getString("glyphyToolTabbedPaneLabelText"), glyphyToolPanel);
    
    messagesTextArea = new JTextArea(5, 80);
    JScrollPane messagesScrollPane = new JScrollPane(messagesTextArea);
    
    add(tabbedPane, BorderLayout.CENTER);
    add(messagesScrollPane, BorderLayout.SOUTH);
    
    pack();
    
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
          }
        }
    );
  }
  
  private JMenuBar createMenuBar()
  {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu fileMenu = getI18nJMenu("fileMenuName", "fileMenuMnemonicKey");

    fileMenu.add(new JMenuItem(new FileOpenAction(this)));
    fileMenu.add(new JMenuItem(new FileSaveAction(this)));
    fileMenu.add(new JMenuItem(new FileCloseAction(this)));
    
//    AbstractAction fileOpenAction = new FileOpenAction(this, applicationPreferences);
//    
//    JMenuItem fileOpenMenuItem = new JMenuItem(fileOpenAction); 
//    AbstractAction fileSaveAction = null; // new FileSaveAction();
//    AbstractAction fileCloseAction = null; // new FileCloseAction();
//    AbstractAction exitAction = null; // new ExitAction();
//    
//    AbstractAction setWindowSizeAction = null; // new SetWindowSizeAction();
//    AbstractAction decreaseTileSizeAction = null; // new DecreaseTileSizeAction();
//    AbstractAction increaseTileSizeAction  = null; // new IncreaseTileSizeAction();
//    
//    AbstractAction glyphyUpdateImageAction = null; // new GlyphyUpdateImageAction();
//    AbstractAction generateFullHTMLAction = null; // new GenerateFullHTMLAction();
//    AbstractAction generateForumHTMLAction = null; // new GenerateForumHTMLAction();
//    
//    AbstractAction programHelpAction = null; // new ProgramHelpAction();
//    AbstractAction creditsAction = null; // new CreditsAction();
    
    menuBar.add(fileMenu);
    return menuBar;
  }

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
  /**eee
   * Clear the user messages area.
   */
  public void clearMessages()               { messagesTextArea.setText(""); }
  
}
