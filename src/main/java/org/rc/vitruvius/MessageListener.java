package org.rc.vitruvius;

/**
 * Class accepting messages.
 * @author rcook
 *
 */
public interface MessageListener
{
  public void addMessage(String s);
  public void clearMessages();
}
