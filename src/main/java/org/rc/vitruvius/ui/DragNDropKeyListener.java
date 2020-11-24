package org.rc.vitruvius.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This listener catches keystrokes for the DragNDrop pane --
 * might be deleted when we implement keymaps for othings.
 * @author rcook
 *
 */
public class DragNDropKeyListener extends KeyAdapter
{
  private DragNDropLayeredPane layeredPane = null;
  
  public DragNDropKeyListener(DragNDropLayeredPane layeredPane)
  {
    this.layeredPane = layeredPane;
  }
  
  @Override
  public void keyTyped(KeyEvent event)
  {
    int keyChar = event.getKeyChar();
    switch (keyChar)
    {
    case KeyEvent.VK_BACK_SPACE:
    case KeyEvent.VK_DELETE:
      layeredPane.deleteSelectedItem();
      break;
    }
  }
}
