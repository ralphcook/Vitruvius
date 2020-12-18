package org.rc.vitruvius.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.TileRow;

/**
 * The panel for displaying a collection of glyphs.
 * @author rcook
 *
 */
public class MapPanel extends JPanel // implements MouseListener, MouseMotionListener
{
  private static final long serialVersionUID = 1L;
  
  int         tileSize = -1;      // # of pixels per tile edge
  public int getTileSize() { return tileSize; }
  
  Dimension   currentMapTileSize;     // number of rows and columns of tiles for the current panel size.
  
  TileArray   tileArray = null;   // new form of pictures array
  public TileArray getTileArray() { return tileArray; }
  
  /**
   * return true if there are any images on the panel.
   * @return
   */
  public boolean anyImages()
  {
    boolean result = true;
    if (tileArray != null)
    {
      for (TileRow row : tileArray)
      {
        for (Tile tile : row)
        {
          if (tile != null && tile.type() != Tile.Type.EMPTY)
          {
            result = false;
            break;
          }
        }
      }
    }
    return result;
  }
  
  public MapPanel(int tileSize)
  {
    this.tileSize = tileSize;
    // set default size of map at 100x100 tiles
    int defaultColumns  = 100;
    int defaultRows     = 100;
    tileArray = new TileArray();
    currentMapTileSize = new Dimension(defaultRows, defaultColumns);
    Dimension pixelSize = new Dimension(defaultRows*tileSize, defaultColumns*tileSize);
    setAllPanelSizes(pixelSize);
    setLayout(null);
    
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
  }
  
  private void setAllPanelSizes(Dimension pixelSize)
  {
    setPreferredSize(pixelSize);
    setSize(pixelSize);
    setMinimumSize(pixelSize);
    setMaximumSize(pixelSize);
  }

  public void decreaseTileSize()
  {
    if (tileSize >= 15)
    {
      tileSize -= 1;
      resizePanel();
      redrawTileArray();
      repaint();
    }
  }
  
  public void increaseTileSize()
  {
    if (tileSize <= 75)
    {
      tileSize += 1;
      resizePanel();
      redrawTileArray();
      repaint();
    }
  }
  
  private void resizePanel()
  {
    int width = tileSize * currentMapTileSize.width;
    int height = tileSize * currentMapTileSize.height;
    Dimension newSize = new Dimension(width, height);
    setAllPanelSizes(newSize);
  }

  /**
   * clear the panel -- remove all images and tile objects and repaint.
   */
  public void clearPanel()
  {
    removeAll();
    tileArray.clear();
    repaint();
  }

  public void setTileArray(TileArray newTileArray)
  {
    tileArray = newTileArray;
    // Ensure the panel is at least big enough for the tileArray;
    //    enlarge it if not.
    Dimension tileArraySize = tileArray.getSize();
    Dimension screenSize = calculateTileArrayScreenSize(tileArraySize);
    if (panelTooSmall(screenSize))
    {
      setAllPanelSizes(screenSize);
    }
    redrawTileArray();
    repaint();
  }

  private Dimension calculateTileArrayScreenSize(Dimension tileArraySize)
          {
    int width = tileArraySize.width * tileSize;
    int height = tileArraySize.height * tileSize;
    return new Dimension(width, height);
  }
  
  private boolean panelTooSmall(Dimension size)
  {
    Dimension panelSize = getSize();
    boolean result = ((panelSize.width < size.width) || (panelSize.height < size.height));
    return result;
  }
  
  /**
   * Put the glyphs from the given TileArray onto the panel; we currently use
   * a JLabel for each glyph, and position it on a null layout using X,Y calculated
   * from tile size and TileArray position.
   * @param tileArray
   */
  private void redrawTileArray()
  {
    removeAll();
    if (tileArray != null)
    {
      int rowNumber = 0;
      for (TileRow row : tileArray)
      {
        int colNumber = 0;
        for (Tile tile : row)
        {
          int x = colNumber * tileSize;
          int y = rowNumber * tileSize;
          JLabel label = tile.getLabel(tileSize);
          if (label != null)
          {
            label.setVisible(true);
            label.setLocation(x,y);
            add(label);
          }
          colNumber++;
        }
        rowNumber++;
      }
    }
  }
}
