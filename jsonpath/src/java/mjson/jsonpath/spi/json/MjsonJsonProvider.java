package mjson.jsonpath.spi.json;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.AbstractJsonProvider;
import mjson.Json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Matt Pratap on 2016-11-05.
 */
public class MjsonJsonProvider extends AbstractJsonProvider {

    public Object parse(String json) throws InvalidJsonException {
        return Json.read(json);
    }

    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] A = new byte[4096];
            for (int cnt = jsonStream.read(A); cnt > -1; cnt = jsonStream.read(A))
                out.write(A, 0, cnt);
            return Json.read(out.toString(charset));
        } catch (IOException e) {
            throw new InvalidJsonException("IOException", e);
        }
    }

    public String toJson(Object obj) {
        return ((Json)obj).toString();
    }

    @Override
    public boolean isArray(final Object obj) {
        return (obj instanceof List || (obj instanceof Json && ((Json)obj).isArray()));
    }

    public Object createArray() {
        return Json.array();
    }

    @Override
    public boolean isMap(final Object obj) {
        return (obj instanceof Map || (obj instanceof Json && ((Json)obj).isObject()));
    }

    public Object createMap() {
        return Json.object();
    }

    @Override
    public void setProperty(final Object obj, final Object key, final Object value) {
        if (isMap(obj)) {
            ((Json) obj).set(key.toString(), Json.make(value));
        }
    }

    @Override
    public Object getArrayIndex(final Object obj, final int idx) {
        return ((Json) obj).asJsonList().get(idx);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getPropertyKeys(final Object obj) {
        Set<String> set = new LinkedHashSet<String>();
        Json json = (Json) obj;
        if (json.isObject()) {
            set.addAll(((Map<String,Object>)json.getValue()).keySet());
        } else if (json.isArray()) {
            for (Json json1 : json.asJsonList()) {
                if (json1.isObject()) {
                    set.addAll(((Map<String, Object>) json1.getValue()).keySet());
                }
            }
        }
        return set;
    }

    @Override
    public Object getMapValue(final Object obj, final String key) {
        Json json = (Json) obj;
        if (json.isObject()) {
            Map<String, Json> jsonMap = json.asJsonMap();
            if (jsonMap.containsKey(key)) {
                return jsonMap.get(key);
            }
        }
//        else if (json.isArray()) {
//            List<Json> results = new ArrayList<Json>();
//            for (Json json1 : json.asJsonList()) {
//                if (json1.isObject()) {
//                    Map<String, Json> jsonMap = json1.asJsonMap();
//                    if (jsonMap.containsKey(key)) {
//                        results.add(jsonMap.get(key));
//                    }
//                }
//            }
//            return results;
//        }
        return UNDEFINED;
    }

    @Override
    public int length(final Object obj) {
        Json json = ((Json)obj);
        if (json.isArray()) {
            return json.asJsonList().size();
//        }
//        else if (!json.isPrimitive()) {
//            return json.asJsonMap().size();
        } else {
            throw new IllegalArgumentException("Cannot determine length of " + obj + ", unsupported type.");
        }
    }

    @Override
    public void setArrayIndex(final Object array, final int index, final Object newValue) {
        Json v = Json.make(newValue);
        List<Json> list = ((Json) array).asJsonList();
        list.add(index, v);
    }

    @Override
    public Object unwrap(Object obj) {
        if (obj != null && obj instanceof Json) {
            return ((Json) obj).getValue();
        } else {
            return obj;
        }
    }

}
