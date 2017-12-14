package com.calaxar.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  LocationListFragment.OnLocationSelectedListener {

    static final int PLACE_PICKER_REQUEST = 1; //request code for place picker
    static final String[] PREF_KEYS = new String[]{"L0", "L1", "L2", "L3", "L4"};
    static final HashSet<String> DEFAULT_VALUE = new HashSet<String>(1){};
    static FloatingActionButton fab;
    static LocationListFragment locationListFragment;
    static SharedPreferences sharedPreferences;
    static List<Location> nLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nLocations = new ArrayList<>();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        for (String s:PREF_KEYS) {
            HashSet<String> value = (HashSet<String>) sharedPreferences.getStringSet(s, DEFAULT_VALUE);
            if (value != DEFAULT_VALUE) {
//                Iterator<String> iterator = value.iterator();
//                String name = iterator.next();
//                String lat = iterator.next();
//                String lon = iterator.next();
                String[] values = value.toArray(new String[0]);
                String name = values[0];
                String lat = values[1];
                String lon = values[2];
                nLocations.add(new Location(name, lat, lon));
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
            getFragmentManager().beginTransaction().add(R.id.fragment, locationListFragment).commit();
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
                    Place place = PlacePicker.getPlace(this, data);
                    Log.d("Place", ("onActivityResult: " + String.format("%.3f%n", place.getLatLng().longitude)));
                    String name = place.getName().toString();
                    String lat = String.format("%.3f", place.getLatLng().latitude);
                    String lon = String.format("%.3f", place.getLatLng().longitude);

                    HashSet<String> locDetails = new HashSet<>();
                    locDetails.add(name);
                    locDetails.add(lat);
                    locDetails.add(lon);

                    if (emptyPrefKey() != null) { //if there's room in shared pref key array
                        //add location to locations array
                        Location nLocation = new Location(name, lat, lon);
                        nLocations.add(nLocation);
                        //update list adapter
                        ((LocationAdapter)locationListFragment.getListAdapter()).notifyDataSetChanged();
                    } else Toast.makeText(this, "Max Location capacity reached", Toast.LENGTH_LONG).show();
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
        HashSet<String> value = new HashSet<String>(3);
        int i = 0;
        for (Location loc:nLocations) {
            value.add(loc.getlName());
            value.add(loc.getlLatitude());
            value.add(loc.getlLongitude());
            sharedPreferences.edit().putStringSet(PREF_KEYS[i], value).apply();
            value.clear();
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

    private String emptyPrefKey() {
        for (String s:PREF_KEYS) {
            if (sharedPreferences.getStringSet(s, DEFAULT_VALUE) == DEFAULT_VALUE) {
                return s;
            }
        }
        return null;
    }

    public FloatingActionButton getFab() {
        return fab;
    }
}
