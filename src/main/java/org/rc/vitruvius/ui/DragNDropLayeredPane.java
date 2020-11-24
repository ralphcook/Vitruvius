package org.rc.vitruvius.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.Draggable;
import org.rc.vitruvius.model.DraggablePicture;
import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;

/**
 * This wrapper extends JLayeredPane, primarily so we can 'drag' a component above everything
 * else in the panel.  This particular one has the mouse listeners to implement making a 
 * dragged component operate as a cursor, dropping that component on the panel with a mouse click, etc.
 * 
 * @author rcook
 *
 */
public class DragNDropLayeredPane extends JLayeredPane implements GlyphSelectionListener 
{
  public static void say(String s) { System.out.println(s); }
  public static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  
  private static final long serialVersionUID = 1L;
  private MapPanel            mapPanel              = null;
  private JPanel              glassPanel            = new JPanel();
  
  private Draggable           draggableItem         = null;     // item being dragged
  private JLabel              draggableJLabel       = null;     // JLabel created from draggable, actual screen component
                                                                // it is specific to tileSize, needs recalc if that changes
                                                                // (or if we get a new draggable item)

  private JLabel              selectedItem          = null;
  
  private boolean             unsavedChanges        = false;
  
  PassAlongMousePressedListener passAlongListener   = new PassAlongMousePressedListener();
  
  private UserMessageListener userMessageListener       = null;
  
  public DragNDropLayeredPane(DragNDropPanel glyphSelectionGenerator, UserMessageListener givenListener, int tileSize)
  {
    glyphSelectionGenerator.addGlyphSelectionListener(this);
    this.userMessageListener = givenListener;
    
    mapPanel = new MapPanel(tileSize); // createWrappedPanel();
//    tileArray = new TileArray();

    glassPanel.setOpaque(false);
    glassPanel.setVisible(false);
    glassPanel.setFocusable(true);
    
    glassPanel.addMouseMotionListener
    (new MouseAdapter() 
      {
        @Override public void mouseMoved(MouseEvent event)
        {
          if (draggableItem != null)
          {
            setToCursorPosition(draggableJLabel, event.getX(), event.getY());
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
                if (insertDraggableIntoTileArrayIfFree(cursorPoint)) 
                                                        { 
                                                          dropDraggableCopy(e.getX(), e.getY());
                                                          if (!draggableItem.isPersistent()) { deactivateDraggingOrMoving(); }
                                                        }
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
              GlyphSelectionEvent gsEvent = new GlyphSelectionEvent(this, "deselect", draggableItem);
              glyphSelectionGenerator.fireGlyphSelectionEvent(gsEvent);
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
    
    mapPanel.addMouseListener
    (
        new MouseAdapter()
        {
          public void mousePressed(MouseEvent event)
          {
            int buttonId = event.getButton();
            if (buttonId == MouseEvent.BUTTON1)
            {
              // if a user double-clicks, we get two events: one for the first click,
              // and another where the click count is 2. If the user continues to click
              // quickly, the system will continue to report higher numbers of click counts.
              int clickCount = event.getClickCount();
              say("clickCount = %d", clickCount);

              // if there's a glyph under the cursor, select it.
              Component c = mapPanel.findComponentAt(event.getX(), event.getY());
              if (c instanceof JLabel)
              {
                if (clickCount == 2)
                {
                  // c should already be the selected item
                  // create a draggableItem out of the selected image
                  startDraggingSelectedItem();
                }
                boolean newSelection = (selectedItem != c);   // true iff the user has selected a new item
                unselectCurrentItem();                        // regardless of whether he has clicked on a new item, we're going to deselect the current one.
                if (newSelection) { selectItem((JLabel)c); }
              }
            }
          }
        }
    );

    mapPanel.addMouseMotionListener
    (
        new MouseAdapter()
        {
          public void mouseDragged(MouseEvent event)
          {
            // in our world here, 'dragging' refers to the user clicking on an icon that
            // is already on the screen, keeping his mouse button down, and dragging that
            // icon to another location.
            // when the user clicks on a drop-down or whatever to put an icon on the screen
            // that was not there before, we use the 'mouseMoved' event for that. So that's
            // 'moving' something, not 'dragging' it, even though we think of it as dragging.
            // There is a 'draggable' interface we use for objects that might be moved either
            // way; this class has an instance variable for the item, whether it is being
            // dragged or moved.
            // if this event determines that something is currently selected and that the mouse
            // event is still on that item, that means it is the first such event in a dragging
            // operation. We detach the icon from the screen, determine what the underlying
            // item is and remove it from the tileArray, and make a Draggable out of it so
            // it is in that instance variable ready to move on the glasspane.
            // 
            // if there is already a draggable in place, can we assume we're dragging it?
            // if it was a new draggable, a mouse button press would have ended its drag, 
            // so there would be no draggable. The only other time we have a draggable is if
            // we're actually dragging something, so that assumption sounds safe.
            //
            // So the first time this event is received, determine if we have a selected label
            // and if that selected label is under the mouse cursor at this first drag event. 
            // If both those are true, create its Draggable and make it the current dragged 
            // object.
            if (draggableItem == null)
            {
              Component c = mapPanel.findComponentAt(event.getX(), event.getY());
              // since we aren't currently dragging something; see if we're on top of one to drag.
              // if we're not, we'll just quietly exit the method...
              if ((selectedItem != null) && (c == selectedItem))
              {
                // ok, we're dragging this puppy. Create its draggable; we'll need the underlying picture
                // (only attempting to support dragging individual glyphs for now)
                Point selectedScreenLocation = selectedItem.getLocation();                            // find our item
                Point selectedTileLocation = calculateIndexTile(selectedScreenLocation);              // get its index tile location
                Tile selectedTile = mapPanel.getTileArray().getTile(selectedTileLocation);            // and its index tile.
                
                mapPanel.getTileArray().deletePictureTiles(selectedTileLocation);                     // remove item being dragged from tile array
                mapPanel.remove(selectedItem);
                
                draggableItem = new DraggablePicture(selectedTile.picture());                         // make our current draggableItem one from the picture in that tile.
//                draggableJLabel = draggableItem.getJLabelJustIcon(mapPanel.getTileSize());            // likewise with our draggableJLabel
                activateDraggingOrMoving(draggableItem);
                setToCursorPosition(draggableJLabel, event.getX(), event.getY());
              }
            }
            else
            {
              setToCursorPosition(draggableJLabel, event.getX(), event.getY());
            }
          }
        }
    );
    
    Dimension mapPanelSize = mapPanel.getPreferredSize();
    add(mapPanel, JLayeredPane.DEFAULT_LAYER);
    add(glassPanel, JLayeredPane.PALETTE_LAYER);
    
    glassPanel.setSize(mapPanelSize);
    glassPanel.setPreferredSize(mapPanelSize);

    setPreferredSize(mapPanelSize);

//    addComponentListener                                    // TODO: when we implement size <> window size, delete this.
//    (
//        // when this (the layered pane) gets a resize event,
//        // resize the panel it contains to the same size.
//        new ComponentAdapter()
//        {
//          @Override public void componentResized(ComponentEvent e)
//          {
//            Component c = e.getComponent();
//            Dimension size = c.getSize();
//            setAllComponentSizes(wrappedPanel, size);
//            setAllComponentSizes(glassPanel, size);
//          }
//        }
//    );
    
    addKeyListener(new DragNDropKeyListener(this));
    
  }
  
  private void startDraggingSelectedItem()
  {
    // get the tile corresponding to the selected item.
    Point screenLocation = selectedItem.getLocation();
    Point tileLocation = calculateIndexTile(screenLocation);
    TileArray tileArray = mapPanel.getTileArray();
    Tile tile = tileArray.getTile(tileLocation.x, tileLocation.y);
    // remove this picture from the tile array, and from the mapPanel.
    tileArray.deletePictureTiles(tileLocation);
    mapPanel.remove(selectedItem);
    // activate dragging
    Draggable newDraggable = new DraggablePicture(tile.picture(), false);
    activateDraggingOrMoving(newDraggable);
    draggableJLabel.setLocation(screenLocation);
  }

  public void setToCursorPosition(JLabel label, int x, int y)
  {
    Point newPoint = getDraggedComponentIndexPoint(x, y);
    label.setLocation(newPoint.x, newPoint.y);
  }
  
  public void decreaseTileSize()
  {
    mapPanel.decreaseTileSize();
    resetDraggableAfterTileResize();
  }
  
  private void resetDraggableAfterTileResize()
  {
    if (draggableItem != null)                            // help -- probably need to do this for the existing glyph draggable as well
    {                                                     // try to think of a way to make this one draggable...
      Draggable savedDraggableItem = draggableItem;
      deactivateDraggingOrMoving();
      activateDraggingOrMoving(savedDraggableItem);
      
      Point currentCursorPoint = MouseInfo.getPointerInfo().getLocation();
      SwingUtilities.convertPointFromScreen(currentCursorPoint, this);

      Rectangle panelRectangle = this.getBounds();
      if (    0 < currentCursorPoint.x 
          &&      currentCursorPoint.x < panelRectangle.width
          &&  0 < currentCursorPoint.y
          &&      currentCursorPoint.y < panelRectangle.height
          )
      {
        setToCursorPosition(draggableJLabel, currentCursorPoint.x, currentCursorPoint.y);
        draggableJLabel.setVisible(true);
      }
    }
  }
  
  public void increaseTileSize()
  {
    mapPanel.increaseTileSize();
    resetDraggableAfterTileResize();
  }
  
  public void glyphSelection(GlyphSelectionEvent gsEvent)
  {
    String action = gsEvent.getAction();
    Draggable draggable = gsEvent.getDraggable();
    if (action.equals("select")) { activateDraggingOrMoving(draggable); }
                            else { deactivateDraggingOrMoving();        }
    repaint();
  }
  
  public boolean  unsavedChanges()                  { return unsavedChanges; }
  public void     setUnsavedChanges(boolean value)  { unsavedChanges = value; }
  
  /**
   * Return the tile array currently in use.
   */
  public TileArray getTileArray() { return mapPanel.getTileArray(); }
  
  /**
   * Set the given tile Array as the current one, and display it.
   * @param tileArray
   */
  public void setTileArray(TileArray tileArray)
  {
    mapPanel.setTileArray(tileArray);
  }
  
  @SuppressWarnings("unused")
  private void printAllSizes(String name, Component c)
  {
    System.out.println(name);
    printSize("  size", c.getSize());
    printSize("  pref", c.getPreferredSize());
    printSize("  min ", c.getMinimumSize());
    printSize("  max ", c.getMaximumSize());
  }
  
  private void printSize(String label, Dimension size)
  {
    String message = "%s size is %d, %d%n";
    System.out.printf(message, label, size.width, size.height);
  }
  
  private void unselectCurrentItem()
  {
    if (selectedItem != null)
    {
      selectedItem.setBorder(null);
      selectedItem = null;
    }
  }
  
  private void selectItem(JLabel item)
  {
    selectedItem = item;
    selectedItem.setBorder(BorderFactory.createLineBorder(Color.darkGray, 2));
    this.requestFocus();
  }
  
  public void deleteSelectedItem()
  {
    if(selectedItem != null)
    {
      // get the tile position of the selected item.
      Point graphicsIndex = selectedItem.getLocation();
      Point indexTile = calculateIndexTile(graphicsIndex); 
      mapPanel.getTileArray().deletePictureTiles(indexTile);
      
      // take the selected item off the panel.
      mapPanel.remove(selectedItem);
      
      setUnsavedChanges(true);

      unselectCurrentItem();
      revalidate();
      repaint();
    }
  }
  
  /**
   * Insert the current draggable into the tile array as indicated by the cursor location,
   * if there are no non-empty tiles that would be overwritten.
   * @param Point cursor location (p'bly middle of the glyph)
   */
  private boolean insertDraggableIntoTileArrayIfFree(Point cursorPoint)
  {
    boolean result = true;
    
    // calculate the tile x,y for the cursor (pixel) x and y
    Point tilePoint = calculateDraggedIndexTile(cursorPoint.x, cursorPoint.y);
    
    // check on whether draggable tile array will go onto the main tile array.
    TileArray draggedTileArray = draggableItem.getTileArray();
    if (mapPanel.getTileArray().accepts(draggedTileArray, tilePoint))
    {
      mapPanel.getTileArray().put(draggedTileArray, tilePoint);
    }
    else 
    {
      result = false;
    }
    
    return result;
  }
  
  /**
   * Set the size, preferredSize, minimum, and maximum size of the given component to the given dimension.
   * @param c
   * @param size
   */
  private void setAllComponentSizes(Component c, Dimension size)
  {
    c.setSize(size);
    c.setPreferredSize(size);
    c.setMaximumSize(size);
    c.setMinimumSize(size);
  }

  /**
   * Given a cursor position, determine the x,y of the upper left corner of the current
   * dragged component.
   * @param x
   * @param y
   * @return
   */
  private Point getDraggedComponentIndexPoint(int x, int y)
  {
    TileArray draggableItemTileArray = draggableItem.getTileArray();
    int width = draggableItemTileArray.columns();
    int height = draggableItemTileArray.rows();

    int tileSize = mapPanel.getTileSize();
    int cursorXTile = x / tileSize;   //  row and column indices 
    int cursorYTile = y / tileSize;   //  of tile containing cursor
        cursorXTile -= width/2;       // adjustment for width of draggable.
        cursorYTile -= height/2;      //   and height

    int newX = cursorXTile * tileSize;  // upper left corner coordinates  
    int newY = cursorYTile * tileSize;  // of upper left tile for glyph
    
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
    // make a copy of the current draggable component
    Dimension copiedLabelSize = draggableJLabel.getSize();
    JLabel copiedLabel = null;
    copiedLabel = new JLabel();
    copiedLabel.setIcon(draggableJLabel.getIcon());
    copiedLabel.setSize(copiedLabelSize);
    copiedLabel.setPreferredSize(copiedLabelSize);
    copiedLabel.setToolTipText(draggableJLabel.getToolTipText());
    PassAlongMousePressedListener mouseListener = new PassAlongMousePressedListener();
    copiedLabel.addMouseListener(mouseListener);
    copiedLabel.addMouseMotionListener(mouseListener);
    Point componentPoint = getDraggedComponentIndexPoint(cursorX, cursorY);
    copiedLabel.setLocation(componentPoint.x, componentPoint.y);

    // add the component to the wrapped pane.
    copiedLabel.setVisible(true);
    mapPanel.add(copiedLabel);
    mapPanel.repaint();
    
    setUnsavedChanges(true);
  }
  
  /**
   * given these x,y coordinates, calculate the x,y position in the
   * tile array of the upper left-hand tile in our draggable item.
   * 
   * @return Point with x,y tile position of index tile.
   */
  private Point calculateDraggedIndexTile(int cursorX, int cursorY)
  {
    int tileSize = mapPanel.getTileSize();
    Point draggableXY = getDraggedComponentIndexPoint(cursorX, cursorY);
    int tileX = draggableXY.x / tileSize;
    int tileY = draggableXY.y / tileSize;
    Point result = new Point(tileX, tileY);
    return result;
  }
  
  /**
   * Calculate the index tile corresponding to a given point
   * @param point
   * @return
   */
  private Point calculateIndexTile(Point point)
  {
    return calculateIndexTile(point.x, point.y);
  }
  
  /**
   * Calculate the index tile corresponding to a given x,y graphics point.
   */
  private Point calculateIndexTile(int x, int y)
  {
    int tileSize = mapPanel.getTileSize();
    int tileX = x / tileSize;
    int tileY = y / tileSize;
    return new Point(tileX, tileY);
  }
  
  /**
   * Activate the glass panel for dragging the given draggable JLabel.
   * @param c
   */
  public void activateDraggingOrMoving(Draggable draggable)
  {
    draggableItem = draggable;
    draggableJLabel = draggable.getJLabelJustIcon(mapPanel.getTileSize());
    
    draggableJLabel.setVisible(false);
    putLabelOnGlassPane(draggableJLabel);
  }
  
  private void putLabelOnGlassPane(JLabel draggableJLabel)
  {
    draggableJLabel.setVisible(false);
    add(draggableJLabel, JLayeredPane.DEFAULT_LAYER);   //, -1);  // or 0
    add(draggableJLabel, JLayeredPane.DRAG_LAYER);      //,   -1);  // or 0
    
    setAllComponentSizes(glassPanel, mapPanel.getSize());
    
    glassPanel.setVisible(true);
    glassPanel.requestFocusInWindow();
    glassPanel.setFocusTraversalKeysEnabled(false);
//    glassPanel.setCursor(blankCursor);
  }
  
  /**
   * Deactivate the glass panel, remove the current dragged JLabel from the glass panel.
   */
  public void deactivateDraggingOrMoving()
  {
    if (draggableJLabel != null)
    {
      remove(draggableJLabel);
      draggableJLabel = null; 
      draggableItem   = null;
      glassPanel.setVisible(false);
//      glassPanel.setCursor(null);
    }
  }
  
}
