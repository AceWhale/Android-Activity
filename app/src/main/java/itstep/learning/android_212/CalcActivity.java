package itstep.learning.android_212;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CalcActivity extends AppCompatActivity {
    private double firstOperand = 0;
    private String currentOperation = "";
    private final int maxResultLength = 12;
    //private final int maxResultDigits = 10;
    private TextView tvExpression;
    private TextView tvResult;
    private boolean needClearResult = true;
    private boolean isErrorDisplayed = false;

    @SuppressLint("DiscouragedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_calc );
        tvExpression = findViewById( R.id.calc_tv_expression );
        tvResult = findViewById( R.id.calc_tv_result );
        onClearClick(null);
        // findViewById( R.id.calc_btn_0 ).setOnClickListener( this::onDigitClick );
        for( int i = 0; i < 10; i++ ) {
            findViewById( // R.id.calc_btn_0
                    getResources().getIdentifier(
                            "calc_btn_" + i,
                            "id",
                            getPackageName()
                    )
            ).setOnClickListener( this::onDigitClick );
        }
        findViewById( R.id.calc_btn_c ).setOnClickListener( this::onClearClick );
        findViewById( R.id.calc_btn_ce ).setOnClickListener( this::onClearEntryClick );
        findViewById( R.id.calc_btn_backspace ).setOnClickListener( this::onBackspaceClick );
        findViewById( R.id.calc_btn_inv ).setOnClickListener( this::onInverseClick );
        findViewById( R.id.calc_btn_sqrt ).setOnClickListener( this::onSqrtClick );
        findViewById( R.id.calc_btn_pm ).setOnClickListener( this::onPlusMinusClick );
        findViewById( R.id.calc_btn_sub ).setOnClickListener( this::onSubtractClick);
        findViewById( R.id.calc_btn_add ).setOnClickListener( this::onPlusClick );
        findViewById( R.id.calc_btn_equal ).setOnClickListener( this::onEqualsClick );
    }

    // region Збереження та відновлення стану при змінах конфігурації
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence( "tvResult", tvResult.getText() );
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText( savedInstanceState.getCharSequence( "tvResult" ) );
    }
    // endregion


    @SuppressLint("SetTextI18n")
    private void onPlusClick(View view){
        if (isErrorDisplayed) return;

        firstOperand = Double.parseDouble(tvResult.getText().toString());
        currentOperation = "+";
        tvExpression.setText(resultFromDouble(firstOperand) + " +");
        tvResult.setText("0");
        needClearResult = true;
    }

    @SuppressLint("SetTextI18n")
    private void onSubtractClick(View view){
        if (isErrorDisplayed) return;

        firstOperand = Double.parseDouble(tvResult.getText().toString());
        currentOperation = "-";
        tvExpression.setText(resultFromDouble(firstOperand) + " -");
        tvResult.setText("0");
        needClearResult = true;
    }

    private void onEqualsClick(View view) {
        if (isErrorDisplayed || currentOperation.isEmpty()) return;

        double secondOperand = Double.parseDouble(tvResult.getText().toString());
        double result = 0;

        switch (currentOperation) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
        }

        tvResult.setText(resultFromDouble(result));
        tvExpression.setText(""); // Очистка выражения
        currentOperation = ""; // Сброс операции
        needClearResult = true;
    }

    private void onPlusMinusClick( View view ) {
        if (isErrorDisplayed) return;

        String result = tvResult.getText().toString();
        if( result.startsWith("-") ) {
            result = result.substring( 1 ) ;
        }
        else {
            result = "-" + result;
        }
        tvResult.setText( result );
    }

    private void onSqrtClick( View view ) {
        if (isErrorDisplayed) return;

        String result = tvResult.getText().toString();
        double arg = Double.parseDouble( result );
        if( arg < 0 ) {
            result = getString( R.string.calc_negative_sqrt );
            isErrorDisplayed = true;
        }
        else {
            result = resultFromDouble( Math.sqrt( arg ) );
        }
        tvResult.setText( result );
        needClearResult = true;
    }

    private void onInverseClick( View view ) {
        if( isErrorDisplayed ) return;

        String result = tvResult.getText().toString();
        double arg = Double.parseDouble( result );
        if( arg == 0 ) {
            result = getString( R.string.calc_div_zero );
            isErrorDisplayed = true;
        }
        else {
            result = resultFromDouble( 1.0 / arg ) ;
        }
        tvResult.setText( result );
        needClearResult = true;
    }

    private String resultFromDouble( double arg ) {
        String ret;
        if( arg == (int)arg ) {
            ret = String.valueOf( (int)arg );
        }
        else {
            ret = String.valueOf( arg );
        }
        int len = ret.length();
        if( len > maxResultLength ) {
            ret = ret.substring( 0, maxResultLength );
        }
        return ret;
    }

    private void onClearClick( View view ) {
        tvResult.setText("0");
        tvExpression.setText("");
        isErrorDisplayed = false;
    }
    private void onClearEntryClick( View view ) {
        tvResult.setText("0");
        isErrorDisplayed = false;
    }
    private void onBackspaceClick( View view ) {
        if( isErrorDisplayed ) {
            onClearClick( view );
            return;
        }
        String result = tvResult.getText().toString();
        int len = result.length();
        if (len <= 1) {
            result = "0";
        }
        else {
            result = result.substring(0, len - 1);
        }
        tvResult.setText(result);
        isErrorDisplayed = false;
    }

    private void onDigitClick( View view ) {
        String result;
        if( needClearResult ) {
            result = "";
            needClearResult = false;
        }
        else {
            result = tvResult.getText().toString();
            if( result.equals("0") ) {
                result = "";
            }
            else if( result.length() >= maxResultLength ) {
                return;
            }
        }
        result += ((Button) view).getText();
        tvResult.setText( result );
        isErrorDisplayed = false;
    }

}
/*
Д.З. Завершити роботу з проєктом "калькулятор"
Звернути увагу на стійкість до змін конфігурації
особливо посередині транзакцій (виконали операцію,
повернули пристрій, продовжили операцію)
 */