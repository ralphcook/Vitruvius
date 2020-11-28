package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.HtmlSettingsDialog;
import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

public class DisplayHtmlSettingsDialogAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  private MainFrame mainFrame = null;
  private Preferences applicationPreferences = null;
  
  HtmlSettingsDialog dialog = null;
  
  public DisplayHtmlSettingsDialogAction(MainFrame mainFrame, Preferences applicationPreferences)
  {
    super(I18n.getString("displayHtmlSettingsDialogActionName"));
    this.mainFrame = mainFrame;
    this.applicationPreferences = applicationPreferences;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    if (dialog == null) { dialog = new HtmlSettingsDialog(mainFrame, applicationPreferences); }
    dialog.setVisible(true);
  }

}
