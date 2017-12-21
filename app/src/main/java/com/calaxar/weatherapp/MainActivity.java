package com.calaxar.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity implements  LocationListFragment.OnLocationSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int PLACE_PICKER_REQUEST = 1; //request code for place picker
    static final String[] PREF_KEYS = new String[]{"L0", "L1", "L2", "L3", "L4", "L5", "L6", "L7", "L8", "L9"};
    static final String DEFAULT_VALUE = "default";
    static boolean mShowVisible = true;
    static FloatingActionButton fab;
    static LocationListFragment locationListFragment;
    static SharedPreferences sharedPreferences;
    static List<Location> nLocations;
    static String url = "https://api.darksky.net/forecast/" + APIKeys.weatherAPI + "/";

    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nLocations = new ArrayList<>();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        for (String s:PREF_KEYS) {
            String value = sharedPreferences.getString(s, DEFAULT_VALUE);
            if (!Objects.equals(value, DEFAULT_VALUE)) {
                String[] values = value.split("/");
                String name = values[0];
                String lat = values[1];
                String lon = values[2];
                nLocations.add(new Location(name, lat, lon));
            }
        }

        if (findViewById(R.id.fragment) != null) {
            if(savedInstanceState != null){
                return;
            }

            //Create an instance of the LocationList Fragment
            locationListFragment = new LocationListFragment();

            //In the case this activity was started with special instructions from an Intent,
            //pass the Intent's extras to the fragment as arguments
            locationListFragment.setArguments(getIntent().getExtras());

            //Ask the Fragment manager to add it to the XML Fragment
            getFragmentManager().beginTransaction().replace(R.id.fragment, locationListFragment).commit();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickLocation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetWeather().execute();
    }

    @Override
    public void onLocationSelected(int position) {
        //Create Fragment and give it an argument for the selected location right away
        LocationDetailFragment swapFragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(LocationDetailFragment.ARG_POSITION, position);
        swapFragment.setArguments(args);

        if (fab != null) fab.setVisibility(View.INVISIBLE);

        //now that the Fragment is prepared, swap it
        getFragmentManager().beginTransaction().replace(R.id.fragment, swapFragment).addToBackStack(null).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (sharedPreferences != null) {
                    final Place place = PlacePicker.getPlace(this, data);
                    final String[] name = new  String[1];
                    final CountDownLatch latch = new CountDownLatch(1);

                    Thread background = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Geocoder geocoder = new Geocoder(getBaseContext());
                                List<Address> addresses;
                                String locationName = "";
                                addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                                name[0] = addresses.get(0).getLocality();
                                latch.countDown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    background.start();
                    try {
                        latch.await();
                    } catch (InterruptedException i) {
                        i.printStackTrace();
                    }

                    String lat = String.format("%.3f", place.getLatLng().latitude);
                    String lon = String.format("%.3f", place.getLatLng().longitude);
                    if (name[0] == "") {
                        Toast.makeText(this, "Location name not processed", Toast.LENGTH_LONG).show();
                    } else {
                        if (!atCapacity()) { //if there's room in shared pref key array
                            //check if location name already exists in list
                            if (hasLocation(name[0])) {
                                Toast.makeText(this, "Location already in list", Toast.LENGTH_LONG).show();
                            } else {
                                //add location to locations array
                                Location nLocation = new Location(name[0], lat, lon);
                                nLocations.add(nLocation);
                                //update list adapter
                                ((LocationAdapter)locationListFragment.getListAdapter()).notifyDataSetChanged();
                            }
                        } else Toast.makeText(this, "Max Location capacity reached", Toast.LENGTH_LONG).show();
                    }

                } else Log.d("issue", "onActivityResult: shared pref == null");
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fab != null) fab.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(mShowVisible);
        menu.findItem(R.id.action_settings).setVisible(mShowVisible);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh) {
            new GetWeather().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        sharedPreferences.edit().clear().commit();
        String value;
        int i = 0;
        for (Location loc:nLocations) {
            value = (loc.getlName() + "/" + loc.getlLatitude() + "/" + loc.getlLongitude());
            sharedPreferences.edit().putString(PREF_KEYS[i], value).apply();
            i++;
        }

        if (pDialog != null) {
            if (pDialog.isShowing()) pDialog.dismiss();
        }

        super.onStop();
    }

    private class GetWeather extends AsyncTask<Void, Void, Void> {
        private final ArrayList<Location> newLocations = new ArrayList<>(LocationAdapter.mLocations.size());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            String jsonStr;

            for (Location l:LocationAdapter.mLocations) {
                // Making a request to url and getting response
                jsonStr = sh.makeServiceCall(url + l.getlLatitude() + "," + l.getlLongitude() + "?exclude=minutely,hourly&units=ca");

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonStr);

                        newLocations.add(new Location(l.getlName(), l.getlLatitude(), l.getlLongitude(), jsonObject));

//                        Long temp = jsonObject.getJSONObject("currently").getLong("temperature");
//                        l.getlForecast().setCurrentTemperature(temp);
                    } catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v(TAG, "Json parsing error" + e.getMessage());
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //dismiss the progress dialog
            if (pDialog.isShowing()) pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //update list adapter
                    ((LocationAdapter)locationListFragment.getListAdapter()).clear();
                    ((LocationAdapter)locationListFragment.getListAdapter()).addAll(newLocations);
                    ((LocationAdapter)locationListFragment.getListAdapter()).notifyDataSetChanged();
                }
            });
        }
    }

    private void pickLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException g) {
            g.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException g) {
            g.printStackTrace();
        }
        new GetWeather().execute();
    }

    private Boolean atCapacity() {
        if (((LocationAdapter) locationListFragment.getListAdapter()).mLocations.size() >= PREF_KEYS.length) return true;
        return false;
    }

    private Boolean hasLocation(String name) {
        for (Location l: ((LocationAdapter)locationListFragment.getListAdapter()).mLocations) {
            if (l.getlName() == name) return true;
        }
        return false;
    }

    public FloatingActionButton getFab() {
        return fab;
    }
}
