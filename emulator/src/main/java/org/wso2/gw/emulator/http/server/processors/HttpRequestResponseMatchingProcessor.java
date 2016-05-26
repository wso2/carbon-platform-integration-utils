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

package org.wso2.gw.emulator.http.server.processors;

import org.wso2.gw.emulator.http.server.contexts.HttpRequestContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerProcessorContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerRequestBuilderContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerResponseBuilderContext;

import java.util.Map;

/**
 * HttpRequestResponseMatchingProcessor
 */
public class HttpRequestResponseMatchingProcessor extends AbstractServerProcessor {

    @Override
    public void process(HttpServerProcessorContext processorContext) {
        Map<HttpServerRequestBuilderContext, HttpServerResponseBuilderContext> requestResponseCorrelation
                = processorContext
                .getServerInformationContext().getRequestResponseCorrelation();

        HttpRequestContext httpRequestContext = processorContext.getHttpRequestContext();
        for (Map.Entry<HttpServerRequestBuilderContext, HttpServerResponseBuilderContext> entry
                : requestResponseCorrelation
                .entrySet()) {
            if (entry.getKey().isMatch(httpRequestContext)) {
                processorContext.setSelectedResponseContext(entry.getValue());
                break;
            }
        }
    }
}
