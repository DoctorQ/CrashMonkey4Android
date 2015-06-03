package com.android.cts.tradefed.result.monkey;

import java.io.IOException;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import sun.awt.SunHints.Value;

import com.android.cts.tradefed.result.CtsXmlResultReporter;

public class KeyTag extends EventTag {

	private static final String VALUE_ATTR = "value";

	private String value = "NA";

	public KeyTag() {
		setType(EVENT_TYPE_KEY);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void serialize(KXmlSerializer serializer, int index, int count)
			throws IOException {
		setIndex(index);
		setPos(count - 1 - index);
		serializer.startTag(CtsXmlResultReporter.ns, EVENT_TAG);
		serializer.attribute(CtsXmlResultReporter.ns, INDEX_ATTR, getIndex()
				+ "");
		serializer.attribute(CtsXmlResultReporter.ns, TYPE_ATTR, getType());
		
		serializer.attribute(CtsXmlResultReporter.ns, VALUE_ATTR, getValue());
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
		setValue(getAttribute(parser, VALUE_ATTR));
		setIndex(Integer.parseInt(getAttribute(parser, INDEX_ATTR)));
		setImage(getAttribute(parser, IMAGE_ATTR));
		setLog(getAttribute(parser, LOG_ATTR));
	}

}
