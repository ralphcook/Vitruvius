package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

import rcutil.file.ExtensionFileFilter;

@SuppressWarnings("serial")
public class FileOpenAction extends AbstractAction
{
  private       Preferences   applicationPreferences  = null;
  private       MainFrame     mainFrame               = null;
  public static String        SAVED_TILE_FILE_DIRECTORY_KEY   = "savedOpenFileDirectory";
  public static String        SAVED_TILE_FILE_FILENAME_KEY    = "savedOpenFileFilename";
  public static String        SAVED_TILE_FILE_FILENAME_EXTENSION = "tiles";
  
  public FileOpenAction(MainFrame mainFrame, Preferences applicationPreferences)
  {
    super(I18n.getString("fileOpenActionName"));
    this.mainFrame           = mainFrame;
    this.applicationPreferences = applicationPreferences;

    String mnemonicKey = I18n.getString("fileOpenMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(MNEMONIC_KEY, keyCode);
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    String defaultPath = System.getProperty("user.home");
    String defaultFolderName = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
    JFileChooser chooser = new JFileChooser(defaultFolderName);
    
    ExtensionFileFilter saveFilter = new ExtensionFileFilter(SAVED_TILE_FILE_FILENAME_EXTENSION);
    chooser.setFileFilter(saveFilter);
    chooser.setDialogTitle(I18n.getString("fileOpenActionName"));
    
    String buttonText = I18n.getString("fileOpenDialogButtonText");
    
    int fileChooseReturn = chooser.showDialog(mainFrame, buttonText);
    if (fileChooseReturn == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = chooser.getSelectedFile();
      try { mainFrame.openTileFile(selectedFile); }
      catch (Exception exception) { exception.printStackTrace(); }
    }
  }
}
