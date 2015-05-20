/**
 * 
 */
package org.lambdamatic.mongodb.metadata.ext;

import org.lambdamatic.mongodb.metadata.QueryArray;

/**
 * {@link QueryArray} extension for document fields that are arrays of collections of {@link String}
 * 
 * @author Xavier Coulon <xcoulon@redhat.com>
 *
 */
public interface QStringArray extends QString, QueryArray<QString> {

}
