package edu.dartmouth.Zixiao.Myruns.backend.data;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import java.util.logging.Logger;

/**
 * Created by Zizi on 2/22/2017.
 */

public class ExerciseEntryEntityConverter {
    private static final Logger mLogger = Logger
            .getLogger(ExerciseEntryEntityConverter.class.getName());

    public static final String ENTITY_NAME = "ExerciseEntry";
    public static ExerciseEntry exerciseEntryFromEntity(Entity entity){
        ExerciseEntry entry = new ExerciseEntry();
        entry.setId((Long)entity.getProperty("row_id"));
        entry.setInputType(((String)entity.getProperty("inputType")));
        entry.setActivityType(((String)entity.getProperty("activityType")));
        entry.setDateTime(((Long)entity.getProperty("dateTime" )).longValue());
        entry.setDuration(((Long)entity.getProperty("duration")).intValue());
        entry.setDistance((Double)entity.getProperty("distance"));
        entry.setAvgSpeed((Double)entity.getProperty("avgSpeed"));
        entry.setCalorie(((Long)entity.getProperty("calorie" )).intValue());
        entry.setClimb((Double)entity.getProperty("climb"));
        entry.setHeartrate(((Long)entity.getProperty("heartrate")).intValue());
        entry.setComment((String)entity.getProperty("comment"));

        return entry;
    }

    public static Entity exerciseEntryToEntity(ExerciseEntry entry){
        Entity entity = new Entity(ENTITY_NAME, entry.indexID);
        long id = entry.indexID;
        //mLogger.log("" + id);

        entity.setProperty("row_id", entry.getId());
        entity.setProperty("inputType", entry.getInputType());
        entity.setProperty("activityType", entry.getActivityType());
        entity.setProperty("dateTime", entry.getDateTimeInMillis());
        entity.setProperty("duration", entry.getDuration());
        entity.setProperty("distance", entry.getDistance());
        entity.setProperty("avgSpeed", entry.getAvgSpeed());
        entity.setProperty("calorie", entry.getCalorie());
        entity.setProperty("climb", entry.getClimb());
        entity.setProperty("heartrate", entry.getHeartrate());
        entity.setProperty("comment", entry.getComment());

        return entity;
    }
}
