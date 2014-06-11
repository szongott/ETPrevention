package de.unihannover.dcsec.eviltwin.prevention;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "ETPrevention";

	private static Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onResume() {
		super.onResume();
		BroadcastReceiver wifiStatusReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SupplicantState supState;
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				supState = wifiInfo.getSupplicantState();

				if (!ConnectionWatchDog.getInstance().connectionAllowed()) {
					switch (supState) {
					case ASSOCIATING:
						Log.d(TAG, "State ASSOCIATING recognized...");
						break;
					case ASSOCIATED:
						Log.d(TAG, "State ASSOCIATED recognized...");
						startETP();
						break;
					case AUTHENTICATING:
						Log.d(TAG, "State AUTHENTICATING recognized...");
						break;
					case COMPLETED:
						Log.d(TAG, "State COMPLETED recognized...");
						break;
					default:
						break;

					}
				} else {
					if (supState.equals(SupplicantState.COMPLETED)) {
						Log.d(TAG, "Connection allowed");
						ConnectionWatchDog.getInstance().setAllowConnection(
								false);
					}
				}

			}
		};
		IntentFilter filter = new IntentFilter(
				WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		this.registerReceiver(wifiStatusReceiver, filter);

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
		disableAllNetworksAndDisconnect();
	}

	private void disableAllNetworksAndDisconnect() {
		Log.d(TAG, "Disabling all networks");
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		int netID = wifiManager.getConnectionInfo().getNetworkId();

		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			wifiManager.disableNetwork(wc.networkId);
		}
		showNotification(netID);
		wifiManager.disconnect();
	}

	private void showNotification(int netID) {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		Intent myCancelServiceIntent = new Intent(mContext,
				CancelConnectionService.class);
		PendingIntent pCancelServiceIntent = PendingIntent.getService(mContext,
				1, myCancelServiceIntent, 0);

		Intent myOKServiceIntent = new Intent(mContext,
				EstablishConnectionService.class);
		myOKServiceIntent.putExtra("netID", netID);
		PendingIntent pOKServiceIntent = PendingIntent.getService(mContext, 2,
				myOKServiceIntent, 0);

		String ssid = "";

		Log.d(TAG, "netID: " + netID);
		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			if (wc.networkId == netID) {
				ssid = Utils.trimQuotesFromString(wc.SSID);
				break;
			}
		}

		Log.d(TAG, "SSID: " + ssid);

		String contentTitle = "New Connection to AP";
		String contentText = "SSID: " + ssid;
		String tickerText = "Connect to network \"" + ssid + "\"?";

		// TODO: Older notifications for older androids

		Notification n = new Notification.Builder(getApplicationContext())
				.setContentTitle(contentTitle)
				.setContentText(contentText)
				.setSmallIcon(R.drawable.evil_twin_study_app_icon_96x96)
				.setTicker(tickerText)
				// .setContentIntent(pCancelServiceIntent)
				.setAutoCancel(true)
				.addAction(R.drawable.icon_ok_48x48, "Verbinden",
						pOKServiceIntent)
				.addAction(R.drawable.icon_cancel_48x48, "Ablehnen",
						pCancelServiceIntent).build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n);
	}

}
