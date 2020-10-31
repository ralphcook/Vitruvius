package org.rc.vitruvius.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class ImagesPanel extends JPanel implements MouseListener, MouseMotionListener
{
  private static final long serialVersionUID = 1L;
  
  int         tileSize = -1;      // # of pixels per tile edge
  Picture[][] pictures = null;    // 2d array of pictures on display
  public Picture[][] getPictures() { return pictures; }
  
  public ImagesPanel()
  {
    Dimension dimension = new Dimension(150,150);
    setPreferredSize(dimension);
  }
  
  public ImagesPanel(int tileSize)
  {
    this();
    this.tileSize = tileSize;
  }
  
  /**
   * create this panel with given width and height number of tiles, with each tile of tileSize.
   * @param width number of tiles wide
   * @param height number of tiles high
   * @param tileSize number of pixels per tile square.
   * <P>N.B. unused except in 'public ImagesPanel(int, Picture[][])' constructor (30-Oct-20)
   */
  public ImagesPanel(int width, int height, int tileSize)
  {
    this.tileSize = tileSize;
    setSize(tileSize*width, tileSize*height);
  }
  
  /**
   * Create the panel to hold the given pictures array with the given tileSize.
   * TODO: reuse the existing panel instead of recreating the old one. I don't know
   * how it's getting into the component hierarchy...
   * ANSWER: this is not getting used (30-Oct-20)
   * @param tileSize number of pixels for the edge of each tile square
   * @param pictures 2D array of picture objects to be displayed.
   */
  public ImagesPanel(int tileSize, Picture[][] pictures)
  {
    this(pictures[0].length, pictures.length, tileSize);
    this.pictures = pictures;
    
    int columns = pictures[0].length;
    int rows    = pictures.length;
    
    Dimension panelSize = new Dimension(columns * tileSize, rows * tileSize);
    setPreferredSize(panelSize);
    setSize(panelSize);
  }
  
  private void setSize(Picture[][] pictures)
  {
    int width = pictures[0].length;
    int height = pictures.length;
    setSize(width*tileSize, height*tileSize);
    setPreferredSize(new Dimension(width*tileSize, height*tileSize));
  }
  
  public void setPictures(Picture[][] pictures)
  {
    this.pictures = pictures;
    setSize(pictures);
  }

  public void paintComponent(Graphics graphics)
  {
    super.paintComponent(graphics);
    
    int rowNumber = 0;
    if (pictures != null) {
      for (Picture[] row : pictures) {
        int colNumber = 0;
        for (Picture p : row) {
          if (p == null)
          {
            System.out.println("ImagesPanel: Some picture is null, don't know how");
          }
          if (p != Picture.OCCUPIED && p != Picture.SPACE) 
          {
            int x = colNumber * tileSize;
            int y = rowNumber * tileSize;
            int width = tileSize * p.columns();
            int height = tileSize * p.rows();
            graphics.drawImage(p.getImage(), x, y, width, height, this);
          }
          colNumber++;
        }
        rowNumber++;
      } 
    }
  }

  @Override  public void mouseDragged(MouseEvent e)  {      }
  @Override  public void mouseMoved(MouseEvent e)  {      }
  
  @Override  public void mouseClicked(MouseEvent e)  
  {      
    int x = e.getX();
    int y = e.getY();
    
  }
  @Override  public void mousePressed(MouseEvent e)  {      }
  @Override  public void mouseReleased(MouseEvent e)  {      }
  @Override  public void mouseEntered(MouseEvent e)  {      }
  @Override  public void mouseExited(MouseEvent e)  {      }

}
