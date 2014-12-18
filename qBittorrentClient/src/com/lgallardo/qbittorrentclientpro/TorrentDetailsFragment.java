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
package com.lgallardo.qbittorrentclientpro;

import org.json.JSONArray;
import org.json.JSONObject;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TorrentDetailsFragment extends Fragment {

	// Torrent variables
	String name, info, hash, ratio, size, state, leechs, seeds, progress, priority, savePath, creationDate, comment, totalWasted, totalUploaded,
			totalDownloaded, timeElapsed, nbConnections, shareRatio, uploadRateLimit, downloadRateLimit, downloaded, eta, downloadSpeed, uploadSpeed,
			percentage = "";

	String url;

	int position;

	JSONObject json2;

	static ContentFile[] files;
	static Tracker[] trackers;
	static String[] names, trackerNames;

	// Adapters
	myFileAdapter fileAdpater;
	myTrackerAdapter trackerAdapter;

	public TorrentDetailsFragment() {
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.torrent_details, container, false);

		savePath = "";
		creationDate = "";
		comment = "";
		uploadRateLimit = "";
		downloadRateLimit = "";
		totalWasted = "";
		totalUploaded = "";
		totalDownloaded = "";
		timeElapsed = "";
		nbConnections = "";
		shareRatio = "";

		try {

			if (savedInstanceState != null) {

				// Get saved values

				name = savedInstanceState.getString("torrentDetailName", "");
				size = savedInstanceState.getString("torrentDetailSize", "");
				hash = savedInstanceState.getString("torrentDetailHash", "");
				ratio = savedInstanceState.getString("torrentDetailRatio", "");
				state = savedInstanceState.getString("torrentDetailState", "");
				leechs = savedInstanceState.getString("torrentDetailLeechs", "");
				seeds = savedInstanceState.getString("torrentDetailSeeds", "");
				progress = savedInstanceState.getString("torrentDetailProgress", "");
				priority = savedInstanceState.getString("torrentDetailPriority", "");
				eta = savedInstanceState.getString("torrentDetailEta", "");
				uploadSpeed = savedInstanceState.getString("torrentDetailUploadSpeed", "");
				downloadSpeed = savedInstanceState.getString("torrentDetailDownloadSpeed", "");
				downloaded = savedInstanceState.getString("torrentDetailDownloaded", "");

				int index = progress.indexOf(".");

				if (index == -1) {
					index = progress.indexOf(",");

					if (index == -1) {
						index = progress.length();
					}
				}

				percentage = progress.substring(0, index);

				// return rootView;

			} else {

				// Get values from current activity
				name = MainActivity.lines[position].getFile();
				size = MainActivity.lines[position].getSize();
				hash = MainActivity.lines[position].getHash();
				ratio = MainActivity.lines[position].getRatio();
				state = MainActivity.lines[position].getState();
				leechs = MainActivity.lines[position].getLeechs();
				seeds = MainActivity.lines[position].getSeeds();
				progress = MainActivity.lines[position].getProgress();
				priority = MainActivity.lines[position].getPriority();
				eta = MainActivity.lines[position].getEta();
				uploadSpeed = MainActivity.lines[position].getUploadSpeed();
				downloadSpeed = MainActivity.lines[position].getDownloadSpeed();
				downloaded = MainActivity.lines[position].getDownloaded();

				int index = MainActivity.lines[position].getProgress().indexOf(".");

				if (index == -1) {
					index = MainActivity.lines[position].getProgress().indexOf(",");

					if (index == -1) {
						index = MainActivity.lines[position].getProgress().length();
					}
				}

				percentage = MainActivity.lines[position].getProgress().substring(0, index);
			}

			TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
			TextView sizeTextView = (TextView) rootView.findViewById(R.id.downloadedVsTotal);
			TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
			TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
			TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
			TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
			TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
			TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
			TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);

			TextView etaTextView = (TextView) rootView.findViewById(R.id.eta);
			TextView uploadSpeedTextView = (TextView) rootView.findViewById(R.id.uploadSpeed);
			TextView downloadSpeedTextView = (TextView) rootView.findViewById(R.id.DownloadSpeed);

			nameTextView.setText(name);
			ratioTextView.setText(ratio);
			stateTextView.setText(state);
			leechsTextView.setText(leechs);
			seedsTextView.setText(seeds);
			progressTextView.setText(progress);
			hashTextView.setText(hash);
			priorityTextView.setText(priority);
			etaTextView.setText(eta);

			downloadSpeedTextView.setText(Character.toString('\u2193') + " " + downloadSpeed);
			uploadSpeedTextView.setText(Character.toString('\u2191') + " " + uploadSpeed);

			// Set Downloaded vs Total size
			sizeTextView.setText(downloaded + " / " + size);

			// Set progress bar
			ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
			TextView percentageTV = (TextView) rootView.findViewById(R.id.percentage);

			progressBar.setProgress(Integer.parseInt(percentage));
			percentageTV.setText(percentage + "%");

			nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);

			if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paused, 0, 0, 0);
			}

			if ("stalledUP".equals(state)) {
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalledup, 0, 0, 0);
			}

			if ("stalledDL".equals(state)) {
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalleddl, 0, 0, 0);
			}

			if ("downloading".equals(state)) {
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloading, 0, 0, 0);
			}

			if ("uploading".equals(state)) {
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uploading, 0, 0, 0);
			}

			if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
				nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.queued, 0, 0, 0);
			}

			// Show progressBar
			if (MainActivity.progressBar != null) {
				MainActivity.progressBar.setVisibility(View.VISIBLE);
			}

			// Get Content files in background
			qBittorrentContentFile qcf = new qBittorrentContentFile();
			qcf.execute(new View[] { rootView });
			
			// Get trackers in background
			qBittorrentTrackers qt = new qBittorrentTrackers();
			qt.execute(new View[] { rootView });

			// Get General info in background
			qBittorrentGeneralInfoTask qgit = new qBittorrentGeneralInfoTask();
			qgit.execute(new View[] { rootView });

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("TorrentDetailsFragment - onCreateView", e.toString());
		}

		return rootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("torrentDetailName", name);
		outState.putString("torrentDetailSize", size);
		outState.putString("torrentDetailHash", hash);
		outState.putString("torrentDetailRatio", ratio);
		outState.putString("torrentDetailState", state);
		outState.putString("torrentDetailLeechs", leechs);
		outState.putString("torrentDetailSeeds", seeds);
		outState.putString("torrentDetailProgress", progress);
		outState.putString("torrentDetailPriority", priority);
		outState.putString("torrentDetailEta", eta);
		outState.putString("torrentDetailUploadSpeed", uploadSpeed);
		outState.putString("torrentDetailDownloadSpeed", downloadSpeed);
		outState.putString("torrentDetailDownloaded", downloaded);

	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {

			if (getActivity().findViewById(R.id.one_frame) != null) {
				menu.findItem(R.id.action_refresh).setVisible(false);
			}
			menu.findItem(R.id.action_search).setVisible(false);
			menu.findItem(R.id.action_resume_all).setVisible(false);
			menu.findItem(R.id.action_pause_all).setVisible(false);
			menu.findItem(R.id.action_add).setVisible(false);

			menu.findItem(R.id.action_resume).setVisible(true);
			menu.findItem(R.id.action_pause).setVisible(true);
			menu.findItem(R.id.action_increase_prio).setVisible(true);
			menu.findItem(R.id.action_decrease_prio).setVisible(true);
			menu.findItem(R.id.action_delete).setVisible(true);
			menu.findItem(R.id.action_delete_drive).setVisible(true);
			menu.findItem(R.id.action_download_rate_limit).setVisible(true);
			menu.findItem(R.id.action_upload_rate_limit).setVisible(true);

		}
	}

	// Here is where the action happens
	private class qBittorrentGeneralInfoTask extends AsyncTask<View, View, View[]> {

		protected View[] doInBackground(View... rootViews) {
			// Get torrent's extra info
			url = "json/propertiesGeneral/";

			try {

				JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
						MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

				json2 = jParser.getJSONFromUrl(url + hash);

				MainActivity.lines[position].setSavePath(json2.getString(MainActivity.TAG_SAVE_PATH));
				MainActivity.lines[position].setCreationDate(json2.getString(MainActivity.TAG_CREATION_DATE));
				MainActivity.lines[position].setComment(json2.getString(MainActivity.TAG_COMMENT));
				MainActivity.lines[position].setTotalWasted(json2.getString(MainActivity.TAG_TOTAL_WASTED));
				MainActivity.lines[position].setTotalUploaded(json2.getString(MainActivity.TAG_TOTAL_UPLOADED));
				MainActivity.lines[position].setTotalDownloaded(json2.getString(MainActivity.TAG_TOTAL_DOWNLOADED));
				MainActivity.lines[position].setTimeElapsed(json2.getString(MainActivity.TAG_TIME_ELAPSED));
				MainActivity.lines[position].setNbConnections(json2.getString(MainActivity.TAG_NB_CONNECTIONS));
				MainActivity.lines[position].setShareRatio(json2.getString(MainActivity.TAG_SHARE_RATIO));
				MainActivity.lines[position].setUploadLimit(json2.getString(MainActivity.TAG_UPLOAD_LIMIT));
				MainActivity.lines[position].setDownloadLimit(json2.getString(MainActivity.TAG_DOWNLOAD_LIMIT));

			} catch (Exception e) {

				Log.e("TorrentFragment:", e.toString());

			}

			return rootViews;

		}

		@Override
		protected void onPostExecute(View[] rootViews) {

			try {

				View rootView = rootViews[0];

				TextView pathTextView, creationDateTextView, commentTextView, uploadRateLimitTextView, downloadRateLimitTextView, totalWastedTextView, totalUploadedTextView, totalDownloadedTextView, timeElapsedTextView, nbConnectionsTextView, shareRatioTextView = null;

				pathTextView = (TextView) rootView.findViewById(R.id.torrentSavePath);
				creationDateTextView = (TextView) rootView.findViewById(R.id.torrentCreationDate);
				commentTextView = (TextView) rootView.findViewById(R.id.torrentComment);
				uploadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentUploadRateLimit);
				downloadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentDownloadRateLimit);
				totalWastedTextView = (TextView) rootView.findViewById(R.id.torrentTotalWasted);
				totalUploadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalUploaded);
				totalDownloadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalDownloaded);
				timeElapsedTextView = (TextView) rootView.findViewById(R.id.torrentTimeElapsed);
				nbConnectionsTextView = (TextView) rootView.findViewById(R.id.torrentNbConnections);
				shareRatioTextView = (TextView) rootView.findViewById(R.id.torrentShareRatio);

				savePath = MainActivity.lines[position].getSavePath();
				creationDate = MainActivity.lines[position].getCreationDate();
				comment = MainActivity.lines[position].getComment();
				uploadRateLimit = MainActivity.lines[position].getUploadLimit();
				downloadRateLimit = MainActivity.lines[position].getDownloadLimit();
				totalWasted = MainActivity.lines[position].getTotalWasted();
				totalUploaded = MainActivity.lines[position].getTotalUploaded();
				totalDownloaded = MainActivity.lines[position].getTotalDownloaded();
				timeElapsed = MainActivity.lines[position].getTimeElapsed();
				nbConnections = MainActivity.lines[position].getNbConnections();
				shareRatio = MainActivity.lines[position].getShareRatio();

				pathTextView.setText(savePath);
				creationDateTextView.setText(creationDate);
				commentTextView.setText(comment);
				uploadRateLimitTextView.setText(uploadRateLimit);
				downloadRateLimitTextView.setText(downloadRateLimit);
				totalWastedTextView.setText(totalWasted);
				totalUploadedTextView.setText(totalUploaded);
				totalDownloadedTextView.setText(totalDownloaded);
				timeElapsedTextView.setText(timeElapsed);
				nbConnectionsTextView.setText(nbConnections);
				shareRatioTextView.setText(shareRatio);

			} catch (Exception e) {
				// TODO Auto-generated catch block

			}

			// Hide progressBar
			if (MainActivity.progressBar != null) {
				MainActivity.progressBar.setVisibility(View.INVISIBLE);
			}

		}

	}

	// // Here is where the action happens
	private class qBittorrentContentFile extends AsyncTask<View, View, View[]> {

		String name, size;
		Double progress;
		int priority;

		protected View[] doInBackground(View... rootViews) {
			// Get torrent's extra info
			url = "json/propertiesFiles/";

			try {

				JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
						MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

				JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

				if (jArray != null) {

					files = new ContentFile[jArray.length()];
					TorrentDetailsFragment.names = new String[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json = jArray.getJSONObject(i);

						name = json.getString(MainActivity.TAG_NAME);
						size = json.getString(MainActivity.TAG_SIZE).replace(",", ".");
						progress = json.getDouble(MainActivity.TAG_PROGRESS);
						priority = json.getInt(MainActivity.TAG_PRIORITY);

						Log.i("Content", name + " " + size + " " + progress + " " + priority);

						files[i] = new ContentFile(name, size, progress, priority);
						names[i] = name;

					}

				}

			} catch (Exception e) {

				Log.e("TorrentFragment:", e.toString());

			}

			return rootViews;

		}

		@Override
		protected void onPostExecute(View[] rootViews) {

			try {

				View rootView = rootViews[0];

				for (int i = 0; i < files.length; i++) {

					Log.i("Content2", "Name: " + files[i]);

				}

				fileAdpater = new myFileAdapter(getActivity(), names, files);

				Log.i("Content2", "Count: " + fileAdpater.getCount());

				LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.files);

				// for (int i = 0; i < fileAdpater.getCount(); i++) {
				// View item = fileAdpater.getView(i, null, null);
				// layout.addView(item);
				// }

				
				ListView lv =  (ListView) rootView.findViewById(R.id.theList);
				//
				lv.setAdapter(fileAdpater);

				setListViewHeightBasedOnChildren(lv);

				layout.addView(lv, layout.getWidth(), layout.getHeight() + lv.getHeight());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Content2", e.toString());

			}

			// // Hide progressBar
			// if (MainActivity.progressBar != null) {
			// MainActivity.progressBar.setVisibility(View.INVISIBLE);
			// }

		}

	}
	
	
	// // Here is where the action happens
	private class qBittorrentTrackers extends AsyncTask<View, View, View[]> {

		String url;

		protected View[] doInBackground(View... rootViews) {
			// Get torrent's extra info
			url = "json/propertiesTrackers/";

			try {

				JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
						MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

				JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

				if (jArray != null) {

					trackers = new Tracker[jArray.length()];
					TorrentDetailsFragment.trackerNames = new String[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject json = jArray.getJSONObject(i);

						url = json.getString(MainActivity.TAG_URL);

						Log.i("Trackers", url );

						trackers[i] = new Tracker(url);
						trackerNames[i] = url;

					}

				}

			} catch (Exception e) {

				Log.e("TorrentFragment:", e.toString());

			}

			return rootViews;

		}

		@Override
		protected void onPostExecute(View[] rootViews) {

			try {

				View rootView = rootViews[0];

				for (int i = 0; i < trackers.length; i++) {

					Log.i("Trackers", "Url: " + trackers[i].getUrl());

				}

				trackerAdapter = new myTrackerAdapter(getActivity(), trackerNames, trackers);

				Log.i("Trackers", "Count: " + trackerAdapter.getCount());

				LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.trackers);

				for (int i = 0; i < trackerAdapter.getCount(); i++) {
					View item = trackerAdapter.getView(i, null, null);
					layout.addView(item);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("Trackers", e.toString());

			}

			// // Hide progressBar
			// if (MainActivity.progressBar != null) {
			// MainActivity.progressBar.setVisibility(View.INVISIBLE);
			// }

		}

	}


	class myFileAdapter extends ArrayAdapter<String> {

		private String[] filesNames;
		private ContentFile[] files;
		private Context context;

		public myFileAdapter(Context context, String[] filesNames, ContentFile[] files) {
			// TODO Auto-generated constructor stub
			super(context, R.layout.contentfile_row, R.id.file, filesNames);

			this.context = context;
			this.filesNames = filesNames;
			this.files = files;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub}
			return (filesNames != null) ? filesNames.length : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = super.getView(position, convertView, parent);

			TextView info = (TextView) row.findViewById(R.id.info);

			info.setText("" + files[position].getSize());

			// Set progress bar
			ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
			TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

			int index = files[position].getProgressAsString().indexOf(".");

			if (index == -1) {
				index = files[position].getProgressAsString().indexOf(",");

				if (index == -1) {
					index = files[position].getProgressAsString().length();
				}
			}

			String percentage = files[position].getProgressAsString().substring(0, index);

			progressBar.setProgress(Integer.parseInt(percentage));

			percentageTV.setText(percentage + "%");

			return (row);
		}
	}

	class myTrackerAdapter extends ArrayAdapter<String> {

		private String[] trackersNames;
		private Tracker[] trackers;
		private Context context;

		public myTrackerAdapter(Context context, String[] trackersNames, Tracker[] trackers) {
			// TODO Auto-generated constructor stub
			super(context, R.layout.tracker_row, R.id.tracker, trackersNames);

			this.context = context;
			this.trackersNames = trackersNames;
			this.trackers = trackers;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub}
			return (trackersNames != null) ? trackersNames.length : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = super.getView(position, convertView, parent);

			TextView tracker = (TextView) row.findViewById(R.id.tracker);

			tracker.setText("" + trackers[position].getUrl());

			return (row);
		}
	}
	/****
	 * Method for Setting the Height of the ListView dynamically. Hack to fix
	 * the issue of not showing all the items of the ListView when placed inside
	 * a ScrollView
	 ****/
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		Log.i("setListViewHeightBasedOnChildren","count: " + listAdapter.getCount());
		
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		
//		Log.i("setListViewHeightBasedOnChildren"," -2 -");
		
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		
//		Log.i("setListViewHeightBasedOnChildren"," - 3 -");
		
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		
//		Log.i("setListViewHeightBasedOnChildren"," - 4 -");
		
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		
		Log.i("setListViewHeightBasedOnChildren"," - 5 -");
		
//		Log.i("setListViewHeightBasedOnChildren","height: " + params.height);
		
		listView.setLayoutParams(params);
		listView.requestLayout();
		
//		Log.i("setListViewHeightBasedOnChildren"," - 6 -");
	}
}
