package org.rc.vitruvius.ui;

import java.util.ResourceBundle;

public class I18n
{
  private static ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle");
  public static String getString(String key) throws RuntimeException
  {
    String value = bundle.getString(key);
    if (value == null) { value = key; } 
    //{ throw new RuntimeException("Could not find resource bundle key <" + key + ">"); }
    return value;
  }
}
