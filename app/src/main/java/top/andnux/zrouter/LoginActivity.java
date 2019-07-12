package top.andnux.zrouter;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import top.andnux.annotation.AutoValue;
import top.andnux.annotation.Router;
import top.andnux.api.ZRouter;

@Router("/login/activity")
public class LoginActivity extends AppCompatActivity {

    @AutoValue()
    private String id;

    @AutoValue()
    private String name;

    private Integer aaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ZRouter.getInstance().inject(this);
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }
}
