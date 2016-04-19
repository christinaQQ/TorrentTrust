package moe.cdn.cweb.dht.storage.annotations;

import com.google.inject.BindingAnnotation;
import net.tomp2p.dht.Storage;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates an injection request for a {@link Storage TomP2P backing store}
 * that is unvalidated and unprotected.
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface UnvalidatedStorage {
}
