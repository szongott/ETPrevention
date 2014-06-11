package de.unihannover.dcsec.eviltwin.prevention;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class CancelConnectionService extends IntentService {
	private static final String TAG = "CancelConnectionService";

	public CancelConnectionService() {
		super(TAG);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Connection will be canceled");
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
		
		ToastUtils.showToastInUiThread(getApplicationContext(),
				"Connection canceled");
		
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.disconnect();
		
		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			wifiManager.enableNetwork(wc.networkId, false);
		}
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	}
}
