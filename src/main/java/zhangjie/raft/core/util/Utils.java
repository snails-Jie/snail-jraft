/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zhangjie.raft.core.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Helper methods for jraft.
 *
 * @author boyan (boyan@alibaba-inc.com)
 *
 * 2018-Apr-07 10:12:35 AM
 */
public class Utils {

    private static final Logger LOG                                 = LoggerFactory.getLogger(Utils.class);

    /**
     * ANY IP address 0.0.0.0
     */
    public static final String IP_ANY = "0.0.0.0";

    private static final Pattern GROUP_ID_PATTER = Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\-_]*$");

    /**
     * Gets the current monotonic time in milliseconds.
     */
    public static long monotonicMs() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
    }

    /**
     * Returns the current time in milliseconds, it's not monotonic,
     * would be forwarded/backward by clock synchronous.
     */
    public static long nowMs() {
        return System.currentTimeMillis();
    }

    /**
     * Gets the current monotonic time in microseconds.
     */
    public static long monotonicUs() {
        return TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
    }

    /**
     * Get string bytes in UTF-8 charset.
     */
    public static byte[] getBytes(final String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public static String getString(final byte[] bs, final int off, final int len) {
        return new String(bs, off, len, StandardCharsets.UTF_8);
    }

    public static void verifyGroupId(final String groupId) {
        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("Blank groupId");
        }
        if (!GROUP_ID_PATTER.matcher(groupId).matches()) {
            throw new IllegalArgumentException(
                    "Invalid group id, it should be started with character 'a'-'z' or 'A'-'Z',"
                            + "and followed with numbers, english alphabet, '-' or '_'. ");
        }
    }
}
