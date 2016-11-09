package mjson.jsonpath.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import mjson.Json;

import java.util.ArrayList;

/**
 * Created by Matt on 2016-11-06.
 */
@SuppressWarnings("unchecked")
public class MjsonMappingProvider implements MappingProvider {

    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        if (source == null) {
            return null;
        }
        Object o = null;
        if (source instanceof Json) {
            String s = targetType.getName();
            Json json = (Json) source;
            if (json.isObject() && s.equals("java.lang.Object")) {
                o = (T) json.getValue();
            } else if (targetType.isAssignableFrom(json.getValue().getClass())) {
                o = (T) json.getValue();
            } else if (s.equals("string") || s.equals("java.lang.String")) {
                o = (T) json.getValue().toString();
            } else if (json.isNumber()) {
                if (s.equals("int") || s.equals("java.lang.Integer")) {
                    o = json.asInteger();
                } else if (s.equals("long") || s.equals("java.lang.Long")) {
                    o = json.asLong();
                } else if (s.equals("double") || s.equals("java.lang.Double")) {
                    o = json.asDouble();
                } else if (s.equals("float") || s.equals("java.lang.Float")) {
                    o = json.asFloat();
                } else if (s.equals("short") || s.equals("java.lang.Short")) {
                    o = json.asShort();
                } else if (s.equals("byte") || s.equals("java.lang.Byte")) {
                    o = json.asByte();
                }
            } else if (json.isBoolean() && s.equals("boolean") || s.equals("java.lang.Boolean")) {
                o = json.asBoolean();
            }
        } else if (targetType.isAssignableFrom(source.getClass())) {
            o = (T) source;
        }
        if (o != null)
            return (T) o;

        try {
            if (targetType.isAssignableFrom(ArrayList.class) && configuration.jsonProvider().isArray(source)) {
                int length = configuration.jsonProvider().length(source);
                @SuppressWarnings("rawtypes")
                ArrayList<Object> list = new ArrayList<Object>(length);
                for (Object o1 : configuration.jsonProvider().toIterable(source)) {
                    list.add(o1);
                }
                return (T) list;
            }
        } catch (Exception e) {

        }
        throw new MappingException("Cannot convert a " + source.getClass().getName() + " to a " + targetType);
    }

    public <T> T map(Object source, TypeRef<T> targetType, Configuration configuration) {
        return null;
    }
}
