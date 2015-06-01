/*
 * Copyright (C) 2013 The Android Open Source Project
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

import com.android.ddmlib.testrunner.TestIdentifier;
import com.android.tradefed.device.ITestDevice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static utility class for handling Pts Results.
 */
public class PtsReportUtil {
    private static final String PTS_RESULT_KEY = "PTS_RESULT";

    /**
     * Utility method to extract PTS result from test metrics
     * @param testMetrics
     * @return result or null if not found
     */
    public static String getPtsResultFromMetrics(Map<String, String> testMetrics) {
        for (Map.Entry<String, String> entry: testMetrics.entrySet()) {
            if (PTS_RESULT_KEY.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
