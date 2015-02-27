package com.lgallardo.qbittorrentclientpro;

import android.app.Notification;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by lgallard on 2/22/15.
 */
public class NotifierService extends BroadcastReceiver {

    public static String qb_version = "3.1.x";
    public static String downloading_hashes;
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
    protected static int httpStatusCode = 0;
    private static String[] params = new String[2];
    private static Context context;
    // Preferences fields
    private SharedPreferences sharedPrefs;
    private StringBuilder builderPrefs;
    private String qbQueryString = "query";

    public static int notifiedCount = 0;


    public NotifierService() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        getSettings();

        Log.i("onReceive", "Cookie:" + cookie);
        Log.i("onReceive", "hostname: " + hostname);
        Log.i("onReceive", "port: " + port);
        Log.i("onReceive", "usernmae: " + username);
        Log.i("onReceive", "password: " + password);
        Log.i("onReceive", "qb_version: " + qb_version);

        String state = "all";

        // Get Settings thr params?

        if (qb_version.equals("2.x")) {
            qbQueryString = "json";
            params[0] = qbQueryString + "/events";
        }

        if (qb_version.equals("3.1.x")) {
            qbQueryString = "json";
            params[0] = qbQueryString + "/torrents";
        }

        if (qb_version.equals("3.2.x")) {
            qbQueryString = "query";
            params[0] = qbQueryString + "/torrents?filter=" + state;

            if (cookie == null || cookie.equals("")) {
                new qBittorrentCookie().execute();
            }

            Log.i("onReceive", "Cookie:" + cookie);

        }

        params[1] = state;

        Log.i("onReceive", "onReceive reached");
        new FetchTorrentListTask().execute(params);

    }

    protected void getSettings() {
        // Preferences stuff
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

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


        cookie = sharedPrefs.getString("qbCookie2", null);

        // Get last state
        lastState = sharedPrefs.getString("lastState", null);

        // Get last state
        downloading_hashes = sharedPrefs.getString("downloading_hashes", "");

    }

    class FetchTorrentListTask extends AsyncTask<String, Integer, Torrent[]> {

        // Torrent Info TAGs
        protected static final String TAG_NAME = "name";
        protected static final String TAG_SIZE = "size";
        protected static final String TAG_PROGRESS = "progress";
        protected static final String TAG_STATE = "state";
        protected static final String TAG_HASH = "hash";
        protected static final String TAG_DLSPEED = "dlspeed";
        protected static final String TAG_UPSPEED = "upspeed";
        protected static final String TAG_NUMLEECHS = "num_leechs";
        protected static final String TAG_NUMSEEDS = "num_seeds";
        protected static final String TAG_RATIO = "ratio";
        protected static final String TAG_PRIORITY = "priority";
        protected static final String TAG_ETA = "eta";
//        QBServiceListener listener;

//
//        FetchTorrentListTask(QBServiceListener listener) {
//            this.listener = listener;
//        }

        @Override
        protected Torrent[] doInBackground(String... params) {

            String name, size, info, progress, state, hash, ratio, leechs, seeds, priority, eta, uploadSpeed, downloadSpeed;

            Torrent[] torrents = null;

            // Get settings
            getSettings();

            JSONParser jParser;

            int httpStatusCode = 0;

            Log.i("TorrentsCompleted", "Getting torrents");

            try {
                // Creating new JSON Parser
                jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

                jParser.setCookie(cookie);

                JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);


                if (jArray != null) {

                    torrents = new Torrent[jArray.length()];

//                    tivity.names = new String[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        name = json.getString(TAG_NAME);
                        size = json.getString(TAG_SIZE).replace(",", ".");
                        progress = String.format("%.2f", json.getDouble(TAG_PROGRESS) * 100) + "%";
                        progress = progress.replace(",", ".");
                        info = "";
                        state = json.getString(TAG_STATE);
                        hash = json.getString(TAG_HASH);
                        ratio = json.getString(TAG_RATIO).replace(",", ".");
                        leechs = json.getString(TAG_NUMLEECHS);
                        seeds = json.getString(TAG_NUMSEEDS);
                        priority = json.getString(TAG_PRIORITY);
                        eta = json.getString(TAG_ETA);
                        downloadSpeed = json.getString(TAG_DLSPEED);
                        uploadSpeed = json.getString(TAG_UPSPEED);

                        torrents[i] = new Torrent(name, size, state, hash, info, ratio, progress, leechs, seeds, priority, eta, downloadSpeed, uploadSpeed, false, false);

//                        MainActivity.names[i] = name;

                        // Get torrent generic properties

                        try {
                            // Calculate total downloaded
                            Double sizeScalar = Double.parseDouble(size.substring(0, size.indexOf(" ")));
                            String sizeUnit = size.substring(size.indexOf(" "), size.length());

                            torrents[i].setDownloaded(String.format("%.1f", sizeScalar * json.getDouble(TAG_PROGRESS)).replace(",", ".") + sizeUnit);

                        } catch (Exception e) {
                            torrents[i].setDownloaded(size);
                        }

                        // Info
                        torrents[i].setInfo(torrents[i].getDownloaded() + " " + Character.toString('\u2193') + " " + torrents[i].getDownloadSpeed() + " "
                                + Character.toString('\u2191') + " " + torrents[i].getUploadSpeed() + " " + Character.toString('\u2022') + " "
                                + torrents[i].getRatio() + " " + Character.toString('\u2022') + " " + torrents[i].getEta());

                    }

                }
            } catch (JSONParserStatusCodeException e) {
                httpStatusCode = e.getCode();
                torrents = null;
                Log.e("JSONParserStatusCode2", e.toString());

            } catch (Exception e) {
                torrents = null;
                Log.e("Binder:", e.toString());
            }

            return torrents;

        }

        @Override
        protected void onPostExecute(Torrent[] torrents) {

            Iterator it;

            completed = new HashMap<String, Torrent>();
            downloading = new HashMap<String, Torrent>();
            notify = new HashMap<String, Torrent>();

            String[] hashesArray = downloading_hashes.split("\\|");
            String downloadingHashes = null;

            String[] completedNames;


            if (torrents != null) {

                // Check torrents
                for (int i = 0; i < torrents.length; i++) {

                    // Downloading torrents
                    if (!(torrents[i].getPercentage().equals("100"))) {

                        downloading.put(torrents[i].getHash(), torrents[i]);

                        // or build new hashes string here
                        if (downloadingHashes == null) {
                            downloadingHashes = torrents[i].getHash();
                        } else {
                            downloadingHashes += "|" + torrents[i].getHash();
                        }

                    }
                    // Completed torrents
                    else {
                        completed.put(torrents[i].getHash(), torrents[i]);
                    }
                }


                // Save downloadingHashes
                sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPrefs.edit();

                // Save key-values
                editor.putString("downloading_hashes", downloadingHashes);


                // Commit changes
                editor.apply();


                // Check last seen downloading torrents
                for (int i = 0; hashesArray.length > i; i++) {

                    if (completed.containsKey(hashesArray[i])) {
                        // Add to notify (change it to notify torrents)
                        notify.put(hashesArray[i], completed.get(hashesArray[i]));
                    }
                }


                // Notify completed torrents

                if (!notify.isEmpty()) {

                    String info = "";

                    Log.i("Completed", "Downloads completed");

                    notifiedCount += notify.size();

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("from","NotifierService");
                    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    it = notify.entrySet().iterator();

                    int i = 0;

                    while (it.hasNext()) {

                        HashMap.Entry pairs = (HashMap.Entry) it.next();

                        Torrent t = (Torrent) pairs.getValue();

                        if(info.equals("")){
                            info += t.getFile();
                        }else{
                            info += ", " + t.getFile();
                        }

                        it.remove(); // avoids a ConcurrentModificationException
                    }



                    // Build notification
                    // the addAction re-use the same intent to keep the example short
                    Notification.Builder builder = new Notification.Builder(context)
                            .setContentTitle("Completed torrents")
                            .setContentText(info)
                            .setNumber(notifiedCount)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true);


                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                    Notification notification;



                    if (android.os.Build.VERSION.SDK_INT >= 16) {

                        // Define and Inbox
                        InboxStyle inbox = new Notification.InboxStyle(builder);

                        inbox.setBigContentTitle("Completed torrents");

                        completedNames = info.split(",");

                        for(int j=0; j < completedNames.length && j < 3; j++){
                            inbox.addLine(completedNames[j]);
                        }

                        inbox.setSummaryText("Total");

                        notification = inbox.build();
                    } else {
                        notification = builder.getNotification();
                    }


                    notificationManager.notify(0, notification);


//                    listener.notifyCompleted(notify);

                }




            }
        }

    }

    private class qBittorrentCookie extends AsyncTask<Void, Integer, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {

            // Get values from preferences
            getSettings();


            // Creating new JSON Parser
            JSONParser jParser = new JSONParser(hostname, subfolder, protocol, port, username, password, connection_timeout, data_timeout);

            String cookie = "";
            String api = "";


            Log.i("qBittorrentCookie =>", "qBittorrentCookie");

            try {

                cookie = jParser.getNewCookie();
//                api = jParser.getApiVersion();

            } catch (JSONParserStatusCodeException e) {

                httpStatusCode = e.getCode();

                Log.i("onReceive", "httpStatusCode: " + httpStatusCode);
//                Log.e("qBittorrentCookie", e.toString());

            }

            if (cookie == null) {
                cookie = "";

            }

            if (api == null) {
                api = "";

            }
//
//            Log.i("qBittorrentCookie", "COOKIE: " + ">" + cookie + "<");
//            Log.i("qBittorrentCookie", "API: >" + api + "<");

            return new String[]{cookie, api};

        }

        @Override
        protected void onPostExecute(String[] result) {
//            Log.i("qBittorrentCookie", "httpStatusCode:" + httpStatusCode);


            cookie = result[0];


            // Save options locally
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            // Save key-values
            editor.putString("qbCookie2", result[0]);


            // Commit changes
            editor.apply();

        }
    }

}

