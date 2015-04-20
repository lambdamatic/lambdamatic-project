/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.List;

import org.lambdamatic.analyzer.ast.node.CapturedArgument;
import org.lambdamatic.analyzer.ast.node.CapturedArgumentRef;
import org.objectweb.asm.Type;

/**
 * Info about the nested Lambda Expression location.
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 * @see SerializedLambda
 */
public class EmbeddedSerializedLambdaInfo extends SerializedLambdaInfo {

	/**
	 * Full constructor
	 * 
	 * @param implClassName
	 *            the fully qualified name of the implementation class.
	 * @param implMethodName
	 *            the name of the implementation method.
	 * @param implMethodDesc
	 *            the signature of the implementation method.
	 * @param capturedArgumentRefs
	 *            the (potentially empty) list of references to some {@link CapturedArgument}.
	 */
	public EmbeddedSerializedLambdaInfo(final String implClassName, final String implMethodName,
			final String implMethodDesc, final List<CapturedArgumentRef> capturedArgumentRefs,
			final List<CapturedArgument> capturedArguments) {
		super(Type.getObjectType(implClassName).getClassName(), implMethodName, implMethodDesc,
				getRelevantCaptureArguments(capturedArguments, capturedArgumentRefs));
	}

	/**
	 * @param capturedArguments
	 *            all the actual captured arguments
	 * @param capturedArgumentRefs
	 *            the references to the captured arguments to keep
	 * @return a {@link List} of {@link CapturedArgument} which only contains the values mentioned in the given
	 *         capturedArgumentRefs
	 */
	static List<CapturedArgument> getRelevantCaptureArguments(final List<CapturedArgument> capturedArguments,
			final List<CapturedArgumentRef> capturedArgumentRefs) {
		final List<CapturedArgument> relevantArguments = new ArrayList<CapturedArgument>();
		capturedArgumentRefs.forEach(ref -> relevantArguments.add(capturedArguments.get(ref.getArgumentIndex())));
		return relevantArguments;
	}

}
