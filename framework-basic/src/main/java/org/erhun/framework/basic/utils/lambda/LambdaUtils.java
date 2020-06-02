package org.erhun.framework.basic.utils.lambda;

import org.erhun.framework.basic.utils.reflection.ReflectionUtils;
import org.erhun.framework.basic.utils.string.StringUtils;
import org.erhun.framework.basic.utils.ClassUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author weichao <gorilla@aliyun.com>
 * @Date 2019-10-12
 */
public class LambdaUtils {


    public static <T> SerializedLambda getSerializedLambda(LambdaFunction fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            java.lang.invoke.SerializedLambda serializedLambda = (java.lang.invoke.SerializedLambda) method.invoke(fn);
            return serializedLambda;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String getMethodName(LambdaFunction fn) {
        java.lang.invoke.SerializedLambda serializedLambda = getSerializedLambda(fn);
        String methodName = serializedLambda.getImplMethodName();
        return methodName;
    }

    public static <T> String getFieldName(LambdaFunction fn) {
        String methodName = getMethodName(fn);
        if(methodName.startsWith(ReflectionUtils.METHOD_GET_PREFIX) || methodName.startsWith(ReflectionUtils.METHOD_SET_PREFIX)){
            methodName = methodName.substring(3);
        }
        String fieldName = StringUtils.uncapitalize(methodName);
        return fieldName;
    }

    public <T> Field getField(LambdaFunction fn) {
        SerializedLambda serializedLambda = getSerializedLambda(fn);
        String methodName = serializedLambda.getImplMethodName();
        if(methodName.startsWith(ReflectionUtils.METHOD_GET_PREFIX) || methodName.startsWith(ReflectionUtils.METHOD_SET_PREFIX)){
            methodName = methodName.substring(3);
        }
        String fieldName = StringUtils.uncapitalize(methodName);
        Class clazz = ClassUtils.getClass(serializedLambda.getImplClass().replace("/", "."));
        Field field = ReflectionUtils.findField(clazz, fieldName);
        return field;
    }

}
