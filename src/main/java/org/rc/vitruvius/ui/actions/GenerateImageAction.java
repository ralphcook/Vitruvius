package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.GlyphyToolPanel;

/**
 * Action to generate the image from the current glyphy tool text.
 * @author rcook
 *
 */
public class GenerateImageAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  GlyphyToolPanel homePanel = null;

  public GenerateImageAction(GlyphyToolPanel homePanel)
  {
    super("Update Image");
    this.homePanel = homePanel;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    homePanel.updateImagesPanelFromGlyphyText();
  }

}
