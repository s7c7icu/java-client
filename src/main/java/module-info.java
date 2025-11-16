import xland.s7c7icu.client.impl.BuiltinAlgorithm;
import xland.s7c7icu.client.impl.BuiltinHashing;
import xland.s7c7icu.client.internal.FieldsAreNonnullByDefault;
import xland.s7c7icu.client.internal.MethodsReturnNonnullByDefault;
import xland.s7c7icu.client.internal.ParametersAreNonnullByDefault;

import xland.s7c7icu.client.spi.*;

@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
open module s7c7icu.javaclient {
    requires static org.jetbrains.annotations;
//    requires static org.bouncycastle.provider;
    requires java.net.http;
    requires static org.json;

    exports xland.s7c7icu.client;
    exports xland.s7c7icu.client.api;
    exports xland.s7c7icu.client.spi;

    uses AlgorithmProvider;
    uses HashingProvider;

    provides AlgorithmProvider with BuiltinAlgorithm.Provider;
    provides HashingProvider with BuiltinHashing.Provider;
}