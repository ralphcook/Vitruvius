package org.rc.vitruvius.ui;

import java.awt.Point;

/**
 * A point in the tileArray that might have a tile in it.
 * @author rcook
 *
 */
public class TilePoint
{
  public int x;     public int column() { return x; }
  public int y;     public int row()    { return y; }
  
  public TilePoint(int tilePointX, int tilePointY)
  {
    this.x = tilePointX;
    this.y = tilePointY;
  }
  
  public Point calculateGraphicsPoint(int tileSize)
  {
    int graphicsX = x * tileSize;
    int graphicsY = y * tileSize;
    return new Point(graphicsX, graphicsY);
  }
}
