package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.text.TextTranslator;
import org.rc.vitruvius.ui.GlyphyToolPanel;
import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

/**
 * Action to generate the image from the current glyphy tool text.
 * <P>
 * Because this action is passed into UI panels for use, and also references UI panels
 * to do its job, there is a chicken-and-egg problem attempting to put those references
 * in the constructor for the action. Therefore, this action can be constructed without
 * those references, and the references updated afterwards (updateReferences(...)).
 *  
 * @author rcook
 *
 */
public class GenerateGlyphyImageAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;

  private static GenerateGlyphyImageAction generateImageAction = new GenerateGlyphyImageAction();
  public static GenerateGlyphyImageAction getSingleton() { return generateImageAction; }

  private MainFrame           mainFrame       = null;
  private GlyphyToolPanel     glyphyToolPanel = null;
  private TextTranslator      textTranslator  = null;
  
  public GenerateGlyphyImageAction()
  {
    super(I18n.getString("updateImageButtonText")); // "Update Image");
    
    String mnemonicKey = I18n.getString("updateImageMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(javax.swing.Action.MNEMONIC_KEY, keyCode);
    
    setEnabled(false);
  }

  public void updateReferences(MainFrame mainFrame, GlyphyToolPanel glyphyToolPanel, TextTranslator textTranslator)
  {
    this.mainFrame = mainFrame;
    this.glyphyToolPanel = glyphyToolPanel;
    this.textTranslator = textTranslator;
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    String text = glyphyToolPanel.getGlyphyText();
    TileArray tileArray = textTranslator.createTileArray(text);
    mainFrame.clearMessages(); 
    glyphyToolPanel.setTileArray(tileArray);
  }

}
