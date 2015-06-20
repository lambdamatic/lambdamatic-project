/**
 * 
 */
package org.lambdamatic.mongodb.utils;

import java.lang.reflect.Method;

import org.lambdamatic.mongodb.exceptions.ConversionException;

/**
 * Utility class for Reflection
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ReflectionUtils {

	/**
	 * 
	 * @param clazz
	 *            the source class
	 * @param methodName
	 *            the name of the method
	 * @param parameterTypes
	 *            the types of the parameters
	 * @return the method
	 * @throws ConversionException
	 *             if not such method exists in the given class
	 */
	public Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ConversionException("Failed to find method named '" + methodName + "' with parameter types "
					+ parameterTypes + " in class " + clazz.getName(), e);
		}
	}

}
