package org.rc.vitruvius.model;

import java.util.HashMap;

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
  
  @SuppressWarnings("unused")
  private DraggablePicture() {}
  
  public DraggablePicture(Picture picture)
  {
    if (picture == null) { throw new IllegalArgumentException("Cannot create DraggablePicture with null Picture"); }
    this.picture = picture;
  }
  
  public ImageIcon getImageIcon(int tileSize) { return picture.getImageIcon(tileSize); }
  public String    getDisplayText()           { return picture.getDisplayText();       }
  
  /**
   * Get the (possibly cached) JLabel for the given tile size.
   */
  @Override
  public JLabel getJLabel(int tileSize)
  {
    JLabel jLabel = jLabelMap.get(tileSize);
    if (jLabel == null)
    {
      jLabel = new JLabel();
      ImageIcon icon = picture.getImageIcon(tileSize);
      String    text = picture.getDisplayText();
      jLabel.setIcon(icon);
      jLabel.setText(text);
      jLabel.setToolTipText(picture.getDisplayText());
      jLabel.setSize(jLabel.getPreferredSize());
      jLabelMap.put(tileSize, jLabel);
    }
    return jLabel;
  }

  @Override
  public TileArray getTileArray()
  {
    return picture.getTileArray();
  }
}
