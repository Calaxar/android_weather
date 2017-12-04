package com.calaxar.weatherapp;

/**
 * Created by Calum on 21/11/2017.
 */

public class Location {
    private String lName;
    private String lLatitude;
    private String lLongitude;
    private Forecast lForecast;

    public Location(String lName, String lLatitude, String lLongitude) {
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

    public String getlLatitude() {
        return lLatitude;
    }

    public String getlLongitude() {
        return lLongitude;
    }

    public Forecast getlForecast() {
        return lForecast;
    }

    }
