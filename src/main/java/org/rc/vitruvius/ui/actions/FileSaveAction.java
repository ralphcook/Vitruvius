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

public class FileSaveAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  private MainFrame   mainFrame = null;
  private Preferences preferences = null;
  
  public FileSaveAction(MainFrame mainFrame, Preferences applicationPreferences)
  {
    super(I18n.getString("fileSaveActionName"));
    this.mainFrame = mainFrame;
    this.preferences = applicationPreferences;

    String mnemonicKey = I18n.getString("fileSaveMnemonicKey");
    int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
    putValue(MNEMONIC_KEY, keyCode);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    String defaultPath        = System.getProperty("user.home");
    String defaultFolderName  = preferences.get(FileOpenAction.SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
    JFileChooser chooser = new JFileChooser(defaultFolderName);
    ExtensionFileFilter saveFilter = new ExtensionFileFilter(FileOpenAction.SAVED_TILE_FILE_FILENAME_EXTENSION);
    chooser.setFileFilter(saveFilter);
    chooser.setDialogTitle(I18n.getString("fileSaveActionName"));

    String buttonText = I18n.getString("fileSaveDialogButtonText");
    
    int fileChooseReturn = chooser.showDialog(mainFrame, buttonText);
    if (fileChooseReturn == JFileChooser.APPROVE_OPTION)
    {
      File selectedFile = chooser.getSelectedFile();
      try 
      { 
        mainFrame.saveTileFile(selectedFile);
        String filename = selectedFile.getName();
        String filepath = selectedFile.getCanonicalPath();
        preferences.put(FileOpenAction.SAVED_TILE_FILE_FILENAME_KEY, filename);
        preferences.put(FileOpenAction.SAVED_TILE_FILE_DIRECTORY_KEY, filepath);
      }
      catch (Exception exception) { exception.printStackTrace(); }
    }
  }

}
