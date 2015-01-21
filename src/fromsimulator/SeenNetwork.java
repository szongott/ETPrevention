package fromsimulator;

public class SeenNetwork {

	private String id;

	private String ssid;
	private String bssid;
	private int level;
	private String capabilities;
	private int frequency;

	public SeenNetwork(String ssid, String bssid,
			int level, String capabilities, int frequency) {
		// this.id = ssid+bssid+capabilities;
		this.id = ssid + bssid + capabilities;
		this.ssid = ssid;
		this.bssid = bssid;
		this.level = level;
		this.capabilities = capabilities;
		this.frequency = frequency;

	}

	public String getID() {
		return id;
	}

}
