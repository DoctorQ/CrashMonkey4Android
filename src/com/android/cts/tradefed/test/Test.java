package com.android.cts.tradefed.test;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import pl.vgtworld.imagedraw.processing.ImageProcessing;

import com.android.cts.tradefed.result.MonkeyReporter;

import junit.framework.TestCase;

public class Test extends TestCase {

	public void test_MonkeyReporter() throws IOException {

		MonkeyReporter reporter = new MonkeyReporter(
				new File(
						"/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_18.23.30/testResult.xml"), null);
		reporter.drawImage();
		// reporter.createReporter();
		// reporter.transferToHtml("/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_10.56.35/report/index.xsl",
		// "/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_10.56.35/testResult.xml",
		// "/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_10.56.35/report/index.html");
		// reporter.transferToHtml("/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_10.56.35/report/result.xsl",
		// "/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_10.56.35/testResult.xml",
		// "/Users/wuxian/Downloads/android-cts/repository/results/2015.05.27_10.56.35/report/result.html");
	}

	public void test_MonkeyReporter1() {

		MonkeyReporter reporter = new MonkeyReporter(
				new File(
						"/Users/wuxian/Downloads/android-cts/repository/results/2015.05.26_11.14.34/testResult.xml"),
				new File("/Users/wuxian/Desktop/index"), null);
	}

	public void test_File() throws IOException {
		ImageProcessing image = new ImageProcessing();
		File imageFile = new File("/Users/wuxian/Desktop/123.png");
		image.open(imageFile);
		image.drawText("Helloworld1", Color.RED, new Font("SansSerif",
				Font.BOLD, 40), 0, 50);
		image.save(imageFile);
	}

	public void test_math() {
		int count = (int)Float.parseFloat("48.0");
		System.out.println(count);
	}

}
