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
  private DragNDropImagesPane imagesPane = null;
  
  public DragNDropKeyListener(DragNDropImagesPane imagesPane)
  {
    this.imagesPane = imagesPane;
  }
  
  @Override
  public void keyTyped(KeyEvent event)
  {
    int keyChar = event.getKeyChar();
    switch (keyChar)
    {
    case KeyEvent.VK_BACK_SPACE:
    case KeyEvent.VK_DELETE:
      imagesPane.deleteSelectedItem();
      break;
    }
  }
}
