package org.rc.vitruvius.text;


import org.rc.vitruvius.MessageListener;
import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.ui.Picture;

public class TextTranslator
{
  private MessageListener messageListener = null;
  
  private static String OCCUPIED_TILE_MESSAGE = "Error placing character <%s> at row %d, col %d; already in use.%n";
  
  public TextTranslator(MessageListener messageListener)
  {
    this.messageListener = messageListener;
  }
  
  public void say(String formatString, Object ... args)
  {
    String resultString = String.format(formatString, args);
    messageListener.addMessage(resultString);
  }
  public TextTranslator() {}
  
  public TileArray createTileArray(String text)
  {
    // We create an empty TileArray and TileRow; for each letter in each
    // newline-terminated string, create a Picture instance and add it to
    // the TileRow. On newline or end of overall string, add the TileRow
    // to the TileArray.
    TileArray tileArray = new TileArray();
    String[] lines = text.split("\n");
////// //    Picture continuationPicture = Picture.CONTINUATION;
////// //    Tile    continuationTile    = new Tile(continuationPicture);
    
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
          // not right; still thinking about this one...  
          // check for continuation tile already in place?
          // currentTile = new Tile(currentCharacterRow, currentCharacterColumn); 
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
                System.out.printf(OCCUPIED_TILE_MESSAGE, character, targetRow, targetColumn);
              }
                
            } // end pictureColumns
          } // end pictureRows
        } // end switch
        currentCharacterColumn++;
      }
      currentCharacterRow++;
    }
    
//    tileArray.completeRectangle();
    
    return tileArray;
  }
}
