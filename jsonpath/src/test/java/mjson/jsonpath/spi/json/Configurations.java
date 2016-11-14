package mjson.jsonpath.spi.json;

import com.jayway.jsonpath.Configuration;
import mjson.jsonpath.spi.mapper.MjsonMappingProvider;

import java.util.Arrays;

public class Configurations {

    public static final Configuration MJSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new MjsonMappingProvider())
            .jsonProvider(new MjsonJsonProvider())
            .build();

    public static Iterable<Configuration> configurations() {
        return Arrays.asList(
                MJSON_CONFIGURATION
        );
    }

    public static Iterable<Configuration> objectMappingConfigurations() {
        return Arrays.asList(
                MJSON_CONFIGURATION
        );
    }
}
