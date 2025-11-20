package xland.s7c7icu.client.api;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public interface DownloadContext {
    int maxSupportedSchema();

    boolean allowsS7c7icuUri();

    OutputStream createOutputStream(String filename) throws IOException;

    @Nullable String defaultMeta();

    // experimental?
//    CompletableFuture<Void> download(FileInfo fileInfo, OutputStream dest) throws IOException;
    CompletableFuture<InputStream> openStream(FileInfo fileInfo) throws IOException;

    Duration TIMEOUT = Duration.ofSeconds(10L);

    HttpClient getHttpClient();

    default String getUserAgent() {
        return "s7c7icu/java-client/" + FileMeta.MAX_SUPPORTED_SCHEMA;
    }
}
