//package de.unihannover.dcsec.eviltwin.prevention;
//
//import java.util.List;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.preference.PreferenceManager;
//
//public class NearbyWifisListReceiver extends BroadcastReceiver {
//	private final String SCAN_REQUEST_PREFERENCE = "WAITING_FOR_SCAN_RESULTS";
//
//	StringBuilder sb = new StringBuilder();
//	List<ScanResult> wifiList;
//	private WifiManager wifimanager;
//
//	@SuppressLint("NewApi")
//	public void onReceive(Context context, Intent intent) {
//		SharedPreferences prefs = PreferenceManager
//				.getDefaultSharedPreferences(context.getApplicationContext());
//
//		final Editor edit = prefs.edit();
//		
//
//		Boolean scanRequested = prefs
//				.getBoolean(SCAN_REQUEST_PREFERENCE, false);
//
//		if (scanRequested) {
//			System.out.println("ScanRequested was TRUE, now set to FALSE");
//			edit.putBoolean(SCAN_REQUEST_PREFERENCE, false);
//			edit.commit();
//
//			// Get all Wifi networks nearby from scan results
//			wifimanager = (WifiManager) context
//					.getSystemService(Context.WIFI_SERVICE);
//			wifiList = wifimanager.getScanResults();
//
//			ScanResults wsr = new ScanResults(wifiList);
//			
//			ETPEngine.getInstance().setTemporaryScanResults(wsr);
//			System.out.println("From Receiver: " + ETPEngine.getInstance());
//
//			// TODO: Ergebnis zurückliefern
////			System.out.println("Aktuelle Umgebung: " + wsr.getAllAPsAsString());
//
//			// MyLogger.getInstance(context.getApplicationContext())
//			// .setWiFisNearby(wsr);
//		}
//		
//		//Debugging
//		wifimanager = (WifiManager) context
//				.getSystemService(Context.WIFI_SERVICE);
//		wifiList = wifimanager.getScanResults();
//		
//		
//		System.out.println(System.nanoTime()/1000000000);
//		for (ScanResult sr : wifiList) {
//			System.out.println("SR = " + sr.SSID + " / " + sr.BSSID + " / " + sr.timestamp/1000000);
//		}
//		System.out.println("-----------------------");
//		
//		
//	}
//}
