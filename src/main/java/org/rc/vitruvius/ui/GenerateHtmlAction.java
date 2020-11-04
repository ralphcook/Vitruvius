package org.rc.vitruvius.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.rc.vitruvius.model.TileArray;

public class GenerateHtmlAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  public enum Target { FORUM, FULL };
  
  private MainFrame   frame     = null;
  private Target      target    = null;
  
  private String      html      = null;
  
  public GenerateHtmlAction(MainFrame frame, Target target, String label)
  {
    super(label);
    this.frame = frame;
    this.target = target;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    TileArray tiles = frame.generateTileArrayFromTextAndUpdateImagePanel();
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
      default: frame.addMessage("Illegal option for HTML Action");
      }
      
      copyTextToClipboard(html);
      JOptionPane.showMessageDialog(frame, "HTML copied to clipboard");
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
