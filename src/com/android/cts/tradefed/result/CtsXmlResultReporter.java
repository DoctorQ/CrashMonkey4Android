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

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kxml2.io.KXmlSerializer;

import com.android.cts.tradefed.build.CtsBuildHelper;
import com.android.cts.tradefed.device.DeviceInfoCollector;
import com.android.cts.tradefed.result.monkey.DragTag;
import com.android.cts.tradefed.result.monkey.EventTag;
import com.android.cts.tradefed.result.monkey.KeyTag;
import com.android.cts.tradefed.result.monkey.MonkeyTestTag;
import com.android.cts.tradefed.result.monkey.MotionTag;
import com.android.cts.tradefed.result.monkey.TapTag;
import com.android.cts.tradefed.testtype.CtsTest;
import com.android.cts.tradefed.testtype.MonkeyTest;
import com.android.cts.tradefed.testtype.monkey.MonkeyDragEvent;
import com.android.cts.tradefed.testtype.monkey.MonkeyEvent;
import com.android.cts.tradefed.testtype.monkey.MonkeyKeyEvent;
import com.android.cts.tradefed.testtype.monkey.MonkeyMotionEvent;
import com.android.cts.tradefed.testtype.monkey.MonkeyTapEvent;
import com.android.ddmlib.Log;
import com.android.ddmlib.Log.LogLevel;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.tradefed.build.IBuildInfo;
import com.android.tradefed.build.IFolderBuildInfo;
import com.android.tradefed.config.Option;
import com.android.tradefed.invoker.TestInvocation;
import com.android.tradefed.log.LogUtil.CLog;
import com.android.tradefed.result.ILogFileSaver;
import com.android.tradefed.result.ITestInvocationListener;
import com.android.tradefed.result.InputStreamSource;
import com.android.tradefed.result.LogDataType;
import com.android.tradefed.result.LogFileSaver;
import com.android.tradefed.result.TestSummary;
import com.android.tradefed.util.FileUtil;
import com.android.tradefed.util.StreamUtil;

/**
 * Writes results to an XML files in the CTS format.
 * <p/>
 * Collects all test info in memory, then dumps to file when invocation is
 * complete.
 * <p/>
 * Outputs xml in format governed by the cts_result.xsd
 */
public class CtsXmlResultReporter implements ITestInvocationListener {
	public File getmLogDir() {
		return mLogDir;
	}

	public void setmLogDir(File mLogDir) {
		this.mLogDir = mLogDir;
	}

	private static final String LOG_TAG = "CtsXmlResultReporter";

	static final String TEST_RESULT_FILE_NAME = "testResult.xml";
	private static final String CTS_RESULT_FILE_VERSION = "1.14";
	private static final String[] CTS_RESULT_RESOURCES = { "cts_result.xsl",
			"cts_result.css", "logo.gif", "newrule-green.png" };

	/** the XML namespace */
	public static final String ns = null;

	static final String RESULT_TAG = "TestResult";
	static final String PLAN_ATTR = "testPlan";
	static final String STARTTIME_ATTR = "starttime";
	static final String LOGCAT_ATTR = "log";

	private static final String REPORT_DIR_NAME = "output-file-path";
	@Option(name = REPORT_DIR_NAME, description = "root file system path to directory to store xml "
			+ "test results and associated logs. If not specified, results will be stored at "
			+ "<cts root>/repository/results")
	protected File mReportDir = null;

	// listen in on the plan option provided to CtsTest
	@Option(name = CtsTest.PLAN_OPTION, description = "the test plan to run.")
	private String mPlanName = "NA";

	// listen in on the continue-session option provided to CtsTest
	@Option(name = CtsTest.CONTINUE_OPTION, description = "the test result session to continue.")
	private Integer mContinueSessionId = null;

	@Option(name = "quiet-output", description = "Mute display of test results.")
	private boolean mQuietOutput = false;

	@Option(name = "result-server", description = "Server to publish test results.")
	private String mResultServer;

	@Option(name = "p", description = "package of test app")
	private String mPackage = null;

	@Option(name = "report-path", description = "result report save path")
	private String mReportPath = null;
	@Option(name = "v", description = "monkey event count")
	private int mInjectEvents = 1000;

	protected IBuildInfo mBuildInfo;
	private String mStartTime;
	private String mDeviceSerial;
	private String mLogPath;
	private TestResults mResults = new TestResults();
	private TestPackageResult mCurrentPkgResult = null;
	private boolean mIsDeviceInfoRun = false;
	private ResultReporter mReporter;
	private File mLogDir;
	private String mSuiteName;
	private MonkeyTestTag monkeyTag = null;
	private File mPngFile = null;
	private File mLogFile = null;

	private EventTag mEventTag = null;

	private static final Pattern mPtsLogPattern = Pattern
			.compile("(.*)\\+\\+\\+\\+(.*)");

	public void setReportDir(File reportDir) {
		mReportDir = reportDir;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invocationStarted(IBuildInfo buildInfo) {
		mBuildInfo = buildInfo;
		if (!(buildInfo instanceof IFolderBuildInfo)) {
			throw new IllegalArgumentException(
					"build info is not a IFolderBuildInfo");
		}
		IFolderBuildInfo ctsBuild = (IFolderBuildInfo) buildInfo;
		CtsBuildHelper ctsBuildHelper = getBuildHelper(ctsBuild);
		mDeviceSerial = buildInfo.getDeviceSerial() == null ? "unknown_device"
				: buildInfo.getDeviceSerial();
		if (mContinueSessionId != null) {
			CLog.d("Continuing session %d", mContinueSessionId);
			// reuse existing directory
			TestResultRepo resultRepo = new TestResultRepo(
					ctsBuildHelper.getResultsDir());
			mResults = resultRepo.getResult(mContinueSessionId);
			if (mResults == null) {
				throw new IllegalArgumentException(String.format(
						"Could not find session %d", mContinueSessionId));
			}
			mPlanName = resultRepo.getSummaries().get(mContinueSessionId)
					.getTestPlan();
			mStartTime = resultRepo.getSummaries().get(mContinueSessionId)
					.getStartTime();
			mReportDir = resultRepo.getReportDir(mContinueSessionId);
		} else {
			if (mReportDir == null) {
				mReportDir = ctsBuildHelper.getResultsDir();
			}
			mReportDir = createUniqueReportDir(mReportDir);

			mStartTime = getTimestamp();
			logResult("Created result dir %s", mReportDir.getName());
		}
		mSuiteName = ctsBuildHelper.getSuiteName();
		mReporter = new ResultReporter(mResultServer, mSuiteName);

		// TODO: allow customization of log dir
		// create a unique directory for saving logs, with same name as result
		// dir
		File rootLogDir = getBuildHelper(ctsBuild).getLogsDir();
		mLogDir = new File(rootLogDir, mReportDir.getName());
		mLogDir.mkdirs();

		// 设置Monkey相关的tag
		monkeyTag = mResults.getMonkeyTag();
		if (mPackage != null)
			monkeyTag.setApplication(mPackage);
		monkeyTag.setCount(mInjectEvents);
	}

	/**
	 * Create a unique directory for saving results.
	 * <p/>
	 * Currently using legacy CTS host convention of timestamp directory names.
	 * In case of collisions, will use {@link FileUtil} to generate unique file
	 * name.
	 * <p/>
	 * TODO: in future, consider using LogFileSaver to create build-specific
	 * directories
	 *
	 * @param parentDir
	 *            the parent folder to create dir in
	 * @return the created directory
	 */
	private static synchronized File createUniqueReportDir(File parentDir) {
		// TODO: in future, consider using LogFileSaver to create build-specific
		// directories

		File reportDir = new File(parentDir, TimeUtil.getResultTimestamp());
		if (reportDir.exists()) {
			// directory with this timestamp exists already! Choose a unique,
			// although uglier, name
			try {
				reportDir = FileUtil.createTempDir(
						TimeUtil.getResultTimestamp() + "_", parentDir);
			} catch (IOException e) {
				CLog.e(e);
				CLog.e("Failed to create result directory %s",
						reportDir.getAbsolutePath());
			}
		} else {
			if (!reportDir.mkdirs()) {
				// TODO: consider throwing an exception
				CLog.e("mkdirs failed when attempting to create result directory %s",
						reportDir.getAbsolutePath());
			}
		}
		return reportDir;
	}

	/**
	 * Helper method to retrieve the {@link CtsBuildHelper}.
	 * 
	 * @param ctsBuild
	 */
	CtsBuildHelper getBuildHelper(IFolderBuildInfo ctsBuild) {
		CtsBuildHelper buildHelper = new CtsBuildHelper(ctsBuild.getRootDir());
		try {
			buildHelper.validateStructure();
		} catch (FileNotFoundException e) {
			// just log an error - it might be expected if we failed to retrieve
			// a build
			CLog.e("Invalid CTS build %s", ctsBuild.getRootDir());
		}
		return buildHelper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testLog(String dataName, LogDataType dataType,
			InputStreamSource dataStream) {

		try {
			File logFile = getLogFileSaver().saveAndZipLogData(dataName,
					dataType, dataStream.createInputStream());
			if (dataType == LogDataType.PNG) {
				// 保存图片
				if (dataName.startsWith(MonkeyTest.FINAL_SCREENSHOT)) {
					monkeyTag.setFinalPng(logFile.getAbsolutePath());
				} else {
					mPngFile = logFile;
				}
			} else if (dataName.startsWith("logcat")
					&& dataType == LogDataType.TEXT) {
				// 保存log信息
				mLogFile = logFile;
				if (logFile != null && mEventTag != null)
					mEventTag.setLog(mLogFile.getPath());
			} else if (dataName.startsWith("monkey")
					&& dataType == LogDataType.TEXT) {
				monkeyTag.setFinalLog(logFile.getAbsolutePath());
				setAndAnalyzeLog(logFile);
				// monkeyTag.setResult(crashAnalyzer.getCrashCount() +
				// " Crash");
			}
			if (TestInvocation.DEVICE_LOG_NAME.equals(dataName)) {
				mLogPath = logFile.getAbsolutePath();
				if (mPackage == null) {
					monkeyTag.setFinalLog(logFile.getAbsolutePath());
					setAndAnalyzeLog(logFile);
				}
			}
			logResult(String.format("Saved log %s", logFile.getName()));
		} catch (IOException e) {
			CLog.e("Failed to write log for %s", dataName);
		}
	}

	private void setAndAnalyzeLog(File logFile) {
		CrashAnalyzer crashAnalyzer = new CrashAnalyzer(logFile);
		crashAnalyzer.parserLogcat();
		if (crashAnalyzer.hasCrash()) {
			mResults.setHasCrash("YES");
			mResults.setCrashFile(crashAnalyzer.getmCrashFile()
					.getAbsolutePath());
			mResults.setResult(crashAnalyzer.getCrashCount() + " Crash.");
		}
	}

	/**
	 * Return the {@link ILogFileSaver} to use.
	 * <p/>
	 * Exposed for unit testing.
	 */
	ILogFileSaver getLogFileSaver() {
		return new LogFileSaver(mLogDir);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunStarted(String name, int numTests) {
		if (mCurrentPkgResult != null
				&& !name.equals(mCurrentPkgResult.getAppPackageName())) {
			// display results from previous run
			logCompleteRun(mCurrentPkgResult);
		}
		mIsDeviceInfoRun = name.equals(DeviceInfoCollector.APP_PACKAGE_NAME);
		if (mIsDeviceInfoRun) {
			logResult("Collecting device info");
		} else {
			if (mCurrentPkgResult == null
					|| !name.equals(mCurrentPkgResult.getAppPackageName())) {
				logResult("-----------------------------------------");
				logResult("Test package %s started", name);
				logResult("-----------------------------------------");
			}
			mCurrentPkgResult = mResults.getOrCreatePackage(name);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testStarted(TestIdentifier test) {
		mCurrentPkgResult.insertTest(test);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testFailed(TestFailure status, TestIdentifier test, String trace) {
		mCurrentPkgResult.reportTestFailure(test, CtsTestStatus.FAIL, trace);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testEnded(TestIdentifier test, Map<String, String> testMetrics) {
		collectPtsResults(test, testMetrics);
		mCurrentPkgResult.reportTestEnded(test);
		Test result = mCurrentPkgResult.findTest(test);
		String stack = result.getStackTrace() == null ? "" : "\n"
				+ result.getStackTrace();
		logResult("%s#%s %s %s", test.getClassName(), test.getTestName(),
				result.getResult(), stack);
	}

	/**
	 * Collect Pts results for both device and host tests to the package result.
	 * 
	 * @param test
	 *            test ran
	 * @param testMetrics
	 *            test metrics which can contain performance result for device
	 *            tests
	 */
	private void collectPtsResults(TestIdentifier test,
			Map<String, String> testMetrics) {
		// device test can have performance results in testMetrics
		String perfResult = PtsReportUtil.getPtsResultFromMetrics(testMetrics);
		// host test should be checked in PtsHostStore.
		if (perfResult == null) {
			perfResult = PtsHostStore.removePtsResult(mDeviceSerial, test);
		}
		if (perfResult != null) {
			// PTS result is passed in Summary++++Details format.
			// Extract Summary and Details, and pass them.
			Matcher m = mPtsLogPattern.matcher(perfResult);
			if (m.find()) {
				mCurrentPkgResult.reportPerformanceResult(test,
						CtsTestStatus.PASS, m.group(1), m.group(2));
			} else {
				logResult("PTS Result unrecognizable:" + perfResult);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunEnded(long elapsedTime, Map<String, String> runMetrics) {
		if (mIsDeviceInfoRun) {
			mResults.populateDeviceInfoMetrics(runMetrics);
		} else {
			mCurrentPkgResult.populateMetrics(runMetrics);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invocationEnded(long elapsedTime) {
		// display the results of the last completed run
		if (mCurrentPkgResult != null) {
			logCompleteRun(mCurrentPkgResult);
		}
		if (mReportDir == null || mStartTime == null) {
			// invocationStarted must have failed, abort
			CLog.w("Unable to create XML report");
			return;
		}

		File reportFile = getResultFile(mReportDir);
		createXmlResult(reportFile, mStartTime, elapsedTime);
		//copyFormattingFiles(mReportDir);
		zipResults(mReportDir);

		try {
			mReporter.reportResult(reportFile);
		} catch (IOException e) {
			CLog.e(e);
		}

		createReporter(reportFile);
	}

	private void createReporter(File xmlFile) {
		MonkeyReporter reporter = null;
		if (mReportPath == null) {
			reporter = new MonkeyReporter(xmlFile);
		} else {
			reporter = new MonkeyReporter(xmlFile, new File(mReportPath));
		}
		reporter.createReporter();
		try {
			reporter.drawImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void logResult(String format, Object... args) {
		if (mQuietOutput) {
			CLog.d(format, args);
		} else {
//			Log.logAndDisplay(LogLevel.DEBUG, mDeviceSerial,
//					String.format(format, args));
		}
	}

	private void logCompleteRun(TestPackageResult pkgResult) {
		if (pkgResult.getAppPackageName().equals(
				DeviceInfoCollector.APP_PACKAGE_NAME)) {
			logResult("Device info collection complete");
			return;
		}
		logResult("%s package complete: Passed %d, Failed %d, Not Executed %d",
				pkgResult.getAppPackageName(),
				pkgResult.countTests(CtsTestStatus.PASS),
				pkgResult.countTests(CtsTestStatus.FAIL),
				pkgResult.countTests(CtsTestStatus.NOT_EXECUTED));
	}

	/**
	 * Creates a report file and populates it with the report data from the
	 * completed tests.
	 */
	private void createXmlResult(File reportFile, String startTimestamp,
			long elapsedTime) {
		String endTime = getTimestamp();
		// 设置Monkey执行时间
		mResults.setDuration(TimeUtil.formatElapsedTime(elapsedTime));
		OutputStream stream = null;
		try {
			stream = createOutputResultStream(reportFile);
			KXmlSerializer serializer = new KXmlSerializer();
			serializer.setOutput(stream, "UTF-8");
			serializer.startDocument("UTF-8", false);
			serializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			serializer
					.processingInstruction("xml-stylesheet type=\"text/xsl\"  "
							+ "href=\"result.xsl\"");
			serializeResultsDoc(serializer, startTimestamp, endTime);
			serializer.endDocument();
			String msg = String.format(
					"XML test result file generated at %s. Passed %d, "
							+ "Failed %d, Not Executed %d",
					mReportDir.getName(),
					mResults.countTests(CtsTestStatus.PASS),
					mResults.countTests(CtsTestStatus.FAIL),
					mResults.countTests(CtsTestStatus.NOT_EXECUTED));
			logResult(msg);
			logResult("Time: %s", TimeUtil.formatElapsedTime(elapsedTime));
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed to generate report data");
		} finally {
			StreamUtil.closeStream(stream);
		}
	}

	/**
	 * Output the results XML.
	 *
	 * @param serializer
	 *            the {@link KXmlSerializer} to use
	 * @param startTime
	 *            the user-friendly starting time of the test invocation
	 * @param endTime
	 *            the user-friendly ending time of the test invocation
	 * @throws IOException
	 */
	private void serializeResultsDoc(KXmlSerializer serializer,
			String startTime, String endTime) throws IOException {
		serializer.startTag(ns, RESULT_TAG);
		serializer.attribute(ns, PLAN_ATTR, mPlanName);
		serializer.attribute(ns, STARTTIME_ATTR, startTime);
		serializer.attribute(ns, "endtime", endTime);
		serializer.attribute(ns, LOGCAT_ATTR, mLogPath);
		serializer.attribute(ns, "version", CTS_RESULT_FILE_VERSION);
		serializer.attribute(ns, "suite", mSuiteName);

		mResults.serialize(serializer);
		// TODO: not sure why, but the serializer doesn't like this statement
		// serializer.endTag(ns, RESULT_TAG);
	}

	private File getResultFile(File reportDir) {
		return new File(reportDir, TEST_RESULT_FILE_NAME);
	}

	/**
	 * Creates the output stream to use for test results. Exposed for mocking.
	 */
	OutputStream createOutputResultStream(File reportFile) throws IOException {
		logResult("Created xml report file at file://%s",
				reportFile.getAbsolutePath());
		return new FileOutputStream(reportFile);
	}

	/**
	 * Copy the xml formatting files stored in this jar to the results directory
	 *
	 * @param resultsDir
	 */
	private void copyFormattingFiles(File resultsDir) {
		for (String resultFileName : CTS_RESULT_RESOURCES) {
			InputStream configStream = getClass().getResourceAsStream(
					String.format("/report/%s", resultFileName));
			if (configStream != null) {
				File resultFile = new File(resultsDir, resultFileName);
				try {
					FileUtil.writeToFile(configStream, resultFile);
				} catch (IOException e) {
					Log.w(LOG_TAG, String.format("Failed to write %s to file",
							resultFileName));
				}
			} else {
				Log.w(LOG_TAG, String.format("Failed to load %s from jar",
						resultFileName));
			}
		}
	}

	/**
	 * Zip the contents of the given results directory.
	 *
	 * @param resultsDir
	 */
	private void zipResults(File resultsDir) {
		try {
			// create a file in parent directory, with same name as resultsDir
			File zipResultFile = new File(resultsDir.getParent(),
					String.format("%s.zip", resultsDir.getName()));
			FileUtil.createZip(resultsDir, zipResultFile);
		} catch (IOException e) {
			Log.w(LOG_TAG,
					String.format("Failed to create zip for %s",
							resultsDir.getName()));
		}
	}

	/**
	 * Get a String version of the current time.
	 * <p/>
	 * Exposed so unit tests can mock.
	 */
	String getTimestamp() {
		return TimeUtil.getTimestamp();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunFailed(String errorMessage) {
		// ignore
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void testRunStopped(long elapsedTime) {
		// ignore
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invocationFailed(Throwable cause) {
		// ignore
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TestSummary getSummary() {
		return null;
	}

	public void addMonkeyEvent(MonkeyEvent event) {
		switch (event.getEventType()) {
		case 0:
			MonkeyKeyEvent key = (MonkeyKeyEvent) event;
			KeyTag keyTag = new KeyTag();
			keyTag.setValue(key.getKeyCode());

			mEventTag = keyTag;
			break;
		case 1:
			MonkeyTapEvent tap = (MonkeyTapEvent) event;
			TapTag tapTag = new TapTag();
			tapTag.setX(tap.getPoint().x);
			tapTag.setY(tap.getPoint().y);
			mEventTag = tapTag;
			break;
		case 2:
			MonkeyMotionEvent motion = (MonkeyMotionEvent) event;
			MotionTag motionTag = new MotionTag();
			motionTag.addTouchDown(motion.getDownPoint());
			for (Point point : motion.getMovePoints()) {
				motionTag.addTouchMove(point);
			}
			motionTag.addTouchUp(motion.getUpPoint());
			mEventTag = motionTag;
			break;
		case 3:
			MonkeyDragEvent drag = (MonkeyDragEvent) event;
			DragTag dragTag = new DragTag();
			dragTag.addTouchDown(drag.getDownPoint());
			dragTag.addTouchUp(drag.getUpPoint());
			mEventTag = dragTag;
		default:
			break;
		}
		if (mPngFile != null)
			mEventTag.setImage(mPngFile.getPath());
		mEventTag.setTime(getTimestamp());
		monkeyTag.addEvent(mEventTag);

	}

}
