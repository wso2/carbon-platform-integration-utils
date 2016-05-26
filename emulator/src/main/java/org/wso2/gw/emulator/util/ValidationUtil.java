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

import org.apache.log4j.Logger;
import org.wso2.gw.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerConfigBuilderContext;

/**
 * Validation util
 */
public class ValidationUtil {
    private static final Logger log = Logger.getLogger(ValidationUtil.class);

    public static void validateMandatoryParameters(HttpClientConfigBuilderContext clientConfigBuilderContext) {
        if (!isHostAvailable(clientConfigBuilderContext.getHost())) {
            log.error("Host is not given");
        }

        if (!isPortAvailable(clientConfigBuilderContext.getPort())) {
            log.error("Host is not given");
        }
    }

    public static void validateMandatoryParameters(HttpServerConfigBuilderContext serverConfigBuilderContext) {
        if (!isHostAvailable(serverConfigBuilderContext.getHost())) {
            log.error("Host is not given");
        }

        if (!isPortAvailable(serverConfigBuilderContext.getPort())) {
            log.error("Host is not given");
        }
    }

    private static boolean isHostAvailable(String host) {
        if (host == null) {
            return false;
        }
        return true;
    }

    private static boolean isPortAvailable(int port) {
        if (port == 0) {
            return false;
        }
        return true;
    }
}
