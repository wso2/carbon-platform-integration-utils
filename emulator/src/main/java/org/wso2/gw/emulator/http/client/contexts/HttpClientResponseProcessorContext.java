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

import io.netty.handler.codec.http.HttpResponse;

/**
 * Get and set Response of server
 */
public class HttpClientResponseProcessorContext extends HttpClientProcessorContext {
    private HttpClientResponseBuilderContext expectedResponse;
    private HttpResponseContext receivedResponseContext;
    private HttpResponse receivedResponse;

    public HttpClientResponseBuilderContext getExpectedResponseContext() {
        return expectedResponse;
    }

    public void setExpectedResponse(HttpClientResponseBuilderContext expectedResponse) {
        this.expectedResponse = expectedResponse;
    }

    public HttpResponseContext getReceivedResponseContext() {
        return receivedResponseContext;
    }

    public void setReceivedResponseContext(HttpResponseContext receivedResponseContext) {
        this.receivedResponseContext = receivedResponseContext;
    }

    public HttpResponse getReceivedResponse() {
        return receivedResponse;
    }

    public void setReceivedResponse(HttpResponse receivedResponse) {
        this.receivedResponse = receivedResponse;
    }

}
