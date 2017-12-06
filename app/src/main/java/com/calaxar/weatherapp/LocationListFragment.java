package com.calaxar.weatherapp;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        setListAdapter(new LocationAdapter(getActivity(), R.layout.location_row, MainActivity.locations));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Notify the parent of the selected item
        callback.onLocationSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_list, container, false);
    }
}
