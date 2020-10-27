package org.rc.vitruvius.ui;

public class HtmlGenerator
{
  private static String startTable = "<table border=0 cellPadding=0 cellspacing=0>\n";    // do we need width & height?
  private static String endTable   = "</table>\n";
  
  private static String startRow = "  <tr>\n";
  private static String endRow   = "  </tr>\n";
  
  private static String startHtml = "<html>\n";
  private static String endHtml   = "</html>\n";
  
  private static void output(String s) { System.out.println(s); }

  private static void output(String formatString, Object ... args) 
  { 
    String resultString = String.format(formatString, args);
    output(resultString);
  }
  
  public static String generateFullHtml(Picture[][] pictures)
  {
    StringBuilder output = new StringBuilder();
    output.append(startHtml);
    generateForumSB(output, pictures);
    output.append(endHtml);
    return new String(output);
  }
  
  public static String generateForumHtml(Picture[][] pictures)
  {
    StringBuilder output = new StringBuilder();
    generateForumSB(output, pictures);
    return new String(output);
  }
  
  private static void generateForumSB(StringBuilder output, Picture[][] pictures)
  {
    int cellSize = 25;
    String cellFormat      = "    <td colspan=%d rowspan=%d><IMG SRC=%s%s.gif style=\"height:%dpx;width:%dpx\" title=\"%s\"></td>\n";
    String emptyCellFormat = "    <td width=%d height=%d> </td>\n";
    output.append(startTable);
    for (Picture[] row: pictures)
    {
      String pictureString = null;
      output.append(startRow);
      for (Picture picture: row)
      {
        switch(picture)
        {
          case OCCUPIED: 
            break;
          case SPACE: 
          case OTHERSPACE: 
            pictureString = String.format(emptyCellFormat, cellSize, cellSize);
            output.append(pictureString);
            break;
          default:
            String imageDir = "/strategy/housing/images/";
            imageDir = "images/";
            pictureString = String.format(cellFormat, picture.columns(), picture.rows(), imageDir, picture.getImageName(), 
                                                  cellSize*picture.columns(), cellSize*picture.rows(), picture.getImageName());
            output.append(pictureString);
            break;
        }
      }
      output.append(endRow);
    }
    output.append(endTable);
  }
}
