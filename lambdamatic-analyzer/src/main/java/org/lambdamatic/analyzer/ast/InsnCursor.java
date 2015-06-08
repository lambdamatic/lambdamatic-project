/**
 * 
 */
package org.lambdamatic.analyzer.ast;

import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * A cursor to navigate on an {@link InsnList}
 * 
 * @author xcoulon
 *
 */
class InsnCursor {

	/** the {@link InsnList} on which this {@link InsnCursor} operates. */
	private final InsnList instructions;
	
	/** the underlying {@link ListIterator} on the {@link InsnList}. */
	private final ListIterator<AbstractInsnNode> iterator;

	/** The current instruction. */
	private AbstractInsnNode currentInstruction = null;

	/** the current position of the cursor in the {@link InsnList}. */
	private int currentPosition = -1;
	
	/** the labels to locate specific instructions. */
	private final Map<String, AbstractInsnNode> labels;
	
	/**
	 * Constructor
	 * 
	 * @param instructions
	 *            the {@link InsnList} to iterate on.
	 *            @param labels the labels to locate specific instructions.
	 */
	@SuppressWarnings("unchecked")
	public InsnCursor(final InsnList instructions, final Map<String, AbstractInsnNode> labels) {
		this.instructions = instructions;
		this.iterator = instructions.iterator();
		this.labels = labels;
	}

	/**
	 * Returns {@code true} if the underlying list iterator has more {@link AbstractInsnNode}s
	 * when traversing the {@link InsnList} in the reverse direction. (In other words,
	 * returns {@code true} if {@link #previous} would return an {@link AbstractInsnNode} rather
	 * than throwing an exception.)
	 *
	 * @return {@code true} if the underlying list iterator has more {@link AbstractInsnNode}s
	 *         when traversing the {@link InsnList} in the reverse direction
	 */
	public boolean hasPrevious() {
		return iterator.hasPrevious();
	}

	/**
	 * Returns the previous {@link AbstractInsnNode} in the underlying list and moves the cursor
	 * position backwards. This method may be called repeatedly to iterate
	 * through the {@link InsnList} backwards, or intermixed with calls to {@link #next} to
	 * go back and forth. (Note that alternating calls to {@code next} and
	 * {@code previous} will return the same {@link AbstractInsnNode} repeatedly.)
	 *
	 * @return the previous element in the {@link InsnList}
	 * @throws NoSuchElementException
	 *             if the underlying iterator has no previous {@link AbstractInsnNode}
	 */
	public AbstractInsnNode getPrevious() {
		this.currentInstruction = iterator.previous();
		this.currentPosition --;
		return this.currentInstruction;
	}

	/**
	 * Moves to the previous {@link AbstractInsnNode} in the underlying list and moves the cursor
	 * position backwards. This method may be called repeatedly to iterate
	 * through the {@link InsnList} backwards, or intermixed with calls to {@link #next} to
	 * go back and forth. (Note that alternating calls to {@code next} and
	 * {@code previous} will return the same {@link AbstractInsnNode} repeatedly.)
	 *
	 * @return {@code this}, by convenience. 
	 * @throws NoSuchElementException
	 *             if the underlying iterator has no previous {@link AbstractInsnNode}
	 */
	public InsnCursor previous() {
		getPrevious();
		return this;
	}

	/**
	 * Returns {@code true} if the underlying list iterator has more {@link AbstractInsnNode}s
	 * when traversing the list in the forward direction. (In other words,
	 * returns {@code true} if {@link #next} would return an {@link AbstractInsnNode} rather than
	 * throwing an exception.)
	 *
	 * @return {@code true} if the underlying list iterator has more {@link AbstractInsnNode}s
	 *         when traversing the list in the forward direction
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * Returns the next {@link AbstractInsnNode} in the list and advances the cursor position.
	 * This method may be called repeatedly to iterate through the {@link InsnList}, or
	 * intermixed with calls to {@link #previous} to go back and forth. (Note
	 * that alternating calls to {@code next} and {@code previous} will return
	 * the same {@link AbstractInsnNode} repeatedly.)
	 *
	 * @return the next {@link AbstractInsnNode} in the list
	 * @throws NoSuchElementException
	 *             if the iteration has no next element
	 */
	public AbstractInsnNode getNext() {
		this.currentInstruction = iterator.next(); 
		this.currentPosition++;
		return this.currentInstruction;
	}

	/**
	 * Moves to the next {@link AbstractInsnNode} in the list and advances the cursor position.
	 * This method may be called repeatedly to iterate through the {@link InsnList}, or
	 * intermixed with calls to {@link #previous} to go back and forth. (Note
	 * that alternating calls to {@code next} and {@code previous} will return
	 * the same {@link AbstractInsnNode} repeatedly.)
	 *
	 * @return {@code this}, by convenience. 
	 * @throws NoSuchElementException
	 *             if the iteration has no next element
	 */
	public InsnCursor next() {
		if(hasNext()) {
			getNext();
		} 
		else {
			this.currentInstruction = null;
		}
		return this;
	}

	/**
	 * Moves to the first {@link AbstractInsnNode} in the list having the given {@code label}.
	 *
	 * @param label the target label to reach
	 * @return {@code this}, by convenience. 
	 * @throws NoSuchElementException
	 *             if the iteration has no next element
	 */
	public InsnCursor move(final Label label) {
		final AbstractInsnNode targetInstruction = labels.get(label.toString());
		if(this.instructions.indexOf(targetInstruction) > this.currentPosition) {
			while(this.currentInstruction != targetInstruction) {
				next();
			}
		} else {
			while(this.currentInstruction != targetInstruction) {
				previous();
			}
		}
		return this;
	}
	
	/**
	 * Returns {@code true} if the current instruction exists, {@code false}
	 * otherwise. A {@code false} result means that either the cursor did not
	 * start yet, or it reached the end of the underlying {@link InsnList}.
	 * 
	 * @return {@code true} if the current instruction exists, {@code false}
	 *         otherwise.
	 */
	public boolean hasCurrent() {
		return this.currentInstruction != null;
	}
	
	/**
	 * Returns the current {@link AbstractInsnNode} or {@code null} if the
	 * cursor was not moved forward yet.
	 * 
	 * @return the current {@link AbstractInsnNode} in the list
	 */
	public AbstractInsnNode getCurrent() {
		return currentInstruction;
	}
	
	/**
	 * Duplicates this {@link InsnCursor}, returning a copy at the same position on the underlying {@link InsnList}.
	 * @return a copy of this cursor.
	 */
	public InsnCursor duplicate() {
		final InsnCursor duplicateCursor = new InsnCursor(this.instructions, this.labels);
		while(duplicateCursor.currentPosition < this.currentPosition && duplicateCursor.hasNext()) {
			duplicateCursor.next();
		}
		return duplicateCursor;
	}

}
