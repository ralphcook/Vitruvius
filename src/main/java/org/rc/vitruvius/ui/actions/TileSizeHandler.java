package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.MainFrame;

public class TileSizeHandler
{
  public class Decrease extends AbstractAction
  {
    private static final long serialVersionUID = 1L;
    
    private MainFrame mainFrame = null;
    public Decrease(MainFrame givenMainFrame)             { this.mainFrame = givenMainFrame;  }
    public void actionPerformed(ActionEvent actionEvent)  { mainFrame.decreaseTileSize();     }
  }
  
  public class Increase extends AbstractAction
  {
    private static final long serialVersionUID = 1L;

    private MainFrame mainFrame = null;
    public Increase(MainFrame givenMainFrame)             { this.mainFrame = givenMainFrame;  }
    public void actionPerformed(ActionEvent actionEvent)  { mainFrame.increaseTileSize();     }
  }
}
