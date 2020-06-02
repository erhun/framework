package org.erhun.framework.basic.utils.reflection;

import org.erhun.framework.basic.utils.ClassUtils;
import org.erhun.framework.basic.utils.datetime.DateUtility;
import org.erhun.framework.basic.utils.number.NumberUtils;
import org.erhun.framework.basic.utils.string.CharUtility;
import org.erhun.framework.basic.utils.string.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * 
 * @author weichao<gorilla@aliyun.com>
 *
 */
public class ReflectionUtils {

	public static final Class <?> [] NO_PARAMETERS = new Class[0];

	public static final Object[] NO_ARGUMENTS = new Object[0];

	public static final Type[] NO_TYPES = new Type[0];

	public static final String METHOD_GET_PREFIX = "get";
	
	public static final String METHOD_IS_PREFIX = "is";
	
	public static final String METHOD_SET_PREFIX = "set";

	private static Method _getMethod0;

	static {
		try {
			_getMethod0 = Class.class.getDeclaredMethod("getMethod0", String.class, Class[].class);
			_getMethod0.setAccessible(true);
		} catch (Exception e) {
			try {
				_getMethod0 = Class.class.getMethod("getMethod", String.class, Class[].class);
			} catch (Exception ex) {
				_getMethod0 =  null;
			}
		}
	}

	/**
	 *
	 * @param c
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethod0(Class <?> c, String name, Class <?> ... parameterTypes) {
		try {
			return (Method) _getMethod0.invoke(c, name, parameterTypes);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 *
	 * @param o
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethod0(Object o, String name, Class <?> ... parameterTypes) {
		try {
			return (Method) _getMethod0.invoke(o.getClass(), name, parameterTypes);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Class <?> [] getClasses(Object... objects) {
		if (objects == null) {
			return null;
		}
		Class <?> [] result = new Class[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				result[i] = objects[i].getClass();
			}
		}
		return result;
	}

	public static void set(Object instance, String field, Object value) {
		Field f = field(instance.getClass(), field);

		if (f == null) {
			return;
		}

		try {
			accessible(f).set(instance, value);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Field field(Class<?> c, String name) {
		while (c != null) {
			try {
				return c.getDeclaredField(name);
			} catch (Exception e) {
				c = c.getSuperclass();
			}
		}
		return null;
	}

	public static <T extends AccessibleObject> T accessible(T object) {
		if (object != null) {
			object.setAccessible(true);
		}
		return object;
	}

	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		throw new IllegalStateException(
				"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
	}

	public static void handleInvocationTargetException(InvocationTargetException ex) {
		if (ex.getTargetException() instanceof RuntimeException) {
			throw (RuntimeException) ex.getTargetException();
		}
		if (ex.getTargetException() instanceof Error) {
			throw (Error) ex.getTargetException();
		}
		throw new IllegalStateException(
				"Unexpected exception thrown by method - " + ex.getTargetException().getClass().getName() +
				": " + ex.getTargetException().getMessage());
	}
	
	public static Method findMethod(Class<?> type, String name, Class<?>[] paramTypes, Class<?> returnType) {
		assert type != null : "'type' cannot be null.";
		assert name != null : "'name' cannot be null.";
		Class<?> searchType = type;
		while(!Object.class.equals(searchType) && searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];				
				if(name.equals(method.getName()) && (method.getReturnType() == returnType) && Arrays.equals(paramTypes, method.getParameterTypes())) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}
	/**
	 * 
	 * @param type
	 * @param name
	 * @param matchParamTypes
	 * @param returnType
	 * @param matchCount
	 * @return
	 */
	public static Method findMethod(Class<?> type, String name, Class<?>[] matchParamTypes, Class<?> returnType, int matchCount) {
		assert type != null : "'type' cannot be null.";
		assert name != null : "'name' cannot be null.";
		Class<?> searchType = type;
		while(!Object.class.equals(searchType) && searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];				
				if(name.equals(method.getName()) && (method.getReturnType() == returnType)) {
					Class<?>[] methodParamTypes = method.getParameterTypes();
					if (matchCount > 0) {
						if (methodParamTypes == matchParamTypes) {
							return method;
						}
						if (methodParamTypes != null && matchParamTypes != null && methodParamTypes.length <= matchParamTypes.length) {
							int length = methodParamTypes.length >= matchCount ? matchCount : matchParamTypes.length;
							boolean b = false;
							for (int j=0; j<length; j++) {
								b = methodParamTypes[j].equals(matchParamTypes[j]);
								if (!b) {
									break;
								}
							}
							if (b) {
								return method;
							}
						}
					}
					else {						
						if (Arrays.equals(matchParamTypes, methodParamTypes)) {
							return method;
						}		
					}
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;		
	}
	
	public static Object invokeMethod(Method method, Object target) {
		return invokeMethod(method, target, (Object[]) null);
	}

	public static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		}
		catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
		catch (InvocationTargetException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 *
	 * @param clss
	 * @param obj
	 * @param method
	 * @param paramClasses
	 * @param params
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public static Object invokeDeclared(Class <?> clss, Object obj, String method, Class <?> [] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = clss.getDeclaredMethod(method, paramClasses);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}

	/**
	 *
	 * @param obj
	 * @param method
	 * @param paramClasses
	 * @param params
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public static Object invokeDeclared(Object obj, String method, Class <?> [] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = obj.getClass().getDeclaredMethod(method, paramClasses);
		m.setAccessible(true);
		return m.invoke(obj, params);
	}

	public static Object invokeDeclared(Object obj, String method, Object... params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class <?> [] paramClass = getClasses(params);
		return invokeDeclared(obj, method, paramClass, params);
	}

	public static Object invokeDeclared(Class <?> clss, Object obj, String method, Object... params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class <?> [] paramClass = getClasses(params);
		return invokeDeclared(clss, obj, method, paramClass, params);
	}

	/**
	 *
	 * @param field
	 * @return
	 */
	public static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}

	/**
	 *
	 * @param field
	 */
	public static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers()) ||
				!Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 *
	 * @param targetClass
	 * @param mc
	 * @throws IllegalArgumentException
	 */
	public static void doWithMethods(Class<?> targetClass, MethodCallback mc) throws IllegalArgumentException {
		doWithMethods(targetClass, mc, null);
	}

	/**
	 *
	 * @param targetClass
	 * @param mc
	 * @param mf
	 * @throws IllegalArgumentException
	 */
	public static void doWithMethods(Class<?> targetClass, MethodCallback mc, MethodFilter mf)
			throws IllegalArgumentException {

		do {
			Method[] methods = targetClass.getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (mf != null && !mf.matches(methods[i])) {
					continue;
				}
				try {
					mc.doWith(methods[i]);
				}
				catch (IllegalAccessException ex) {
					throw new IllegalStateException(
							"Shouldn't be illegal to access method '" + methods[i].getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null);
	}

	/**
	 *
	 * @param leafClass
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Method[] getAllDeclaredMethods(Class<?> leafClass) throws IllegalArgumentException {
		final List<Method> l = new LinkedList<Method>();
		doWithMethods(leafClass, new MethodCallback() {
			@Override
			public void doWith(Method m) {
				l.add(m);
			}
		});
		return (Method[]) l.toArray(new Method[l.size()]);
	}

	public static void doWithFields(Class<?> targetClass, FieldCallback fc) throws IllegalArgumentException {
		doWithFields(targetClass, fc, null);
	}

	public static void doWithFields(Class<?> targetClass, FieldCallback fc, FieldFilter ff)
			throws IllegalArgumentException {

		do {
			Field[] fields = targetClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (ff != null && !ff.matches(fields[i])) {
					continue;
				}
				try {
					fc.doWith(fields[i]);
				}
				catch (IllegalAccessException ex) {
					throw new IllegalStateException(
							"Shouldn't be illegal to access with '" + fields[i].getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
	}

	public static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
		if (src == null) {
			throw new IllegalArgumentException("Source for with copy cannot be null");
		}
		if (dest == null) {
			throw new IllegalArgumentException("Destination for with copy cannot be null");
		}
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() +
					"] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		doWithFields(src.getClass(), new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				makeAccessible(field);
				Object srcValue = field.get(src);
				field.set(dest, srcValue);
			}
		}, ReflectionUtils.COPYABLE_FIELDS);
	}

	public static interface MethodCallback {
		void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
	}

	public static interface MethodFilter {
		boolean matches(Method method);
	}

	public static interface FieldCallback {
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}

	public static interface FieldFilter {
		boolean matches(Field field);
	}

	public static FieldFilter COPYABLE_FIELDS = new FieldFilter() {
		@Override
		public boolean matches(Field field) {
			return !(Modifier.isStatic(field.getModifiers()) ||
					Modifier.isFinal(field.getModifiers()));
		}
	};

	public static void forceAccess(AccessibleObject accObject){
		if (accObject.isAccessible() == true) {
			return;
		}
		try {
			accObject.setAccessible(true);
		} catch (SecurityException sex) {
		}
	}

    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isPublic(Class <?> c) {
        return Modifier.isPublic(c.getModifiers());
    }

    private static void addFieldIfNotExist(List<Field> allFields, Field newField) {
        for (Field f : allFields) {
            if (f.getName().equals(newField.getName())) {
                return;
            }
        }
        allFields.add(newField);
    }


	private static void addMethodIfNotExist(List<Method> allMethods, Method newMethod) {
		for (Method m : allMethods) {
			if (ClassUtils.compare(m, newMethod)) {
				return;
			}
		}
		allMethods.add(newMethod);
	}

    public static Field[] getSupportedFields(Class <?> clazz, Class <?> limit) {
            ArrayList<Field> supportedFields = new ArrayList<Field>();
            for (Class <?> c = clazz; c != limit; c = c.getSuperclass()) {
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    boolean found = false;
                    for (Field supportedField : supportedFields) {
                        if (field.getName().equals(supportedField.getName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        supportedFields.add(field);
                    }
                }
            }
            return supportedFields.toArray(new Field[supportedFields.size()]);
        }

    public static Method[] getSupportedMethods(Class <?> clazz, Class <?> limit) {
        ArrayList<Method> supportedMethods = new ArrayList<Method>();
        for (Class <?> c = clazz; c != limit; c = c.getSuperclass()) {
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                boolean found = false;
                for (Method supportedMethod : supportedMethods) {
                    if (ClassUtils.compare(method, supportedMethod)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    supportedMethods.add(method);
                }
            }
        }
        return supportedMethods.toArray(new Method[supportedMethods.size()]);
    }


    public static Field[] getAccessibleFields(Class <?> clazz, Class <?> limit) {
		Package topPackage = clazz.getPackage();
		List<Field> fieldList = new ArrayList<Field>();
		int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
		boolean top = true;
		do {
			if (clazz == null) {
				break;
			}
			Field[] declaredFields = clazz.getDeclaredFields();
			for (Field field : declaredFields) {
				if (top == true) {
					fieldList.add(field);
					continue;
				}
				int modifier = field.getModifiers();
				if (Modifier.isPrivate(modifier) == true) {
					continue;
				}
				if (Modifier.isPublic(modifier) == true) {
					addFieldIfNotExist(fieldList, field);
					continue;
				}
				if (Modifier.isProtected(modifier) == true) {
					addFieldIfNotExist(fieldList, field);
					continue;
				}
				Package pckg = field.getDeclaringClass().getPackage();
				int pckgHash = pckg == null ? 0 : pckg.hashCode();
				if (pckgHash == topPackageHash) {
					addFieldIfNotExist(fieldList, field);
				}
			}
			top = false;
		} while ((clazz = clazz.getSuperclass()) != limit);

		Field[] fields = new Field[fieldList.size()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = fieldList.get(i);
		}
		return fields;
	}

    public static Method[] getAccessibleMethods(Class <?> clazz, Class <?> limit) {
        Package topPackage = clazz.getPackage();
        List<Method> methodList = new ArrayList<Method>();
        int topPackageHash = topPackage == null ? 0 : topPackage.hashCode();
        boolean top = true;
        do {
            if (clazz == null) {
                break;
            }
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (Modifier.isVolatile(method.getModifiers())) {
                    continue;
                }
                if (top == true) {				// add all top declared methods
                    methodList.add(method);
                    continue;
                }
                int modifier = method.getModifiers();
                if (Modifier.isPrivate(modifier) == true) {
                    continue;										// ignore super private methods
                }
                if (Modifier.isAbstract(modifier) == true) {		// ignore super abstract methods
                    continue;
                }
                if (Modifier.isPublic(modifier) == true) {
                    addMethodIfNotExist(methodList, method);		// add super public methods
                    continue;
                }
                if (Modifier.isProtected(modifier) == true) {
                    addMethodIfNotExist(methodList, method);		// add super protected methods
                    continue;
                }
                // add super default methods from the same package
                Package pckg = method.getDeclaringClass().getPackage();
                int pckgHash = pckg == null ? 0 : pckg.hashCode();
                if (pckgHash == topPackageHash) {
                    addMethodIfNotExist(methodList, method);
                }
            }
            top = false;
        } while ((clazz = clazz.getSuperclass()) != limit);

        Method[] methods = new Method[methodList.size()];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = methodList.get(i);
        }
        return methods;
    }

    public static boolean isObjectMethod(final Method method) {
        return method.getDeclaringClass() == Object.class;
    }

	public static String getBeanPropertyGetterName(Method method) {
		if (isObjectMethod(method)) {
			return null;
		}
		String methodName = method.getName();
		Class <?> returnType = method.getReturnType();
		Class <?> [] paramTypes =  method.getParameterTypes();
		if (methodName.startsWith(METHOD_GET_PREFIX)) {
			if ((returnType != null) && (paramTypes.length == 0)) {
				return CharUtility.toLowerAscii(methodName.charAt(3)) + methodName.substring(4);
			}
		} else if (methodName.startsWith(METHOD_IS_PREFIX)) {
			if ((returnType != null)  && (paramTypes.length == 0)) {
				return CharUtility.toLowerAscii(methodName.charAt(2)) + methodName.substring(3);
			}
		}
		return null;
	}

    public static String getBeanPropertySetterName(Method method) {
        if (isObjectMethod(method)) {
            return null;
        }
        String methodName = method.getName();
        Class <?> [] paramTypes =  method.getParameterTypes();
        if (methodName.startsWith(METHOD_SET_PREFIX)) {	        // setter must start with a 'set'
            if (paramTypes.length == 1) {				        // setter must have just one argument
                return CharUtility.toLowerAscii(methodName.charAt(3)) + methodName.substring(4);
            }
        }
        return null;
    }

    public static String getBeanPropertyGetterName(Field field) {
		
    	String name = Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
    	
    	if(field.getType() == Boolean.class || field.getType() == boolean.class){
    		return METHOD_IS_PREFIX + name;
    	}
    	
		return METHOD_GET_PREFIX + name;
		
	}
    
    /**
     * 
     * @param field
     * @return
     */
    public static String getBeanPropertySetterName(Field field) {
		
		return getBeanPropertySetterName(field.getName());
		
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public static String getBeanPropertySetterName(String name) {
		
		return METHOD_SET_PREFIX + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		
	}

	public static String getPropertyNameByMethodName(String methodName) {
		if(methodName.startsWith(METHOD_IS_PREFIX)){
			methodName = methodName.substring(2);
		}
		if(methodName.startsWith(METHOD_GET_PREFIX) || methodName.startsWith(METHOD_SET_PREFIX)){
			methodName = methodName.substring(3);
		}
		methodName = StringUtils.uncapitalize(methodName);
		return methodName;
	}

	public static String getPropertyNameByMethodName(Method method) {
		return getPropertyNameByMethodName(method.getName());
	}

	public static Field getPropertyFieldByMethodName(Class clazz, String methodName) {
		if(methodName.startsWith(METHOD_IS_PREFIX)){
			methodName = methodName.substring(2);
		}
		if(methodName.startsWith(METHOD_GET_PREFIX) || methodName.startsWith(METHOD_SET_PREFIX)){
			methodName = methodName.substring(3);
		}
		methodName = StringUtils.uncapitalize(methodName);
		return findField(clazz, methodName);
	}
    
	/**
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Object invoke( Object object, String fieldName ) throws Exception{

		Field field = object.getClass().getField(fieldName);
		
		return object.getClass().getMethod( getBeanPropertyGetterName(field) ).invoke( object );
		
	}

	/**
	 * 
	 * @param object
	 * @param methodName
	 * @param types
	 * @param args
	 * @return
	 */
	public static Object invoke( Object object, String methodName, Class <?> [] types, Object... args ) {

		Method method = null;
		try{
			method = object.getClass().getMethod( methodName, types );
		}catch( SecurityException e ){
			e.printStackTrace();
		}catch( NoSuchMethodException e ){
			e.printStackTrace();
		}

		try{
			return method.invoke( object, args );
		}catch( IllegalArgumentException e ){
			e.printStackTrace();
		}catch( IllegalAccessException e ){
			e.printStackTrace();
		}catch( InvocationTargetException e ){
			e.printStackTrace();
		}

		return null;

	}

	/**
	 *
	 * @param object
	 * @param method
	 * @param args
	 * @return
	 */
	public static Object invoke( Object object, Method method, Object... args ) {

		try{
			return method.invoke( object, args );
		}catch( IllegalArgumentException e ){
			e.printStackTrace();
		}catch( IllegalAccessException e ){
			e.printStackTrace();
		}catch( InvocationTargetException e ){
			e.printStackTrace();
		}

		return null;

	}
	
	/**
	 * 
	 * @param targetClass
	 * @param source
	 * @param superClass
	 * @param deep 拷贝的深度
	 *            
	 */
	@SuppressWarnings("rawtypes")
	private static void copyProperties( Object source, Object target, Class <?> superClass, Class <?> targetClass, int deep, boolean nullNotCopy ) throws Exception {

		if(source instanceof Map){
			copiesOfMap((Map) source, target, targetClass, nullNotCopy);
			return;
		}
		
		if(deep != -1 && deep < 1 || superClass == Object.class){
			return;
		}

		for( Field field : superClass.getDeclaredFields() ){
			field.setAccessible(true);
			Object value = field.get(source);
			if(nullNotCopy && value == null){
				continue;
			}
			if(Map.class.isAssignableFrom(targetClass)){
				if(value != null){
					((Map)target).put(field.getName(), value);
				}
			}else{
				try{
					Method method = targetClass.getMethod( getBeanPropertySetterName(field), new Class[]{ field.getType() } );
					method.invoke( target, value );
				}catch(NoSuchMethodException ex ){
					try {
						Field targetField = findField(targetClass, field.getName());
						targetField.setAccessible(true);
						targetField.set(target, value);
					}catch (Exception e){
						e.printStackTrace();
					}
				}catch (Exception ex){
					ex.printStackTrace();

				}
			}
		}

		copyProperties( source, target, superClass.getSuperclass(), targetClass, ( deep != -1 ? --deep : deep ), nullNotCopy );

	}

	@SuppressWarnings("rawtypes")
	private static void copiesOfMap(Map source, Object target, Class<?> targetClass, boolean nullNotCopy) {
		
		Set set = source.entrySet();
		
		Iterator <Map.Entry <?, ?>> iter = set.iterator();
		
		while(iter.hasNext()){
			Map.Entry <?, ?> entry = iter.next();
			Object value = entry.getValue();
			if(nullNotCopy && value == null){
				continue;
			}
			if(Map.class.isAssignableFrom(targetClass)){
				if(value != null){
					((Map)target).put(entry.getKey(), value);
				}
			}else{
				try{
					Field field = findField( targetClass, (String) entry.getKey() );
					Method method = targetClass.getMethod( getBeanPropertySetterName((String) entry.getKey()), new Class[]{ field.getType() } );
					method.invoke( target, value );
				}catch( Exception ex ){
				}
			}

		}
	}

	/**
	 * 
	 * @param target
	 * @param source
	 */
	public static <T> T copyProperties( Object source, Object target ) {
		copyProperties( source, target, -1 );
		return (T) target;

	}

	/**
	 * 
	 * @param src
	 * @param dest
	 * @param deep
	 */
	public static void copyProperties( Object src, Object dest, int deep ) {
		try{
			copyProperties( src, dest, src.getClass(), dest.getClass(), deep, false );
		}catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param c
	 * @param key
	 * @return
	 */
	public static Field findField( Class <?> c, String key ) {

		if(c.getName().endsWith( ".Object" )){
			return null;
		}
		
		Field field = null;
		
		try{
			field = c.getDeclaredField( key );
		}catch( NoSuchFieldException ex ){
			field = findField( c.getSuperclass(), key );
		}
		
		return field;

	}
	
	public static List <Field> findField( Class <?> clazz, Class <? extends Annotation> annotationClass ) {
		
	    List <Field> list = new ArrayList <Field> (3);
	    
		while( clazz != null && clazz != Object.class){
			Field fields [] =  clazz.getDeclaredFields();
			for( Field f : fields ){
				if(f.getAnnotation(annotationClass) != null){
				    list.add(f);
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		return list;
		
	}

	public static Field findField(Field [] fields, String fieldName) {

		if(fields == null){
			return null;
		}
		for (Field field : fields) {
			if(field.getName().equals(fieldName)){
				return field;
			}
		}
		return null;

	}
	
	public static Field set( Object instance, Field field, Object value) {
		field.setAccessible(true);
		try{
			field.set(instance, value);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}
		return field;
	}
	 

	/**
	 * 
	 * @param object
	 * @param property
	 * @param value
	 */
	@SuppressWarnings("rawtypes")
	public static void setProperty( Object object, String property, Object value ) {

		if(Map.class.isAssignableFrom(object.getClass())){
			((Map)object).put(property, value);
			return;
		}
		
		Field field = findField( object.getClass(), property );

		try{
			if(field != null){
				field.setAccessible(true);
				field.set( object, convertToBasicValue( field.getType(), value ) );
				// invoke( object , setterAlias( property ) , new Class[]{
				// with.getType() } , getBaseValue( with.getType() , value )
				// );
			}
		}catch( Exception ex ){
		}

	}

	/**
	 * 
	 * @param <T>
	 * @param object
	 * @param property
	 * @return
	 */
    public static <T> T getValue( Object object, String property ) {
    	if(object == null){
    		return null;
		}
    	if(object instanceof Map){
    		return (T) ((Map)object).get(property);
		}
		Field field = findField( object.getClass(), property );
		return (T) getValue(object, field);
	}

    public static Object getValue(Object object, Field field) {
        try{
			if(field != null){
				field.setAccessible( true );
				return field.get( object );
			}
		}catch( Exception ex ){
		}
        return null;
    }

	/**
	 * 
	 * @param source
	 * @param target
	 */
	public static <T> T copyProperties( Map <String, Object> source, Object target ) {

		Iterator <Map.Entry <String, Object>> iter = source.entrySet().iterator();

		while(iter.hasNext()){
			Map.Entry <String, Object> entry = iter.next();
			setProperty( target, entry.getKey(), entry.getValue() );
		}
		
		return (T) target;

	}

	/**
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object convertToBasicValue(Class <?> type, Object value ) {

		try{
			
			if(!(value instanceof String)){
				return value;
			}
			
			if(value == null || ( (value instanceof String) && ((String)value).length() == 0 )){
				return null;
			}
	
			String buf = (String) value;
	
			if(type == String.class){
				return buf;
			}
			if(type == int.class || type == Integer.class){
				if(NumberUtils.isNumber(buf)){
					return new Integer(buf);
				}
			}else if(type == Short.class || type == short.class){
				if("true".equals(buf) || "on".equals(buf)){
					buf = "1";
				}else if("false".equals(buf) || "off".equals(buf)){
					buf = "0";
				}
				return new Short( buf );
			}else if(type == Float.class){
				if(NumberUtils.isNumber(buf)){
					return new Float(buf);
				}
			}else if(type == java.sql.Date.class){
				return DateUtility.convertToSqlDate(buf);
			}else if(type == java.sql.Timestamp.class){
				return DateUtility.convertToTimestamp(buf);
			}else if(type == Long.class){
				return new Long( buf );
			}else if(type == Boolean.class){
				if("1".equals(buf) || "true".equals(buf) || "on".equals(buf)){
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}else if(type == Double.class){
				return new Double( buf );
			}else if(type == java.util.Date.class){
				return DateUtility.toSqlDate( buf );
			}else if(type == java.math.BigDecimal.class){
				if( NumberUtils.isNumber(buf) ){
					return new java.math.BigDecimal(buf);
				}
			}
			return buf;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;

	}

    @SuppressWarnings("unchecked")
	public static <T> T newInstance( Class <?> clazz, Object... arguments ) throws RuntimeException{
		
		try{
			if(arguments != null && arguments.length > 0){
				Class <?> types [] = new Class <?> [arguments.length];
				for( int i = 0; i < types.length; i++ ){
					types[i] = arguments[i].getClass();
				}
				Constructor <?> constructor = clazz.getConstructor(types);
				if(constructor != null){
					return (T) constructor.newInstance(arguments);
				}
				return null;
			}
			Object entity = clazz.newInstance();
			return (T) entity;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
		
	}
    

}
