package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import org.rc.vitruvius.UserMessageListener;
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
//  public static void say(String msg) { System.out.println(msg); }
  
  private JTextArea   messagesTextArea  = null;
  
  Preferences applicationPreferences = null;
  
  DragNDropPanel dragNDropPanel = null;
  GlyphyToolPanel glyphyToolPanel = null;
  
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
    
    Picture.checkImageFiles();    // outputs syserror messages if we have letters that are supposed to correspond to particular buildings,
                                  // but we have no file with the filename saved for that letter.
    
    JMenuBar menuBar = createMenuBar();
    setJMenuBar(menuBar);
    
    messagesTextArea = new JTextArea(5, 80);
    JScrollPane messagesScrollPane = new JScrollPane(messagesTextArea);
    
    JTabbedPane tabbedPane = new JTabbedPane();
    dragNDropPanel  = new DragNDropPanel(this);
    glyphyToolPanel = new GlyphyToolPanel(this);
 
    tabbedPane.addTab(I18n.getString("dragNDropTabbedPaneLabelText"),   dragNDropPanel);
    tabbedPane.addTab(I18n.getString("glyphyToolTabbedPaneLabelText"), glyphyToolPanel);
    
    add(tabbedPane, BorderLayout.CENTER);
    add(messagesScrollPane, BorderLayout.SOUTH);
    
    pack();
    
    setWindowPosition();      // set position of the window on the screen.
    
    addKeyListener(new DragNDropKeyListener(null));
    
    // DEBUG
//    textMapTextArea.setText
//    (
//          "GGGGFGGGG"
//        + "\nH.H.GH.H."
//        + "\n........."
//        + "\n#########"
//        + "\nH.H.GH.H."
//        + "\n........."
//        + "\nGGGGFGGGG"
//    );
    
  }
  
  private JMenuBar createMenuBar()
  {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu fileMenu = getI18nJMenu("fileMenuName", "fileMenuMnemonicKey");

    fileMenu.add(new JMenuItem(new FileOpenAction(this, applicationPreferences)));
    fileMenu.add(new JMenuItem(new FileSaveAction(this, applicationPreferences)));
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
  
//  private JMenuItem getFileOpenMenuItem(AbstractAction fileOpenAction)
//  {
//    JMenuItem menuItem = new JMenuItem(fileOpenAction);
//    return menuItem;
//  }
  
  public void openTileFile(File fileToOpen) throws Exception
  {
    dragNDropPanel.openTileFile(fileToOpen);
  }
  
  public void closeTileFile()
  {
    String message = "File Closed; figure out about different panels, different files.";
    System.out.println(message);
    addMessage(message);
  }
  
  public void saveTileFile(File fileToSave)
  {
    addMessage("saving the file DEBUG");
    dragNDropPanel.saveTileFile(fileToSave);
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
