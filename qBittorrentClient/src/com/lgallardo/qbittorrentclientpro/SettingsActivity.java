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
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsActivity extends PreferenceActivity implements android.content.SharedPreferences.OnSharedPreferenceChangeListener {

	private ListPreference currentServer;
	private EditTextPreference hostname;
	private CheckBoxPreference https;
	private EditTextPreference port;
	private EditTextPreference username;
	private EditTextPreference password;
	private CheckBoxPreference old_version;
	private String currentServerValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		// Get preferences from screen
		currentServer = (ListPreference) findPreference("currentServer");
		hostname = (EditTextPreference) findPreference("hostname");
		https = (CheckBoxPreference) findPreference("https");
		port = (EditTextPreference) findPreference("port");
		username = (EditTextPreference) findPreference("username");
		password = (EditTextPreference) findPreference("password");
		old_version = (CheckBoxPreference) findPreference("old_version");

		// Get values for server
		 getQBittorrentServerValues(currentServer.getValue());

		Preference pref = findPreference("currentServer");
		pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// do whatever you want with new value

				// Read and load preferences
				saveQBittorrentServerValues();
				getQBittorrentServerValues(newValue.toString());
//				Log.i("Preferences", "Preferences loaded");
//				Log.i("Preferences", "currentServerValue: " + currentServer.getValue());
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub

		// Update values on Screen
		refreshScreenValues();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

		saveQBittorrentServerValues();
		super.onPause();
	}

	public void getQBittorrentServerValues(String value) {

		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

		currentServer.setSummary(currentServer.getEntry());
		hostname.setText(sharedPrefs.getString("hostname" + value, ""));
		hostname.setSummary(sharedPrefs.getString("hostname" + value, ""));

		https.setChecked(sharedPrefs.getBoolean("https" + value, false));

		port.setText(sharedPrefs.getString("port" + value, ""));
		port.setSummary(sharedPrefs.getString("port" + value, ""));

		username.setText(sharedPrefs.getString("username" + value, ""));
		username.setSummary(sharedPrefs.getString("username" + value, ""));

		password.setText(sharedPrefs.getString("password" + value, ""));
		old_version.setChecked(sharedPrefs.getBoolean("old_version" + value, false));

	}
	
	public void refreshScreenValues() {

		currentServer.setSummary(currentServer.getEntry());
		hostname.setSummary(hostname.getText());
		port.setSummary(port.getText());
		username.setSummary(username.getText());

	}

	public void saveQBittorrentServerValues() {

		currentServerValue = currentServer.getValue();

		Log.i("Preferences", "Saving Preferences");
		Log.i("Preferences", "currentServerValue: " + currentServer.getValue());

		// Save options locally
		SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

		// SharedPreferences sharedPrefs =
		// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = sharedPrefs.edit();

		if (hostname.getText().toString() != null && hostname.getText().toString() != "") {

			editor.putString("hostname" + currentServerValue, hostname.getText().toString());
			Log.i("Preferences", "Saving hostname" + currentServer.getValue());
		} 
		
		editor.putBoolean("https" + currentServerValue, https.isChecked());

		if (port.getText().toString() != null && port.getText().toString() != "") {

			editor.putString("port" + currentServerValue, port.getText().toString());
		}

		if (username.getText().toString() != null && username.getText().toString() != "") {

			editor.putString("username" + currentServerValue, username.getText().toString());
		}

		if (password.getText().toString() != null && password.getText().toString() != "") {

			editor.putString("password" + currentServerValue, password.getText().toString());
		}

		editor.putBoolean("old_version" + currentServerValue, old_version.isChecked());

		// Commit changes
		editor.commit();

	}

}
