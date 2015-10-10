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

/**
 * A comment on a {@link BlogEntry}
 * 
 * @author xcoulon
 *
 */
@EmbeddedDocument
public class BlogEntryComment {

  /** comment author. */
  private String author;

  /** comment date. */
  private Date date;

  /** comment content. */
  private String content;

  /**
   * @param author
   * @param date
   * @param content
   */
  public BlogEntryComment(String author, Date date, String content) {
    super();
    this.author = author;
    this.date = date;
    this.content = content;
  }

  public BlogEntryComment() {}

  /**
   * @return the author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * @param author the author to set
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }

}
