package itstep.learning.android_212;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import itstep.learning.android_212.nburate.RateAdapter;
import itstep.learning.android_212.orm.NbuRate;

public class RateActivity extends AppCompatActivity {
    private static final String nbuurl = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=%s&json";
    private LinearLayout ratesContainer;
    private RecyclerView ratesRecyclerViewRight;
    private List<NbuRate> nbuRates; // Один список для всех данных
    private Drawable rateBg;
    private Button datePickerButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private TextView selectedDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rateBg = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rate_ng
        );
        ratesContainer = findViewById(R.id.rate_container);
        ratesRecyclerViewRight = findViewById(R.id.ratesRecyclerViewRight);
        datePickerButton = findViewById(R.id.datePickerButton);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
        loadRates(dateFormat.format(calendar.getTime()));
        selectedDateTextView.setText("Дата: " + formatDateForDisplay(calendar.getTime()));
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());
                    loadRates(selectedDate);
                    selectedDateTextView.setText("Дата: " + formatDateForDisplay(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadRates(String date) {
        String url = String.format(nbuurl, date);
        new Thread(() -> {
            try (InputStream urlStream = new URL(url).openStream()) {
                String content = readStreamToString(urlStream);
                JSONArray arr = new JSONArray(content);
                nbuRates = new ArrayList<>(); // Используем один список
                for (int i = 0; i < arr.length(); i++) {
                    nbuRates.add(NbuRate.fromJsonObject(arr.getJSONObject(i)));
                }
                runOnUiThread(this::showRates);
            } catch (MalformedURLException e) {
                Log.e("RateActivity::loadRates", "MalformedURLException:" + e.getMessage());
            } catch (IOException e) {
                Log.e("RateActivity::loadRates", "IOException:" + e.getMessage());
            } catch (JSONException e) {
                Log.e("RateActivity::loadRates", "JSONException:" + e.getMessage());
            }
        }).start();
    }

    private void showRates() {
        ratesContainer.removeAllViews();
        for (NbuRate nbuRate : nbuRates) {
            ratesContainer.addView(rateView(nbuRate));
        }

        ratesRecyclerViewRight.setLayoutManager(new LinearLayoutManager(this));
        RateAdapter rightAdapter = new RateAdapter(nbuRates); // Используем тот же список
        ratesRecyclerViewRight.setAdapter(rightAdapter);
    }

    private View rateView(NbuRate nbuRate) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(10, 5, 10, 5);

        LinearLayout layout = new LinearLayout(RateActivity.this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackground(rateBg);
        layout.setLayoutParams(layoutParams);

        TextView tv = new TextView(RateActivity.this);
        tv.setText(nbuRate.getCc());
        tv.setLayoutParams(layoutParams);
        layout.addView(tv);

        tv = new TextView(RateActivity.this);
        tv.setLayoutParams(layoutParams);
        tv.setText(getString(R.string.rate_rate_tpl, nbuRate.getRate()));
        layout.addView(tv);

        layout.setTag(nbuRate);
        layout.setOnClickListener(this::onRateClick);
        return layout;
    }

    private void onRateClick(View view) {
        if (view.getTag() instanceof NbuRate) {
            NbuRate nbuRate = (NbuRate) view.getTag();
            new AlertDialog.Builder(RateActivity.this)
                    .setTitle(nbuRate.getTxt())
                    .setMessage(getString(
                            R.string.rate_alert_tpl,
                            nbuRate.getCc(),
                            nbuRate.getR030(),
                            NbuRate.dateDayMonthFormat.format(nbuRate.getExchangeDate()),
                            nbuRate.getCc(),
                            nbuRate.getRate())
                    )
                    .show();
        }
    }

    private String readStreamToString(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            byteBuilder.write(buffer, 0, len);
        }
        return byteBuilder.toString();
    }

    private String formatDateForDisplay(Date date) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return displayFormat.format(date);
    }
}