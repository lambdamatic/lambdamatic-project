/**
 * 
 */
package org.lambdamatic.testutils;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Assert;
import org.lambdamatic.SerializablePredicate;
import org.lambdamatic.analyzer.ArrayUtil;

import com.sample.model.TestPojo;

/**
 * Utility class to provide a quick access to the most commonly used Java {@link Method} in the unit tests.
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class JavaMethods {

	public static Method Object_equals;
	public static Method TestPojo_elementMatch;
	public static Method ArrayUtil_toArray;
	public static Method TestPojo_getStringValue;
	public static Method TestPojo_getPrimitiveBooleanValue;
	public static Method TestPojo_getBooleanValue;
	public static Method TestPojo_getPrimitiveByteValue;
	public static Method TestPojo_getByteValue;
	public static Method TestPojo_getPrimitiveShortValue;
	public static Method TestPojo_getShortValue;
	public static Method TestPojo_getPrimitiveIntValue;
	public static Method TestPojo_getIntegerValue;
	public static Method TestPojo_getPrimitiveLongValue;
	public static Method TestPojo_getLongValue;
	public static Method TestPojo_getPrimitiveCharValue;
	public static Method TestPojo_getCharacterValue;
	public static Method TestPojo_getPrimitiveFloatValue;
	public static Method TestPojo_getFloatValue;
	public static Method TestPojo_getPrimitiveDoubleValue;
	public static Method TestPojo_getDoubleValue;
	public static Method TestPojo_getEnumPojo;
	public static Method TestPojo_matches_String;
	public static Method TestPojo_matches_TestPojo;
	public static Method List_size;
	public static Method Character_valueOf;
	
	static {
		try {
			Object_equals = Object.class.getMethod("equals", Object.class);
			TestPojo_elementMatch = TestPojo.class.getMethod("elementMatch", SerializablePredicate.class);
			ArrayUtil_toArray = ArrayUtil.class.getMethod("toArray", Object[].class);
			TestPojo_getStringValue = TestPojo.class.getMethod("getStringValue");
			TestPojo_getPrimitiveBooleanValue = TestPojo.class.getMethod("getPrimitiveBooleanValue");
			TestPojo_getBooleanValue = TestPojo.class.getMethod("getBooleanValue");
			TestPojo_getPrimitiveByteValue = TestPojo.class.getMethod("getPrimitiveByteValue");
			TestPojo_getByteValue = TestPojo.class.getMethod("getByteValue");
			TestPojo_getPrimitiveShortValue = TestPojo.class.getMethod("getPrimitiveShortValue");
			TestPojo_getShortValue = TestPojo.class.getMethod("getShortValue");
			TestPojo_getPrimitiveIntValue = TestPojo.class.getMethod("getPrimitiveIntValue");
			TestPojo_getIntegerValue = TestPojo.class.getMethod("getIntegerValue");
			TestPojo_getPrimitiveLongValue = TestPojo.class.getMethod("getPrimitiveLongValue");
			TestPojo_getLongValue = TestPojo.class.getMethod("getLongValue");
			TestPojo_getPrimitiveCharValue = TestPojo.class.getMethod("getPrimitiveCharValue");
			TestPojo_getCharacterValue = TestPojo.class.getMethod("getCharacterValue");
			TestPojo_getPrimitiveFloatValue = TestPojo.class.getMethod("getPrimitiveFloatValue");
			TestPojo_getFloatValue = TestPojo.class.getMethod("getFloatValue");
			TestPojo_getPrimitiveDoubleValue = TestPojo.class.getMethod("getPrimitiveDoubleValue");
			TestPojo_getDoubleValue = TestPojo.class.getMethod("getDoubleValue");
			TestPojo_getEnumPojo = TestPojo.class.getMethod("getEnumPojo");
			TestPojo_matches_String = TestPojo.class.getMethod("matches", String[].class);
			TestPojo_matches_TestPojo = TestPojo.class.getMethod("matches", TestPojo[].class);
			List_size = List.class.getMethod("size");
			Character_valueOf = Character.class.getMethod("valueOf", char.class);

		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			Assert.fail("Failed to retrieve Java method");
		}
	}
	
}
