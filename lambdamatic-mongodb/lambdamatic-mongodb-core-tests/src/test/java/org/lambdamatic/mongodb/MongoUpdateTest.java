/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.mongodb;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.sample.BlogEntry;
import com.sample.BlogEntryCollection;
import com.sample.BlogEntryComment;

/**
 * Testing the Update operations
 * 
 * @author Xavier Coulon
 *
 */
public class MongoUpdateTest extends MongoBaseTest {

  private BlogEntryCollection blogEntryCollection;

  public MongoUpdateTest() {
    super(BlogEntry.class);
  }

  @Before
  public void setup() {
    this.blogEntryCollection = new BlogEntryCollection(getMongoClient(), DATABASE_NAME);
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  @ShouldMatchDataSet()
  public void shouldReplaceDocument() {
    // given
    final BlogEntry blogEntry =
        blogEntryCollection.filter(e -> e.id.equals("1") && e.authorName.equals("jdoe")).first();
    Assertions.assertThat(blogEntry).isNotNull();
    blogEntry.setAuthorName("John Doe");
    blogEntry.setContent("Updating documents...");
    final GregorianCalendar gregorianCalendar = new GregorianCalendar(2015, 05, 07, 16, 40, 0);
    gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    final Date publishDate = gregorianCalendar.getTime();
    blogEntry.setPublishDate(publishDate);
    final List<String> tags = Arrays.asList("doc", "update");
    blogEntry.setTags(tags);
    // when
    blogEntryCollection.replace(blogEntry);
    // then... let NoSqlUnit perform post-update assertions using the file given in the
    // @ShouldMatchDataSet
    // annotation
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  @ShouldMatchDataSet()
  public void shouldUpdateDocument() {
    // given
    final BlogEntry blogEntry = blogEntryCollection.filter(e -> e.id.equals("1"))
        .projection(e -> Projection.include(e.authorName, e.title, e.publishDate)).first();
    final GregorianCalendar gregorianCalendar = new GregorianCalendar(2015, 05, 07, 16, 40, 0);
    gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    final Date publishDate = gregorianCalendar.getTime();

    final BlogEntryComment comment =
        new BlogEntryComment("anonymous", publishDate, "lorem ipsum! what else ?");
    Assertions.assertThat(blogEntry).isNotNull();
    // when
    final Date now = gregorianCalendar.getTime();
    blogEntryCollection.filter(e -> e.id.equals("1")).forEach(e -> {
      e.lastUpdate = now;
      e.commentsNumber++;
      e.comments.push(comment);
    });

  }

}
