/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lambdamatic.mongodb.Projection.include;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.plaf.ListUI;

import org.assertj.core.api.Condition;
import org.assertj.core.description.TextDescription;
import org.junit.Before;
import org.junit.Test;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.sample.BlogEntry;
import com.sample.BlogEntryCollection;
import com.sample.Foo;

/**
 * Testing the MongoDB Lambda-based Fluent API.
 * 
 * @author Xavier Coulon
 *
 */
public class MongoQueryTest extends MongoBaseTest {

  private BlogEntryCollection blogEntryCollection;

  public MongoQueryTest() {
    super(Foo.class);
  }

  @Before
  public void setup() throws UnknownHostException {
    this.blogEntryCollection = new BlogEntryCollection(getMongoClient(), DATABASE_NAME);
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindOneFoo() throws IOException {
    // when
    final BlogEntry blogEntry =
        blogEntryCollection.filter(e -> e.id.equals("1") && e.authorName.equals("jdoe")).first();
    // then
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getAuthorName().equals("jdoe");
      }
    });
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindFirstOfAll() throws IOException {
    // when
    final BlogEntry blogEntry =
        blogEntryCollection.filter(e -> e.id.equals("1") && e.authorName.equals("jdoe")).first();
    // then
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getAuthorName().equals("jdoe");
      }
    });
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindFirstOfAllWithSkip() throws IOException {
    // when
    final BlogEntry blogEntry =
        blogEntryCollection.filter(e -> e.authorName.equals("jdoe")).skip(1).first();
    // then
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getId().equals("2");
      }
    });
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindFirstOfAllWithSkipAndLimit() throws IOException {
    // when
    final List<BlogEntry> blogEntries =
        blogEntryCollection.filter(e -> e.authorName.equals("jdoe")).skip(1).limit(1).toList();
    // then
    assertThat(blogEntries).hasSize(1);
    final BlogEntry blogEntry = blogEntries.get(0);
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getId().equals("2");
      }
    });
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindOneCommentAuthor() throws IOException {
    // when
    final BlogEntry blogEntry = blogEntryCollection
        .filter(e -> e.comments.elementMatch(c -> c.author.equals("anonymous"))).first();
    // then
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getId().equals("3");
      }
    });
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindOneFooWithElementMatchBar() throws IOException {
    // when
    final BlogEntry blogEntry = blogEntryCollection
        .filter(e -> e.comments.elementMatch(c -> c.author.equals("anonymous"))).first();
    // then
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getId().equals("3");
      }
    });
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
  public void shouldFindOneFooWithFieldInclusionProjection() throws IOException {
    // when
    final BlogEntry blogEntry = blogEntryCollection.filter(e -> e.id.equals("1"))
        .projection(e -> include(e.id, e.authorName)).first();
    // then
    assertThat(blogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getId().equals("1") && blogEntry.getAuthorName().equals("jdoe")
            && blogEntry.getComments() == null && blogEntry.getContent() == null;
      }
    }.as(new TextDescription("only a 'id' and 'authorName' fields initialized")));
  }

  @Test
  @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
  public void shouldFindOneFooWithBinaryFieldInclusionProjection() throws IOException {
    // given
    final BlogEntry blogEntry = new BlogEntry();
    blogEntry.setId("1");
    blogEntry.setAuthorName("jdoe");
    final List<byte[]> photos = Arrays.asList(new byte[] {1, 2, 3, 4}, new byte[] {5, 6, 7, 8});
    blogEntry.setPhotos(photos);
    blogEntryCollection.add(blogEntry);
    // when
    final BlogEntry foundBlogEntry = blogEntryCollection.filter(e -> e.id.equals("1"))
        .projection(e -> include(e.id, e.authorName, e.photos)).first();
    // then
    assertThat(foundBlogEntry).isNotNull().has(new Condition<BlogEntry>() {
      @Override
      public boolean matches(final BlogEntry blogEntry) {
        return blogEntry.getId().equals("1") && blogEntry.getAuthorName().equals("jdoe")
            && blogEntry.getComments() == null && blogEntry.getContent() == null
            && Objects.deepEquals(blogEntry.getPhotos().toArray(), photos.toArray());
      }
    }.as(new TextDescription("only a 'id', 'authorName' and 'photos' fields initialized")));
  }
  
}
