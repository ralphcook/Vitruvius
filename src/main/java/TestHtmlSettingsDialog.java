import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.rc.vitruvius.ui.HtmlSettingsDialog;
import org.rc.vitruvius.ui.MainFrame;

public class TestHtmlSettingsDialog extends JFrame
{

  private static final long serialVersionUID = 1L;

  public static void main(String[] args)
  {
    TestHtmlSettingsDialog me = new TestHtmlSettingsDialog();
    me.go();
  }
  
  private void go()
  {
    Preferences preferences = Preferences.userNodeForPackage(MainFrame.class);
    HtmlSettingsDialog dialog = new HtmlSettingsDialog(new MainFrame(), preferences);
    dialog.setVisible(true);
  }

}
