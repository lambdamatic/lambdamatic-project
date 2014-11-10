/**
 * 
 */
package org.lambdamatic.analyzer.ast.node;

import org.objectweb.asm.tree.LabelNode;


/**
 * @author xcoulon
 *
 */
public abstract class ASTNode {
	
	private final String label;
	
	/**
	 * Default constructor
	 */
	public ASTNode() {
		this.label = null;
	}

	/**
	 * Constructor with a given {@link LabelNode}
	 * @param label
	 */
	public ASTNode(final LabelNode labelNode) {
		this.label = (labelNode != null) ? labelNode.getLabel().toString() : null;
	}
	
	public String getLabel() {
		return label;
	}
	
	protected boolean hasLabel() {
		return getLabel() != null;
	}

	/**
	 * @return the length of the given node in term of number of bytecode instructions (including {@link LabelNode}s) 
	 */
	public int getNumberOfBytecodeInstructions() {
		return 1 + (hasLabel() ? 1 : 0);
	}

}

