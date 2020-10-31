package org.rc.vitruvius.model;

import java.util.ArrayList;
import java.util.Iterator;

public class TileArray
{
  private ArrayList<TileRow> tiles = new ArrayList<>();
  
  public int length() { return tiles.size(); }
  
  Iterator<TileRow> rowIterator()
  {
    return new TileRowIterator(this);
  }
  
  public TileRow getRow(int index)
  {
    if (index > tiles.size())
    {
      String errorMessage = String.format("Attempt to get %d TileRow from TileArray with %d elements",
                                          index, tiles.size()
                                          );
      throw new IllegalArgumentException(errorMessage);
    }
    return tiles.get(index);
  }
  
  public Tile getTile(int rowNumber, int columnNumber)
  {
    TileRow row = getRow(rowNumber);
    Tile    tile = row.get(columnNumber);
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
}
