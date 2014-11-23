package org.lambdamatic;

import java.io.Serializable;

@FunctionalInterface
public interface FilterExpression<T> extends Serializable {

    public boolean test(T t);
    
}

