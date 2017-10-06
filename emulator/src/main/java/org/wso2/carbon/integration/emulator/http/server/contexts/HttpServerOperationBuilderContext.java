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

package org.wso2.carbon.integration.emulator.http.server.contexts;

import org.apache.log4j.Logger;
import org.wso2.carbon.integration.emulator.dsl.contexts.AbstractServerOperationBuilderContext;
import org.wso2.carbon.integration.emulator.util.ValidationUtil;

/**
 * HttpServerOperationBuilderContext
 */
public class HttpServerOperationBuilderContext extends AbstractServerOperationBuilderContext {
    private static final Logger log = Logger.getLogger(HttpServerOperationBuilderContext.class);
    private HttpServerInformationContext httpServerInformationContext;

    public HttpServerOperationBuilderContext(HttpServerInformationContext httpServerInformationContext) {
        this.httpServerInformationContext = httpServerInformationContext;
    }

    @Override
    public HttpServerOperationBuilderContext start() {
        ValidationUtil.validateMandatoryParameters(httpServerInformationContext.getServerConfigBuilderContext());

        try {
            httpServerInformationContext.getHttpServerInitializer().start();
        } catch (Exception e) {
            log.error("Exception occurred while starting the Emulator server", e);
        }
        return this;
    }

    @Override
    public HttpServerOperationBuilderContext stop() {
        try {
            httpServerInformationContext.getHttpServerInitializer().shutdown();
        } catch (Exception e) {
            log.error("Exception occurred while stopping the Emulator server", e);
        }
        return this;
    }
}
