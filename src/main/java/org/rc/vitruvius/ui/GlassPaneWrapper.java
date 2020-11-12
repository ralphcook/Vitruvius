package org.rc.vitruvius.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
//  private static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  
  private static final long serialVersionUID = 1L;
  private JPanel            glassPanel            = new JPanel();
  private Component         draggedComponent      = null;
  private boolean           draggedComponentAdded = false;
  private Container         wrappedPanel          = null;
  
  private Cursor            blankCursor           = null;
  private int               tileSize              = 25;     // TODO: get the actual tileSize from the main panel into this instance, and to change it.
  
  public GlassPaneWrapper(JComponent givenPanel)
  {
    wrappedPanel = givenPanel;
    
    // Transparent 16 x 16 pixel 'blank cursor' image.
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    
    glassPanel.setOpaque(false);
    glassPanel.setVisible(false);
    glassPanel.setFocusable(true);
//    glassPanel.setBorder(BorderFactory.createLineBorder(Color.red));
    
//    Dimension wrappedPanelSize = wrappedPanel.getPreferredSize();
//    wrappedPanel.setSize(wrappedPanel.getPreferredSize());
//    glassPanel.setSize(wrappedPanelSize);
//    glassPanel.setPreferredSize(wrappedPanelSize);
//    setPreferredSize(wrappedPanelSize);
//
//    add(wrappedPanel, JLayeredPane.DEFAULT_LAYER);
//    add(glassPanel, JLayeredPane.PALETTE_LAYER);
    
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
              setAllSizes(glassPanel, wrappedPanel.getSize());    // .getParent().getSize();    // worked.
              // TODO: test, do I need to do this again on resize for glassPanel?
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
              Point cursorPoint = new Point(e.getX(), e.getY());
              if (glassPanel.contains(cursorPoint)) { dropCurrentComponentCopy(e.getX(), e.getY()); }
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
        // these two methods prevent the dragged component from staying on the glasspanel if the mouse leaves it.
        @Override public void mouseExited(MouseEvent event)   { if (draggedComponent != null) { draggedComponent.setVisible(false); } }
        @Override public void mouseEntered(MouseEvent event)  { if (draggedComponent != null) { draggedComponent.setVisible(true);  } }
      }
    );

    Dimension wrappedPanelSize = wrappedPanel.getPreferredSize();
    wrappedPanel.setSize(wrappedPanel.getPreferredSize());

    add(wrappedPanel, JLayeredPane.DEFAULT_LAYER);
    add(glassPanel, JLayeredPane.PALETTE_LAYER);
    
    glassPanel.setSize(wrappedPanelSize);
    glassPanel.setPreferredSize(wrappedPanelSize);

    setPreferredSize(wrappedPanel.getPreferredSize());

    addComponentListener
    (
        // when this (the layered pane) gets a resize event,
        // resize the panel it contains to the same size.
        new ComponentAdapter()
        {
          @Override public void componentResized(ComponentEvent e)
          {
            Component c = e.getComponent();
            Dimension size = c.getSize();
            setAllSizes(wrappedPanel, c.getSize());
            wrappedPanel.setPreferredSize(size);
            wrappedPanel.setMaximumSize(size);
            wrappedPanel.setSize(size);
            wrappedPanel.repaint();
          }
        }
    );

  }
  
  private void setAllSizes(Component c, Dimension size)
  {
    c.setSize(size);
    c.setPreferredSize(size);
    c.setMaximumSize(size);
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
    // TODO: This only does JLabel; does it/can it do a Component?
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
