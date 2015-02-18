/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.lambdamatic.analyzer.exception.AnalyzeException;

/**
 * Utility class to around Reflection API
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ReflectionUtils {

	/**
	 * Finds and returns the closest matching method with the given name and given argument types on the given {@code source} Object
	 * 
	 * @param methodName
	 *            the name of the declared method to find
	 * @param source
	 *            the Class instance on which the method should be found, or the Class itself if the method to find is static
	 * @param argTypes
	 *            the argument types
	 * @return the {@link Method}
	 * @throws AnalyzeException if no method matched
	 */
	public static Method getMethodToInvoke(final Object source, final String methodName, final Class<?>... argTypes) {
		if (source instanceof Class) {
			return getMethodToInvoke((Class<?>) source, methodName, argTypes);
		}
		return getMethodToInvoke(source.getClass(), methodName, argTypes);
	}

	private static Method getMethodToInvoke(final Class<?> clazz, final String methodName, final Class<?>... argTypes) {
		final Method matchingMethod = MethodUtils.getMatchingAccessibleMethod(clazz, methodName, argTypes);
		if(matchingMethod == null) {
			throw new AnalyzeException("Could not find a method named '" + methodName + "' in class " + clazz.getName() + " with parameters matching " + argTypes);
		}
		return matchingMethod;
	}

	/**
	 * Finds and returns the method with the given name and given argument types on the given {@code source} Object
	 * 
	 * @param methodName
	 *            the name of the declared method to find
	 * @param source
	 *            the source (class instance) on which the method should be found, or {@code null} if the method to find is static
	 * @param argTypes
	 *            the argument types
	 * @return the {@link Method}
	 * @throws NoSuchMethodException
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static Field getFieldToInvoke(final Object source, final String fieldName) throws NoSuchFieldException, SecurityException {
		if (source instanceof Class) {
			return getFieldToInvoke((Class<?>) source, fieldName);
		}
		return getFieldToInvoke(source.getClass(), fieldName);
	}

	// FIXME: we should cover all cases: method in superclass and all variants of superclass of any/all arguments.
	private static Field getFieldToInvoke(final Class<?> clazz, final String fieldName) throws  NoSuchFieldException,
			SecurityException {
		return clazz.getField(fieldName);
	}
	
	
	public static <T> T[] cast(final Object value, final Class<T> componentType) {
		final Object[] values = (Object[])value;
		@SuppressWarnings("unchecked")
		final T[] array = (T[]) Array.newInstance(componentType, values.length);
		System.arraycopy(values, 0, array, 0, values.length);
		return array;
	}
	
}
