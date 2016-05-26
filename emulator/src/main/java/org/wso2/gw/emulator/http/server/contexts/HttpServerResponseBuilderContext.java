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

import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.contexts.AbstractResponseBuilderContext;
import org.wso2.gw.emulator.http.params.Cookie;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.util.FileReaderUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HttpServerResponseBuilderContext
 */
public class HttpServerResponseBuilderContext extends AbstractResponseBuilderContext {
    private static final Logger log = Logger.getLogger(HttpServerResponseBuilderContext.class);
    private static HttpServerResponseBuilderContext serverResponse;
    private HttpResponseStatus statusCode = HttpResponseStatus.OK;
    private List<Cookie> cookies;
    private List<Header> headers;
    private String body;

    private static HttpServerResponseBuilderContext getInstance() {
        serverResponse = new HttpServerResponseBuilderContext();
        return serverResponse;
    }

    public static HttpServerResponseBuilderContext response() {
        return getInstance();
    }

    public HttpServerResponseBuilderContext withStatusCode(HttpResponseStatus statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpServerResponseBuilderContext withCookie(String name, String value) {
        if (cookies == null) {
            this.cookies = new ArrayList<Cookie>();
        }
        cookies.add(new Cookie(name, value));
        return this;
    }

    public HttpServerResponseBuilderContext withCookies(Cookie... cookies) {
        if (this.cookies == null) {
            this.cookies = new ArrayList<Cookie>();
        }
        if (cookies != null && cookies.length > 0) {
            this.cookies.addAll(Arrays.asList(cookies));
        }
        return this;
    }

    public HttpServerResponseBuilderContext withHeader(String name, String value) {
        if (headers == null) {
            this.headers = new ArrayList<Header>();
        }
        headers.add(new Header(name, value));
        return this;
    }

    public HttpServerResponseBuilderContext withHeaders(Header... headers) {
        if (this.headers == null) {
            this.headers = new ArrayList<Header>();
        }

        if (headers != null && headers.length > 0) {
            this.headers.addAll(Arrays.asList(headers));
        }
        return this;
    }

    public HttpServerResponseBuilderContext withCustomProcessor(String customRequestProcessor) {
        return this;
    }

    public HttpServerResponseBuilderContext withBody(String body) {
        this.body = body;
        return this;
    }

    public HttpServerResponseBuilderContext withBody(File filePath) {
        try {
            this.body = FileReaderUtil.getFileBody(filePath);
        } catch (IOException e) {
            log.error("Exception occurred while reading file", e);
        }
        return this;
    }

    public HttpServerResponseBuilderContext withEmptyBody() {
        return this;
    }

    public HttpResponseStatus getStatusCode() {
        return statusCode;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
