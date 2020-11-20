package org.rc.vitruvius.model;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

/**
 * Contains file handling logic and constants for Vitruvius
 * @author rcook
 *
 */
public class FileHandler
{
  private MainFrame mainFrame = null;
  
  private FileOpenAction fileOpenAction = null;
  
  public FileHandler(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
    
    fileOpenAction = new FileOpenAction(mainFrame);
  }
  
  public FileOpenAction getOpenAction() { return fileOpenAction; }
  
  class FileOpenAction extends AbstractAction
  {
    private static final long serialVersionUID = 1L;
    private       MainFrame     mainFrame               = null;
    
    public FileOpenAction(MainFrame mainFrame)
    {
      // Establish action name; appears in menus, etc.
      super(I18n.getString("fileOpenActionName"));
      this.mainFrame           = mainFrame;
      
      // TODO: also need key map to contain hotkey mapping. Somewhere.
      
      // Establish key for alt-action that causes this action.
      String mnemonicKey = I18n.getString("fileOpenMnemonicKey");
      int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
      putValue(MNEMONIC_KEY, keyCode);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
      mainFrame.getCurrentWorkingPane().openFile();
    }
  }

}
