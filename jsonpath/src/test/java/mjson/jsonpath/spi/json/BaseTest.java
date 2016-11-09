package mjson.jsonpath.spi.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
import com.jayway.jsonpath.spi.json.*;
import com.jayway.jsonpath.spi.mapper.*;
import mjson.jsonpath.spi.mapper.MjsonMappingProvider;

import java.util.HashMap;

public class BaseTest {

    public static final Configuration MJSON_CONFIGURATION = Configuration
            .builder()
            .mappingProvider(new MjsonMappingProvider())
            .jsonProvider(new MjsonJsonProvider())
            .build();

    public static final String JSON_BOOK_DOCUMENT =
            "{ " +
            "   \"category\" : \"reference\",\n" +
            "   \"author\" : \"Nigel Rees\",\n" +
            "   \"title\" : \"Sayings of the Century\",\n" +
            "   \"display-price\" : 8.95\n" +
            "}";
    public static final String JSON_DOCUMENT = "{\n" +
            "   \"string-property\" : \"string-value\", \n" +
            "   \"int-max-property\" : " + Integer.MAX_VALUE + ", \n" +
            "   \"long-max-property\" : " + Long.MAX_VALUE + ", \n" +
            "   \"boolean-property\" : true, \n" +
            "   \"null-property\" : null, \n" +
            "   \"int-small-property\" : 1, \n" +
            "   \"max-price\" : 10, \n" +
            "   \"store\" : {\n" +
            "      \"book\" : [\n" +
            "         {\n" +
            "            \"category\" : \"reference\",\n" +
            "            \"author\" : \"Nigel Rees\",\n" +
            "            \"title\" : \"Sayings of the Century\",\n" +
            "            \"display-price\" : 8.95\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"Evelyn Waugh\",\n" +
            "            \"title\" : \"Sword of Honour\",\n" +
            "            \"display-price\" : 12.99\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"Herman Melville\",\n" +
            "            \"title\" : \"Moby Dick\",\n" +
            "            \"isbn\" : \"0-553-21311-3\",\n" +
            "            \"display-price\" : 8.99\n" +
            "         },\n" +
            "         {\n" +
            "            \"category\" : \"fiction\",\n" +
            "            \"author\" : \"J. R. R. Tolkien\",\n" +
            "            \"title\" : \"The Lord of the Rings\",\n" +
            "            \"isbn\" : \"0-395-19395-8\",\n" +
            "            \"display-price\" : 22.99\n" +
            "         }\n" +
            "      ],\n" +
            "      \"bicycle\" : {\n" +
            "         \"foo\" : \"baz\",\n" +
            "         \"escape\" : \"Esc\\b\\f\\n\\r\\t\\n\\t\\u002A\",\n" +
            "         \"color\" : \"red\",\n" +
            "         \"display-price\" : 19.95,\n" +
            "         \"foo:bar\" : \"fooBar\",\n" +
            "         \"dot.notation\" : \"new\",\n" +
            "         \"dash-notation\" : \"dashes\"\n" +
            "      }\n" +
            "   },\n" +
            "   \"foo\" : \"bar\",\n" +
            "   \"@id\" : \"ID\"\n" +
            "}";

    public static final String JSON_POJO =
            "[" +
                    "{\n" +
                    "   \"foo\" : \"foo0\",\n" +
                    "   \"bar\" : 0,\n" +
                    "   \"baz\" : true,\n" +
                    "   \"gen\" : {\"eric\" : \"yepp\"}" +
                    "}," +
                    "{\n" +
                    "   \"foo\" : \"foo1\",\n" +
                    "   \"bar\" : 1,\n" +
                    "   \"baz\" : true,\n" +
                    "   \"gen\" : {\"eric\" : \"yepp\"}" +
                    "}," +
                    "{\n" +
                    "   \"foo\" : \"foo2\",\n" +
                    "   \"bar\" : 2,\n" +
                    "   \"baz\" : true,\n" +
                    "   \"gen\" : {\"eric\" : \"yepp\"}" +
                    "}" +
                    "]";

    public Predicate.PredicateContext createPredicateContext(final Object check) {
        return new PredicateContextImpl(check, check, Configuration.defaultConfiguration(), new HashMap<Path, Object>());
    }
}
