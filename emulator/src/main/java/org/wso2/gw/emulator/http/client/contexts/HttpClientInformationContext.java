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

import org.wso2.gw.emulator.http.client.HttpClientInitializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Client request response information context
 */
public class HttpClientInformationContext {
    private HttpClientConfigBuilderContext clientConfigBuilderContext;
    private Map<HttpClientRequestBuilderContext, HttpClientResponseBuilderContext> correlation;
    private HttpClientInitializer clientInitializer;
    private HttpClientRequestBuilderContext requestContext;
    private HttpClientResponseBuilderContext expectedResponse;
    private HttpClientResponseProcessorContext receivedProcessContext;

    public HttpClientConfigBuilderContext getClientConfigBuilderContext() {
        return clientConfigBuilderContext;
    }

    public void setClientConfigBuilderContext(HttpClientConfigBuilderContext clientConfigBuilderContext) {
        this.clientConfigBuilderContext = clientConfigBuilderContext;
    }

    public Map<HttpClientRequestBuilderContext, HttpClientResponseBuilderContext> getRequestResponseCorrelation() {
        return correlation;
    }

    public void addCorrelation(HttpClientRequestBuilderContext httpClientRequestBuilderContext,
            HttpClientResponseBuilderContext httpClientResponseBuilderContext) {
        if (correlation == null) {
            this.correlation = new HashMap<HttpClientRequestBuilderContext, HttpClientResponseBuilderContext>();
        }
        correlation.put(httpClientRequestBuilderContext, httpClientResponseBuilderContext);
    }

    public HttpClientInitializer getClientInitializer() {
        return clientInitializer;
    }

    public void setClientInitializer(HttpClientInitializer clientInitializer) {
        this.clientInitializer = clientInitializer;
    }

    public HttpClientRequestBuilderContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(HttpClientRequestBuilderContext requestContext) {
        this.requestContext = requestContext;
    }

    public HttpClientResponseBuilderContext getExpectedResponse() {
        return expectedResponse;
    }

    public void setExpectedResponse(HttpClientResponseBuilderContext expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public HttpClientResponseProcessorContext getReceivedResponseProcessContext() {
        return receivedProcessContext;
    }

    public void setReceivedResponseProcessContext(HttpClientResponseProcessorContext receivedProcessContext) {
        this.receivedProcessContext = receivedProcessContext;
    }
}
