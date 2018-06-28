package com.usoroos.usorosyncprototype;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.usoroos.usorosyncprototype.TCP.Client;

import java.util.Arrays;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static com.usoroos.usorosyncprototype.ExtractUrl.extractUrl;
import static com.usoroos.usorosyncprototype.R.string.message_sent;
import static com.usoroos.usorosyncprototype.R.string.server_offline;
import static com.usoroos.usorosyncprototype.R.string.share_error;
import static com.usoroos.usorosyncprototype.R.string.wifi_off;

public class MessageSender extends BroadcastReceiver {
    private static final String TAG = "UsoroMessageSender";
    private Context con;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        String icon = null;
        con = context;
        switch (Objects.requireNonNull(action)) {
            case "notification":
                byte[] byteArray = intent.getByteArrayExtra("icon");
                if (byteArray != null)
                    icon = Arrays.toString(byteArray);
                String msg = "NOTIF: "+ "IMG11: " + icon + "TITL22: " + title + "TXT33: " + text;
                sendMessage(msg);

            case "clipboard":
                msg = "CB"+text;
                sendMessage(msg);

            case "link":
                msg = extractUrl(text);
                sendMessage(msg);
        }

    }

    private void sendMessage(String msg) throws IllegalArgumentException {
        WifiManager wifi = (WifiManager) con.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = Objects.requireNonNull(wifi).isWifiEnabled();
        if (isWifiEnabled) {
            try {
                new Client(msg);
                Log.i(TAG, "Sent Message");
                Toast.makeText(con, message_sent,
                        LENGTH_SHORT).show();
            } catch (IllegalArgumentException ae) {
                Log.i(TAG, "Server is Offline");
                Toast.makeText(con, server_offline,
                        LENGTH_LONG).show();
                throw ae;
            } catch (Exception e) {
                Toast.makeText(con, share_error + e.toString(),
                        LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "No Wifi");
            Toast.makeText(con, wifi_off,
                    LENGTH_LONG).show();
        }
    }
}
