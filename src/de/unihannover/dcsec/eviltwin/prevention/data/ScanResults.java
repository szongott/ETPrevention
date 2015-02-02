package de.unihannover.dcsec.eviltwin.prevention.data;

import java.util.Iterator;
import java.util.List;

import de.unihannover.dcsec.eviltwin.prevention.utils.Utils;

import android.net.wifi.ScanResult;

public class ScanResults implements Iterable<ScanResult> {

	private List<ScanResult> scanResultList;

	public ScanResults(List<ScanResult> wifiList) {
		scanResultList = wifiList;
	}

	public int size() {
		if (scanResultList != null) {
			return scanResultList.size();
		} else {
			throw new NullPointerException();
		}
	}

	public String getAllAPsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < size(); i++) {
			sb.append("{SSID:" + (scanResultList.get(i)).SSID + ",");
			sb.append("BSSID:" + (scanResultList.get(i)).BSSID + ",");
			sb.append("Caps:" + (scanResultList.get(i)).capabilities + ",");
			sb.append("lvl:" + (scanResultList.get(i)).level + ",");
			sb.append("freq:" + (scanResultList.get(i)).frequency + ",");
			sb.append("md5sub:" + getHashedAP(scanResultList.get(i)) + "}");

			sb.append(i == size() - 1 ? "" : ",");
		}
		sb.append("}");
		return sb.toString();
	}

	public String getAllAPsAsHashes() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < size(); i++) {
			sb.append("{" + getHashedAP(scanResultList.get(i)) + "}");

			sb.append(i == size() - 1 ? "" : ",");
		}
		sb.append("}");
		return sb.toString();
	}

	private String getHashedAP(ScanResult sr) {
		StringBuilder sb = new StringBuilder();
		sb.append("{SSID:" + sr.SSID + ",");
		sb.append("BSSID:" + sr.BSSID + ",");
		sb.append("Caps:" + sr.capabilities + ",");
		sb.append("freq:" + sr.frequency + "}");
		return Utils.md5Sub(sb.toString());
	}

	@Override
	public Iterator<ScanResult> iterator() {
		return scanResultList.iterator();
	}
}
