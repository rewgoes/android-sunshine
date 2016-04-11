package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int DETAIL_LOADER = 1;

    final static String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMINITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_WIND_DEGREE = 7;
    private static final int COL_WEATHER_PRESSURE = 8;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView weekDayView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView windView;
        public final TextView pressureView;
        public final TextView humidityView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            weekDayView = (TextView) view.findViewById(R.id.detail_week_date_textview);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        }
    }

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");

        ViewHolder viewHolder = (ViewHolder) getView().getTag();

        if (!data.moveToFirst()) { return; }

        // Use placeholder image for now
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        long date = data.getLong(COL_WEATHER_DATE);
        String dateString = Utility.formatDate(date);

        String weekDate = Utility.getDayName(getContext(), date);
        viewHolder.weekDayView.setText(weekDate);

        String monthDate = Utility.getFormattedMonthDay(getContext(), date);
        viewHolder.dateView.setText(monthDate);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getActivity());

        // Read high temperature from cursor
        String high = Utility.formatTemperature(
                getContext(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        viewHolder.highTempView.setText(high);

        // Read low temperature from cursor
        String low = Utility.formatTemperature(
                getContext(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        viewHolder.lowTempView.setText(low);

        String humidity = String.format(
                getContext().getString(R.string.format_humidity),
                data.getFloat(COL_WEATHER_HUMINITY)
        );
        viewHolder.humidityView.setText(humidity);

        String wind = Utility.getFormattedWind(
                getContext(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_WIND_DEGREE));
        viewHolder.windView.setText(wind);

        String pressure = String.format(
                getContext().getString(R.string.format_pressure),
                data.getFloat(COL_WEATHER_PRESSURE)
        );
        viewHolder.pressureView.setText(pressure);

        mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mForecast != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
