package xland.s7c7icu.client.api;


public interface Salter {
    SalterType<?> type();

    // outside the Hashing wrapper
    HasherInputStream wrap(HasherInputStream original);
    HasherOutputStream wrap(HasherOutputStream original);
}
