package com.cqupt.filter;

public class ECGFilter {
	public ECGFilter() {
		load();
	}

	private void load() {
		try {
			System.loadLibrary("filter");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public native long init();

	public native void filterArray(long filter, int[] inBuf, int[] outBuf,
			int size);

	public native float filterInt(long filter, int in);

	public native int deInit(long filter);

}
