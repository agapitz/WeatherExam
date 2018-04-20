package com.exam.weatherexam.DATABASE;

/**
 * Created by acer e5-573 on 2/9/2015.
 */
public class Tables {

    public static String weather = "CREATE TABLE  weather (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name STRING DEFAULT ''," +
            "main STRING DEFAULT ''," +
            "description STRING DEFAULT ''," +
            "icon STRING DEFAULT ''," +
            "temp STRING DEFAULT ''," +

            "sunrise STRING DEFAULT ''," +
            "sunset STRING DEFAULT ''," +

            "pressure STRING DEFAULT ''," +
            "humidity STRING DEFAULT ''," +

            "speed STRING DEFAULT ''," +
            "deg STRING DEFAULT ''," +

            "rem STRING DEFAULT ''" +
            ")";

}
