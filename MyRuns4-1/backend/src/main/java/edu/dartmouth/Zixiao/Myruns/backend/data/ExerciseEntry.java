package edu.dartmouth.Zixiao.Myruns.backend.data;

/**
 * Created by Zizi on 2/7/2017.
 */

//import android.location.Location;
//
//import com.google.android.gms.maps.model.LatLng;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.text.SimpleDateFormat;
import java.util.Calendar;
@Entity
public class ExerciseEntry {

    @Id
    public Long indexID;

    public String mInputType;
    public String mActivityType;
    public Calendar mDateTime;
    public int mDuration;
    public double mDistance;
    public double mAvgSpeed;
    public int mCalorie;
    public double mClimb;
    public int mHeartRate;
    public String mComment;
    public String formatDate;

    public ExerciseEntry() {
        this.mInputType = "";
        this.mActivityType = "";
        this.mDateTime = Calendar.getInstance();
        this.mDuration = 0;
        this.mDistance = 0;
        this.mAvgSpeed = 0;
        this.mCalorie = 0;
        this.mClimb = 0;
        this.mHeartRate = 0;
        this.mComment = "";
        this.formatDate = "";

    }

    public Long getId() {
        return indexID;
    }

    public void setId(Long id) {
        this.indexID = id;
    }

    public String getInputType() {
        return mInputType;
    }

    public void setInputType(String inputType) {
        this.mInputType = inputType;
    }

    public String getActivityType() {
        return mActivityType;
    }

    public void setActivityType(String activityType) {
        this.mActivityType = activityType;
    }

    public Calendar getDateTime() {
        return mDateTime;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public long getDateTimeInMillis(){
        return mDateTime.getTimeInMillis();
    }

    public void setDateTime(long timestamp) {
        this.mDateTime.setTimeInMillis(timestamp);
        this.formatDate = getDate(timestamp,"hh:mm:ss MMM dd yyyy");

    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.mAvgSpeed = avgSpeed;
    }

    public int getCalorie() {
        return mCalorie;
    }

    public void setCalorie(int calorie) {
        this.mCalorie = calorie;
    }

    public double getClimb() {
        return mClimb;
    }

    public void setClimb(double climb) {
        this.mClimb = climb;
    }

    public int getHeartrate() {
        return mHeartRate;
    }

    public void setHeartrate(int heartrate) {
        this.mHeartRate = heartrate;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public JSONObject fromJSONObject(JSONObject obj) {
        try {
            setId(obj.getLong("id"));
            setInputType(obj.getString("inputType"));
            setActivityType(obj.getString("activityType"));
            setDateTime(obj.getLong("dateTime"));
            setDuration(obj.getInt("duration"));
            setDistance(obj.getDouble("distance"));
            setAvgSpeed(obj.getDouble("avgSpeed"));
            setCalorie(obj.getInt("calorie"));
            setClimb(obj.getDouble("climb"));
            setHeartrate(obj.getInt("heartrate"));
            setComment(obj.getString("comment"));
        } catch (JSONException e) {
            return null;
        }
        return obj;
    }



}
