package com.android.cts.tradefed.result;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pl.vgtworld.imagedraw.processing.ImageProcessing;

import com.android.cts.tradefed.result.monkey.DragTag;
import com.android.cts.tradefed.result.monkey.EventTag;
import com.android.cts.tradefed.result.monkey.KeyTag;
import com.android.cts.tradefed.result.monkey.MonkeyTestTag;
import com.android.cts.tradefed.result.monkey.MotionTag;
import com.android.cts.tradefed.result.monkey.TapTag;
import com.android.cts.tradefed.result.monkey.TouchTag;
import com.android.ddmlib.Log;
import com.android.tradefed.util.FileUtil;
import com.android.tradefed.util.xml.AbstractXmlParser.ParseException;

public class MonkeyReporter extends AbstractXmlPullParser {
	private static final String LOG_TAG = "MonkeyReporter";

	private static final String[] MONKEY_RESULT_RESOURCES = { "index.xsl",
			"bootstrap.css", "result.xsl","trace.xsl" };
	private static final String REPORT_DIR = "report";
	private List<EventTag> items = new LinkedList<EventTag>();
	// 存放目录
	private File mSaveFile = null;
	private File mXmlFile = null;
	// 报告目录位于存放目录下
	private File reporterDir = null;

	public MonkeyReporter(File xmlFile, File saveFile) {
		this.mXmlFile = xmlFile;
		this.mSaveFile = saveFile;
		if (mXmlFile == null || !mXmlFile.exists())
			return;
		// 初始化
		init();
	}

	// 如果只传xml文件路径，那么保存的目录就在xml同级目录
	public MonkeyReporter(File xmlFile) {
		this.mXmlFile = xmlFile;
		if (mXmlFile == null || !mXmlFile.exists())
			return;
		this.mSaveFile = mXmlFile.getParentFile();
		init();
	}

	private void init() {
		// 当前目录下创建reporter目录
		reporterDir = mSaveFile;
		reporterDir.mkdir();
		if (!reporterDir.exists())
			return;
		// 将报告所需要的资源文件copy到reporter目录下
		copyFormattingFiles(reporterDir);

		parserXmlFile();

	}

	// 创建报告
	public void createReporter() {
		String index_xsl = new File(reporterDir, MONKEY_RESULT_RESOURCES[0])
				.getAbsolutePath();
		String result_xsl = new File(reporterDir, MONKEY_RESULT_RESOURCES[2])
				.getAbsolutePath();
		String trace_xsl = new File(reporterDir,MONKEY_RESULT_RESOURCES[3]).getAbsolutePath();
		String xml = mXmlFile.getAbsolutePath();
		String indeHtml = new File(reporterDir, "index.html").getAbsolutePath();
		String resultHtml = new File(reporterDir, "result.html")
				.getAbsolutePath();
		String traceHtml = new File(reporterDir,"trace.html").getAbsolutePath();

		transferToHtml(result_xsl, xml, resultHtml);
		transferToHtml(index_xsl, xml, indeHtml);
		transferToHtml(trace_xsl, xml, traceHtml);

	}

	private void parserXmlFile() {
		MonkeyTestTag testTag = new MonkeyTestTag();
		try {
			testTag.parse(new BufferedReader(new FileReader(mXmlFile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		items = testTag.getEvents();
	}

	private void copyFormattingFiles(File resultsDir) {
		for (String resultFileName : MONKEY_RESULT_RESOURCES) {
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

	// 绘图
	public void drawImage() throws IOException {
		ImageProcessing image = new ImageProcessing();
		for (EventTag eventTag : items) {
			File imageFile = new File(eventTag.getImage());
			image.open(imageFile);
			if (eventTag instanceof TapTag) {
				drawCircle(image, eventTag);
			} else if (eventTag instanceof DragTag) {
				drawArrowHead(image, eventTag);

			} else if (eventTag instanceof KeyTag) {
				drawText(image, eventTag);
			}
			image.save(imageFile);

		}
	}

	private void drawArrowHead(ImageProcessing image, EventTag eventTag) {
		DragTag motionTag = (DragTag) eventTag;
		List<TouchTag> touches = motionTag.getTouches();
		Point start = new Point();
		Point end = new Point();
		for (TouchTag touch : touches) {
			String direct = touch.getDirection();
			if (MotionTag.DIRECTION_DOWN.equals(direct)) {
				start.x = touch.getX();
				start.y = touch.getY();
			} else if (MotionTag.DIRECTION_MOVE.equals(direct)) {

			} else if (MotionTag.DIRECTION_UP.equals(direct)) {
				end.x = touch.getX();
				end.y = touch.getY();
			}
		}
		image.drawArrowHead(Color.RED, start, end, 5f);
		image.drawText(String.format("(%d,%d)~(%d,%d)", start.x, start.y,
				end.x, end.y), Color.RED, new Font("SansSerif", Font.BOLD, 50),
				0, 50);
	}

	private void drawText(ImageProcessing image, EventTag eventTag) {
		KeyTag keyTag = (KeyTag) eventTag;
		String keyValue = keyTag.getValue();
		image.drawText(keyValue, Color.RED,
				new Font("SansSerif", Font.BOLD, 40), 0, 50);
	}

	private void drawCircle(ImageProcessing image, EventTag eventTag) {
		TapTag tapt = (TapTag) eventTag;
		image.drawCircle(Color.RED, new Point((int) tapt.getX() - 50,
				(int) tapt.getY() - 50), 100, 5f);
		image.drawText(
				String.format("(%d,%d)", (int) tapt.getX(), (int) tapt.getY()),
				Color.RED, new Font("SansSerif", Font.BOLD, 50), 0, 50);
	}

	@Override
	public void parse(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		// TODO Auto-generated method stub

	}

	public void transferToHtml(String xsl, String xml, String html) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(html);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(
					xsl));
			transformer.transform(new StreamSource(xml), new StreamResult(out));
		} catch (FileNotFoundException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
