package com.exam.weatherexam;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.exam.weatherexam.DATABASE.SqliteDB;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity {

    ListView lvWeather;
    AsyncHttpClient weather;
    SqliteDB db;
    ImageView imgIcon;
    Button btnRefresh;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new SqliteDB(MainActivity.this);
        lvWeather = (ListView) findViewById(R.id.lvWeather);
        imgIcon = (ImageView) findViewById(R.id.imgIcon);
        weather = new AsyncHttpClient();
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        getWeather();
//        getLocation();

//        btnRefresh.setText(getDate(1524230826));
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWeather();

            }
        });
    }


    private void loadWeather() {
        Cursor weatherData = db.SelectQuery("select '' as _id,* from weather");
        String[] from = {"name", "main", "temp"};
        int[] to = {R.id.txtLoc, R.id.txtWeather, R.id.txtTemperature};
        SimpleCursorAdapter weatherAdapter = new ImageCursorAdapter(MainActivity.this, R.layout.weather_item, weatherData, from, to);
        lvWeather.setAdapter(weatherAdapter);

        lvWeather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = (Cursor) adapterView.getItemAtPosition(i);
                Intent cont = new Intent(MainActivity.this, Weather.class);
                cont.putExtra("id", c.getString(c.getColumnIndex("id")));
                startActivity(cont);
            }
        });

    }

    private void getWeather() {
        String locIds = "2643743,3067696,5391959";
        String appId = "fdf675d6082d286c5a2c60991f336e5f";
        weather.get("http://api.openweathermap.org/data/2.5/group?id=" + locIds + "&units=metric&appid=" + appId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    db.executeQuery("delete from weather");
                    JSONArray arrJson = response.getJSONArray("list");
                    progress = new ProgressDialog(MainActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false);
                    progress.show();
                    for (int i = 0; i < arrJson.length(); i++) {

                        JSONObject weather = arrJson.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                        JSONObject temperature = arrJson.getJSONObject(i).getJSONObject("main");

                        String name = arrJson.getJSONObject(i).getString("name") + ", " + arrJson.getJSONObject(i).getJSONObject("sys").getString("country");
                        String main = weather.getString("main");
                        String description = weather.getString("description");
                        String link = "http://openweathermap.org/img/w/" + weather.getString("icon") + ".png";
                        String temp = temperature.getString("temp") + " \u2103";
                        String sunset = arrJson.getJSONObject(i).getJSONObject("sys").getString("sunset");
                        String sunrise = arrJson.getJSONObject(i).getJSONObject("sys").getString("sunrise");
                        String pressure = temperature.getString("pressure");
                        String humidity = temperature.getString("humidity");
                        String speed = arrJson.getJSONObject(i).getJSONObject("wind").getString("speed");
//                        String deg = arrJson.getJSONObject(i).getJSONObject("wind").getString("deg");


                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        int SDK_INT = android.os.Build.VERSION.SDK_INT;
                        if (SDK_INT > 8) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                    .permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }
                        URL url = new URL(link);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String icon = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        db.executeQuery("insert into weather (name,main,description,icon,temp,sunrise,sunset,pressure,humidity,speed) " +
                                "values ('" + name + "','" + main + "','" + description + "','" + icon + "','" + temp + "','" + sunrise + "','" + sunset + "','" + pressure + "','" + humidity + "','" + speed + "')");

                        if (i + 1 == arrJson.length()) {
                            progress.dismiss();
                            loadWeather();
                        }
                    }

                } catch (JSONException e) {
                    progress.dismiss();
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    progress.dismiss();
                    e.printStackTrace();
                } catch (IOException e) {
                    progress.dismiss();
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

            }

            @Override
            public void onRetry(int retryNo) {

            }

        });

    }

    private void getLocation() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        Toast.makeText(MainActivity.this, longitude + "--" + latitude, Toast.LENGTH_LONG).show();

    }


}

