/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package com.sample;

import java.util.Date;

import org.lambdamatic.mongodb.annotations.EmbeddedDocument;

@EmbeddedDocument
public class BlogEntryComment {

  /** comment author. */
  private String author;

  /** comment date. */
  private Date date;

  /** comment content. */
  private String content;

  /**
   * Constructor.
   * 
   * @param author the author
   * @param date the date
   * @param content the content
   */
  public BlogEntryComment(String author, Date date, String content) {
    super();
    this.author = author;
    this.date = date;
    this.content = content;
  }

  public BlogEntryComment() {}

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
