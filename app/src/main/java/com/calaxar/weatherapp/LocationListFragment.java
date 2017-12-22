package com.calaxar.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class LocationListFragment extends ListFragment {

    public interface OnLocationSelectedListener {
        void  onLocationSelected(int position);
    }

    OnLocationSelectedListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();

        //Make sure that the container Activity has implemented
        //the interface. if not, throw an exception so we can fix it
        try{
            callback = (OnLocationSelectedListener) activity;
        }catch(ClassCastException e ){
            throw new ClassCastException(activity.toString() + "must implement OnLocationSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //set new adapter containing locations stored in nLocations from sharedPreferences
        setListAdapter(new LocationAdapter(getActivity(), R.layout.location_row, MainActivity.nLocations));
        //show fab for adding new locations
        MainActivity.fab.show();
    }

    @Override
    public void onStart() {
        MainActivity.mShowVisible = true; //make settings and refresh buttons visible to user
        getActivity().invalidateOptionsMenu(); //refresh options menu
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_list, container, false);
    }

    @Override
    public void onResume() {
        //set long click listener to delete locations from list
        getListView().setLongClickable(true);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Delete");
                dialogBuilder.setMessage("Would you like to delete this location?");
                dialogBuilder.setPositiveButton("No", null);
                dialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.nLocations.remove(position);
                        ((LocationAdapter) getListAdapter()).notifyDataSetChanged();
                    }
                });
                dialogBuilder.create().show();
                return true;
            }
        });
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Notify the parent of the selected item
        callback.onLocationSelected(position);
    }
}
