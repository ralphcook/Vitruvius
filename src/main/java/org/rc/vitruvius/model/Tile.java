package org.rc.vitruvius.model;

import javax.swing.JLabel;

import org.rc.vitruvius.ui.Picture;

/**
 * One tile on the display grid; Types currently support an empty tile, a tile with the upper left
 * tile of a graphic, or a 'continuation' tile. This last refers to each tile occupied by a glyph
 * except for the upper left one, i.e., if the glyph covers 2x2 tiles, the upper left is a PICTURE
 * tile and the other three are CONTINUATION tiles. 
 * 
 * @author rcook
 *
 */
public class Tile
{
  public enum Type { EMPTY, PICTURE, CONTINUATION };
  
  private Picture picture = null;
  private Type    type    = null;
  private int     row     = -1;
  private int     column  = -1;
  
  public Tile()                 { type = Type.EMPTY; }
  public Tile(int row, int col) { type = Type.CONTINUATION; this.picture = null;  this.row = row; this.column = col; } 
  public Tile(Picture p)        { type = Type.PICTURE;      this.picture = p;     this.row = -1;  this.column = -1; }
  
  public Picture picture()  { return picture; }
  public Tile.Type type()   { return type; }
  public int row()          { return row; }
  public int column()       { return column; }
  
  public void set(int row, int col) { type = Type.CONTINUATION; this.picture = null;  this.row = row; this.column = col; }
  public void set(Picture pic)      { type = Type.PICTURE;      this.picture = pic;   this.row = -1;  this.column = -1; }

  /**
   * return type of tile; if picture, name of picture file; if continuation, row and column referenced.
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder(type.toString());
    if (type == Type.PICTURE) { sb.append(": "); sb.append(picture.getImageName()); }
    else if (type == Type.CONTINUATION) { sb.append(String.format(": r%d,c%d", row, column)); } 
    return new String(sb);
  }
  
  /**
   * Return a JLabel of a given size to display for this tile.
   * <P> If null, indicates no component is displayed for this tile;
   * it could be that the space this tile would occupy is already
   * occupied by a component that originates in another tile and
   * shows on multiple tiles.
   * @param tileSize number of pixels of one edge of a one tile; used
   * to scale the component, which might occupy more than one tile,
   * to its final size.
   * @return
   */
  public JLabel getLabel(int tileSize)
  {
    // FOR THE MOMENT: we're only doing images of buildings; later
    // we can decide whether to return something for landscape.
    JLabel label = null;
    if (picture != null) { label = picture.getLabel(tileSize); }
    return label;
  }
}
