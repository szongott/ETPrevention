package de.unihannover.dcsec.eviltwin.prevention;

public class ConnectionWatchDog {

	private static ConnectionWatchDog instance;
	private boolean allowConnection;

	private ConnectionWatchDog() {
		allowConnection = false;
	}

	public static ConnectionWatchDog getInstance() {
		if (instance == null) {
			instance = new ConnectionWatchDog();
		}
		return instance;
	}

	public void setAllowConnection(boolean ac) {
		this.allowConnection = ac;
	}

	public boolean connectionAllowed() {
		return this.allowConnection;
	}
}
