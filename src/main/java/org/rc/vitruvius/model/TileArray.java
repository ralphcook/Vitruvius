package org.rc.vitruvius.model;

import java.util.ArrayList;
import java.util.Iterator;

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
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
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
   *  If the tile array is not currently big enough to have this tile, enlarge it so it can.
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
  
  public int length() { return tileRows.size(); }
  
  Iterator<TileRow> rowIterator()
  {
    return new TileRowIterator(this);
  }
  
  public void addRow(TileRow row) { tileRows.add(row); }
  
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
  
  class TileRowIterator implements Iterator<TileRow>
  {
    private TileArray tileArray       = null;
    private int       currentPosition = -1;
    
    public TileRowIterator(TileArray givenTileArray)
    {
      tileArray = givenTileArray;
      currentPosition = 0;
    }
    
    @Override    public boolean hasNext() 
    { 
      return (currentPosition > tileArray.length()); 
    }
    
    @Override    public TileRow next()
    {
      if (!hasNext())
      {
        throw new IllegalStateException("Call for next on TileArray past end of list.");
      }
      else
      {
        TileRow result = tileArray.getRow(currentPosition);
        currentPosition++;
        return result;
      }
    }
  }

  @Override
  public Iterator<TileRow> iterator()
  {
    return tileRows.iterator();
  }
  
  /**
   * calculate the EFFECTIVE width of each row, i.e., the number
   * of tiles occupied by the row. If a picture occupies columns such that it 
   * makes the row wider than the number of tiles stored for the row, that's the
   * width we return.
   * @return
   */
  public int getWidestEffectiveWidth()
  {
    int widestWidth  = 0;
    // calculate max width of all the rows
    for (TileRow row: tileRows)
    {
      int rowWidth = row.effectiveWidth();
      widestWidth = Math.max(widestWidth, rowWidth);
    }
    return widestWidth;
  }
  
}
