package xland.s7c7icu.client.impl.salter;

import xland.s7c7icu.client.api.Salter;
import xland.s7c7icu.client.api.SalterType;
import xland.s7c7icu.client.spi.SalterTypeProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.random.RandomGenerator;

public final class BuiltinSalterTypeProvider implements SalterTypeProvider {
    @Override
    public Collection<? extends SalterType<?>> values() {
        // No need to include EmptySalter since it's the default
        return Collections.singleton(PostAppend.TYPE);
    }

    public static Salter random(RandomGenerator random) {
        return PostAppend.random(random);
    }
}
