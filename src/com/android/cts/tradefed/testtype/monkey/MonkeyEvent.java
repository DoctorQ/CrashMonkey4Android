/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.cts.tradefed.testtype.monkey;

import com.android.chimpchat.adb.AdbChimpDevice;

/**
 * abstract class for monkey event
 */
public abstract class MonkeyEvent{
	protected int eventType;
	public static final int EVENT_TYPE_KEY = 0;
	public static final int EVENT_TYPE_TAP = 1;
	public static final int EVENT_TYPE_MOTION = 2;
	public static final int EVENT_TYPE_DRAG = 3;
	

	

	public MonkeyEvent(int type) {
		eventType = type;
	}

	/* (non-Javadoc)
	 * @see com.android.cts.tradefed.testtype.monkey.IMonkeyEvent#getEventType()
	 */
	public int getEventType() {
		return eventType;
	}

	/* (non-Javadoc)
	 * @see com.android.cts.tradefed.testtype.monkey.IMonkeyEvent#isThrottlable()
	 */
	public boolean isThrottlable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.android.cts.tradefed.testtype.monkey.IMonkeyEvent#fireEvent(com.android.chimpchat.adb.AdbChimpDevice)
	 */
	public abstract int fireEvent(AdbChimpDevice acDevice);
}
