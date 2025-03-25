package itstep.learning.android_212.orm;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NbuRate {
    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT );
    public static final SimpleDateFormat dateDayMonthFormat =
            new SimpleDateFormat("dd.MM", Locale.ROOT );

    private int r030;
    private String txt;
    private double rate;
    private String cc;
    private Date exchangeDate;

    public static NbuRate fromJsonObject(JSONObject obj) throws JSONException {
        NbuRate nbuRate = new NbuRate ();
        nbuRate.setR030( obj.getInt("r030") );
        nbuRate.setTxt( obj.getString("txt" ) );
        nbuRate.setRate( obj.getDouble("rate" ) );
        nbuRate.setCc( obj.getString("cc" ) );
        try {
            nbuRate.setExchangeDate(dateFormat.parse(obj.getString ( "exchangedate")));
        }
        catch( ParseException ex ) {
            throw new JSONException(ex.getMessage());
        }
        return nbuRate;
    }

    public int getR030() {
        return r030;
    }

    public void setR030(int r030) {
        this.r030 = r030;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }
}
