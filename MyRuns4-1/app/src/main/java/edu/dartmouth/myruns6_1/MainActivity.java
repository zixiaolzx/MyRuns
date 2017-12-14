package edu.dartmouth.myruns6_1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import edu.dartmouth.zixiao.myruns.backend.registration.Registration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends Activity {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private ArrayList<Fragment> fragments;
    private ViewPageAdapter myRunsViewPagerAdapter;
    private HistoryTabFragment mHistoryTabFragment;
    private StartTabFragment mStartTabFragment;
    private SettingsTabFragment mSettingsTabFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

//      viewPager
        mHistoryTabFragment = new HistoryTabFragment();
        mSettingsTabFragment = new SettingsTabFragment();
        mStartTabFragment = new StartTabFragment();


        fragments = new ArrayList<Fragment>();
        fragments.add(mStartTabFragment);
        fragments.add(mHistoryTabFragment);
        fragments.add(mSettingsTabFragment);

        myRunsViewPagerAdapter =new ViewPageAdapter(getFragmentManager(),
                fragments);
        viewPager.setAdapter(myRunsViewPagerAdapter);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);


        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                switch (position){

                    case Globals.TAB_HISTORY:
                        mHistoryTabFragment.updateHistoryEntries(getApplicationContext());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });
        new GcmAsyncTask(this).execute();
    }

    private void checkPermissions() {

        if(Build.VERSION.SDK_INT < 23)
            return;

        //requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //start audio recording or whatever you planned to do
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    class GcmAsyncTask extends AsyncTask<Void, Void, String> {

        private Registration regService = null;
        private GoogleCloudMessaging gcm;
        private Context context;

        private static final String SENDER_ID = "124454615516";

        public GcmAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(Void... params) {
            if(regService == null){
                Registration.Builder builder =
                        new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://adroit-archive-159521.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer(){
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?>
                                                           abstractGoogleClientRequest) throws IOException{
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                regService = builder.build();
            }

            String message = "";
            try{
                if(gcm == null){
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                String regId = gcm.register(SENDER_ID);
                message = "Device registered, registration ID =" + regId;

                regService.register(regId).execute();
            }catch(IOException ex){
                ex.printStackTrace();
                message = "Error: " + ex.getMessage();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String message){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, message);
        }
    }

}
