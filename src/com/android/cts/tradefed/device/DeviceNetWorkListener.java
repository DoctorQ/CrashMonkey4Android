package com.android.cts.tradefed.device;

import com.android.tradefed.device.DeviceNotAvailableException;
import com.android.tradefed.device.ITestDevice;
import com.android.tradefed.log.LogUtil.CLog;
import com.android.tradefed.targetprep.TargetSetupError;

public class DeviceNetWorkListener implements Runnable {
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	private String mWifiSSID = null;
	private String mWifiPsk = null;
	private boolean running = true;

	private ITestDevice mDevice;

	public DeviceNetWorkListener(ITestDevice device, String wifiSsid,
			String wifiPsk) {
		// TODO Auto-generated constructor stub
		this.mDevice = device;
		this.mWifiSSID = wifiSsid;
		this.mWifiPsk = wifiPsk;
	}

	@Override
	public void run() {
		if (mWifiSSID == null)
			return;
		while (running) {
			try {
				if (mDevice.checkWifiConnection(mWifiSSID)) {
					CLog.i(String.format("%s connected ", mWifiSSID));
				} else {
					CLog.i(String.format("please connect wifi : %s", mWifiSSID));
					mDevice.connectToWifiNetwork(mWifiSSID, mWifiPsk);
				}
			} catch (TargetSetupError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DeviceNotAvailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		CLog.i("DeviceNetWorkListener stop.....");
		// TODO Auto-generated method stub

	}

}
