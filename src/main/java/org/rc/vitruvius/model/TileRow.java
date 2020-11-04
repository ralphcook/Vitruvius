package org.rc.vitruvius.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.rc.vitruvius.ui.Picture;

public class TileRow implements Iterable<Tile>
{
  private ArrayList<Tile> row = null;
  
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
   * put the given tile at the given 0-based column number on this TileRow.
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
  
  public int length() { return row.size(); }
  
  public int effectiveWidth()
  {
    int position = 0;
    while (position < row.size())
    {
      Tile tile = row.get(position);
      if (tile == null || tile.picture() == null)
      {
        position++;
      }
      else
      {
        Picture picture = tile.picture();
        position += picture.columns();
      }
    }
    return position;
  }
  
  public void add(Tile tile) { row.add(tile); }
  
  /**
   * return the tile at the given 0-based position on this row,
   * or null if there is no such tile. 
   * @param index
   * @return
   */
  public Tile get(int index) 
  {
    Tile result = null;
    if (index < row.size()) { result = row.get(index); }
    return result;
  }
  
  public void set(int index, Tile tile)
  {
    checkLength(index);
    row.set(index, tile);
  }
  
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
  
  public Iterator<Tile> getTileIterator()
  {
    return new TileIterator(this);
  }
  
  class TileIterator implements Iterator<Tile>
  {
    TileRow row = null;
    private int currentPosition = -1;
    
    public TileIterator(TileRow givenRow)
    {
      if (givenRow == null) { row = new TileRow(); }
                       else { row = givenRow; }
      currentPosition = 0;
    }
    
    public boolean hasNext()     {      return (currentPosition > row.length());    }
    public Tile next()
    {
      Tile result = null;
      if (hasNext()) 
      {
        result = row.get(currentPosition);
        currentPosition++;
      }
      return result;
    }
  }

  @Override
  public Iterator<Tile> iterator()
  {
    return row.iterator();
  }
}
