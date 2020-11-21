package org.rc.vitruvius.model;

/**
 * This interface defines actions supported by both the Drag-N-Drop
 * and the Glyphy tool "panes", i.e., the panes in the tabbed pane.
 * @author rcook
 *
 */
public interface VitruviusWorkingPane
{
  /**
   * Get a file specification from the user to open
   * and display on the current pane.
   * @return
   */
  public void     openFile();
  
  /**
   * Save the information from the current pane to the file from which it was read;
   * if there is no such file, have the user pick one as in SaveFileAs().
   */
  public void     saveFile();
  
  /**
   * Save the information from the current pane to a file
   * specified by the user.
   */
  public void     saveFileAs();
  
  /**
   * Clear the current pane's information, leaving a blank pane.
   */
  public void     clearPanel();

  /**
   * Set the size of the image panel on the current pane.
   */
  public void     setPanelSize();
  
  /**
   * Decrease the size of the tiles on the current pane.
   */
  public void     decreaseTileSize();
  
  /**
   * Increase the size of the tiles on the current pane.
   */
  public void     increaseTileSize();
  
  /**
   * Save the Information on the current image panel (the image panel
   * of the current pane) as HTML (file or clipboard?)  
   */
  public void     generateFullHTML();
  
  /**
   * Save the information on the current image panel (the image panel of the 
   * current pane) as HTML that can be included on another webpage, such as the
   * Heavengames Forum.
   */
  public void     generateForumHTML();

  /**
   * Display a help dialog for the current pane.
   */
  public void     displayHelp();
  
}
