package org.rc.vitruvius.model;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * A Draggable object is one that is dragged on the DragNDropImagesPane; it was
 * created so that later we might be able to drag something besides a JLabel,
 * without stopping to design now just what that would be. Later on I determined
 * that it would be possible to create an image out of several Picture images,
 * and so a JLabel could be built with an image of more than one Picture. 
 * 
 * @author rcook
 *
 */
public interface Draggable
{
  /**
   * Return a JLabel for this Draggable that has no label text, but just 
   * its Icon, adjusted for the given tile size. It can/should have tooltip 
   * text, so that, if it is placed on the panel, the tooltip text is 
   * available to the user.
   *  
   * @param tileSize
   * @return
   */
  public JLabel     getJLabelJustIcon(int tileSize);
  
  /**
   * Get the tile array corresponding to this Draggable.
   * @return
   */
  public TileArray  getTileArray();
  
  /**
   * Get the image icon for this draggable, adjusted to the given tile size.
   * @param tileSize
   * @return
   */
  public ImageIcon  getImageIcon(int tileSize);
  
  /**
   * Get the display text for this draggable.
   * @return
   */
  public String     getDisplayText();
  
  /**
   * Return true if, after dropping this draggable, it remains draggable
   * so that it can be dropped again. Otherwise, after dropping, it should
   * cease to be the draggable item.
   */
  public boolean isPersistent();
  
  /**
   * Return true if we allow this glyph to be duplicated by dragging it across
   * the screen.
   */
  public boolean isMultidrop();
}
