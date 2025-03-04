package itstep.learning.android_212;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AnimActivity extends AppCompatActivity {
    private Animation alphaAnimation;
    private Animation rotateAnimation;
    private Animation scaleAnimation;
    private Animation translateAnimation;
    private Animation bellAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anim);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.demo_alpha);
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.demo_rotate);
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.demo_scale);
        translateAnimation = AnimationUtils.loadAnimation(this, R.anim.demo_translate);
        bellAnimation = AnimationUtils.loadAnimation(this, R.anim.demo_bell);
        findViewById(R.id.anim_v_alpha).setOnClickListener(this::onAnimClick);
        findViewById(R.id.anim_v_rotate).setOnClickListener(this::onRotateClick);
        findViewById(R.id.anim_v_scale).setOnClickListener(this::onScaleClick);
        findViewById(R.id.anim_v_translate).setOnClickListener(this::onTranslateClick);
        findViewById(R.id.anim_iv_bell).setOnClickListener(this::onBellClick);
    }

    private void onAnimClick(View view){
        view.startAnimation(alphaAnimation);
    }
    private void onRotateClick(View view){
        view.startAnimation(rotateAnimation);
    }
    private void onScaleClick(View view){
        view.startAnimation(scaleAnimation);
    }
    private void onTranslateClick(View view){
        if(view.getTag() instanceof Animation) {
            view.clearAnimation();
            view.setTag(null);
        }
        else {
            view.startAnimation(translateAnimation);
            view.setTag(translateAnimation);
        }
    }

    private void onBellClick(View view){
        view.startAnimation(bellAnimation);
    }
}