
package com.erhun.framework.basic.utils.generics;

import com.erhun.framework.basic.utils.ClassUtils;

import java.lang.reflect.*;
import java.util.*;

/**
 * weichao<gorilla@aliyun.com>
 */
@SuppressWarnings("unchecked")
public class GenericsUtils {

    public static Class extractGenericReturnType(Class containingClassType, Method method) {
        return extractActualTypeAsClass(containingClassType, method.getDeclaringClass(), method.getGenericReturnType(),
                method.getReturnType());

    }

    /**
     *
     * @param containingClassType
     * @param field
     * @return
     */
    public static Class extractGenericFieldType(Class containingClassType, Field field) {
        return extractActualTypeAsClass(containingClassType, field.getDeclaringClass(), field.getGenericType(),
                field.getType());
    }

    /**
     *
     * @param owner
     * @param field
     * @return
     */
    public static Type extractActualType(Type owner, Field field) {
        return extractActualType(owner, field.getDeclaringClass(), field.getGenericType(), field.getType());
    }

    /**
     *
     * @param owner
     * @param method
     * @return
     */
    public static Type extractActualType(Type owner, Method method) {
        return extractActualType(owner, method.getDeclaringClass(), method.getGenericReturnType(),
                method.getReturnType());
    }

    /**
     *
     * @param containingType
     * @param declaringClass
     * @param type
     * @param defaultType
     * @return
     */
    private static Type extractActualType(final Type containingType, final Class declaringClass, final Type type,
                                          final Class defaultType) {

        if (type instanceof ParameterizedType) {
            return type;
        }
        if (!(type instanceof TypeVariable))
            return defaultType;

        TypeVariable typeVariable = (TypeVariable) type;

        if (!declaringClass.isAssignableFrom(asClass(containingType))) {
            throw new RuntimeException(String.format(
                    "%s must be a subclass of %s", declaringClass.getName(), asClass(containingType).getName()));
        }

        Type extractedType = type;
        if (containingType instanceof ParameterizedType) {
            final int i = getTypeVariableIndex(asClass(containingType), typeVariable);
            extractedType = ((ParameterizedType) containingType).getActualTypeArguments()[i];
            if (extractedType instanceof Class || extractedType instanceof ParameterizedType) {
                return extractedType;
            }
        }
        final LinkedList<Type> classStack = new LinkedList<Type>();
        Type cur = containingType;
        while (cur != null && !asClass(cur).equals(declaringClass)) {
            classStack.add(0, cur);
            cur = asClass(cur).getSuperclass();
        }

        int typeArgumentIndex = getTypeVariableIndex(declaringClass, (TypeVariable) extractedType);

        for (Type descendant : classStack) {
            final Class descendantClass = asClass(descendant);
            final ParameterizedType parameterizedType = (ParameterizedType) descendantClass.getGenericSuperclass();

            extractedType = parameterizedType.getActualTypeArguments()[typeArgumentIndex];

            if (extractedType instanceof Class || extractedType instanceof ParameterizedType) {
                return extractedType;
            }

            if (extractedType instanceof TypeVariable) {
                typeArgumentIndex = getTypeVariableIndex(descendantClass, (TypeVariable) extractedType);
            } else {
                // I don't know what else this could be?
                break;
            }
        }

        return defaultType;
    }

    /**
     *
     * @param containingClassType
     * @param declaringClass
     * @param type
     * @param defaultType
     * @return
     */
    private static Class extractActualTypeAsClass(Class containingClassType, Class<?> declaringClass, Type type,
                                                  Class<?> defaultType) {
        final Type actualType = extractActualType(containingClassType, declaringClass, type, defaultType);

        return asClass(actualType);
    }

    public static Class asClass(Type actualType) {
        if (actualType instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) actualType).getRawType();
            if (rawType instanceof Class) {
                return (Class) rawType;
            }
        }

        return (Class) actualType;
    }

    private static int getTypeVariableIndex(Class clazz, TypeVariable typeVar) {
        // the label from the class (the T in List<T>, the K and V in Map<K,V>, etc)
        String typeVarName = typeVar.getName();
        int typeArgumentIndex = 0;
        final TypeVariable[] typeParameters = clazz.getTypeParameters();
        for (; typeArgumentIndex < typeParameters.length; typeArgumentIndex++) {
            if (typeParameters[typeArgumentIndex].getName().equals(typeVarName)) {
                break;
            }
        }
        return typeArgumentIndex;
    }

    public static boolean isAssignable(Type type, Type toType) {
        return isAssignable(type, toType, null);
    }

    private static boolean isAssignable(Type type, Type toType,
            Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (toType == null || toType instanceof Class<?>) {
            return isAssignable(type, (Class<?>) toType);
        }

        if (toType instanceof ParameterizedType) {
            return isAssignable(type, (ParameterizedType) toType, typeVarAssigns);
        }

        if (toType instanceof GenericArrayType) {
            return isAssignable(type, (GenericArrayType) toType, typeVarAssigns);
        }

        if (toType instanceof WildcardType) {
            return isAssignable(type, (WildcardType) toType, typeVarAssigns);
        }

        // *
        if (toType instanceof TypeVariable<?>) {
            return isAssignable(type, (TypeVariable<?>) toType, typeVarAssigns);
        }
        // */

        throw new IllegalStateException("found an unhandled type: " + toType);
    }

    /**
     *
     * @param type
     * @param toClass
     * @return
     */
    private static boolean isAssignable(Type type, Class<?> toClass) {
        if (type == null) {
            return toClass == null || !toClass.isPrimitive();
        }

        if (toClass == null) {
            return false;
        }

        if (toClass.equals(type)) {
            return true;
        }

        if (type instanceof Class<?>) {
            return ClassUtils.isAssignable((Class<?>) type, toClass);
        }

        if (type instanceof ParameterizedType) {
            return isAssignable(getRawType((ParameterizedType) type), toClass);
        }

        if (type instanceof TypeVariable<?>) {
            for (Type bound : ((TypeVariable<?>) type).getBounds()) {
                if (isAssignable(bound, toClass)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof GenericArrayType) {
            return toClass.equals(Object.class)
                    || toClass.isArray()
                    && isAssignable(((GenericArrayType) type).getGenericComponentType(), toClass
                            .getComponentType());
        }

        if (type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     *
     * @param type
     * @param toParameterizedType
     * @param typeVarAssigns
     * @return
     */
    private static boolean isAssignable(Type type, ParameterizedType toParameterizedType,
            Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        if (toParameterizedType == null) {
            return false;
        }

        if (toParameterizedType.equals(type)) {
            return true;
        }

        Class<?> toClass = getRawType(toParameterizedType);
        Map<TypeVariable<?>, Type> fromTypeVarAssigns = getTypeArguments(type, toClass, null);

        if (fromTypeVarAssigns == null) {
            return false;
        }

        if (fromTypeVarAssigns.isEmpty()) {
            return true;
        }

        Map<TypeVariable<?>, Type> toTypeVarAssigns = getTypeArguments(toParameterizedType,
                toClass, typeVarAssigns);

        for (Map.Entry<TypeVariable<?>, Type> entry : toTypeVarAssigns.entrySet()) {
            Type toTypeArg = entry.getValue();
            Type fromTypeArg = fromTypeVarAssigns.get(entry.getKey());

            if (fromTypeArg != null
                    && !toTypeArg.equals(fromTypeArg)
                    && !(toTypeArg instanceof WildcardType && isAssignable(fromTypeArg, toTypeArg,
                            typeVarAssigns))) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param type
     * @param toGenericArrayType
     * @param typeVarAssigns
     * @return
     */
    private static boolean isAssignable(Type type, GenericArrayType toGenericArrayType,
            Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        if (toGenericArrayType == null) {
            return false;
        }

        if (toGenericArrayType.equals(type)) {
            return true;
        }

        Type toComponentType = toGenericArrayType.getGenericComponentType();

        if (type instanceof Class<?>) {
            Class<?> cls = (Class<?>) type;
            return cls.isArray()
                    && isAssignable(cls.getComponentType(), toComponentType, typeVarAssigns);
        }

        if (type instanceof GenericArrayType) {
            return isAssignable(((GenericArrayType) type).getGenericComponentType(),
                    toComponentType, typeVarAssigns);
        }

        if (type instanceof WildcardType) {
            for (Type bound : getImplicitUpperBounds((WildcardType) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof TypeVariable<?>) {
            for (Type bound : getImplicitBounds((TypeVariable<?>) type)) {
                if (isAssignable(bound, toGenericArrayType)) {
                    return true;
                }
            }

            return false;
        }

        if (type instanceof ParameterizedType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     *
     * @param type
     * @param toWildcardType
     * @param typeVarAssigns
     * @return
     */
    private static boolean isAssignable(Type type, WildcardType toWildcardType,
            Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        if (toWildcardType == null) {
            return false;
        }

        if (toWildcardType.equals(type)) {
            return true;
        }

        Type[] toUpperBounds = getImplicitUpperBounds(toWildcardType);
        Type[] toLowerBounds = getImplicitLowerBounds(toWildcardType);

        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = getImplicitUpperBounds(wildcardType);
            Type[] lowerBounds = getImplicitLowerBounds(wildcardType);

            for (Type toBound : toUpperBounds) {
                toBound = substituteTypeVariables(toBound, typeVarAssigns);
                for (Type bound : upperBounds) {
                    if (!isAssignable(bound, toBound, typeVarAssigns)) {
                        return false;
                    }
                }
            }

            for (Type toBound : toLowerBounds) {
                toBound = substituteTypeVariables(toBound, typeVarAssigns);
                for (Type bound : lowerBounds) {
                    if (!isAssignable(toBound, bound, typeVarAssigns)) {
                        return false;
                    }
                }
            }

            return true;
        }

        for (Type toBound : toUpperBounds) {
            if (!isAssignable(type, substituteTypeVariables(toBound, typeVarAssigns),
                    typeVarAssigns)) {
                return false;
            }
        }

        for (Type toBound : toLowerBounds) {
            if (!isAssignable(substituteTypeVariables(toBound, typeVarAssigns), type,
                    typeVarAssigns)) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param type
     * @param toTypeVariable
     * @param typeVarAssigns
     * @return
     */
    private static boolean isAssignable(Type type, TypeVariable<?> toTypeVariable,
            Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type == null) {
            return true;
        }

        if (toTypeVariable == null) {
            return false;
        }

        if (toTypeVariable.equals(type)) {
            return true;
        }

        if (type instanceof TypeVariable<?>) {
            Type[] bounds = getImplicitBounds((TypeVariable<?>) type);

            for (Type bound : bounds) {
                if (isAssignable(bound, toTypeVariable, typeVarAssigns)) {
                    return true;
                }
            }
        }

        if (type instanceof Class<?> || type instanceof ParameterizedType
                || type instanceof GenericArrayType || type instanceof WildcardType) {
            return false;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     * <p> </p>
     *
     * @param type
     * @param typeVarAssigns
     * @return
     */
    private static Type substituteTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarAssigns) {
        if (type instanceof TypeVariable<?> && typeVarAssigns != null) {
            Type replacementType = typeVarAssigns.get(type);

            if (replacementType == null) {
                throw new IllegalArgumentException("missing assignment type for type variable "
                        + type);
            }

            return replacementType;
        }

        return type;
    }

    public static Map<TypeVariable<?>, Type> getTypeArguments(ParameterizedType type) {
        return getTypeArguments(type, getRawType(type), null);
    }

    /**
     *
     * @param type
     * @param toClass
     * @return
     */
    public static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass) {
        return getTypeArguments(type, toClass, null);
    }

    /**
     *
     * @param type
     * @param toClass
     * @param subtypeVarAssigns
     * @return
     */
    private static Map<TypeVariable<?>, Type> getTypeArguments(Type type, Class<?> toClass,
            Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (type instanceof Class<?>) {
            return getTypeArguments((Class<?>) type, toClass, subtypeVarAssigns);
        }

        if (type instanceof ParameterizedType) {
            return getTypeArguments((ParameterizedType) type, toClass, subtypeVarAssigns);
        }

        if (type instanceof GenericArrayType) {
            return getTypeArguments(((GenericArrayType) type).getGenericComponentType(), toClass
                    .isArray() ? toClass.getComponentType() : toClass, subtypeVarAssigns);
        }

        if (type instanceof WildcardType) {
            for (Type bound : getImplicitUpperBounds((WildcardType) type)) {
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }

            return null;
        }

        if (type instanceof TypeVariable<?>) {
            for (Type bound : getImplicitBounds((TypeVariable<?>) type)) {
                if (isAssignable(bound, toClass)) {
                    return getTypeArguments(bound, toClass, subtypeVarAssigns);
                }
            }

            return null;
        }

        throw new IllegalStateException("found an unhandled type: " + type);
    }

    /**
     * @param parameterizedType
     * @param toClass
     * @param subtypeVarAssigns
     * @return
     */
    private static Map<TypeVariable<?>, Type> getTypeArguments(
            ParameterizedType parameterizedType, Class<?> toClass,
            Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        Class<?> cls = getRawType(parameterizedType);

        // make sure they're assignable
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        Type ownerType = parameterizedType.getOwnerType();
        Map<TypeVariable<?>, Type> typeVarAssigns;

        if (ownerType instanceof ParameterizedType) {
            ParameterizedType parameterizedOwnerType = (ParameterizedType) ownerType;
            typeVarAssigns = getTypeArguments(parameterizedOwnerType,
                    getRawType(parameterizedOwnerType), subtypeVarAssigns);
        } else {
            typeVarAssigns = subtypeVarAssigns == null ? new HashMap<TypeVariable<?>, Type>()
                    : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns);
        }

        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeParams = cls.getTypeParameters();

        for (int i = 0; i < typeParams.length; i++) {
            Type typeArg = typeArgs[i];
            typeVarAssigns.put(typeParams[i], typeVarAssigns.containsKey(typeArg) ? typeVarAssigns
                    .get(typeArg) : typeArg);
        }

        if (toClass.equals(cls)) {
            return typeVarAssigns;
        }

        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }

    /**
     * @param cls
     * @param toClass
     * @param subtypeVarAssigns
     * @return
     */
    private static Map<TypeVariable<?>, Type> getTypeArguments(Class<?> cls, Class<?> toClass,
            Map<TypeVariable<?>, Type> subtypeVarAssigns) {
        if (!isAssignable(cls, toClass)) {
            return null;
        }

        if (cls.isPrimitive()) {
            if (toClass.isPrimitive()) {
                return new HashMap<TypeVariable<?>, Type>();
            }
            cls = ClassUtils.primitiveToWrapper(cls);
        }

        HashMap<TypeVariable<?>, Type> typeVarAssigns = subtypeVarAssigns == null ? new HashMap<TypeVariable<?>, Type>()
                : new HashMap<TypeVariable<?>, Type>(subtypeVarAssigns);

        if (cls.getTypeParameters().length > 0 || toClass.equals(cls)) {
            return typeVarAssigns;
        }
        return getTypeArguments(getClosestParentType(cls, toClass), toClass, typeVarAssigns);
    }

    /**
     *
     * @param cls
     * @param superType
     * @return
     */
    public static Map<TypeVariable<?>, Type> determineTypeArguments(Class<?> cls,
            ParameterizedType superType) {
        Class<?> superClass = getRawType(superType);

        if (!isAssignable(cls, superClass)) {
            return null;
        }

        if (cls.equals(superClass)) {
            return getTypeArguments(superType, superClass, null);
        }

        Type midType = getClosestParentType(cls, superClass);

        if (midType instanceof Class<?>) {
            return determineTypeArguments((Class<?>) midType, superType);
        }

        ParameterizedType midParameterizedType = (ParameterizedType) midType;
        Class<?> midClass = getRawType(midParameterizedType);
        Map<TypeVariable<?>, Type> typeVarAssigns = determineTypeArguments(midClass, superType);
        mapTypeVariablesToArguments(cls, midParameterizedType, typeVarAssigns);

        return typeVarAssigns;
    }

    /**
     * <p> </p>
     *
     * @param cls
     * @param parameterizedType
     * @param typeVarAssigns
     */
    private static <T> void mapTypeVariablesToArguments(Class<T> cls,
            ParameterizedType parameterizedType, Map<TypeVariable<?>, Type> typeVarAssigns) {
        Type ownerType = parameterizedType.getOwnerType();

        if (ownerType instanceof ParameterizedType) {
            mapTypeVariablesToArguments(cls, (ParameterizedType) ownerType, typeVarAssigns);
        }

        Type[] typeArgs = parameterizedType.getActualTypeArguments();

        TypeVariable<?>[] typeVars = getRawType(parameterizedType).getTypeParameters();

        List<TypeVariable<Class<T>>> typeVarList = Arrays.asList(cls
                .getTypeParameters());

        for (int i = 0; i < typeArgs.length; i++) {
            TypeVariable<?> typeVar = typeVars[i];
            Type typeArg = typeArgs[i];
            if (typeVarList.contains(typeArg)
                    && typeVarAssigns.containsKey(typeVar)) {
                typeVarAssigns.put((TypeVariable<?>) typeArg, typeVarAssigns.get(typeVar));
            }
        }
    }

    /**
     *
     * @param cls
     * @param superClass
     * @return
     */
    private static Type getClosestParentType(Class<?> cls, Class<?> superClass) {
        if (superClass.isInterface()) {
            Type[] interfaceTypes = cls.getGenericInterfaces();
            Type genericInterface = null;

            for (int i = 0; i < interfaceTypes.length; i++) {
                Type midType = interfaceTypes[i];
                Class<?> midClass = null;

                if (midType instanceof ParameterizedType) {
                    midClass = getRawType((ParameterizedType) midType);
                } else if (midType instanceof Class<?>) {
                    midClass = (Class<?>) midType;
                } else {
                    throw new IllegalStateException("Unexpected generic"
                            + " interface type found: " + midType);
                }

                if (isAssignable(midClass, superClass)
                        && isAssignable(genericInterface, (Type) midClass)) {
                    genericInterface = midType;
                }
            }

            if (genericInterface != null) {
                return genericInterface;
            }
        }

        return cls.getGenericSuperclass();
    }

    /**
     *
     * @param value
     * @param type
     * @return
     */
    public static boolean isInstance(Object value, Type type) {
        if (type == null) {
            return false;
        }

        return value == null ? !(type instanceof Class<?>) || !((Class<?>) type).isPrimitive()
                : isAssignable(value.getClass(), type, null);
    }

    /**
     *
     * @param bounds
     * @return
     */
    public static Type[] normalizeUpperBounds(Type[] bounds) {
        if (bounds.length < 2) {
            return bounds;
        }

        Set<Type> types = new HashSet<Type>(bounds.length);

        for (Type type1 : bounds) {
            boolean subtypeFound = false;

            for (Type type2 : bounds) {
                if (type1 != type2 && isAssignable(type2, type1, null)) {
                    subtypeFound = true;
                    break;
                }
            }

            if (!subtypeFound) {
                types.add(type1);
            }
        }

        return types.toArray(new Type[0]);
    }

    /**
     *
     * @param typeVariable
     * @return
     */
    public static Type[] getImplicitBounds(TypeVariable<?> typeVariable) {
        Type[] bounds = typeVariable.getBounds();

        return bounds.length == 0 ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }

    /**
     *
     * @param wildcardType
     * @return
     */
    public static Type[] getImplicitUpperBounds(WildcardType wildcardType) {
        Type[] bounds = wildcardType.getUpperBounds();

        return bounds.length == 0 ? new Type[] { Object.class } : normalizeUpperBounds(bounds);
    }

    /**
     *
     * @param wildcardType
     * @return
     */
    public static Type[] getImplicitLowerBounds(WildcardType wildcardType) {
        Type[] bounds = wildcardType.getLowerBounds();

        return bounds.length == 0 ? new Type[] { null } : bounds;
    }

    /**
     *
     * @param typeVarAssigns
     * @return
     */
    public static boolean typesSatisfyVariables(Map<TypeVariable<?>, Type> typeVarAssigns) {
        for (Map.Entry<TypeVariable<?>, Type> entry : typeVarAssigns.entrySet()) {
            TypeVariable<?> typeVar = entry.getKey();
            Type type = entry.getValue();

            for (Type bound : getImplicitBounds(typeVar)) {
                if (!isAssignable(type, substituteTypeVariables(bound, typeVarAssigns),
                        typeVarAssigns)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     *
     * @param parameterizedType
     * @return
     */
    private static Class<?> getRawType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class<?>)) {
            throw new IllegalStateException("Wait... What!? Type of rawType: " + rawType);
        }

        return (Class<?>) rawType;
    }

    /**
     *
     * @param type
     * @param assigningType
     * @return
     */
    public static Class<?> getRawType(Type type, Type assigningType) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }

        if (type instanceof ParameterizedType) {
            return getRawType((ParameterizedType) type);
        }

        if (type instanceof TypeVariable<?>) {
            if (assigningType == null) {
                return null;
            }

            Object genericDeclaration = ((TypeVariable<?>) type).getGenericDeclaration();

            if (!(genericDeclaration instanceof Class<?>)) {
                return null;
            }

            Map<TypeVariable<?>, Type> typeVarAssigns = getTypeArguments(assigningType,
                    (Class<?>) genericDeclaration);

            if (typeVarAssigns == null) {
                return null;
            }

            Type typeArgument = typeVarAssigns.get(type);

            if (typeArgument == null) {
                return null;
            }

            return getRawType(typeArgument, assigningType);
        }

        if (type instanceof GenericArrayType) {
            Class<?> rawComponentType = getRawType(((GenericArrayType) type)
                    .getGenericComponentType(), assigningType);
            return Array.newInstance(rawComponentType, 0).getClass();
        }

        if (type instanceof WildcardType) {
            return null;
        }

        throw new IllegalArgumentException("unknown type: " + type);
    }
    
    /**
     * 
     * @param clazz
     * @param index
     * @return
     */
    public static Class<?> getSuperInterfaceGenricType(Class<?> clazz, int index) {
        
        if(clazz.getGenericInterfaces().length == 0) {
            return null;
        }

        Type genType = clazz.getGenericInterfaces()[0];

        if (!(genType instanceof ParameterizedType)) {

            return Object.class;
        }

        Type [] params = ((ParameterizedType) genType).getActualTypeArguments();
        
        if (index >= params.length || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        
        return (Class<?>) params[index];
    }
    
    /**
     * 
     * @param clazz
     * @param index
     * @return
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {

            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        
        if (index >= params.length || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        
        return (Class<?>) params[index];
    }

    /**
     * 
     * @param clazz
     * @return
     */
    public static Class<?> getSuperClassGenricType(Class<?> clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 
     * @param method
     * @param index
     * @return
     */
    public static Class<?> getMethodGenericReturnType(Method method, int index) {

        Type returnType = method.getGenericReturnType();

        if (returnType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            if (index >= typeArguments.length || index < 0) {
                throw new ArrayIndexOutOfBoundsException("actual " + index + " but index " + typeArguments.length);
            }
            return (Class<?>) typeArguments[index];
        }
        
        return Object.class;
    }

    /**
     * 
     * @param method
     * @return
     */
    public static Class<?> getMethodGenericReturnType(Method method) {
        return getMethodGenericReturnType(method, 0);
    }

    /**
     * 
     * @param method
     * @param index
     * @return
     */
    public static List<Class<?>> getMethodGenericParameterTypes(Method method, int index) {

        Type [] types = method.getGenericParameterTypes();

        if (index >= types.length || index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        Type type = types[index];

        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] argTypes = paramType.getActualTypeArguments();
            List<Class<?>> results = new ArrayList<Class<?>>(argTypes.length);
            for (Type arg : argTypes) {
                results.add((Class<?>) arg);
            }
            return results;
        }
        
        return Collections.emptyList();
    }

    /**
     * 
     * @param method
     * @return
     */
    public static List<Class<?>> getMethodGenericParameterTypes(Method method) {
        return getMethodGenericParameterTypes(method, 0);
    }

    /**
     * 
     * @param field
     * @param index
     * @return
     */
    public static Class<?> getFieldGenericType(Field field, int index) {

        Type genType = field.getGenericType();

        if (genType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genType;
            Type[] argTypes = paramType.getActualTypeArguments();
            if (index >= argTypes.length || index < 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            return (Class<?>) argTypes[index];
        }
        
        return Object.class;
    }

    /**
     * 
     * @param field
     * @return
     */
    public static Class<?> getFieldGenericType(Field field) {

        return getFieldGenericType(field, 0);
    }
}
