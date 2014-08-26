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

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;

public class TorrentDetailsFragment extends Fragment {

	// Torrent variables
	String name, info, hash, ratio, size, state, leechs, seeds, progress, priority, savePath, creationDate, comment, totalWasted, totalUploaded,
			totalDownloaded, timeElapsed, nbConnections, shareRatio, uploadRateLimit, downloadRateLimit, downloaded = "";

	String hostname;
	String protocol;
	int port;
	String username;
	String password;

	int position;

	public TorrentDetailsFragment() {
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Tell the host activity that your fragment has menu options that it
		// wants to add/replace/delete using the onCreateOptionsMenu method.
		setHasOptionsMenu(true);

		View rootView = inflater.inflate(R.layout.torrent_details, container, false);

		Log.i("TorrentDetails", "Position =>>> " + position);

		try {
			if (MainActivity.lines != null && position != -1) {
				name = MainActivity.lines[position].getFile();
				size = MainActivity.lines[position].getSize();
				hash = MainActivity.lines[position].getHash();
				ratio = MainActivity.lines[position].getRatio();
				state = MainActivity.lines[position].getState();
				leechs = MainActivity.lines[position].getLeechs();
				seeds = MainActivity.lines[position].getSeeds();
				progress = MainActivity.lines[position].getProgress();
				hash = MainActivity.lines[position].getHash();
				priority = MainActivity.lines[position].getPriority();
				savePath = MainActivity.lines[position].getSavePath();

				creationDate = MainActivity.lines[position].getCreationDate();
				comment = MainActivity.lines[position].getComment();
				totalWasted = MainActivity.lines[position].getTotalWasted();
				totalUploaded = MainActivity.lines[position].getTotalUploaded();
				totalDownloaded = MainActivity.lines[position].getTotalDownloaded();
				timeElapsed = MainActivity.lines[position].getTimeElapsed();
				nbConnections = MainActivity.lines[position].getNbConnections();
				shareRatio = MainActivity.lines[position].getShareRatio();
				uploadRateLimit = MainActivity.lines[position].getUploadLimit();
				downloadRateLimit = MainActivity.lines[position].getDownloadLimit();

				downloaded = MainActivity.lines[position].getTotalDownloaded();
				downloaded = downloaded.substring(0, downloaded.indexOf("(") - 1);

				TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
				TextView sizeTextView = (TextView) rootView.findViewById(R.id.downloadedVsTotal);
				TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
				TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
				TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
				TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
				TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
				TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);
				TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
				TextView pathTextView = (TextView) rootView.findViewById(R.id.torrentSavePath);
				TextView creationDateTextView = (TextView) rootView.findViewById(R.id.torrentCreationDate);
				TextView commentTextView = (TextView) rootView.findViewById(R.id.torrentComment);
				TextView totalWastedTextView = (TextView) rootView.findViewById(R.id.torrentTotalWasted);
				TextView totalUploadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalUploaded);
				TextView totalDownloadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalDownloaded);
				TextView timeElapsedTextView = (TextView) rootView.findViewById(R.id.torrentTimeElapsed);
				TextView nbConnectionsTextView = (TextView) rootView.findViewById(R.id.torrentNbConnections);
				TextView shareRatioTextView = (TextView) rootView.findViewById(R.id.torrentShareRatio);
				TextView uploadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentUploadRateLimit);
				TextView downloadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentDownloadRateLimit);

				nameTextView.setText(name);
				ratioTextView.setText(ratio);
				stateTextView.setText(state);
				leechsTextView.setText(leechs);
				seedsTextView.setText(seeds);
				progressTextView.setText(progress);
				hashTextView.setText(hash);
				priorityTextView.setText(priority);
				pathTextView.setText(savePath);
				creationDateTextView.setText(creationDate);
				commentTextView.setText(comment);
				totalWastedTextView.setText(totalWasted);
				totalUploadedTextView.setText(totalUploaded);
				totalDownloadedTextView.setText(totalDownloaded);
				timeElapsedTextView.setText(timeElapsed);
				nbConnectionsTextView.setText(nbConnections);
				shareRatioTextView.setText(shareRatio);
				uploadRateLimitTextView.setText(uploadRateLimit);
				downloadRateLimitTextView.setText(downloadRateLimit);

				// Set Downloaded vs Total size
				sizeTextView.setText(downloaded + " / " + size);

				// Set progress bar
				ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
				TextView percentageTV = (TextView) rootView.findViewById(R.id.percentage);

				int index = MainActivity.lines[position].getProgress().indexOf(".");

				if (index == -1) {
					index = MainActivity.lines[position].getProgress().indexOf(",");

					if (index == -1) {
						index = MainActivity.lines[position].getProgress().length();
					}
				}

				String percentage = MainActivity.lines[position].getProgress().substring(0, index);

				progressBar.setProgress(Integer.parseInt(percentage));
				percentageTV.setText(percentage + "%");

				// Set status icon
				ImageView icon = (ImageView) rootView.findViewById(R.id.icon);

				if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
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

				if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
					icon.setImageResource(R.drawable.queued);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("TorrentDetailsFragment - onCreateView", e.toString());
		}

		return rootView;
	}

	// @Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Log.i("FragmentLIst", "Item touched");
	}

	// @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {

			if (getActivity().findViewById(R.id.one_frame) != null) {
				menu.findItem(R.id.action_refresh).setVisible(false);
			}
			menu.findItem(R.id.action_add).setVisible(false);
			menu.findItem(R.id.action_resume_all).setVisible(false);
			menu.findItem(R.id.action_pause_all).setVisible(false);

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

}
