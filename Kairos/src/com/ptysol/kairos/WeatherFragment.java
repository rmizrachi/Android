package com.ptysolutions.kairos;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View WeatherFragmentView;
    boolean FollowMe;


    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        WeatherFragmentView = inflater.inflate(R.layout.fragment_weather, container, false);
        LoadSavedPreferences();
        GetWeather();
        OnWeatherRefresh();
        return WeatherFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private  void LoadSavedPreferences(){
        try {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String address = sharedPref.getString(getString(R.string.pref_address), " ");
            FollowMe = sharedPref.getBoolean(getString(R.string.pref_follow_me), false);


            TextView TextViewAddress = (TextView) WeatherFragmentView.findViewById(R.id.textViewAddress);
            TextViewAddress.setText(address);



        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void GetWeather(){
        try {


            LocationHelper locationHelper = new LocationHelper();
            LocationInfo locationInfo =  locationHelper.GetLocationFromSavedPreferences(getActivity(),getContext());

            if(FollowMe) {
                LocationInfo currLocation = locationHelper.GetLocationByLatLong(locationInfo.Latitude, locationInfo.Longitude);
                TextView TextViewAddress = (TextView) WeatherFragmentView.findViewById(R.id.textViewAddress);
                TextViewAddress.setText(currLocation.Address);
            }



            WeatherHelper weatherHelper = new WeatherHelper();
            WeatherResult[] weatherResults = weatherHelper.GetWeather(locationInfo.Latitude, locationInfo.Longitude);

            //sometimes open weather will return yesterday's weather in first entry
            int firstForecastEntryToUse =  GetFirstForecastEntry(weatherResults[0].NormalDate);
            TextView todayWeatherDescription = (TextView) WeatherFragmentView.findViewById(R.id.textViewTodayWeatherDescription);
            todayWeatherDescription.setText(weatherResults[0].WeatherSummary);

            TextView todayMorningTemp = (TextView) WeatherFragmentView.findViewById(R.id.textViewTodayMorningTemp);
            todayMorningTemp.setText(weatherResults[firstForecastEntryToUse].MorningTemp);

            TextView todayDayTemp = (TextView) WeatherFragmentView.findViewById(R.id.textViewTodayDayTemp);
            todayDayTemp.setText(weatherResults[firstForecastEntryToUse].DayTemp);

            TextView todayNightTemp = (TextView) WeatherFragmentView.findViewById(R.id.textViewTodayNightTemp);
            todayNightTemp.setText(weatherResults[firstForecastEntryToUse].NightTemp);

            ImageView todayWeatherIcon = (ImageView) WeatherFragmentView.findViewById(R.id.imageViewTodayWeather);
            Picasso.with(WeatherFragmentView.getContext()).load( weatherResults[firstForecastEntryToUse].WeatherImgUrl ).into(todayWeatherIcon);



            TextView tomorrowWeatherDescription = (TextView) WeatherFragmentView.findViewById(R.id.textViewTomorrowWeather);
            tomorrowWeatherDescription.setText(weatherResults[firstForecastEntryToUse+1].WeatherSummary);

            TextView tomorrowMorningTemp = (TextView) WeatherFragmentView.findViewById(R.id.textViewTomorrowMorningTemp);
            tomorrowMorningTemp.setText(weatherResults[firstForecastEntryToUse+1].MorningTemp);

            TextView tomorrowDayTemp = (TextView) WeatherFragmentView.findViewById(R.id.textViewTomorrowDayTemp);
            tomorrowDayTemp.setText(weatherResults[firstForecastEntryToUse+1].DayTemp);

            TextView tomorrowNightTemp = (TextView) WeatherFragmentView.findViewById(R.id.textViewTomorrowNightTemp);
            tomorrowNightTemp.setText(weatherResults[firstForecastEntryToUse+1].NightTemp);

            ImageView tomorrowWeatherIcon = (ImageView) WeatherFragmentView.findViewById(R.id.imageViewTomorrowWeather);
            Picasso.with(WeatherFragmentView.getContext()).load( weatherResults[firstForecastEntryToUse+1].WeatherImgUrl ).into(tomorrowWeatherIcon);

            String currentDateTimeString = String.format("Last updated on %s", DateFormat.getDateTimeInstance().format(new Date()));

            TextView TextViewRefreshDate = (TextView) WeatherFragmentView.findViewById(R.id.textViewLastUpdate);
            TextViewRefreshDate.setText(currentDateTimeString);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private  int GetFirstForecastEntry(Date forecastDate)
    {
        Calendar c = Calendar.getInstance();
        int currentDOW = c.get(Calendar.DAY_OF_WEEK);

        c.setTime(forecastDate);
        int forecastDOW = c.get(Calendar.DAY_OF_WEEK);

        if(forecastDOW != currentDOW) return  1;
        return  0;

    }


    private  void OnWeatherRefresh(){
        WeatherFragmentView.findViewById(R.id.imageButtonRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetWeather();
                Toast.makeText(getActivity().getBaseContext(), "Weather updated", Toast.LENGTH_LONG).show();
            }
        });
    }

   /* private  void CreateNotification(String message, String title, int color, Date scheduleDate){
            PendingIntent pi = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), View.class), 0);

            Notification notification = new NotificationCompat.Builder(getContext())
                    .setTicker(message)
                    .setSmallIcon(R.drawable.ic_umbrella)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setColor(color)
                    .setWhen(scheduleDate.getTime())
                    .build();

            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
*/





}
