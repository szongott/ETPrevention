package de.unihannover.dcsec.eviltwin.prevention;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class EstablishConnectionService extends IntentService {
	private static final String TAG = "EstablishConnectionService";

	public EstablishConnectionService() {
		super(TAG);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancelAll();

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		int netID = intent.getIntExtra("netID", -1);
		Log.d(TAG, "Connection will be established (netID " + netID + ")");

		if (netID > -1) {
//			ConnectionWatchDog.getInstance().setAllowConnection(true);
			
			//TODO: Learn this environment

			ETPEngine etp = ETPEngine.getInstance();
			
			KnowledgeDB.getInstance().learnAccessPoint(etp.learnCandidate_SSID, etp.learnCandidate_BSSID);
			
			Log.d(TAG, "Enabling network with ID " + netID);
			wifiManager.enableNetwork(netID, true);
			wifiManager.reconnect();
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
