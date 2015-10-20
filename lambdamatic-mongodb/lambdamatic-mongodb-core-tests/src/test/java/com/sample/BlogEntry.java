/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package com.sample;

import java.util.Date;
import java.util.List;

import org.lambdamatic.mongodb.annotations.Document;
import org.lambdamatic.mongodb.annotations.DocumentId;

@Document(collection = "blog")
public class BlogEntry {

  @DocumentId
  private String id;

  /** Name of author of the blog entry. */
  private String authorName;

  /** Blog entry title. */
  private String title;

  /** date of publication. */
  private Date publishDate;

  /** date of last change. */
  private Date lastUpdate;

  /** Content on the blog entry. */
  private String content;

  /** set of tags for this content. */
  private List<String> tags;

  /** Number of comments. */
  private int commentsNumber;

  /** list of comments. */
  private List<BlogEntryComment> comments;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public int getCommentsNumber() {
    return commentsNumber;
  }

  public void setCommentsNumber(int commentsNumber) {
    this.commentsNumber = commentsNumber;
  }

  public List<BlogEntryComment> getComments() {
    return comments;
  }

  public void setComments(List<BlogEntryComment> comments) {
    this.comments = comments;
  }

}
