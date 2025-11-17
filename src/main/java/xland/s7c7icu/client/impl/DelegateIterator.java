package xland.s7c7icu.client.impl;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("ClassCanBeRecord")
public final class DelegateIterator<T, B> implements Iterator<T> {
    private final Iterator<B> backing;
    private final Function<B, T> fromBacking;

    public DelegateIterator(Iterator<B> backing, Function<B, T> fromBacking) {
        Objects.requireNonNull(backing, "backing");
        Objects.requireNonNull(fromBacking, "fromBacking");
        this.backing = backing;
        this.fromBacking = fromBacking;
    }

    @Override
    public boolean hasNext() {
        return backing.hasNext();
    }

    @Override
    public T next() {
        return fromBacking.apply(backing.next());
    }

    @Override
    public void remove() {
        backing.remove();
    }
}
