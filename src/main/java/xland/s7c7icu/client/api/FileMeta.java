package xland.s7c7icu.client.api;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FileMeta {
    int MAX_SUPPORTED_SCHEMA = 4;

    int schemaVersion();

    @Nullable String filename();

    int size();

    List<Algorithm> algorithms();

    Map<Hashing, String> hashes();

    FileDataInfo data();

    Salter salter();

    Collection<FileFlag> flags();
}
