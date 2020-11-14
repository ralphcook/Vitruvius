package org.rc.vitruvius;

/**
 * Class accepting messages displayed for the user..
 * @author rcook
 *
 */
public interface UserMessageListener
{
  /**
   * Publish the given message to the user.
   */
  public void addMessage(String s);
  
  /**
   * Clear any messages visible to the user.
   */
  public void clearMessages();
}
