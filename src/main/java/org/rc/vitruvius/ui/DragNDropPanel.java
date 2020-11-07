package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class DragNDropPanel extends JPanel
{
  private static final long serialVersionUID = 1L;
  
  private static void say(String s) { System.out.println(s); }

  public DragNDropPanel()
  {
    JPanel currentImagePanel    = getCurrentImagePanel();
    JPanel glyphDropdownsPanel  = getGlyphDropdownsPanel();
    
    JPanel  leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
    leftPanel.add(currentImagePanel);
    leftPanel.add(createStandardSpace());
    leftPanel.add(glyphDropdownsPanel);
    
    setLayout(new BorderLayout());
    JPanel leftContainingPanel = new JPanel();
    leftContainingPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLACK, Color.WHITE));
    leftContainingPanel.add(leftPanel);
    add(leftContainingPanel, BorderLayout.WEST);
  }
  
  private JPanel getGlyphDropdownsPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    
//    String[] roadList = { "roads...", "Road", "Plaza" };
    JComboBox<Glyph> roadsDropdown = new JComboBox<>(getRoadList());
    JComboBox<Glyph> farmDropdown = new JComboBox<>(getFarmList());

    panel.add(roadsDropdown);
    panel.add(createHalfStandardSpace());
    panel.add(farmDropdown);
    
    return panel;
  }
  
  private Dimension getHalfStandardDimension() { return new Dimension(8,8); }
  private Dimension getStandardDimension() { return new Dimension(16,16); }
  private Component createStandardSpace()  { Dimension d = getStandardDimension(); return createSpace(d.width, d.height);  }
  private Component createHalfStandardSpace() { Dimension d = getHalfStandardDimension(); return createSpace(d.width, d.height); }
  private Component createSpace(int width, int height)  {    return Box.createRigidArea(new Dimension(width, height));  }

  private JPanel getCurrentImagePanel()
  {
    JPanel currentImagePanel = new JPanel();
    currentImagePanel.setLayout(new BoxLayout(currentImagePanel, BoxLayout.PAGE_AXIS));
    
    JLabel currentImageLabel = new JLabel(I18n.getString("CurrentGlyphLabelText"), createTransparentIcon(75,75), SwingConstants.CENTER);
    
    currentImageLabel.setBorder(BorderFactory.createDashedBorder(Color.BLUE));
    currentImageLabel.setHorizontalTextPosition(JLabel.CENTER);
    currentImageLabel.setVerticalTextPosition(JLabel.BOTTOM);
    currentImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    Dimension defaultLabelSize = new Dimension(100,100);
    currentImageLabel.setPreferredSize(defaultLabelSize);
    currentImageLabel.setMaximumSize(defaultLabelSize);
    
    JButton clearButton = new JButton();
    clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    clearButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    clearButton.setAction
    (
        new AbstractAction("Clear")
        {
          private static final long serialVersionUID = 1L;

          public void actionPerformed(ActionEvent event)
          {
            currentImageLabel.setIcon(createTransparentIcon(75,75));
            say("need to clear current image from where it's stored...");
          }
        }
    );
    
    currentImagePanel.add(currentImageLabel);
    currentImagePanel.add(createHalfStandardSpace()); 
    currentImagePanel.add(clearButton);
    
    currentImagePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    return currentImagePanel;
  }
  
  private Icon createTransparentIcon (final int width, final int height)
  {
    return new ImageIcon(createTransparentImage(width, height));
  }

  private BufferedImage createTransparentImage (final int width, final int height)
  {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }
  
  private Glyph[] getRoadList()
  {
    Glyph[] roadList = { new Glyph("roadFileName", "road", 1, 1)
                        ,new Glyph("plazaFileName", "plaza", 1, 1)
                       };
    return roadList;
  }
  
  private Glyph[] getFarmList()
  {
    Glyph[] farmList = { new Glyph("a", "wheat", 3, 3)
                        ,new Glyph("b", "fruit", 3, 3)
                        ,new Glyph("c", "vegs", 3, 3)
                        ,new Glyph("d", "pigs", 3, 3)
                        ,new Glyph("e", "olives", 3, 3)
                        ,new Glyph("f", "vines", 3, 3)
                       };
    return farmList;
  }
  
}
