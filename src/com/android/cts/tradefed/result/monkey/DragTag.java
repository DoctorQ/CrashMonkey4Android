package com.android.cts.tradefed.result.monkey;

import java.awt.Point;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.cts.tradefed.result.CtsXmlResultReporter;

public class DragTag extends EventTag {
	
	public static final String DIRECTION_UP = "up";
	public static final String DIRECTION_DOWN = "down";
	public static final String DIRECTION_MOVE = "move";

	public List<TouchTag> getTouches() {
		return touches;
	}

	public DragTag() {
		setType(EVENT_TYPE_DRAG);
	}

	private List<TouchTag> touches = new LinkedList<TouchTag>();

	@Override
	public void serialize(KXmlSerializer serializer, int index,int count)
			throws IOException {
		setIndex(index);
		setPos(count - 1 - index);
		// TODO Auto-generated method stub
		serializer.startTag(CtsXmlResultReporter.ns, EVENT_TAG);
		serializer.attribute(CtsXmlResultReporter.ns, INDEX_ATTR, getIndex()
				+ "");
		serializer.attribute(CtsXmlResultReporter.ns, TYPE_ATTR, getType());
		serializer.attribute(CtsXmlResultReporter.ns, IMAGE_ATTR, getImage());
		serializer.attribute(CtsXmlResultReporter.ns, LOG_ATTR, getLog());
		serializer.attribute(CtsXmlResultReporter.ns, POS_ATTR, getPos() + "");
		for (TouchTag touchTag : touches) {
			touchTag.serialize(serializer);
		}
		serializer.endTag(CtsXmlResultReporter.ns, EVENT_TAG);
	}

	public void addTouchTag(TouchTag touchTag) {
		touches.add(touchTag);
	}

	public void addTouchUp(Point point) {
		TouchTag tag = new TouchTag();
		tag.setDirection(DIRECTION_UP);
		setXY(point, tag);
	}

	public void addTouchMove(Point point) {
		TouchTag tag = new TouchTag();
		tag.setDirection(DIRECTION_MOVE);
		setXY(point, tag);
	}

	public void addTouchDown(Point point) {
		TouchTag tag = new TouchTag();
		tag.setDirection(DIRECTION_DOWN);
		setXY(point, tag);
	}

	private void setXY(Point point, TouchTag tag) {
		tag.setX(point.x);
		tag.setY(point.y);
		touches.add(tag);
	}

	@Override
	public void parse(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (!parser.getName().equals(EVENT_TAG)) {
			throw new XmlPullParserException(String.format(
					"invalid XML: Expected %s tag but received %s", EVENT_TAG,
					parser.getName()));
		}
		setIndex(Integer.parseInt(getAttribute(parser, INDEX_ATTR)));
		setImage(getAttribute(parser, IMAGE_ATTR));
		setLog(getAttribute(parser, LOG_ATTR));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG
					&& parser.getName().equals(TouchTag.TOUCH_TAG)) {
				TouchTag touchTag = new TouchTag();
				touchTag.parse(parser);
				touches.add(touchTag);
			} else if (eventType == XmlPullParser.END_TAG
					&& parser.getName().equals(EVENT_TAG)) {
				return;
			}

			eventType = parser.next();
		}
	}
	
	
}
