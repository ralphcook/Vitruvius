package org.rc.vitruvius.ui.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.text.TextTranslator;
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
  
  private UserMessageListener messageListener = null;
  private GlyphyToolPanel glyphyToolPanel = null;
  private Target          target          = null;
  
  private TextTranslator  textTranslator  = null;
  
  private String      html      = null;
  
  public GenerateHtmlAction(UserMessageListener messageListener, GlyphyToolPanel glyphyToolPanel, Target target, String label)
  {
    super(label);
    this.messageListener = messageListener;
    this.glyphyToolPanel = glyphyToolPanel;
    this.target = target;
    
    textTranslator = new TextTranslator(messageListener);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    String text = glyphyToolPanel.getGlyphyText();
    TileArray tiles = textTranslator.createTileArray(text);
//    TileArray tiles = homePanel.updateImagesPanelFromGlyphyText();
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
      String message = I18n.getString("htmlCopiedMessageText");
      messageListener.addMessage(message);
      JOptionPane.showMessageDialog(glyphyToolPanel, message);
//      mainFrame.clearMessages(); 
//      glyphyToolPanel.setTileArray(tiles);
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
