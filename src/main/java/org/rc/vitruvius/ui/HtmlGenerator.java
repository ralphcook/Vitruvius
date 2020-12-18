package org.rc.vitruvius.ui;

import java.util.prefs.Preferences;

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
  // in the 0 position, strings used for full html;
  // in the 1 position, strings used for the forum (where it turns out newlines are significant)
  private static String[][] tagStrings = 
                          {
                              // HTML
                             { "<html>\n", "<html>" }
                           , { "",         "" }           // no HTML tags on forum html
                             // TABLE
                           , { "<table border=0 cellPadding=0 cellspacing=0>\n", "<table border=0 cellPadding=0 cellspacing=0>" }
                           , { "</table>\n", "</table>" }
                             // row
                           , { "  <tr>\n", "<tr>" }
                           , { "  </tr>\n", "</tr>" }
                             // cell
                           , { "    <td rowspan=%d colspan=%d><IMG SRC=%s%s.gif style=\"height:%dpx;width:%dpx\" title=\"%s\"></td>\n",
                               "<td rowspan=%d colspan=%d><IMG SRC=%s%s.gif style=\"height:%dpx;width:%dpx\" title=\"%s\"></td>"
                             }
                           , { "    <td width=%d height=%d> </td>\n", "<td width=%d height=%d> </td>" } 
                          };
  
  private static int FULL_HTML_TAG_STRINGS = 0;
  private static int FORUM_HTML_TAG_STRINGS = 1;
  
  private static int HTML_START     = 0;
  private static int HTML_END       = 1;
  private static int TABLE_START    = 2;
  private static int TABLE_END      = 3;
  private static int ROW_START      = 4;
  private static int ROW_END        = 5;
  private static int CELL           = 6;
  private static int EMPTY_CELL     = 7;
  
//  private static String startTable = "<table border=0 cellPadding=0 cellspacing=0>\n";    // do we need width & height?
//  private static String endTable   = "</table>\n";
//  
//  private static String startRow = "  <tr>\n";
//  private static String endRow   = "  </tr>\n";
//  
//  private static String startHtml = "<html>\n";
//  private static String endHtml   = "</html>\n";
  
//  String pictureCellFormat  = "    <td rowspan=%d colspan=%d><IMG SRC=%s%s.gif style=\"height:%dpx;width:%dpx\" title=\"%s\"></td>\n";
//  String emptyCellFormat    = "    <td width=%d height=%d> </td>\n";

  private static int FORUM_CELL_SIZE = 14;
  private static int FULL_HTML_CELL_SIZE = 25;

  public static void say(String s) { System.out.println(s); }

  @SuppressWarnings("unused")
  public  static void say(String formatString, Object ... args) { System.out.println(String.format(formatString, args)); }
  
  /**
   * Generate HTML suitable as an entire HTML page.
   * @param tiles
   * @param prefix
   * @return
   */
  public static String generateFullHtml(TileArray tiles, Preferences applicationPreferences)
  {
    StringBuilder output = new StringBuilder();
    output.append(tagStrings[HTML_START][FULL_HTML_TAG_STRINGS]);
    
    String imagePrefix = applicationPreferences.get(HtmlSettingsDialog.FULL_HTML_PREFIX_PREF_KEY, "");
    generateInnerHtml(output, tiles, imagePrefix, FULL_HTML_TAG_STRINGS, FULL_HTML_CELL_SIZE);
    output.append(tagStrings[HTML_END][FULL_HTML_TAG_STRINGS]);
    return new String(output);
  }
  
  /**
   * Generate HTML suitable for inclusion in the Heavegames forum (or elsewhere), 
   * i.e., HTML without html tags.
   * @param tiles
   * @return
   */
  public static String generateForumHtml(TileArray tiles, Preferences applicationPreferences)
  {
    StringBuilder output = new StringBuilder();
    String prefix = applicationPreferences.get(HtmlSettingsDialog.HEAVEN_GAMES_PREFIX_PREF_KEY, "");
    generateInnerHtml(output, tiles, prefix, FORUM_HTML_TAG_STRINGS, FORUM_CELL_SIZE);
    return new String(output);
  }
  
  /**
   * From the given set of tiles, generate HTML without start/end HTML tags
   * (suitable for the Heavengames forum) and accumulate it in the given 
   * StringBuilder.
   * @param output
   * @param tiles
   * @param imagePrefix
   */
  private static void generateInnerHtml(StringBuilder output, TileArray tiles, String imagePrefix, int tagStringsIndex, int cellSize)
  {
    
    output.append(tagStrings[TABLE_START][tagStringsIndex]);
    for (TileRow row: tiles)
    {
      String pictureString = null;
      output.append(tagStrings[ROW_START][tagStringsIndex]);
      boolean tileOutputForRow = false;
      for (Tile tile: row)
      {
        switch (tile.type()) 
        {
        case CONTINUATION:
          // nothing to do here.
          break;
        case EMPTY:
          output.append(String.format(tagStrings[EMPTY_CELL][tagStringsIndex], cellSize, cellSize));
          tileOutputForRow = true;
          break;
        default:
          Picture picture = tile.picture();
          pictureString = String.format(tagStrings[CELL][tagStringsIndex], 
                                      picture.rows(), picture.columns(), 
                                      imagePrefix, picture.getImageName(), 
                                      cellSize*picture.rows(), cellSize*picture.columns(), 
                                      picture.getImageName());
          output.append(pictureString);
          tileOutputForRow = true;
          break;
        }
      }
      // if we didn't output anything for this row, output one empty cell as a placeholder.
      if (!tileOutputForRow) { output.append(String.format(tagStrings[EMPTY_CELL][tagStringsIndex], cellSize, cellSize)); }
      output.append(tagStrings[ROW_END][tagStringsIndex]);
    }
    output.append(tagStrings[TABLE_END][tagStringsIndex]);
  }
}
