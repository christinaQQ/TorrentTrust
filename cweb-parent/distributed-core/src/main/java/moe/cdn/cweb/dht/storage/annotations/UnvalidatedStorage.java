package moe.cdn.cweb.dht.storage.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import net.tomp2p.dht.Storage;

/**
 * Indicates an injection request for a {@link Storage TomP2P backing store}
 * that is unvalidated and unprotected.
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface UnvalidatedStorage {
}
