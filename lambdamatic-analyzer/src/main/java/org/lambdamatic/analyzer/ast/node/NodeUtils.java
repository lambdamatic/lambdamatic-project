/*******************************************************************************
 * Copyright (c) 2015 Red Hat. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial Contribution
 *******************************************************************************/

package org.lambdamatic.analyzer.ast.node;

import java.io.IOException;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for all {@link Node} things.
 * 
 * @author Xavier Coulon
 * 
 */
public class NodeUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeUtils.class);

  private static final char EOS = (char) (-1);

  /**
   * Returns the given statement as a pretty formatted {@link String}
   * 
   * @param statement the statement
   * @return the pretty formatted representation of the given statement.
   */
  public static String prettyPrint(final Node statement) {
    try {
      final StringBuilder builder = new StringBuilder();
      int indent = 0;
      // reads the statement and insert CR/LF and indentations when
      // crossing curly brackets.
      StringReader reader = new StringReader(statement.toString());
      char current;
      while ((current = (char) reader.read()) != EOS) {
        if (current == '{') {
          builder.append(current).append('\n');
          indent++;
          for (int i = 0; i < indent; i++) {
            builder.append(' ');
          }
        } else if (current == '}') {
          builder.append('\n');
          indent--;
          for (int i = 0; i < indent; i++) {
            builder.append(' ');
          }
          builder.append(current);
        } else {
          builder.append(current);
        }
      }
      return builder.toString();
    } catch (IOException e) {
      LOGGER.warn("Failed to pretty print the given statement: " + statement, e);
      return null;
    }
  }

}

