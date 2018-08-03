package com.ptysol.kairos;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;



import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View SettingsFragmentView;

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int INITIAL_REQUEST=1337;
    private static final int LOCATION_REQUEST=INITIAL_REQUEST+3;
    LocationManager KairosLocationManager;

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10000; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 60000; // in Milliseconds


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        SettingsFragmentView = inflater.inflate(R.layout.fragment_settings, container, false);

        InitDailyUpdateValues();
        InitRefreshIntervalsValues();
        OnSaveChangesClicked();
        OnFindLocationUsingAddressClicked();
        OnFindLocationGPSClicked();
        LoadSavedChanges();
        InitializeLocationManager();

        return SettingsFragmentView;
    }

    private  void InitDailyUpdateValues(){
        Spinner spinner = (Spinner) SettingsFragmentView.findViewById(R.id.spinnerAlertTime);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.kairos_share_preferences), Context.MODE_PRIVATE);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.notification_time_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //find saved selection
        String saveIntervals = sharedPref.getString(getString(R.string.pref_alert_time), "8:00 am");
        int savedPosition = adapter.getPosition(saveIntervals);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(savedPosition,true);
    }

    private  void InitRefreshIntervalsValues(){
        Spinner spinner = (Spinner) SettingsFragmentView.findViewById(R.id.spinnerRecUpdate);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.kairos_share_preferences), Context.MODE_PRIVATE);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.refresh_interval_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //find saved selection
        String saveIntervals = sharedPref.getString(getString(R.string.pref_alert_rec_time), "12 hrs");
        int savedPosition = adapter.getPosition(saveIntervals);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(savedPosition,true);
    }


    // TODO: Rename method, update argument and hook method into UI event
  /*  public void onSaveButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    private  void OnSaveChangesClicked(){
        SettingsFragmentView.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinAlertTime = (Spinner) getActivity().findViewById(R.id.spinnerAlertTime);
                Spinner spinRecAlertTime = (Spinner) getActivity().findViewById(R.id.spinnerRecUpdate);
                Switch followMe = (Switch) getActivity().findViewById(R.id.switchFollowMe);
                EditText editTextLatitude = (EditText) getActivity().findViewById(R.id.editTextLatitude);
                EditText editTextLongitude = (EditText) getActivity().findViewById(R.id.editTextLongitude);
                EditText editTextAddress = (EditText) getActivity().findViewById(R.id.editTextAddressFormatted);

                int alertHour = GetSelectedHour(spinAlertTime.getSelectedItem().toString());
                int alterRecHour = GetRecHour(spinRecAlertTime.getSelectedItem().toString());

                //save preferences
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.kairos_share_preferences),Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString(getString(R.string.pref_alert_time),spinAlertTime.getSelectedItem().toString());
                editor.putString(getString(R.string.pref_alert_rec_time),spinRecAlertTime.getSelectedItem().toString());
                editor.putBoolean(getString(R.string.pref_follow_me), followMe.isChecked());
                editor.putString(getString(R.string.pref_latitude), editTextLatitude.getText().toString());
                editor.putString(getString(R.string.pref_longitude),editTextLongitude.getText().toString());
                editor.putString(getString(R.string.pref_address),editTextAddress.getText().toString());
                editor.putInt(getString(R.string.pref_alert_time_hr),alertHour);
                editor.putInt(getString(R.string.pref_alert_rec_time_hr),alterRecHour);

                editor.commit();

                ScheduleNotification(alertHour,alterRecHour);
                Toast.makeText(getActivity().getBaseContext(),"Preferences saved", Toast.LENGTH_LONG).show();

            }
        });
    }

    private  void LoadSavedChanges(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.kairos_share_preferences),Context.MODE_PRIVATE);;
        boolean followMe = sharedPref.getBoolean(getString(R.string.pref_follow_me), false);
        String latitude = sharedPref.getString(getString(R.string.pref_latitude), " ");
        String longitude = sharedPref.getString(getString(R.string.pref_longitude), " ");
        String address =  sharedPref.getString(getString(R.string.pref_address), " ");

        Switch followMeCtl = (Switch) SettingsFragmentView.findViewById(R.id.switchFollowMe);
        followMeCtl.setChecked(followMe);
        EditText editTextLatitude = (EditText) SettingsFragmentView.findViewById(R.id.editTextLatitude);
        editTextLatitude.setText(latitude);
        EditText editTextLongitude = (EditText) SettingsFragmentView.findViewById(R.id.editTextLongitude);
        editTextLongitude.setText(longitude);
        EditText editTextAddress = (EditText) SettingsFragmentView.findViewById(R.id.editTextAddressFormatted);
        editTextAddress.setText(address);
    }

    private  void InitializeLocationManager()
    {
        KairosLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        GrantGPSPermissions();

        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            KairosLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MINIMUM_TIME_BETWEEN_UPDATES,
                    MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                    new KairosLocationListener(getActivity())
            );
        }
        else
            Toast.makeText(getActivity().getBaseContext(),"To use GPS location, permissions are needed", Toast.LENGTH_LONG).show();

    }
    private  Integer GetSelectedHour(String hourString)
    {
        HashMap<String , Integer> hmap = new HashMap<String, Integer>();
        hmap.put("12:00 am", 0);
        hmap.put("1:00 am", 1);
        hmap.put("2:00 am", 2);
        hmap.put("3:00 am", 2);
        hmap.put("4:00 am", 4);
        hmap.put("5:00 am", 5);
        hmap.put("6:00 am", 6);
        hmap.put("7:00 am", 7);
        hmap.put("8:00 am", 8);
        hmap.put("9:00 am", 9);
        hmap.put("10:00 am", 10);
        hmap.put("11:00 am", 11);
        hmap.put("12:00 pm", 12);
        hmap.put("1:00 pm", 13);
        hmap.put("2:00 pm", 14);
        hmap.put("3:00 pm", 15);
        hmap.put("4:00 pm", 16);
        hmap.put("5:00 pm", 17);
        hmap.put("6:00 pm", 18);
        hmap.put("7:00 pm", 19);
        hmap.put("8:00 pm", 20);
        hmap.put("9:00 pm", 21);
        hmap.put("10:00 pm", 22);
        hmap.put("11:00 pm", 23);

       return hmap.get(hourString);

    }

    private  Integer GetRecHour(String recHour )
    {
        HashMap<String , Integer> hmap = new HashMap<String, Integer>();
        hmap.put("never", 0);
        hmap.put("1 hr", 1);
        hmap.put("2 hrs", 2);
        hmap.put("4 hrs", 4);
        hmap.put("6 hrs", 6);
        hmap.put("8 hrs", 8);
        hmap.put("12 hrs", 12);

        return  hmap.get(recHour);
    }


    private  void  OnFindLocationUsingAddressClicked(){
        SettingsFragmentView.findViewById(R.id.buttonFindAddressManual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*   Toast.makeText(getActivity().getBaseContext(), "Address " + address.getText().toString(), Toast.LENGTH_LONG).show();*/
                FindLocationManually();
          }
        });

    }


    private  void  OnFindLocationGPSClicked(){
        SettingsFragmentView.findViewById(R.id.buttonSearchGPS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetLocationByGPS();
              /*   Toast.makeText(getActivity().getBaseContext(), "Address " + address.getText().toString(), Toast.LENGTH_LONG).show();*/

            }
        });

    }


    private  void  GetLocationByGPS(){
        try {

            boolean isGpsEnabled,isNetworkEnabled;
            Location lastKnownLocation;
            String locationProvider;

            isGpsEnabled = KairosLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = KairosLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGpsEnabled)
                locationProvider= LocationManager.GPS_PROVIDER;
            else if(isNetworkEnabled)
                locationProvider= LocationManager.NETWORK_PROVIDER;
            else
            {
                Toast.makeText(getActivity().getBaseContext(), "GPS not enabled", Toast.LENGTH_LONG).show();
                return;
            }


            if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                lastKnownLocation = KairosLocationManager.getLastKnownLocation(locationProvider);
                if(lastKnownLocation != null) {
                    LocationHelper locHelper = new LocationHelper();
                    LocationInfo locInfo = locHelper.GetLocationByLatLong(String.valueOf(lastKnownLocation.getLatitude()), String.valueOf(lastKnownLocation.getLongitude()));
                    SetFoundLocation(locInfo);
                }
                else {
                    Toast.makeText(getActivity().getBaseContext(), "Last known location not found.", Toast.LENGTH_LONG).show();
                }

            }
            else {
                Toast.makeText(getActivity().getBaseContext(),"You need to grant GPS access to be able to use this option", Toast.LENGTH_LONG).show();
            }

        }catch ( Exception ex)
        {
            ex.printStackTrace();
        }
    }



    private void FindLocationManually()
    {

        try {
            EditText address = (EditText) getActivity().findViewById(R.id.editTextAddress);
            LocationHelper locHelper = new LocationHelper();
            LocationInfo locInfo = locHelper.GetLocation(address.getText().toString());
            SetFoundLocation(locInfo);
        }
        catch ( Exception ex){
            ex.printStackTrace();
        }
    }

    private  void SetFoundLocation(LocationInfo locInfo)
    {
        EditText editTextLatitude = (EditText) getActivity().findViewById(R.id.editTextLatitude);
        editTextLatitude.setText(locInfo.Latitude);

        EditText editTextLongitude = (EditText) getActivity().findViewById(R.id.editTextLongitude);
        editTextLongitude.setText(locInfo.Longitude);

        EditText editTextAddress = (EditText) getActivity().findViewById(R.id.editTextAddressFormatted);
        editTextAddress.setText(locInfo.Address);
    }


    private void ScheduleNotification( int alertHour, int alertRecHour) {

        Calendar calendar = Calendar.getInstance();

        Intent notificationIntent = new Intent(getContext(), WeatherNotificationPublisher.class);
        notificationIntent.putExtra(WeatherNotificationPublisher.NotificationId, 1);

        //       LocationInfo locationInfo = GetLocationInfoFromSavePreferences();
//
//        notificationIntent.putExtra(WeatherNotificationPublisher.Latitude, locationInfo.Latitude);
//        notificationIntent.putExtra(WeatherNotificationPublisher.Longitude, locationInfo.Longitude);
//        notificationIntent.putExtra(WeatherNotificationPublisher.Address, locationInfo.Address);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.add(Calendar.SECOND, alertHour);
        calendar.set(Calendar.HOUR_OF_DAY, alertHour);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(  getContext().ALARM_SERVICE);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 15000, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);

        //if it is zero then don't schedule a recurring notification
        if(alertRecHour>0)
        {
            calendar.setTimeInMillis(System.currentTimeMillis());
            alarmManager = (AlarmManager) getContext().getSystemService(  getContext().ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alertRecHour*60*60*1000, pendingIntent);
        }

    }

  /*  public  LocationInfo GetLocationInfoFromSavePreferences() {
        LocationHelper locationHelper = new LocationHelper();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean followMe = sharedPref.getBoolean("followMe", false);

        //if user selected follow me, then refresh location
        if(followMe) {
            GetLocationByGPS();
        }

        return  locationHelper.GetLocationFromSavedPreferences(getActivity(),getContext());

    }
*/
    private  void GrantGPSPermissions() {

        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            return;
        }

        requestPermissions(LOCATION_PERMS,LOCATION_REQUEST);

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
}
