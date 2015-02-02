package de.unihannover.dcsec.eviltwin.prevention.utils;

public class Timer {

	private static Timer instance;

	private static long NOT_SET = -1;

	long start;
	long stop;

	public static Timer getInstance() {
		if (instance == null) {
			instance = new Timer();
		}

		return instance;
	}

	private void Timer() {
		start = NOT_SET;
		stop = NOT_SET;

	}

	public void start() {
		start = System.currentTimeMillis();
	}

	public void stop() {
		stop = System.currentTimeMillis();
	}

	public long getDelta() {
		if ((start != NOT_SET) && (stop != NOT_SET)) {
			return stop - start;
		}
		return NOT_SET;
	}

	public void clear() {
		start = NOT_SET;
		stop = NOT_SET;
	}
}
