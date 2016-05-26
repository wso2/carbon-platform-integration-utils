/*
 * *
 *  * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  * WSO2 Inc. licenses this file to you under the Apache License,
 *  * Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.wso2.gw.emulator.util;

import io.netty.channel.ChannelDuplexHandler;
import org.apache.log4j.Logger;

/**
 * wire log
 **/
public class WireLogHandler extends ChannelDuplexHandler {

    private static boolean wireLog = false;

    private static final Logger log = Logger.getLogger(WireLogHandler.class);

    public static void requestWireLog(Object msg) {
        String[] array = msg.toString().split("\\r?\\n");
        for (int i = 0; i < array.length; i++) {
            log.info("wire << " + array[i]);
        }
        wireLog = true;
    }

    public static void responseWireLog(Object msg) {

        if (wireLog) {
            String[] array = msg.toString().split("\\r?\\n");
            for (int i = 0; i < array.length; i++) {
                log.info("wire >> " + array[i]);
            }
        }
    }

    public static void logRequestBody(String body) {
        if (wireLog) {
            log.info("wire << " + body);
        }
    }

    public static void logResponseBody(String body) {
        if (wireLog) {
            log.info("wire >> " + body);
        }

    }
}
