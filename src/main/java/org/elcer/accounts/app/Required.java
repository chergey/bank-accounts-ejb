package org.elcer.accounts.app;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used to specify required params for the request method
 * @see RequiredParamResourceFilterFactory
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Required {
    String[] value();
}