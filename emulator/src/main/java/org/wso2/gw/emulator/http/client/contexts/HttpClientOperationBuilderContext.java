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

package org.wso2.gw.emulator.http.client.contexts;

import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.contexts.AbstractClientOperationBuilderContext;

/**
 * Request sending operation of client
 */
public class HttpClientOperationBuilderContext extends AbstractClientOperationBuilderContext {
    private static final Logger log = Logger.getLogger(HttpClientOperationBuilderContext.class);
    private HttpClientInformationContext httpClientInformationContext;

    public HttpClientOperationBuilderContext(HttpClientInformationContext httpClientInformationContext) {
        this.httpClientInformationContext = httpClientInformationContext;
    }

    @Override
    public HttpClientResponseProcessorContext send() {  //recommanded for a one request only
        try {
            this.httpClientInformationContext.getClientInitializer().initialize();
        } catch (Exception e) {
            log.error("Exception occurred while sending message" + e);
        }
        return httpClientInformationContext.getReceivedResponseProcessContext();
    }
}
