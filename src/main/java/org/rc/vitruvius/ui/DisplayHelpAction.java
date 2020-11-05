package org.rc.vitruvius.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

/**
 * Action to display the help text.
 * @author rcook
 *
 */
public class DisplayHelpAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  JDialog dialog = null;
  
  public DisplayHelpAction(JDialog dialog)
  {
    super("Text Help");
    this.dialog = dialog;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    dialog.setVisible(true);
  }

}
