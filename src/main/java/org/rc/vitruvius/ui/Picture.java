package org.rc.vitruvius.ui;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public enum Picture
{
   OCCUPIED       ("",   null,                1, 1) // put in by the program for cells that have no graphic
  ,SPACE          (".",  null,                1, 1)
  ,OTHERSPACE     (" ",  null,                1, 1)
  ,academy        ("A","academy",             3, 3)
  ,MILacademy     ("Q","academy-military",    3, 3)
  ,amphitheater   ("X","amphitheater",        3, 3)
//  ,aqH            ("-","aq-h",                1, 1)
  ,arcH           ("[","arc-h",               3, 3)
//  ,arcV           ("]","arc-v",               3, 3)
  ,artcolony      ("a","artcolony",           3, 3)
  ,barber         ("B","barber",              1, 1)
  ,barracks       ("q","barracks",            3, 3)
  ,bath           ("b","bath",                2, 2)
  ,ceres          ("0","ceres-s",             2, 2)
  ,ceresL         ("5","ceres-l",             3, 3)
  ,chariotmaker   ("z","chariotmaker",        3, 3)
  ,clay           ("c","clay",                2, 2)
  ,colloseum      ("C","colloseum",           5, 5)
  ,doctor         ("D","doctor",              1, 1)
  ,engineer       ("E","engineer",            1, 1)
  ,fortG          ("j","fort-ground",         4, 4)
  ,fortH          ("J","fort-house",          3, 3)
  ,forum          ("f","forum",               2, 2)
  ,fountain       ("F","fountain",            1, 1)
  ,fruit          ("e","fruit",               3, 3)
  ,garden1        ("G","garden1x1",           1, 1)
  ,gatehouseH     ("(","gatehouse-h",         2, 2)
  ,gatehouseV     (")","gatehouse-v",         2, 2)
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
  
  private int     refX = -1;   public int getRefX() { return refX; }
  private int     refY = -1;   public int getRefY() { return refY; }
  public void setRefXAndY(int i, int j) { refX = i; refY = j; }  
  
  private static int maxWidth = 0;
  private static int maxHeight = 0;
  
  private Picture(String key, String imageName, int columns, int rows)
  {
    this.key       = key;
    this.imageName = imageName;
    this.columns   = columns;
    this.rows      = rows;
  }
  
  public String getKey()       { return key; }
  public String getImageName() { return imageName; }
  
//  /**
//   * return the maximum width of all images, i.e., largest number of columns occupied
//   * @return
//   */
//  public static int maxWidth()
//  {
//    if (maxWidth == 0) { calculateMaxHeightAndWidth(); }
//    return maxWidth;
//  }
//  
//  /**
//   * return the maximum height of all images, i.e., largest number of rows occupied
//   * @return
//   */
//  public static int maxHeight()
//  {
//    if (maxHeight == 0) { calculateMaxHeightAndWidth(); }
//    return maxHeight;
//  }
//  
//  private static void calculateMaxHeightAndWidth()
//  {
//    for (Picture p: values())
//    {
//      maxHeight = Math.max(maxHeight, p.rows());
//      maxWidth  = Math.max(maxWidth,  p.columns());
//    }
//  }
//  
  /**
   * Report any image files that are not where they're supposed to be.
   */
  public static void checkImageFiles()
  {
    for (Picture p: values())
    {
      if (p != OCCUPIED && p != SPACE && p != OTHERSPACE)
      {
        Image image = p.getImage();
        if (image == null)
        {
          System.err.println(String.format("No image for %s (%s)", p.name(), p.getImageName()));
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
  
  public Image getImage()
  {
    URL url = Picture.class.getResource("images/" + imageName + ".gif");
    Image image = null;
    if (url != null) { image = Toolkit.getDefaultToolkit().getImage(url); }
    return image;
  }
  
  public int columns()  { return columns; }
  public int rows()     { return rows; }
}
