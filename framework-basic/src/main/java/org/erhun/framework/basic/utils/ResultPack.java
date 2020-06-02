package org.erhun.framework.basic.utils;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author weichao <gorilla@aliyun.com>
 */
public abstract class ResultPack implements Serializable {

    /**
     * 
     */
    public static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    private static final int SUCCESS = 1;

    /**
     * 失败
     */
    private static final int FAIL = 0;

    private static final String SUCCESS_TEXT = "成功";

    private static final String FAIL_TEXT = "失败";

    public final static ResultPack SUCCEED = new Result(SUCCESS, SUCCESS_TEXT, true);


    public final static ResultPack FAILED = new Result(FAIL, FAIL_TEXT, true);
    
    public static ResultPack result(Integer code, String message) {
        return new Result(code, message);
    }

    public static ResultPack result(Integer code, String message, Object data) {
        return new Result(code, message).set("data", data);
    }

    public static ResultPack result(long maxRecords, List<?> data) {
        Result pack = new Result(SUCCESS, SUCCESS_TEXT);
        return pack.set("totalRecords", maxRecords).set("data", data);
    }

    public static ResultPack succeed() {
        return new Result(SUCCESS, SUCCESS_TEXT);
    }

    public static ResultPack succeed(String message) {
        return new Result(SUCCESS, message);
    }

    public static ResultPack failed() {
        return new Result(FAIL, FAIL_TEXT);
    }

    public static ResultPack failed(String message) {
        return new Result(FAIL, message);
    }

    public abstract ResultPack set(String key, Object value);

    public abstract boolean isSucceed();

    public abstract boolean isFailed();

    public abstract ResultPack model(Object model);

    public abstract <T> T model();

    public abstract <T> T get(String key);
    
    public abstract <T> T code();
    
    public abstract <T> T message();

    private static class Result extends ResultPack implements Map<String, Object>, Serializable {

        private static final long serialVersionUID = 1L;

        private Entry table[] = new Entry[2];

        private int modCount = 0;

        private boolean unmodifiable;

        private Result() {
        }

        private Result(Integer code, String message) {
            this(code, message, false);
        }

        private Result(Integer code, String message, boolean unmodifiable) {
            this.put("code", code);
            this.put("message", message);
            this.unmodifiable = unmodifiable;
        }

        @Override
        public int size() {
            return table.length;
        }

        @Override
        public boolean isEmpty() {
            return table.length == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return get((String)key);
        }

        @Override
        public Result put(String key, Object value) {
            if (unmodifiable) {
                throw new UnsupportedOperationException();
            }
            int i = modCount++;
            if (modCount > table.length) {
                Entry newTable[] = new Entry[table.length * 2];
                i = 0;
                for (; i < table.length; i++) {
                    newTable[i] = table[i];
                    table[i] = null;
                }
                table = newTable;
            }
            table[i] = new Entry(key, value);
            return this;
        }

        @Override
        public Result set(String key, Object value) {
            return this.put(key, value);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T model() {
            return (T) get("model");
        }

        @Override
        public ResultPack model(Object model) {
            this.put("model", model);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            for (Entry entry : table) {
                if (key.equals(entry.getKey())) {
                    return (T) entry.getValue();
                }
            }
            return null;
        }

        @Override
        public boolean isSucceed() {
            Integer code = (Integer) get("code");
            if (code != null && SUCCESS == code) {
                return true;
            }
            return false;
        }

        @Override
        public boolean isFailed() {
            Integer code = (Integer) get("code");
            if (code != null && FAIL == code) {
                return true;
            }
            return false;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void clear() {
        }

        @Override
        public Set<String> keySet() {
            return null;
        }

        @Override
        public Collection<Object> values() {
            return null;
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            return new EntrySet();
        }

        class Entry implements Map.Entry<String, Object> {
            String key;
            Object value;
            Entry(String String, Object v) {
                value = v;
                key = String;
            }
            @Override
            public final String getKey() {
                return key;
            }
            @Override
            public final Object getValue() {
                return value;
            }
            @Override
            public final Object setValue(Object newValue) {
                Object oldValue = value;
                value = newValue;
                return oldValue;
            }
            @Override
            public final String toString() {
                return getKey() + "=" + getValue();
            }
        }

        private final class EntrySet extends AbstractSet<Map.Entry<String, Object>> {
            @Override
            public Iterator<Map.Entry<String, Object>> iterator() {
                return new EntryIterator();
            }
            @Override
            public int size() {
                return table.length;
            }
        }

        private class EntryIterator implements Iterator<Map.Entry<String, Object>> {
            int index;

            @Override
            public Map.Entry<String, Object> next() {
                return table[index++];
            }

            @Override
            public final boolean hasNext() {
                return index < modCount;
            }
            @Override
            public void remove() {
            }
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
        }

        @Override
        public <T> T code() {
            return get("code");
        }

        @Override
        public <T> T message() {
            return get("message");
        }

    }

}
