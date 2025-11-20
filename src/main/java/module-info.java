import xland.s7c7icu.client.impl.BuiltinAlgorithm;
import xland.s7c7icu.client.impl.BuiltinHashing;
import xland.s7c7icu.client.impl.V4FileFlagProvider;
import xland.s7c7icu.client.impl.salter.BuiltinSalterTypeProvider;
import xland.s7c7icu.client.internal.FieldsAreNonnullByDefault;
import xland.s7c7icu.client.internal.MethodsReturnNonnullByDefault;
import xland.s7c7icu.client.internal.ParametersAreNonnullByDefault;

import xland.s7c7icu.client.spi.*;

@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
open module s7c7icu.javaclient {
    requires static org.jetbrains.annotations;
    requires java.net.http;
    requires static org.json;
    requires static software.pando.crypto.nacl;

    exports xland.s7c7icu.client;
    exports xland.s7c7icu.client.api;
    exports xland.s7c7icu.client.api.json;
    exports xland.s7c7icu.client.spi;

    uses AlgorithmProvider;
    uses HashingProvider;
    uses SalterTypeProvider;
    uses FileFlagProvider;

    provides AlgorithmProvider with BuiltinAlgorithm.Provider;
    provides HashingProvider with BuiltinHashing.Provider;
    provides SalterTypeProvider with BuiltinSalterTypeProvider;
    provides FileFlagProvider with V4FileFlagProvider;
}