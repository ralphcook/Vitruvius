package org.rc.vitruvius.model;

import java.util.ArrayList;
import java.util.Iterator;

public class TileRow
{
  private ArrayList<Tile> row = null;
  
  public int length() { return row.size(); }
  public Tile get(int index) 
  {
    if (index > row.size()) 
    { 
      String errorMessage = String.format("Attempt to get tile %d from TileRow with %d elements",
                                          index, row.size()
                                          );
      throw new IllegalArgumentException(errorMessage);
    }
    return row.get(index); 
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
}
