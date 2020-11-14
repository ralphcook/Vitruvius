package org.rc.vitruvius.ui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.TileRow;

/**
 * The panel for displaying a collection of glyphs.
 * @author rcook
 *
 */
public class ImagesPanel extends JPanel // implements MouseListener, MouseMotionListener
{
  private static final long serialVersionUID = 1L;
  
  int         tileSize = -1;      // # of pixels per tile edge
  TileArray   tileArray = null;   // new form of pictures array
  public TileArray getTileArray() { return tileArray; }
  
  public ImagesPanel()
  {
    Dimension dimension = new Dimension(150,150);
    setPreferredSize(dimension);
    setSize(dimension);
    setLayout(null);
  }
  
  public ImagesPanel(int tileSize)
  {
    this();
    this.tileSize = tileSize;
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
