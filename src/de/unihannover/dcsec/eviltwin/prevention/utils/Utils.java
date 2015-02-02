package de.unihannover.dcsec.eviltwin.prevention.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.unihannover.dcsec.eviltwin.prevention.data.SeenNetwork;
import de.unihannover.dcsec.eviltwin.prevention.data.SeenNetworkList;

import android.net.wifi.ScanResult;


public class Utils {
	public static String trimQuotesFromString(String str) {
		if (str.startsWith("\"")) {
			str = str.substring(1);
		}
		if (str.endsWith("\"")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public static String md5Sub(String in) {
		MessageDigest digest;
		Security.addProvider(new BouncyCastleProvider());
		try {
			digest = MessageDigest.getInstance("MD5", "BC");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString().substring(0, 10);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return in;
	}

	public static double calculateJaccardIndex(SeenNetworkList list1,
			SeenNetworkList list2) {
		Set<String> knownSet = new HashSet<String>();
		Set<String> currentSet = new HashSet<String>();

		for (String id : list1) {
			knownSet.add(id);
		}
		// System.out.println("known (" + knownSet.size() + "): " +
		// knownSet);

		for (String id : list2) {
			currentSet.add(id);
		}
		// System.out
		// .println("current (" + currentSet.size() + "): " + currentSet);

		Set<String> inCommon = new HashSet<String>();
		inCommon.addAll(knownSet);
		inCommon.retainAll(currentSet);
		int countInCommon = inCommon.size();
		// System.out.println("inCommon = " + countInCommon);

		Set<String> union = new HashSet<String>();
		union.addAll(knownSet);
		union.addAll(currentSet);
		int countUnion = union.size();
		// System.out.println("union = " + countUnion);

		double jaccard = (double) countInCommon / (double) countUnion;
		// System.out.println("Jaccard : " + jaccard);
		// System.out.println("=================");

		// System.exit(0);
		return jaccard;
	}
	
	public static SeenNetworkList convertScanResultsToSNL(List<ScanResult> lsr) {
		SeenNetworkList snl = new SeenNetworkList();
		for (ScanResult sr : lsr) {
			SeenNetwork sn = new SeenNetwork(sr.SSID, sr.BSSID, sr.level,
					sr.capabilities, sr.frequency);
			snl.addNetwork(sn);
		}
		return snl;
	}

}
