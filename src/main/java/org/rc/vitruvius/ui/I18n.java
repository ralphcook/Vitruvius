package org.rc.vitruvius.ui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n
{
  private static ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle");
  private static String newLine = String.format("%n");
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
      value = value.replace("\\n", newLine);
    }
    catch (MissingResourceException mre)
    {
      value = key;
      System.err.println(mre.getMessage());
    }
    return value;
    
  }

  public static String getString(String key, Object... arguments)
  {
    String value = null;
    try
    {
      String template = bundle.getString(key);
      value = MessageFormat.format(template,  arguments);
    }
    catch (MissingResourceException mre)
    {
      value = key;
      for (int i=0; i<arguments.length; i++)
      {
        value = value + " : " + arguments[i].toString();
        System.err.println(mre.getMessage());
      }
    }
    return value;
  }
}
