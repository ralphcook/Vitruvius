# Vitruvius
Standalone map planning tool for Caesar III & Julius

This program provides two tools, a drag-and-drop map builder and a "glyphy tool", for
creating collections of images in a 'map' based on the Sierra Impressions game "Caesar III".

The images are from a web-based "glyphy tool" that has been used for years on the website
"Caesar III Heaven" (heavengames.com). That tool allowed a user to enter a block of text where letters
stood for certain buildings and other graphics from the game, and the tool would translate
the text block to a block of image 'glyphs' to illustrate 
"housing blocks" to show other players. Results could be put
into HTML and posted on the Heavengames forum. The Vitruvius "Glyphy Tool" duplicates that 
functionality, removing the original 40x40 size restriction.

Vitruvius also provides a "drag and drop" tool for creating the same kinds of maps. It is
similar to building the maps in the game, except from a birds-eye horizontal/vertical view as opposed to the
perspective view of the game. The different orientation 
of the map makes it easier, especially for a novice, to see the patterns of roads and buildings 
being shown.

Drag and drop works much like in the game. The user selects the name of a glyph from a drop-down
box and thereby puts the tool into "dragging mode". In this mode, moving the mouse cursor across
the panel holding the map shows the selected glyph; the tool restricts placement to the 'tiles'
on the map. The user moves the glyph to its desired location and clicks the mouse to 'drop' 
it there. Continuing to move the mouse continues to show the
glyph, so the user can place multiple copies of it without having to reselect. He ends dragging
mode by right-clicking, pressing Escape on the keyboard, or clicking a "Clear" button provided on
the tool.

The user can also click on a glyph to select it, and press backspace or delete to remove it from
the map, or double-click to start a dragging mode for that glyph and move it somewhere else.

The glyphy tool provides two panels, one for entering text and one for showing the resulting map.
The tool regards each character as representing one 'tile' on the map, so a glyph that occupies more
than one tile requires placeholder characters in the text to avoid overlapping glyphs. For instance
an 'H' character represents a house occupying a 2x2 tile space; roads (R), gardens (G) and fountains
(F) are one tile each. So to represent two houses with a fountain and some garden, one could enter:

<PRE>
GGGGG
H. H.
..F..
RRRRR
</PRE>

This produces a row of garden, two rows occupied by the houses and a garden, and a road next to
the houses. The period characters are placeholders, helping the user space things so that the text
doesn't indicate an overlap of glyphs. There is also a space character above the fountain, and the
resulting map would have a blank tile at that location.

Vitruvius will save and read files containing Drag and Drop maps or Glyphy Tool text, and also 
generate HTML for the images from either tool.
