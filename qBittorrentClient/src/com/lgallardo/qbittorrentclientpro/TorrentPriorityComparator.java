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

import java.util.Comparator;

public class TorrentPriorityComparator implements Comparator<Torrent> {
    @Override
    public int compare(Torrent t1, Torrent t2) {

        String priority1 = t1.getPriority();
        String priority2 = t2.getPriority();

        // If torrent has no priority, give it a really low priority for sorting purposes
        if (priority1 == null || priority1.equals("*")) {
            priority1 = "10000";
        }

        // If torrent has no priority, give it a really low priority for sorting purposes
        if (priority2 == null || priority2.equals("*")) {
            priority2 = "10000";
        }

        int p1 = Integer.parseInt(priority1);
        int p2 = Integer.parseInt(priority2);

        // Ascending order
        return p1 - p2;
    }
}
