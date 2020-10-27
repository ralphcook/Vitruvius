package org.example;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.rc.vitruvius.ui.Picture;

public class MinSize extends JFrame
{
  private static final long serialVersionUID = 1L;

  public MinSize()
  {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    
    ImagesPanel imagesPanel = new ImagesPanel();
    add(imagesPanel);

    pack();
  }

  public static void main(String ... args)
  {
    MinSize frame = new MinSize();
    frame.setVisible(true);
  }
}

class ImagesPanel extends JPanel
{
  private static final long serialVersionUID = 1L;

  public ImagesPanel()
  {
    Dimension dimension = new Dimension(150,150);
    setMinimumSize(dimension);
    setPreferredSize(dimension);
    setSize(dimension);
  }

  public void paintComponent(Graphics realGraphics)
  {
    super.paintComponent(realGraphics);
    Picture p = Picture.house2;
    
    Image image = p.getImage();
    realGraphics.drawImage(image, 25, 25, 50, 50, this);
  }
  
}
