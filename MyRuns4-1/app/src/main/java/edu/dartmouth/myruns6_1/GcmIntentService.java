package edu.dartmouth.myruns6_1;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Zizi on 2/22/2017.
 */

public class GcmIntentService extends IntentService {
    private static final String TAG = "GcmIntentService: ";
    private ExerciseEntryDbHelper entryDbHelper;

    public GcmIntentService(){
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(extras != null && !extras.isEmpty()){
            if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                Log.d(TAG,"gcm received");
                int deleteId = Integer.valueOf(extras.getString("message"));
                entryDbHelper = new ExerciseEntryDbHelper(this);
                entryDbHelper.removeEntry(deleteId);
            }

        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        showToast("entry "+ extras.getString("message") + "deleted");
    }

    protected void showToast(final String message){
        new Handler(Looper.getMainLooper()).post(new Runnable(){
           @Override
            public void run(){
               Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
           }
        });
    }
}
