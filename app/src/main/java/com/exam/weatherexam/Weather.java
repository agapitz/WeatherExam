package com.exam.weatherexam;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exam.weatherexam.DATABASE.SqliteDB;

import java.util.Calendar;
import java.util.Locale;

public class Weather extends AppCompatActivity {

    TextView txtWeather, txtLoc, txtTemperature, txtDesc, txtPressure, txtHumidity, txtSunrise, txtSunset, txtWind;
    ImageView imgIcon;
    SqliteDB db;
    Button btnRefresh;
    Intent info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        db = new SqliteDB(Weather.this);

        txtWeather = (TextView) findViewById(R.id.txtWeather);
        txtLoc = (TextView) findViewById(R.id.txtLoc);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        txtPressure = (TextView) findViewById(R.id.txtPressure);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtSunrise = (TextView) findViewById(R.id.txtSunrise);
        txtSunset = (TextView) findViewById(R.id.txtSunset);
        txtWind = (TextView) findViewById(R.id.txtWind);
        imgIcon = (ImageView) findViewById(R.id.imgIcon);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        info = getIntent();
        loadInfo(info.getStringExtra("id"));


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInfo(info.getStringExtra("id"));
            }
        });


    }


    private void loadInfo(String id) {
        Cursor data = db.SelectQuery("select * from weather where id = " + id + " limit 1");
        String icon = data.getString(data.getColumnIndex("icon"));
        byte[] decodedString = Base64.decode(icon, Base64.DEFAULT);
        Bitmap bitIcon = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        imgIcon.setImageBitmap(bitIcon);
        txtWeather.setText(data.getString(data.getColumnIndex("main")));
        txtLoc.setText(data.getString(data.getColumnIndex("name")));
        txtTemperature.setText(data.getString(data.getColumnIndex("temp")));
        txtDesc.setText(data.getString(data.getColumnIndex("description")));
        txtPressure.setText(data.getString(data.getColumnIndex("pressure")));
        txtHumidity.setText(data.getString(data.getColumnIndex("humidity")));
        txtSunrise.setText(data.getString(data.getColumnIndex("sunrise")));
        txtSunset.setText(data.getString(data.getColumnIndex("sunset")));
        txtWind.setText("Speed: " + data.getString(data.getColumnIndex("speed")));
    }

}
