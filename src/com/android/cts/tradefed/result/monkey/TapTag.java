package com.android.cts.tradefed.result.monkey;

import java.io.IOException;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.cts.tradefed.result.CtsXmlResultReporter;

public class TapTag extends EventTag {

	public TapTag() {
		setType(EVENT_TYPE_TAP);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	private static final String X_ATTR = "x";
	private static final String Y_ATTR = "y";

	private float x = 0;
	private float y = 0;

	@Override
	public void serialize(KXmlSerializer serializer, int index, int count)
			throws IOException {
		setIndex(index);
		setPos(count - 1 - index);
		// TODO Auto-generated method stub
		serializer.startTag(CtsXmlResultReporter.ns, EVENT_TAG);
		serializer.attribute(CtsXmlResultReporter.ns, INDEX_ATTR, getIndex()
				+ "");
		serializer.attribute(CtsXmlResultReporter.ns, TYPE_ATTR, getType());

		serializer.attribute(CtsXmlResultReporter.ns, X_ATTR, getX() + "");
		serializer.attribute(CtsXmlResultReporter.ns, Y_ATTR, getY() + "");
		serializer.attribute(CtsXmlResultReporter.ns, TIME_ATTR, getTime());
		serializer.attribute(CtsXmlResultReporter.ns, IMAGE_ATTR, getImage());
		serializer.attribute(CtsXmlResultReporter.ns, LOG_ATTR, getLog());
		serializer.attribute(CtsXmlResultReporter.ns, POS_ATTR, getPos() + "");
		serializer.endTag(CtsXmlResultReporter.ns, EVENT_TAG);

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
		setX(Float.parseFloat(getAttribute(parser, X_ATTR)));
		setY(Float.parseFloat(getAttribute(parser, Y_ATTR)));
	}

}
