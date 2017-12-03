package com.calaxar.weatherapp;

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

public class MainActivity extends AppCompatActivity implements  LocationListFragment.OnLocationSelectedListener {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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


    public FloatingActionButton getFab() {
        return fab;
    }
}
