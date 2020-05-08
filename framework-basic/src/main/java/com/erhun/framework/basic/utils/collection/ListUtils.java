package com.erhun.framework.basic.utils.collection;

import com.erhun.framework.basic.utils.string.StringPool;
import com.erhun.framework.basic.utils.string.StringUtils;

import java.util.*;

/**
 * gorilla
 */
public class ListUtils {

    private ListUtils() {}


    public static <T> List<T> emptyIfNull(final List<T> list) {
        return list == null ? Collections.<T>emptyList() : list;
    }

    public static <T> List<T> defaultIfNull(final List<T> list, final List<T> defaultList) {
        return list == null ? defaultList : list;
    }

    /**
     * 交集
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> intersection(final List<? extends E> list1, final List<? extends E> list2) {

        final List<E> result = new ArrayList<E>();

        List<? extends E> smaller = list1;
        List<? extends E> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }

        final HashSet<E> hashSet = new HashSet<E>(smaller);

        for (final E e : larger) {
            if (hashSet.contains(e)) {
                result.add(e);
                hashSet.remove(e);
            }
        }
        return result;
    }

    /**
     * 减集
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> subtract(final List<E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<E>();
        for (final E e : list1) {
            if (!list2.remove(e)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 合并不重复元素
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> sum(final List<? extends E> list1, final List<? extends E> list2) {
        return subtract(union(list1, list2), intersection(list1, list2));
    }

    /**
     * 合并
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> union(final List<? extends E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<E>(list1);
        result.addAll(list2);
        return result;
    }

    /**
     *
     * @param list1
     * @param list2
     * @return
     */
    public static boolean isEqualList(final Collection<?> list1, final Collection<?> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        final Iterator<?> it1 = list1.iterator();
        final Iterator<?> it2 = list2.iterator();
        Object obj1 = null;
        Object obj2 = null;

        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();

            if (!(obj1 == null ? obj2 == null : obj1.equals(obj2))) {
                return false;
            }
        }

        return !(it1.hasNext() || it2.hasNext());
    }

    /**
     *
     * @param list
     * @return
     */
    public static int hashCodeForList(final Collection<?> list) {
        if (list == null) {
            return 0;
        }
        int hashCode = 1;
        final Iterator<?> it = list.iterator();

        while (it.hasNext()) {
            final Object obj = it.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    public static <E> List<E> retainAll(final Collection<E> collection, final Collection<?> retain) {
        final List<E> list = new ArrayList<E>(Math.min(collection.size(), retain.size()));
        for (final E obj : collection) {
            if (retain.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    public static <E> List<E> removeAll(final Collection<E> collection, final Collection<?> remove) {
        final List<E> list = new ArrayList<E>();
        for (final E obj : collection) {
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

	public static boolean isEmpty( Collection <?> collection ) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNotEmpty( Collection <?> collection ) {
		return !isEmpty(collection);
	}

	public static boolean isEmpty( String [] args ) {
		if(args != null){
			for( String str : args ){
				return StringUtils.isEmpty(str);
			}
		}
		return true;
	}
	
    public static <T> List <T> as(List source) {
        return (List<T>) source;
    }
	
    public static Object [] as(Object ... values) {
        return values;
    }
	
	public static boolean isNotEmpty( Object [] array ) {
		return !isEmpty(array);
	}

	public static boolean isEmpty( Object [] array ) {
		if(array == null || array.length == 0){
			return true;
		}
		return false;
	}
	
    public static void add(List source, Object [] array) {
        
	    if(source == null || array == null) {
	        return;
	    }
	    
	    for (Object o : array) {
            source.add(o);
        }
	    
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String toString(List source) {

        if(source == null || source.isEmpty()) {
            return StringPool.EMPTY;
        }

        StringBuilder buf = new StringBuilder();

        for (Object o : source) {
            if(buf.length() > 0){
                buf.append(",");
            }
            buf.append(o);
        }

        return buf.toString();

    }
	
}
