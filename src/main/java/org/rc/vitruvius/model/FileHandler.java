package org.rc.vitruvius.model;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.rc.vitruvius.ui.I18n;
import org.rc.vitruvius.ui.MainFrame;

import rcutil.file.ExtensionFileFilter;

/**
 * Contains file handling logic and constants for Vitruvius
 * @author rcook
 *
 */
public class FileHandler
{
  private MainFrame mainFrame = null;
  
  private FileOpenAction fileOpenAction = null;
  private FileSaveAction fileSaveAction = null;
  private FileSaveAsAction fileSaveAsAction = null;
  
  public FileHandler(MainFrame mainFrame)
  {
    this.mainFrame = mainFrame;
    
    fileOpenAction = new FileOpenAction();
    fileSaveAction = new FileSaveAction();
    fileSaveAsAction = new FileSaveAsAction();
  }
  
  public FileOpenAction getOpenAction() { return fileOpenAction; }
  public FileSaveAction getSaveAction() { return fileSaveAction; }
  public FileSaveAsAction getSaveAsAction() { return fileSaveAsAction; }
  
  public class FileOpenAction extends AbstractAction
  {
    private static final long serialVersionUID = 1L;
    
    public FileOpenAction()
    {
      // Establish action name; appears in menus, etc.
      super(I18n.getString("fileOpenActionName"));
      
      // Establish key for alt-action that causes this action.
      String mnemonicKey = I18n.getString("fileOpenMnemonicKey");
      int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
      putValue(MNEMONIC_KEY, keyCode);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
      mainFrame.getCurrentWorkingPane().openFile();   // possibly should be mainFrame.openFile()
    }
  }
  
  public class FileSaveAction extends AbstractAction
  {
    private static final long serialVersionUID = 1L;

    public FileSaveAction()
    {
      super(I18n.getString("fileSaveActionName"));

      String mnemonicKey = I18n.getString("fileSaveMnemonicKey");
      int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
      putValue(MNEMONIC_KEY, keyCode);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      mainFrame.getCurrentWorkingPane().saveFile();
    }
  }
  
  public class FileSaveAsAction extends AbstractAction
  {
    private static final long serialVersionUID = 1L;

    public FileSaveAsAction()
    {
      super(I18n.getString("fileSaveAsActionName"));

      String mnemonicKey = I18n.getString("fileSaveAsMnemonicKey");
      int keyCode = KeyEvent.getExtendedKeyCodeForChar(mnemonicKey.charAt(0));
      putValue(MNEMONIC_KEY, keyCode);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      mainFrame.getCurrentWorkingPane().saveFileAs();
    }
  }
  
  /**
   * Static class, inner to FileHandler, provides the fields returned from a user file choice operation.
   * Available fields are the chosen file, message indicating success or failure, boolean indicating
   * success or failure, and boolean indicating cancellation. If this last is true, the succeeded flag
   * is false and the other values are unspecified. If the method did not succeed, the file field value
   * is not specified.
   * 
   * @author rcook
   *
   */
  public static class FileChoice
  {
    private File file = null;           public File getFile()       { return file; }
    private String message = null;      public String getMessage()  { return message; }
    private boolean succeeded = false;  public boolean succeeded()  { return succeeded; }
    private boolean cancelled = false;  public boolean cancelled()   { return cancelled; }
    
    private FileChoice(File f, String m, boolean succeeded)
    {
      this(f, m, succeeded, true);
    }
    
    private FileChoice(boolean cancelled)
    {
      this(null, null, false, cancelled);
      if (!cancelled) throw new IllegalArgumentException("programming error: this FileChoice constructor must have 'true' parameter value");
    }

    private FileChoice(File f, String m, boolean succeeded, boolean cancelled)
    {
      file = f; message = m; this.succeeded = succeeded; this.cancelled = cancelled;
    }
    
    public static FileChoice createCancelledFileChoice() { return new FileChoice(true); }
    public static FileChoice createFileChoice(File f, String m, boolean s) { return new FileChoice(f, m, s, false); }
  }
  
    public FileChoice getFileToOpen
    (
        Component parentWindow,
        String defaultFolderName,
        String defaultExtension,
        String dialogTitle,
        String buttonText
    )
      {
//        String defaultPath = System.getProperty("user.home");
//        String defaultFolderName = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
//        String defaultExtension = SAVED_TILE_FILE_FILENAME_EXTENSION;

        // create a chooser dialog starting at the designated folder.
        JFileChooser chooser = new JFileChooser(defaultFolderName);
        
        // unless the designated extension is null,
        // create a file filter for it and put that on the chooser.
        if (defaultExtension != null)
        {
          
          ExtensionFileFilter saveFilter = new ExtensionFileFilter(defaultExtension);
          chooser.setFileFilter(saveFilter);
        }
        
        // set the designated dialog title and button text.
        chooser.setDialogTitle(dialogTitle);

        // this displays the dialog and gets the user's choice.
        int fileChooseReturn = chooser.showDialog(parentWindow, buttonText);
        
        FileChoice fileChoice   = null;
        File selectedFile       = null;
        String selectedFilename = "";
        String message          = null;
        Boolean success         = null;
        
        // the user can choose a file or cancel the dialog; 
        // we only do the following if he chose a file.
        if (fileChooseReturn != JFileChooser.APPROVE_OPTION)
        {
          fileChoice = FileChoice.createCancelledFileChoice();
        }
        {
          try 
          { 
            // if the user ends the filename with a period, it is stripped off before it gets here.
            // determine if the user has entered a file without an extension
            selectedFile = chooser.getSelectedFile();
            selectedFile = addExtensionIfNeeded(selectedFile, defaultExtension);
            selectedFilename = selectedFile.getCanonicalPath();
            if (!selectedFile.exists())
            {
              message = I18n.getString("noSuchFile", selectedFilename);
              success = false;
            }
            else
            {
              success = true;
              message = I18n.getString("fileToOpenSuccess", selectedFilename);
            }
          }
          catch (Exception exception) 
          { 
            success = false;
            message = I18n.getString("errorOpeningFile", selectedFilename, exception.getMessage());
          }

          fileChoice = FileChoice.createFileChoice(selectedFile, message, success); 
        }
        return fileChoice;
      }
    
    /**
     * Get a file to which to save something; the given parentWindow, foldername, extension, dialog title,
     * and button text are all put to their obvious uses. This method displays a file chooser dialog and gets
     * the user's choice; a FileChoice return indicates success or failure, contains an error message on the
     * latter, and provides a flag to indicate the user cancelled the operation.
     * 
     * @param parentWindow
     * @param defaultFolderName
     * @param defaultExtension
     * @param dialogTitle
     * @param buttonText
     * @return FileChoice
     * 
     * @see FileChoice
     */
    public FileChoice getFileToSave
                                    ( 
                                      Component parentWindow,
                                      String defaultFolderName,
                                      String defaultExtension,
                                      String dialogTitle,
                                      String buttonText
                                    )
    {
      // create a file chooser for the given folder, extension, title, and button text.
      JFileChooser chooser = new JFileChooser(defaultFolderName);
      ExtensionFileFilter saveFilter = new ExtensionFileFilter(defaultExtension);
      chooser.setFileFilter(saveFilter);
      chooser.setDialogTitle(dialogTitle);
      
      // show the file chooser to the user, return when he's selected something.
      int fileChooseReturn = chooser.showDialog(parentWindow, buttonText);
      
      FileChoice  fileChoice        = null;
      File        selectedFile      = null;
      String      selectedFilename  = "";
      
      if (!(fileChooseReturn == JFileChooser.APPROVE_OPTION))
      {
        fileChoice = FileChoice.createCancelledFileChoice();
      }
      else
      {
        try
        {
          // if the user ends the filename with a period, it is stripped off before we get here.
          // If there's no extension on the filename, we either have to add one or assume the user
          // meant it to be missing. We're going to do the former, so there is no way for the user
          // to choose a file(name) for saving that has no extension.
          selectedFile = chooser.getSelectedFile();
          selectedFilename = selectedFile.getCanonicalPath();
          selectedFile = addExtensionIfNeeded(selectedFile, defaultExtension);
          selectedFilename = selectedFile.getCanonicalPath();
          
          boolean okToGo = true;
          if (selectedFile.exists())
          {
            // have the user confirm that he wants to overwrite this file.
            String confirmOverwriteMessage      = I18n.getString("confirmFileOverwriteMessage", selectedFilename);
            String confirmOverwriteDialogTitle  = I18n.getString("confirmFileOverwriteDialogTitle");
            int confirmOverwrite = JOptionPane.showConfirmDialog(parentWindow, confirmOverwriteMessage, confirmOverwriteDialogTitle, JOptionPane.YES_NO_OPTION);
            if (confirmOverwrite == JOptionPane.NO_OPTION) { okToGo = false; }
          }
          
          if (!okToGo) { fileChoice = FileChoice.createCancelledFileChoice(); }
          else { fileChoice = FileChoice.createFileChoice(selectedFile, I18n.getString("fileToSaveSuccess"), true); }
          return fileChoice;
        }
        catch (Exception e)
        {
          fileChoice = FileChoice.createFileChoice(null, I18n.getString("errorGettingSaveFile", selectedFilename, e.getMessage()), false);
        }
      }
      return fileChoice;
    }
    
    private File addExtensionIfNeeded(File selectedFile, String defaultExtension) throws Exception
    {
      File fileToReturn = null;
      String filename = selectedFile.getCanonicalPath();
      if (filename.contains("."))
      {
        fileToReturn = selectedFile;
      }
      else
      {
        filename = filename + "." + defaultExtension;
        fileToReturn = new File(filename);
      }
      return fileToReturn;
    }
  
//    public void openFile()
//    {
//      // TODO: consider a method (somewhere) that accepts necessary parameters for user to choose
//      // a file to save to; parameters would include:
//      // defaultPath
//      // defaultFolderName
//      // filter, or extension(s) for filter
//      // dialog title
//      // dialog button text
//      //
//      // would the method handle checking for existing file and warning of overwrite?
//      // would the method handle defaulting the extension and warning of overriding?
//      // 
//      String defaultPath = System.getProperty("user.home");
//      String defaultFolderName = applicationPreferences.get(SAVED_TILE_FILE_DIRECTORY_KEY, defaultPath);
//      String defaultExtension = SAVED_TILE_FILE_FILENAME_EXTENSION;
//      JFileChooser chooser = new JFileChooser(defaultFolderName);
//      
//      ExtensionFileFilter saveFilter = new ExtensionFileFilter(defaultExtension);
//      chooser.setFileFilter(saveFilter);
//      chooser.setDialogTitle(I18n.getString("fileOpenActionName"));
//      
//      String buttonText = I18n.getString("fileOpenDialogButtonText");
//      
//      int fileChooseReturn = chooser.showDialog(this, buttonText);
//      
//      File selectedFile = null;
//      String selectedFilename = null;
//      String message = null;
//      
//      if (fileChooseReturn == JFileChooser.APPROVE_OPTION)
//      {
//        try 
//        { 
//          selectedFile = chooser.getSelectedFile();
//          selectedFilename = selectedFile.getCanonicalPath();
//          if (!selectedFilename.contains(".")) 
//          { 
//            // user has a filename without an extension, we'll have to add it, then replace selected file...
//            selectedFilename = selectedFilename + "." + defaultExtension; 
//            selectedFile = new File(selectedFilename);
//          }
//          if (!selectedFile.exists())
//          {
//            message = I18n.getString("noSuchFile", selectedFilename);
//          }
//          else
//          {
//            TileArray tileArray = TileArray.readFromFile(selectedFile);
//            mapPane.setTileArray(tileArray); 
//          }
//        }
//        catch (Exception exception) 
//        { 
//          try
//          {
//            message = I18n.getString("errorOpeningFile", selectedFile.getCanonicalPath());
//          }
//          catch (IOException ioe)
//          {
//            message = I18n.getString("errorOpeningFile");
//          }
//          message = String.format("%s (%s)", message, exception.getMessage());
//        }
//        if (message != null) { userMessageListener.addMessage(message); }
//      }
//    }
//  
//  
//  }

}
