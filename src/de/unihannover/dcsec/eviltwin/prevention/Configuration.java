package de.unihannover.dcsec.eviltwin.prevention;

import java.sql.Timestamp;

public class Configuration {

	// ===========================
	// = Debugging configuration =
	// ===========================
	public static final boolean DEBUG = false;

	public static final boolean DEBUG_HEARTBEAT_SERVICE = false;
	public static final boolean DEBUG_SECURE_SENDTASK = false;

	public static final boolean DEBUG_KILLSWITCH = false;

	public static final boolean DEBUG_WIFISTATUSRECEIVER = false;

	public static final boolean DEBUG_DB_CONNECTED = false;
	public static final boolean DEBUG_DB_CONFIGURED = false;
	public static final boolean DEBUG_DB_LOCATION = false;
	public static final boolean DEBUG_DB_SEEN_NETWORKS = false;

	public static final boolean DEBUG_NOTIFICATION = false;

	public static final boolean DEBUG_GET_COUNT_OF_LOTS = false;

	public static final boolean DEBUG_ETS_LOCATION_GRABBER = false;

	// =========================
	// = General configuration =
	// =========================
	public static final int STATUS_DISPLAY_REFRESH_INTERVAL = 3000; // 3 sec.

	public static final String LOCATION_PROVIDER_FUSED = "fused";

	// =========================
	// = Connection Parameters =
	// =========================

	public static final String SSL_PINNING_SHA512_CERT_HASH = "71E02B49FDD2B9C848DA9DE8D6CFE30F67449EFFE383BAF664C594754B0CA90CA88EA5C2AB0501694B1573F3A5223D36455C762671C8A3FF153C1482F427BA73";

	public static final String PSK_WEBSERVICE = "0392729b41e36f9fbe83943374b45015";
	public static final String PSK_KILLSWITCH = "7584365hj43g5hj3u4fb34f7773fv437";

	public static final String WEBSERVICE_URI_SCHEME = "https";
	public static final String WEBSERVICE_URI_AUTHORITY = "mobileresearch.dcsec.uni-hannover.de";
	public static final String WEBSERVICE_URI_PATH = "eviltwinstudyservice/webservice.php";

	// =========================
	// = Killswitch Parameters =
	// =========================
	public static final String URL_KILLSWITCH = "https://mobileresearch.dcsec.uni-hannover.de/eviltwinstudyservice/killswitch.php";
	public static final String PREF_KILL_SWITCH = "PREF_KILL_SWITCH";

	// ==========================
	// = Competition Parameters =
	// ==========================

	/**
	 * Time after which the FIRST lot is achieved (in milliseconds)
	 * 
	 * DEFAULT = 1 week = 7 * 24 * 60 * 60 * 1000
	 * 
	 * DEBUG = 5 minutes = 5 * 60 * 1000
	 */
	public static final long COMPETITION_FIRST_LOT_INTERVAL = 7 * 24 * 60 * 60
			* 1000;

	/**
	 * Time interval after which another lot is achieved (in milliseconds)
	 * 
	 * DEFAULT = 24 hours = 24 * 60 * 60 * 1000
	 * 
	 * DEBUG = 1 minute = 2 * 60 * 1000
	 */
	public static final long COMPETITION_LOT_INTERVAL = 24 * 60 * 60 * 1000;

	/**
	 * End point of the study, competition and survey submission
	 */
	public static Timestamp COMPETITION_END = Timestamp
			.valueOf("2014-02-28 23:59:59.000000000");

	/**
	 * Timespan before the end of the competition, when a notification should be
	 * shown.
	 * 
	 * DEFAULT = 1 week = 7 * 24 * 60 * 60 * 1000
	 */
	public static final long COMPETITION_WARNING_DELTA = 7 * 24 * 60 * 60
			* 1000;

	// ======================
	// = ETSLocationGrabber =
	// ======================

	// The maximum age of the fix in ms (2 minutes)
	public static final float THRESHOLD_AGE = 2.0f * 60.0f * 1000.0f;

	// How long should it be tried to retrieve a new location in s
	public static final int TIME_TO_CANCEL_GPS_FIX = 60;
	public static final int TIME_TO_CANCEL_NETWORK_FIX = 60;

	// constant to show much too old fixes
	public static final long MUCH_TOO_OLD = 1000000l;

	// =====================
	// =   Warning Codes   =
	// =====================

	public static final int ETPCODE_NOT_SET = 999;
	public static final int ETPCODE_CONNECTION_OK = 0;
	public static final int ETPCODE_UNKNOWN_SSID = -1;
	public static final int ETPCODE_UNKNOWN_BSSID = -2;
	public static final int ETPCODE_UNKNOWN_ENVIRONMENT = -3;
	public static final int ETPCODE_LOCATION_UNAVAILABLE = -4;

	public static final long MAX_TIMESPAN_RECENT_APPROVED_CONNECTION = 5000l;
	
	
	// =====================
	// =    Other stuff    =
	// =====================
	
	public static double JACCARD_ENVIRONMENT_OK = 0.7;
	public static double JACCARD_MIN_IMPROVEMENT = 0.7;
	
	public static long LEARNING_PHASE_NEW_AP_LENGTH = 7 * 24 * 60 * 60 * 1000;
	
	// How great is the distance in which two locations are handled as equal?
	public static double MAXIMUM_DISTANCE_THRESHOLD = 100.0;
}
