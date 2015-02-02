package de.unihannover.dcsec.eviltwin.prevention;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class ETPLocationGrabber implements ConnectionCallbacks,
		OnConnectionFailedListener {
	private static ETPLocationGrabber instance = null;

	private static Context appcontext = null;

	private final String TAG = "ETPrevention";

	LocationClient locationClient;

	UUID id;

	public static ETPLocationGrabber getInstance() throws Exception {
		if (instance == null) {
			if (appcontext != null) {
				instance = new ETPLocationGrabber(appcontext);
			} else {
				throw new Exception(
						"Appcontext has to be set before intstantiation.");
			}
		}
		return instance;
	}

	private ETPLocationGrabber(Context context) {
	}

	public static void setAppContext(Context context) {
		appcontext = context;
	}

	public void saveCurrentLocation() {
		final int playServicesAvailable = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(appcontext);

		if (playServicesAvailable == ConnectionResult.SUCCESS) {
			startPlayServiceLocator();
		} else {
			new Exception("Google Play Services not available");
		}

	}

	private void startPlayServiceLocator() {
		locationClient = new LocationClient(appcontext, this, this);
		locationClient.connect();
	}

	// Methods for Google Play Services
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		new Exception("Connection to Google Play Services failed!");
	}

	@SuppressLint("NewApi")
	@Override
	public void onConnected(Bundle arg0) {
		if (Configuration.DEBUG_ETS_LOCATION_GRABBER)
			Log.d(TAG,
					"Connection to Google Play Services Location established");

		Location playLoc = null;

		if (locationClient.isConnected()) {
			playLoc = locationClient.getLastLocation();
		}

		if (playLoc != null) {

//			System.out.println("Current Location: " + playLoc);

			ETPEngine.getInstance().learnCandidateLocation = playLoc;
		
		} else {
			new Exception("Something went wrong with Google Play Services...");
		}

	}

	@Override
	public void onDisconnected() {
		// Log.d(TAG, "Disconnected from Google Play Services Location");

	}
}
