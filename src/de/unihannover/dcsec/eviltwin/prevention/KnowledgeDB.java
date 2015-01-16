package de.unihannover.dcsec.eviltwin.prevention;

import java.util.ArrayList;
import java.util.HashMap;

public class KnowledgeDB {

	ArrayList<String> knownSSIDs = new ArrayList<String>();

	HashMap<String, HashMap<String, APProfiles>> db = new HashMap<String, HashMap<String, APProfiles>>();

	private static KnowledgeDB instance;

	public static KnowledgeDB getInstance() {
		if (instance == null) {
			instance = new KnowledgeDB();
		}
		return instance;
	}

	private KnowledgeDB() {
		String ssid = "LUHWPA";
		APProfiles apProfile;
		String bssid;

		HashMap<String, APProfiles> mapAPProfiles = new HashMap<String, APProfiles>();

		bssid = "00:14:a8:14:43:c3";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		bssid = "40:f4:ec:b2:30:13";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		bssid = "00:0f:f7:eb:08:83";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		bssid = "40:f4:ec:b2:30:1c";
		apProfile = new APProfiles(bssid);
		mapAPProfiles.put(bssid, apProfile);

		db.put(ssid, mapAPProfiles);
	}

	public boolean isSSIDKnown(String ssid) {
		ssid = Utils.trimQuotesFromString(ssid);

		return db.keySet().contains(ssid);
	}

	public boolean isTupleKnown(String ssid, String bssid) {
		ssid = Utils.trimQuotesFromString(ssid);		
		return db.get(ssid).containsKey(bssid);
	}

	public void learnAccessPoint(String ssid,
			String bssid) {
		
		

		APProfiles apProfile = new APProfiles(bssid);
		
		HashMap<String, APProfiles> mapAPProfiles = new HashMap<String, APProfiles>();
		
		mapAPProfiles.put(bssid, apProfile);
		
		db.put(ssid, mapAPProfiles);
	}
}
