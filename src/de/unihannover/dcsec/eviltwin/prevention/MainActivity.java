package de.unihannover.dcsec.eviltwin.prevention;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class MainActivity extends Activity {

	private static final String TAG = "ETPrevention";

	private static Context mContext = null;

	private Button btnRefresh;
	private OnClickListener btnRefreshClickListener;

	LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ETPEngine.getInstance().setAppContext(getApplicationContext());
		System.out.println("From Main: " + ETPEngine.getInstance());

		mContext = this;

		BroadcastReceiver wifiStatusReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SupplicantState supState;
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				supState = wifiInfo.getSupplicantState();

				if (ConnectionWatchDog.getInstance().isConnectionAllowed()) {
					if (supState.equals(SupplicantState.COMPLETED)) {
						Log.d(TAG, "Connection allowed");
						ConnectionWatchDog.getInstance().setAllowConnection(
								false);
					} else {
						switch (supState) {
						case ASSOCIATING:
							Log.d(TAG, "State ASSOCIATING recognized...");
							break;
						case ASSOCIATED:
							Log.d(TAG, "State ASSOCIATED recognized...");
							break;
						case AUTHENTICATING:
							Log.d(TAG, "State AUTHENTICATING recognized...");
							break;
						case COMPLETED:
							Log.d(TAG, "State COMPLETED recognized...");
							startETP();
							break;
						default:
							break;

						}
					}
				}

			}
		};
		IntentFilter filter = new IntentFilter(
				WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		this.registerReceiver(wifiStatusReceiver, filter);

		// Refresh button
		btnRefresh = (Button) this.findViewById(R.id.refreshButton);

		btnRefreshClickListener = new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "View refreshed");
				updateDisplay();
			}
		};

		btnRefresh.setOnClickListener(btnRefreshClickListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onResume() {
		super.onResume();

		updateDisplay();
	}

	private void updateDisplay() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		TextView tvSSID = (TextView) this.findViewById(R.id.textView5);
		tvSSID.setText(Utils.trimQuotesFromString(wifiManager
				.getConnectionInfo().getSSID()));

		TextView tvBSSID = (TextView) this.findViewById(R.id.textView6);
		tvBSSID.setText(wifiManager.getConnectionInfo().getBSSID());
	}

	private void startETP() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		int netID = wifiManager.getConnectionInfo().getNetworkId();
		// System.out.println(netID);

		// Getting SSID
		String ssid = Utils.trimQuotesFromString(wifiManager
				.getConnectionInfo().getSSID());
		System.out.println("SSID to connect to: " + ssid);

		// Getting BSSID
		String bssid = wifiManager.getConnectionInfo().getBSSID();
		System.out.println("BSSID to connect to: " + bssid);

		int result = ETPEngine.getInstance().evaluateConnection(ssid, bssid);

		System.out.println("ETPResult=" + result);

		// Getting CellInfo
		TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation location = telephonyManager.getCellLocation();
		GsmCellLocation gsmLocation = (GsmCellLocation) location;
		int cellId = gsmLocation.getCid();
		int lac = gsmLocation.getLac();
		Log.d(TAG, "CellID: " + cellId + " / lac: " + lac);
		System.out.println("Current cellID : " + cellId);
		System.out.println("Current LAC : " + lac);

		// Getting Position
		final int playServicesAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		if (playServicesAvailable == ConnectionResult.SUCCESS) {
			Log.d(TAG, "Starting PlayServiceLocator...");
			ETPLocationGrabber.setAppContext(getApplicationContext());
			try {
				ETPLocationGrabber.getInstance().saveCurrentLocation();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.d(TAG,
					"Can not determine position, Google Play Services unavailable..");
		}

		if (result == 0) {
			// Do nothing
		} else {
			disableAllNetworksAndDisconnect();
			showNotification(netID, result);
		}

	}

	private void showNotification(int netID, int result) {
		// result should be used to display the correct warning message
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		Intent myCancelServiceIntent = new Intent(mContext,
				CancelConnectionService.class);
		PendingIntent pCancelServiceIntent = PendingIntent.getService(mContext,
				netID, myCancelServiceIntent, 0);

		Intent myOKServiceIntent = new Intent(mContext,
				EstablishConnectionService.class);
		System.out.println(netID);
		myOKServiceIntent.putExtra("netID", netID);
		PendingIntent pOKServiceIntent = PendingIntent.getService(mContext,
				netID, myOKServiceIntent, 0);

		String ssid = "";

		// Log.d(TAG, "netID: " + netID);
		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			if (wc.networkId == netID) {
				ssid = Utils.trimQuotesFromString(wc.SSID);
				break;
			}
		}

		// Log.d(TAG, "SSID: " + ssid);

		String contentTitle = "New Connection to AP";
		String contentText = "SSID: " + ssid;
		String tickerText = "Connect to network \"" + ssid + "\"?";

		// TODO: Older notifications for older androids

		Notification n = new Notification.Builder(getApplicationContext())
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

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.cancelAll();
		notificationManager.notify(0, n);
	}

	private void disableAllNetworksAndDisconnect() {
		Log.d(TAG, "Disabling all networks...");
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			wifiManager.disableNetwork(wc.networkId);
		}

		Log.d(TAG, "Disconnecting...");
		wifiManager.disconnect();
	}
}
