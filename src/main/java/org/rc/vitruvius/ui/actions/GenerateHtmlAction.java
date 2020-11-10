package org.rc.vitruvius.ui.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.rc.vitruvius.MessageListener;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.ui.GlyphyToolPanel;
import org.rc.vitruvius.ui.HtmlGenerator;
import org.rc.vitruvius.ui.I18n;

/**
 * Action to generate HTML from the given display.
 * @author rcook
 *
 */
public class GenerateHtmlAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  public enum Target { FORUM, FULL };
  
  private MessageListener messageListener = null;
  private GlyphyToolPanel homePanel       = null;
  private Target          target          = null;
  
  private String      html      = null;
  
  public GenerateHtmlAction(MessageListener messageListener, GlyphyToolPanel homePanel, Target target, String label)
  {
    super(label);
    this.messageListener = messageListener;
    this.homePanel = homePanel;
    this.target = target;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    TileArray tiles = homePanel.updateImagesPanelFromGlyphyText();
    if (tiles != null)
    {
      switch(target)
      {
      case FORUM: 
        html = HtmlGenerator.generateForumHtml(tiles); 
        break;
      case FULL : 
        html = HtmlGenerator.generateFullHtml(tiles); 
        break;
      default: throw new RuntimeException("Illegal type of HTML generation; programming error.");
      }
      
      copyTextToClipboard(html);
      String message = I18n.getString("HTMLCopiedMessageText");
      JOptionPane.showMessageDialog(homePanel, message);
      messageListener.addMessage(message);
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
