package edu.dartmouth.Zixiao.Myruns.backend;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.repackaged.com.google.gson.JsonArray;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.Zixiao.Myruns.backend.data.EntryDataStore;

/**
 * Created by Zizi on 2/23/2017.
 */

public class UpdateServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String msg = request.getParameter("JSON");
        if(msg == null && !msg.equals("")){
            return;
        }
        EntryDataStore.deleteAll();
        try{
            JSONArray jsonArray = new JSONArray(msg);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                EntryDataStore.add(jsonObject);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }

    }
