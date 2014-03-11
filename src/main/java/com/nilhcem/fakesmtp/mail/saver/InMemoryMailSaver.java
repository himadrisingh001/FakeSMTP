package com.nilhcem.fakesmtp.mail.saver;

import com.nilhcem.fakesmtp.core.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

  /*
  X-Mailer: Nodemailer (0.4.4; +http://www.nodemailer.com/)
Date: Wed, 05 Mar 2014 05:44:05 GMT
Message-Id: <1393998245575.f8b68f89@Nodemailer>
X-Smtpapi: {"category": ["staging"]}
From: "Metamarkets" <accounts@metamarkets.com>
To: test.singh@testing.com, accounts@metamarkets.com
Subject: Please reset your Metamarkets password
Content-Type: text/html; charset=utf-8
Content-Transfer-Encoding: quoted-printable



<div>Hi Test,</div>

<div>Either you or your system administrator requested that we reset your =
Metamarkets password. Clicking the below link will take you to a page where=
 you can create a new password.</div><br>

<div><a href=3D'http://localhost:8080/user/reset=3Fu=3D136b325b-9f57-4f34-9=
f9d-98b5c1095022'>Reset your Metamarkets password</a></div><br>

<div>Thanks,</div>
<div>The Metamarkets Team</div>
<div>www.metamarkets.com</div>
   */

public class InMemoryMailSaver implements MailSaver
{
  private static final Logger log = LoggerFactory.getLogger(InMemoryMailSaver.class);
  private final Pattern subjectPattern = Pattern.compile("^Subject: (.*)$");
  private final Pattern datePattern = Pattern.compile("^Date: (.*)$");

  private final List<Mail> list = new ArrayList<Mail>();

  public void saveEmailAndNotify(String from, String recipient, InputStream data)
  {
    String original = convertStreamToString(data);
    Mail mail = new Mail(
        extractPattern(datePattern, original), from, recipient,
        extractPattern(subjectPattern, original), original
    );
    list.add(mail);

    log.info(String.format("You've got %s mails! . %s", list.size(), mail));
  }

  private String convertStreamToString(InputStream is)
  {
    final long lineNbToStartCopy = 4; // Do not copy the first 4 lines (received part)
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(I18n.UTF8)));
    StringBuilder sb = new StringBuilder();

    String line;
    long lineNb = 0;
    try {
      while ((line = reader.readLine()) != null) {
        if (++lineNb > lineNbToStartCopy) {
          sb.append(line + System.getProperty("line.separator"));
        }
      }
    }
    catch (IOException e) {
      log.error("", e);
    }
    return sb.toString();
  }

  private String extractPattern(Pattern pattern, String data)
  {
    try {
      BufferedReader reader = new BufferedReader(new StringReader(data));

      String line;
      while ((line = reader.readLine()) != null) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
          return matcher.group(1);
        }
      }
    }
    catch (IOException e) {
      log.error("", e);
    }
    return "";
  }

  public void deleteEmails(){
    list.clear();
  }

  public Object getLock(){
    return this;
  }

  public List<Mail> getMails(){
    return list;
  }

}
