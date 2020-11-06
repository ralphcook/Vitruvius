package org.rc.vitruvius.text;


import org.rc.vitruvius.MessageListener;
import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.ui.Picture;

/**
 * Translates from text to a TileArray.
 * @author rcook
 *
 */
public class TextTranslator
{
  private MessageListener messageListener = null;
  
  private static String OCCUPIED_TILE_MESSAGE = "Error placing character <%s> at row %d, col %d; tile already in use.%n";
  
  /**
   * Constructor, including object to listen for messages from methods from this object.
   * @param messageListener
   */
  public TextTranslator(MessageListener messageListener)
  {
    this.messageListener = messageListener;
  }
  
  /**
   * Send given string, constructed as for String.format(String, Object...), to this
   * object's message listener.
   * @param formatString
   * @param args
   */
  public void say(String formatString, Object ... args)
  {
    String resultString = String.format(formatString, args);
    messageListener.addMessage(resultString);
  }

  /**
   * Create a TileArray from the given text. Each character represents a glyph for
   * the tiles, with newlines between rows of tiles.
   * @param text
   * @return
   */
  public TileArray createTileArray(String text)
  {
    TileArray tileArray = new TileArray();
    String[] lines = text.split("\n");
    
    int currentCharacterRow = 0;
    for (String line :  lines)
    {
      int currentCharacterColumn = 0;
      for (int position=0; position<line.length(); position++)
      {
        String character = line.substring(position, position+1);
        Picture picture = Picture.lookup(character);
        Tile currentTile = null;
        
        // if the character indicates a continuation tile, we should not have to place it
        // since it should already be in the tileArray from having its primary tile placed.
        // It doesn't interfere with anything already there. So we skip it; either the
        // tile will be covered by multi-tile picture, or it will be null and nothing will
        // be put there. We assume here that all continuation characters are 1x1 tile.
        // TODO: what if there's a misplaced continuation character in the text array?
        switch (picture)
        {
        case CONTINUATION : 
          break;
        case SPACE        : currentTile = new Tile(); 
          break;
        default           :
          currentTile = new Tile(picture);
          int currentTileRow    = currentCharacterRow;
          int currentTileColumn = currentCharacterColumn;
          
          // some pictures require more than one row and/or column; for each
          // tile this one occupies beyond its upper left one, put a continuation
          // tile to indicate that the tile is in use.
          for (int pictureRow=0; pictureRow<picture.rows(); pictureRow++) 
          {
            for (int pictureColumn=0; pictureColumn<picture.columns(); pictureColumn++)
            {
              int targetRow = currentCharacterRow + pictureRow;
              int targetColumn = currentCharacterColumn + pictureColumn;
              // if the tile where we would put this is already occupied, print a warning.
              Tile existingTile = tileArray.getTile(targetRow, targetColumn);
              if (existingTile == null || existingTile.type() == Tile.Type.EMPTY)
              {
                if (pictureRow==0 && pictureColumn==0)
                      { tileArray.put(currentTile,                                 targetRow, targetColumn); }
                else  { tileArray.put(new Tile(currentTileRow, currentTileColumn), targetRow, targetColumn); } 
              }
              else
              {
                String message = String.format(OCCUPIED_TILE_MESSAGE, character, targetRow, targetColumn);
                messageListener.addMessage(message);
              }
                
            } // end pictureColumns
          } // end pictureRows
        } // end switch
        currentCharacterColumn++;
      }
      currentCharacterRow++;
    }
    return tileArray;
  }
}
