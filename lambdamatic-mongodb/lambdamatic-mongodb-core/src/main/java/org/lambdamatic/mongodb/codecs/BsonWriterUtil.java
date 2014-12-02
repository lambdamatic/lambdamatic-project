/**
 * 
 */
package org.lambdamatic.mongodb.codecs;

import org.bson.BsonWriter;

/**
 * Utility class to write in a {@link BsonWriter}
 * @author xcoulon
 *
 */
public class BsonWriterUtil {
	
	/**
	 * Writes the given key/value pair
	 * @param writer the {@link BsonWriter} to write into.
	 * @param key the key 
	 * @param value the value
	 */
	//FIXME: need to complete with more 'instanceof', and support for Enumerations, too.
	public static void write(final BsonWriter writer, final String key, final Object value) {
		if(value == null) {
			writer.writeNull(key);
		}
		else if(value instanceof Integer) {
			writer.writeInt32(key, (Integer)value);
		}
		else if(value instanceof Long) {
			writer.writeInt64(key, (Long)value);
		}
		else if(value instanceof String) {
			writer.writeString(key, (String)value);
		}
	}

}
