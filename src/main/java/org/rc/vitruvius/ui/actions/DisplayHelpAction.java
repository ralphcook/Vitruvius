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
  private String    resourceName = null;
  
  public DisplayHelpAction(MainFrame mainFrame, String resourceName, String nameKey, String mnemonicKey)
  {
    super(I18n.getString(nameKey));
    this.mainFrame = mainFrame;
    this.resourceName = resourceName;
    
    if (mnemonicKey != null)
    {
      String mnemonic = I18n.getString(mnemonicKey);
      int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonic.charAt(0));
      putValue(MNEMONIC_KEY, keyCode);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    HtmlDialog.display(mainFrame, resourceName);
//    HtmlDialog dialog = new HtmlDialog(mainFrame);
//    dialog.setVisible(true);
  }

}
