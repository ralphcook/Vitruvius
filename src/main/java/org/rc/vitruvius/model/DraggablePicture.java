package org.rc.vitruvius.model;

import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.rc.vitruvius.ui.Picture;

/** 
 * This implementation of Draggable starts with a Picture object.
 * 
 * @see org.rc.vitruvius.model.Draggable;
 * 
 * @author rcook
 *
 */
public class DraggablePicture implements Draggable
{
  private Picture                       picture     = null;
  private HashMap<Integer, JLabel>      jLabelMap   = new HashMap<>();    // indexed by tileSize
  private boolean                       persistent  = true;
  
  @SuppressWarnings("unused")
  private DraggablePicture() {}
  
  /**
   * Create a Draggable object from the given Picture.
   * @param picture
   */
  public DraggablePicture(Picture picture)
  {
    if (picture == null) { throw new IllegalArgumentException("Cannot create DraggablePicture with null Picture"); }
    this.picture = picture;
  }
  
  /**
   * Create a Draggable object from the given picture; if persistent, then after
   * placement, the draggable will remain available for another placement. Otherwise,
   * after placing the draggable on the screen, it is no longer available and disappears
   * from dragging.
   * @param picture
   * @param persistent
   */
  public DraggablePicture(Picture picture, boolean persistent)
  {
    this(picture);
    this.persistent = persistent;
  }
  
  public ImageIcon getImageIcon(int tileSize) { return picture.getImageIcon(tileSize); }
  public String    getDisplayText()           { return picture.getDisplayText();       }
  public boolean   isPersistent()             { return persistent;                     }
  
  /**
   * Get the (possibly cached) JLabel for the given tile size.
   * <P>Experimenting: this method returns a JLabel without text,
   * sized to the size of the icon alone. I'm adding a method to
   * get a JLabel *with* text; that will be sized to the JLabel's
   * preferred size, and won't be cached.
   */
  @Override
  public JLabel getJLabelJustIcon(int tileSize)
  {
    JLabel jLabel = jLabelMap.get(tileSize);
    if (jLabel == null)
    {
      jLabel = getBasicJLabel(tileSize);
      jLabel.setText(null);
      Icon icon = jLabel.getIcon();
      Dimension iconSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
      jLabel.setSize(iconSize);
      jLabelMap.put(tileSize, jLabel);
    }
    return jLabel;
  }
  
  public JLabel getJLabelWithText(int tileSize)
  {
    JLabel jLabel = getBasicJLabel(tileSize);
    String    text = picture.getDisplayText();
    jLabel.setText(text);
    jLabel.setSize(jLabel.getPreferredSize());
    return jLabel;
  }
  
  private JLabel getBasicJLabel(int tileSize)
  {
    JLabel jLabel = new JLabel();
    ImageIcon icon = picture.getImageIcon(tileSize);
    jLabel.setIcon(icon);
    jLabel.setToolTipText(picture.getDisplayText());
    return jLabel;
  }

  @Override
  public TileArray getTileArray()
  {
    return picture.getTileArray();
  }
  
  public boolean isMultidrop()
  {
    return (Picture.isMultiDrop(picture));
  }
}
