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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.rc.vitruvius.UserMessageListener;
import org.rc.vitruvius.model.Draggable;
import org.rc.vitruvius.model.DraggablePicture;
import org.rc.vitruvius.model.FileHandler;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.VitruviusWorkingPane;

/**
 * Panel for drag-n-drop Caesar III map planning, including ways to drag glyphs 
 * onto a grid.
 * @author rcook
 *
 */
public class DragNDropPanel extends JPanel implements VitruviusWorkingPane, GlyphSelectionListener
{
  public static void say(String s) { System.out.println(s); }
  public static void say(String format, Object... args) { System.out.println(String.format(format, args)); }
  private static final long serialVersionUID = 1L;
  
//  private MainFrame           mainFrame               = null;
  private UserMessageListener userMessageListener     = null;

  private JLabel              currentPictureLabel     = null;   // label of current picture, displayed upper left.
  private Icon                currentPictureLabelDefaultIcon = null;
  private String              currentPictureLabelDefaultText = I18n.getString("currentDragComponentDefaultLabelText");
  private DragNDropImagesPane mapPane                 = null;
  private Preferences         applicationPreferences  = null;
  
  private FileHandler         fileHandler             = null;
  private File                currentlyOpenFile       = null;
  
  /**
   * Create the DragNDrop panel
   */
  public DragNDropPanel(MainFrame mainFrame, UserMessageListener userMessageListener, Preferences applicationPreferences)
  {
    this.userMessageListener    = userMessageListener;
    this.applicationPreferences = applicationPreferences;
    
    this.fileHandler = new FileHandler(mainFrame);
    
    JPanel leftPanel   = createLeftPanel(); 
           mapPane     = new DragNDropImagesPane(this, userMessageListener);
    
    JScrollPane leftScrollPane = new JScrollPane(leftPanel);
    setLayout(new BorderLayout());
    add(leftScrollPane, BorderLayout.WEST);
    add(mapPane, BorderLayout.CENTER);
    
    addKeyListener(new DragNDropKeyListener(mapPane));
    addGlyphSelectionListener(this);
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
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//    panel.getInsets().set(3, 3, 3, 15);   // didn't work, p'bly because of borderlayout
    Border bevelBorder = BorderFactory.createSoftBevelBorder(BevelBorder.RAISED);
    Border emptyBorder = BorderFactory.createEmptyBorder(3, 3, 3, 15);
    Border compoundBorder = new CompoundBorder(emptyBorder, bevelBorder);
    panel.setBorder(compoundBorder);
    
    addDropdown(panel, "roads", Picture.road, Picture.plaza, Picture.garden);                                                     // 3
    addDropdown(panel, "houses", Picture.house1, Picture.house2, Picture.house3, Picture.palace);                                 // 4
    addDropdown(panel, "water", Picture.reservoir, Picture.fountain);                                                             // 2
    addDropdown(panel, "farms", Picture.wheat, Picture.fruit, Picture.olives, Picture.vines, Picture.pigs, Picture.vegetables);   // 6
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
    String categoryName = I18n.getString(categoryKey);
//    JLabel label = new JLabel(categoryName);
    JComboBox<Object> cbox = createPictureComboBox(categoryName, pictures);
//    panel.add(label);
    panel.add(cbox);
  }
  
  /**
   * Create a JComboBox containing the given list of Pictures, and
   * add its action listener to it.
   * @param list
   * @return
   */
  private JComboBox<Object> createPictureComboBox(String label, Picture[] list)
  {
    JComboBox<Object> cBox = new JComboBox<>();
    cBox.addItem(label);
    for (Picture p: list) { cBox.addItem(p); }
    
    cBox.addActionListener(new ActionListener()
                            {
                              public void actionPerformed(ActionEvent e)
                              {
                                Object selectedItem = cBox.getSelectedItem();
                                if (selectedItem instanceof Picture)
                                {
                                  @SuppressWarnings("unchecked")
                                  Picture selectedPicture = (Picture) selectedItem;
                                  DraggablePicture draggablePicture = new DraggablePicture(selectedPicture);
                                  GlyphSelectionEvent gsEvent = new GlyphSelectionEvent(this, "select", draggablePicture);
                                  fireGlyphSelectionEvent(gsEvent);
                                  cBox.setSelectedIndex(0);
                                }
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
    
    currentPictureLabelDefaultIcon = createTransparentIcon(75,75);    // TODO: make this the correct size, jfthoi
    currentPictureLabel = new JLabel(currentPictureLabelDefaultText, currentPictureLabelDefaultIcon, SwingConstants.CENTER);
    
    currentPictureLabel.setBorder(BorderFactory.createDashedBorder(Color.BLUE));
    currentPictureLabel.setHorizontalTextPosition(JLabel.CENTER);
    currentPictureLabel.setVerticalTextPosition(JLabel.BOTTOM);
    currentPictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    Dimension defaultLabelSize = new Dimension(100,100);    // TODO: what is this size supposed to be?
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
            GlyphSelectionEvent gsEvent = new GlyphSelectionEvent(this, "deselect", null);
            fireGlyphSelectionEvent(gsEvent);
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
  
  public void glyphSelection(GlyphSelectionEvent gsEvent)
  {
    Draggable draggable = null;
    if (gsEvent.getAction().equals("select"))
    {
      draggable = gsEvent.getDraggable();
    }
    setCurrentPicture(draggable);
  }
  
  /**
   * Set the given picture as the one that will be dragged onto the drag-n-drop panel.
   * @param draggablePicture
   */
  private void setCurrentPicture(Draggable draggablePicture)
  {
    if (draggablePicture == null)
    {
      currentPictureLabel.setIcon(currentPictureLabelDefaultIcon);
      currentPictureLabel.setText(currentPictureLabelDefaultText);
      currentPictureLabel.setToolTipText("");
    }
    else
    {
      ImageIcon icon = draggablePicture.getImageIcon(25);     // TODO: figure out what we really want as sizing here.
      String    text = draggablePicture.getDisplayText();
      currentPictureLabel.setIcon(icon);
      currentPictureLabel.setText(text);                      
      currentPictureLabel.setToolTipText(draggablePicture.getDisplayText());
    }
    repaint();
  }
  
  public boolean unsavedChanges() { return mapPane.unsavedChanges(); }

  // ================================= VitruviusActions methods =============================================
  
  public static String        SAVED_TILE_FILE_DIRECTORY_KEY   = "savedOpenFileDirectory";
  public static String        SAVED_TILE_FILE_FILENAME_KEY    = "savedOpenFileFilename";
  public static String        SAVED_TILE_FILE_FILENAME_EXTENSION = "tiles";
  
  @Override public String getDisplayName() { return I18n.getString("dragNDropPaneName"); }
  
  @Override
  public void openFile()
  {
    // if there are unsaved changes, ask about wiping them.
    int option = JOptionPane.YES_OPTION;
    if (unsavedChanges())
    {
      String message = I18n.getString("unsavedChangesOnePanelMessage", getDisplayName());
      String title   = I18n.getString("unsavedChangesDialogTitle");
      option = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }
    if (option == JOptionPane.YES_OPTION)
    {
      String defaultPath = System.getProperty("user.home");
      String defaultFolderName = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
      String defaultExtension = SAVED_TILE_FILE_FILENAME_EXTENSION;
      
      String dialogTitle = I18n.getString("fileOpenActionName");
      String buttonText = I18n.getString("fileOpenDialogButtonText");
      
      FileHandler.FileChoice fileChoice = fileHandler.getFileToOpen(this, defaultFolderName, defaultExtension, dialogTitle, buttonText);
      String message = null;
      String filename = null;
      if (!fileChoice.cancelled())
      {
        if (!fileChoice.succeeded())
        {
          userMessageListener.addMessage(fileChoice.getMessage());
        } else
        {
          try
          {
            File file = fileChoice.getFile();
            filename = file.getCanonicalPath();
            TileArray tileArray = TileArray.readFromFile(file);
            mapPane.setTileArray(tileArray);
            saveFileInPreferences(file);
            currentlyOpenFile = file;
          } catch (Exception e)
          {
            message = I18n.getString("errorOpeningFile", filename, e.getMessage());
            userMessageListener.addMessage(message);
          }
        } // end of if
      }
    }
  }     // end of openFile()
    
  
  /**
   * Save this map to the file it came from; if there is no such map file, then
   * convert to "save as" so the user can pick one.
   */
  public void saveFile()
  {
    if (currentlyOpenFile == null)
    {
      saveFileAs();
    }
    else
    {
      String filepath = "";
      try 
      { 
        filepath = currentlyOpenFile.getCanonicalPath();
        saveCurrentTileArray(currentlyOpenFile); 
      }
      catch (Exception e)
      {
        String message = I18n.getString("fileCouldNotBeSaved", filepath);
        userMessageListener.addMessage(message);
      }
    }
  }

  /**
   * Save the current map to a file, including a file dialog allowing the user to choose
   * the filename to save to.
   */
  @Override
  public void saveFileAs()
  {
    String resultMessage      = null;
    String defaultPath        = System.getProperty("user.home");
    String defaultFolderName  = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
    
    FileHandler.FileChoice fileChoice = fileHandler.getFileToSave(this,  
                                                                  defaultFolderName, 
                                                                  SAVED_TILE_FILE_FILENAME_EXTENSION, 
                                                                  I18n.getString("fileSaveActionName"), 
                                                                  I18n.getString("fileSaveDialogButtonText")
                                                                 );

    if (!fileChoice.cancelled())
    {
      if (!fileChoice.succeeded())
      {
        resultMessage = fileChoice.getMessage();
      }
      else 
      {
        File selectedFile = fileChoice.getFile();
        String filepath = "";
        try 
        { 
          saveCurrentTileArray(selectedFile);
          saveFileInPreferences(selectedFile);
          resultMessage = I18n.getString("fileSavedMessage", filepath);
        }
        catch (Exception exception) 
        { 
          resultMessage = I18n.getString("fileCouldNotBeSaved", filepath);
        }
      }
      userMessageListener.addMessage(resultMessage);
    }
    
  }
  
  private void saveFileInPreferences(File selectedFile) throws Exception
  {
    // now that we've saved it, save the filename and directory for use later.
    String filename = selectedFile.getName();
    String filepath = selectedFile.getCanonicalPath();
    applicationPreferences.put(SAVED_TILE_FILE_FILENAME_KEY, filename);
    applicationPreferences.put(SAVED_TILE_FILE_DIRECTORY_KEY, filepath);
  }
  
  private void saveCurrentTileArray(File saveFile) throws Exception
  {
    TileArray tileArray = mapPane.getTileArray();
    tileArray.saveToFile(saveFile);
    mapPane.setUnsavedChanges(false);
    currentlyOpenFile = saveFile;
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
  
  // =================================== 'glyph selection' event ====================================
  
  HashSet<GlyphSelectionListener> glyphSelectionListeners = new HashSet<>();
  
  public void addGlyphSelectionListener(GlyphSelectionListener listener)    { glyphSelectionListeners.add(listener);     }
  public void removeGlyphSelectionListener(GlyphSelectionListener listener) { glyphSelectionListeners.remove(listener);  }
  public void fireGlyphSelectionEvent(GlyphSelectionEvent gsEvent)          
  { 
    for (GlyphSelectionListener listener: glyphSelectionListeners) 
    { 
      listener.glyphSelection(gsEvent);  
    }  
  }
}
