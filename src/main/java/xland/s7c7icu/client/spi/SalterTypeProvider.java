package xland.s7c7icu.client.spi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xland.s7c7icu.client.Provider;
import xland.s7c7icu.client.api.EmptySalter;
import xland.s7c7icu.client.api.SalterType;

import java.util.Objects;

public interface SalterTypeProvider extends Provider<SalterType<?>> {
    @NotNull
    static SalterType<?> getFromId(@Nullable String id) {
        if (id == null) return EmptySalter.INSTANCE.type();

        SalterType<?> type = Provider.getValues(SalterTypeProvider.class).get(id);
        return Objects.requireNonNullElse(type, EmptySalter.INSTANCE.type());
    }
}
