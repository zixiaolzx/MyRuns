package edu.dartmouth.myruns6_1;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Zizi on 2/7/2017.
 */

public class TrackingService extends Service implements LocationListener, SensorEventListener {

    private static final String TAG = "TrackingService: ";
    private final IBinder binder = new TrackingServiceBinder();
    //private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());
    private boolean isStarted;
    LocationManager locationManager;
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    private ExerciseEntry entry;
    public boolean sensorsRunning = false;

    private Location currentLocation;
    private Location lastLocation;
    public static final String MSG_ENTITY_UPDATE = "update";
    private onSensorChangedTask mAsyncTask;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayBlockingQueue<Double> mAccBuffer = new ArrayBlockingQueue<>(Globals.ACCELEROMETER_BUFFER_CAPACITY);

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = false;
        //mContext = getActivity();
        Log.d(TAG, "onCreate():Tracking service starts");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start(intent);

        return START_NOT_STICKY;
    }

    private void start(Intent intent) {
        if (isStarted) return;
        isStarted = true;
        entry = new ExerciseEntry();

        if (intent.getExtras().getInt(Globals.KEY_INPUT_TYPE) == Globals.INPUT_TYPE_AUTOMATIC) {
            entry.setInputType(Globals.INPUT_TYPE_AUTOMATIC);
            startSensors();
        } else {
            entry.setInputType(Globals.INPUT_TYPE_GPS);
            entry.setActivityType(intent.getExtras().getInt(Globals.KEY_ACTIVITY_TYPE));
        }
//        if (entry.getActivityType() == 0) {
//            entry.setInputType(2);
//        } else {
//            entry.setInputType(1);
//        }
        startLocationUpdate();

    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind() called");
        start(intent);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return true;
    }


    private void startLocationUpdate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> all = locationManager.getAllProviders();
        Log.d(TAG, "All available location providers: " + all.toString());

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        //check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            }
        }
        //String provider = locationManager.GPS_PROVIDER;
        String provider = locationManager.getBestProvider(criteria, true);
        Log.d(TAG, "Provider: " + provider);

        currentLocation = locationManager.getLastKnownLocation(provider);
        Log.d(TAG, "currentLocation get");

        locationManager.requestLocationUpdates(provider, 200, (float) 0.5, locationListener);

    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged() is called");

            currentLocation = location;
            entry.insertLocation(currentLocation);
            updateExerciseEntry();

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);
            //check capacity
            try {
                mAccBuffer.add(new Double(m));
            } catch (IllegalStateException e) {
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (entry != null && location != null) {
            entry.insertLocation(location);
            updateExerciseEntry();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class TrackingServiceBinder extends Binder {
        public ExerciseEntry getExerciseEntry() {
            return entry;
        }

        TrackingService getService() {
            return TrackingService.this;
        }
    }

    @Override
    public void onDestroy() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(sensorsRunning) {
            sensorManager.unregisterListener(this);
            sensorsRunning = false;
        }
        isStarted = false;
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
    }

    private void startSensors() {
        sensorsRunning = true;
        Log.d(TAG,"sensor starts");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d(TAG, "registered");
//        new onSensorChangedTask().execute();
        AsyncTaskCompat.executeParallel(new onSensorChangedTask());

    }

    private class onSensorChangedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            Double[] block = new Double[Globals.ACCELEROMETER_BLOCK_CAPACITY + 1];
            int blockSize = 0;
            FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
            double max = Double.MIN_VALUE;
            Log.d(TAG, "testhere");

            while (true) {
                try {
                    if (isCancelled() == true) {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0;

                        max = 0.0;
                        for (double val : re) {
                            max = Math.max(max, val);
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            block[i] = Math.sqrt(im[i] * im[i] + re[i] * re[i]);
                            //clear the field
                            im[i] = 0.0;

                        }
                        block[Globals.ACCELEROMETER_BLOCK_CAPACITY] = max;
                        double double_activity_type = WekaClassifier1.classify(block);
                        Log.d(TAG, "weka");

                        int activity_type = (int) double_activity_type;
                        switch (activity_type) {
                            case 0:
                                activity_type = 2;
                                break;
                            case 1:
                                activity_type = 1;
                                break;
                            case 2:
                                activity_type = 0;
                                break;
                            default:
                                break;
                        }
                        entry.setActivityType(activity_type);
                        entry.addActivityCount(activity_type);
                        updateExerciseEntry();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateExerciseEntry() {
        Intent intent = new Intent(MapsActivity.EntityUpdateReceiver.class.getName());
        intent.putExtra(MSG_ENTITY_UPDATE, true);
        this.sendBroadcast(intent);
        Log.d(TAG, "send broadcast");
    }
}
