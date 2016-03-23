package moe.cdn.cweb.examples;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.junit.Test;

import java.util.Optional;

public class GuiceExploratoryTest {
    private static final String VALUE_ASSISTED_NAME = "value";

    @Test
    public void instantiateFactory() {
        Injector injector = Guice.createInjector(new Module());

        AoeuFactory aoeuFactory = injector.getInstance(Key.get(new TypeLiteral<AoeuFactory<String>>() {}));
        AoeuInterface<String> aoeu = aoeuFactory.create(Optional.of("aoeu"));

        System.out.println(aoeu.getValue());
    }

    public static class Module extends AbstractModule {
        @Override
        protected void configure() {
            install(new FactoryModuleBuilder()
                    .implement(
                            new TypeLiteral<AoeuInterface<String>>() {},
                            new TypeLiteral<AoeuClass<String>>() {})
                    .build(new TypeLiteral<AoeuFactory<String>>() {}));
        }
    }

    public static interface AoeuInterface<T> {
        public T getValue();
    }

    public static class AoeuClass<T> implements AoeuInterface<T> {
        private final T value;

        @Inject
        public AoeuClass(@Assisted(VALUE_ASSISTED_NAME) final Optional<T> value) {
            this.value = value.get();
        }

        public T getValue() {
            return value;
        }
    }

    public static interface AoeuFactory<T> {
        public AoeuInterface<T> create(
                @Assisted(VALUE_ASSISTED_NAME) final Optional<T> value);
    }
}