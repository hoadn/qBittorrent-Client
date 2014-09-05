/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclientpro;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsActivity extends PreferenceActivity implements android.content.SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		// Read preferences 
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
		Editor editor = sharedPrefs.edit();
		
		String qBittorrentServer = sharedPrefs.getString("prefSyncFrequency", "-1");
		
		boolean https = sharedPrefs.getBoolean("http", false);
		
		Log.i("Preference - prefSyncFrequency", qBittorrentServer);
		


		
	    Preference pref = findPreference("prefSyncFrequency");        
	    pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
	        @Override
	        public boolean onPreferenceChange(Preference preference, Object newValue) {
	            // do whatever you want with new value

	            // true to update the state of the Preference with the new value
	            // in case you want to disallow the change return false
	        	
	        	Log.i("Preference - key", preference.getKey());
	        	Log.i("Preference - value", newValue.toString());
	        	
	        	preference.setSummary(preference.getKey());
	        	CheckBoxPreference pref2 = (CheckBoxPreference) findPreference("https");
	        	
	        	pref2.setChecked(true);
	        	
	    		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
	    		Editor editor = sharedPrefs.edit();
	    		
	    		
	    		// Read preferences 
	    		

	
        	
	            return true;
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		
		ListPreference lp = (ListPreference) findPreference("prefSyncFrequency");
		
		
		// Save options locally
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();

		// Save key-values
//		editor.putString("global_max_num_connections", global_max_num_connections);
//		editor.putString("max_num_conn_per_torrent", max_num_conn_per_torrent);
//		editor.putString("max_num_upslots_per_torrent", max_num_upslots_per_torrent);
//		editor.putString("global_upload", global_upload);
//		editor.putString("global_download", global_download);
//		editor.putString("alt_upload", alt_upload);
//		editor.putString("alt_download", alt_download);
//		editor.putBoolean("torrent_queueing", torrent_queueing);
//		editor.putString("max_act_downloads", max_act_downloads);
//		editor.putString("max_act_uploads", max_act_uploads);
//		editor.putString("max_act_torrents", max_act_torrents);

		// Commit changes
//		editor.commit();
//		
//		
//		
//		if(lp != null){
//			
//			Log.i("Preference", lp.getValue());
//		}
//		else{
//			Log.i("Preference", "Dunno");
//		}
//			
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
	    getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	    super.onPause();
	}

}
