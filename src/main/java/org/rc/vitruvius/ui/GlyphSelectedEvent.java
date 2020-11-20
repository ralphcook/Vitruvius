package org.rc.vitruvius.ui;

import java.util.EventObject;

import org.rc.vitruvius.model.Draggable;

public class GlyphSelectedEvent extends EventObject
{
  private static final long serialVersionUID = 1L;
  
  private Draggable draggable = null;

  public GlyphSelectedEvent(Object source, Draggable draggable)
  {
    super(source);
    this.draggable = draggable;
  }
  
  public Draggable getDraggable() { return draggable; }

}
