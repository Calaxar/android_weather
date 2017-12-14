package com.calaxar.weatherapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Calum on 22/11/2017.
 */

public class Forecast {


    public static final HashMap<String, String> icons = createMap();
    private ArrayList<dayForecast> weekForecast;
    private long currentTemperature;
    private String currentIcon;
    private String currentSummary;

    public Forecast() {

        currentTemperature = 20;
        currentIcon = icons.get("clear-day");
        currentSummary = "Light rain on Friday, with temperatures falling to 8°C tomorrow.";
        weekForecast = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            weekForecast.add(new dayForecast(25, 15, icons.get("partly-cloudy-day")));
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

    public class dayForecast {
        private long maxTemp;
        private long minTemp;
        private String weatherIcon;

        public dayForecast(int maxTemp, int minTemp, String weatherIcon) {
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
        HashMap<String,String> myMap = new HashMap<String,String>();
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
