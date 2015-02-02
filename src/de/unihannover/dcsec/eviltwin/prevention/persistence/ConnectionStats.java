package de.unihannover.dcsec.eviltwin.prevention.persistence;

import java.util.HashMap;
import java.util.Map;

public class ConnectionStats {

	private Map<String, Integer> ssidConnectionCount;
	private Map<String, Integer> apConnectionCount;

	private static ConnectionStats instance = null;

	public static ConnectionStats getInstance() {
		if (instance == null) {
			instance = new ConnectionStats();
		}

		return instance;
	}

	private ConnectionStats() {
		ssidConnectionCount = new HashMap<String, Integer>();
		apConnectionCount = new HashMap<String, Integer>();
	}

	
	public void incSSIDConnection(String ssid) {
		int newvalue = 1;
		if(ssidConnectionCount.keySet().contains(ssid)) {
			newvalue = ssidConnectionCount.get(ssid) + 1;
		} 
		ssidConnectionCount.put(ssid, newvalue);
	}
	
	public int getSSIDConnections(String ssid) {
		int result = 0;
		if(ssidConnectionCount.keySet().contains(ssid)) {
			result = ssidConnectionCount.get(ssid);
		}
		return result;
	}
	
	public void incAPConnection(String bssid) {
		int newvalue = 1;
		if(apConnectionCount.keySet().contains(bssid)) {
			newvalue = apConnectionCount.get(bssid) + 1;
		} 
		apConnectionCount.put(bssid, newvalue);
	}
	
	public int getAPConnections(String bssid) {
		int result = 1;
		if(apConnectionCount.keySet().contains(bssid)) {
			result = apConnectionCount.get(bssid);
		}
		return result;
	}
}
