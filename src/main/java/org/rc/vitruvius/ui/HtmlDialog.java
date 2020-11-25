package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class HtmlDialog extends JDialog implements ActionListener
{
  private static final long serialVersionUID = 1L;
  
  MainFrame mainFrame = null;

  public HtmlDialog(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
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
    
    // example adds some CSS StyleSheet rules here
    
    // we depart from the example at this point; trying to use JEditorPane's read(InputStream, Object)
    // method to read in from a file
    InputStream inStream = getClass().getResourceAsStream("VitruviusHelp.html");
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
