/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D.
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	// URL to get JSON Array
	private static String[] urls = new String[1];

	// JSON Node Names
	protected static final String TAG_USER = "user";
	protected static final String TAG_ID = "id";
	protected static final String TAG_ALTDWLIM = "alt_dl_limit";
	protected static final String TAG_DWLIM = "dl_limit";
	
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

	protected static final String TAG_INFO = "info";

	
	protected static final String TAG_ACTION 		= "action";
	protected static final String TAG_START 		= "start";
	protected static final String TAG_PAUSE 		= "pause";
	protected static final String TAG_DELETE 		= "delete";
	protected static final String TAG_DELETE_DRIVE = "deleteDrive";
	

	private static final int ACTION_CODE 			= 0;
	protected static final int START_CODE 			= 1;
	protected static final int PAUSE_CODE 			= 2;
	protected static final int DELETE_CODE 			= 3;
	protected static final int DELETE_DRIVE_CODE	= 4;
	
	protected static String hostname;
	protected static int port;
	protected static String protocol;
	protected static String username;
	protected static String password;
	protected static boolean oldVersion;
	

	// Preferences fields
	private SharedPreferences sharedPrefs;
	private StringBuilder builderPrefs;

	static myObject[] lines;
	static String[] names;

	TextView name1, size1;

	private JSONArray user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		// Get preferences
		getPreferences();
		
		// If it were awaked from an intent-filter, 
		// get intent from the intent filter adn Add URL torrent
		Intent intent = getIntent();
		String urlTorrent = intent.getDataString();
		
		if (urlTorrent != null && urlTorrent.length() != 0){		
			addTorrent(intent.getDataString());
		}
		
		// Refresh UI
		refresh();
	}

	// Rotation handling
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LinearLayout container = (LinearLayout) findViewById(R.id.container);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			container.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			container.setOrientation(LinearLayout.VERTICAL);
		}
	}

	private void refresh() {
		
		
		if(oldVersion == true){
			urls[0] = "json/events";
		}
		else{
			urls[0] = "json/torrents";
		}

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {

			// Execute the task in background
			qBittorrentTask qtt = new qBittorrentTask();

			// Connecting message
			Toast.makeText(getApplicationContext(), R.string.connecting,Toast.LENGTH_LONG).show();

			qtt.execute(urls);
		} else {

			// Connection Error message
			Toast.makeText(getApplicationContext(), R.string.connection_error,Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		 case R.id.action_add:
			 // Add URL torrent
			 addUrlTorrent();
		
		 return true;
		case R.id.action_refresh:
			// Refresh option clicked.
			refresh();
			return true;
		case R.id.action_settings:
			// Settings option clicked.
			openPreferences();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Make sure the request was successful
		if (resultCode == RESULT_OK && requestCode == ACTION_CODE) {
			
			// Get Torrent's action
			int action = data.getIntExtra(TAG_ACTION, 0);

			// Get torrent's hash
			String hash = data.getStringExtra(TAG_HASH);
			
			// Check which request we're responding to
			switch (action) {
			case START_CODE:
				startTorrent(hash);
				break;
			case PAUSE_CODE:
				pauseTorrent(hash);
				break;
			case DELETE_CODE:
				deleteTorrent(hash);
				break;
			case DELETE_DRIVE_CODE:
				deleteDriveTorrent(hash);
				break;
			}

		}

	}
	
	private void addUrlTorrent(){
		
		
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(MainActivity.this);
		View addTorrentView = li.inflate(R.layout.add_torrent, null);
		
		
		// URL input		
		final EditText urlInput = (EditText) addTorrentView.findViewById(R.id.url);
		
		// Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		
		// Set add_torrent.xml to AlertDialog builder
		builder.setView(addTorrentView);
		
		// Cancel
		builder.setNeutralButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// Ok
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User accepted the dialog											
						addTorrent(urlInput.getText().toString());
					}
				});

		// Create dialog
		AlertDialog dialog = builder.create();

		// Show dialog
		dialog.show();

		
	}

	private void openPreferences() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
		startActivity(intent);

	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		// selection.setText(items[position]);

		Intent intent = new Intent(this, TorrentActionsActivity.class);
	
		// Torrent info
		intent.putExtra(TAG_NAME, MainActivity.lines[position].getFile());
		intent.putExtra(TAG_SIZE, MainActivity.lines[position].getSize());
		intent.putExtra(TAG_INFO, MainActivity.lines[position].getInfo());
		intent.putExtra(TAG_RATIO, MainActivity.lines[position].getRatio());
		intent.putExtra(TAG_PROGRESS, MainActivity.lines[position].getProgress());
		intent.putExtra(TAG_STATE, MainActivity.lines[position].getState());
		intent.putExtra(TAG_NUMLEECHS, MainActivity.lines[position].getLeechs());
		intent.putExtra(TAG_NUMSEEDS, MainActivity.lines[position].getSeeds());
			

		intent.putExtra(TAG_HASH, MainActivity.lines[position].getHash());

		// Http client params
		intent.putExtra("hostname", hostname);
		intent.putExtra("protocol", protocol);
		intent.putExtra("port", port);
		intent.putExtra("username", username);
		intent.putExtra("password", password);
		
		startActivityForResult(intent, ACTION_CODE);	
	}
	
	public void startTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "start", hash});
	}
	
	public void pauseTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "pause", hash});
	}
	
	public void deleteTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "delete", hash});
	}
	
	public void deleteDriveTorrent(String hash) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "deleteDrive", hash});
	}

	public void addTorrent(String url) {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "addTorrent", url});
	}
		
	public void pauseAllTorrents() {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "pauseAll", null});
	}
	
	public void resumeAllTorrents() {
		// Execute the task in background
		qBittorrentCommand qtc = new qBittorrentCommand();
		qtc.execute(new String[] { "resumeAll", null});
	}
	
	// Get Preferences
	protected void getPreferences() {
		// Preferences stuff
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);

		builderPrefs = new StringBuilder();

		builderPrefs.append("\n" + sharedPrefs.getString("language", "NULL"));

		// Get values from preferences
		hostname 	= sharedPrefs.getString("hostname", "NULL");
		protocol 	= sharedPrefs.getString("protocol", "NULL");
		port 		= Integer.parseInt(sharedPrefs.getString("port", "80"));
		username 	= sharedPrefs.getString("username", "NULL");
		password 	= sharedPrefs.getString("password", "NULL");
		oldVersion 	= sharedPrefs.getBoolean("old_version", false);
	}
	
	// Here is where the action happens
	private class qBittorrentCommand extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			// Get values from preferences
			getPreferences();

			// Creating new JSON Parser
			JSONParser jParser = new JSONParser(hostname, port, username, password);
			
			jParser.postCommand(params[0], params[1]);
				
			return params[0];

		}

		@Override
		protected void onPostExecute(String result) {
			
			int messageId = R.string.connection_error;
			
			if (result == null) {
				messageId = R.string.connection_error;
			} 
			
			if ("start".equals(result)) {
				messageId = R.string.torrentStarted;
			}
			
			if ("pause".equals(result)) {
				messageId = R.string.torrentPaused;
			}
			
			if ("delete".equals(result)) {
				messageId = R.string.torrentDeleled;				
			}
			
			if ("deleteDrive".equals(result)) {
				messageId = R.string.torrentDeletedDrive;
			}
			
			if ("addTorrent".equals(result)) {
				messageId = R.string.torrentAdded;
			}
			
			Toast.makeText(getApplicationContext(),messageId, Toast.LENGTH_LONG).show();
			
		}
	}

	// Here is where the action happens
	private class qBittorrentTask extends
			AsyncTask<String, Integer, myObject[]> {

		@Override
		protected myObject[] doInBackground(String... params) {

			String name, size, info, progress, state, hash, ratio, leechs, seeds;
			
			
			myObject[] objects = null;

			// Preferences stuff
			getPreferences();

			// Creating new JSON Parser
			JSONParser jParser = new JSONParser(hostname, port, username, password);

			JSONArray jArray = jParser.getJSONArrayFromUrl(params[0]);

			if (jArray != null) {

				//Log.i("jArray length", "" + jArray.length());

				try {

					objects = new myObject[jArray.length()];

					MainActivity.names = new String[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json = jArray.getJSONObject(i);

						name 		= json.getString(TAG_NAME);
						size 		= json.getString(TAG_SIZE);
						progress 	= String.format("%.2f", json.getDouble(TAG_PROGRESS) * 100) + "%";
						info 		= size + " | D:" + json.getString(TAG_DLSPEED) + " | U:" + json.getString(TAG_UPSPEED) + " | " + progress;
						state 		= json.getString(TAG_STATE);
						hash 		= json.getString(TAG_HASH);
						ratio 		= json.getString(TAG_RATIO);
						leechs 		= json.getString(TAG_NUMLEECHS);
						seeds 		= json.getString(TAG_NUMSEEDS);
						

						objects[i]	= new myObject(name, size, state, hash,info, ratio, progress, leechs, seeds);
						
						MainActivity.names[i] = name;
					}
				} catch (JSONException e) {
					Log.e("MAIN:", e.toString());
				}

			}
			return objects;

		}

		@Override
		protected void onPostExecute(myObject[] result) {

			if (result == null) {

				Toast.makeText(getApplicationContext(), R.string.connection_error,Toast.LENGTH_LONG).show();

			} else {

				MainActivity.lines = result;

				try {
					setListAdapter(new myAdapter());

				} catch (Exception e) {
					// TODO: handle exception
					Log.e("ADAPTER", e.toString());
				}

			}
		}

	}

	class myObject {

		private String file;
		private String size;
		private String info;
		private String state;
		private String hash;
		private String downloadSpeed;
		private String ratio;
		private String progress;
		private String leechs;
		private String seeds;
	

		public myObject(String file, String size, String state, String hash, String info, String ratio, String progress, String leechs, String seeds) {
			this.file 		= file;
			this.size 		= size;
			this.state 		= state;
			this.hash 		= hash;
			this.info 		= info;
			this.ratio 		= ratio;
			this.progress	= progress;
			this.leechs		= leechs;
			this.seeds		= seeds;
		}

		public String getFile() {
			return this.file;
		}

		public String getSize() {
			return this.size;
		}

		public String getState() {
			return this.state;
		}

		public String getHash() {
			return this.hash;
		}

		public String getInfo() {
			return this.info;
		}

		public String getRatio() {
			return this.ratio;
		}

		public String getProgress() {
			return this.progress;
		}

		public String getLeechs() {
			return this.leechs;
		}

		public String getSeeds() {
			return this.seeds;
		}

		public void setInfo(String info) {
			this.info = info;
		}

	}

	class myAdapter extends ArrayAdapter<String> {
		public myAdapter() {
			// TODO Auto-generated constructor stub
			super(MainActivity.this, R.layout.row, R.id.file, names);
			Log.i("myAdapter", "lines: " + lines.length);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = super.getView(position, convertView, parent);

			String state = lines[position].getState();

			TextView info = (TextView) row.findViewById(R.id.info);

			info.setText("" + lines[position].getInfo());

			ImageView icon = (ImageView) row.findViewById(R.id.icon);

			if ("pausedUP".equals(state)) {
				icon.setImageResource(R.drawable.paused);
			}

			if ("pausedDL".equals(state)) {
				icon.setImageResource(R.drawable.paused);
			}

			if ("stalledUP".equals(state)) {
				icon.setImageResource(R.drawable.stalledup);
			}

			if ("stalledDL".equals(state)) {
				icon.setImageResource(R.drawable.stalleddl);
			}

			if ("downloading".equals(state)) {
				icon.setImageResource(R.drawable.downloading);
			}

			if ("uploading".equals(state)) {
				icon.setImageResource(R.drawable.uploading);
			}

			return (row);
		}
	}

}
