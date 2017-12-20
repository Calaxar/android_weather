package com.calaxar.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity implements  LocationListFragment.OnLocationSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int PLACE_PICKER_REQUEST = 1; //request code for place picker
    static final String[] PREF_KEYS = new String[]{"L0", "L1", "L2", "L3", "L4"};
    static final String DEFAULT_VALUE = "default";
    static boolean mShowVisible = true;
    static FloatingActionButton fab;
    static LocationListFragment locationListFragment;
    static SharedPreferences sharedPreferences;
    static List<Location> nLocations;
    static String url = "https://api.darksky.net/" + APIKeys.weatherAPI + "/";


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
        super.onStop();
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

    public String makeServiceCall(String reqUrl) {
        String response = null;

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //read the response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public FloatingActionButton getFab() {
        return fab;
    }
}
