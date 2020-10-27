package org.rc.vitruvius.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class GenerateHtmlAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  public enum Target { FORUM, FULL };
  
  private MainFrame   frame     = null;
  private Target      target    = null;
  
  public GenerateHtmlAction(MainFrame frame, Target target, String label)
  {
    super(label);
    this.frame = frame;
    this.target = target;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    
//    Picture[][] pictures = frame.getUpdatedPictures();
    Picture[][] pictures = frame.generateImage();
    if (pictures != null)
    {
      String html = null;
      switch(target)
      {
      case FORUM: 
        html = HtmlGenerator.generateForumHtml(pictures); 
        break;
      case FULL : 
        html = HtmlGenerator.generateFullHtml(pictures); 
        break;
      default: frame.addMessage("Illegal option for HTML Action");
      }
      
      String[] options = { "Copy", "Done" };
      int returnCode = JOptionPane.showOptionDialog(frame, html, "html", JOptionPane.DEFAULT_OPTION, 
          JOptionPane.PLAIN_MESSAGE, null, options, null);
      if (returnCode == 0)
      {
        // user selected "Copy" button to terminate dialog; put the HTML text on the clipboard.
        StringSelection stringSelection = new StringSelection(html);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);  // no owner
      }
    }
  }

}
