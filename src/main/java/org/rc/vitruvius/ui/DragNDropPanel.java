 package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.Draggable;
import org.rc.vitruvius.model.DraggablePicture;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.VitruviusWorkingPane;
import org.rc.vitruvius.ui.actions.FileOpenAction;

import rcutil.file.ExtensionFileFilter;

/**
 * Panel for drag-n-drop Caesar III map planning, including ways to drag glyphs 
 * onto a grid.
 * @author rcook
 *
 */
public class DragNDropPanel extends JPanel implements VitruviusWorkingPane, GlyphSelectedListener
{
  public static void say(String s) { System.out.println(s); }
  public static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  private static final long serialVersionUID = 1L;

  private JLabel              currentPictureLabel     = null;   // label of current picture, displayed upper left.
  private String              defaultPictureText      = I18n.getString("currentDragComponentDefaultLabelText");
  private DragNDropImagesPane mapPane                 = null;
  private UserMessageListener userMessageListener     = null;
  private Preferences         applicationPreferences  = null;
  private MainFrame           mainFrame               = null;
  
  /**
   * Create the DragNDrop panel
   */
  public DragNDropPanel(MainFrame mainFrame, UserMessageListener userMessageListener, Preferences applicationPreferences)
  {
    this.mainFrame              = mainFrame;
    this.userMessageListener    = userMessageListener;
    this.applicationPreferences = applicationPreferences;
    
    JPanel leftPanel   = createLeftPanel(); 
           mapPane     = new DragNDropImagesPane(this, userMessageListener);
    
    setLayout(new BorderLayout());
    add(leftPanel, BorderLayout.WEST);
    add(mapPane, BorderLayout.CENTER);
    
    addKeyListener(new DragNDropKeyListener(mapPane));
    addGlyphSelectedListener(this);
  }
  
  /**
   * Create the panel that goes on the Left (i.e., WEST) side of the overall panel.
   * @return
   */
  private JPanel createLeftPanel()
  {
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
    innerPanel.add(createCurrentImagePanel());
    innerPanel.add(createStandardSpace());
    innerPanel.add(createPictureDropdownsPanel());
    
    JPanel leftPanel = new JPanel();        // we wrap the panel with the components so the components don't stretch.
    leftPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLACK, Color.WHITE));
    leftPanel.add(innerPanel);
    return leftPanel;
  }
  
  // ============================== (private) methods used to create the UI components ==================================== 
  
  /**
   * Create the panel containing the dropdown components for picking what structure to drag
   * onto the map.
   * @return
   */
  private JPanel createPictureDropdownsPanel()
  {
    JPanel panel = new JPanel();
    panel.setLayout(new NonUniformGridLayout(0,2,10,5));   // rows and columns for categories and picture dropdowns
    panel.setBorder(BorderFactory.createSoftBevelBorder(BevelBorder.RAISED));
    
    addDropdown(panel, "roads", Picture.road, Picture.plaza, Picture.garden);                                                     // 3
    addDropdown(panel, "houses", Picture.house1, Picture.house2, Picture.house3, Picture.palace);                                 // 4
    // TODO: add well and aqueducts to Picture
    addDropdown(panel, "water", Picture.reservoir, Picture.fountain);                                                             // 2
    addDropdown(panel, "farms", Picture.wheat, Picture.fruit, Picture.olives, Picture.vines, Picture.pigs, Picture.vegetables);   // 6
    // TODO: add boatyard; do we need a better category for these?
    addDropdown(panel, "food",  Picture.granary, Picture.market, Picture.wharf);                                                  // 3
    addDropdown(panel, "gods",  Picture.ceres,  Picture.mars,  Picture.mercury,  Picture.neptune,  Picture.venus,
        Picture.ceresL, Picture.marsL, Picture.mercuryL, Picture.neptuneL, Picture.venusL, Picture.oracle);                       //11
    addDropdown(panel, "entertainment", Picture.theater,  Picture.actorcolony, Picture.amphitheater, Picture.gladiator,
                                        Picture.coliseum, Picture.lionpit,     Picture.hippodroom,   Picture.chariotmaker);       // 8
    addDropdown(panel, "industry", Picture.clay, Picture.workshopP, Picture.iron,   Picture.workshopW,
                                   Picture.wood, Picture.workshopF, Picture.marble, Picture.workshopw, Picture.workshopO,
                                   Picture.warehouse);                                                                            //10
    addDropdown(panel, "services",  Picture.prefect,  Picture.engineer,  Picture.library,  Picture.school,  Picture.barber,    
                                    Picture.bath,     Picture.forum,     Picture.hospital, Picture.academy, Picture.doctor,
                                    Picture.statue1,  Picture.statue2,   Picture.statue3,  Picture.mission);                      //14
    addDropdown(panel, "military", Picture.fortG,   Picture.fortH,        Picture.gatehouseH, Picture.arcH, 
                                   Picture.barracks, Picture.MILacademy,  Picture.tower);                                         // 7
                                                                                                                                  //total 3+4+2+6+3+11+8+10+14+7 = 68
                                                                                                                                  //left out: gov-small, 
    
    return panel;
  }
  
  /**
   * Add (1) a JLabel with category text looked up with the given key, and (2) a dropdown containing the list of 
   * Picture objects, to the given panel. This method assumes the layout manager for the panel is set, and that
   * using the panel's <code>add()</code> method can be used, first for the label, then for the dropdown.
   * @param panel to which to add the two components 
   * @param categoryKey key in to the properties file to look up the category name for this locale 
   * @param pictures array of Picture objects to put in the dropdown.
   */
  private void addDropdown(Container panel, String categoryKey, Picture... pictures)
  {
    JLabel label = new JLabel(I18n.getString(categoryKey));
    JComboBox<Picture> cbox = createPictureComboBox(pictures);
    panel.add(label);
    panel.add(cbox);
  }
  
  /**
   * Create a JComboBox containing the given list of Pictures, and
   * add its action listener to it.
   * @param list
   * @return
   */
  private JComboBox<Picture> createPictureComboBox(Picture[] list)
  {
    JComboBox<Picture> cBox = new JComboBox<>();
    
    for (Picture p: list) { cBox.addItem(p); }
    
    cBox.addActionListener(new ActionListener()
                            {
                              public void actionPerformed(ActionEvent e)
                              {
                                Object o = e.getSource();
                                @SuppressWarnings("unchecked")
                                JComboBox<Picture> cBox = (JComboBox<Picture>)o;
                                Picture p = (Picture) cBox.getSelectedItem();
                                DraggablePicture draggablePicture = new DraggablePicture(p);
                                GlyphSelectedEvent gsEvent = new GlyphSelectedEvent(this, draggablePicture);
//                                setCurrentPicture(draggablePicture);
                                fireGlyphSelectedEvent(gsEvent);
                              }
                            }
                          );
    return cBox;
  }
  
  // Convenience methods for creating either a standard or a half standard space or
  // spacing component, where 'standard' spacing is just what we define for this panel.
  private Dimension getHalfStandardDimension() { return new Dimension(8,8); }
  private Dimension getStandardDimension() { return new Dimension(16,16); }
  private Component createStandardSpace()  { Dimension d = getStandardDimension(); return createSpace(d.width, d.height);  }
  private Component createHalfStandardSpace() { Dimension d = getHalfStandardDimension(); return createSpace(d.width, d.height); }
  private Component createSpace(int width, int height)  {    return Box.createRigidArea(new Dimension(width, height));  }

  /**
   * Create the panel to contain the 'current image', i.e., the one the user most recently selected in the dropdowns as the one he
   * is dragging onto the images panel.
   * @return
   */
  private JPanel createCurrentImagePanel()
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
         // Create the 'Clear' button for clearing the currently selected glyph from the panel.
        new AbstractAction("Clear")
        {
          private static final long serialVersionUID = 1L;

          public void actionPerformed(ActionEvent event)
          {
            currentPictureLabel.setIcon(createTransparentIcon(75,75));
            currentPictureLabel.setText(defaultPictureText);
            mapPane.deactivateDragging();
          }
        }
    );
    
    currentImagePanel.add(currentPictureLabel);
    currentImagePanel.add(createHalfStandardSpace()); 
    currentImagePanel.add(clearButton);
    
    currentImagePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    return currentImagePanel;
  }
  
  /**
   * Create a transparent icon.
   * @param width
   * @param height
   * @return
   */
  private Icon createTransparentIcon (final int width, final int height)
  {
    return new ImageIcon(createTransparentImage(width, height));
  }

  /**
   * Create a transparent image of the given width and height.
   * @param width
   * @param height
   * @return
   */
  private BufferedImage createTransparentImage (final int width, final int height)
  {
    return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  }

  // ================================= methods called by various actions ======================================
  
  public void glyphSelected(GlyphSelectedEvent gsEvent)
  {
    say("glyphSelectedEvent in DnDropPanel");
    Draggable draggable = gsEvent.getDraggable();
    setCurrentPicture(draggable);
  }
  
  /**
   * Set the given picture as the one that will be dragged onto the drag-n-drop panel.
   * @param draggablePicture
   */
  private void setCurrentPicture(Draggable draggablePicture)
  {
//    currentPictureLabel = draggablePicture.getJLabelJustIcon(25);
//    currentPictureLabel.setText(draggablePicture.getDisplayText());
    
    // note to self -- commented out code above gets a new label; I could use it,
    // but then would have to replace the label currently on the panel. Code below
    // sets attributes of the label currently on the panel.
    ImageIcon icon = draggablePicture.getImageIcon(25);     // TODO: figure out what we really want as sizing here.
    String    text = draggablePicture.getDisplayText();
    currentPictureLabel.setIcon(icon);
    currentPictureLabel.setText(text);                      
    currentPictureLabel.setToolTipText(draggablePicture.getDisplayText());
    
//    JLabel dragLabel = new JLabel(icon);
//    Dimension size = dragLabel.getPreferredSize();
////    say("dragLabel size %d, %d", size.width, size.height);
//    dragLabel.setSize(size);
//    mapPane.activateDragging(draggablePicture);
    repaint();
  }

  // ================================= VitruviusActions methods =============================================
  
  public static String        SAVED_TILE_FILE_DIRECTORY_KEY   = "savedOpenFileDirectory";
  public static String        SAVED_TILE_FILE_FILENAME_KEY    = "savedOpenFileFilename";
  public static String        SAVED_TILE_FILE_FILENAME_EXTENSION = "tiles";
  

  @Override
  public boolean openFile()
  {
    // TODO: consider a method (somewhere) that accepts necessary parameters for user to choose
    // a file to save to; parameters would include:
    // defaultPath
    // defaultFolderName
    // filter, or extension(s) for filter
    // dialog title
    // dialog button text
    // 
    String defaultPath = System.getProperty("user.home");
    String defaultFolderName = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
    JFileChooser chooser = new JFileChooser(defaultFolderName);
    
    ExtensionFileFilter saveFilter = new ExtensionFileFilter(SAVED_TILE_FILE_FILENAME_EXTENSION);
    chooser.setFileFilter(saveFilter);
    chooser.setDialogTitle(I18n.getString("fileOpenActionName"));
    
    String buttonText = I18n.getString("fileOpenDialogButtonText");
    
    int fileChooseReturn = chooser.showDialog(this, buttonText);    // TODO: check experiment of dialog centered on panel not JFrame
    if (fileChooseReturn == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = chooser.getSelectedFile();
      try 
      { 
        TileArray tileArray = TileArray.readFromFile(selectedFile);
        mapPane.setTileArray(tileArray); 
      }
      catch (Exception exception) 
      { 
        String message = I18n.getString("errorOpeningFile");
        String fullMessage = String.format("%s (%s)", message, exception.getMessage());
        userMessageListener.addMessage(fullMessage);
//        exception.printStackTrace(); 
      }
    }
    return true;      // TODO: if this is all we do, eliminate the return value.
  }

  @Override
  public void saveFile()
  {
    String resultMessage      = null;
    String defaultPath        = System.getProperty("user.home");
    String defaultFolderName  = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
    JFileChooser chooser = new JFileChooser(defaultFolderName);
    ExtensionFileFilter saveFilter = new ExtensionFileFilter(SAVED_TILE_FILE_FILENAME_EXTENSION);
    chooser.setFileFilter(saveFilter);
    chooser.setDialogTitle(I18n.getString("fileSaveActionName"));

    String buttonText = I18n.getString("fileSaveDialogButtonText");
    
    int fileChooseReturn = chooser.showDialog(this, buttonText);    // TODO: check experiment of opening dialog above this panel
    if (fileChooseReturn == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = chooser.getSelectedFile();
      try 
      { 
        TileArray tileArray = mapPane.getTileArray();
        tileArray.saveToFile(selectedFile);
        // now that we've saved it, save the filename and directory for use later.
        String filename = selectedFile.getName();
        String filepath = selectedFile.getCanonicalPath();
        applicationPreferences.put(SAVED_TILE_FILE_FILENAME_KEY, filename);
        applicationPreferences.put(SAVED_TILE_FILE_DIRECTORY_KEY, filepath);
        
        // and let the user know that it's been saved
        resultMessage = I18n.getString("tileFileSavedMessage");
      }
      catch (Exception exception) 
      { 
        resultMessage = I18n.getString("tileFileCouldNotBeSaved");
        exception.printStackTrace(); 
      }
    }
    userMessageListener.addMessage(resultMessage);
  }

  @Override
  public void clearPanel()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setPanelSize()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void decreaseTileSize()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void increaseTileSize()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void generateFullHTML()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void generateForumHTML()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void displayHelp()
  {
    // TODO Auto-generated method stub
    
  }
  
  // =================================== 'glyph selected' event ====================================
  
  HashSet<GlyphSelectedListener> glyphSelectedListeners = new HashSet<>();
  
  public void addGlyphSelectedListener(GlyphSelectedListener listener)    { glyphSelectedListeners.add(listener);     }
  public void removeGlyphSelectedListener(GlyphSelectedListener listener) { glyphSelectedListeners.remove(listener);  }
  public void fireGlyphSelectedEvent(GlyphSelectedEvent gsEvent)          
  { 
    say("Firing GlyphSelected, %d listeners", glyphSelectedListeners.size());
    for (GlyphSelectedListener listener: glyphSelectedListeners) 
    { 
      listener.glyphSelected(gsEvent);  
      say("listener notified");
    }  
  }
}
