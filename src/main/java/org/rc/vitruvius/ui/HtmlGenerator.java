package org.rc.vitruvius.ui;

import org.rc.vitruvius.model.Tile;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.TileRow;

public class HtmlGenerator
{
  private static String startTable = "<table border=0 cellPadding=0 cellspacing=0>\n";    // do we need width & height?
  private static String endTable   = "</table>\n";
  
  private static String startRow = "  <tr>\n";
  private static String endRow   = "  </tr>\n";
  
  private static String startHtml = "<html>\n";
  private static String endHtml   = "</html>\n";
  
  private static void output(String s) { System.out.println(s); }

  @SuppressWarnings("unused")
  private static void output(String formatString, Object ... args) 
  { 
    String resultString = String.format(formatString, args);
    output(resultString);
  }
  
  public static String generateFullHtml(TileArray tiles)
  {
    StringBuilder output = new StringBuilder();
    output.append(startHtml);
    generateForumSB(output, tiles);
    output.append(endHtml);
    return new String(output);
  }
  
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
   * <P>TODO: known problem: blank spaces in the tiles grid leads to skipped cells
   * in the HTML table, and other images then center in the combined space
   * instead of staying within their planned space.
   * <P>TODO: known problem: if some rows are longer than other rows, skipped
   * cells at the ends of rows causes images to truncated or scaled into their
   * space, causing other cells to be non-square.
   * @param output
   * @param tiles
   */
  private static void generateForumSB(StringBuilder output, TileArray tiles)
  {
    int cellSize = 25;
    String pictureCellFormat  = "    <td rowspan=%d colspan=%d><IMG SRC=%s%s.gif style=\"height:%dpx;width:%dpx\" title=\"%s\"></td>\n";
    String emptyCellFormat    = "    <td width=%d height=%d> </td>\n";
    // get the width of the widest row
    int tableWidth = tiles.getWidestEffectiveWidth();
    
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
    }
    output.append(endTable);
  }
}
