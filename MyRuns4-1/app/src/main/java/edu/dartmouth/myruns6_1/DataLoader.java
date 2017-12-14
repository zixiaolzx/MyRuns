package edu.dartmouth.myruns6_1;

/**
 * Created by Zizi on 2/7/2017.
 */

import java.util.ArrayList;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

public class DataLoader extends AsyncTaskLoader<ArrayList<ExerciseEntry>> {
    public Context mContext;
    public DataLoader(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<ExerciseEntry> loadInBackground() {
        Log.d("TAGG","Started");
        ExerciseEntryDbHelper mExerciseEntryDbHelper = new ExerciseEntryDbHelper(mContext);
        ArrayList<ExerciseEntry> entryList = mExerciseEntryDbHelper
                .fetchEntries();
        Log.d("TAGG","Finished");

        return entryList;
    }
}
