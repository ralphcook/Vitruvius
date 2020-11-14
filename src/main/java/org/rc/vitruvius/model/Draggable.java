package org.rc.vitruvius.model;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public interface Draggable
{
  public JLabel getJLabel(int tileSize);
  public TileArray getTileArray();
  
  public ImageIcon getImageIcon(int tileSize);
  public String    getDisplayText();
}
