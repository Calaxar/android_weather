package com.calaxar.weatherapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Calum on 21/11/2017.
 */

public class Location {
    private String lName;
    private double lLatitude;
    private double lLongitude;
    private Forecast lForecast;

    public Location(String lName, double lLatitude, double lLongitude) {
        this.lName = lName;
        this.lLatitude = lLatitude;
        this.lLongitude = lLongitude;
        lForecast = new Forecast();
    }

    public void refreshForecast() {
        int currentTime = (int) (System.currentTimeMillis() / 1000L);
        lForecast = new Forecast();
    }

    public String getlName() {
        return lName;
    }

    public double getlLatitude() {
        return lLatitude;
    }

    public double getlLongitude() {
        return lLongitude;
    }

    public Forecast getlForecast() {
        return lForecast;
    }

    }
