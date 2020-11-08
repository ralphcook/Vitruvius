package org.rc.vitruvius.ui;

public class Glyph
{
  private String filename;
  private String name;
  private int    rows;
  private int    cols;
  
  public Glyph(String filename, String name, int rows, int cols)
  {
    this.filename = filename;
    this.name     = name;
    this.rows     = rows;
    this.cols     = cols;
  }
  
  public String toString() { return name; }
  public String getName()  { return name; }
}
