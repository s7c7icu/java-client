package xland.s7c7icu.client.api;

import org.jetbrains.annotations.Nullable;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record FileInfo(String meta, String slug, byte[] password) {
    public FileInfo {
        password = password.clone();
    }

    public static FileInfo create(String uri, @Nullable DownloadContext context) {
        if (!uri.startsWith("s7c7icu://")) throw exception(uri);
        String[] split = uri.substring("s7c7icu://".length()).split("/", 3);
        if (split.length == 1) throw exception(uri);

        String slug = split[0];
        byte[] password = Base64.getDecoder().decode(split[1]);

        String meta;
        if (split.length == 2) {
            if (context == null || (meta = context.defaultMeta()) == null) {
                throw new IllegalArgumentException("Missing meta in s7c7icu URI: " + uri);
            }
        } else {
            meta = URLDecoder.decode(split[2], StandardCharsets.UTF_8);
        }

        return new FileInfo(meta, slug, password);
    }

    private static IllegalArgumentException exception(String s) {
        return new IllegalArgumentException("Illegal s7c7icu URI: " + s);
    }

    @Override
    public byte[] password() {
        return password.clone();
    }
}
