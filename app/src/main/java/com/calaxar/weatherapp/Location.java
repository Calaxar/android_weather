package com.calaxar.weatherapp;

import org.json.JSONObject;

/**
 * Created by Calum on 21/11/2017.
 */

public class Location {
    private String lName;
    private String lLatitude;
    private String lLongitude;
    private Forecast lForecast;

    public Location(String lName, String lLatitude, String lLongitude) {
        //creates initial dummy text Location object to be later replaced with JSON infused Location
        this.lName = lName;
        this.lLatitude = lLatitude;
        this.lLongitude = lLongitude;
        lForecast = new Forecast();
    }

    public Location(String lName, String lLatitude, String lLongitude, JSONObject jsonObject) {
        //creates Location and give JSON data to lForecast to assign values as required
        this.lName = lName;
        this.lLatitude = lLatitude;
        this.lLongitude = lLongitude;
        lForecast = new Forecast(jsonObject);
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
