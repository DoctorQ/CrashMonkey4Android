package com.android.cts.tradefed.testtype.monkey;

import java.io.IOException;

import com.android.chimpchat.adb.AdbChimpDevice;


//设备按键事件,包括:基本的导航事件(nav),主要导航事件(majornav)和系统事件(syskeys)
public class MonkeyKeyEvent extends MonkeyEvent {
	public String getKeyCode() {
		return mKeyCode;
	}

	public void setKeyCode(String mKeyCode) {
		this.mKeyCode = mKeyCode;
	}

	private String mKeyCode;

	public MonkeyKeyEvent(String keyCode) {
		super(EVENT_TYPE_KEY);
		this.mKeyCode = keyCode;
	}
	
	@Override
	public int fireEvent(AdbChimpDevice acDevice) {
		try {
			acDevice.getManager().press(mKeyCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
