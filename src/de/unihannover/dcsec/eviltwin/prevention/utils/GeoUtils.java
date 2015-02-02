package de.unihannover.dcsec.eviltwin.prevention.utils;

import android.util.Log;
import de.unihannover.dcsec.eviltwin.prevention.Configuration;
import de.unihannover.dcsec.eviltwin.prevention.data.Position;

public class GeoUtils {
	private static final boolean DEBUG = false;

	public static double distanceBetween(Position p1, Position p2) {
		double earthRadius = 6378160.0;

		double degreesToRadians = Math.PI / 180.0;

		double phi1 = (90.0f - p1.getLatitude()) * degreesToRadians;
		double phi2 = (90.0f - p2.getLatitude()) * degreesToRadians;

		double theta1 = p1.getLongitude() * degreesToRadians;
		double theta2 = p2.getLongitude() * degreesToRadians;

		double tmp = Math.sin(phi1) * Math.sin(phi2)
				* Math.cos(theta1 - theta2) + Math.cos(phi1) * Math.cos(phi2);

		double arc = Math.acos(tmp);

		return arc * earthRadius;
	}

	public static boolean isDistanceShortEnough(Position p1, Position p2) {
		// System.out.println(GeoUtils.distanceBetween(p1, p2));
		// System.out.println(p1.getAccuracy() + p2.getAccuracy());

		double distance = GeoUtils.distanceBetween(p1, p2);
		double accuracyRadiuses = p1.getAccuracy() + p2.getAccuracy();

		if (DEBUG) {
			Log.d("GeoUtils",
					"==> DBG: Pos1: (" + p1.getLatitude() + ";"
							+ p1.getLongitude() + ")");
			Log.d("GeoUtils",
					"==> DBG: Pos2: (" + p2.getLatitude() + ";"
							+ p2.getLongitude() + ")");
			Log.d("GeoUtils",
					String.format("==> DBG: Distance: %.3f%n", distance));
			Log.d("GeoUtils", "==> DBG: accuracyRadius: " + accuracyRadiuses);
			Log.d("GeoUtils", "==> DBG: distance < accRadiuses : "
					+ (distance < accuracyRadiuses));
			Log.d("GeoUtils", "==> DBG: distance < maximumDist : "
					+ (distance < Configuration.MAXIMUM_DISTANCE_THRESHOLD));
		}

		// return distance < accuracyRadiuses || distance <
		// Configuration.MAXIMUM_DISTANCE_THRESHOLD;
		return distance < Configuration.MAXIMUM_DISTANCE_THRESHOLD;

	}
}
