package org.rc.vitruvius.model;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.JLabel;

import org.rc.vitruvius.ui.Picture;

/**
 * One tile on the display grid; Tile.Type is an enum of EMPTY, PICTURE, and CONTINUATION;
 * the latter is for tiles that are occpupied by a glyph beyond their upper-left-hand corner.
 * I.e., if the glyph covers 2x2 tiles, the upper left is a PICTURE tile and the other three 
 * are CONTINUATION tiles.
 * 
 *  @see org.rc.vitruvius.model.TileRow
 *  @see org.rc.vitruvius.model.TileArray
 * 
 * @author rcook
 *
 */
public class Tile
{
  public enum Type { EMPTY, PICTURE, CONTINUATION };
  
  private Picture picture = null;
  private Type    type    = null;
//  private int     row     = -1;
//  private int     column  = -1;
  
  /**
   * Create a new EMPTY tile.
   */
  public Tile()                 { type = Type.EMPTY; }
  /**
   * Create a new CONTINUATION tile, referencing the given row and column.
   * @param row
   * @param col
   */
  public Tile(Type CONTINUATION) { type = Type.CONTINUATION; this.picture = null; } 
  /**
   * Create a new PICTURE tile, loading -1 for the row and column reference.
   * @param p
   */
  public Tile(Picture p)        { type = Type.PICTURE;      this.picture = p;    /* this.row = -1;  this.column = -1; */ }
  
  // access routines, traditonally would be called 'getX()'
  /**
   * Get the picture from this Tile.
   * @return
   */
  public Picture    picture()  { return picture; }
  /**
   * Get the Tile.Type of this tile.
   * @return
   */
  public Tile.Type  type()   { return type; }
//  /**
//   * Return the row referenced by this tile; if not a CONTINUATION tile, will be -1; a CONTINUATION
//   * tile will have the row and column of the upper-left corner tile (the 'index' tile) of the glyph
//   * for which this tile is a continuation.
//   * @return
//   */
//  public int        row()          { return row; }
//  /**
//   * Return the column referenced by this tile; if not a CONTINUATION tile, will be -1; a CONTINUATION
//   * tile will have the row and column of the upper-left corner tile (the 'index' tile) of the glyph
//   * for which this tile is a continuation.
//   * @return
//   */
//  public int        column()       { return column; }
//
//  /**
//   * Set the referenced row and column referenced by this (CONTINUATION) tile.
//   * @param row
//   * @param col
//   */
//  public void set(int row, int col) { type = Type.CONTINUATION; this.picture = null;  this.row = row; this.column = col; }
//  /**
//   * Set the Picture for this tile.
//   * @param row
//   * @param col
//   */
//  public void set(Picture pic)      { type = Type.PICTURE;      this.picture = pic;   this.row = -1;  this.column = -1; }

  /**
   * debugging convenience: return type of tile; if picture, name of picture file; if continuation, row and column of the
   * index tile for the supported image.
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder(type.toString());
    if (type == Type.PICTURE) { sb.append(":"); sb.append(picture.getImageName()); }
//    else if (type == Type.CONTINUATION) { sb.append(String.format(": r%d,c%d", row, column)); } 
    return new String(sb);
  }
  
  public boolean saveToFile(BufferedWriter saveFile) throws IOException
  {
    boolean result = true;
    saveFile.write(type.toString());
    if (type == Type.PICTURE) { saveFile.write(":");
                                saveFile.write(picture.getImageName());
                              }
    return result;
  }
  
  public static Tile getTileFromPictureKey(String pictureKey)
  {
    Picture picture = Picture.getPictureFromKey(pictureKey);
    Tile returnTile = new Tile(picture);
    return returnTile;
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
    // TODO: put tooltip here? option for with or without label, so we can use this for the map itself?
    return label;
  }
}
