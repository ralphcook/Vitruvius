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
import org.rc.vitruvius.text.TextTranslator;

public class GlyphyToolPanel extends JPanel implements DocumentListener
{
  private static final long serialVersionUID = 1L;

  private MainFrame   mainFrame         = null;
  
  private JTextArea   textMapTextArea   = null;     // contains text representing a portion of a map
  private ImagesPanel imagesPanel       = null;     // contains images representing a portion of a map
  private JButton     generateImageButton = null;   // generates the image on the images panel;
  // disabled when activated, enabled when text changes.
  private TextTranslator textTranslator = null;
  
  private boolean dirtyText = false; 
  public void setDirtyText(boolean b) { dirtyText = b; generateImageButton.setEnabled(true); } 
  public boolean textDirty() { return dirtyText; }
  
  public GlyphyToolPanel(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame; 
  }
  
  public GlyphyToolPanel createPanel()
  {
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

            generateImageButton         = new JButton();
            generateImageButton.setEnabled(false);
    JButton generateForumHtmlButton     = new JButton("Forum HTML");
    JButton generateFullHtmlButton      = new JButton("Full HTML");
    JButton displayHelpButton           = new JButton("Help");
    buttonsPanel.add(generateImageButton);
    buttonsPanel.add(generateForumHtmlButton);
    buttonsPanel.add(generateFullHtmlButton);
    buttonsPanel.add(displayHelpButton);
    
    outerButtonsPanel.add(buttonsPanel, BorderLayout.NORTH);
    JScrollPane buttonsPanelScrollPane = new JScrollPane(outerButtonsPanel);
    
    add(mainSplitPane, BorderLayout.CENTER);
    add(buttonsPanelScrollPane, BorderLayout.WEST);
    
    GenerateImageAction generateImageAction = new GenerateImageAction(this);
    generateImageButton.setAction(generateImageAction);
    GenerateHtmlAction generateForumHtmlAction = new GenerateHtmlAction(mainFrame, this, GenerateHtmlAction.Target.FORUM, "Forum HTML");
    generateForumHtmlButton.setAction(generateForumHtmlAction);
    GenerateHtmlAction generateFullHtmlAction = new GenerateHtmlAction(mainFrame, this, GenerateHtmlAction.Target.FULL, "Full HTML");
    generateFullHtmlButton.setAction(generateFullHtmlAction);
    
    JDialog helpDialog = new HelpDialog(mainFrame);
    DisplayHelpAction displayHelpAction = new DisplayHelpAction(helpDialog);
    displayHelpButton.setAction(displayHelpAction);
    
    return this;
  }

  /**
   * Create a tile array from the glyphy tool text area and set the resulting
   * images in the images panel.
   * @return
   */
  public TileArray updateImagesPanelFromGlyphyText()
  {
    mainFrame.clearMessages();
    TileArray tiles = getTileArrayFromCurrentText();
    if (tiles != null)
    {
      imagesPanel.setTileArray(tiles);
      Container c = imagesPanel.getParent();
      c.repaint();
      generateImageButton.setEnabled(false);
    }
    return tiles;
  }
  
  /**
   * If the glyphy tool text area has changed since the tile array was last generated,
   * re-generate it, otherwise just get it from the images panel.
   * 
   * TODO: a little messy -- this method knows the tiles are stored in the images panel,
   * but doesn't update it when it generates another tile array. Inspect with caller(s)
   * and clean up.
   * @return new tile array.
   */
  private TileArray getTileArrayFromCurrentText()
  {
    TileArray tiles = null;
    
    String text = textMapTextArea.getText();
    if (text == null || text.length() == 0)
    {
      mainFrame.addMessage("No text for which to generate image");
    }
    else
    {
      if (textDirty()) 
      { 
        if (textTranslator == null) { textTranslator = new TextTranslator(mainFrame); }
        tiles = textTranslator.createTileArray(text); 
        setDirtyText(false);    // image matches text, so text no longer dirty
      }
      else
      {
        tiles = imagesPanel.getTileArray();
      }
    }
    return tiles;
  }
  

  // DocumentEvents happen when someone changes the text of the text area.
  @Override public void insertUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void removeUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void changedUpdate(DocumentEvent e) { }

}
