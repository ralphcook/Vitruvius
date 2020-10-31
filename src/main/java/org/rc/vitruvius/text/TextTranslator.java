package org.rc.vitruvius.text;


import org.rc.vitruvius.MessageListener;
import org.rc.vitruvius.ui.Picture;

public class TextTranslator
{
  private MessageListener messageListener = null;
  
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
  
  public Picture[][] createPictureArray(String text)
  {
    String[] textArray = text.split("\n");
//    Picture[][] pictureArray = createEmptyPictureArray(textArray);
    Picture[][] pictureArray = createEmptyPictureArray2(textArray);
    say("picture array of width %d and height %d%n", pictureArray[0].length, pictureArray.length);
    
    // now that we have a 2d array the correct size, we go through each letter, putting the
    // appropriate Picture instance(s) in the array for that letter. If the picture for that letter
    // occupies multiple cells, the 'extra' cells are filled in with OCCUPIED pictures; since
    // we traverse left-to-right then top-to-bottom, and pre-fill for this case in those same
    // directions starting with the upper-left corner of any multi-cell building,
    // we will fill cells with OCCUPIED before any letter that the user put in those slots; user 
    // should be using space or "." in those slots. 
    
    for (int currentRow=0; currentRow<pictureArray.length; currentRow++)
//    for (int currentRow=0; currentRow<textArray.length; currentRow++)
    {
      // if we're in a row with no corresponding string, we'll be putting
      // a Picture.SPACE in the null portions. We achieve that by assigning an empty
      // string to the row.
      String sourceString = null;
      if (currentRow >= textArray.length) { sourceString = ""; }
                                    else { sourceString = textArray[currentRow]; }
      
      for (int currentColumn=0; currentColumn<pictureArray[currentRow].length; currentColumn++)
//      for (int currentColumn=0; currentColumn<textArray[currentRow].length(); currentColumn++)
//      for (int currentColumn=0; currentColumn<currentString.length(); currentColumn++)
      {
        // if array is longer than the string, put in spaces for cells beyond the string length
        if (currentColumn >= sourceString.length())
        {
          if (pictureArray[currentRow][currentColumn] == null)
          {
            pictureArray[currentRow][currentColumn] = Picture.SPACE;
          }
        }
        else
        {
          String currentLetter = sourceString.substring(currentColumn,currentColumn+1);
          
          // pictureArray cell should be null;
          // if it's occupied because of a previously encountered
          // multi-cell building, we just report it.
          if (pictureArray[currentRow][currentColumn] != null)
          {
            if (!currentLetter.equals(".") && !currentLetter.equals(" "))
            { int row = currentRow + 1;
            int col = currentColumn + 1;
            say("letter %s in row %d, column %d ignored; space already occupied%n", 
                currentLetter, row, col
                );
            }
          }
          else
          {
            // get the picture for this letter and put it in the upper-lefthand corner
            // of the set of cells/tiles that will hold it.
            Picture currentPicture = Picture.lookup(currentLetter);
            pictureArray[currentRow][currentColumn] = currentPicture;
            
            // for each row and column occupied by this picture, put an OCCUPIED picture in
            // the corresponding cell/tile to signal that this space is already occupied.
            int numRows = pictureArray.length;
            int numCols = pictureArray[0].length;   // (all rows have the same length)
            int pictureRows    = currentPicture.rows();
            int pictureColumns = currentPicture.columns();
            for (int j=currentRow; j<currentRow+pictureRows; j++)
              for (int k=currentColumn; k<currentColumn+pictureColumns; k++)
              {
                if (j<numRows && k<numCols && (j>currentRow || k>currentColumn))
                {
                  Picture p = Picture.OCCUPIED;
                  p.setRefXAndY(currentRow, currentColumn);
                  pictureArray[j][k] = p;
                }
              }
          }
        }
      }
    }
    
    // for the entire array: replace any null values with OCCUPIED ones
    
    return pictureArray;
  }
  
  private Picture[][] createEmptyPictureArray2(String[] text)
  {
    int columns = 0;
    int rows = 0;
    
    // The text represents the array, one letter per building or space.
    // Each building takes a minimum of one row and column, but might take
    // more than that in either direction. So the array might need to be 
    // wider or longer than the number of letters in either direction,
    // and that extra might come from a letter that is not at the end of
    // that dimension. So we have to calculate the width and length (columns
    // and rows) for each letter, and allow that the letter determining the
    // columns (for instance) might not be at the end of a string.
    
    // In our loop for each string,
    // for each letter in our string, add the width represented by the letter
    // to the current number of letters in the string, and set the currently
    // determined array width to the max of that and the already determined
    // array width.
    // TODO: sort-of bug, if the user puts, say HH at the end of the string, this 
    // code will ensure two slots for the second H, even though the image-placing 
    // code will ignore the second H because its position is occupied by the first
    // H. I'm not sure how much difference it makes; we probably need to have either
    // a data structure that does not have to pre-determine the length of the arrays,
    // or a pre-determined maximum length. 
    for (int stringPosition = 0; stringPosition < text.length; stringPosition++)
    {
      String s = text[stringPosition];
      for (int letterPosition = 0; letterPosition < s.length(); letterPosition++)
      {
        String letter = s.substring(letterPosition,letterPosition+1);
        Picture p = Picture.lookup(letter);
        int tempColumns = letterPosition + p.columns();
        if (tempColumns > columns) { columns = tempColumns; }
        
        int tempRows = stringPosition + p.rows();
        if (tempRows > rows) { rows = tempRows; }
      }
    }
    
    Picture[][] array = new Picture[rows][columns];
    return array;
  }
  
  private Picture[][] createEmptyPictureArray(String[] text)
  {
    // first determine the size of the array we need
    // need to add the heights and widths of the component pieces;
    // the number and width of the strings aren't enough if there are buildings
    // with widths that spill over those figures.
    int     maxStringLength = 0;
    
    if (text != null && text.length > 0)
    {
      for (String s: text)
      {
        maxStringLength = Math.max(maxStringLength, s.length());
      }
    }
    
    // we'll make one column per letter of the maximum string length,
    // and one row per text string.
    int columns = maxStringLength;
    int rows    = text.length;
    Picture[][] pictureArray = new Picture[rows][columns];
    return pictureArray;
  }
}
