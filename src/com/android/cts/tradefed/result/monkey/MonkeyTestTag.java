package com.android.cts.tradefed.result.monkey;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.cts.tradefed.result.AbstractXmlPullParser;
import com.android.cts.tradefed.result.CtsXmlResultReporter;

public class MonkeyTestTag extends AbstractXmlPullParser {

	public String getFinalLog() {
		return finalLog;
	}

	public void setFinalLog(String finalLog) {
		this.finalLog = finalLog;
	}

	public String getFinalPng() {
		return finalPng;
	}

	public void setFinalPng(String finalPng) {
		this.finalPng = finalPng;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<EventTag> getEvents() {
		return events;
	}

	public void setEvents(List<EventTag> events) {
		this.events = events;
	}

	private static final String MONKEY_TEST_TAG = "MonkeyTest";
	private static final String APPLICATION_ATTR = "application";
	private static final String RESULT_ATTR = "result";
	private static final String EVENT_COUNT_ATTR = "count";
	private static final String FINALPNG_ATT = "final";
	private static final String FINALLOG_ATT = "log";
	private List<EventTag> events = new LinkedList<EventTag>();
	private EventTag currentTag = null;

	private String application = "NA";
	private String result = "NA";
	private int count = 0;
	private String finalPng = "NA";
	private String finalLog = "NA";

	public void addEvent(EventTag event) {
		events.add(event);
		currentTag = event;
	}

	public void serialize(KXmlSerializer serializer) throws IOException {
		serializer.startTag(CtsXmlResultReporter.ns, MONKEY_TEST_TAG);
		serializer.attribute(CtsXmlResultReporter.ns, APPLICATION_ATTR,
				getApplication());
		serializer.attribute(CtsXmlResultReporter.ns, EVENT_COUNT_ATTR,
				getCount() + "");
		serializer.attribute(CtsXmlResultReporter.ns, RESULT_ATTR, getResult());
		serializer.attribute(CtsXmlResultReporter.ns, FINALPNG_ATT,
				getFinalPng());
		serializer.attribute(CtsXmlResultReporter.ns, FINALLOG_ATT,
				getFinalLog());
		for (int i = events.size() - 1; i >= 0; i--) {
			events.get(i).serialize(serializer, i, count);
		}
		serializer.endTag(CtsXmlResultReporter.ns, MONKEY_TEST_TAG);
	}

	public EventTag getCurrentTag() {
		return currentTag;
	}

	public void setCurrentTag(EventTag currentTag) {
		this.currentTag = currentTag;
	}

	@Override
	public void parse(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG
					&& parser.getName().equals(EventTag.EVENT_TAG)) {
				String type = getAttribute(parser, EventTag.TYPE_ATTR);
				if (EventTag.EVENT_TYPE_DRAG.equals(type)) {
					DragTag dragTag = new DragTag();
					dragTag.parse(parser);
					events.add(dragTag);
				} else if (EventTag.EVENT_TYPE_KEY.equals(type)) {
					KeyTag keyTag = new KeyTag();
					keyTag.parse(parser);
					events.add(keyTag);
				} else if (EventTag.EVENT_TYPE_TAP.equals(type)) {
					TapTag tapTag = new TapTag();
					tapTag.parse(parser);
					events.add(tapTag);
				}
				if (events.size() >= 50)
					return;
			}
			eventType = parser.next();
		}

	}

}
