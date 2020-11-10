package org.rc.vitruvius.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GlassPaneWrapper extends JLayeredPane
{
  private static void say(String s) { System.out.println(s); }
  private static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  
  private static final long serialVersionUID = 1L;
  private JPanel            glassPanel = new JPanel();
  private Component         draggedComponent = null;
  private boolean           draggedComponentAdded = false;
  private Container         wrappedPanel = null;
  
  private Cursor            blankCursor = null;
  private int               tileSize    = 25;
  // TODO: get the actual tileSize from the main panel into this instance.
  // include a way to change it.
  
  public GlassPaneWrapper(JComponent givenPanel)
  {
    wrappedPanel = givenPanel;
    
    // Transparent 16 x 16 pixel 'blank cursor' image.
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    
    glassPanel.setOpaque(false);
    glassPanel.setVisible(false);
    glassPanel.setFocusable(true);
    
    glassPanel.addMouseMotionListener
    (new MouseAdapter() 
      {
        @Override public void mouseMoved(MouseEvent event)
        {
          if (draggedComponent != null)
          {
            if (!draggedComponentAdded) 
            {
              add(draggedComponent, JLayeredPane.DEFAULT_LAYER);
              add(draggedComponent, JLayeredPane.DRAG_LAYER);
              draggedComponentAdded = true;
            }
            Point newPoint = getComponentPoint(event.getX(), event.getY());
            draggedComponent.setLocation(newPoint.x, newPoint.y);
          }
        }
      }
    );
    
    glassPanel.addMouseListener
    (
      new MouseAdapter()
      {
        @Override
        public void mousePressed(MouseEvent e)
        {
          int button = e.getButton();
          switch (button)
          {
          case MouseEvent.BUTTON1:
            if (draggedComponent != null)
            {
              // drop a copy of the current component being dragged.
              dropCurrentComponentCopy(e.getX(), e.getY());
            }
            break;
          case MouseEvent.BUTTON3:
            if (draggedComponent != null)
            {
              // cancel current component being dragged.
              deactivateDragging();
            }
            break;
          default:
            say("Some mouse event besides button 1 or 2, no effect");
            break;
          }
        }
      }
    );

    Dimension wrappedPanelSize = wrappedPanel.getPreferredSize();
    wrappedPanel.setSize(wrappedPanel.getPreferredSize());

    add(wrappedPanel, JLayeredPane.DEFAULT_LAYER);
    add(glassPanel, JLayeredPane.PALETTE_LAYER);
    
    glassPanel.setSize(wrappedPanelSize);
    glassPanel.setPreferredSize(wrappedPanelSize);

    setPreferredSize(wrappedPanel.getPreferredSize());
  }

  /**
   * Given a cursor position, determine the x,y of the upper left corner of the current
   * dragged component.
   * @param x
   * @param y
   * @return
   */
  private Point getComponentPoint(int x, int y)
  {
    Dimension d = draggedComponent.getSize();
    int xOffset = d.width / 2;
    int yOffset = d.height / 2;
    int xTile = (x - xOffset) / tileSize;
    int yTile = (y - yOffset) / tileSize;
    int newX = xTile * tileSize;
    int newY = yTile * tileSize;
    return new Point(newX, newY);
  }
  
  private void dropCurrentComponentCopy(int cursorX, int cursorY)
  {
    // make a copy of the current component
    // TODO: determine if this needs to cover more than a JLabel, which is all this will do.
    JLabel copiedLabel = null;
    Dimension copiedLabelSize = null;
    if (draggedComponent instanceof JLabel)
    {
      JLabel currentLabel = (JLabel) draggedComponent;
      copiedLabel = new JLabel();
      copiedLabel.setIcon(currentLabel.getIcon());
      copiedLabelSize = currentLabel.getSize();
      copiedLabel.setSize(copiedLabelSize);
      copiedLabel.setPreferredSize(copiedLabelSize);
      copiedLabel.setToolTipText(currentLabel.getToolTipText());
    }
    Point componentPoint = getComponentPoint(cursorX, cursorY);
    copiedLabel.setLocation(componentPoint.x, componentPoint.y);

    // TODO: limit drop, perhaps drag, to x/y of the map panel.
    
    // add the component to the wrapped pane.
    wrappedPanel.add(copiedLabel);
    copiedLabel.setVisible(true);
    
    wrappedPanel.repaint();
  }
  
  /**
   * Activate the glass panel for dragging the given component.
   * @param c
   */
  public void activateDragging(Component c)
  {
    draggedComponent = c;
    draggedComponentAdded = false;
    glassPanel.setVisible(true);
    glassPanel.requestFocusInWindow();
    glassPanel.setFocusTraversalKeysEnabled(false);
    glassPanel.setCursor(blankCursor);
  }
  
  /**
   * Deactivate the glass panel, remove the current dragged component.
   */
  public void deactivateDragging()
  {
    if (draggedComponent != null)
    {
      remove(draggedComponent);
      draggedComponent = null; 
      glassPanel.setVisible(false);
      glassPanel.setCursor(null);
    }
  }
  
}
