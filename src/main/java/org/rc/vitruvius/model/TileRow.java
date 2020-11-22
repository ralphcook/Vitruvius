package org.rc.vitruvius.model;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A row of tiles; used in TileArray.
 * @author rcook
 * 
 * @see org.rc.vitruvius.model.TileArray
 * @see org.rc.vitruvius.model.Tile
 *
 */
public class TileRow implements Iterable<Tile>
{
  private ArrayList<Tile> row = null;
  
  /**
   * Return a string of "row: " plus a concatenation of strings representing each tile.
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (row == null) { sb.append("<null row>"); }
    else
    {
      sb.append("row:");
      for (Tile t: row)
      {
        if (t == null) { sb.append("null tile"); }
        else { sb.append(t.toString()); }
        sb.append(";");
      }
    }
    return new String(sb);
  }
  
  public boolean saveToFile(BufferedWriter writer) throws IOException
  {
    boolean result = true;
    writer.write("row:");
    // if row is null or empty, no lines get written for row
    // otherwise the tile representations get written after "row:"
    if ((row != null) && (!row.isEmpty()))
    {
      for (Tile tile: row)
      {
        tile.saveToFile(writer);
        writer.write(";");
      }
    }
    writer.newLine();
    return result;
  }
  
  public TileRow()
  {
    row = new ArrayList<>();
  }
  
  public TileRow(int cols)
  {
    row = new ArrayList<>(cols);
    for (int i=0; i<cols; i++) { row.add(null); }
  }
  
  /**
   * Put the given tile at the given 0-based column number on this TileRow.
   * Ensure that the row is long enough to contain the tile.
   * @param tile
   * @param colNumber
   */
  public void put(Tile tile, int colNumber)
  {
    if (row == null) { row = new ArrayList<>(); }
    int columnsNeeded = (colNumber + 1) - row.size();
    for (int i=0; i<columnsNeeded; i++) { row.add(new Tile()); }
    row.set(colNumber, tile);
  }
  
  /**
   * Return the size of the row.
   * @return
   */
  public int length() { return row.size(); }
  
  /**
   * Add the given tile to the end of the row.
   * @param tile
   */
  public void add(Tile tile) { row.add(tile); }
  
  /**
   * return the tile at the given 0-based position on this row,
   * or null if there is no such tile. The index can be outside
   * the range of the row.
   * @param index
   * @return
   */
  public Tile get(int index) 
  {
    Tile result = null;
    if (index < row.size()) { result = row.get(index); }
    return result;
  }
  
  /**
   * Put the given tile at the 0-based index of this row; throw an exception
   * if the index is outside the bounds of the row.
   * @param index
   * @param tile
   * @see put(Tile, int)
   */
  public void set(int index, Tile tile)
  {
    checkLength(index);
    row.set(index, tile);
  }
  
  /**
   * Throw an exception if the given index is outside the bounds of this TileRow.
   * @param index
   */
  private void checkLength(int index)
  {
    if (index >= row.size()) 
    { 
      String errorMessage = String.format("Attempt to get tile %d from TileRow with %d elements",
                                          index, row.size()
                                          );
      throw new IllegalArgumentException(errorMessage);
    }
  }
  
  /**
   * return true iff there are no non-empty tiles in this row
   * @return
   */
  public boolean isEmpty()
  {
    boolean result = true;
    for (Tile tile: row)
    {
      result = (tile == null) || tile.type() == Tile.Type.EMPTY;
      if (!result) break;
    }
    return result;
  }
  
  /**
   * Return the iterator for this TileRow.
   */
  @Override
  public Iterator<Tile> iterator()
  {
    return row.iterator();
  }
}
