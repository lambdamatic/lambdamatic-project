/**
 * 
 */
package org.lambdamatic.jpa.apt;

import com.github.mustachejava.Mustache;

/**
 * An import statement, used in the {@link Mustache} template
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public class ImportStatement {

	private final String name;
	
	public ImportStatement(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportStatement other = (ImportStatement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}

