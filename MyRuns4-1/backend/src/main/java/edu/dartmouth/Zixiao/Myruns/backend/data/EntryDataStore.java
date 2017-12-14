package edu.dartmouth.Zixiao.Myruns.backend.data;

/**
 * Created by Zizi on 2/22/2017.
 */

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class EntryDataStore {
    private static final Logger logger = Logger.getLogger(EntryDataStore.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public static boolean add(JSONObject jsonObject) throws JSONException{
        ExerciseEntry entry = new ExerciseEntry();
        entry.fromJSONObject(jsonObject);
        Entity entity = ExerciseEntryEntityConverter.exerciseEntryToEntity(entry);
        datastore.put(entity);
        return true;
    }
    //delete an entry in the datastore
    public static boolean delete(String id){
        Query query = new Query(ExerciseEntryEntityConverter.ENTITY_NAME);
        Query.Filter filter = new Query.FilterPredicate("row_id",
                Query.FilterOperator.EQUAL, Long.parseLong(id));
        query.setFilter(filter);
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity result = preparedQuery.asSingleEntity();
        if(result == null){
            return false;
        }
        datastore.delete(result.getKey());
        return true;
    }

    public static boolean deleteAll(){
        boolean allDeleted = true;
        for(ExerciseEntry entry : fetchAll()){
            allDeleted = delete("" + entry.indexID);
        }
        return allDeleted;
    }

    public static List<ExerciseEntry> fetchAll(){
        List<ExerciseEntry> resultList = new ArrayList<ExerciseEntry>();
        Query query = new Query(ExerciseEntryEntityConverter.ENTITY_NAME);
        query.setFilter(null);
        PreparedQuery preparedQuery = datastore.prepare(query);
        for(Entity entity : preparedQuery.asIterable()){
            resultList.add(ExerciseEntryEntityConverter.exerciseEntryFromEntity(entity));
        }
        return resultList;
    }
}
