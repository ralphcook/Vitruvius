package org.rc.vitruvius.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * When added as a MouseListener, dispatches mousePressed and mouseDragged events
 * to the next component up the UI hierarchy; if there isn't one, it does nothing.
 * 
 * @author rcook
 *
 */
public class PassAlongMousePressedListener extends MouseAdapter
{
  public static void say(String s) { System.out.println(s); }
  
  public void mousePressed(MouseEvent e) { passAlongEvent(e); }
  public void mouseDragged(MouseEvent e) { passAlongEvent(e); }
  
  private void passAlongEvent(MouseEvent e)
  {
    Component component = (Component)e.getSource();
    Component parent    = component.getParent();
    if (parent != null)
    {
      MouseEvent parentEvent = SwingUtilities.convertMouseEvent(component, e, parent);
      parent.dispatchEvent(parentEvent);
    }
  }
}
