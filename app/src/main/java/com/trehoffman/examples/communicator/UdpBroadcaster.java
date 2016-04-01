package com.trehoffman.examples.communicator;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by trevor on 3/31/2016.
 */
public class UdpBroadcaster {
    private AsyncTask<Void, Void, Void> async_cient;
    public String Message;

    @SuppressLint("NewApi")
    public void Send() {
        String broadcastAddress = GetBroadcastAddress();
        if (broadcastAddress != null) {
            Log.i(MainActivity.TAG, "Got Broadcast Address: " + broadcastAddress);
            MainActivity.BROADCAST_ADDRESS = broadcastAddress;
        }

        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;

                try {
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    dp = new DatagramPacket(Message.getBytes(), Message.length(), InetAddress.getByName(MainActivity.BROADCAST_ADDRESS), MainActivity.PORT);
                    ds.setBroadcast(true);
                    ds.send(dp);

                    MainActivity.toastText = "Packet sent to: " + MainActivity.BROADCAST_ADDRESS + " " + Integer.toString(MainActivity.PORT)
                            + "\n"
                            + "Packet data: " + Message;
                    MainActivity.messageList.add(MainActivity.toastText);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        if (Build.VERSION.SDK_INT >= 11)
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else async_cient.execute();
    }

    private String GetBroadcastAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (!intf.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : intf.getInterfaceAddresses()) {
                        if (interfaceAddress != null) {
                            if (interfaceAddress.getBroadcast() != null) {
                                return interfaceAddress.getBroadcast().toString().substring(1);
                            }
                        }
                    }
                }
            }
        } catch(SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
