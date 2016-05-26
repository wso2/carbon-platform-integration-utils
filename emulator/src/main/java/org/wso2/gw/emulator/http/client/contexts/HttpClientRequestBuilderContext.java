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

import io.netty.handler.codec.http.HttpMethod;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.contexts.AbstractRequestBuilderContext;
import org.wso2.gw.emulator.http.params.Cookie;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.http.params.QueryParameter;
import org.wso2.gw.emulator.util.FileReaderUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Client request information
 */
public class HttpClientRequestBuilderContext extends AbstractRequestBuilderContext {
    private static final Logger log = Logger.getLogger(HttpClientRequestBuilderContext.class);
    private static HttpClientRequestBuilderContext clientRequest;
    private HttpMethod method;
    private String path;
    private String body;
    private List<Header> headers;
    private List<QueryParameter> queryParameters;
    private List<Cookie> cookies;

    private static HttpClientRequestBuilderContext getInstance() {
        clientRequest = new HttpClientRequestBuilderContext();
        return clientRequest;
    }

    public static HttpClientRequestBuilderContext request() {
        return getInstance();
    }

    public HttpClientRequestBuilderContext withMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpClientRequestBuilderContext withPath(String path) {
        this.path = path;
        return this;
    }

    public HttpClientRequestBuilderContext withBody(String body) {
        this.body = body;
        return this;
    }

    public HttpClientRequestBuilderContext withBody(File filePath) {
        try {
            this.body = FileReaderUtil.getFileBody(filePath);
        } catch (IOException e) {
            log.error("Exception occurred while reading file" + e);
        }
        return this;
    }

    public HttpClientRequestBuilderContext withHeader(String name, String value) {
        Header header = new Header(name, value);
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        headers.add(header);
        return this;
    }

    public HttpClientRequestBuilderContext withHeaders(Header... headerList) {
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        for (Header header : headerList) {
            headers.add(header);
        }
        return this;
    }

    public HttpClientRequestBuilderContext withQueryParameter(String name, String value) {
        QueryParameter queryParameter = new QueryParameter(name, value);

        if (queryParameters == null) {
            queryParameters = new ArrayList<QueryParameter>();
        }
        queryParameters.add(queryParameter);
        return this;
    }

    public HttpClientRequestBuilderContext withQueryParameters(QueryParameter... queryParameterList) {
        if (queryParameters == null) {
            queryParameters = new ArrayList<QueryParameter>();
        }

        for (QueryParameter queryParameter : queryParameterList) {
            queryParameters.add(queryParameter);
        }
        return this;
    }

    public HttpClientRequestBuilderContext withCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);

        if (cookies == null) {
            cookies = new ArrayList<Cookie>();
        }
        cookies.add(cookie);
        return this;
    }

    public HttpClientRequestBuilderContext withCookies(Cookie... cookieList) {
        if (cookies == null) {
            cookies = new ArrayList<Cookie>();
        }
        for (Cookie cookie : cookieList) {
            cookies.add(cookie);
        }
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public List<QueryParameter> getQueryParameters() {
        return queryParameters;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

}
