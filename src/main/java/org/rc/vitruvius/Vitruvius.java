package org.rc.vitruvius;

import org.rc.vitruvius.ui.MainFrame;

/**
 * Vitruvius is a city planner desktop application for the Caear III game; it provides a drag-and-drop
 * interface to place game structure glyphs on an image panel. It also supports a text-based
 * method for specifying blocks of structures to be translated into their glyph equivalents,
 * based on the "glyphy tool" from the website Heavengames.com. 
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
