package de.unihannover.dcsec.eviltwin.prevention;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationClient;

public class MainActivity extends Activity {

	private static final String TAG = "ETPrevention:Main";

	private Button btnRefresh;
	private OnClickListener btnRefreshClickListener;

	LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ETPEngine.getInstance().setAppContext(getApplicationContext());

		BroadcastReceiver wifiStatusReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SupplicantState supState;
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				supState = wifiInfo.getSupplicantState();

				switch (supState) {
				case ASSOCIATING:
					// Log.d(TAG, "State ASSOCIATING recognized...");
					break;
				case ASSOCIATED:
					// Log.d(TAG, "State ASSOCIATED recognized...");
					break;
				case AUTHENTICATING:
					// Log.d(TAG, "State AUTHENTICATING recognized...");
					break;
				case COMPLETED:
					Log.d(TAG, "State COMPLETED recognized...");
					if (ETPEngine.getInstance().connectionEvaluated) {
						Log.d(TAG, "Connection allowed");
						ETPEngine.getInstance().connectionEvaluated = false;
					} else {
						Log.d(TAG, "Starting ETP...");
						startETP();
					}
					break;
				default:
					break;

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

		ETPEngine.getInstance().startEvaluatedConnection();
	}

}
