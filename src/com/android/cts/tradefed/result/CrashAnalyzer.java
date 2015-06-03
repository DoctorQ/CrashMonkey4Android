package com.android.cts.tradefed.result;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class CrashAnalyzer {
	public File getmCrashFile() {
		return mCrashFile;
	}

	public void setmCrashFile(File mCrashFile) {
		this.mCrashFile = mCrashFile;
	}

	private static final String ANR = "ANR";
	private static final String JAVACRASH = "JAVACRASH";
	private static final String CRASH_FILE = "crash.txt";
	private static final String HEAD_REGEX = "^[0-1][0-9]-[0-2][0-9]\\s[0-2][0-9]:[0-6][0-9]:[0-5][0-9].[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+E\\s";
	private static final String ANR_REGEX = HEAD_REGEX
			+ "ActivityManager:\\sANR\\sin.*";
	private static final String CRASH_REGEX = HEAD_REGEX + "AndroidRuntime:.*";
	private static final String COMMON_REGEX = "^[0-1][0-9]-[0-2][0-9]\\s[0-2][0-9]:[0-6][0-9]:[0-5][0-9].[\\d]+\\s+[\\d]+\\s+[\\d]+\\s+[VDIWE]\\s.*";
	private File mLogFile = null;
	private File mCrashFile = null;
	private boolean isCrashMessage = false;
	private int crashCount = 0;
	private int anrCount = 0;

	// private static Map<String, String> mCrash = new HashMap<String,
	// String>();
	// static {
	// mCrash.put(ANR, "ANR");
	// mCrash.put(JAVACRASH, "java.lang.NullPointerException");
	// }

	public CrashAnalyzer(File file, File crashFile) {
		mLogFile = file;
		mCrashFile = crashFile;
	}

	public CrashAnalyzer(File logFile) {
		mLogFile = logFile;
		if (mCrashFile == null)
			mCrashFile = new File(mLogFile.getParent(), CRASH_FILE);

	}
	
	public void parserLogcat() {
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter wr = null;

		try {
			fr = new FileReader(mLogFile);
			br = new BufferedReader(fr);
			wr = new FileWriter(mCrashFile);
			String line = null;
			boolean continueWrite = false;
			while ((line = br.readLine()) != null) {

				// 先判断有没有crash
				if (Pattern.matches(CRASH_REGEX, line)) {
					if (Pattern.matches(HEAD_REGEX
							+ "AndroidRuntime:\\s+Process.*", line)) {
						String str = getHead("Crash " + (crashCount++)
								+ " Message");
						wr.write(str);
					}
					wr.write(line + "\n");
					continueWrite = true;
					continue;
				}

				// 判断有没有ANR
				if (Pattern.matches(ANR_REGEX, line)) {
					String str = getHead("ANR " + anrCount + " Message");
					wr.write(str);
					wr.write(line + "\n");
					continueWrite = true;
					continue;
				}

				// 输出有E标识的信息
				if (Pattern.matches(HEAD_REGEX + ".*", line)) {
					wr.write(line + "\n");
					// continueWrite = true;
					continue;
				}
				// 结束符
				if (Pattern.matches(COMMON_REGEX, line)) {
					if (!continueWrite)
						continue;
					String str = getHead("Done");

					wr.write(str + "\n");
					continueWrite = false;
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (wr != null)
					wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String getHead(String str) {
		String output = "===================================================================="
				+ str
				+ "====================================================================\n";

		return output;
	}

	public boolean hasCrash() {
		return anrCount > 0 || crashCount > 0;
	}
	
	public int getCrashCount(){
		return anrCount + crashCount;
	}

	public static void main(String[] args) {
		File file = new File(
				"/Users/wuxian/Documents/android-cts/repository/logs/2015.06.03_11.41.21/monkey_8465242561598443342.txt");
		CrashAnalyzer analyzer = new CrashAnalyzer(file);
		analyzer.parserLogcat();

	}

}
