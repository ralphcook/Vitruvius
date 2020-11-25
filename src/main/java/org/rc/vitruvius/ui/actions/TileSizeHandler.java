package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

/**
 * Contains the actions for increasing and decreasing the tile size of the map panels; the
 * actions are stored as static singletons in this class.
 * 
 * @author rcook
 *
 */
// NOTE: if the class gains references to objects that are not readily available at construction
// time, look at the scheme currently implemented in GenerateImageAction -- the action is constructed
// without references it would need at runtime, and updated with those references after construction but
// before the UI is displayed.

public class TileSizeHandler
{
  private static TileSizeHandler localInstance;
  private static Decrease decrease = null;
  private static Increase increase = null;
  
  public static void initialize(MainFrame mainFrame)
  {
    localInstance = new TileSizeHandler();
    decrease = localInstance.new Decrease(mainFrame);
    increase = localInstance.new Increase(mainFrame);
  }
  
  public static Decrease getDecreaseAction() { return decrease; }
  public static Increase getIncreaseAction() { return increase; }
  
  public class Decrease extends AbstractAction
  {
    private static final long serialVersionUID = 1L;
    
    private MainFrame mainFrame = null;
    public Decrease(MainFrame givenMainFrame)             
    {
      super(I18n.getString("viewDecreaseTileSizeName"));
      setMnemonicKey(this, "viewDecreaseTileSizeKey");    // set the key for this menu option
      this.mainFrame = givenMainFrame;  
    }
    public void actionPerformed(ActionEvent actionEvent)  { mainFrame.decreaseTileSize();     }
  }
  
  public class Increase extends AbstractAction
  {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame = null;
    public Increase(MainFrame givenMainFrame)             
    {
      super(I18n.getString("viewIncreaseTileSizeName"));
      setMnemonicKey(this, "viewIncreaseTileSizeKey");    // set the key for this menu option
      this.mainFrame = givenMainFrame;  
    }
    public void actionPerformed(ActionEvent actionEvent)  { mainFrame.increaseTileSize();     }
  }
  
  private static void setMnemonicKey(AbstractAction action, String messagesBundleKey)
  {
    String mnemonicKey = I18n.getString(messagesBundleKey);
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    action.putValue(javax.swing.Action.MNEMONIC_KEY, keyCode);
  }
}
