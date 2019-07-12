package top.andnux.api;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import top.andnux.annotation.AutoValue;

public class ZRouter {

    private static Map<String, Class<?>> sRouteMap = new HashMap<>();
    private static ZRouter instance = null;
    private String mUrl;
    private static Application sApplication;
    private Map<String, Object> mParams = new HashMap<>();
    private static final String KEY_DATA = "data";

    public static void init(Application application) {
        sApplication = application;
        try {
            Class<?> cls = Class.forName("top.andnux.router.Routers");
            Object o = cls.newInstance();
            Field mRouteMapField = cls.getDeclaredField("mRouteMap");
            mRouteMapField.setAccessible(true);
            Map<String, Object> objectMap = (Map<String, Object>) mRouteMapField.get(o);
            Set<String> strings = objectMap.keySet();
            for (String string : strings) {
                sRouteMap.put(string, (Class<?>) objectMap.get(string));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ZRouter() {
    }

    public ZRouter with(String url) {
        mParams.clear();
        mUrl = url;
        return this;
    }

    public Fragment navigation() {
        return navigation(null);
    }

    public Fragment navigation(NavigationListener listener) {
        Class<?> aClass = sRouteMap.get(mUrl);
        if (aClass == null) {
            if (listener != null) {
                listener.onError(new NavigationException("路径未找到：" + mUrl));
            }
            return null;
        }
        if (Activity.class.isAssignableFrom(aClass)) {
            Intent intent = new Intent(sApplication, aClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!mParams.isEmpty()) {
                intent.putExtra(KEY_DATA, JSON.toJSONString(mParams));
            }
            sApplication.startActivity(intent);
            if (listener != null) {
                listener.onSuccess();
            }
            return null;
        } else if (Fragment.class.isAssignableFrom(aClass)) {
            try {
                Fragment fragment = (Fragment) aClass.newInstance();
                if (!mParams.isEmpty()) {
                    Bundle args = new Bundle();
                    args.putString(KEY_DATA, JSON.toJSONString(mParams));
                    fragment.setArguments(args);
                }
                if (listener != null) {
                    listener.onSuccess();
                }
                return fragment;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (listener != null) {
            listener.onError(new NavigationException("目前只支持Activity和Fragment"));
        }
        return null;
    }

    public static ZRouter getInstance() {
        if (instance == null) {
            synchronized (ZRouter.class) {
                if (instance == null) {
                    instance = new ZRouter();
                }
            }
        }
        return instance;
    }

    public ZRouter putParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    public void inject(Activity activity) {
        String data = activity.getIntent().getStringExtra(KEY_DATA);
        if (TextUtils.isEmpty(data)) {
            return;
        }
        Map map = JSON.parseObject(data, Map.class);
        if (map == null || map.isEmpty()) {
            return;
        }
        List<Field> declaredFields = getDeclaredFields(activity.getClass());
        for (Field field : declaredFields) {
            field.setAccessible(true);
            AutoValue annotation = field.getAnnotation(AutoValue.class);
            if (annotation == null) continue;
            String value = annotation.value();
            if (value.length() == 0) {
                value = field.getName();
            }
            Object o = map.get(value);
                if (o != null) {
                    try {
                        field.set(activity, o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    public void inject(Fragment fragment) {
        Bundle extras = fragment.getArguments();
        if (extras == null) {
            return;
        }
        String data = extras.getString(KEY_DATA);
        Map map = JSON.parseObject(data, Map.class);
        if (map == null || map.isEmpty()) {
            return;
        }
        List<Field> declaredFields = getDeclaredFields(fragment.getClass());
        for (Field field : declaredFields) {
            field.setAccessible(true);
            AutoValue annotation = field.getAnnotation(AutoValue.class);
            if (annotation == null) continue;
            String value = annotation.value();
            if (value.length() == 0) {
                value = field.getName();
            }
            Object o = map.get(value);
            if (o != null) {
                try {
                    field.set(fragment, o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private List<Field> getDeclaredFields(Class<?> clazz) {
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
}
