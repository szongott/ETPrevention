package de.unihannover.dcsec.eviltwin.prevention.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.unihannover.dcsec.eviltwin.prevention.Configuration;
import de.unihannover.dcsec.eviltwin.prevention.ETPEngine;
import de.unihannover.dcsec.eviltwin.prevention.data.AP;
import de.unihannover.dcsec.eviltwin.prevention.data.Position;
import de.unihannover.dcsec.eviltwin.prevention.data.SeenNetwork;
import de.unihannover.dcsec.eviltwin.prevention.data.SeenNetworkList;
import de.unihannover.dcsec.eviltwin.prevention.utils.GeoUtils;
import de.unihannover.dcsec.eviltwin.prevention.utils.Utils;

import android.location.Location;
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
				// System.out.println("Jaccard: " + jaccard);
				if (jaccard >= Configuration.JACCARD_ENVIRONMENT_OK) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isCellInfoKnown(String ssid, String bssid, int cellID,
			int lac) {
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

		if (ap.isCellInfoOK(cellID, lac)) {
			return true;
		}
		return false;
	}

	public boolean isLocationKnown(String ssid, String bssid, Location loc) {

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

		return GeoUtils.isDistanceShortEnough(new Position(loc.getLatitude(),
				loc.getLongitude(), loc.getAccuracy()), ap.getPosition());
	}

	// LEARNING

	public void learnNewAccesspoint(String ssid, String bssid,
			List<ScanResult> lsr, int cellID, int lac, Position pos) {
		AP ap = new AP(bssid);
		ap.addCellInfo(cellID, lac);
		ap.mergeNewPosition(pos);
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

	public void learnNewEnvironment(String ssid, String bssid,
			List<ScanResult> lsr, int cellID, int lac, Position pos) {
		List<AP> apList = db.get(ssid);

		// Add environment to appropriate AP
		for (int i = 0; i <= apList.size() - 1; i++) {
			if (apList.get(i).getBSSID().equals(bssid)) {
				apList.get(i)
						.addEnvironment(Utils.convertScanResultsToSNL(lsr));
				apList.get(i).addCellInfo(cellID, lac);
				apList.get(i).mergeNewPosition(pos);
				break;
			}
		}

	}

	public void improveCellIDAndLAC(String ssid, String bssid, int cellID,
			int lac) {
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
		ap.addCellInfo(cellID, lac);
	}

	public void improveNetworkEnvironment(String ssid, String bssid,
			List<ScanResult> netEnv) {
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

		// Construct SeenNetworkList from ScanResults
		SeenNetworkList currentSNL = new SeenNetworkList();
		for (ScanResult sr : netEnv) {
			SeenNetwork sn = new SeenNetwork(sr.SSID, sr.BSSID, sr.level,
					sr.capabilities, sr.frequency);
			currentSNL.addNetwork(sn);
		}

		ap.addEnvironment(currentSNL);
	}

	public void improveLocation(String ssid, String bssid, Location loc) {
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

		// Construct Position from Android Location
		Position pos = new Position(loc.getLatitude(), loc.getLongitude(),
				loc.getAccuracy());
		ap.mergeNewPosition(pos);
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
				// CellIDs
				System.out.print("    CellIDs: ");
				for (int cellID : ap.getAllCellIDs()) {
					System.out.print(cellID + " ");
				}
				System.out.println();

				// LACs
				System.out.print("    LACs: ");
				for (int lac : ap.getAllLACs()) {
					System.out.print(lac + " ");
				}
				System.out.println();

				// Location
				System.out.println("    Loc:" + ap.getPosition());

			}
		}
	}

	public int getTotalNetworks() {
		return db.keySet().size();
	}

	public int getTotalAPs() {
		int count = 0;
		for (String key : db.keySet()) {
			count += db.get(key).size();
		}
		return count;
	}

	public int getTotalEnvironments() {
		int count = 0;
		for (String ssid : db.keySet()) {
			for (AP ap : db.get(ssid)) {
				count += ap.getAllEnvironments().size();
			}
		}
		return count;

	}
}
