package mjson.jsonpath.spi.json;

import mjson.Json;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.jayway.jsonpath.JsonPath.using;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MjsonJsonProviderTest extends BaseTest {

    @Test
    public void json_string_can_be_parsed() {
        String jsonPath = "$.string-property";
        Json node =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        assertThat(node.getValue()).isEqualTo("string-value");
    }

    @Test
    public void json_file_can_be_parsed() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./src/test/resources/json-test-doc.json");
            String jsonPath = "$.type";
            Json node = using(MJSON_CONFIGURATION).parse(fis).read(jsonPath);
            assertThat(node.getValue()).isEqualTo("donut");
        } catch (IOException e) {
            fail(e.getMessage(), e);
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
            }
        }
    }

    @Test
    public void strings_are_unwrapped() {
        String jsonPath = "$.string-property";
        Json node = using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        String unwrapped = using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, String.class);

        assertThat(unwrapped).isEqualTo("string-value");
        assertThat(unwrapped).isEqualTo(node.asString());
        //default json string class is String
        assertThat(unwrapped).isEqualTo(node.getValue());
    }

    @Test
    public void ints_are_unwrapped() {
        String jsonPath = "$.int-max-property";
        Json node = using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        int unwrapped = using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, int.class);

        assertThat(unwrapped).isEqualTo(Integer.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asInteger());
    }

    @Test
    public void longs_are_unwrapped() {
        String jsonPath = "$.long-max-property";
        Json node =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        long unwrapped = using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, long.class);

        assertThat(unwrapped).isEqualTo(Long.MAX_VALUE);
        assertThat(unwrapped).isEqualTo(node.asLong());
        //default json integer class is Long
        assertThat(unwrapped).isEqualTo(node.getValue());
    }

    @Test
    public void doubles_are_unwrapped() {
        String jsonPath = "$.store.book[0].display-price";
        Json node =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        double unwrapped =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, double.class);

        assertThat(unwrapped).isEqualTo(8.95D);
        assertThat(unwrapped).isEqualTo(node.asDouble());
    }

    @Test
    public void booleans_are_unwrapped() {
        String jsonPath = "$.boolean-property";
        Json node =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        boolean unwrapped =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, boolean.class);

        assertThat(unwrapped).isEqualTo(true);
        assertThat(unwrapped).isEqualTo(node.asBoolean());
    }

    @Test
    public void object_test() {
        String jsonPath = "$.store";
        Json node =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        Object unwrapped =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, Object.class);

        assertThat(unwrapped.getClass()).isAssignableFrom(HashMap.class);
        assertThat(node.isObject()).isEqualTo(true);
    }

    @Test
    public void array_test() {
        String jsonPath = "$.store.book";
        Json node =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath);
        Object unwrapped =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read(jsonPath, Object.class);

        assertThat(unwrapped.getClass()).isAssignableFrom(ArrayList.class);
        assertThat(node.isArray()).isEqualTo(true);
    }


    @Test
    public void number_conversions() {
        //from Long
        String json = "{\"val\": 1}";
        final String val = "val";
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, int.class)).isEqualTo(1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Integer.class)).isEqualTo(1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, long.class)).isEqualTo(1L);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Long.class)).isEqualTo(1L);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, double.class)).isEqualTo(1D);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Double.class)).isEqualTo(1D);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, float.class)).isEqualTo(1F);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Float.class)).isEqualTo(1F);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, short.class)).isEqualTo((short)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Short.class)).isEqualTo((short)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, byte.class)).isEqualTo((byte)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Byte.class)).isEqualTo((byte)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, String.class)).isEqualTo("1");
        //from Double
        json = "{\"val\": 1.234}";
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, int.class)).isEqualTo(1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Integer.class)).isEqualTo(1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, long.class)).isEqualTo(1L);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Long.class)).isEqualTo(1L);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, double.class)).isEqualTo(1.234D);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Double.class)).isEqualTo(1.234D);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, float.class)).isEqualTo(1.234F);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Float.class)).isEqualTo(1.234F);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, short.class)).isEqualTo((short)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Short.class)).isEqualTo((short)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, byte.class)).isEqualTo((byte)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, Byte.class)).isEqualTo((byte)1);
        assertThat(using(MJSON_CONFIGURATION).parse(json).read(val, String.class)).isEqualTo("1.234");
    }


    @Test
    public void list_of_numbers() {
        Json objs =  using(MJSON_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[*].display-price");
        assertThat(objs.asList()).containsExactly(8.95D, 12.99D, 8.99D, 22.99D);
    }

//    @Test
//    public void an_object_can_be_mapped_to_pojo() {
//
//        String json = "{\n" +
//                "   \"foo\" : \"foo\",\n" +
//                "   \"bar\" : 10,\n" +
//                "   \"baz\" : true\n" +
//                "}";
//
//
//        TestClazz testClazz = JsonPath.using(MJSON_CONFIGURATION).parse(json).read("$", TestClazz.class);
//
//        assertThat(testClazz.foo).isEqualTo("foo");
//        assertThat(testClazz.bar).isEqualTo(10L);
//        assertThat(testClazz.baz).isEqualTo(true);
//
//    }
//
//    @Test
//    public void test_type_ref() throws IOException {
//        TypeRef<List<FooBarBaz<Gen>>> typeRef = new TypeRef<List<FooBarBaz<Gen>>>() {};
//
//        List<FooBarBaz<Gen>> list = JsonPath.using(MJSON_CONFIGURATION).parse(JSON_POJO).read("$", typeRef);
//
//        assertThat(list.get(0).gen.eric).isEqualTo("yepp");
//    }

//    @Test(expected = MappingException.class)
//    public void test_type_ref_fail() throws IOException {
//        TypeRef<List<FooBarBaz<Integer>>> typeRef = new TypeRef<List<FooBarBaz<Integer>>>() {};
//
//        Json node = using(MJSON_CONFIGURATION).parse(JSON_POJO).read("$");
//    }

    /**
     * Used for testing JSON to POJO
     * @param <T>
     */
    public static class FooBarBaz<T> {
        public T gen;
        public String foo;
        public Long bar;
        public boolean baz;
    }


    /**
     * Used for testing JSON to POJO
     */
    public static class Gen {
        public String eric;
    }

    /**
     * Used for testing JSON to POJO
     */
    public static class TestClazz {
        public String foo;
        public Long bar;
        public boolean baz;
    }




}
