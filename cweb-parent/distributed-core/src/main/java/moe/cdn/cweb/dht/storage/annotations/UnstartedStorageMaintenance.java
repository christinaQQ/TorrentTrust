package moe.cdn.cweb.dht.storage.annotations;

import com.google.inject.BindingAnnotation;
import net.tomp2p.dht.StorageLayer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates an injection request for a {@link StorageLayer} that has not been started yet.
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface UnstartedStorageMaintenance {
}
