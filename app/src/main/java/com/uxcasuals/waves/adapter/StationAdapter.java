package com.uxcasuals.waves.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uxcasuals.waves.R;
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
import java.util.List;

/**
 * Created by Dhakchianandan on 02-07-2015.
 */
public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {

    public static interface Listener {
        public void onClick(RadioStation position);
        public void notifyModification(List<RadioStation> stations);
    }

    private List<RadioStation> stations;
    private Listener listener;

    public StationAdapter() {
    }

    public StationAdapter(List<RadioStation> stations) {
        this.stations = this.stations;
    }

    public StationAdapter(List<RadioStation> stations, Listener listener) {
        this.stations = stations;
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public List<RadioStation> getStations() {
        return stations;
    }

    public void setStations(List<RadioStation> stations) {
        this.stations = stations;
    }

    @Override
    public StationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.radio_view, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        CardView radioView = viewHolder.cardView;
        TextView radioName = (TextView)radioView.findViewById(R.id.radio_name);
        ImageView radioImage = (ImageView)radioView.findViewById(R.id.radio_image);

        final RadioStation radio = stations.get(i);
        radioName.setText(radio.getName());
        radioImage.setImageResource(R.drawable.ic_icon);
        new ImageDownloader(radioImage, radio.getLogo()).execute();
        radioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(radio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations!= null ? stations.size(): 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView view) {
            super(view);
            cardView = view;
        }
    }

    public void notifyModification(List<RadioStation> stations) {
        this.stations = stations;
        notifyDataSetChanged();
        listener.notifyModification(stations);
    }

    class ImageDownloader extends AsyncTask {
        ImageView imageView;
        String SERVER_URL;

        public ImageDownloader(ImageView imageView, String SERVER_URL) {
            this.imageView = imageView;
            this.SERVER_URL = SERVER_URL;
        }

        @Override
        protected Bitmap doInBackground(Object[] params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                int response = httpURLConnection.getResponseCode();
                if(response == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Object o) {
            Bitmap bitmap = (Bitmap) o;
            imageView.setImageBitmap(bitmap);
        }
    }
}