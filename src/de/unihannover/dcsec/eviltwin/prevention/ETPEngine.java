package de.unihannover.dcsec.eviltwin.prevention;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ETPEngine {

	private static final String TAG = "ETPrevention";

	private final String SCAN_REQUEST_PREFERENCE = "WAITING_FOR_SCAN_RESULTS";
	private Context appContext;

	ScanResults scanResults = null;

	public String learnCandidate_SSID = null;
	public String learnCandidate_BSSID = null;
	public List<ScanResult> learnCandidate_NetworkEnvironment = null;

	private int currentResult = Configuration.ETPCODE_NOT_SET;

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

	public void clearLearningCandidatesAndResult() {
		learnCandidate_SSID = null;
		learnCandidate_BSSID = null;
		learnCandidate_NetworkEnvironment = null;

		currentResult = Configuration.ETPCODE_NOT_SET;
	}

	public void startEvaluatedConnection() {

		WifiManager wifiManager = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);
		wifiManager.startScan();

		Log.d(TAG, "Waiting for network scan...");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int netID = wifiManager.getConnectionInfo().getNetworkId();

		// Getting SSID
		String ssid = Utils.trimQuotesFromString(wifiManager
				.getConnectionInfo().getSSID());
		this.learnCandidate_SSID = ssid;
		Log.d(TAG, "SSID to connect to: " + ssid);

		// Getting BSSID
		String bssid = wifiManager.getConnectionInfo().getBSSID();
		this.learnCandidate_BSSID = bssid;
		Log.d(TAG, "BSSID to connect to: " + bssid);

		// Getting network environment
		this.learnCandidate_NetworkEnvironment = wifiManager.getScanResults();

		disableAllNetworksAndDisconnect();

		KnowledgeDB kdb = KnowledgeDB.getInstance();
		// Check if SSID is known
		if (kdb.isSSIDKnown(ssid)) {
			// Check if BSSID is known
			if (kdb.isSSID_BSSIDTupleKnown(ssid, bssid)) {
				if (learnCandidate_NetworkEnvironment == null)
					System.out.println("Aaaaaaaaaaaaaaaargh");
				if (kdb.isNetworkEnvironmentKnown(ssid, bssid,
						learnCandidate_NetworkEnvironment)) {
					currentResult = Configuration.ETPCODE_CONNECTION_OK;
				} else {
					currentResult = Configuration.ETPCODE_UNKNOWN_ENVIRONMENT;
				}
			} else {
				// unknown bssid
				currentResult = Configuration.ETPCODE_UNKNOWN_BSSID;
			}
		} else {
			// unknown SSID
			currentResult = Configuration.ETPCODE_UNKNOWN_SSID;
		}

		System.out.println("ETPEngine result : " + currentResult);

		if (currentResult == Configuration.ETPCODE_CONNECTION_OK) {
			connectToSpecificNetwork(netID);
			connectionEvaluated = true;
		} else {
			showNotification(netID, currentResult);
		}
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

		String contentTitle = "New Connection to AP";
		String contentText = "SSID: " + learnCandidate_SSID;
		String tickerText = "Connect to network \"" + learnCandidate_SSID
				+ "\"?";

		switch (result) {
		case Configuration.ETPCODE_UNKNOWN_SSID:
			contentTitle = "Unknown SSID";
			break;
		case Configuration.ETPCODE_UNKNOWN_BSSID:
			contentTitle = "Unknown BSSID";
			contentText += "\nBSSID: " + learnCandidate_BSSID;
			break;
		case Configuration.ETPCODE_UNKNOWN_ENVIRONMENT:
			contentTitle = "Unknown network environment";
		default:
			break;
		}

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

	private void startLearning() {
		KnowledgeDB kdb = KnowledgeDB.getInstance();

		switch (currentResult) {
		case Configuration.ETPCODE_UNKNOWN_SSID:
			kdb.learnNewAccesspoint(learnCandidate_SSID, learnCandidate_BSSID,
					learnCandidate_NetworkEnvironment);
			break;
		case Configuration.ETPCODE_UNKNOWN_BSSID:
			kdb.learnNewAccesspoint(learnCandidate_SSID, learnCandidate_BSSID,
					learnCandidate_NetworkEnvironment);
			break;
		case Configuration.ETPCODE_UNKNOWN_ENVIRONMENT:
			kdb.learnNewEnvironment(learnCandidate_SSID, learnCandidate_BSSID,
					learnCandidate_NetworkEnvironment);
		default:
			break;
		}
	}

	private void connectToSpecificNetwork(int netID) {
		WifiManager wifiManager = (WifiManager) appContext
				.getSystemService(Context.WIFI_SERVICE);
		Log.d(TAG, "Enabling network with ID " + netID);
		wifiManager.enableNetwork(netID, true);
		wifiManager.reconnect();

		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			wifiManager.enableNetwork(wc.networkId, false);
		}
	}

	public void userActionConnectionAllowed(int netID) {
		startLearning();
		connectToSpecificNetwork(netID);
	}

}
