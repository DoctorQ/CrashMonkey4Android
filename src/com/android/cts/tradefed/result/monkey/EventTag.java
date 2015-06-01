package com.android.cts.tradefed.result.monkey;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.kxml2.io.KXmlSerializer;

import com.android.cts.tradefed.result.AbstractXmlPullParser;

public abstract class EventTag extends AbstractXmlPullParser {
	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	protected static final String EVENT_TAG = "Event";
	protected static final String INDEX_ATTR = "index";
	protected static final String TYPE_ATTR = "type";
	protected static final String IMAGE_ATTR = "image";
	protected static final String LOG_ATTR = "log";
	protected static final String POS_ATTR = "pos";

	public static final String EVENT_TYPE_DRAG = "drag";
	public static final String EVENT_TYPE_TAP = "tap";
	public static final String EVENT_TYPE_KEY = "key";
	private int index = 0;
	private String type = "NA";
	private String image = "ss";
	private String log = "NA";
	private int pos = 0;
	

	public int getIndex() {
		return index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.cts.tradefed.result.monkey.Event#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.cts.tradefed.result.monkey.Event#getType()
	 */
	public String getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.cts.tradefed.result.monkey.Event#setType(java.lang.String)
	 */

	public void setType(String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.cts.tradefed.result.monkey.Event#getImage()
	 */

	public String getImage() {
		return image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.cts.tradefed.result.monkey.Event#setImage(java.lang.String)
	 */

	public void setImage(String image) {
		this.image = image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.cts.tradefed.result.monkey.Event#getLog()
	 */

	public String getLog() {
		return log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.android.cts.tradefed.result.monkey.Event#setLog(java.lang.String)
	 */

	public void setLog(String log) {
		this.log = log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.cts.tradefed.result.monkey.Event#serialize(org.kxml2.io.
	 * KXmlSerializer, int)
	 */

	public abstract void serialize(KXmlSerializer serializer, int index,int count)
			throws IOException;

}
