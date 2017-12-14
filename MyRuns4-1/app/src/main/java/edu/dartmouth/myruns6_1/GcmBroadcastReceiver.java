package edu.dartmouth.myruns6_1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Zizi on 2/22/2017.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //specify that GcmIntentService will handle the intent
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        //start the service
        //keep the device awake while it is launching
        startWakefulService(context,(intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
