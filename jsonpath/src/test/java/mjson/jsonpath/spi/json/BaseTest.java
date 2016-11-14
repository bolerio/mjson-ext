package mjson.jsonpath.spi.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.PredicateContextImpl;
import mjson.jsonpath.spi.mapper.MjsonMappingProvider;

import java.util.HashMap;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

    public Predicate.PredicateContext createPredicateContext(final Object check) {

        return new PredicateContextImpl(check, check, Configuration.defaultConfiguration(), new HashMap<Path, Object>());
    }

    //below helper functions copied from jayway jsonpath TestUtils

    public static void assertEvaluationThrows(final String json, final String path,
                                              Class<? extends JsonPathException> expected) {
        assertEvaluationThrows(json, path, expected, Configuration.defaultConfiguration());
    }

    /**
     * Shortcut for expected exception testing during path evaluation.
     *
     * @param json json to parse
     * @param path jsonpath do evaluate
     * @param expected expected exception class (reference comparison, not an instanceof)
     * @param conf conf to use during evaluation
     */
    public static void assertEvaluationThrows(final String json, final String path,
                                              Class<? extends JsonPathException> expected, final Configuration conf) {
        try {
            using(conf).parse(json).read(path);
            fail("Should throw " + expected.getName());
        } catch (JsonPathException exc) {
            if (exc.getClass() != expected)
                throw exc;
        }
    }

    /**
     * Assertion which requires empty list as a result of indefinite path search.
     * @param json json to be parsed
     * @param path path to be evaluated
     * @param conf conf to use during evaluation
     */
    public static void assertHasNoResults(final String json, final String path, Configuration conf) {
        assertHasResults(json, path, 0, conf);
    }

    /**
     * Assertion which requires list of one element as a result of indefinite path search.
     * @param json json to be parsed
     * @param path path to be evaluated
     */
    public static void assertHasOneResult(final String json, final String path, Configuration conf) {
        assertHasResults(json, path, 1, conf);
    }

    /**
     * Shortcut for counting found nodes.
     * @param json json to be parsed
     * @param path path to be evaluated
     * @param expectedResultCount expected number of nodes to be found
     * @param conf conf to use during evaluation
     */
    public static void assertHasResults(final String json, final String path, final int expectedResultCount, Configuration conf) {
        Object result = using(conf).parse(json).read(path);
        assertThat(conf.jsonProvider().length(result)).isEqualTo(expectedResultCount);
    }
}
