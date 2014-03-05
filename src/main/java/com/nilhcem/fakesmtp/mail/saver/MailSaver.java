package com.nilhcem.fakesmtp.mail.saver;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: himadri
 * Date: 3/5/14
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MailSaver
{
  void saveEmailAndNotify(String from, String to, InputStream data);

  void deleteEmails();

  Object getLock();
}
