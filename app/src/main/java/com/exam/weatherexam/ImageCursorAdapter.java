package com.exam.weatherexam;

/**
 * Created by pccwuser on 20/04/2018.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageCursorAdapter extends SimpleCursorAdapter {

    private Cursor c;
    private Context context;

    public ImageCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.c = c;
        this.context = context;
    }

    public View getView(int pos, View inView, ViewGroup parent) {
        View v = inView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.weather_item, null);
        }
        this.c.moveToPosition(pos);
        String strName = this.c.getString(this.c.getColumnIndex("name"));
        String strMain = this.c.getString(this.c.getColumnIndex("main"));
        String strTemp = this.c.getString(this.c.getColumnIndex("temp"));
        String icon = this.c.getString(this.c.getColumnIndex("icon"));
        byte[] decodedString = Base64.decode(icon, Base64.DEFAULT);
        Bitmap bitIcon = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ImageView imgIcon = (ImageView) v.findViewById(R.id.imgIcon);
        imgIcon.setImageBitmap(bitIcon);
        TextView name = (TextView) v.findViewById(R.id.txtLoc);
        name.setText(strName);
        TextView main = (TextView) v.findViewById(R.id.txtWeather);
        main.setText(strMain);
        TextView temp = (TextView) v.findViewById(R.id.txtTemperature);
        temp.setText(strTemp);
        return (v);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, url);
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}