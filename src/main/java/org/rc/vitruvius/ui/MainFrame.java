package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.rc.vitruvius.MessageListener;
import org.rc.vitruvius.text.TextTranslator;

import rcutil.swing.SavedWindowPositionJFrame;

public class MainFrame extends SavedWindowPositionJFrame implements MessageListener, DocumentListener
{
//  public static void say(String msg) { System.out.println(msg); }
  
  private JTextArea   textMapTextArea   = null;     // contains text representing a portion of a map
  private ImagesPanel imagesPanel       = null;     // contains images representing a portion of a map
  private JTextArea   messagesTextArea  = null;     // gets messages from the program to the user.
  private JButton     generateImageButton = null;   // generates the image on the images panel;
                                                    // disabled when activated, enabled when text changes.
  private Picture[][] pictures          = null;     // 2D array of images.
  
  Preferences applicationPreferences = null;
  
  public Picture[][] getPictures() 
  { 
    getUpdatedPictures();
    return pictures; 
  }
  
  private boolean dirtyText = false; 
  public void setDirtyText(boolean b) { dirtyText = b; generateImageButton.setEnabled(true); } 
  public boolean textDirty() { return dirtyText; }
  
  private static final long serialVersionUID = 1L;

  public MainFrame()
  {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    applicationPreferences = Preferences.userNodeForPackage(this.getClass());
    // numbers here represent default window position for first time program is run
    initializeSavedWindowPosition(applicationPreferences, 100, 100, 500, 400);
    
    Picture.checkImageFiles();    // outputs error messages if we have letters that are supposed to correspond to particular buildings,
                                  // but have no image file for the buildings.
    
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
    
    // create a panel for buttons to control the program operation,
    // and put a couple of buttons in it.
    JPanel outerButtonsPanel = new JPanel();
    outerButtonsPanel.setLayout(new BorderLayout());
    JPanel buttonsPanel = new JPanel();
    GridLayout gridLayout = new GridLayout(4, 1);   // GridLayout makes the buttons the same size, 
                                                    // another panel using BorderLayout.NORTH makes 
                                                    // them their preferred height at the top of the panel.
    buttonsPanel.setLayout(gridLayout);
    generateImageButton         = new JButton("Image");
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
    
    messagesTextArea = new JTextArea(5, 80);
    JScrollPane messagesScrollPane = new JScrollPane(messagesTextArea);
    
//  String stringMap = getTestText();
//  setText(textMap, stringMap);
//  Picture[][] pictureArray = getTestPictureArray(stringMap);
  
//  ImagesPanel imagesPanel = new ImagesPanel(50, pictureArray);
  
//    HtmlGenerator.generateFullHtml(pictureArray);
    
    add(mainSplitPane);
    add(buttonsPanelScrollPane, BorderLayout.WEST);
    add(messagesScrollPane, BorderLayout.SOUTH);
    
//    add(textMapScrollPane, BorderLayout.NORTH);
//    add(imagesScrollPane);

    pack();
    
    setWindowPosition();      // set position of the window on the screen.
    
    GenerateImageAction generateImageAction = new GenerateImageAction(this);
    generateImageButton.setAction(generateImageAction);
    GenerateHtmlAction generateForumHtmlAction = new GenerateHtmlAction(this, GenerateHtmlAction.Target.FORUM, "Forum HTML");
    GenerateHtmlAction generateFullHtmlAction = new GenerateHtmlAction(this, GenerateHtmlAction.Target.FULL, "Full HTML");
    generateForumHtmlButton.setAction(generateForumHtmlAction);
    generateFullHtmlButton.setAction(generateFullHtmlAction);
    
    JDialog helpDialog = new HelpDialog(this);
    DisplayHelpAction displayHelpAction = new DisplayHelpAction(helpDialog);
    displayHelpButton.setAction(displayHelpAction);
    
  }
  
  public Picture[][] getUpdatedPictures()
  {
    String text = textMapTextArea.getText();
    if (text == null || text.length() == 0)
    {
      addMessage("No text for which to generate image");
      pictures = null;
    }
    else
    {
      if (textDirty()) 
      { 
        pictures = new TextTranslator(this).createPictureArray(text); 
        setDirtyText(false);
      }
    }
    return pictures;
  }
  
  public Picture[][] generateImage()
  {
    messagesTextArea.setText("");
    pictures = getUpdatedPictures();
    if (pictures != null)
    {
      imagesPanel.setPictures(pictures);
      this.repaint();
      generateImageButton.setEnabled(false);
    }
    return pictures;
  }
  
  public void addMessage(String message)
  {
    messagesTextArea.append(message);
  }

  // DocumentEvents happen when someone changes the text of the text area.
  @Override public void insertUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void removeUpdate(DocumentEvent e)  {    setDirtyText(true);  }
  @Override public void changedUpdate(DocumentEvent e) { }
  
}
