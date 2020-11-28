package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import rcutil.layout.RowColumnGroupLayout;

public class HtmlSettingsDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  
  public static final String HEAVEN_GAMES_PREFIX_PREF_KEY           = "heavenGamesImagePrefixKey";
  public static final String HEAVEN_GAMES_PREFIX_HARD_CODED_DEFAULT = "/strategy/housing/images/";
  public static final String FULL_HTML_PREFIX_PREF_KEY              = "fullHtmlImagePrefixKey";
  public static final String FULL_HTML_PREFIX_HARD_CODED_DEFAULT    = "images/";

  Preferences applicationPreferences = null;
  
  JTextField forumPrefix  = null;
  JTextField fullPrefix   = null;
  
  public HtmlSettingsDialog(MainFrame mainFrame, Preferences givenPreferences)
  {
    super(mainFrame, I18n.getString("htmlSettingsDialogTitle"), true);
    this.applicationPreferences = givenPreferences;
    createDisplay();
    setLocationRelativeTo(mainFrame);
  }
  
  private void createDisplay()
  {
    setLayout(new BorderLayout());
    
    JLabel blankLabel1      = new JLabel("");
    JLabel blankLabel2      = new JLabel("");
    JLabel blankLabel3      = new JLabel("");
    JLabel blankLabel4      = new JLabel("");
    JLabel imagePrefixLabel = new JLabel("Image Prefix");
    JLabel forumHtmlLabel   = new JLabel("Forum HTML");
    JLabel fullHtmlLabel    = new JLabel("Full HTML");
    JButton heavenGamesDefaultButton = new JButton("HeavenGames default prefix");
    JButton fullHtmlDefaultButton = new JButton("Full default prefix");
           forumPrefix    = new JTextField();
           fullPrefix     = new JTextField();
    
    JButton okButton          = new JButton("OK");
    JButton cancelButton      = new JButton("Cancel");
    JPanel buttonPanel = new JPanel();
    BoxLayout buttonPanelLayout = new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS);
    buttonPanel.setLayout(buttonPanelLayout);
    buttonPanel.setBorder(BorderFactory.createLineBorder(Color.blue, 2));
    
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    
    JPanel entryPanel = new JPanel();
    RowColumnGroupLayout entryPanelLayout = new RowColumnGroupLayout(entryPanel);
    entryPanel.setLayout(entryPanelLayout);
    
    entryPanelLayout.addColumn(blankLabel1, forumHtmlLabel, blankLabel2, fullHtmlLabel, blankLabel3, blankLabel4);
    entryPanelLayout.addColumn(imagePrefixLabel, forumPrefix, heavenGamesDefaultButton, 
                                                  fullPrefix, fullHtmlDefaultButton, buttonPanel);
    entryPanelLayout.addRow(blankLabel1, imagePrefixLabel);
    entryPanelLayout.addRow(forumHtmlLabel, forumPrefix);
    entryPanelLayout.addRow(blankLabel2, heavenGamesDefaultButton);
    entryPanelLayout.addRow(fullHtmlLabel, fullPrefix);
    entryPanelLayout.addRow(blankLabel3, fullHtmlDefaultButton);
    entryPanelLayout.addRow(blankLabel4, buttonPanel);
    
    JScrollPane scrollPane = new JScrollPane(entryPanel);
    
    add(scrollPane, BorderLayout.CENTER);
    
    pack();

    String forumValue = applicationPreferences.get(HEAVEN_GAMES_PREFIX_PREF_KEY, HEAVEN_GAMES_PREFIX_HARD_CODED_DEFAULT);
    forumPrefix.setText(forumValue);
    String fullValue = applicationPreferences.get(FULL_HTML_PREFIX_PREF_KEY, FULL_HTML_PREFIX_HARD_CODED_DEFAULT);
    fullPrefix.setText(fullValue);
    
    heavenGamesDefaultButton.addActionListener
    ( (ActionEvent event) -> { forumPrefix.setText(HEAVEN_GAMES_PREFIX_HARD_CODED_DEFAULT); } );
    
    fullHtmlDefaultButton.addActionListener
    ( (ActionEvent event) -> { fullPrefix.setText(FULL_HTML_PREFIX_HARD_CODED_DEFAULT); } );
    
    okButton.addActionListener
    ( (ActionEvent event) -> 
      { String forumText = forumPrefix.getText().trim();
        String fullText  = fullPrefix.getText().trim();
        
        applicationPreferences.put(HEAVEN_GAMES_PREFIX_PREF_KEY, forumText);
        applicationPreferences.put(FULL_HTML_PREFIX_PREF_KEY, fullText);
        this.dispose();
      } 
    );
    
    cancelButton.addActionListener    ( (ActionEvent event) ->    { this.dispose();    }    );
  }
}
