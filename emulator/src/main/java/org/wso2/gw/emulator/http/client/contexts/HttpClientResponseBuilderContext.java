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

import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.contexts.AbstractResponseBuilderContext;
import org.wso2.gw.emulator.http.params.Cookie;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.http.params.HeaderOperation;
import org.wso2.gw.emulator.util.FileReaderUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Expected response details
 */
public class HttpClientResponseBuilderContext extends AbstractResponseBuilderContext {
    private static final Logger log = Logger.getLogger(HttpClientResponseBuilderContext.class);
    private static HttpClientResponseBuilderContext clientResponseBuilderContext;
    private HttpResponseStatus statusCode;
    private List<Header> headers;
    private List<Cookie> cookies;
    private String body;
    private boolean isIgnored;
    private HeaderOperation operations;

    private static HttpClientResponseBuilderContext getInstance() {
        clientResponseBuilderContext = new HttpClientResponseBuilderContext();
        return clientResponseBuilderContext;
    }

    public static HttpClientResponseBuilderContext response() {
        return getInstance();
    }

    public HttpClientResponseBuilderContext withStatusCode(HttpResponseStatus statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpClientResponseBuilderContext withHeader(String name, String value) {
        if (headers == null) {
            this.headers = new ArrayList<Header>();
        }
        headers.add(new Header(name, value));
        return this;
    }

    public HttpClientResponseBuilderContext withHeaders(HeaderOperation operation, Header... headers) {
        this.operations = operation;
        if (this.headers == null) {
            this.headers = new ArrayList<Header>();
        }

        if (headers != null && headers.length > 0) {
            this.headers.addAll(Arrays.asList(headers));
        }
        return this;
    }

    public HttpClientResponseBuilderContext withCookie(String name, String value) {
        if (cookies == null) {
            this.cookies = new ArrayList<Cookie>();
        }
        cookies.add(new Cookie(name, value));
        return this;
    }

    public HttpClientResponseBuilderContext withCookies(Cookie... cookies) {
        if (this.cookies == null) {
            this.cookies = new ArrayList<Cookie>();
        }

        if (cookies != null && cookies.length > 0) {
            this.cookies.addAll(Arrays.asList(cookies));
        }
        return this;
    }

    public HttpClientResponseBuilderContext withBody(String body) {
        this.body = body;
        return this;
    }

    public HttpClientResponseBuilderContext withBody(File filePath) {
        try {
            this.body = FileReaderUtil.getFileBody(filePath);
        } catch (IOException e) {
            log.error(e);
        }
        return this;
    }

    public boolean getAssertionStatus() {
        return isIgnored;
    }

    public HttpClientResponseBuilderContext assertionIgnore() {
        this.isIgnored = true;
        return this;
    }

    public HttpResponseStatus getStatusCode() {
        return statusCode;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public String getBody() {
        return body;
    }

    public HeaderOperation getOperations() {
        return operations;
    }
}
