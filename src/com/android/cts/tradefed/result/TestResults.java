/*
 * Copyright (C) 2010 The Android Open Source Project
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
package com.android.cts.tradefed.result;

import com.android.cts.tradefed.build.CtsBuildProvider;
import com.android.cts.tradefed.result.monkey.MonkeyTestTag;
import com.android.tradefed.log.LogUtil.CLog;

import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data structure for the detailed CTS test results.
 * <p/>
 * Can deserialize results for test packages from XML
 */
class TestResults extends AbstractXmlPullParser {

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getCrashFile() {
		return crashFile;
	}

	public void setCrashFile(String crashFile) {
		this.crashFile = crashFile;
	}

	public String getHasCrash() {
		return hasCrash;
	}

	public void setHasCrash(String hasCrash) {
		this.hasCrash = hasCrash;
	}

	private static final String ns = CtsXmlResultReporter.ns;

	// XML constants
	static final String SUMMARY_TAG = "Summary";
	static final String HAS_CRASH_ATTR = "crash";
	static final String CRASH_FILE_ATTR = "path";
	static final String DURATION_ATTR = "duration";
	private static final String RESULT_ATTR = "result";

	public static final String FAILED_ATTR = "failed";

	public static final String TIMEOUT_ATTR = "notExecuted";

	public static final String NOT_EXECUTED_ATTR = "timeout";

	public static final String PASS_ATTR = "pass";

	private String hasCrash = "NA";
	private String crashFile = "NA";
	private String duration = "NA";
	private String result = "0 Crash.";
	

	private Map<String, TestPackageResult> mPackageMap = new LinkedHashMap<String, TestPackageResult>();
	private DeviceInfoResult mDeviceInfo = new DeviceInfoResult();

	private MonkeyTestTag monkeyTag = new MonkeyTestTag();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG
					&& parser.getName().equals(DeviceInfoResult.TAG)) {
				mDeviceInfo.parse(parser);
			}
			if (eventType == XmlPullParser.START_TAG
					&& parser.getName().equals(TestPackageResult.TAG)) {
				TestPackageResult pkg = new TestPackageResult();
				pkg.parse(parser);
				if (pkg.getAppPackageName() != null) {
					mPackageMap.put(pkg.getAppPackageName(), pkg);
				} else {
					CLog.w("Found package with no app package name");
				}
			}
			eventType = parser.next();
		}
	}

	/**
	 * @return the list of {@link TestPackageResult}.
	 */
	public Collection<TestPackageResult> getPackages() {
		return mPackageMap.values();
	}

	/**
	 * Count the number of tests with given status
	 * 
	 * @param pass
	 * @return
	 */
	public int countTests(CtsTestStatus status) {
		int total = 0;
		for (TestPackageResult result : mPackageMap.values()) {
			total += result.countTests(status);
		}
		return total;
	}

	/**
	 * Serialize the test results to XML.
	 *
	 * @param serializer
	 * @throws IOException
	 */
	public void serialize(KXmlSerializer serializer) throws IOException {
		mDeviceInfo.serialize(serializer);
		serializeHostInfo(serializer);
		serializeTestSummary(serializer);
		monkeyTag.serialize(serializer);
		// sort before serializing
		List<TestPackageResult> pkgs = new ArrayList<TestPackageResult>(
				mPackageMap.values());
		Collections.sort(pkgs, new PkgComparator());
		for (TestPackageResult r : pkgs) {
			r.serialize(serializer);
		}
	}

	// private void serializeMonkeyTest(KXmlSerializer serializer)throws
	// IOException{
	// monkeyTag.serialize(serializer);
	// }
	/**
	 * Output the host info XML.
	 *
	 * @param serializer
	 */
	private void serializeHostInfo(KXmlSerializer serializer)
			throws IOException {
		serializer.startTag(ns, "HostInfo");

		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ignored) {
		}
		serializer.attribute(ns, "name", hostName);

		serializer.startTag(ns, "Os");
		serializer.attribute(ns, "name", System.getProperty("os.name"));
		serializer.attribute(ns, "version", System.getProperty("os.version"));
		serializer.attribute(ns, "arch", System.getProperty("os.arch"));
		serializer.endTag(ns, "Os");

		serializer.startTag(ns, "Java");
		serializer.attribute(ns, "name", System.getProperty("java.vendor"));
		serializer.attribute(ns, "version", System.getProperty("java.version"));
		serializer.endTag(ns, "Java");

		serializer.startTag(ns, "Cts");
		serializer.attribute(ns, "version", CtsBuildProvider.CTS_BUILD_VERSION);
		// TODO: consider outputting other tradefed options here
		serializer.startTag(ns, "IntValue");
		serializer.attribute(ns, "name", "testStatusTimeoutMs");
		// TODO: create a constant variable for testStatusTimeoutMs value.
		// Currently it cannot be
		// changed
		serializer.attribute(ns, "value", "600000");
		serializer.endTag(ns, "IntValue");
		serializer.endTag(ns, "Cts");

		serializer.endTag(ns, "HostInfo");
	}

	/**
	 * Output the test summary XML containing summary totals for all tests.
	 *
	 * @param serializer
	 * @throws IOException
	 */
	private void serializeTestSummary(KXmlSerializer serializer)
			throws IOException {
		serializer.startTag(ns, SUMMARY_TAG);

		serializer.attribute(ns, HAS_CRASH_ATTR, getHasCrash());
		serializer.attribute(ns, CRASH_FILE_ATTR, getCrashFile());
		serializer.attribute(ns, DURATION_ATTR, getDuration());
		serializer.attribute(CtsXmlResultReporter.ns, RESULT_ATTR, getResult());
		serializer.endTag(ns, SUMMARY_TAG);
	}

	private static class PkgComparator implements Comparator<TestPackageResult> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(TestPackageResult o1, TestPackageResult o2) {
			return o1.getAppPackageName().compareTo(o2.getAppPackageName());
		}

	}

	/**
	 * Return existing package with given app package name. If not found, create
	 * a new one.
	 * 
	 * @param name
	 * @return
	 */
	public TestPackageResult getOrCreatePackage(String appPackageName) {
		TestPackageResult pkgResult = mPackageMap.get(appPackageName);
		if (pkgResult == null) {
			pkgResult = new TestPackageResult();
			pkgResult.setAppPackageName(appPackageName);
			mPackageMap.put(appPackageName, pkgResult);
		}
		return pkgResult;
	}

	/**
	 * Populate the results with collected device info metrics.
	 * 
	 * @param runMetrics
	 */
	public void populateDeviceInfoMetrics(Map<String, String> runMetrics) {
		mDeviceInfo.populateMetrics(runMetrics);
	}

	public MonkeyTestTag getMonkeyTag() {
		return monkeyTag;
	}

	public void setMonkeyTag(MonkeyTestTag monkeyTag) {
		this.monkeyTag = monkeyTag;
	}
	
	public int getStatusBarHeight(){
		String height = mDeviceInfo.getStatusBarHeight();
		return (int)Float.parseFloat("48.0");
	}

}
