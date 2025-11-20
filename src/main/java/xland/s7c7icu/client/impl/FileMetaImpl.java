package xland.s7c7icu.client.impl;

import org.json.JSONObject;
import xland.s7c7icu.client.Provider;
import xland.s7c7icu.client.SinceSchema;
import xland.s7c7icu.client.api.*;
import xland.s7c7icu.client.api.json.JsonElement;
import xland.s7c7icu.client.api.json.JsonException;
import xland.s7c7icu.client.api.json.JsonObject;
import xland.s7c7icu.client.impl.json.JsonElementImpl;
import xland.s7c7icu.client.spi.AlgorithmProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public record FileMetaImpl(
        @Override int schemaVersion,    // schema
        @Override String filename,      // filename
        @Override int size,             // size?: int | -1
        @Override List<Algorithm> algorithms,   // alg: string
        @Override Map<Hashing, String> hashes,  // hash: object
        @Override FileDataInfo data,    // data: object | FileDataInfo.EMPTY
        @Override Salter salter,        // salter: object{id?: string} | EmptySalter
        @Override Collection<FileFlag> flags    // flags: array<string>
) implements FileMeta {
    public static FileMetaImpl deserialize(JsonObject j, DownloadContext context) throws JsonException {
        JSONObject obj = ((JsonElementImpl.JsonObjectImpl) j).obj();
        final int maxSupportedSchema = context.maxSupportedSchema();
        Predicate<? super SinceSchema> isSchemaSupported = SinceSchema.filtersSupported(maxSupportedSchema);

        final int schema;
        final List<Algorithm> algorithms;
        final Map<Hashing, String> hashes;

        schema = j.get("schema").flatMap(JsonElement::asNumber).orElseThrow(JsonElementImpl.keyAbsent("schema")).intValue();
        if (schema < 0 || schema > maxSupportedSchema) {
            throw new JsonException("Schema version out of range: " + schema);
        }

        try {
            // algorithms
            String algString = obj.getString("alg");
            if (!algString.contains("aes")) throw new JsonException("Illegal algorithms: " + algString + ". 'aes' is absent.");

            Map<String, ? extends Algorithm> algorithmMap = Provider.getValues(AlgorithmProvider.class);
            algorithms = Arrays.stream(algString.split("\\+")).map(s -> {
                final Algorithm alg = algorithmMap.get(s);
                if (alg != null && isSchemaSupported.test(alg)) return alg;
                throw new JsonException("Unknown algorithm: " + s);
            }).toList();

            // hashes
            JSONObject hashObj = obj.getJSONObject("hash");
            if (hashObj.isEmpty()) throw new JsonException("There must be at least one hash");

            // fixme:
            //  In JS version (non-flow): a salter executes (byte[], Map<Hashing, String>) -> HashResult.
            //  However, we are implementing a flow. This requires
            //  (InputStream, Map<Hashing, String>) -> HasherInputStream, which sacrifices
            //  customization of HashingInputStream::matchesHash.
            //  As a special reminder, the arg0 `InputStream` should be preprocessed by FileFlag[filename-preappend]
            //
            //  Also remind that an output stream version, i.e. (OutputStream).use -> Map<Hashing, String>
            //  is required.
        } catch (org.json.JSONException e) {
            throw new JsonException(e.getMessage());
        }
    }
}
