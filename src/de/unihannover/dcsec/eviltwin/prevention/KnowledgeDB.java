package de.unihannover.dcsec.eviltwin.prevention;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fromsimulator.AP;
import fromsimulator.SeenNetwork;
import fromsimulator.SeenNetworkList;

import android.net.wifi.ScanResult;
import android.util.Log;

public class KnowledgeDB {

	private static final String TAG = "ETP:KnowledgeDB";

	// SSID --> AP
	HashMap<String, List<AP>> db = new HashMap<String, List<AP>>();

	private static KnowledgeDB instance;

	public static KnowledgeDB getInstance() {
		if (instance == null) {
			instance = new KnowledgeDB();
		}
		return instance;
	}

	private KnowledgeDB() {
		// initialize();
	}

	public void initialize() {
		// String ssid = "LUHWPA";
		// AP ap;
		// String bssid;
		//
		// HashMap<String, AP> mapAPProfiles = new HashMap<String, AP>();
		//
		// bssid = "00:14:a8:14:43:c3";
		// ap = new AP(bssid, pos, timestamp, cellID, lac)
		//
		// apProfile = new APProfiles(bssid);
		// mapAPProfiles.put(bssid, apProfile);
		//
		// bssid = "40:f4:ec:b2:30:13";
		// apProfile = new APProfiles(bssid);
		// mapAPProfiles.put(bssid, apProfile);
		//
		// bssid = "00:0f:f7:eb:08:83";
		// apProfile = new APProfiles(bssid);
		// mapAPProfiles.put(bssid, apProfile);
		//
		// bssid = "40:f4:ec:b2:30:1c";
		// apProfile = new APProfiles(bssid);
		// mapAPProfiles.put(bssid, apProfile);
		//
		// db.put(ssid, mapAPProfiles);
	}

	// CHECKING

	public boolean isSSIDKnown(String ssid) {
		ssid = Utils.trimQuotesFromString(ssid);

		return db.keySet().contains(ssid);
	}

	public boolean isSSID_BSSIDTupleKnown(String ssid, String bssid) {
		ssid = Utils.trimQuotesFromString(ssid);

		boolean result = false;

		for (AP ap : db.get(ssid)) {
			if (ap.getBSSID().equals(bssid)) {
				result = true;
			}
		}
		return result;
	}

	public boolean isNetworkEnvironmentKnown(String ssid, String bssid,
			List<ScanResult> netEnv) {
		ssid = Utils.trimQuotesFromString(ssid);

		// Setup current SeenNetworkList
		SeenNetworkList currentSNL = new SeenNetworkList();
		for (ScanResult sr : netEnv) {
			SeenNetwork sn = new SeenNetwork(sr.SSID, sr.BSSID, sr.level,
					sr.capabilities, sr.frequency);
			currentSNL.addNetwork(sn);
		}

		// Get list of APs
		List<AP> apList = db.get(ssid);
		AP ap = null;

		// Find appropriate AP
		for (int i = 0; i <= apList.size() - 1; i++) {
			if (apList.get(i).getBSSID().equals(bssid)) {
				ap = apList.get(i);
				break;
			}
		}

		// Search for suiting environments
		if (ap != null) {
			ArrayList<SeenNetworkList> listOfSNL = ap.getAllEnvironments();

			for (SeenNetworkList snl : listOfSNL) {
				double jaccard = Utils.calculateJaccardIndex(currentSNL, snl);
				System.out.println("Jaccard: " + jaccard);
				if (jaccard >= Configuration.JACCARD_ENVIRONMENT_OK) {
					return true;
				}
			}
		}

		return false;
	}

	// LEARNING
	
	public void learnNewAccesspoint(String ssid, String bssid,
			List<ScanResult> lsr) {
		AP ap = new AP(bssid);
		ap.addEnvironment(Utils.convertScanResultsToSNL(lsr));

		if (db.containsKey(ssid)) {
			// Insert into AP list
			db.get(ssid).add(ap);
			Log.d(TAG, "Inserted AP");
		} else {
			// create ssid and insert in AP list
			List<AP> apList = new ArrayList<AP>();
			apList.add(ap);
			db.put(ssid, apList);
			Log.d(TAG, "Created ssid and inserted AP");
		}

		printCompleteKnowledge();
		ETPEngine.getInstance().clearLearningCandidatesAndResult();
	}
	
	public void learnNewEnvironment(String ssid, String bssid, List<ScanResult> lsr) {
		List<AP> apList = db.get(ssid);
		
		// Add environment to appropriate AP
		for (int i = 0; i <= apList.size() - 1; i++) {
			if (apList.get(i).getBSSID().equals(bssid)) {
				apList.get(i).addEnvironment(Utils.convertScanResultsToSNL(lsr));
				break;
			}
		}
		
		
		
		
	}

	public void printCompleteKnowledge() {
		if (db.keySet().size() == 0) {
			System.out.println("KnownledgeDB is empty");
		}
		for (String ssid : db.keySet()) {
			System.out.println("SSID: " + ssid);
			for (AP ap : db.get(ssid)) {
				System.out.println("  BSSID: " + ap.getBSSID());
				for (SeenNetworkList snl : ap.getAllEnvironments()) {
					// System.out.println("    SNL: " + snl);
					System.out.println("    SNL: " + snl.size());
				}

			}
		}
	}

}
