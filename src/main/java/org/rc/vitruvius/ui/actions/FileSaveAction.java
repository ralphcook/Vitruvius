package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

public class FileSaveAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  private MainFrame   mainFrame = null;
  
  public FileSaveAction(MainFrame mainFrame)
  {
    super(I18n.getString("fileSaveActionName"));
    this.mainFrame = mainFrame;

    String mnemonicKey = I18n.getString("fileSaveMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(MNEMONIC_KEY, keyCode);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    mainFrame.getCurrentWorkingPane().saveFile();
  }

}
