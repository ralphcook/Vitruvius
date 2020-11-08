package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

  Picture  currentPicture       = null;
  JLabel currentPictureLabel    = null;
  String defaultPictureText     = I18n.getString("CurrentPictureDefaultLabelText");
  
  @SuppressWarnings("unused")
  private static void say(String s) { System.out.println(s); }

  public DragNDropPanel()
  {
    JPanel currentImagePanel      = getCurrentImagePanel();
    JPanel pictureDropdownsPanel  = getPictureDropdownsPanel();
    
    JPanel  leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
    leftPanel.add(currentImagePanel);
    leftPanel.add(createStandardSpace());
    leftPanel.add(pictureDropdownsPanel);
    
    setLayout(new BorderLayout());
    JPanel leftContainingPanel = new JPanel();
    leftContainingPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLACK, Color.WHITE));
    leftContainingPanel.add(leftPanel);
    add(leftContainingPanel, BorderLayout.WEST);
  }
  
  private JPanel getPictureDropdownsPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    
//    String[] roadList = { "roads...", "Road", "Plaza" };
//    JComboBox<Picture> roadsDropdown = new JComboBox<>(getRoadList());
//    JComboBox<Picture> farmDropdown = new JComboBox<>(getFarmList());

    JComboBox<Picture> roadsDropdown = createPictureComboBox("Roads", getRoadList());
    panel.add(roadsDropdown);
    panel.add(createHalfStandardSpace());
    JComboBox<Picture> farmDropdown = createPictureComboBox("Farms", getFarmList());
    panel.add(farmDropdown);
    
    return panel;
  }
  
  private JComboBox<Picture> createPictureComboBox(String categoryName, Picture[] list)
  {
    JComboBox<Picture> cBox = new JComboBox<>();
    
//    Picture categoryPicture = new Picture(null, categoryName, 0, 0);
//    cBox.addItem(categoryPicture);
    
    for (Picture p: list) { cBox.addItem(p); }
    
    cBox.addActionListener(new ActionListener()
                            {
                              public void actionPerformed(ActionEvent e)
                              {
                                Object o = e.getSource();
                                JComboBox<Picture> cBox = (JComboBox<Picture>)o;
                                Picture p = (Picture) cBox.getSelectedItem();
                                System.out.println("picture name: " + p.getDisplayText());
                                setCurrentPicture(p);
                              }
                            }
                          );
    return cBox;
  }
  
  private void setCurrentPicture(Picture picture)
  {
    ImageIcon icon = picture.getImageIcon();
    String    text = picture.getDisplayText();
    currentPictureLabel.setIcon(icon);
    currentPictureLabel.setText(text);
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
    
    currentPictureLabel = new JLabel(defaultPictureText, createTransparentIcon(75,75), SwingConstants.CENTER);
    
    currentPictureLabel.setBorder(BorderFactory.createDashedBorder(Color.BLUE));
    currentPictureLabel.setHorizontalTextPosition(JLabel.CENTER);
    currentPictureLabel.setVerticalTextPosition(JLabel.BOTTOM);
    currentPictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    Dimension defaultLabelSize = new Dimension(100,100);
    currentPictureLabel.setPreferredSize(defaultLabelSize);
    currentPictureLabel.setMaximumSize(defaultLabelSize);
    
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
            currentPictureLabel.setIcon(createTransparentIcon(75,75));
            currentPictureLabel.setText(defaultPictureText);
            currentPicture = null;
          }
        }
    );
    
    currentImagePanel.add(currentPictureLabel);
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
  
  private Picture[] getRoadList()
  {
    Picture[] roadList = {    Picture.road
                            , Picture.plaza
                       };
    return roadList;
  }
  
  private Picture[] getFarmList()
  {
    Picture[] farmList = {  Picture.wheat
                          , Picture.fruit
                          , Picture.vegetables
                          , Picture.pigs
                          , Picture.olives
                          , Picture.vines
                        };
    return farmList;
  }
  
}
