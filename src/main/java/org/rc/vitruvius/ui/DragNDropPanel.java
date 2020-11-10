package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

public class DragNDropPanel extends JPanel
{
  private static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  private static final long serialVersionUID = 1L;

  private Picture  currentPicture         = null;
  private JLabel   currentPictureLabel    = null;
  private String   defaultPictureText     = I18n.getString("CurrentDragComponentDefaultLabelText");
  private GlassPaneWrapper mapPane        = null;
  
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
    add(getGlassPane());
  }
  
  private JPanel getPictureDropdownsPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new NonUniformGridLayout(0,2,10,5));   // rows and columns for categories and picture dropdowns
    panel.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
    
    addDropdown(panel, "roads", Picture.road, Picture.plaza, Picture.garden);
    addDropdown(panel, "houses", Picture.house1, Picture.house2, Picture.house3, Picture.palace);
    // TODO: add well and aqueducts to Picture
    addDropdown(panel, "water", Picture.reservoir, Picture.fountain);
    addDropdown(panel, "farms", Picture.wheat, Picture.olives, Picture.vines, Picture.pigs, Picture.vegetables);
    // TODO: add boatyard; do we need a better category for these?
    addDropdown(panel, "food",  Picture.granary, Picture.market, Picture.wharf);
    addDropdown(panel, "gods",  Picture.ceres, Picture.mars, Picture.mercury, Picture.neptune, Picture.venus,
                                    Picture.ceresL, Picture.marsL, Picture.mercuryL, Picture.neptuneL, Picture.venusL, Picture.oracle);
    addDropdown(panel, "entertainment", Picture.theater, Picture.actorcolony, Picture.amphitheater, Picture.gladiator,
                                            Picture.coliseum, Picture.lionpit, Picture.hippodroom, Picture.chariotmaker);
    addDropdown(panel, "industry", Picture.clay, Picture.workshopP, Picture.iron, Picture.workshopW,
                                        Picture.wood, Picture.workshopF, Picture.marble, Picture.workshopw, Picture.workshopO);
    addDropdown(panel, "services", Picture.library, Picture.school, Picture.barber, Picture.doctor, 
                            Picture.bath,    Picture.forum,  Picture.hospital, Picture.prefect, 
                            Picture.statue1, Picture.statue2, Picture.statue3);
    addDropdown(panel, "military", Picture.fortG, Picture.fortH, Picture.gatehouseH, Picture.arcH, Picture.barracks, Picture.MILacademy);
    
    return panel;
  }
  
  private void addDropdown(Container panel, String categoryKey, Picture... pictures)
  {
    JLabel label = new JLabel(I18n.getString(categoryKey));
    JComboBox<Picture> cbox = createPictureComboBox(pictures);
    panel.add(label);
    panel.add(cbox);
  }
  
  private GlassPaneWrapper getGlassPane()
  {
    JPanel mapPanel = new JPanel();
    mapPanel.setLayout(null);
    // TODO: figure out how to size this thing.
    Dimension mapSize = new Dimension(400,400);
    mapPanel.setSize(mapSize);
    mapPanel.setPreferredSize(mapSize);
    mapPanel.setMaximumSize(mapSize);
    mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    mapPane = new GlassPaneWrapper(mapPanel);
    return mapPane;
  }
  
  private JComboBox<Picture> createPictureComboBox(Picture[] list)
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
    say("setting current picture, edt=" + SwingUtilities.isEventDispatchThread());
    ImageIcon icon = picture.getImageIcon(25);
    String    text = picture.getDisplayText();
    currentPictureLabel.setIcon(icon);
    currentPictureLabel.setText(text);
    currentPictureLabel.setToolTipText(picture.getDisplayText());
    JLabel dragLabel = new JLabel(icon);
    Dimension size = dragLabel.getPreferredSize();
//    say("dragLabel size %d, %d", size.width, size.height);
    dragLabel.setSize(size);
    mapPane.activateDragging(dragLabel);
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
            mapPane.deactivateDragging();
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
