package com.android.cts.tradefed.device;

import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.device.ITestDevice;
import com.android.tradefed.log.LogUtil.CLog;

public class MonkeyActivityListener implements Runnable {
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	private ITestDevice mDevice;
	private String mPackage;
	private String mActivity;
	private boolean running = true;
	private static final String GET_ACTIVITY_CMD = "dumpsys activity top | grep ACTIVITY";

	public MonkeyActivityListener(ITestDevice device, String goalPackage,
			String goalActivity) {
		this.mDevice = device;
		this.mPackage = goalPackage;
		this.mActivity = goalActivity;
	}

	@Override
	public void run() {
		while (running) {
			try {
				String result = mDevice.executeShellCommand(GET_ACTIVITY_CMD);
				if (!result.contains(mPackage)) {
					CLog.i(String
							.format("Current activity is %s, It is not your test app %s.restart app",
									result, mPackage));
					mDevice.executeShellCommand("am start " + mPackage + "/"
							+ mActivity);
				}
			} catch (DeviceNotAvailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		CLog.i("MonkeyActivityListener stop.....");
	}
}
