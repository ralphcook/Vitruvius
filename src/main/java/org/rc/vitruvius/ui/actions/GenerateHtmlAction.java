package org.rc.vitruvius.ui.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

/**
 * Action to generate HTML from the given display.
 * @author rcook
 *
 */
public class GenerateHtmlAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  public enum Target { FORUM, FULL };
  
  private MainFrame       mainFrame = null;
  private Target          target    = null;
  
  public GenerateHtmlAction(MainFrame mainFrame, Target target, String label)
  {
    super(label);
    this.mainFrame  = mainFrame;
    this.target     = target;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    String html = mainFrame.getCurrentWorkingPane().generateHtml(target);
    if (html == null)
    {
      JOptionPane.showMessageDialog(mainFrame, I18n.getString("noImagesForHtml"));
    }
    else
    {
      copyTextToClipboard(html);
      
      // put message in message area and in a popup message dialog.
      String message = I18n.getString("htmlCopiedMessageText");
      mainFrame.addMessage(message);
      JOptionPane.showMessageDialog(mainFrame, message);
    }
  }
  
  private void copyTextToClipboard(String text)
  {
    // user selected "Copy" button to terminate dialog; put the HTML text on the clipboard.
    StringSelection stringSelection = new StringSelection(text);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);  // no owner
  }

}
