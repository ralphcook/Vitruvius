package org.rc.vitruvius.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
  private static ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle");
  public static String getString(String key) throws RuntimeException
  {
    String value = null;
    try
    {
      value = bundle.getString(key);
    }
    catch (MissingResourceException mre)
    {
      value = key;
      System.err.println(mre.getMessage());
    }
    return value;
  }
}
