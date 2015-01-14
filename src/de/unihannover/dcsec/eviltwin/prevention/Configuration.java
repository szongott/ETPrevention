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

	// ====================
	// = HeartbeatService =
	// ====================
	public static final long HEARTBEAT_INTERVAL = 30 * 1000; // 30 seconds

	// ===================
	// = WelcomeActivity =
	// ===================
	public static final int BUTTON_COLOR_LUMINOSITY = 160;

	// ==========
	// = Survey =
	// ==========
	// increment for new submission of results
	public static final int CURRENT_SURVEY_SUBMISSION_VERSION = 0;

	public static final String PREF_SURVEY = "WLAN Studie Fragebogen";

}
