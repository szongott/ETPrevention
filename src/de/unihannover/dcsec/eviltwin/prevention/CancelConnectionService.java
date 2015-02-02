package de.unihannover.dcsec.eviltwin.prevention;

import de.unihannover.dcsec.eviltwin.prevention.utils.ToastUtils;
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
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
		
		ToastUtils.showToastInUiThread(getApplicationContext(),
				"Connection aborted!");
		
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.disconnect();
		
		Log.d(TAG, "Enabling all networks...");
		for (WifiConfiguration wc : wifiManager.getConfiguredNetworks()) {
			wifiManager.enableNetwork(wc.networkId, false);
		}
		
		Log.d(TAG, "Connection aborted!");
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
