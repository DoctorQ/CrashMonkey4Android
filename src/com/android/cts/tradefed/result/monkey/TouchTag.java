package com.android.cts.tradefed.result.monkey;

import java.io.IOException;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.cts.tradefed.result.AbstractXmlPullParser;
import com.android.cts.tradefed.result.CtsXmlResultReporter;

public class TouchTag extends AbstractXmlPullParser {
	public static final String TOUCH_TAG = "Touch";
	private static final String DIRECTION_ATTR = "direction";
	private static final String X_ATTR = "x";
	private static final String Y_ATTR = "y";
	private String direction = "NA";
	private int x = 0;
	private int y = 0;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void serialize(KXmlSerializer serializer) throws IOException {
		serializer.startTag(CtsXmlResultReporter.ns, TOUCH_TAG);
		serializer.attribute(CtsXmlResultReporter.ns, DIRECTION_ATTR,
				getDirection());
		serializer.attribute(CtsXmlResultReporter.ns, X_ATTR, getX() + "");
		serializer.attribute(CtsXmlResultReporter.ns, Y_ATTR, getY() + "");
		serializer.endTag(CtsXmlResultReporter.ns, TOUCH_TAG);

	}

	@Override
	public void parse(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (!parser.getName().equals(TOUCH_TAG)) {
			throw new XmlPullParserException(String.format(
					"invalid XML: Expected %s tag but received %s", TOUCH_TAG,
					parser.getName()));
		}

		setDirection(getAttribute(parser, DIRECTION_ATTR));
		setX(Integer.parseInt(getAttribute(parser, X_ATTR)));
		setY(Integer.parseInt(getAttribute(parser, Y_ATTR)));
	}

}
