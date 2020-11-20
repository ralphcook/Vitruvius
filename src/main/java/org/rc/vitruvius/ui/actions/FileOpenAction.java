package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

// TODO: this is very similar to the FileSaveAction -- can they be combined somehow?
@SuppressWarnings("serial")
public class FileOpenAction extends AbstractAction
{
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
