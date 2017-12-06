package com.calaxar.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements  LocationListFragment.OnLocationSelectedListener {

    static final int PLACE_PICKER_REQUEST = 1; //request code for place picker
    FloatingActionButton fab;
    static SharedPreferences sharedPreferences;
    static Location locations[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locations = new Location[0];
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();


        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getValue() instanceof HashSet<?>) {
                String name = entry.getKey();
                HashSet<String> coord = (HashSet<String>) entry.getValue();
                String lat = (String) coord.toArray()[0];
                String lon = (String) coord.toArray()[1];
                locations = addNewLocation(locations, new Location(name, lat, lon));
            }

        }

        if (findViewById(R.id.fragment) != null) {
            if(savedInstanceState != null){
                return;
            }

            //Create an instance of the LocationList Fragment
            LocationListFragment locationListFragment = new LocationListFragment();

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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, swapFragment).addToBackStack(null).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Log.d("Place", ("onActivityResult: " + String.format("%.3f%n", place.getLatLng().longitude)));

                HashSet<String> locDetails = new HashSet<>();
                locDetails.add(String.format("%.3f%n", place.getLatLng().latitude));
                locDetails.add(String.format("%.3f%n", place.getLatLng().longitude));

                if (sharedPreferences != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet(place.getName().toString(), locDetails);
                    editor.commit();
                }
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

    private Location[] addNewLocation(Location[] s, Location l) {
        if (s.length == 0) {
            Location[] nLocations = new  Location[1];
            nLocations[0] = l;
            return nLocations;
        }
        Location[] locations = new Location[s.length + 1];
        for (int i=0; i<s.length; i++) {
            locations[i] = s[i];
        }
        locations[s.length] = l;
        return locations;
    }

    public FloatingActionButton getFab() {
        return fab;
    }
}
