package com.trehoffman.examples.communicator;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<String> messageList = new ArrayList<String>();
    public static final String TAG = "Communicator";
    public static String BROADCAST_ADDRESS = "";
    public static int PORT = 2055;
    public static Context context;
    public static String toastText = "";
    public UdpBroadcaster ub;
    public static ListAdapter adapter;
    public static ListView list;
    public static TextInputEditText msg;
    public static Boolean notificationsOn = true;
    public static Uri ringtoneNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public static Boolean vibrationNotifications = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadSettings();

        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (ListView)findViewById(R.id.list);
        adapter = createAdapter();
        list.setAdapter(adapter);

        msg = (TextInputEditText)findViewById(R.id.message);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ub.Message = msg.getText().toString();
                msg.setText("");
                ub.Send();
                Snackbar.make(view, "Message sent to " + BROADCAST_ADDRESS, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        UdpListener ul = new UdpListener();
        ul.Start();

        ub = new UdpBroadcaster();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates and returns a list adapter for the current list activity
     * @return
     */
    protected ListAdapter createAdapter()
    {
        // Create a simple array adapter (of type string) with the test values
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.messageList);

        return adapter;
    }

    public void loadSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences((this));
        PORT = Integer.parseInt(prefs.getString("broadcast_port", "2055"));
        notificationsOn = prefs.getBoolean("notifications_new_message", true);
        vibrationNotifications = prefs.getBoolean("notifications_new_message_vibrate", true);
        ringtoneNotification = Uri.parse(prefs.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound"));
    }
}
