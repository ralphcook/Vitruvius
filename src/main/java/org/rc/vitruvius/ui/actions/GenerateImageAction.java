package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.text.TextTranslator;
import org.rc.vitruvius.ui.GlyphyToolPanel;
import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

/**
 * Action to generate the image from the current glyphy tool text.
 * @author rcook
 *
 */
public class GenerateImageAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  GlyphyToolPanel glyphyToolPanel = null;
  
  TextTranslator  textTranslator  = null;
  UserMessageListener messageListener = null;
  MainFrame       mainFrame       = null;

  public GenerateImageAction(GlyphyToolPanel glyphyPanel, MainFrame mainFrame, TextTranslator textTranslator)
  {
    super(I18n.getString("updateImageButtonText")); // "Update Image");
    this.glyphyToolPanel = glyphyPanel;
    this.mainFrame = mainFrame;
    this.textTranslator = textTranslator;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
//    glyphyToolPanel.updateImagesPanelFromGlyphyText();
    String text = glyphyToolPanel.getGlyphyText();
    TileArray tileArray = textTranslator.createTileArray(text);
    mainFrame.clearMessages(); 
    glyphyToolPanel.setTileArray(tileArray);
  }

}
