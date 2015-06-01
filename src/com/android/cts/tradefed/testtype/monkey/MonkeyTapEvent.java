package com.android.cts.tradefed.testtype.monkey;

import java.awt.Point;
import java.io.IOException;

import com.android.chimpchat.adb.AdbChimpDevice;

//触摸事件是指在屏幕中的一个down-up事件，即在屏幕某处按下并抬起的操作

public class MonkeyTapEvent extends MonkeyEvent {

	public static final int ACTION_UP = 0;
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_DOWN = 2;
	private Point mPoint;

	public MonkeyTapEvent(Point point) {
		super(EVENT_TYPE_TAP);
		this.mPoint = point;
	}

	public Point getPoint() {
		return mPoint;
	}

	public void setPoint(Point mPoint) {
		this.mPoint = mPoint;
	}

	@Override
	public int fireEvent(AdbChimpDevice acDevice) {
		// TODO Auto-generated method stub
		try {
			acDevice.getManager().tap(mPoint.x, mPoint.y);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
