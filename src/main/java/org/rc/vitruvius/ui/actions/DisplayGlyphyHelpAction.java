package org.rc.vitruvius.ui.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.rc.vitruvius.ui.I18n;

/**
 * Action to display the 'glyphy tool' help text, showing what characters
 * correspond to what Caesar III structures. All this action does is make
 * the dialog visible.
 * 
 * @see org.rc.vitruvius.ui.GlyphyHelpDialog
 * 
 * @author rcook
 *
 */
public class DisplayGlyphyHelpAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  private JFrame mainFrame = null;
  private JDialog dialog = null;
  
  public DisplayGlyphyHelpAction(JFrame mainFrame, JDialog dialog)
  {
    super(I18n.getString("glyphyTextHelpButtonText"));
    this.mainFrame = mainFrame;
    this.dialog = dialog;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
//    dialog.setLocationRelativeTo(mainFrame);
    Rectangle mainFrameLocation = mainFrame.getBounds();
    mainFrameLocation.width = 400;
    dialog.setBounds(mainFrameLocation);
    dialog.setVisible(true);
  }

}
