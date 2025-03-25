package itstep.learning.android_212;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView( R.layout.activity_main );
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //     Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //     return insets;
        // });

        findViewById( R.id.home_btn_calc ).setOnClickListener( this::onButtonCalc );
        findViewById( R.id.home_btn_game ).setOnClickListener( this::onButtonGame );
        findViewById( R.id.home_btn_anim ).setOnClickListener( this::onButtonAnim );
        findViewById( R.id.home_btn_rate ).setOnClickListener( this::onButtonRate );
        findViewById( R.id.home_btn_chat ).setOnClickListener( this::onButtonChat );
    }

    private void onButtonCalc( View view ) {
        Intent activityIntent = new Intent(
                MainActivity.this, CalcActivity.class );
        startActivity( activityIntent );
    }

    private void onButtonGame( View view ) {
        Intent activityIntent = new Intent(
                MainActivity.this, GameActivity.class );
        startActivity( activityIntent );
    }
    private void onButtonAnim( View view ) {
        Intent activityIntent = new Intent(
                MainActivity.this, AnimActivity.class );
        startActivity( activityIntent );
    }
    private void onButtonRate( View view ) {
        Intent activityIntent = new Intent(
                MainActivity.this, RateActivity.class );
        startActivity( activityIntent );
    }
    private void onButtonChat( View view ) {
        Intent activityIntent = new Intent(
                MainActivity.this, ChatActivity.class );
        startActivity( activityIntent );
    }
}
/*
Д.З. Додати до проєкту текстове поле з числом та дві кнопки "-" та "+".
Реалізувати роботу цих кнопок які зменшують
та збільшують число, до якого відносяться
 */