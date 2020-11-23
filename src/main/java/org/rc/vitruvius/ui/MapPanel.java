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
  Dimension   currentTileSize;    // number of rows and columns of tiles for the current panel size.
  TileArray   tileArray = null;   // new form of pictures array
  public TileArray getTileArray() { return tileArray; }
  
  public MapPanel(int tileSize)
  {
    this.tileSize = tileSize;
    // set default size of map at 100x100 tiles
    int defaultColumns  = 100;
    int defaultRows     = 100;
    tileArray = new TileArray();
    currentTileSize = new Dimension(defaultRows, defaultColumns);
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

//  /**
//   * set the size of the images panel so that it is big enough to accommodate the given tile array.
//   * @param tileArray
//   */
//  private void setSize(TileArray tileArray)
//  {
//    int height = 0;
//    int width = 0;
//    
//    int currentRow = 0; 
//    for (TileRow row: tileArray)
//    {
//      int currentCol = 0;
//      for (Tile tile: row)
//      {
//        Picture p = tile.picture();
//        if (p != null)
//        {
//          int tileRows = p.rows();
//          int tileCols = p.columns();
//          height = Math.max(height, currentRow + tileRows);
//          width  = Math.max(width,  currentCol + tileCols);
//        }
//        currentCol++;
//      }
//      currentRow++;
//    }
//    setSize(width*tileSize, height*tileSize);
//    setPreferredSize(new Dimension(width*tileSize, height*tileSize));
//  }
  
  public void setTileArray(TileArray newTileArray)
  {
    tileArray = newTileArray;
    // Ensure the panel is big enough for the map.
    Dimension tileArraySize = tileArray.getSize();
    Dimension screenSize = calculateTileArrayScreenSize(tileArraySize);
    if (panelTooSmall(screenSize))
    {
      setAllPanelSizes(screenSize);
    }

    removeAll();
    drawTileArray(tileArray);
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
  private void drawTileArray(TileArray tileArray)
  {
    removeAll();
    this.tileArray = tileArray;
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
