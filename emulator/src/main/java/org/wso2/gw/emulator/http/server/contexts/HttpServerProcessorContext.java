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

package org.wso2.gw.emulator.http.server.contexts;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

/**
 * HttpServerProcessorContext
 */
public class HttpServerProcessorContext {
    private HttpServerInformationContext serverInformationContext;
    private HttpRequestContext httpRequestContext;
    private HttpRequest httpRequest;
    private HttpContent httpContent;
    private HttpServerResponseBuilderContext selectedResponseContext;
    private FullHttpResponse finalResponse;
    private HttpServerRequestBuilderContext requestBuilderContext;

    public HttpServerRequestBuilderContext getRequestBuilderContext() {
        return requestBuilderContext;
    }

    public void setRequestBuilderContext(HttpServerRequestBuilderContext requestBuilderContext) {
        this.requestBuilderContext = requestBuilderContext;
    }

    public HttpServerInformationContext getServerInformationContext() {
        return serverInformationContext;
    }

    public void setServerInformationContext(HttpServerInformationContext serverInformationContext) {
        this.serverInformationContext = serverInformationContext;
    }

    public HttpRequestContext getHttpRequestContext() {
        return httpRequestContext;
    }

    public void setHttpRequestContext(HttpRequestContext httpRequestContext) {
        this.httpRequestContext = httpRequestContext;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpContent getHttpContent() {
        return httpContent;
    }

    public void setHttpContent(HttpContent httpContent) {
        this.httpContent = httpContent;
    }

    public HttpServerResponseBuilderContext getSelectedResponseContext() {
        return selectedResponseContext;
    }

    public void setSelectedResponseContext(HttpServerResponseBuilderContext selectedResponseContext) {
        this.selectedResponseContext = selectedResponseContext;
    }

    public FullHttpResponse getFinalResponse() {
        return finalResponse;
    }

    public void setFinalResponse(FullHttpResponse finalResponse) {
        this.finalResponse = finalResponse;
    }
}
