package top.andnux.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectUtil {

    /***
     * 获取所有的私有字段
     * @param clazz 反射得类
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            if (clazz.getName().startsWith("java") ||
                    clazz.getName().startsWith("javax") ||
                    clazz.getName().startsWith("android")) {
                break;
            }
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /***
     * 获取所有的私有方法
     * @param clazz 反射得类
     * @return
     */
    public static List<Method> getDeclaredMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        while (clazz != null) {
            if (clazz.getName().startsWith("java") ||
                    clazz.getName().startsWith("javax") ||
                    clazz.getName().startsWith("android")) {
                break;
            }
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;

    }

    /***
     * 获取私有字段
     * @param name 可以为null
     * @param clazz 反射得类
     * @return
     */
    public static Field getDeclaredField(String name, Class<?> clazz) {
        List<Field> fields = getDeclaredFields(clazz);
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}

