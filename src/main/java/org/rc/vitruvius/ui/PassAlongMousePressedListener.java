package org.rc.vitruvius.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

/**
 * When added as a MouseListener, dispatches the mouse event to the 
 * next component up the UI hierarchy; if there isn't one, it does nothing.
 * @author rcook
 *
 */
public class PassAlongMousePressedListener extends MouseAdapter
{
  public void mousePressed(MouseEvent e)
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
