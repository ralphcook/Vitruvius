package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

public class HtmlDialog extends JDialog implements ActionListener
{
  private static final long serialVersionUID = 1L;
  
  JFrame mainFrame = null;
  String resourceName = null;
  
  // later we'll cache dialogs as we create them.
//  private static HashMap<String, HtmlDialog> dialogMap = new HashMap<>();
  
  /**
   * Display the HTML from the given resource name (which is relative
   * to the package of HtmlDialog, not the package of the caller).
   * @param resourceName
   */
  public static void display(JFrame mainFrame, String resourceName)
  {
    HtmlDialog dialog = new HtmlDialog(mainFrame, resourceName);
    dialog.setVisible(true);
  }

  public HtmlDialog(JFrame mainFrame, String resourceName)
  {
    this.mainFrame = mainFrame;
    this.resourceName = resourceName;
    createUI();
  }
  
  private void createUI()
  {
    JEditorPane htmlPane = new JEditorPane();
    htmlPane.setEditable(false);
    htmlPane.setContentType("text/html");
    
    JScrollPane scrollPane = new JScrollPane(htmlPane);
    scrollPane.setPreferredSize(new Dimension(600,600));
    
    // add an html editor kit
    HTMLEditorKit kit = new HTMLEditorKit();
    htmlPane.setEditorKit(kit);
    
    InputStream inStream = getClass().getResourceAsStream(resourceName);
    Document doc = kit.createDefaultDocument();
    try { htmlPane.read(inStream, doc);  }
    catch (Exception e) { e.printStackTrace(); }
    htmlPane.setCaretPosition(0);
    
    add(scrollPane, BorderLayout.CENTER);
    
    JButton okButton = new JButton(I18n.getString("closeButtonText"));
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    add(buttonPanel, BorderLayout.SOUTH);
    
    okButton.addActionListener(this);
    
    pack();
    setVisible(true);

    setLocationRelativeTo(mainFrame);
    
}
  
  public void actionPerformed(ActionEvent event)
  {
    dispose();
  }

}
