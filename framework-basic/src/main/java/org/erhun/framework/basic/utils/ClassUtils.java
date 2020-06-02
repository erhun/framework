package org.erhun.framework.basic.utils;

import org.erhun.framework.basic.utils.string.StringPool;
import org.erhun.framework.basic.utils.string.StringUtils;

import java.lang.reflect.*;
import java.util.*;

/**
 * @Author weichao <gorilla@aliyun.com>
 */
public class ClassUtils {

	public static final char PACKAGE_SEPARATOR_CHAR = '.';

	public static final String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);

	public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

	public static final String INNER_CLASS_SEPARATOR = "$";

	private static Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>();

	static {
		primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
		primitiveWrapperMap.put(Byte.TYPE, Byte.class);
		primitiveWrapperMap.put(Character.TYPE, Character.class);
		primitiveWrapperMap.put(Short.TYPE, Short.class);
		primitiveWrapperMap.put(Integer.TYPE, Integer.class);
		primitiveWrapperMap.put(Long.TYPE, Long.class);
		primitiveWrapperMap.put(Double.TYPE, Double.class);
		primitiveWrapperMap.put(Float.TYPE, Float.class);
		primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
	}

	private static Map<String, String> abbreviationMap = new HashMap<String, String>();

	static {
		abbreviationMap.put("int", "I");
		abbreviationMap.put("boolean", "Z");
		abbreviationMap.put("float", "F");
		abbreviationMap.put("long", "J");
		abbreviationMap.put("short", "S");
		abbreviationMap.put("byte", "B");
		abbreviationMap.put("double", "D");
		abbreviationMap.put("char", "C");
	}

	/**
	 *
	 * @param object
	 * @param valueIfNull
	 * @return
	 */
	public static String getShortClassName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getShortClassName(object.getClass().getName());
	}

	/**
	 *
	 * @param cls
	 * @return
	 */
	public static String getShortClassName(Class<?> cls) {
		if (cls == null) {
			return StringPool.EMPTY;
		}
		return getShortClassName(cls.getName());
	}

	/**
	 *
	 * @param className
	 * @return
	 */
	public static String getShortClassName(String className) {
		if (className == null) {
			return StringPool.EMPTY;
		}
		if (className.length() == 0) {
			return StringPool.EMPTY;
		}
		char[] chars = className.toCharArray();
		int lastDot = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == PACKAGE_SEPARATOR_CHAR) {
				lastDot = i + 1;
			} else if (chars[i] == INNER_CLASS_SEPARATOR_CHAR) {
				chars[i] = PACKAGE_SEPARATOR_CHAR;
			}
		}
		return new String(chars, lastDot, chars.length - lastDot);
	}

	public static String getPackageName(Object object, String valueIfNull) {
		if (object == null) {
			return valueIfNull;
		}
		return getPackageName(object.getClass().getName());
	}

	public static String getPackageName(Class<?> cls) {
		if (cls == null) {
			return StringPool.EMPTY;
		}
		return getPackageName(cls.getName());
	}

	public static String getPackageName(String className) {
		if (className == null) {
			return StringPool.EMPTY;
		}
		int i = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
		if (i == -1) {
			return StringPool.EMPTY;
		}
		return className.substring(0, i);
	}

	public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
		if (cls == null) {
			return null;
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		Class<?> superclass = cls.getSuperclass();
		while (superclass != null) {
			classes.add(superclass);
			superclass = superclass.getSuperclass();
		}
		return classes;
	}

	public static List<Class<?>> getAllInterfaces(Class<?> cls) {
		if (cls == null) {
			return null;
		}
		List<Class<?>> list = new ArrayList<Class<?>>();
		while (cls != null) {
			Class<?>[] interfaces = cls.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				if (list.contains(interfaces[i]) == false) {
					list.add(interfaces[i]);
				}
				List<Class<?>> superInterfaces = getAllInterfaces(interfaces[i]);
				for (Iterator<Class<?>> it = superInterfaces.iterator(); it.hasNext();) {
					Class<?> intface = it.next();
					if (list.contains(intface) == false) {
						list.add(intface);
					}
				}
			}
			cls = cls.getSuperclass();
		}
		return list;
	}

	public static List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
		if (classNames == null) {
			return null;
		}
		List<Class<?>> classes = new ArrayList<Class<?>>(classNames.size());
		for (Iterator<String> it = classNames.iterator(); it.hasNext();) {
			String className = it.next();
			try {
				classes.add(Class.forName(className));
			} catch (Exception ex) {
				classes.add(null);
			}
		}
		return classes;
	}

	/**
	 *
	 * @param classes
	 * @return
	 */
	public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
		if (classes == null) {
			return null;
		}
		List<String> classNames = new ArrayList<String>(classes.size());
		for (Iterator<Class<?>> it = classes.iterator(); it.hasNext();) {
			Class<?> cls = it.next();
			if (cls == null) {
				classNames.add(null);
			} else {
				classNames.add(cls.getName());
			}
		}
		return classNames;
	}

	/**
	 *
	 * @param cls
	 * @param toClass
	 * @return
	 */
	public static boolean isAssignable(Class<?> cls, Class<?> toClass) {
		if (toClass == null) {
			return false;
		}

		if (cls == null) {
			return !(toClass.isPrimitive());
		}
		if (cls.equals(toClass)) {
			return true;
		}
		if (cls.isPrimitive()) {
			if (toClass.isPrimitive() == false) {
				return false;
			}
			if (Integer.TYPE.equals(cls)) {
				return Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Long.TYPE.equals(cls)) {
				return Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Boolean.TYPE.equals(cls)) {
				return false;
			}
			if (Double.TYPE.equals(cls)) {
				return false;
			}
			if (Float.TYPE.equals(cls)) {
				return Double.TYPE.equals(toClass);
			}
			if (Character.TYPE.equals(cls)) {
				return Integer.TYPE.equals(toClass)
						|| Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Short.TYPE.equals(cls)) {
				return Integer.TYPE.equals(toClass)
						|| Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			if (Byte.TYPE.equals(cls)) {
				return Short.TYPE.equals(toClass)
						|| Integer.TYPE.equals(toClass)
						|| Long.TYPE.equals(toClass)
						|| Float.TYPE.equals(toClass)
						|| Double.TYPE.equals(toClass);
			}
			// should never get here
			return false;
		}
		return toClass.isAssignableFrom(cls);
	}

	/**
	 *
	 * @param cls
	 * @return
	 */
	public static Class<?> primitiveToWrapper(Class<?> cls) {
		Class<?> convertedClass = cls;
		if (cls != null && cls.isPrimitive()) {
			convertedClass = primitiveWrapperMap.get(cls);
		}
		return convertedClass;
	}

	/**
	 *
	 * @param classes
	 * @return
	 */
	public static Class<?>[] primitivesToWrappers(Class<?>[] classes) {
		if (classes == null) {
			return null;
		}

		if (classes.length == 0) {
			return classes;
		}

		Class<?>[] convertedClasses = new Class[classes.length];
		for (int i = 0; i < classes.length; i++) {
			convertedClasses[i] = primitiveToWrapper(classes[i]);
		}
		return convertedClasses;
	}

	/**
	 *
	 * @param cls
	 * @return
	 */
	public static boolean isInnerClass(Class<?> cls) {
		if (cls == null) {
			return false;
		}
		return cls.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) >= 0;
	}

	public static Class<?> getClass(ClassLoader classLoader, String className, boolean initialize) {
		Class<?> clazz;
		try {
			if (abbreviationMap.containsKey(className)) {
				String clsName = "[" + abbreviationMap.get(className);
				clazz = Class.forName(clsName, initialize, classLoader).getComponentType();
			} else {
				clazz = Class.forName(toProperClassName(className), initialize, classLoader);
			}
			return clazz;
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 *
	 * @param classLoader
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getClass(ClassLoader classLoader, String className){
		return getClass(classLoader, className, true);
	}

	public static Class<?> getClass(String className){
		return getClass(className, true);
	}

	public static Class<?> getClass(String className, boolean initialize) {
		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		ClassLoader loader = contextCL == null ? ClassUtils.class.getClassLoader() : contextCL;
		return getClass(loader, className, initialize);
	}

	public static Class<?> getGenericType(Class<?> clazz) {
		Type type = clazz.getGenericSuperclass();
		if (type == null || type instanceof Class<?>) {
			return Object.class;
		}
		ParameterizedType pt = (ParameterizedType) type;
		type = pt.getActualTypeArguments()[0];
		if (type instanceof GenericArrayType) {
			type = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance((Class<?>) type, 0).getClass();
		}
		if (type instanceof ParameterizedType) {
			pt = (ParameterizedType) type;
			type = pt.getRawType();
		}
		return (Class<?>) type;
	}

	/**
	 *
	 * @param cls
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static Method getPublicMethod(Class<?> cls, String methodName, Class<?> parameterTypes[])
			throws SecurityException, NoSuchMethodException {

		Method declaredMethod = cls.getMethod(methodName, parameterTypes);
		if (Modifier.isPublic(declaredMethod.getDeclaringClass().getModifiers())) {
			return declaredMethod;
		}

		List<Class<?>> candidates = new ArrayList<Class<?>>();
		candidates.addAll(getAllInterfaces(cls));
		candidates.addAll(getAllSuperclasses(cls));

		for (Iterator<Class<?>> it = candidates.iterator(); it.hasNext();) {
			Class<?> candidateClass = it.next();
			if (!Modifier.isPublic(candidateClass.getModifiers())) {
				continue;
			}
			Method cm;
			try {
				cm = candidateClass.getMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException ex) {
				continue;
			}
			if (Modifier.isPublic(cm.getDeclaringClass().getModifiers())) {
				return cm;
			}
		}
		throw new NoSuchMethodException("Can't find a public method for "+ methodName + " from " + cls);
	}

	private static String toProperClassName(String className) {

		assert (className != null);
		className = StringUtils.removeWhitespace(className);

		if (className.endsWith("[]")) {
			StringBuffer classNameBuffer = new StringBuffer();
			while (className.endsWith("[]")) {
				className = className.substring(0, className.length() - 2);
				classNameBuffer.append("[");
			}
			String abbreviation = abbreviationMap.get(className);
			if (abbreviation != null) {
				classNameBuffer.append(abbreviation);
			} else {
				classNameBuffer.append("L").append(className).append(";");
			}
			className = classNameBuffer.toString();
		}
		return className;
	}

	public static String getQualifiedMethodName(Method method) {
		assert method != null : "Method must not be null";
		return method.getDeclaringClass().getName() + "." + method.getName();
	}

	public static Field[] getDeclaredFields(Class<?> clazz) {
	    if(clazz == null) {
	        return null;
	    }
		List<Field> all_fields = new ArrayList<Field>();
		Class<?> type = clazz;
		while (type != Object.class) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				all_fields.add(field);
			}
			type = type.getSuperclass();
		}
		return all_fields.toArray(new Field[0]);
	}

	public static Field getDeclaredField(Class<?> clazz, String fieldName) {
		Class<?> type = clazz;
		while (type != Object.class) {
			Field[] fields = type.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals(fieldName)) {
					return field;
				}
			}
			type = type.getSuperclass();
		}
		return null;
	}

	public static Method[] getPublicMethods(Class<?> cls) {
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
			int mods = method.getModifiers();
			if (Modifier.isPublic(mods) && (!Modifier.isFinal(mods)) && (!Modifier.isStatic(mods))) {
				methodList.add(method);
			}
		}
		return methodList.toArray(new Method[methodList.size()]);
	}

	public static Field[] getPublicFields(Class<?> clss) {
		List<Field> fieldList = new ArrayList<Field>();
		Field[] fields = clss.getFields();
		for (Field field : fields) {
			int mods = field.getModifiers();
			if (Modifier.isPublic(mods)
					&& (!Modifier.isTransient(mods))
					&& (!Modifier.isFinal(mods))
					&& (!Modifier.isStatic(mods))) {
				fieldList.add(field);
			}
		}
		return fieldList.toArray(new Field[fieldList.size()]);
	}

	public static boolean equals(Object object1, Object object2) {
		if (object1 != null && object2 != null) {
			return (object1.equals(object2));
		} else if (object1 == null && object2 != null) {
			return false;
		} else if (object1 != null && object2 == null) {
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	public static int compare(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null && o2 != null) {
			return 1;
		}
		if (o1 != null && o2 == null) {
			return -1;
		}

		if (o1 instanceof Comparable<?>) {
			return ((Comparable) o1).compareTo(o2);
		} else {
			return 0;
		}
	}

    public static boolean compare(Class[] first, Class[] second) {
        if (first.length != second.length) {
            return false;
        }
        for (int i = 0; i < first.length; i++) {
            if (first[i] != second[i]) {
                return false;
            }
        }
        return true;
    }

	public static boolean compare(Method method1, Method method2) {
		int comparison = compare((Object) method1, (Object) method2);
		if (comparison != 0) {
			return false;
		}

		comparison = method1.getName().compareTo(method2.getName());
		if (comparison != 0) {
			return false;
		}

		if  (! (method1.getReturnType() == method2.getReturnType())) {
			return false;
		}

        if (!compare(method1.getParameterTypes(), method2.getParameterTypes())) {
            return false;
        }

		return true;
	}

	public static String toString(Object[] array) {

		StringBuilder buffer = new StringBuilder(30);

		buffer.append("[");
		for (int i = 0; i < array.length; i++) {
			buffer.append(array[i].toString());
			if (i < array.length - 1) {
				buffer.append(", ");
			}
		}
		buffer.append("]");

		return buffer.toString();
	}

	public static boolean isGetter(Method member) {

		if (member == null) {
			throw new NullPointerException("No Method instance provided in call to ClassUtil.isGetter(...)");
		}

		if (member.getParameterTypes().length > 0) {
			return false;
		}

		if (member.getReturnType() == void.class || member.getReturnType() == null) {
			return false;
		}

		return member.getName().startsWith("get")
				|| member.getName().startsWith("is");
	}

	public static boolean isSetter(Method member) {
		if (member == null) {
			throw new NullPointerException("No Method instance provided in call to ClassUtil.isSetter(...)");
		}

		if (!member.getName().startsWith("set")) {
			return false;
		}
		if (member.getParameterTypes().length != 1) {
			return false;
		}
		return true;
	}

	public static boolean isSubclass(Class thisClass, Class target) {
		if (target.isInterface() != false) {
			return isInterfaceImplemented(thisClass, target);
		}
		for (Class x = thisClass; x != null; x = x.getSuperclass()) {
			if (x == target) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInterfaceImplemented(Class thisClass, Class targetInterface) {
		for (Class x = thisClass; x != null; x = x.getSuperclass()) {
			Class[] interfaces = x.getInterfaces();
			for (Class i : interfaces) {
				if (i == targetInterface) {
					return true;
				}
				if (isInterfaceImplemented(i, targetInterface)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isInstanceOf(Object o, Class target) {
		return isSubclass(o.getClass(), target);
    }
	
	
	public static String toTypeName(Class type) {
		
		if (type.isArray()) {
			return toTypeName(type.getComponentType()) + "[]";
		}

		return type.getName();
	}

	public static String[] toTypeNames(Class[] types) {
		String[] result = new String[types.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = toTypeName(types[i]);
		}

		return result;
	}
}
