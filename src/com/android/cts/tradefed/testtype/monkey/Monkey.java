package com.android.cts.tradefed.testtype.monkey;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import com.android.chimpchat.adb.AdbChimpDevice;
import com.android.cts.tradefed.result.CtsXmlResultReporter;

public class Monkey {

	private String testPackage;

	private MonkeySourceRandom mEventSource;

	/** Categories we are allowed to launch **/
	private ArrayList<String> mMainCategories = new ArrayList<String>();
	/** Applications we can switch to. */

	private long mThrottle = 300;
	private int mVerbose = 1;
	private boolean mRandomizeThrottle = false;
	private AdbChimpDevice mDevice;
	private Rectangle mRectangle = null;

	public Monkey(String testPackage, AdbChimpDevice device, float[] factors) {
		this.testPackage = testPackage;
		this.mDevice = device;
		init(factors);
	}

	/**
	 * Fire next random event
	 */
	public void nextRandomEvent(CtsXmlResultReporter ctsXmlResultReporter) {
		MonkeyEvent ev = mEventSource.getNextEvent();
		
		// System.out.println("Firing Monkey Event:" + ev.toString());
		if (ev != null) {
			ctsXmlResultReporter.addMonkeyEvent(ev);
			ev.fireEvent(mDevice);
		}

	}
	
	public void setY(int y){
		mRectangle.setY(y);
	}

	/**
	 * Initiate the monkey
	 */
	private void init(float[] factors) {
		Random mRandom = new SecureRandom();
		mRandom.setSeed(-1);

		mRectangle = new Rectangle(Integer.parseInt(mDevice
				.getProperty("display.width")), Integer.parseInt(mDevice
				.getProperty("display.height")));
		mEventSource = new MonkeySourceRandom(mRandom, mThrottle,
				mRandomizeThrottle, mRectangle);
		mEventSource.setVerbose(mVerbose);
		for (int i = 0; i < factors.length; i++) {
			if (factors[i] > 0) {
				mEventSource.setFactors(i, -factors[i]);
			}
		}

		mEventSource.validate();

		// start a random activity
		// mEventSource.generateActivity();
	}

}
