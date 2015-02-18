/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import java.io.IOException;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for all {@link ASTNode} things.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 * 
 */
public class ASTNodeUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ASTNodeUtils.class);

	/**
	 * Returns the given statement as a pretty formatted {@link String}
	 * 
	 * @param statement
	 *            the statement
	 * @return the pretty formatted representation of the given statement.
	 * @throws IOException
	 */
	public static String prettyPrint(final ASTNode statement) {
		try {
			final StringBuilder builder = new StringBuilder();
			int indent = 0;
			// reads the statement and insert CR/LF and indentations when
			// crossing curly brackets.
			StringReader reader = new StringReader(statement.toString());
			char c;
			final char EOS = (char)(-1);
			while ((c = (char) reader.read()) != EOS) {
				if (c == '{') {
					builder.append(c).append('\n');
					indent++;
					for (int i = 0; i < indent; i++) {
						builder.append(' ');
					}
				} else if (c == '}') {
					builder.append('\n');
					indent--;
					for (int i = 0; i < indent; i++) {
						builder.append(' ');
					}
					builder.append(c);
				} else {
					builder.append(c);
				}
			}
			return builder.toString();
		} catch (IOException e) {
			LOGGER.warn("Failed to pretty print the given statement: " + statement, e);
			return null;
		}
	}
	
}

