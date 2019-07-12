package top.andnux.zrouter;

import android.app.Application;
import android.content.Context;

import top.andnux.api.ZRouter;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ZRouter.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
