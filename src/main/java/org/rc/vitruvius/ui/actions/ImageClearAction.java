package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

public class ImageClearAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  
  private MainFrame mainFrame = null;
  
  public ImageClearAction(MainFrame mainFrame)
  {
    super(I18n.getString("fileCloseActionName"));
    this.mainFrame = mainFrame;

    String mnemonicKey = I18n.getString("fileCloseMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(MNEMONIC_KEY, keyCode);
  }

  @Override
  public void actionPerformed(ActionEvent e) 
  {
    String message = I18n.getString("confirmCloseWithoutSave");
    String title   = I18n.getString("confirmCloseDialogTitle");
    int option = JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION);
    if (option == JOptionPane.OK_OPTION)
    {
      try 
      { 
//        mainFrame.closeTileFile(); 
      }
      catch (Exception exception) { throw new RuntimeException("problem in File/Close", exception); }
    }
  }

}
