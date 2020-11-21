package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.rc.vitruvius.model.TileArray;
import org.rc.vitruvius.model.VitruviusWorkingPane;
import org.rc.vitruvius.text.TextTranslator;
import org.rc.vitruvius.ui.actions.DisplayGlyphyHelpAction;
import org.rc.vitruvius.ui.actions.GenerateHtmlAction;
import org.rc.vitruvius.ui.actions.GenerateImageAction;

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
  
  private static void say(String s) { System.out.println(s); }

//  private MainFrame   mainFrame         = null;
  
  private JTextArea   textMapTextArea   = null;     // contains text representing a portion of a map
  private ImagesPanel imagesPanel       = null;     // contains images representing a portion of a map
  private JButton     generateImageButton = null;   // generates the image on the images panel;
  // disabled when activated, enabled when text changes.
//  private TextTranslator textTranslator = null;
  
  private boolean dirtyText = false; 
  public boolean  textDirty()             { return dirtyText; }
  public void     setDirtyText(boolean b) { dirtyText = b; if (b) { setUnsavedChanges(true); } generateImageButton.setEnabled(b); } 
  
  private boolean unsavedChanges = false;
  public boolean unsavedChanges()             { return unsavedChanges; } 
  public void    setUnsavedChanges(boolean b) { unsavedChanges = b; }
  
  public GlyphyToolPanel(MainFrame mainFrame)
  {
//    this.mainFrame = mainFrame; 
    setLayout(new BorderLayout());
    
    // Create a text area where the user can put text to convert to a map picture
    textMapTextArea = new JTextArea(5,20);
    Font font = new Font("Courier", Font.PLAIN, 18); // new Font(Font.MONOSPACED, 20, Font.PLAIN);
    textMapTextArea.setFont(font);
    textMapTextArea.getDocument().addDocumentListener(this);
    JScrollPane textMapScrollPane = new JScrollPane(textMapTextArea);
    
    // create a panel where we'll display a resulting set of map images
    imagesPanel = new ImagesPanel(25);
    JScrollPane imagesScrollPane = new JScrollPane(imagesPanel);
    
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
    
    GenerateImageAction generateImageAction = new GenerateImageAction(this, mainFrame, new TextTranslator(mainFrame));
    generateImageButton.setAction(generateImageAction);
    GenerateHtmlAction generateForumHtmlAction 
      = new GenerateHtmlAction(mainFrame, this, GenerateHtmlAction.Target.FORUM, I18n.getString("forumHTMLButtonText"));
    generateForumHtmlButton.setAction(generateForumHtmlAction);
    GenerateHtmlAction generateFullHtmlAction 
      = new GenerateHtmlAction(mainFrame, this, GenerateHtmlAction.Target.FULL, I18n.getString("fullHTMLButtonText"));
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
    imagesPanel.setTileArray(tileArray);
    Container c = imagesPanel.getParent();
    c.repaint();
    generateImageButton.setEnabled(false);
    setDirtyText(false);    // also sets unsavedChanges to false;
  }

  /**
   * Return the text entered from the text area holding the text.
   *  
   * @return
   */
  public String getGlyphyText()
  {
    return textMapTextArea.getText();
  }
  
  // DocumentEvents happen when someone changes the text of the text area.
  @Override public void insertUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void removeUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void changedUpdate(DocumentEvent e) { }
  @Override
  public void openFile()
  {
    System.out.println("open glyphy tool file here");
    setUnsavedChanges(false);
  }
  @Override
  public void saveFile()
  {
    say("save glyphy tool file here");
    setUnsavedChanges(false);
  }
  @Override
  public void saveFileAs()
  {
    System.out.println("save glyphy tool file here");
    setUnsavedChanges(false);
  }
  @Override
  public void clearPanel()
  {
    // TODO Auto-generated method stub
    setUnsavedChanges(false);
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
}
