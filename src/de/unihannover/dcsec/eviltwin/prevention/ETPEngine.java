package de.unihannover.dcsec.eviltwin.prevention;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

public class ETPEngine {

	private final String SCAN_REQUEST_PREFERENCE = "WAITING_FOR_SCAN_RESULTS";

	ArrayList<String> knownSSIDs = new ArrayList<String>();

	HashMap<String, HashMap<String, APProfiles>> database = new HashMap<String, HashMap<String, APProfiles>>();

	private static ETPEngine instance;

	public static ETPEngine getInstance() {
		if (instance == null) {
			instance = new ETPEngine();
		}
		return instance;
	}

	private ETPEngine() {
		String ssid = "LUHWPA";
		APProfiles apProfile;
		String bssid;

		HashMap<String, APProfiles> mapAPProfiles = new HashMap<String, APProfiles>();

		bssid = "00:14:a8:14:43:c3";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		bssid = "40:f4:ec:b2:30:13";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		bssid = "00:0f:f7:eb:08:83";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		bssid = "40:f4:ec:b2:30:1c";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		database.put(ssid, mapAPProfiles);

	}

	public int evaluateConnection(String ssid, String bssid) {
		// Check if SSID is known
		if (database.keySet().contains(ssid)) {
			if (database.get(ssid).containsKey(bssid)) {
				return 0;
			} else {
				// unknown bssid
				return -2;
			}
		} else {
			// unknown SSID
			return -1;
		}
	}

	// TODO: HIER GEHT ES WEITER...
	// Hier wird die Suche der umgebenden Netzwerke angestossen, aber wie kommen
	// die Ergebnisse zurück in die Engine? Nach einer Lösung suchen und dann
	// den Algorithmus entsprechend erweitern.

	private void getNetworkEnvironment(Context appContext) {
		// Getting network environment
		WifiManager wifiManager = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(appContext);
		final Editor edit = prefs.edit();
		edit.putBoolean(SCAN_REQUEST_PREFERENCE, true);
		edit.commit();
		wifiManager.startScan();
	}

}
