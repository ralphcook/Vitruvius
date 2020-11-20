package org.rc.vitruvius.ui;

/**
 * Listener for the "glyph selected" event; this happens when the user does
 * something that specifies a particular glyph which the user can then drag
 * onto the DragNDrop pane for placement.
 * 
 * @author rcook
 *
 */
public interface GlyphSelectionListener
{
  public void glyphSelection(GlyphSelectionEvent event);
}
