/**
 * 
 */
package org.lambdamatic.mongodb.converters;

import java.io.StringWriter;
import java.io.Writer;

import org.bson.BsonWriter;
import org.bson.json.JsonWriter;
import org.junit.Test;
import org.lambdamatic.FilterExpression;

import com.sample.User_;

/**
 * Testing the {@link LambdamaticFilterExpressionCodec}
 * @author xcoulon
 *
 */
public class LambdamaticDocumentCodecTest {
	
	
	@Test
	public void shouldEncodeFilterExpression() {
		// given
		final FilterExpression<User_> expr = u -> u.firstName.equals("John") && u.lastName.equals("Doe");
		final Writer stringWriter = new StringWriter();
		final BsonWriter bsonWriter = new JsonWriter(stringWriter); 
		// when
		new LambdamaticFilterExpressionCodec().encode(bsonWriter, expr, null); 
		// then
		
		
	}
}
