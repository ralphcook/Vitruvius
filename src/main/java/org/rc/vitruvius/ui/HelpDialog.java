package org.rc.vitruvius.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Contains the help text for the glyphy tool function, i.e., letters and their 
 * corresponding glyph descriptions.
 * @author rcook
 *
 */
public class HelpDialog extends JDialog
{
  private static final long serialVersionUID = 1L;
  
  private JTextArea textArea = null;

  public HelpDialog(JFrame parent)
  {
    super(parent);
    setTitle(I18n.getString("GlyphyTextHelpTitleText"));
    addWindowListener(closeWindow);
  }
  
  public void setVisible(boolean visible)
  {
    if (textArea == null)
    {
      JScrollPane textScrollPane = createTextScrollPane();
      JButton okButton = createOkButton();
      
      Container contextPane = getContentPane();
      JPanel horizontalPanel = new JPanel();
      horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.LINE_AXIS));

      contextPane.setLayout(new BorderLayout());
      contextPane.add(textScrollPane);
      contextPane.add(okButton, BorderLayout.SOUTH);

      pack();
    }
    textArea.setCaretPosition(0);
    textArea.setEditable(false);
    super.setVisible(visible);
  }
  
  private JButton createOkButton()
  {
    JButton okButton = new JButton(I18n.getString("OkButtonText"));
    okButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
      }
    });
    return okButton;
  }
  
  private JScrollPane createTextScrollPane()
  {
    textArea = new JTextArea(30, 30);
    textArea.setFont(new Font("Courier", Font.PLAIN, 18));    // new Font(Font.MONOSPACED, 20, Font.PLAIN);
    try 
    {
      String filename = I18n.getString("GlyphyHelpTextFilename");
      InputStream stream = this.getClass().getResourceAsStream(filename + ".txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      String line = reader.readLine();
      while (line != null) 
      {
        textArea.append(line);
        textArea.append("\n");
        line = reader.readLine();
      }
    } 
    catch (FileNotFoundException e1)    {      e1.printStackTrace();    } 
    catch (IOException e1)     {      e1.printStackTrace();    }

    JScrollPane textScrollPane = new JScrollPane(textArea);
    return textScrollPane;
  }

  private static WindowListener closeWindow = new WindowAdapter()
  {
    public void windowClosing(WindowEvent e)
    {
      e.getWindow().dispose();
    }
  };

}
