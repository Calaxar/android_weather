package com.calaxar.weatherapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;


/**
 * Created by Calum on 22/11/2017.
 */

public class Forecast {


    public static final HashMap<String, String> icons = createMap();
    private ArrayList<dayForecast> weekForecast; //stores weather data for next 7 days
    private long currentTemperature;
    private String currentIcon;
    private String currentSummary;

    public Forecast() {
        //dummy text forecast data for later replacement
        currentTemperature = 20;
        currentIcon = icons.get("clear-day");
        currentSummary = "Light rain on Friday, with temperatures falling to 8°C tomorrow.";
        weekForecast = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            weekForecast.add(new dayForecast(25, 15, icons.get("partly-cloudy-day")));
        }
    }

    public Forecast(JSONObject jsonObject) {
        //uses JSON object to assign values as required
        try {
            currentTemperature = jsonObject.getJSONObject("currently").getLong("temperature");
            currentIcon = icons.get(jsonObject.getJSONObject("currently").getString("icon"));
            currentSummary = jsonObject.getJSONObject("daily").getString("summary");

            weekForecast = new ArrayList<>();
            JSONArray data = jsonObject.getJSONObject("daily").getJSONArray("data");
            long wMax;
            long wMin;
            String wIcon;
            for (int i = 0; i < 6; i++) {
                wMax = data.getJSONObject(i).getLong("temperatureMax");
                wMin = data.getJSONObject(i).getLong("temperatureMin");
                wIcon = data.getJSONObject(i).getString("icon");
                weekForecast.add(new dayForecast(wMax, wMin, icons.get(wIcon)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
    }

    public ArrayList<dayForecast> getWeekForecast() {
        return weekForecast;
    }

    public long getCurrentTemperature() {
        return currentTemperature;
    }

    public String getCurrentIcon() {
        return currentIcon;
    }

    public String getCurrentSummary() { return currentSummary; }

    public class dayForecast { //used to store data for a single day
        private long maxTemp;
        private long minTemp;
        private String weatherIcon;

        public dayForecast(long maxTemp, long minTemp, String weatherIcon) {
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.weatherIcon = weatherIcon;
        }

        public long getMaxTemp() {
            return maxTemp;
        }

        public long getMinTemp() {
            return minTemp;
        }

        public String getWeatherIcon() {
            return weatherIcon;
        }
    }

    private static HashMap<String, String> createMap()
    {
        //reads text provided by Dark Sky API, and returns associated drawable name
        HashMap<String,String> myMap = new HashMap<>();
        myMap.put("clear-day", "clear_day");
        myMap.put("clear-night", "clear_night");
        myMap.put("rain", "rain");
        myMap.put("snow", "snow");
        myMap.put("sleet", "sleet");
        myMap.put("wind", "wind");
        myMap.put("fog", "fog");
        myMap.put("cloudy", "cloudy");
        myMap.put("partly-cloudy-day", "cloudy_day");
        myMap.put("partly-cloudy-night", "cloudy_night");
        return myMap;
    }
}
