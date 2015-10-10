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

/**
 * @author xcoulon
 *
 */
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

  /** Number of comments */
  private int commentsNumber;

  /** list of comments. */
  private List<BlogEntryComment> comments;

  /**
   * @return the entry Id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the entry Id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the authorName
   */
  public String getAuthorName() {
    return authorName;
  }

  /**
   * @param authorName the authorName to set
   */
  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  /**
   * @return the publishDate
   */
  public Date getPublishDate() {
    return publishDate;
  }

  /**
   * @param publishDate the publishDate to set
   */
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

  /**
   * @return the tags
   */
  public List<String> getTags() {
    return tags;
  }

  /**
   * @param tags the tags to set
   */
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  /**
   * @return the commentsNumber
   */
  public int getCommentsNumber() {
    return commentsNumber;
  }

  /**
   * @param commentsNumber the commentsNumber to set
   */
  public void setCommentsNumber(int commentsNumber) {
    this.commentsNumber = commentsNumber;
  }

  /**
   * @return the comments
   */
  public List<BlogEntryComment> getComments() {
    return comments;
  }

  /**
   * @param comments the comments to set
   */
  public void setComments(List<BlogEntryComment> comments) {
    this.comments = comments;
  }

}
