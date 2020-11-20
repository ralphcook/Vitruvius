package org.rc.vitruvius.ui;

import java.util.EventObject;

import org.rc.vitruvius.model.Draggable;

/**
 * This event indicates that the user has either selected a glyph to be dragged 
 * onto the Drag-N-drop map, or that he has deselected the currently selected glyph.
 * Possible actions are "select" and "deselect".
 * @author rcook
 *
 */
public class GlyphSelectionEvent extends EventObject
{
  private static final long serialVersionUID = 1L;
  
  private Draggable draggable = null;
  private String action = null;

  public GlyphSelectionEvent(Object source, String actionName, Draggable draggable)
  {
    super(source);
    this.draggable = draggable;
    this.action    = actionName;
  }
  
  public Draggable getDraggable() { return draggable; }
  public String    getAction()    { return action; }

}
