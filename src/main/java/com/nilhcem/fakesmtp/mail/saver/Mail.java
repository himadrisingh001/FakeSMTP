package com.nilhcem.fakesmtp.mail.saver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Mail
{
  private final String date;
  private final String from;
  private final String to;
  private final String subject;
  private final String original;

  @JsonCreator
  public Mail(
      @JsonProperty("date") String date,
      @JsonProperty("from") String from,
      @JsonProperty("to") String to,
      @JsonProperty("subject") String subject,
      @JsonProperty("original") String original
  )
  {
    this.date = date;
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.original = original;
  }

  @JsonProperty
  public String getFrom()
  {
    return from;
  }

  @JsonProperty
  public String getTo()
  {
    return to;
  }

  @JsonProperty
  public String getSubject()
  {
    return subject;
  }

  @JsonProperty
  public String getOriginal()
  {
    return original;
  }

  @JsonProperty
  public String getDate()
  {
    return date;
  }

  @Override
  public String toString()
  {
    return "Mail{" +
           "date='" + date + '\'' +
           ", from='" + from + '\'' +
           ", to='" + to + '\'' +
           ", subject='" + subject + '\'' +
           ", original='" + original + '\'' +
           '}';
  }
}
