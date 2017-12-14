package edu.dartmouth.myruns6_1;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapDisplayActivity: ";
    private Intent serviceIntent;
    private TrackingService trackingService;
    private ExerciseEntry entry;
    private ExerciseEntryDbHelper entryDbHelper;
    private boolean isBound;
    private boolean isNewTask;
    private Marker startMarker;
    private Marker endMarker;
    private Polyline mLocationTrace;
    private int taskType;

    private TextView mapType;
    private TextView mapAvgSpeed;
    private TextView mapCurSpeed;
    private TextView mapClimb;
    private TextView mapCalorie;
    private TextView mapDistance;

    private boolean firstTime = true;

    private static final int MENU_ID_DELETE = 0;

    // BroadcastReceiver for receiving ExerciseEntry updates
    private EntityUpdateReceiver mEventUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
        Log.d(TAG, "onCreate");
        setUpMapIfNeeded();

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras == null) {
            Log.d(TAG, "extras == null");
            finish();
            return;
        }

//        if(extras.getString(Globals.EXTRA_TASK_TYPE).equals(Globals.TASK_TYPE_NEW)){
//            isNewTask = true;
//        } else {
//            isNewTask = false;
//        }

        int activity_type = extras.getInt(Globals.KEY_ACTIVITY_TYPE);
        int input_type = extras.getInt(Globals.KEY_INPUT_TYPE);
//        startTrackingService(activity_type, input_type);
        //initialize database
        entryDbHelper = new ExerciseEntryDbHelper(this);
        //initialize broadcast receiver
        mEventUpdateReceiver = new EntityUpdateReceiver();
        taskType = extras.getInt(Globals.KEY_TASK_TYPE);

        switch (taskType) {
            case Globals.TASK_TYPE_NEW:
                // start tracking service if it is a new task
                isNewTask = true;
                startTrackingService(activity_type, input_type);
                break;
            case Globals.TASK_TYPE_HISTORY:
                // show the trace on the map. tracking service is not needed
                // No longer need "Save" and "Cancel" button in history mode
                ((Button) findViewById(R.id.buttonMapSave))
                        .setVisibility(View.GONE);
                ((Button) findViewById(R.id.buttonMapCancel))
                        .setVisibility(View.GONE);

                long rowid = extras.getLong(Globals.KEY_ROWID);
                // read track from database
                try {
                    entry = entryDbHelper.fetchEntryByIndex(rowid);
                    Log.d(TAG, "rowid =  " + rowid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                finish();
                return;
        }

        Log.d(TAG, "activity created");
    }

    //handle service binding
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrackingService.TrackingServiceBinder binder = (TrackingService.TrackingServiceBinder) iBinder;
            trackingService = binder.getService();
            entry = binder.getExerciseEntry();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            trackingService = null;
            Log.d(TAG, "ServiceDisconnected");
        }
    };

    //start the service
    private void startTrackingService(int activityType, int inputType) {
        serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.putExtra(Globals.KEY_ACTIVITY_TYPE, activityType);
        serviceIntent.putExtra(Globals.KEY_INPUT_TYPE, inputType);
        Log.d(TAG, "startTrackingService called");
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
    }

    //stop the service
    private void stopTrackingService() {
        if (trackingService != null) {
            if (isBound) {
                unbindService(serviceConnection);
                isBound = false;
            }
            stopService(serviceIntent);
        }
    }

    //unbind service
    private void doUnbindService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "map ready");

//         Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mLocationTrace = mMap.addPolyline(new PolylineOptions());

        updateTrace();
        updateInfo();
    }

    private void updateTrace() {
        Log.d(TAG, "update trace");

        if (entry == null ) {
            Log.d(TAG, "entry == null");
            return;
        }

        if(entry.getLocationLatLngList().size() == 0){
            Log.d(TAG, "getLocationLatLngList().size() == 0");
            return;
        }
//get the start and end location
        LatLng start = entry.getLocationLatLngList().get(0);
        LatLng end = entry.getLocationLatLngList().get(entry.getLocationLatLngList().size() - 1);

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(end));
        //draw the trace
        mLocationTrace.setPoints(entry.getLocationLatLngList());
        if (startMarker != null) {
            startMarker.remove();
        }
        startMarker = mMap.addMarker(new MarkerOptions().position(start).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        if (endMarker != null) {
            endMarker.remove();
        }
        endMarker = mMap.addMarker(new MarkerOptions().position(end).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //zoom in and make the endPoint the center
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(end));
        if (firstTime) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 17));
            firstTime = false;
        }
        Log.d(TAG, "zoom");
    }

    //show current run infos
    private void updateInfo() {
        if (entry == null) {
            return;
        }
        Log.d(TAG, "info updated");

        String type = "Type: "
                + Utils.parseActivityType(entry.getActivityType(), this);
        String currentSpeed = "Cur_Speed: "
                + Utils.parseCurrentSpeed(entry.getCurSpeed(), getApplicationContext());
        String avgSpeed = "Avg_Speed: "
                + Utils.parseSpeed(entry.getAvgSpeed(), getApplicationContext());
        String climb = "Climb: "
                + Utils.parseClimb(entry.getClimb(), getApplicationContext());
        String calories = "Calories: "
                + Utils.parseCalories(entry.getCalorie(), getApplicationContext());
        String distance = "Distance: "
                + Utils.parseDistance(entry.getDistance(), getApplicationContext());

        mapType = (TextView) findViewById(R.id.map_type);
        mapCurSpeed = (TextView) findViewById(R.id.map_currentSpeed);
        mapAvgSpeed = (TextView) findViewById(R.id.map_avgSpeed);
        mapClimb = (TextView) findViewById(R.id.map_climb);
        mapCalorie = (TextView) findViewById(R.id.map_calorie);
        mapDistance = (TextView) findViewById(R.id.map_distance);

        mapType.setText(type);
        mapAvgSpeed.setText(avgSpeed);
        mapCurSpeed.setText(currentSpeed);
        mapClimb.setText(climb);
        mapCalorie.setText(calories);
        mapDistance.setText(distance);
    }

    //save the trace to entry
    public void onSaveClicked(View v) {
        Log.d(TAG, "Save button is clicked");
        v.setEnabled(false);

        if (entry != null) {
            if (entry.getActivityCount() != null) {
                int[] activityCount = entry.getActivityCount();

                int I = 0;
                for (int i = 1; i < activityCount.length; i++) {
                    if (activityCount[i] > activityCount[I]) {
                        I = i;
                    }
                }

                entry.setActivityType(I);
                String type = Integer.toString(I);
                Log.d(TAG, "activity type is " + type);
            }

            entry.updateDuration();
            new MapsActivity.InsertDbTask().execute(entry);
//            AsyncTaskCompat.executeParallel(new InsertDbTask(), entry);
            Log.d(TAG, "MapsActivity.InsertDbTask().execute(entry)");
        }
        stopTrackingService();
        doUnbindService();
        finish();
    }

    private class InsertDbTask extends AsyncTask<ExerciseEntry, Void, String> {
        @Override
        protected String doInBackground(ExerciseEntry... exerciseEntries) {
            //Write exercise entry object to the DB
            Log.d(TAG, "insertDBTask");
            long id = entryDbHelper.insertEntry(exerciseEntries[0]);
            Log.d(TAG, "afterinsertDBTask");
            return "Entry " + id + " saved.";
        }
        @Override
        protected void onPostExecute(String info) {
            Toast.makeText(MapsActivity.this, info, Toast.LENGTH_SHORT);
        }
    }

    //when user click cancel button
    public void onCancelClicked(View v) {
        v.setEnabled(false);
        //stopTrackingService();
        finish();
    }

    //when user click back button
    @Override
    public void onBackPressed() {
        stopTrackingService();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (isNewTask) {
            IntentFilter intentFilter = new IntentFilter(
                    EntityUpdateReceiver.class.getName());
            registerReceiver(mEventUpdateReceiver, intentFilter);
        }
        // update trace
        //updateTrace();
        // update exercise stats
        //updateInfo();
        Log.d(TAG, "activity resumed");
        //super.onResume();
    }

    @Override
    protected void onPause() {
        if (taskType == Globals.TASK_TYPE_NEW) {
            unregisterReceiver(mEventUpdateReceiver);
        }
        // unbind the service to avoid bind leaking
        doUnbindService();

        Log.d(Globals.TAG, "Activity paused");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            stopTrackingService();
        }
    }

    //Sets up the map if it is possible to do so
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            Log.d(TAG, "setUpMapIfNeeded mMap == null");
            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.d(TAG, "setUpMapIfNeeded mMap != null");
                setUpMap();
                // Configure the map display options

            }
        }
    }

    private void setUpMap() {
        Log.d(TAG, "Map is being setup");
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_DELETE:
                if (entry != null) {
                    entryDbHelper.removeEntry(entry.getId());
                }
                finish();
                return true;
            default:
                finish();
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuitem;
        if (taskType == Globals.TASK_TYPE_HISTORY) {
            menuitem = menu.add(Menu.NONE, MENU_ID_DELETE, MENU_ID_DELETE, "Delete");
            menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    public class EntityUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onReceive", "onReceive");
            // update trace
            updateTrace();
            // update exercise stats
            updateInfo();
        }
    }
}
