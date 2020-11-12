package org.rc.vitruvius.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
  private static ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle");
  
  /**
   * Get the resources value associated with the given key for the current
   * default locale. If the resource is not available, print a message to
   * syserror and return the key.
   * @param key String indexing the desired value
   * @return String resource for the given key, or the key if the resource is not found.
   * @throws RuntimeException
   */
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
