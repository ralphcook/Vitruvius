package org.rc.vitruvius;

import org.rc.vitruvius.ui.MainFrame;

/**
 * Main code container for the program.
 * @author rcook
 *
 */
public class Vitruvius
{
  public static void main(String[] args) throws Exception
  {
    MainFrame mainFrame = new MainFrame();
    try
    {
      java.awt.EventQueue.invokeAndWait
      ( new Runnable() { public void run() { mainFrame.createDisplay(); } }
      );
    } 
    catch (Throwable t)
    {
      throw new Exception("Last chance exception handler", t);
    }
    mainFrame.setVisible(true);
  }
}
