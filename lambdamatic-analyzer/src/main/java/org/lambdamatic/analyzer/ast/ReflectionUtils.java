/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Utility class to around Reflection API
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ReflectionUtils {

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
	 */
	public static Method getMethodToInvoke(final Object source, final String methodName, final List<Class<?>> argTypes)
			throws NoSuchMethodException {
		if (source instanceof Class) {
			return getMethodToInvoke((Class<?>) source, methodName, argTypes);
		}
		return getMethodToInvoke(source.getClass(), methodName, argTypes);
	}

	// FIXME: we should cover all cases: method in superclass and all variants of superclass of any/all arguments.
	private static Method getMethodToInvoke(final Class<?> clazz, final String methodName, final List<Class<?>> argTypes)
			throws NoSuchMethodException {
		return clazz.getDeclaredMethod(methodName, argTypes.toArray(new Class<?>[argTypes.size()]));
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
	
}
