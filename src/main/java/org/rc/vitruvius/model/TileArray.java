package org.rc.vitruvius.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Dynamic 2d collection of Tiles, organized as rows of tiles.
 * 
 * @author rcook
 * 
 * @see org.rc.vitruvius.model.Tile
 *
 */
public class TileArray implements Iterable<TileRow>
{
  private ArrayList<TileRow> tileRows = null;
  
  public TileArray()
  {
    tileRows = new ArrayList<>();
  }
  
  public TileArray(int rows, int cols)
  {
    this();
    for (int i=0; i<rows; i++)
    {
      TileRow tileRow = new TileRow(cols);
      tileRows.add(tileRow);
    }
  }
  
  /**
   * Return a string that is "tileArray: " followed by a concatenation of the strings 
   * representing each row, or an indication that there are no rows.
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder("tileArray: ");
    if (tileRows == null) { sb.append("<null tileRows>"); }
    else
    {
      for (TileRow tileRow: tileRows)
      {
        if (tileRow == null) { sb.append("<null TR>"); }
        else
        {
          sb.append(tileRow.toString());
        }
        sb.append("\n");
      }
      
    }
    return new String(sb);
  }
  
  /**
   *  Put the given tile at the given 0-based column and row number in this tileArray.
   *  Ensure the tile array is big enough to contain the tile.
   *
   * @param t Tile to place.
   * @param colNumber column number, starts at 0
   * @param rowNumber row number, starts at 0
   */
  public void put(Tile t, int rowNumber, int colNumber)
  {
    int rowsNeeded = (rowNumber + 1) - tileRows.size();
    for (int i=0; i<rowsNeeded; i++) { tileRows.add(new TileRow()); }
    TileRow tileRow = tileRows.get(rowNumber);
    tileRow.put(t, colNumber);
  }
  
  /**
   * Return the number of tiles in the tile array.
   * @return
   */
  public int length() { return tileRows.size(); }
  
  /**
   * Add the given row to the tile array.
   * @param row
   */
  public void addRow(TileRow row) { tileRows.add(row); }
  
  /**
   * Get the row of tiles at the given 0-based index.
   * @param index
   * @return
   */
  public TileRow getRow(int index)
  {
    if (index > tileRows.size())
    {
      String errorMessage = String.format("Attempt to get %d TileRow from TileArray with %d elements",
                                          index, tileRows.size()
                                          );
      throw new IllegalArgumentException(errorMessage);
    }
    return tileRows.get(index);
  }
  
  /**
   * get the tile at the given (0-based) row and column number.
   * Return null if there is no such tile, e.g., row and column
   * are outside the bounds for this tileArray.
   * @param rowNumber
   * @param columnNumber
   * @return
   */
  public Tile getTile(int rowNumber, int columnNumber)
  {
    Tile    tile = null;
    TileRow row  = null;
    
    if (rowNumber < (tileRows.size()) ) 
    { 
      row = getRow(rowNumber); 
      tile = row.get(columnNumber);
    }
    return tile;
  }
  
  /**
   * Return an iterator over the rows in the TileArray.
   */
  @Override
  public Iterator<TileRow> iterator()
  {
    return tileRows.iterator();
  }
  
}
