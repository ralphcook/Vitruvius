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

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.Draggable;
import org.rc.vitruvius.model.TileArray;

/**
 * This wrapper extends JLayeredPane, allowing operations above the given 'wrapped' panel 
 * such as dragging a component above everything else on the panel. This partaicular one 
 * has the mouse listeners to implement making a dragged component operate as a cursor, dropping
 * that component on the panel with a mouse click, etc.
 * 
 * @author rcook
 *
 */
public class DragNDropImagesPane extends JLayeredPane
{
  private static void say(String s) { System.out.println(s); }
//  private static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  
  private static final long serialVersionUID = 1L;
  private Container           wrappedPanel          = null;
  private JPanel              glassPanel            = new JPanel();
  
  private Draggable           draggableItem         = null;     // item being dragged
  private JLabel              draggableJLabel       = null;     // JLabel created from draggable, actual screen component
                                                              // it is specific to tileSize, needs recalc if that changes
  private boolean             draggableAdded        = false;

  private TileArray           tileArray             = null;
  
  private Cursor              blankCursor           = null;
  private int                 tileSize              = 25;     // TODO: get the actual tileSize from the main panel into this instance, and to change it.
  
  private UserMessageListener userMessageListener       = null;
  
  public DragNDropImagesPane(JComponent givenPanel, TileArray tileArray, UserMessageListener givenListener)
  {
    wrappedPanel = givenPanel;
    this.tileArray = tileArray;
    this.userMessageListener = givenListener;
    
    // Transparent 16 x 16 pixel 'blank cursor' image.
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    
    glassPanel.setOpaque(false);
    glassPanel.setVisible(false);
    glassPanel.setFocusable(true);
//    glassPanel.setBorder(BorderFactory.createLineBorder(Color.red));
    
    glassPanel.addMouseMotionListener
    (new MouseAdapter() 
      {
        @Override public void mouseMoved(MouseEvent event)
        {
          if (draggableItem != null)
          {
            if (!draggableAdded) 
            {
              draggableJLabel = draggableItem.getJLabel(tileSize);
              draggableJLabel.setText("");      // TODO: eliminates label during dragging; is this the best way to do this?
              add(draggableJLabel, JLayeredPane.DEFAULT_LAYER);
              add(draggableJLabel, JLayeredPane.DRAG_LAYER);
              setAllComponentSizes(glassPanel, wrappedPanel.getSize());    // .getParent().getSize();    // worked.
              draggableAdded = true;
            }
            Point newPoint = getComponentPoint(event.getX(), event.getY());
            draggableJLabel.setLocation(newPoint.x, newPoint.y);
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
            if (draggableItem != null)
            {
              // drop a copy of the current dragged item.
              Point cursorPoint = e.getPoint();
              if (glassPanel.contains(cursorPoint)) 
              { 
                if (insertDraggableIfFree(cursorPoint)) { dropDraggableCopy(e.getX(), e.getY()); }
                                                   else { 
                                                           String clearedLandMessage = I18n.getString("clearedLandMessage");
                                                           userMessageListener.addMessage(clearedLandMessage); 
                                                        }
              }
            }
            break;
          case MouseEvent.BUTTON3:
            if (draggableItem != null)
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
        @Override public void mouseExited(MouseEvent event)   { if (draggableJLabel != null) { draggableJLabel.setVisible(false); } }
        @Override public void mouseEntered(MouseEvent event)  { if (draggableJLabel != null) { draggableJLabel.setVisible(true);  } }
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
            setAllComponentSizes(wrappedPanel, size);
            setAllComponentSizes(glassPanel, size);
          }
        }
    );
  }
  
  /**
   * Return true if the given point is legal to put down the current draggable label.
   * @param Point point of the cursor (p'bly middle of the glyph)
   */
  private boolean insertDraggableIfFree(Point cursorPoint)
  {
    boolean result = true;
    
    // calculate the tile x,y for the cursor (pixel) x and y
    Point tilePoint = calculateIndexTile(cursorPoint.x, cursorPoint.y);
    
    // check on whether draggable tile array will go onto the main tile array.
    TileArray draggedTileArray = draggableItem.getTileArray();
    if (tileArray.accepts(draggedTileArray, tilePoint))
    {
      tileArray.put(draggedTileArray, tilePoint);
    }
    else 
    {
      result = false;
    }
    
    return result;
  }
  
  /**
   * Set the size, preferredSize, and maximum size of the given component to the given dimension.
   * @param c
   * @param size
   */
  private void setAllComponentSizes(Component c, Dimension size)
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
    Dimension d = draggableJLabel.getSize();
    int xOffset = d.width / 2;
    int yOffset = d.height / 2;
    int xTile = (x - xOffset) / tileSize;
    int yTile = (y - yOffset) / tileSize;
    int newX = xTile * tileSize;
    int newY = yTile * tileSize;
    return new Point(newX, newY);
  }
  
  /**
   * Make a copy of the currently dragged component and drop it at the current 
   * cursor position. Whether this drop is legal should be determined before this
   * method is called.
   * 
   * <P> Currently only handles JLabel; need to figure out what we'll do with non-JLabel
   * components.
   *  
   * @param cursorX
   * @param cursorY
   */
  private void dropDraggableCopy(int cursorX, int cursorY)
  {
//    // calculate the tile x,y for the object
//    Point tilePoint = calculateIndexTile(cursorX, cursorY);
//    // put the Picture object in the tile array
//    TileArray draggedTileArray = draggableItem.getTileArray();
//    tileArray.put(draggedTileArray, tilePoint.x, tilePoint.y);
    
    // make a copy of the current component
    JLabel copiedLabel = null;
    Dimension copiedLabelSize = draggableJLabel.getSize();
    copiedLabel = new JLabel();
    copiedLabel.setIcon(draggableJLabel.getIcon());
    copiedLabel.setSize(copiedLabelSize);
    copiedLabel.setPreferredSize(copiedLabelSize);
    copiedLabel.setToolTipText(draggableJLabel.getToolTipText());
    Point componentPoint = getComponentPoint(cursorX, cursorY);
    copiedLabel.setLocation(componentPoint.x, componentPoint.y);

    // add the component to the wrapped pane.
    copiedLabel.setVisible(true);
    wrappedPanel.add(copiedLabel);
    wrappedPanel.repaint();
  }
  
  /**
   * given these x,y coordinates, calculate the x,y position in the
   * tile array of the upper left-hand tile in our draggable item.
   * 
   * @return Point with x,y tile position of index tile.
   */
  private Point calculateIndexTile(int cursorX, int cursorY)
  {
    Point draggableXY = getComponentPoint(cursorX, cursorY);
    int tileX = draggableXY.x / tileSize;
    int tileY = draggableXY.y / tileSize;
    Point result = new Point(tileX, tileY);
    return result;
  }
  
  /**
   * Activate the glass panel for dragging the given draggable JLabel.
   * @param c
   */
  public void activateDragging(Draggable label)
  {
    draggableItem = label;
    draggableAdded = false;
    glassPanel.setVisible(true);
    glassPanel.requestFocusInWindow();
    glassPanel.setFocusTraversalKeysEnabled(false);
    glassPanel.setCursor(blankCursor);
  }
  
  /**
   * Deactivate the glass panel, remove the current dragged JLabel from the glass panel.
   */
  public void deactivateDragging()
  {
    if (draggableJLabel != null)
    {
      remove(draggableJLabel);
      draggableJLabel = null; 
      glassPanel.setVisible(false);
      glassPanel.setCursor(null);
    }
  }
  
}
