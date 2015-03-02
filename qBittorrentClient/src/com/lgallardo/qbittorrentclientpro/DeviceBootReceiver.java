package com.lgallardo.qbittorrentclientpro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;

/**
 * Created by lgallard on 2/28/15.
 */
// This class set the alarm (check of completed torrents) after rebooting the device
public class DeviceBootReceiver extends BroadcastReceiver {


    public static String qb_version = "3.1.x";
    public static String downloading_hashes;
    public static String completed_hashes;
    // Cookie (SID - Session ID)
    public static String cookie = null;
    protected static HashMap<String, Torrent> completed, downloading, notify;
    protected static String hostname;
    protected static String subfolder;
    protected static int port;
    protected static String protocol;
    protected static String username;
    protected static String password;
    protected static boolean https;
    protected static boolean auto_refresh;
    protected static int refresh_period;
    protected static int connection_timeout;
    protected static int data_timeout;
    protected static String sortby;
    protected static boolean reverse_order;
    protected static boolean dark_ui;
    protected static String lastState;

    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;


    // Alarm manager
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intentReceived) {

        this.context = context;

        if (intentReceived.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            // Get preferences
            getSettings();

            // Set Alarm for checking completed torrents
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotifierService.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    1200000L,
                    120000L, alarmIntent);

        }
    }

    // Get settings
    protected void getSettings() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);

        builderPrefs = new StringBuilder();

        builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

        // Get values from preferences
        hostname = sharedPrefs.getString("hostname", "");
        subfolder = sharedPrefs.getString("subfolder", "");

        protocol = sharedPrefs.getString("protocol", "NULL");

        // If user leave the field empty, set 8080 port
        try {
            port = Integer.parseInt(sharedPrefs.getString("port", "8080"));
        } catch (NumberFormatException e) {
            port = 8080;

        }
        username = sharedPrefs.getString("username", "NULL");
        password = sharedPrefs.getString("password", "NULL");
        https = sharedPrefs.getBoolean("https", false);

        // Check https
        if (https) {
            protocol = "https";

        } else {
            protocol = "http";
        }

        // Get refresg info
        auto_refresh = sharedPrefs.getBoolean("auto_refresh", true);

        try {
            refresh_period = Integer.parseInt(sharedPrefs.getString("refresh_period", "120000"));
        } catch (NumberFormatException e) {
            refresh_period = 120000;
        }

        // Get connection and data timeouts
        try {
            connection_timeout = Integer.parseInt(sharedPrefs.getString("connection_timeout", "5"));
        } catch (NumberFormatException e) {
            connection_timeout = 5;
        }

        try {
            data_timeout = Integer.parseInt(sharedPrefs.getString("data_timeout", "8"));
        } catch (NumberFormatException e) {
            data_timeout = 8;
        }

        sortby = sharedPrefs.getString("sortby", "NULL");
        reverse_order = sharedPrefs.getBoolean("reverse_order", false);

        dark_ui = sharedPrefs.getBoolean("dark_ui", false);

        qb_version = sharedPrefs.getString("qb_version", "3.1.x");


        MainActivity.cookie = sharedPrefs.getString("qbCookie", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);


    }
}
