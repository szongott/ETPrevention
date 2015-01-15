package de.unihannover.dcsec.eviltwin.prevention;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class ETPEngine {

	private static final String TAG = "ETPrevention";

	private final String SCAN_REQUEST_PREFERENCE = "WAITING_FOR_SCAN_RESULTS";
	private Context appContext;

	ScanResults scanResults = null;

	ArrayList<String> knownSSIDs = new ArrayList<String>();

	HashMap<String, HashMap<String, APProfiles>> database = new HashMap<String, HashMap<String, APProfiles>>();

	private static ETPEngine instance;

	public static ETPEngine getInstance() {
		if (instance == null) {
			instance = new ETPEngine();
		}
		return instance;
	}

	public void setAppContext(Context context) {
		this.appContext = context;
	}

	public Context getAppContext() {
		if (appContext == null) {
			new Exception("ETPEngine: AppContext must not be null!");
		}
		return appContext;
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
			// Check if BSSID is known
			if (database.get(ssid).containsKey(bssid)) {
				System.out.println("Getting network environment...");
				startNetworkScan();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (scanResults != null) {
					System.out.println("ScanResults Size : "
							+ scanResults.size());
				} else {
					System.out.println("ScanResults not ready");
				}
				scanResults = null;
				return Configuration.CONNECTION_OK;
			} else {
				// unknown bssid
				return Configuration.UNKNOWN_BSSID;
			}
		} else {
			// unknown SSID
			return Configuration.UNKNOWN_SSID;
		}
	}

	public void setTemporaryScanResults(ScanResults sr) {
		this.scanResults = sr;
	}

	private void startNetworkScan() {
		// Starting the WiFi scan
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
