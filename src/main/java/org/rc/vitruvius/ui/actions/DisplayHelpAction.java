package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.HtmlDialog;
import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

public class DisplayHelpAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  
  private MainFrame mainFrame = null;
  
  public DisplayHelpAction(MainFrame mainFrame)
  {
    super(I18n.getString("displayHelpActionName"));
    this.mainFrame = mainFrame;
    
    String mnemonicKey = I18n.getString("displayHelpMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(MNEMONIC_KEY, keyCode);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    HtmlDialog dialog = new HtmlDialog(mainFrame);
    dialog.setVisible(true);
  }

}
