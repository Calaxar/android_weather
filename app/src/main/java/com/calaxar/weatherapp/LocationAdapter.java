package com.calaxar.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Calum on 29/11/2017.
 */

public class LocationAdapter extends ArrayAdapter<Location> {

    Context mContext;
    int mLayoutResId;
    Location mLocations[] = null;

    public LocationAdapter(@NonNull Context context, int resource, @NonNull Location[] objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResId = resource;
        mLocations = objects;
    }

    @Nullable
    @Override
    public Location getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        //inflate layout for a single row
        LayoutInflater inflater = LayoutInflater.from(mContext);
        row = inflater.inflate(mLayoutResId, parent, false);

        //get a reference to the different view elements we wish to update
        TextView nameView = (TextView) row.findViewById(R.id.locationTextView);
        TextView coordinateView = (TextView) row.findViewById(R.id.coordinateTextView);
        TextView temperatureView = (TextView) row.findViewById(R.id.locationTemperatureTextView);
        ImageView iconView = (ImageView) row.findViewById(R.id.weatherImageView);

        //get the data from the data array
        Location location = mLocations[position];

        //setting the view to reflect the data we need to display
        nameView.setText(location.getlName());
        coordinateView.setText(location.getlLatitude() + ", " + location.getlLongitude());
        temperatureView.setText(Long.toString(location.getlForecast().getCurrentTemperature()) + '\u00B0');

        int resId = mContext.getResources().getIdentifier(location.getlForecast().getCurrentIcon(), "drawable", mContext.getPackageName());
        iconView.setImageResource(resId);

        //returning the row view
        return row;
    }
}
