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

  /**
   * set the size of the images panel so that it is big enough to accommodate the given tile array.
   * @param tileArray
   */
  private void setSize(TileArray tileArray)
  {
    int height = 0;
    int width = 0;
    
    // the text might have multi-column or multi-row glyphs in the last
    // few columns/rows of the text, so the width and height of the tile
    // array might need to be larger than the number of characters in the
    // strings or the number of strings in the text. The loop calculates
    // the grid dimensions needed so we can size the panel holding the glyphs.
    int currentRow = 0; 
    for (TileRow row: tileArray)
    {
      int currentCol = 0;
      for (Tile tile: row)
      {
        Picture p = tile.picture();
        if (p != null)
        {
          int tileRows = p.rows();
          int tileCols = p.columns();
          height = Math.max(height, currentRow + tileRows);
          width  = Math.max(width,  currentCol + tileCols);
        }
        currentCol++;
      }
      currentRow++;
    }
    setSize(width*tileSize, height*tileSize);
    setPreferredSize(new Dimension(width*tileSize, height*tileSize));
  }
  
  public void setTileArray(TileArray newTileArray)
  {
    tileArray = newTileArray;
    // Ensure the panel is the right size and clear it.
    setSize(tileArray);
    removeAll();
    drawTileArray(tileArray);
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

    
// the following is the "setTileArray(TileArray)" code from a previous version of 
// DragNDropImagesPane, which did not use this class. I'm converting over for that pane/panel
// to use this class instead, and having this one handle the tilearray-on-a-panel operations
// for both drag-and-drop and glyphy functionality. Based on this code, I added (1)
// assignment of tileArray to this instance var, and (2) removal of any existing components before
// starting the creation and drawing of picture labels.
    
//    wrappedPanel.removeAll();
//    this.tileArray = tileArray;
//    int rowNumber = 0;
//    for (TileRow tileRow: tileArray)
//    {
//      int colNumber = 0;
//      for (Tile tile: tileRow)
//      {
//        if (tile != null && tile.type() == Tile.Type.PICTURE)
//        {
//          Picture picture = tile.picture();
//          JLabel label = picture.getLabel(tileSize);
//          int x = colNumber * tileSize;
//          int y = rowNumber * tileSize;
//          label.setLocation(x,y);
//          label.setVisible(true);
//          wrappedPanel.add(label);
//        }
//        colNumber++;
//      }
//      rowNumber++;
//    }
//    wrappedPanel.invalidate();
//    wrappedPanel.repaint();
//    invalidate();
//    repaint();
    
    
//    invalidate();     // TODO: figure out if this is necessary; caller liable to invalidate higher-level things anyway.
                      // Include repaint/invalidate decisions in javadoc.
  }

//  @Override  public void mouseDragged(MouseEvent e)  {      }
//  @Override  public void mouseMoved(MouseEvent e)  {      }
//  
//  @Override  public void mouseClicked(MouseEvent e)    {     }
//  @Override  public void mousePressed(MouseEvent e)  {      }
//  @Override  public void mouseReleased(MouseEvent e)  {      }
//  @Override  public void mouseEntered(MouseEvent e)  {      }
//  @Override  public void mouseExited(MouseEvent e)  {      }

}
