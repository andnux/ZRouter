package top.andnux.zrouter;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import top.andnux.api.ZRouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        ZRouter.getInstance().with("/login/activity")
                .putString("name","张春林")
                .navigation();
    }
}
