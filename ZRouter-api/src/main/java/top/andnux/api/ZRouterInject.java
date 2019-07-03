package top.andnux.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

import top.andnux.annotation.AutoValue;

public class ZRouterInject {
    public static void init(Activity activity) {
        List<Field> declaredFields = ReflectUtil.getDeclaredFields(activity.getClass());
        for (Field field : declaredFields) {
            field.setAccessible(true);
            AutoValue annotation = field.getAnnotation(AutoValue.class);
            if (annotation == null) continue;
            Intent intent = activity.getIntent();
            String value = annotation.value();
            if (value.length() == 0) {
                value = field.getName();
            }
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Object o = extras.get(value);
                if (o != null) {
                    try {
                        field.set(activity, o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
