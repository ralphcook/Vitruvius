package org.rc.vitruvius.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.Draggable;
import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.TileRow;

/**
 * This wrapper extends JLayeredPane, primarily so we can 'drag' a component above everything
 * else in the panel.  This particular one has the mouse listeners to implement making a 
 * dragged component operate as a cursor, dropping that component on the panel with a mouse click, etc.
 * 
 * @author rcook
 *
 */
public class DragNDropImagesPane extends JLayeredPane implements GlyphSelectedListener 
{
  public static void say(String s) { System.out.println(s); }
  public static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  
  private static final long serialVersionUID = 1L;
  private JPanel              wrappedPanel          = null;
  private JPanel              glassPanel            = new JPanel();
  
  private Draggable           draggableItem         = null;     // item being dragged
  private JLabel              draggableJLabel       = null;     // JLabel created from draggable, actual screen component
                                                                // it is specific to tileSize, needs recalc if that changes
                                                                // (or if we get a new draggable item)

  private TileArray           tileArray             = null;
  
  private Cursor              blankCursor           = null;
  private int                 tileSize              = 25;     // TODO: get the actual tileSize from the main panel into this instance, and to change it.
  
  private JLabel              selectedItem          = null;
  
  PassAlongMousePressedListener passAlongListener   = new PassAlongMousePressedListener();
  
  private DragNDropPanel      glyphSelectionGenerator   = null;
  private UserMessageListener userMessageListener       = null;
  
  // TODO: decide whether we still want the 'givenPanel' passed in -- it is not used by the caller at this time.
  // Neither is the tile array.
  public DragNDropImagesPane(DragNDropPanel glyphSelectionGenerator, UserMessageListener givenListener)
  {
    this.glyphSelectionGenerator = glyphSelectionGenerator;
    glyphSelectionGenerator.addGlyphSelectedListener(this);
    this.userMessageListener = givenListener;
    
    wrappedPanel = createWrappedPanel();
    tileArray = new TileArray();

    // Transparent 16 x 16 pixel 'blank cursor' image.
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    
    glassPanel.setOpaque(false);
    glassPanel.setVisible(false);
    glassPanel.setFocusable(true);
    glassPanel.setBorder(BorderFactory.createLineBorder(Color.red));
    
    glassPanel.addMouseMotionListener
    (new MouseAdapter() 
      {
        @Override public void mouseMoved(MouseEvent event)
        {
          if (draggableItem != null)
          {
            Point newPoint = getDraggedComponentIndexPoint(event.getX(), event.getY());
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
    
    wrappedPanel.addMouseListener
    (
        new MouseAdapter()
        {
          public void mousePressed(MouseEvent event)
          {
            int buttonId = event.getButton();
            if (buttonId == MouseEvent.BUTTON1)
            {
              // if there's a glyph under the cursor, select it.
              Component c = wrappedPanel.findComponentAt(event.getX(), event.getY());
              if (c instanceof JLabel)
              {
                boolean newSelection = (selectedItem != c);   // true iff the user has selected a new item
                unselectCurrentItem();                        // regardless of whether he has clicked on a new item, we're going to deselect the current one.
                if (newSelection) { selectItem((JLabel)c); }
              }
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

    addComponentListener                                    // TODO: when we implement size <> window size, delete this.
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
    
    addKeyListener(new DragNDropKeyListener(this));
    
  }
  
  public void glyphSelected(GlyphSelectedEvent gsEvent)
  {
    say("glyphSelectedEvent in DNDImagesPane");
    Draggable draggable = gsEvent.getDraggable();
    ImageIcon icon = draggable.getImageIcon(25);      // TODO: this doesn't look like the right size.
    JLabel dragLabel = new JLabel(icon);
    Dimension size = dragLabel.getPreferredSize();
    dragLabel.setSize(size);
    activateDragging(draggable);
    repaint();
  }
  
  /**
   * Create the JPanel that is 'wrapped' by this JLayeredPane; this is the
   * panel that will be the parent of the JLabel objects holding the glyphs.
   * @return
   */
  public JPanel createWrappedPanel()
  {
    JPanel mapPanel = new JPanel();
    mapPanel.setLayout(null);
    // TODO: figure out why this sizing is still needed.
    Dimension mapSize = new Dimension(400,400);
    mapPanel.setSize(mapSize);
    mapPanel.setPreferredSize(mapSize);
    mapPanel.setMaximumSize(mapSize);
    mapPanel.setBorder(BorderFactory.createLineBorder(Color.green, 3));
    return mapPanel;
  }
  
  /**
   * Return the tile array currently in use.
   */
  public TileArray getTileArray() { return tileArray; }
  
  /**
   * Set the given tile Array as the current one, and display it.
   * @param tileArray
   */
  public void setTileArray(TileArray tileArray)
  {
    wrappedPanel.removeAll();
    this.tileArray = tileArray;
    int rowNumber = 0;
    for (TileRow tileRow: tileArray)
    {
      int colNumber = 0;
      for (Tile tile: tileRow)
      {
        if (tile != null && tile.type() == Tile.Type.PICTURE)
        {
          Picture picture = tile.picture();
          JLabel label = picture.getLabel(tileSize);
          int x = colNumber * tileSize;
          int y = rowNumber * tileSize;
          label.setLocation(x,y);
          label.setVisible(true);
          wrappedPanel.add(label);
        }
        colNumber++;
      }
      rowNumber++;
    }
    wrappedPanel.invalidate();
    wrappedPanel.repaint();
    invalidate();
    repaint();
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
      tileArray.deletePictureTiles(indexTile);
      
      // take the selected item off the panel.
      wrappedPanel.remove(selectedItem);

      unselectCurrentItem();
      revalidate();
      repaint();
    }
  }
  
  /**
   * Return true if it is legal to put the current draggable label
   * down at the given point.
   * @param Point cursor location (p'bly middle of the glyph)
   */
  private boolean insertDraggableIfFree(Point cursorPoint)
  {
    boolean result = true;
    
    // calculate the tile x,y for the cursor (pixel) x and y
    Point tilePoint = calculateDraggedIndexTile(cursorPoint.x, cursorPoint.y);
    
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
    // make a copy of the current draggable component
    Dimension copiedLabelSize = draggableJLabel.getSize();
    JLabel copiedLabel = null;
    copiedLabel = new JLabel();
    copiedLabel.setIcon(draggableJLabel.getIcon());
    copiedLabel.setSize(copiedLabelSize);
    copiedLabel.setPreferredSize(copiedLabelSize);
    copiedLabel.setToolTipText(draggableJLabel.getToolTipText());
    Point componentPoint = getDraggedComponentIndexPoint(cursorX, cursorY);
    copiedLabel.setLocation(componentPoint.x, componentPoint.y);

    // add the component to the wrapped pane.
    copiedLabel.setVisible(true);
    wrappedPanel.add(copiedLabel);
    wrappedPanel.repaint();
    
    copiedLabel.addMouseListener(passAlongListener);
  }
  
  /**
   * given these x,y coordinates, calculate the x,y position in the
   * tile array of the upper left-hand tile in our draggable item.
   * 
   * @return Point with x,y tile position of index tile.
   */
  private Point calculateDraggedIndexTile(int cursorX, int cursorY)
  {
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
    int tileX = x / tileSize;
    int tileY = y / tileSize;
    return new Point(tileX, tileY);
  }
  
  
  /**
   * Activate the glass panel for dragging the given draggable JLabel.
   * @param c
   */
  public void activateDragging(Draggable draggable)
  {
    System.out.println("activateDragging()");
    draggableItem = draggable;
    
    draggableJLabel = draggable.getJLabelJustIcon(tileSize);
    draggableJLabel.setVisible(false);
    add(draggableJLabel, JLayeredPane.DEFAULT_LAYER);   //, -1);  // or 0
    add(draggableJLabel, JLayeredPane.DRAG_LAYER);      //,   -1);  // or 0
    
    setAllComponentSizes(glassPanel, wrappedPanel.getSize());
    
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
    System.out.println("deactivateDragging()");
    if (draggableJLabel != null)
    {
      remove(draggableJLabel);
      draggableJLabel = null; 
      glassPanel.setVisible(false);
      glassPanel.setCursor(null);
    }
  }
  
}
