package edu.dartmouth.myruns6_1;

/**
 * Created by Zizi on 2/23/2017.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryUploader {
    private static final String TAG = "HistoryUploader";

    private Context mContext;

    public HistoryUploader(Context context){
        this.mContext = context.getApplicationContext();
    }

    public void syncBackend(){
        new AsyncTask<Void, Void, String>(){
            @Override
            //get all entries
            protected String doInBackground(Void... args) {
                Log.d(TAG, "syncing");
                ExerciseEntryDbHelper dataStore = new ExerciseEntryDbHelper(mContext);
                dataStore.getWritableDatabase();

                List<ExerciseEntry> entryList = dataStore.fetchEntries();
                //put entries in JSONArray
                JSONArray jsonArray = new JSONArray();
                for(int i = 0; i < entryList.size(); i++){
                    ExerciseEntry entry = entryList.get(i);
                    jsonArray.put(entry.toJSONObject());
                }
                int size = entryList.size();
                Log.d(TAG,"entrylist size if:" + size);
                //send the data to updateServlet
                String syncStatus = "Sync finished";
                try{
                    Map<String, String> map = new HashMap<>();
                    map.put("JSON", jsonArray.toString());
                    ServerUtilities.post("https://adroit-archive-159521.appspot.com/update.do", map);
                }catch(IOException e){
                    syncStatus = "Sync failed:" + e.getMessage();
                }
                return syncStatus;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Log.d(TAG, result);
                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
