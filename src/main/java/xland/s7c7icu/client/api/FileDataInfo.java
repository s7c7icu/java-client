package xland.s7c7icu.client.api;

import xland.s7c7icu.client.api.json.JsonElement;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.internal.SneakyThrow;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public sealed interface FileDataInfo {
    CompletableFuture<Void> downloadTo(OutputStream outputStream, DownloadContext context) throws IOException;

    String fieldName();

    record Raw(String value) implements FileDataInfo {
        @Override
        public CompletableFuture<Void> downloadTo(OutputStream outputStream, DownloadContext context) throws IOException {
            byte[] asUtf8Bytes = this.value().getBytes(StandardCharsets.UTF_8);
            outputStream.write(asUtf8Bytes);
            return new CompletableFuture<>();
        }

        @Override
        public String fieldName() {
            return "raw";
        }
    }

    Raw EMPTY = new Raw("");

    record Base64(String value) implements FileDataInfo {
        @Override
        public CompletableFuture<Void> downloadTo(OutputStream outputStream, DownloadContext context) throws IOException {
            byte[] asBytes = java.util.Base64.getDecoder().decode(this.value());
            outputStream.write(asBytes);
            return new CompletableFuture<>();
        }

        @Override
        public String fieldName() {
            return "base64";
        }
    }

    record Fetch(String value) implements FileDataInfo {
        @Override
        public CompletableFuture<Void> downloadTo(OutputStream outputStream, DownloadContext context) throws IOException {
            String value = this.value();
            if (value.startsWith("s7c7icu://")) {
                if (!context.allowsS7c7icuUri()) {
                    throw new IllegalArgumentException("s7c7icu URIs are not allowed here, but it's present: " + value);
                }
                FileInfo fileInfo = FileInfo.create(value, context);
                return context.download(fileInfo, outputStream);
            } else {
                URI uri = URI.create(value);
                return context.getHttpClient().sendAsync(
                        HttpRequest.newBuilder(uri)
                                .header("User-Agent", context.getUserAgent())
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofInputStream()
                ).thenAcceptAsync(response -> {
                    try {
                        if (isStatusOk(response.statusCode())) {
                            try (InputStream inputStream = response.body()) {
                                inputStream.transferTo(outputStream);
                            }
                        } else {
                            throw new IOException("Failed to fetch " + uri);
                        }
                    } catch (IOException ex) {
                        throw SneakyThrow.sneakyThrow(ex);
                    }
                });
            }
        }

        private static boolean isStatusOk(int statusCode) {
            return statusCode >= 200 && statusCode <= 299;
        }

        @Override
        public String fieldName() {
            return "fetch";
        }
    }

    static FileDataInfo fromJsonObject(JsonObject obj) {
        return Optional.<FileDataInfo>empty()
                .or(() -> obj.get("fetch").flatMap(JsonElement::asString).map(Fetch::new))
                .or(() -> obj.get("base64").flatMap(JsonElement::asString).map(Base64::new))
                .or(() -> obj.get("raw").flatMap(JsonElement::asString).map(Raw::new))
                .orElse(EMPTY);
    }
}
