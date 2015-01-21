package fromsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.unihannover.dcsec.eviltwin.prevention.Configuration;
import de.unihannover.dcsec.eviltwin.prevention.Utils;


public class SeenNetworkList implements Iterable<String> {

	private HashMap<String, SeenNetwork> networkList;

	public SeenNetworkList() {
		networkList = new HashMap<String, SeenNetwork>();
	}

	public boolean addNetwork(SeenNetwork sn) {
		if (networkList.containsKey(sn.getID())) {
			return false;
		} else {
			networkList.put(sn.getID(), sn);
			return true;
		}
	}

	public void removeNetwork(String id) {
		networkList.remove(id);
	}

	public ArrayList<String> getIDsAsArray() {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = networkList.keySet().iterator();

		while (it.hasNext()) {
			list.add(networkList.get(it.next()).getID());
		}
		return list;
	}

	public boolean containsNetwork(SeenNetwork sn) {
		return networkList.containsKey(sn.getID());
	}

	public boolean containsNetwork(String id) {
		if (networkList.containsKey(id)) {
			return true;
		}
		return false;
	}

	public int size() {
		return networkList.size();
	}

	public SeenNetwork getNetwork(String id) {
		return networkList.get(id);
	}

	public static boolean isEnvironmentOK(AP ap, SeenNetworkList current) {
			return isEnvironmentOK_JaccardIndex(ap, current);
	}

	/**
	 * Implements the Jaccard coefficient
	 * 
	 * @param ap
	 * @param current
	 * @return
	 */
	private static boolean isEnvironmentOK_JaccardIndex(AP ap,
			SeenNetworkList current) {

		ArrayList<SeenNetworkList> allEnvironments = ap.getAllEnvironments();

		for (SeenNetworkList knownList : allEnvironments) {
			double jaccard = Utils.calculateJaccardIndex(knownList, current);
			if (jaccard >= Configuration.JACCARD_ENVIRONMENT_OK) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<String> iterator() {
		return networkList.keySet().iterator();
	}

	public String toString() {
		Iterator<String> it = networkList.keySet().iterator();

		String str = "[";

		while (it.hasNext()) {
			str += networkList.get(it.next()).getID() + ",";
		}

		return str + "]";
	}

}
