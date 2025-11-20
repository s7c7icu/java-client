package xland.s7c7icu.client.impl;

import xland.s7c7icu.client.api.FileFlag;
import xland.s7c7icu.client.spi.FileFlagProvider;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class V4FileFlagProvider implements FileFlagProvider {
    public static final FileFlag ZIP_INDEX = new FileFlagV4Impl("zipindex");
    public static final FileFlag FILENAME_PREPEND = new FileFlagV4Impl("filename-preappend");

    @Override
    public Collection<? extends FileFlag> values() {
        return List.of(ZIP_INDEX, FILENAME_PREPEND);
    }

    private static final class FileFlagV4Impl extends FileFlag {
        private final String id;

        private FileFlagV4Impl(String id) {
            Objects.requireNonNull(id, "id");
            this.id = id;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public int availableSinceSchema() {
            return 4;
        }
    }
}
