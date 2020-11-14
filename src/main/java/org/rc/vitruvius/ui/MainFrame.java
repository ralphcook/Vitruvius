package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.util.prefs.Preferences;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.rc.vitruvius.UserMessageListener;

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
    
    messagesTextArea = new JTextArea(5, 80);
    JScrollPane messagesScrollPane = new JScrollPane(messagesTextArea);
    
    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel dragNDropPanel  = new DragNDropPanel(this);
    JPanel glyphyToolPanel = new GlyphyToolPanel(this);
 
    tabbedPane.addTab(I18n.getString("DragNDropTabbedPaneLabelText"),   dragNDropPanel);
    tabbedPane.addTab(I18n.getString("GlyphyToolTabbedPaneLabelText"), glyphyToolPanel);
    
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
  
  /**
   * Put the given message in the user messages area.
   */
  public void addMessage(String message)    { messagesTextArea.append(message); messagesTextArea.append("\n"); }
  /**
   * Clear the user messages area.
   */
  public void clearMessages()               { messagesTextArea.setText(""); }
  
}
