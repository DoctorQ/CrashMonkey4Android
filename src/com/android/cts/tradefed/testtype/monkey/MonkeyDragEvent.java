package com.android.cts.tradefed.testtype.monkey;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.android.chimpchat.adb.AdbChimpDevice;

public class MonkeyDragEvent extends MonkeyEvent {

	private Point downPoint = null;
	private Point upPoint = null;

	public MonkeyDragEvent() {
		super(EVENT_TYPE_DRAG);
		// TODO Auto-generated constructor stub
	}

	public void setDownPoint(Point point) {
		downPoint = point;
	}

	public void setUpPoint(Point point) {
		upPoint = point;
	}

	public Point getDownPoint() {
		return downPoint;
	}

	public Point getUpPoint() {
		return upPoint;
	}

	@Override
	public int fireEvent(AdbChimpDevice acDevice) {
		// TODO Auto-generated method stub

		acDevice.drag(downPoint.x, downPoint.y, upPoint.x, upPoint.y, 10, 1000);

		return 0;
	}

}
