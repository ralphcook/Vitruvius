package org.rc.vitruvius.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class GenerateImageAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  MainFrame mainFrame = null;

  public GenerateImageAction(MainFrame mainFrame)
  {
    super("Image");
    this.mainFrame = mainFrame;
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    mainFrame.generateTileArrayFromTextAndUpdateImagePanel();
  }

}
