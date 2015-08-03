package org.lambdamatic.mongodb.apt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

/**
 * A field type to be used in the class templates. Provides the simple Java type name along with all types to declare as
 * import statements.
 * <p>
 * Eg: field of type <code>List&lt;Foo&gt;</code> requires imports of <code>java.util.List</code> and
 * <code>com.sample.Foo</code>.
 * </p>
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class FieldType {

	/** the simple name of the Java field type. */
	private final String simpleName;

	/** all Java types to declare in the imports. */
	private final Set<String> requiredTypes = new HashSet<>();

	/**
	 * Constructor for a simple type
	 * 
	 * @param javaType
	 *            the Java field type.
	 */
	public FieldType(final Class<?> javaType) {
		this(javaType, Collections.emptyList());
	}

	/**
	 * Constructor for a parameterized type
	 * 
	 * @param javaType
	 *            the Java field type.
	 * @param parameterTypes
	 *            the Java parameter types.
	 */
	public FieldType(final Class<?> javaType, final Object... parameterTypes) {
		this(javaType, Arrays.asList(parameterTypes));
	}

	/**
	 * Constructor for a parameterized type
	 * 
	 * @param javaType
	 *            the Java field type.
	 * @param parameterTypes
	 *            the Java parameter types.
	 */
	public FieldType(final Class<?> javaType, final Collection<Object> parameterTypes) {
		final StringBuilder simpleNameBuilder = new StringBuilder();
		simpleNameBuilder.append(javaType.getSimpleName());
		if(!javaType.isPrimitive()) {
			this.requiredTypes.add(javaType.getName());
		}
		if (!parameterTypes.isEmpty()) {
			simpleNameBuilder.append('<');
			parameterTypes.stream().forEach(p -> {
				if (p instanceof String) {
					final String fullyQualifiedName = (String) p;
					simpleNameBuilder.append(ClassUtils.getShortClassName(fullyQualifiedName)).append(", ");
					this.requiredTypes.add(fullyQualifiedName);
				} else if (p instanceof FieldType) {
					final FieldType fieldType = (FieldType) p;
					simpleNameBuilder.append(fieldType.getSimpleName()).append(", ");
					this.requiredTypes.addAll(fieldType.getRequiredTypes());
				}
			});
			// little hack: there's an extra ", " sequence that needs to be remove from the simpleNameBuilder
			simpleNameBuilder.delete(simpleNameBuilder.length() - 2, simpleNameBuilder.length());
			simpleNameBuilder.append('>');
		}
		this.simpleName = simpleNameBuilder.toString();
	}

	/**
	 * Constructor for a parameterized type
	 * 
	 * @param javaTypeName
	 *            the fully qualified name of the Java field type.
	 */
	public FieldType(final String javaTypeName) {
		this.simpleName = ClassUtils.getShortCanonicalName(javaTypeName);
		this.requiredTypes.add(javaTypeName);
	}

	/**
	 * @return the simple name of the Java field type.
	 */
	public String getSimpleName() {
		return simpleName;
	}

	/**
	 * @return all the Java types to declare in the imports
	 */
	public Set<String> getRequiredTypes() {
		return requiredTypes;
	}
}