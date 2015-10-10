/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights
 * reserved. This program is made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.lambdamatic.mongodb.apt.testutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Xavier Coulon
 *
 */
public class FileAssertion extends AbstractAssert<FileAssertion, File> {

  /** The file content. */
  private final String content;

  private FileAssertion(final File actual) throws FileNotFoundException, IOException {
    super(actual, FileAssertion.class);
    this.content = IOUtils.toString(new FileInputStream(actual));
  }

  public static FileAssertion assertThat(final File actual)
      throws FileNotFoundException, IOException {
    return new FileAssertion(actual);
  }

  public static FileAssertion assertThat(final String first, String... more)
      throws FileNotFoundException, IOException {
    final File file = FileSystems.getDefault().getPath(first, more).toFile();
    return new FileAssertion(file);
  }

  public FileAssertion doesNotContain(final String unwantedContent) {
    isNotNull();
    if (content.contains(unwantedContent)) {
      failWithMessage("Did not want file <%s> to contain <%s> but it did.",
          actual.getAbsolutePath(), unwantedContent);
    }
    return this;
  }

}

