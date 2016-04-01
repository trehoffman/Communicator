package com.trehoffman.examples.communicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by trevor on 3/31/2016.
 */
public class UdpListener {
    private AsyncTask<Void, Void, Void> async;
    private boolean Server_aktiv = true;

    @SuppressLint("NewApi")
    public void Start()
    {
        async = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                byte[] lMsg = new byte[4096];
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                DatagramSocket ds = null;

                try
                {
                    ds = new DatagramSocket(MainActivity.PORT);

                    while(Server_aktiv)
                    {
                        ds.receive(dp);

                        //Packet received
                        Log.i(MainActivity.TAG, "Packet received from: " + dp.getAddress().getHostAddress());
                        String data = new String(dp.getData()).trim();
                        Log.i(MainActivity.TAG, "Packet received; data: " + data);


                        MainActivity.toastText = "Packet received from: " + dp.getAddress().getHostAddress() + " " + Integer.toString(MainActivity.PORT)
                                + "\n"
                                + "Packet data: " + data;
                        MainActivity.messageList.add(MainActivity.toastText);


                        Handler handler =  new Handler(MainActivity.context.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                //Toast.makeText(MainActivity.context, MainActivity.toastText, Toast.LENGTH_LONG).show();
                                ((BaseAdapter) MainActivity.list.getAdapter()).notifyDataSetChanged();
                                if (MainActivity.notificationsOn) {
                                    Ringtone r = RingtoneManager.getRingtone(MainActivity.context, MainActivity.ringtoneNotification);
                                    r.play();
                                    if (MainActivity.vibrationNotifications) {
                                        Vibrator v = (Vibrator) MainActivity.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        v.vibrate(500);
                                    }
                                }
                            }
                        });

                        /*
                        Intent i = new Intent();
                        i.setAction(Main.MESSAGE_RECEIVED);
                        i.putExtra(Main.MESSAGE_STRING, new String(lMsg, 0, dp.getLength()));
                        Main.MainContext.getApplicationContext().sendBroadcast(i);
                        */
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }

                return null;
            }
        };

        if (Build.VERSION.SDK_INT >= 11) async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async.execute();
    }

    public void Stop()
    {
        Server_aktiv = false;
    }
}
