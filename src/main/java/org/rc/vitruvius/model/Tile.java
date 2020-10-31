package org.rc.vitruvius.model;

import org.rc.vitruvius.ui.Picture;

/**
 * One tile on the display grid; might have a picture, part of a picture, or nothing.
 * @author ralph
 *
 */
public class Tile
{
  private Picture picture = null;
  
  public Tile() {}
  public Tile(Picture p) { picture = p; }
  public boolean hasPicture() { return picture != null; }
  public Picture getPicture() { return picture; }
}
