package org.rc.vitruvius.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.rc.vitruvius.ui.DragNDropPanel;
import org.rc.vitruvius.ui.GlyphSelectionEvent;

public class EndDragAction extends AbstractAction
{
  private static final long serialVersionUID = 1L;
  DragNDropPanel dndPanel = null;
  
  public EndDragAction(DragNDropPanel dndPanel)
  {
    this.dndPanel = dndPanel;
  }

  public void actionPerformed(ActionEvent actionEvent)
  {
    GlyphSelectionEvent gsEvent = new GlyphSelectionEvent(this, "deselect", null);
    dndPanel.fireGlyphSelectionEvent(gsEvent);
  }
}
