package de.unihannover.dcsec.eviltwin.prevention;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
	
	public String learnCandidate_SSID = null;
	public String learnCandidate_BSSID = null;



	public boolean connectionEvaluated = false;

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
	}
	
	public void clearLearningCandidates() {
		learnCandidate_SSID = null;
		learnCandidate_BSSID = null;
	}

	public void startEvaluatedConnection() {
		int result = 999;

		WifiManager wifiManager = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);

		int netID = wifiManager.getConnectionInfo().getNetworkId();
		// System.out.println(netID);

		// Getting SSID
		String ssid = Utils.trimQuotesFromString(wifiManager
				.getConnectionInfo().getSSID());
		this.learnCandidate_SSID = ssid;
		System.out.println("SSID to connect to: " + ssid);

		// Getting BSSID
		String bssid = wifiManager.getConnectionInfo().getBSSID();
		this.learnCandidate_BSSID = bssid;
		System.out.println("BSSID to connect to: " + bssid);

		disableAllNetworksAndDisconnect();

		// Check if SSID is known
		if (KnowledgeDB.getInstance().isSSIDKnown(ssid)) {
			// Check if BSSID is known
			if (KnowledgeDB.getInstance().isTupleKnown(ssid, bssid)) {
				// System.out.println("Getting network environment...");
				// startNetworkScan();
				// try {
				// Thread.sleep(2000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				// if (scanResults != null) {
				// System.out.println("ScanResults Size : "
				// + scanResults.size());
				// } else {
				// System.out.println("ScanResults not ready");
				// }
				// scanResults = null;
				result = Configuration.CONNECTION_OK;
			} else {
				// unknown bssid
				result = Configuration.UNKNOWN_BSSID;
			}
		} else {
			// unknown SSID
			result = Configuration.UNKNOWN_SSID;
		}

		System.out.println("ETPEngine result : " + result);
		
		if (result == Configuration.CONNECTION_OK) {
			Log.d(TAG, "Enabling network with ID " + netID);
			wifiManager.enableNetwork(netID, true);
			wifiManager.reconnect();

			connectionEvaluated = true;
		} else {
			showNotification(netID, result);
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

	private void disableAllNetworksAndDisconnect() {
		Log.d(TAG, "Disabling all networks...");
		WifiManager wifiManager = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);

		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			wifiManager.disableNetwork(wc.networkId);
		}

		Log.d(TAG, "Disconnecting...");
		wifiManager.disconnect();
	}

	private void showNotification(int netID, int result) {
		// result should be used to display the correct warning message
		WifiManager wifiManager = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);

		Intent myCancelServiceIntent = new Intent(appContext,
				CancelConnectionService.class);
		PendingIntent pCancelServiceIntent = PendingIntent.getService(
				appContext, netID, myCancelServiceIntent, 0);

		Intent myOKServiceIntent = new Intent(appContext,
				EstablishConnectionService.class);
		// System.out.println(netID);
		myOKServiceIntent.putExtra("netID", netID);
		PendingIntent pOKServiceIntent = PendingIntent.getService(appContext,
				netID, myOKServiceIntent, 0);

		String ssid = "";

		String contentTitle = "New Connection to AP";
		String contentText = "SSID: " + learnCandidate_SSID;
		String tickerText = "Connect to network \"" + learnCandidate_SSID + "\"?";
		
		switch (result) {
		case Configuration.UNKNOWN_SSID:
			contentTitle = "Unknown SSID";
			break;
		case Configuration.UNKNOWN_BSSID:
			contentTitle = "Unknown BSSID";
			contentText += "\nBSSID: " + learnCandidate_BSSID;
			break;
		default:
			break;
		}
		

		// TODO: Older notifications for older androids

		Notification n = new Notification.Builder(appContext)
				.setContentTitle(contentTitle)
				.setContentText(contentText)
				.setSmallIcon(R.drawable.evil_twin_study_app_icon_96x96)
				.setTicker(tickerText)
				.setPriority(Notification.PRIORITY_MAX)
				// .setContentIntent(pCancelServiceIntent)
				.setAutoCancel(false)
				.addAction(R.drawable.icon_ok_48x48, "Verbinden",
						pOKServiceIntent)
				.addAction(R.drawable.icon_cancel_48x48, "Ablehnen",
						pCancelServiceIntent).build();

		NotificationManager notificationManager = (NotificationManager) appContext
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancelAll();
		notificationManager.notify(0, n);
	}

}
