package org.bytesparadise.lambdamatic.query;

import java.io.Serializable;

/**
 * Created by xcoulon on 12/16/13.
 */
@FunctionalInterface
public interface FilterExpression<T> extends Serializable {

    public boolean test(T t);
}
