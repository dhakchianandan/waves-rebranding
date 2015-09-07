package com.uxcasuals.waves.utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uxcasuals.waves.adapter.StationAdapter;
import com.uxcasuals.waves.models.RadioStation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dhakchianandan on 06/09/15.
 */
public class DBUtilities extends AsyncTask {

    private static final String TAG = DBUtilities.class.getName();
    private static final String SERVER_URL = "https://uxcasuals-waves.herokuapp.com/api/stations";
    private InputStream inputStream;
    List<RadioStation> stations = Collections.EMPTY_LIST;
    StationAdapter stationAdapter;

    public DBUtilities(StationAdapter stationAdapter) {
        this.stationAdapter = stationAdapter;
    }

    @Override
    protected List<RadioStation> doInBackground(Object[] params) {

        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            int response = httpURLConnection.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                Reader reader = new InputStreamReader(inputStream);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                stations = new ArrayList<RadioStation>();
                stations = Arrays.asList(gson.fromJson(reader, RadioStation[].class));
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stations;
    }

    @Override
    protected void onPostExecute(Object o) {
        List<RadioStation> stations = (List<RadioStation>) o;
        stationAdapter.notifyModification(stations);
    }
}
