package org.rc.vitruvius.ui;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Each enum value Represents one glyph image. The name of each glyph type 
 * is used to index the display text for that glyph. Therefore the name of
 * an enum cannot be changed without changing its corresponding entry in
 * MessageBundle.properties; if we get more languages for this text, the
 * key would need to be changed in all the properties files where it appears. 
 * 
 * TODO: create rotated glyphs, maybe aqueducts and other things missing.
 * @author rcook
 *
 */
public enum Picture
{
 //  type         letter filename            rows cols
   CONTINUATION   (".",  "(cont)",            1, 1)   // A period represents a tile that will have part of a glyph other than 
  ,SPACE          (" ",  "(spc)",             1, 1)   // its upper left-hand corner.
  ,
   academy        ("A","academy",             3, 3)
  ,MILacademy     ("Q","academy-military",    3, 3)
  ,amphitheater   ("X","amphitheater",        3, 3)
//  ,aqH            ("-","aq-h",                1, 1)
  ,arcH           ("[","arc-h",               3, 3)
//  ,arcV           ("]","arc-v",               3, 3)
  ,actorcolony    ("a","actorcolony",         3, 3)   // renamed from "artcolony"
  ,barber         ("B","barber",              1, 1)
  ,barracks       ("q","barracks",            3, 3)
  ,bath           ("b","bath",                2, 2)
  ,ceres          ("0","ceres-s",             2, 2)
  ,ceresL         ("5","ceres-l",             3, 3)
  ,chariotmaker   ("z","chariotmaker",        3, 3)
  ,clay           ("c","clay",                2, 2)
  ,coliseum       ("C","colloseum",           5, 5)
  ,doctor         ("D","doctor",              1, 1)
  ,engineer       ("E","engineer",            1, 1)
  ,fortG          ("j","fort-ground",         4, 4)
  ,fortH          ("J","fort-house",          3, 3)
  ,forum          ("f","forum",               2, 2)
  ,fountain       ("F","fountain",            1, 1)
  ,fruit          ("e","fruit",               3, 3)
  ,garden         ("G","garden1x1",           1, 1)
  ,gatehouseH     ("(","gatehouse-h",         2, 2)
//  ,gatehouseV     (")","gatehouse-v",         2, 2)
  ,gladiator      ("y","gladiator",           3, 3)
  ,gov            ("k","gov-small",           4, 4)
  ,granary        ("g","granary",             3, 3)
  ,hippodroom     ("Z","hippodroom",         15, 5)
  ,hospital       ("+","hospital",            3, 3)
  ,house1         ("h","house1x1",            1, 1)
  ,house2         ("H","house2x2",            2, 2)
  ,house3         ("d","house3x3",            3, 3)
  ,iron           ("i","iron",                2, 2)
  ,library        ("L","library",             2, 2)
  ,lionpit        ("l","lionpit",             3, 3)
  ,marble         ("m","marble",              2, 2)
  ,market         ("M","market",              2, 2)
  ,mars           ("3","mars-s",              2, 2)
  ,marsL          ("8","mars-l",              3, 3)
  ,mercury        ("2","mercury-s",           2, 2)
  ,mercuryL       ("7","mercury-l",           3, 3)
  ,mission        ("Y","mission",             2, 2)
  ,neptune        ("1","neptune-s",           2, 2)
  ,neptuneL       ("6","neptune-l",           3, 3)
  ,olives         ("o","olives",              3, 3)
  ,oracle         ("@","oracle",              2, 2)
  ,palace         ("$","palace",              4, 4)
  ,pigs           ("p","pigs",                3, 3)
  ,plaza          ("#","plaza",               1, 1)
  ,prefect        ("P","prefect",             1, 1)
  ,reservoir      ("r","reservoir",           3, 3)
  ,road           ("R","road",                1, 1)
  ,school         ("S","school",              2, 2)
//  ,senate         ("s","senate",              5, 5)
  ,statue1        ("*","statue1x1",           1, 1)
  ,statue2        (":","statue2x2",           2, 2)
  ,statue3        ("!","statue3x3",           3, 3)
  ,theater        ("x","theater",             2, 2)
  ,tower          ("T","tower",               2, 2)
  ,vegetables     ("v","vegetables",          3, 3)
  ,venus          ("4","venus-s",             2, 2)
  ,venusL         ("9","venus-l",             3, 3)
  ,vines          ("u","vines",               3, 3)
//  ,wall           ("t","wall-h",              1, 1)
  ,warehouse      ("W","warehouse",           3, 3)
  ,wharf          ("V","wharf",               2, 2)
  ,wheat          ("w","wheat",               3, 3)
  ,wood           ("n","wood",                2, 2)
  ,workshopW      ("I","workshop-weapons",    2, 2)
  ,workshopP      ("K","workshop-pottery",    2, 2)
  ,workshopF      ("N","workshop-furniture",  2, 2)
  ,workshopO      ("O","workshop-oil",        2, 2)
  ,workshopw      ("U","workshop-wine",       2, 2)  
  ;
  
  private String  key;
  private String  imageName;
  private int     columns;
  private int     rows;
  
  private ImageIcon cachedImageIcon = null;   // lazy instantiation at first use.
  
  private final static String IMAGE_FILEPATH_FORMAT = "/images/%s.gif";
  
  private Picture(String key, String imageName, int columns, int rows)
  {
    this.key       = key;
    this.imageName = imageName;
    this.columns   = columns;
    this.rows      = rows;
  }
  
  public String getKey()       { return key; }
  public String getImageName() { return imageName; }
  public String getDisplayText() 
  { 
    // use the enum's name as the key to the resource bundle
    String enumName = name();
    String displayText = I18n.getString(enumName);
    if (displayText == null) { displayText = enumName; }
    return displayText;
  }
  
  /**
   * Report any image files that are not where they're supposed to be.
   */
  public static void checkImageFiles()
  {
    for (Picture p: values())
    {
      if (p != CONTINUATION && p != SPACE)
      {
        ImageIcon image = p.getImageIcon();
        if (image == null)
        {
          System.err.println(String.format("No glyph image for %s (%s)", p.name(), p.getImageName()));
        }
      }
    }
  }

  /**
   * Look up the Picture that corresponds to this key.
   * @param key
   * @return
   */
  public static Picture lookup(String key)
  {
    Picture returnValue = null;
    if (key != null)
    {
      for (Picture p: values())
      {
        if (p.key.equals(key))
        {
          returnValue = p;
          break;
        }
      }
    }
    return returnValue;
  }
  
  /**
   * return the Swing component associated with this picture;
   * if this picture is a continuation of a picture originating
   * somewhere else, this method returns null.
   * @param tileSize - number of pixels on each edge of the square
   * tile; used to scale the image according to the number of rows
   * and columns of tiles occupied by the picture.
   * @param height
   * @return
   */
  public JLabel getLabel(int tileSize)
  {
    JLabel result = null;
    if (this != CONTINUATION && this != SPACE)
    {
      ImageIcon imageIcon = getImageIcon(tileSize);
      result = new JLabel(imageIcon);

      int width = columns * tileSize;
      int height = rows * tileSize;
      result.setSize(new Dimension(width, height));
      result.setToolTipText(getDisplayText());
    }
    return result;
  }
  
  /**
   * Get a scaled image icon for this picture
   * @param tileSize size in pixels of the square size of the resulting icon
   * @return ImageIcon of this picture's glyph scaled to the given size.
   */
  public ImageIcon getImageIcon(int tileSize)
  {
    ImageIcon imageIcon = getImageIcon();
    int width = columns * tileSize;
    int height = rows * tileSize;
    Image scaledImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
    ImageIcon result = new ImageIcon(scaledImage);
    return result;
  }
  
  /**
   * Get the image icon for this Picture.
   * @return
   */
  public ImageIcon getImageIcon()
  {
    if (cachedImageIcon == null)
    {
      String filepath = String.format(IMAGE_FILEPATH_FORMAT, imageName);
      java.net.URL imgURL = getClass().getResource(filepath);
      if (imgURL != null) { cachedImageIcon = new ImageIcon(imgURL); }
    }
    return cachedImageIcon;
  }
  
  /**
   * Return the number of columns used by this glyph, i.e., the width in tiles.
   * @return
   */
  public int columns()  { return columns; }
  
  /**
   * Return the number of rows used by this glyph, i.e., the height in tiles.
   * @return
   */
  public int rows()     { return rows; }
  
  public String toString() { return I18n.getString(name()); }
}
