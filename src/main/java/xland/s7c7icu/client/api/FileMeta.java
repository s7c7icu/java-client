package xland.s7c7icu.client.api;

import org.jetbrains.annotations.Nullable;
import xland.s7c7icu.client.api.json.JsonException;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.impl.FileMetaImpl;

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

    static FileMeta deserialize(JsonObject obj, DownloadContext context) throws JsonException {
        return FileMetaImpl.deserialize(obj, context);
    }
}
