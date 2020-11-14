package org.rc.vitruvius.text;


import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.ui.Picture;

/**
 * Translates from text to a TileArray; this is based on Heavengames.com "glyphy script", which does the same 
 * operation but limits to 40x40 tiles.
 * @author rcook
 *
 */
public class TextTranslator
{
  private UserMessageListener messageListener = null;
  
  private static String OCCUPIED_TILE_MESSAGE = "Error placing character <%s> at row %d, col %d; tile already in use.%n";
  
  /**
   * Constructor, including object to listen for messages from methods from this object.
   * @param messageListener
   */
  public TextTranslator(UserMessageListener messageListener)
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
        if (picture == null)
        {
          picture = Picture.lookup(" ");
          messageListener.addMessage(String.format("No picture for character %s", character));
        }
        Tile currentTile = null;
        
        // if the character indicates a continuation tile, we should not have to place it
        // since it should already be in the tileArray from having its primary tile placed.
        // It doesn't interfere with anything already there. So we skip it; either the
        // tile will be covered by multi-tile picture, or it will be null and nothing will
        // be put there. We assume here that all continuation characters are 1x1 tile.
        switch (picture)
        {
        case CONTINUATION : 
          break;
        case SPACE        : currentTile = new Tile(); 
          break;
        default           :
          currentTile = new Tile(picture);
          
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
                      { tileArray.put(currentTile,                      targetRow, targetColumn); }
                else  { tileArray.put(new Tile(Tile.Type.CONTINUATION), targetRow, targetColumn); } 
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
