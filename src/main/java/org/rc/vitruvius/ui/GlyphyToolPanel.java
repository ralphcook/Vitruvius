package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.rc.vitruvius.model.FileHandler;
import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.VitruviusWorkingPane;
import org.rc.vitruvius.ui.actions.DisplayGlyphyHelpAction;
import org.rc.vitruvius.ui.actions.GenerateGlyphyImageAction;
import org.rc.vitruvius.ui.actions.GenerateHtmlAction;

/**
 * This panel supports the entry of a block of characters representing glyphs
 * which, in turn, represent Caesar III game structures (buildings, roads, etc.).
 * This functionality was conceived by someone else, and implemented on the
 * Heavengames.com website as the "glyphy tool". 
 * 
 * @author rcook
 *
 */
public class GlyphyToolPanel extends JPanel implements DocumentListener, VitruviusWorkingPane
{
  private static final long serialVersionUID = 1L;
  
  public static void say(String s) { System.out.println(s); }

  private MainFrame   mainFrame               = null;
  private Preferences applicationPreferences  = null;
  
  private FileHandler fileHandler             = null;
  
  private JTextArea   textMapTextArea         = null;   // contains text representing a portion of a map
  private MapPanel    mapPanel                = null;   // contains images representing a portion of a map
  private JButton     generateImageButton     = null;   // generates the image on the images panel;
  
  private boolean dirtyText = false;
  public boolean  textDirty()             { return dirtyText; }
  public void     setDirtyText(boolean b) 
  { 
    dirtyText = b; 
    if (b) { setUnsavedChanges(true); } 
    GenerateGlyphyImageAction.getSingleton().setEnabled(b);
  } 
  
  private boolean unsavedChanges = false;
  public boolean unsavedChanges()             { return unsavedChanges; } 
  public void    setUnsavedChanges(boolean b) { unsavedChanges = b; }
  
  private File          currentlyOpenFile   = null;
  
  public GlyphyToolPanel(MainFrame mainFrame, Preferences applicationPreferences)
  {
    this.mainFrame = mainFrame;
    this.applicationPreferences = applicationPreferences;
    fileHandler = new FileHandler(mainFrame);
    
    setLayout(new BorderLayout());
    
    // Create a text area where the user can put text to convert to a map picture
    textMapTextArea = new JTextArea(5,20);
    Font font = new Font("Courier", Font.PLAIN, 18); // new Font(Font.MONOSPACED, 20, Font.PLAIN);
    textMapTextArea.setFont(font);
    textMapTextArea.getDocument().addDocumentListener(this);
    JScrollPane textMapScrollPane = new JScrollPane(textMapTextArea);
    
    // create a panel where we'll display a resulting set of map images
    mapPanel = new MapPanel(25);
    JScrollPane imagesScrollPane = new JScrollPane(mapPanel);
    
    // the text area and the images panel go in a split pane so the user
    // can adjust their sizes.
    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textMapScrollPane, imagesScrollPane);
    mainSplitPane.setDividerLocation(250);
    
    // create a panel for buttons to control the program operation,
    // and put a couple of buttons in it.
    JPanel outerButtonsPanel = new JPanel();
    outerButtonsPanel.setLayout(new BorderLayout());
    JPanel buttonsPanel = new JPanel();
    GridLayout gridLayout = new GridLayout(4, 1);   // GridLayout makes the buttons the same size, 
                                                    // another panel using BorderLayout.NORTH makes 
                                                    // them their preferred height at the top of the panel.
    buttonsPanel.setLayout(gridLayout);

            generateImageButton  = new JButton();
            generateImageButton.setAction(GenerateGlyphyImageAction.getSingleton());
            generateImageButton.setEnabled(false);
    JButton generateForumHtmlButton     = new JButton();
    JButton generateFullHtmlButton      = new JButton();
    JButton displayHelpButton           = new JButton();
    buttonsPanel.add(generateImageButton);
    buttonsPanel.add(generateForumHtmlButton);
    buttonsPanel.add(generateFullHtmlButton);
    buttonsPanel.add(displayHelpButton);
    
    outerButtonsPanel.add(buttonsPanel, BorderLayout.NORTH);
    JScrollPane buttonsPanelScrollPane = new JScrollPane(outerButtonsPanel);
    
    add(mainSplitPane, BorderLayout.CENTER);
    add(buttonsPanelScrollPane, BorderLayout.WEST);
    
//    generateImageButton.setText("Update Image");
    GenerateHtmlAction generateForumHtmlAction 
      = new GenerateHtmlAction(mainFrame, GenerateHtmlAction.Target.FORUM, I18n.getString("forumHTMLButtonText"));
    generateForumHtmlButton.setAction(generateForumHtmlAction);
    GenerateHtmlAction generateFullHtmlAction 
      = new GenerateHtmlAction(mainFrame, GenerateHtmlAction.Target.FULL, I18n.getString("fullHTMLButtonText"));
    generateFullHtmlButton.setAction(generateFullHtmlAction);
    
    JDialog helpDialog = new GlyphyHelpDialog(mainFrame);
    DisplayGlyphyHelpAction displayHelpAction = new DisplayGlyphyHelpAction(mainFrame, helpDialog);
    displayHelpButton.setAction(displayHelpAction);
  }
  
  /**
   * Set the given tile array on the glyphy tool images panel.
   * 
   * @param tileArray
   */
  public void setTileArray(TileArray tileArray)
  {
    mapPanel.setTileArray(tileArray);
    Container c = mapPanel.getParent();
    c.repaint();
    generateImageButton.setEnabled(false);
    setDirtyText(false);    // also sets unsavedChanges to false;
  }

  /**
   * Return the text entered from the text area holding the text.
   *  
   * @return
   */
  public String getGlyphyText()  {    return textMapTextArea.getText();  }
  
  // DocumentEvents happen when someone changes the text of the text area.
  @Override public void insertUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void removeUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void changedUpdate(DocumentEvent e) { }
  
  // Vitrovius Working Pane methods
  @Override public void setGenerateImageActionStatus()
  {
    boolean enabled = textDirty();
    GenerateGlyphyImageAction.getSingleton().setEnabled(enabled);
  }
  @Override public String getDisplayName() { return I18n.getString("glyphyToolPaneName"); }
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
      String defaultFolderName = applicationPreferences.get("savedOpenGlyphTextFileDirectory", defaultPath);
      String defaultExtension = "txt";
      
      String dialogTitle = I18n.getString("fileOpenGlyphTextFileActionName");
      String buttonText = I18n.getString("fileOpenDialogButtonText");
      
      FileHandler.FileChoice fileChoice = fileHandler.getFileToOpen(this, defaultFolderName, defaultExtension, dialogTitle, buttonText);
      String message = null;
      String filename = null;
      if (!fileChoice.cancelled())
      {
        if (!fileChoice.succeeded())
        {
          mainFrame.addMessage(fileChoice.getMessage());
        } 
        else
        {
          try
          {
            File file = fileChoice.getFile();
            filename = file.getCanonicalPath();
            String text = readTextFromFile(file);
            textMapTextArea.setText(text);
            saveFileInPreferences(file);
            currentlyOpenFile = file;
            setDirtyText(true);
            setUnsavedChanges(false);
          } 
          catch (Exception e)
          {
            message = I18n.getString("errorOpeningFile", filename, e.getMessage());
            mainFrame.addMessage(message);
          }
        }
      }
    }
  }
  
  private String readTextFromFile(File file) throws Exception
  {
    BufferedReader reader = null;
    StringBuilder textSB = new StringBuilder();
      String filename = file.getCanonicalPath();
      reader = new BufferedReader(new FileReader(file));
      String line = reader.readLine();
      String newLine = "\n"; // System.getProperty("line.separator"); // String.format("%n");
      if (line == null) 
      { 
        if (reader != null) { reader.close(); }
        throw new Exception(I18n.getString("glyphTextFileNoText", filename)); 
      }
      else
      {
        while(line != null)
        {
          textSB.append(line);
          textSB.append(newLine);
          line = reader.readLine();
        }
      }
    if (reader != null) { reader.close(); }
    return textSB.toString();
  }
  
  @Override
  public void saveFile()
  {
    if (currentlyOpenFile == null)
    {
      saveFileAs();
    }
    else
    {
      String message = "";
      String filepath = "";
      try                 { filepath = currentlyOpenFile.getCanonicalPath(); 
                            message = saveToFile(currentlyOpenFile);
                            setUnsavedChanges(false);
                          }
      catch (Exception e) { message = I18n.getString("fileCouldNotBeSaved", filepath); }
      finally             { mainFrame.addMessage(message);      }
    }
  }
  
  private String saveToFile(File selectedFile) throws Exception
  {
    String filepath = selectedFile.getCanonicalPath();
    saveCurrentTextEntry(selectedFile);
    saveFileInPreferences(selectedFile);
    String resultMessage = I18n.getString("fileSavedMessage", filepath);
    return resultMessage;
  }
  
  private void saveCurrentTextEntry(File file) throws Exception
  {
    BufferedWriter writer = null;
    writer = new BufferedWriter(new FileWriter(file));
    
    String text = textMapTextArea.getText();
    try   { 
            writer.write(text);
//            writer.newLine();
          }
    catch (Exception exception) { throw new Exception("Error writing glyph save file", exception); }
    finally { writer.close(); }
  }
  
  private void saveFileInPreferences(File selectedFile) throws Exception
  {
    // now that we've saved it, save the filename and directory for use later.
//    String filename = selectedFile.getName();
    String filepath = selectedFile.getCanonicalPath();
    // TODO: implement preferences saving for glyph tile file
//    applicationPreferences.put(SAVED_TILE_FILE_FILENAME_KEY, filename);   // we don't seem to use this.
    applicationPreferences.put("savedOpenGlyphTextFileDirectory", filepath);
  }
  
  @Override
  public void saveFileAs()
  {
    String text = textMapTextArea.getText();
    if (text == null || text.length() == 0)
    {
      JOptionPane.showMessageDialog(this, I18n.getString("noGlyphTextMessage"), I18n.getString("noGlyphTextTitle"), JOptionPane.INFORMATION_MESSAGE);
    }
    else
    {
      String resultMessage      = null;
      String defaultPath        = System.getProperty("user.home");
      String defaultFolderName  = applicationPreferences.get("savedOpenGlyphTextFileDirectory", defaultPath);
      
      FileHandler.FileChoice fileChoice = fileHandler.getFileToSave(this,  
          defaultFolderName, 
          "txt",          // TODO make this and the directory name key constants
          I18n.getString("fileSaveGlyphTextFileActionName"), 
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
            resultMessage = saveToFile(selectedFile);
            setUnsavedChanges(false);
          }
          catch (Exception exception) 
          { 
            resultMessage = I18n.getString("fileCouldNotBeSaved", filepath);
          }
        }
        mainFrame.addMessage(resultMessage);
      }
    }
  }
  @Override
  public void clearPanel()
  {
    String message = I18n.getString("confirmCloseWithoutSave");
    String title   = I18n.getString("confirmCloseDialogTitle");
    int option = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    if (option == JOptionPane.OK_OPTION)
    {
      try 
      { 
        textMapTextArea.setText("");
        setUnsavedChanges(false);
      }
      catch (Exception exception) { throw new RuntimeException("problem in GlyphyToolPanel.clearPanel()", exception); }
    }
  }
  
  @Override
  public void setPanelSize()
  {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void decreaseTileSize()
  {
    mapPanel.decreaseTileSize();
  }
  @Override
  public void increaseTileSize()
  {
    mapPanel.increaseTileSize();
  }
  @Override
  public String generateHtml(GenerateHtmlAction.Target target)
  {
    String result = null;
    if (textDirty())
    {
      JOptionPane.showMessageDialog(mainFrame, 
                                    I18n.getString("cannotGenerateHtmlDirtyTextMessage"), 
                                    I18n.getString("cannotGenerateHtmlDirtyTextTitle"), 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    else
    {
      TileArray tiles = mapPanel.getTileArray(); 
      
      if (!tiles.isEmpty())
      {
        if (target == GenerateHtmlAction.Target.FULL) 
              { result = HtmlGenerator.generateFullHtml(tiles, applicationPreferences); }
        else  { result = HtmlGenerator.generateForumHtml(tiles, applicationPreferences); }
      }
    }
    return result;
  }
  @Override
  public void displayHelp()
  {
    // TODO Auto-generated method stub
    
  }
}
