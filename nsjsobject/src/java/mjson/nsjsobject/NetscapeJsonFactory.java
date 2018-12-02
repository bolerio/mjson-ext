/** 
 * Copyright (c) Granthika Co., All Rights Reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * Written by Borislav Iordanov <borislav@granthika.co>
 */
package mjson.nsjsobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mjson.Json;
import netscape.javascript.JSObject;

/**
 * <p>
 * Implements <a href="http://bolerio.github.io/mjson/">mJson</a> elements as wrappers over native browser JavaScript objects.
 * Since JDK 1.8, the Java runtime comes with the ability to embed a WebKit browser and interface with
 * its JavaScript runtime interpreter through the <code>netscape.javascript.JSObject</code>, or
 * what used to be <code>LiveConnect</code>. This factory will represents JSON objects and arrays
 * as actual JavaScript objects and array, inside the browser environment. This makes it possible
 * for code to manipulate a JSON structure generically whether it is to be server to a remote
 * client via a JSON textual encoding, or directly manipulated inside an embedded browser.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class NetscapeJsonFactory extends Json.DefaultFactory implements java.io.Closeable
{
    JSObject global;
    
    public static NetscapeJsonFactory enter(JSObject global)
    {
        NetscapeJsonFactory factory = new NetscapeJsonFactory(global);
        Json.attachFactory(factory);
        return factory;
    }
    
    public NetscapeJsonFactory(JSObject global)
    {
        this.global = global;
    }
    
    @Override
    public void close()
    {
        Json.detachFactory();
    }
    
    class ObjectJson extends Json
    {
        private static final long serialVersionUID = 1L;
        
        JSObject object;
        
        ObjectJson() { object = (JSObject)global.eval("new Object()"); }
        ObjectJson(Json e) { super(e); object = (JSObject)global.eval("new Object()"); }
        ObjectJson(JSObject object) { this.object = object; }
        
        Set<String> propertyNames() 
        {
            final JSObject propertyNames = (JSObject)object.eval("Object.getOwnPropertyNames(this)");
            final int length = (Integer)propertyNames.getMember("length");
            Set<String> S = new HashSet<String>();
            for (int i = 0; i < length; i++)
                S.add((String)propertyNames.getSlot(i));
            return S;
        }
            
        public Json dup() 
        { 
            ObjectJson j = new ObjectJson();
            propertyNames().forEach(name -> {
                Json v =  at(name).dup();
                j.set(name, v);        
            });
            return j;
        }
        
        public boolean has(String property)
        {
            // based on this: https://developer.mozilla.org/en-US/docs/Archive/Web/LiveConnect/LiveConnect_Overview#Undefined_Values
            // "The value is converted to an instance of java.lang.String whose value is the string "undefined"."
            // and this: http://mail.openjdk.java.net/pipermail/nashorn-dev/2015-March/004418.html
            return !object
                    .eval(String.format("typeof this['%s']", property))
                    .equals("undefined");
        }
        
        public boolean is(String property, Object value) 
        { 
            Json p = at(property);
            if (p == null)
                return false;
            else
                return p.equals(make(value));
        }       
        
        public Json at(String property)
        {
            if (has(property))
                return make(object.getMember(property));
            else
                return null;
        }

        protected Json withOptions(Json other, Json allOptions, String path)
        {
            Json options = allOptions.at(path, object());
            boolean duplicate = options.is("dup", true);
            if (options.is("merge", true))
            {
                for (Map.Entry<String, Json> e : other.asJsonMap().entrySet())
                {
                    Json local = at(e.getKey());
                    if (local instanceof ObjectJson)
                        ((ObjectJson)local).withOptions(e.getValue(), allOptions, path + "/" + e.getKey());
                    else if (local instanceof ArrayJson)
                        ((ArrayJson)local).withOptions(e.getValue(), allOptions, path + "/" + e.getKey());
                    else
                        set(e.getKey(), duplicate ? e.getValue().dup() : e.getValue());
                }
            }
            else if (duplicate)
                for (Map.Entry<String, Json> e : other.asJsonMap().entrySet())
                    set(e.getKey(), e.getValue().dup());
            else
                for (Map.Entry<String, Json> e : other.asJsonMap().entrySet())
                    set(e.getKey(), e.getValue());
            return this;
        }

        public Json with(Json x, Json...options)
        {
            if (x == null) return this;         
            if (!x.isObject())
                throw new UnsupportedOperationException();
            if (options.length > 0)
            {
                Json O = collectWithOptions(options);
                return withOptions(x, O, "");
            }
            else for (Map.Entry<String, Json> e : x.asJsonMap().entrySet())
                set(e.getKey(), e.getValue());
            return this;
        }
        
        public Json set(String property, Json el)
        {
            if (property == null)
                throw new IllegalArgumentException("Null property names are not allowed, value is " + el);
            Object value = el == null ? null : el.getValue();
            object.setMember(property, value);
            return this;
        }

        public Json atDel(String property) 
        {
            Json value = at(property);
            object.removeMember(property);
            return value;
        }
        
        public Json delAt(String property) 
        {
            object.removeMember(property);
            return this;
        }
        
        public Object getValue() { return this.object; }
        public boolean isObject() { return true; }
        
        /*
        private void recurseMap(Map<String, Object> map, IdentityHashMap<Object, Json> done)
        {
            for (String name : propertyNames())
            {
                Object value = _getMember(name);
                Json asjson = done.get(value);
                if (asjson == null)
                {
                    asjson = make(value);
                    done.put(value, asjson);
                }
                map.put(name, value);
            }           
        } */
        
        public Map<String, Object> asMap() 
        {
            HashMap<String, Object> m = new HashMap<String, Object>();
            for (String name : propertyNames())
            {
                Json value = at(name);
                m.put(name, value.getValue());
            }
//          recurseMap(m, new IdentityHashMap<Object, Json>());
            return m;
        }
        
        @Override
        public Map<String, Json> asJsonMap() 
        { 
            HashMap<String, Json> m = new HashMap<String, Json>();
            for (String name : propertyNames())
                m.put(name, at(name));
            return m; 
        }
        
        public String toString()
        {
            return toString(Integer.MAX_VALUE);
        }
        
        public String toString(int maxCharacters)
        {
            StringBuilder sb = new StringBuilder("{");
            for (Iterator<String> i = propertyNames().iterator(); i.hasNext(); )
            {
                String name = i.next();
                Json value = at(name);      
                sb.append('"');             
                sb.append(Json.help.escape(name));
                sb.append('"');
                sb.append(":");
                String s = value.toString(maxCharacters);
                if (sb.length() + s.length() > maxCharacters)
                    s = s.substring(0, Math.max(0, maxCharacters - sb.length()));
                sb.append(s);
                if (i.hasNext())
                    sb.append(",");
                if (sb.length() >= maxCharacters)
                {
                    sb.append("...");
                    break;
                }
            }
            sb.append("}");
            return sb.toString();
        }
        public int hashCode() { return object.hashCode(); }
        public boolean equals(Object x)
        {           
            return x instanceof ObjectJson && ((ObjectJson)x).object.equals(object); 
        }               
    }
    
    class ArrayJson extends Json
    {
        private static final long serialVersionUID = 1L;
        
        JSObject array;
        
        ArrayJson() { array = (JSObject)global.eval("[]"); }
        ArrayJson(Json e) { super(e); array = (JSObject)global.eval("[]"); }
        ArrayJson(JSObject array) { this.array = array; }
        
        int length() { return  (Integer)array.getMember("length"); }

        public Json dup() 
        { 
            ArrayJson j = new ArrayJson();
            for (int i = 0; i < length(); i++)
            {
                Json v = at(i);
                j.add(v.dup());
            }
            return j;
        }
        
        public Json set(int index, Object value) 
        { 
            array.setSlot(index, make(value).getValue());
            return this;
        }
        
        public List<Json> asJsonList() 
        { 
            ArrayList<Json> L = new ArrayList<Json>();
            for (int i = 0; i < length(); i++)
                L.add(at(i));
            return L; 
        }
        public List<Object> asList() 
        {
            return asJsonList().stream().map(Json::getValue).collect(Collectors.toList());
        }
        public boolean is(int index, Object value) 
        { 
            if (index < 0 || index >= length())
                return false;
            else
                return at(index).equals(make(value));
        }               
        public Object getValue() { return array; }
        public boolean isArray() { return true; }
        public Json at(int index) { return make(array.getSlot(index)); }
        public Json add(Json el) 
        { 
            array.call("push", el.getValue()); 
            //el.enclosing = this; 
            return this; 
        }
        
        public Json remove(Json el) 
        { 
            Object value = el.getValue();
            Integer i = (Integer)array.call("indexOf", value);
            if (i > -1)
                array.call("splice", i, 1);
            return this; 
        }

        boolean isEqualJson(Json left, Json right)
        {
            if (left == null)
                return right == null;
            else
                return left.equals(right);
        }

        boolean isEqualJson(Json left, Json right, Json fields)
        {
            if (fields.isNull())
                return left.equals(right);
            else if (fields.isString())
                return isEqualJson(Json.help.resolvePointer(fields.asString(), left),
                                   Json.help.resolvePointer(fields.asString(), right));
            else if (fields.isArray())
            {
                for (Json field : fields.asJsonList())
                    if (!isEqualJson(Json.help.resolvePointer(field.asString(), left),
                            Json.help.resolvePointer(field.asString(), right)))
                        return false;
                return true;
            }
            else
                throw new IllegalArgumentException("Compare by options should be either a property name or an array of property names: " + fields);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        int compareJson(Json left, Json right, Json fields)
        {
            if (fields.isNull())
                return ((Comparable)left.getValue()).compareTo(right.getValue());
            else if (fields.isString())
            {
                Json leftProperty = Json.help.resolvePointer(fields.asString(), left);
                Json rightProperty = Json.help.resolvePointer(fields.asString(), right);
                return ((Comparable)leftProperty).compareTo(rightProperty);
            }
            else if (fields.isArray())
            {
                for (Json field : fields.asJsonList())
                {
                    Json leftProperty = Json.help.resolvePointer(field.asString(), left);
                    Json rightProperty = Json.help.resolvePointer(field.asString(), right);
                    int result = ((Comparable) leftProperty).compareTo(rightProperty);
                    if (result != 0)
                        return result;
                }
                return 0;
            }
            else
                throw new IllegalArgumentException("Compare by options should be either a property name or an array of property names: " + fields);
        }

        Json withOptions(Json array, Json allOptions, String path)
        {
            Json opts = allOptions.at(path, object());
            boolean dup = opts.is("dup", true);
            Json compareBy = opts.at("compareBy", nil());
            if (opts.is("sort", true))
            {
                int thisIndex = 0, thatIndex = 0;
                while (thatIndex < array.asJsonList().size())
                {
                    Json thatElement = array.at(thatIndex);
                    if (thisIndex == length())
                    {
                        add(dup ? thatElement.dup() : thatElement);
                        thisIndex++;
                        thatIndex++;
                        continue;
                    }
                    int compared = compareJson(at(thisIndex), thatElement, compareBy);
                    if (compared < 0) // this < that
                        thisIndex++;
                    else if (compared > 0) // this > that
                    {
                        this.array.call("splice", thisIndex, dup ? thatElement.dup().getValue() : thatElement.getValue());
                        thatIndex++;
                    } else { // equal, ignore 
                        thatIndex++;
                    }
                }
            }
            else
            {
                for (Json thatElement : array.asJsonList())
                {
                    boolean present = false;
                    for (Json thisElement : asJsonList())
                        if (isEqualJson(thisElement, thatElement, compareBy))
                        {
                            present = true;
                            break;
                        }
                    if (!present)
                        add(dup ? thatElement.dup() : thatElement);
                }
            }
            return this;
        }

        public Json with(Json object, Json...options)
        {
            if (object == null) return this;
            if (!object.isArray())
                add(object);
            else if (options.length > 0)
            {
                Json O = collectWithOptions(options);
                return withOptions(object, O, "");
            }
            else
            {
                ((JSObject)array.getMember("push")).call("apply", array, ((ArrayJson)object).array);
            }
            return this;
        }
        
        public Json atDel(int index) 
        { 
            Object el = array.getSlot(index); 
            return make(el); 
        }
        
        public Json delAt(int index) 
        { 
            array.getSlot(index); 
            return this; 
        }
        
        public String toString()
        {
            return toString(Integer.MAX_VALUE);
        }
        
        public String toString(int maxCharacters) 
        {
            StringBuilder sb = new StringBuilder("[");          
            for (Iterator<Json> i = asJsonList().iterator(); i.hasNext(); )
            {
                String s = i.next().toString(maxCharacters);
                if (sb.length() + s.length() > maxCharacters)
                    s = s.substring(0, Math.max(0, maxCharacters - sb.length()));
                else
                    sb.append(s);
                if (i.hasNext())
                    sb.append(",");
                if (sb.length() >= maxCharacters)
                {
                    sb.append("...");
                    break;
                }
            }           
            sb.append("]");
            return sb.toString();           
        }
        
        public int hashCode() { return array.hashCode(); }
        public boolean equals(Object x)
        {           
            return x instanceof ArrayJson && ((ArrayJson)x).array.equals(array); 
        }       
    }

    @Override
    public Json object()
    {
        return new ObjectJson();
    }

    @Override
    public Json array()
    {
        return new ArrayJson();
    }

    @Override
    public Json make(Object anything)
    {
        if (anything instanceof JSObject)
        {
            JSObject x = (JSObject)anything;
            Object cons = x.getMember("constructor");
            if ("undefined".equals(cons))
                return new ObjectJson(x);
            if (! (cons instanceof JSObject))
                System.err.println("Oops " + x + " is not  a jsobject");
            if (((JSObject)cons).getMember("name").toString().equals("Array"))
                return new ArrayJson(x);
            else
                return new ObjectJson(x);
        }
        else
            return super.make(anything);
    }

}
