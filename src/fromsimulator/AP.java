package fromsimulator;

import java.util.ArrayList;

import de.unihannover.dcsec.eviltwin.prevention.Configuration;

public class AP {
	private String bssid;
	// private static final long LASTSEEN_UNSET = -1;
	private Position pos;
	// private String capabilities;
	// private static final int FREQUENCY_UNSET = -1;
	// private int frequency;

	private ArrayList<SeenNetworkList> snl;

	private ArrayList<Integer> cellIDs = new ArrayList<Integer>();
	private ArrayList<Integer> lacs = new ArrayList<Integer>();

	private long firstSeen;

	public AP(String bssid) {
		this.bssid = bssid;
		this.snl = new ArrayList<SeenNetworkList>();
	}

	public AP(String bssid, Position pos, long timestamp, int cellID, int lac) {
		this.bssid = bssid;
		this.pos = pos;
		this.firstSeen = timestamp;
		this.snl = new ArrayList<SeenNetworkList>();

		this.cellIDs.add(cellID);
		this.lacs.add(lac);
	}

	public boolean isLearning(long timestamp) {
		return (timestamp - this.firstSeen < Configuration.LEARNING_PHASE_NEW_AP_LENGTH);
	}

	public ArrayList<SeenNetworkList> getAllEnvironments() {
		return snl;
	}

	public void addEnvironment(SeenNetworkList snl) {
		this.snl.add(snl);
		environmentReduce();
	}
	
	private void environmentReduce() {
		//TODO: Remove duplicate entries from SeenNetworklist snl
	}

	public String getBSSID() {
		return bssid;
	}

	public Position getPosition() {
		return this.pos;
	}

	public void mergeNewPosition(Position pos) {
		if (this.pos != null) {
			Position newPos = new Position(
					(this.pos.getLatitude() + pos.getLatitude()) / 2,
					(this.pos.getLongitude() + pos.getLongitude()) / 2,
					(this.pos.getAccuracy() + pos.getAccuracy()) / 2);

			this.pos = newPos;
		} else {
			this.pos = pos;
		}
	}

	public boolean deleteSeenNet(int profile, String id) {
		snl.get(profile).removeNetwork(id);
		return true;
	}

	// public boolean addSeenNet(int profile, String id) {
	// SeenNetwork sn = ETPSimulation.getSeenNetwork(id);
	// if (sn != null) {
	// snl.get(profile).addNetwork(sn);
	// }
	// return true;
	// }

	public String toString() {
		return "";
	}

	public long getLastSighting() {
		// TODO: Look up oldest timestamp from sightings and return it
		return 0l;
	}

	public boolean isCellInfoOK(int cellID, int lac) {
		boolean result = false;

		if (cellIDs.contains(cellID) && lacs.contains(lac)) {
			result = true;
		}

		return result;
	}

	public void addCellInfo(int cellID, int lac) {
		if ((cellID != -1) && (lac != -1)) {
			if (!cellIDs.contains(cellID)) {
				cellIDs.add(cellID);
			}
			if (!lacs.contains(lac)) {
				lacs.add(lac);
			}
		}
	}

	// public void improveEnvironment(long timestamp, SeenNetworkList current) {
	// ArrayList<SeenNetworkList> allEnvironments = getAllEnvironments();
	// ArrayList<Double> jaccards = new ArrayList<Double>();
	//
	// // Initialize jaccard array
	// for (int i = 0; i < getAllEnvironments().size(); i++) {
	// jaccards.add(-1.0);
	// }
	//
	// // System.out.println(allEnvironments.size());
	// // System.out.println(jaccards.size());
	// // System.out.println("---");
	//
	// // System.out.println("V" + jaccards);
	//
	// for (int i = 0; i < allEnvironments.size(); i++) {
	// SeenNetworkList snl = allEnvironments.get(i);
	// double jaccard = Utils.calculateJaccardIndex(snl, current);
	// if (jaccard >= Configuration.JACCARD_MIN_IMPROVEMENT) {
	// jaccards.add(i, jaccard);
	// }
	// }
	//
	// // System.out.println("N" + jaccards);
	//
	// // Find maximum Jaccard
	// double max = 0;
	// int indexMax = -1;
	// for (int i = 0; i < jaccards.size(); i++) {
	// if (jaccards.get(i) > max) {
	// max = jaccards.get(i);
	// indexMax = i;
	// }
	// }
	//
	// if (indexMax != -1) {
	//
	// SeenNetworkList known = allEnvironments.get(indexMax);
	// ArrayList<String> k = known.getIDsAsArray();
	// // System.out.println("known :" + k);
	//
	// ArrayList<String> c = current.getIDsAsArray();
	// // System.out.println("current :" + c);
	//
	// for (String id : k) {
	// if (!c.contains(id)) {
	// deleteSeenNet(indexMax, id);
	// }
	// }
	//
	// for (String id : c) {
	// if (!k.contains(id)) {
	// addSeenNet(indexMax, id);
	// // System.out.println("Proposed for Addition: " + id);
	// }
	// }
	// } else {
	// if (isLearning(timestamp)) {
	// this.snl.add(current);
	// }
	// }
	// }

	public ArrayList<Integer> getAllCellIDs() {
		return cellIDs;
	}

	public ArrayList<Integer> getAllLACs() {
		return lacs;
	}

}
