package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

public class ProgramExitAction extends AbstractAction
{
  private MainFrame mainFrame;
  
  public ProgramExitAction(MainFrame mainFrame)
  {
    super(I18n.getString("exitActionName"));
    this.mainFrame = mainFrame;
    
    String mnemonicKey = I18n.getString("exitActionMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(MNEMONIC_KEY, keyCode);
  }

  private static final long serialVersionUID = 1L;

  public void actionPerformed(ActionEvent eventAction)
  {
    int option = JOptionPane.YES_OPTION;    // by default, we'll bypass all the checking for unsaved changes
    // as if user said "Yes" to losing them.
    if (mainFrame.unsavedChanges())
    {
      String message = I18n.getString("unsavedChangesGenericMessage");
      String title = I18n.getString("unsavedChangesDialogTitle");
      option = JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION); 
    }

    if (option == JOptionPane.YES_OPTION)
    {
      System.exit(0);   // I tried mainFrame.dispose(), but that takes down eclipse as well.
    }
  }

}
