package com.calaxar.weatherapp;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Calum on 30/11/2017.
 */

public class LocationDetailFragment extends Fragment {
    final static String ARG_POSITION = "position";
    private int currentPosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //if we recreated this Fragment (for instance from a screen rotate)
            //restore the previous article selection by getting it here
            currentPosition = savedInstanceState.getInt(ARG_POSITION);
        }
        //inflate the view for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_location_detail, container, false);
        return myFragmentView;
    }

    public void updateLocationDetailView(int position) {
        View v = getView();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        //get a reference to the different view elements we wish to update
        ImageView currentIcon = (ImageView) v.findViewById(R.id.current_icon);
        TextView currentTemperature = (TextView) v.findViewById(R.id.current_temperature);
        TextView detailLocation = (TextView) v.findViewById(R.id.detail_location);
        TextView detailSummary = (TextView) v.findViewById(R.id.detail_summary);

        TextView day1 = (TextView) v.findViewById(R.id.day1);
        TextView day1Max = (TextView) v.findViewById(R.id.day1_max);
        ImageView day1Icon = (ImageView) v.findViewById(R.id.day1_icon);
        TextView day1Min = (TextView) v.findViewById(R.id.day1_min);

        TextView day2 = (TextView) v.findViewById(R.id.day2);
        TextView day2Max = (TextView) v.findViewById(R.id.day2_max);
        ImageView day2Icon = (ImageView) v.findViewById(R.id.day2_icon);
        TextView day2Min = (TextView) v.findViewById(R.id.day2_min);

        TextView day3 = (TextView) v.findViewById(R.id.day3);
        TextView day3Max = (TextView) v.findViewById(R.id.day3_max);
        ImageView day3Icon = (ImageView) v.findViewById(R.id.day3_icon);
        TextView day3Min = (TextView) v.findViewById(R.id.day3_min);

        TextView day4 = (TextView) v.findViewById(R.id.day4);
        TextView day4Max = (TextView) v.findViewById(R.id.day4_max);
        ImageView day4Icon = (ImageView) v.findViewById(R.id.day4_icon);
        TextView day4Min = (TextView) v.findViewById(R.id.day4_min);

        TextView day5 = (TextView) v.findViewById(R.id.day5);
        TextView day5Max = (TextView) v.findViewById(R.id.day5_max);
        ImageView day5Icon = (ImageView) v.findViewById(R.id.day5_icon);
        TextView day5Min = (TextView) v.findViewById(R.id.day5_min);

        TextView day6 = (TextView) v.findViewById(R.id.day6);
        TextView day6Max = (TextView) v.findViewById(R.id.day6_max);
        ImageView day6Icon = (ImageView) v.findViewById(R.id.day6_icon);
        TextView day6Min = (TextView) v.findViewById(R.id.day6_min);

        Location data = MainActivity.nLocations.get(position); //get reference to location data to update views

        int resId = this.getContext().getResources().getIdentifier(data.getlForecast().getCurrentIcon(), "drawable", this.getContext().getPackageName());
        currentIcon.setImageResource(resId);
        currentTemperature.setText(Long.toString(data.getlForecast().getCurrentTemperature()));
        detailLocation.setText(data.getlName());
        detailSummary.setText(data.getlForecast().getCurrentSummary());

        day1.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()));
        day1Max.setText(Long.toString(data.getlForecast().getWeekForecast().get(0).getMaxTemp()));
        resId = this.getContext().getResources().getIdentifier(data.getlForecast().getWeekForecast().get(0).getWeatherIcon(), "drawable", this.getContext().getPackageName());
        day1Icon.setImageResource(resId);
        day1Min.setText(Long.toString(data.getlForecast().getWeekForecast().get(0).getMinTemp()));
        //increment calendar by 1 day
        cal.add(Calendar.DAY_OF_YEAR, 1);

        day2.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()));
        day2Max.setText(Long.toString(data.getlForecast().getWeekForecast().get(1).getMaxTemp()));
        resId = this.getContext().getResources().getIdentifier(data.getlForecast().getWeekForecast().get(1).getWeatherIcon(), "drawable", this.getContext().getPackageName());
        day2Icon.setImageResource(resId);
        day2Min.setText(Long.toString(data.getlForecast().getWeekForecast().get(1).getMinTemp()));
        //increment calendar by 1 day
        cal.add(Calendar.DAY_OF_YEAR, 1);

        day3.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()));
        day3Max.setText(Long.toString(data.getlForecast().getWeekForecast().get(2).getMaxTemp()));
        resId = this.getContext().getResources().getIdentifier(data.getlForecast().getWeekForecast().get(2).getWeatherIcon(), "drawable", this.getContext().getPackageName());
        day3Icon.setImageResource(resId);
        day3Min.setText(Long.toString(data.getlForecast().getWeekForecast().get(2).getMinTemp()));
        //increment calendar by 1 day
        cal.add(Calendar.DAY_OF_YEAR, 1);

        day4.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()));
        day4Max.setText(Long.toString(data.getlForecast().getWeekForecast().get(3).getMaxTemp()));
        resId = this.getContext().getResources().getIdentifier(data.getlForecast().getWeekForecast().get(3).getWeatherIcon(), "drawable", this.getContext().getPackageName());
        day4Icon.setImageResource(resId);
        day4Min.setText(Long.toString(data.getlForecast().getWeekForecast().get(3).getMinTemp()));
        //increment calendar by 1 day
        cal.add(Calendar.DAY_OF_YEAR, 1);

        day5.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()));
        day5Max.setText(Long.toString(data.getlForecast().getWeekForecast().get(4).getMaxTemp()));
        resId = this.getContext().getResources().getIdentifier(data.getlForecast().getWeekForecast().get(4).getWeatherIcon(), "drawable", this.getContext().getPackageName());
        day5Icon.setImageResource(resId);
        day5Min.setText(Long.toString(data.getlForecast().getWeekForecast().get(4).getMinTemp()));
        //increment calendar by 1 day
        cal.add(Calendar.DAY_OF_YEAR, 1);

        day6.setText(new SimpleDateFormat("EE", Locale.ENGLISH).format(cal.getTime()));
        day6Max.setText(Long.toString(data.getlForecast().getWeekForecast().get(5).getMaxTemp()));
        resId = this.getContext().getResources().getIdentifier(data.getlForecast().getWeekForecast().get(5).getWeatherIcon(), "drawable", this.getContext().getPackageName());
        day6Icon.setImageResource(resId);
        day6Min.setText(Long.toString(data.getlForecast().getWeekForecast().get(5).getMinTemp()));

        currentPosition = position;
    }

    @Override
    public void onStart() {
        super.onStart();
        //During startup, we should check if there are arguments (data)
        //passed to this Fragment. We know the layout has already been
        //applied to the Fragment so we can safely call the method that
        //sets the article text

        Bundle args = getArguments();
        if (args != null) {
            //set the location based on the argument passed in
            updateLocationDetailView(args.getInt(ARG_POSITION, 1));
        } else if (currentPosition != -1) {
            Snackbar.make(this.getView(), ("current pos != -1"), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            //set the location based on the saved instance state defined during onCreateView
        }

        //make settings and refresh buttons invisible
        MainActivity.mShowVisible = false;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //set new menu which shows delete option
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the current selection for later recreation of this Fragment
        outState.putInt(ARG_POSITION, currentPosition);
    }

}
