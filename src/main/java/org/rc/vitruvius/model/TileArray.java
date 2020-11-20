package org.rc.vitruvius.model;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.rc.vitruvius.model.Tile.Type;
import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.Picture;

/**
 * Dynamic 2d collection of Tiles, organized as a list of TileRow.
 * 
 * @author rcook
 * 
 * @see org.rc.vitruvius.model.Tile
 * @see org.rc.vitruvius.model.TileRow
 *
 */
public class TileArray implements Iterable<TileRow>
{
  public static void say(String s) { System.out.println(s); }
  
  private ArrayList<TileRow>  tileRows = null;
  private int                 rows      = 0;
  private int                 columns   = 0;
  private boolean             rowsAndColumnsDirty = true;
  
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
    calculateRowsAndColumns();
  }
  
  /**
   * Given the index tile position in the tile array, set to
   * Tile.Type.EMPTY all the tiles that otherwise have values
   * because of the Picture object at the index location.
   * @param indexTilePosition
   */
  public void deletePictureTiles(Point indexTilePosition)
  {
    int indexCol = indexTilePosition.x;
    int indexRow = indexTilePosition.y;
    Tile indexTile = getTile(indexRow, indexCol);
    Picture p = indexTile.picture();
    Tile emptyTile = new Tile();
    for (int i=0; i<p.rows(); i++)
    {
      for (int j=0; j<p.columns(); j++)
      {
        put(emptyTile, indexRow+i, indexCol+j);
      }
    }
  }
  
  /**
   * return the number of non-empty rows in the enclosing rectangle
   * of this TileArray.
   * @return
   */
  // TODO: don't keep recalculating this; implement a 'dirty' flag set
  // when the value changes, calculate lazily when needed.
  public int rows()
  {
    if (rowsAndColumnsDirty) { calculateRowsAndColumns(); }
    return rows;
  }

  /**
   * return the number of non-empty columns in the enclosing rectangle
   * of this tile array. 
   * @return
   */
  // TODO: redo calculation for empty tiles. Handles tileArrays made from
  // pictures for the moment, since those are all solid rectangles.
  public int columns()
  {
    if (rowsAndColumnsDirty) { calculateRowsAndColumns(); }
    return columns;
  }
  
  private void calculateRowsAndColumns()
  {
    rows = tileRows.size();
    for (TileRow row : tileRows)
    {
      columns = Math.max(columns, row.length());
    } 
    rowsAndColumnsDirty = false;
  }
  
  /**
   * return true if this tile array, starting at the given row and column,
   * has any non-empty tiles at the same position as the given tile array
   * has starting at its 0,0. 
   */
  public boolean anyOverlap(TileArray smallerTileArray, int startColumn, int startRow)
  {
    int columns = smallerTileArray.columns();
    int rows    = smallerTileArray.rows();
    boolean result = false;
    for (int i=0; i<rows; i++)
    {
      for (int j=0; j<columns; j++)
      {
        Tile smallerArrayTile = smallerTileArray.getTile(i, j);
        Tile largerArrayTile  = getTile(i+startRow, j+startColumn);
        if (smallerArrayTile != null && smallerArrayTile.type() != Tile.Type.EMPTY)
        {
          if (largerArrayTile != null && largerArrayTile.type() != Tile.Type.EMPTY)
          {
            result = true;    // found an overlap.
            break;
          }
          if (result) { break; }
        }
        if (result) { break; }
      }
    }
    return result;
  }
  
  /**
   * Return a string that is "tileArray: " followed by a concatenation of the strings 
   * representing each row, or an indication that there are no rows.
   */
  public String toString()
  {
    String newLine = System.getProperty("line.separator");
    StringBuilder sb = new StringBuilder("tileArray: ");
    sb.append(newLine);
    if (tileRows == null) 
    { 
      sb.append("<null tileRows>");
      sb.append(newLine);
    }
    else
    {
      for (TileRow tileRow: tileRows)
      {
        if (tileRow == null) { sb.append("<null TR>"); }
                        else { sb.append(tileRow.toString()); }
        sb.append(newLine);
      }
      
    }
    return new String(sb);
  }
  
  /**
   * Create a TileArray object by reading given file.
   * @param saveFile
   * @throws IOException
   * @throws Exception
   */
  public static TileArray readFromFile(File saveFile) throws IOException, Exception
  {
    BufferedReader reader = null;
    
    TileArray readTileArray = new TileArray();
    try
    {
      reader = new BufferedReader(new FileReader(saveFile));
      
      String line = reader.readLine();
      if (!(line.startsWith("tileArray:")))
      {
        throw new Exception(I18n.getString("tileFileIncorrectFormat"));
      }
      else
      {
        line = reader.readLine();
        while(line.startsWith("row:"))
        {
          line = line.substring(4);   // cut off "row:"
          TileRow tileRow = new TileRow();
          String[] tileStrings = line.split(";");
          if (tileStrings.length > 1 || (!tileStrings[0].equals("")))
          {
            for (String part: tileStrings)
            {
              Tile newTile = null;
//            if (part.length() == 0)                   { newTile = null; }
//            else if (part.startsWith("null"))         { newTile = null; }
//            else 
              if (part.startsWith("EMPTY"))        { newTile = new Tile(); }
              else if (part.startsWith("CONTINUATION")) { newTile = new Tile(Type.CONTINUATION); }
              else if (part.startsWith("PICTURE"))      { String pictureKey = part.substring("PICTURE:".length());
              newTile = Tile.getTileFromPictureKey(pictureKey);
              }
              else 
              { say("unexpected tileString while reading: <" + part + ">"); }
              tileRow.add(newTile);
            }
          }
          line = reader.readLine();
          readTileArray.addRow(tileRow);
        }
        if (!(line.startsWith("endTileArray"))) { throw new Exception(I18n.getString("tileFileIncorrectFormat")); }
      }
    }
    catch (IOException ioe)
    {
      throw new IOException(I18n.getString("errorOpeningTileFileException"), ioe);
    }
    finally
    {
      if (reader != null) { reader.close(); }
    }
    return readTileArray;
//    displayTileArray(readTileArray);
  }
  
  public void saveToFile(File file) throws Exception
  {
    BufferedWriter writer = null;
    writer = new BufferedWriter(new FileWriter(file));

    try
    {
      writer.write("tileArray:");
      writer.newLine();

      // if tileRows is null or empty, no lines get written for rows.
      if (tileRows != null && (!tileRows.isEmpty()))
      {
        for (TileRow row: tileRows)
        {
          say("Writing row");
          row.saveToFile(writer);
        }
      }
      
      writer.write("endTileArray");
      writer.newLine();
    }
    catch (Exception exception)
    {
      throw new Exception("Error writing save file", exception);
    }
    finally
    {
      writer.close();
    }
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
  
  public boolean accepts(TileArray ta, Point arrayPosition)
  {
    boolean result = true;
    for (int rowOffset=0; rowOffset < ta.rows(); rowOffset++)
    {
      for (int colOffset=0; colOffset < ta.columns(); colOffset++)
      {
        Tile currentTile = ta.getTile(rowOffset, colOffset);
        if (currentTile != null && currentTile.type() != Tile.Type.EMPTY)
        {
          Tile existingTile = getTile(arrayPosition.y + rowOffset, arrayPosition.x + colOffset);
          boolean tileFree = (existingTile == null || existingTile.type() == Tile.Type.EMPTY);
          if (!tileFree)
          {
            result = false;
            break;
          }
        }
      }
      if (!result) { break; }
    }
    return result;
  }
  
  /**
   * Put the entire given Tile array in this tile array at the given location;
   * involves putting the picture tile in the given tile location,
   * and continuation tiles in place for the columns and rows taken
   * up by the Picture.
   * @param p
   * @param rowNumber
   * @param colNumber
   */
  public void put(TileArray ta, Point arrayPosition)
  {
    for (int rowOffset=0; rowOffset < ta.rows(); rowOffset++)
    {
      for (int colOffset=0; colOffset < ta.columns(); colOffset++)
      {
        Tile currentTile = ta.getTile(rowOffset, colOffset);
        put(currentTile, arrayPosition.y + rowOffset, arrayPosition.x + colOffset);
      }
    }
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
