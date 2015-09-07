package com.uxcasuals.waves;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.uxcasuals.waves.adapter.StationAdapter;
import com.uxcasuals.waves.models.RadioStation;
import com.uxcasuals.waves.services.MusicService;
import com.uxcasuals.waves.utilities.DBUtilities;
import com.uxcasuals.waves.utilities.NetworkUtilities;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Logger
    private static final String TAG = MainActivity.class.getName();
    private static boolean IS_RADIO_PLAYING = false;
    private static boolean IS_RADIO_SELECTED = false;

    Messenger mMessenger = null;
    boolean mBound = false;
    List<RadioStation> stations = Collections.EMPTY_LIST;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            mBound = true;
            Log.v(TAG, "Connection Successfull..");
            sendMessage(MusicService.MEDIA_PLAYER_INIT, null);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            mBound = false;
            Log.v(TAG, "Connection Closed..");
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView radiosView = (RecyclerView) findViewById(R.id.radios_view);
        RecyclerView.LayoutManager layout = new GridLayoutManager(this, 2);
        radiosView.setLayoutManager(layout);

        final StationAdapter stationAdapter = new StationAdapter(stations);
        radiosView.setAdapter(stationAdapter);

        stationAdapter.setListener(new StationAdapter.Listener() {
            @Override
            public void onClick(RadioStation position) {

            }

            @Override
            public void notifyModification(List<RadioStation> data) {
                stations = data;
                Log.v(TAG, stations.toString());
            }
        });

        if(NetworkUtilities.getConnectionStatus(getApplicationContext())) {
            new DBUtilities(stationAdapter).execute();
        } else {
            // Todo
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mBound) {
            unbindService(connection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleMediaPlayer(View view) {
        if(MainActivity.IS_RADIO_PLAYING) {
            sendMessage(MusicService.MEDIA_PLAYER_PAUSE, null);
            MainActivity.IS_RADIO_PLAYING = false;
            return;
        } else {
            if(!MainActivity.IS_RADIO_SELECTED) {
                Bundle data = new Bundle();
                data.putString("name", stations.get(0).getName());
                data.putString("url", stations.get(0).getUrl());
                sendMessage(MusicService.MEDIA_PLAYER_PLAY, data);
                MainActivity.IS_RADIO_SELECTED = true;
                MainActivity.IS_RADIO_PLAYING = true;
                return;
            }
        }

        sendMessage(MusicService.MEDIA_PLAYER_PLAY, null);
        MainActivity.IS_RADIO_PLAYING = true;
    }

    private void sendMessage(int MESSAGE, Bundle data) {
        if(!mBound) return;

        Message message = Message.obtain(null, MESSAGE, 0, 0);
        if(data!=null) message.setData(data);
        try {
            mMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
