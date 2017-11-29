package com.calaxar.weatherapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Calum on 22/11/2017.
 */

public class Forecast {

    private ArrayList<dayForecast> weekForecast;
    private long currentTemperature;
    private String currentIcon;
    private String currentSummary;

    public Forecast() {

        currentTemperature = 20;
        currentIcon = "sunny";
        currentSummary = "Light rain on Friday, with temperatures falling to 8°C tomorrow.";
        weekForecast = new ArrayList<>();

        for (int i = 1; i < 7; i++) {
            weekForecast.add(new dayForecast(25, 15, "sunny"));
        }
//      try{
//            currentTemperature = j.getLong("temperature");
//            currentIcon = j.getString("icon");
//            JSONArray dayArray = j.getJSONObject("daily").getJSONArray("data");
//            for (int i = 1; i < 7; i++) {
//                JSONObject day = dayArray.getJSONObject(i);
//                int maxTemp = (int) day.getLong("temperatureHigh");
//                int minTemp = (int) day.getLong("temperatureLow");
//                String icon = day.getString("icon");
//                weekForecast.add(new dayForecast(maxTemp, minTemp, icon));
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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

}
