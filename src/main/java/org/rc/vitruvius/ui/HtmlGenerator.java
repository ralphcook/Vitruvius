package org.rc.vitruvius.ui;

import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.TileRow;

/**
 * Generate HTML from the current TileArray.
 * @author rcook
 *
 */
public class HtmlGenerator
{
  private static String startTable = "<table border=0 cellPadding=0 cellspacing=0>\n";    // do we need width & height?
  private static String endTable   = "</table>\n";
  
  private static String startRow = "  <tr>\n";
  private static String endRow   = "  </tr>\n";
  
  private static String startHtml = "<html>\n";
  private static String endHtml   = "</html>\n";
  
  public static void say(String s) { System.out.println(s); }

  @SuppressWarnings("unused")
  public  static void say(String formatString, Object ... args) { System.out.println(String.format(formatString, args)); }
  
  /**
   * Generate HTML suitable as an entire HTML page.
   * @param tiles
   * @return
   */
  public static String generateFullHtml(TileArray tiles)
  {
    StringBuilder output = new StringBuilder();
    output.append(startHtml);
    generateForumSB(output, tiles);
    output.append(endHtml);
    return new String(output);
  }
  
  /**
   * Generate HTML suitable for inclusion in the Heavegames forum (or elsewhere), 
   * i.e., HTML without html tags.
   * @param tiles
   * @return
   */
  public static String generateForumHtml(TileArray tiles)
  {
    StringBuilder output = new StringBuilder();
    generateForumSB(output, tiles);
    return new String(output);
  }
  
  /**
   * From the given set of tiles, generate HTML without start/end HTML tags
   * (suitable for the Heavengames forum) and accumulate it in the given 
   * StringBuilder.
   * @param output
   * @param tiles
   */
  private static void generateForumSB(StringBuilder output, TileArray tiles)
  {
    int cellSize = 25;
    String pictureCellFormat  = "    <td rowspan=%d colspan=%d><IMG SRC=%s%s.gif style=\"height:%dpx;width:%dpx\" title=\"%s\"></td>\n";
    String emptyCellFormat    = "    <td width=%d height=%d> </td>\n";
    
    output.append(startTable);
    for (TileRow row: tiles)
    {
      String pictureString = null;
      output.append(startRow);
      for (Tile tile: row)
      {
        switch (tile.type()) 
        {
        case CONTINUATION:
          // nothing to do here.
          break;
        case EMPTY:
          output.append(String.format(emptyCellFormat,  cellSize, cellSize));
          break;
        default:
          Picture picture = tile.picture();
              String imageDir = "/strategy/housing/images/";
              imageDir = "images/";
              pictureString = String.format(pictureCellFormat, 
                                          picture.rows(), picture.columns(), 
                                          imageDir, picture.getImageName(), 
                                          cellSize*picture.rows(), cellSize*picture.columns(), 
                                          picture.getImageName());
              output.append(pictureString);
              break;
        }
      }
      output.append(endRow);
    }
    output.append(endTable);
  }
}
