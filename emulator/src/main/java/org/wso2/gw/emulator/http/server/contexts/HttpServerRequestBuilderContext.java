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

import io.netty.handler.codec.http.HttpMethod;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.contexts.AbstractRequestBuilderContext;
import org.wso2.gw.emulator.http.params.Cookie;
import org.wso2.gw.emulator.http.params.CookieOperation;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.http.params.HeaderOperation;
import org.wso2.gw.emulator.http.params.QueryParameter;
import org.wso2.gw.emulator.http.params.QueryParameterOperation;
import org.wso2.gw.emulator.util.FileReaderUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * HttpServerRequestBuilderContext
 */
public class HttpServerRequestBuilderContext extends AbstractRequestBuilderContext {
    private static final Logger log = Logger.getLogger(HttpServerRequestBuilderContext.class);
    private static HttpServerRequestBuilderContext serverRequest;
    private HttpMethod method;
    private String path;
    private String body;
    private String context;
    private Header header;
    private Pattern pathRegex;
    private QueryParameter queryParameter;
    private Cookie cookie;
    private List<Header> headers;
    private List<QueryParameter> queryParameters;
    private List<Cookie> cookies;
    private HeaderOperation operation;
    private CookieOperation cookieOperation;
    private QueryParameterOperation queryOperation;

    private static HttpServerRequestBuilderContext getInstance() {
        serverRequest = new HttpServerRequestBuilderContext();
        return serverRequest;
    }

    public static HttpServerRequestBuilderContext request() {
        return getInstance();
    }

    public HttpServerRequestBuilderContext withMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpServerRequestBuilderContext withPath(String path) {
        this.path = path;
        return this;
    }

    public HttpServerRequestBuilderContext withBody(String body) {
        this.body = body;
        return this;
    }

    public HttpServerRequestBuilderContext withBody(File filePath) {
        try {
            this.body = FileReaderUtil.getFileBody(filePath);
        } catch (IOException e) {
            log.error(e);
        }
        return this;
    }

    public HttpServerRequestBuilderContext withHeader(String name, String value) {
        this.header = new Header(name, value);
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        headers.add(header);
        return this;
    }

    public HeaderOperation getOperation() {
        return operation;
    }

    public CookieOperation getCookieOperation() {
        return cookieOperation;
    }

    public HttpServerRequestBuilderContext withHeaders(HeaderOperation operation, Header... headers) {
        this.operation = operation;
        this.headers = Arrays.asList(headers);
        return this;
    }

    public HttpServerRequestBuilderContext withQueryParameter(String name, String value) {
        this.queryParameter = new QueryParameter(name, value);
        if (queryParameters == null) {
            queryParameters = new ArrayList<QueryParameter>();
        }
        queryParameters.add(queryParameter);
        return this;
    }

    public QueryParameterOperation getQueryOperation() {
        return queryOperation;
    }

    public HttpServerRequestBuilderContext withQueryParameters(QueryParameterOperation queryOperation,
            QueryParameter... queryParameters) {
        this.queryOperation = queryOperation;
        this.queryParameters = Arrays.asList(queryParameters);
        return this;
    }

    public HttpServerRequestBuilderContext withCookie(String name, String value) {
        if (cookie == null) {
            this.cookies = new ArrayList<Cookie>();
        }
        this.cookies.add(new Cookie(name, value));
        return this;
    }

    public HttpServerRequestBuilderContext withCookies(CookieOperation cookieOperation, Cookie... cookies) {
        this.cookieOperation = cookieOperation;
        this.cookies = Arrays.asList(cookies);
        return this;
    }

    public HttpServerRequestBuilderContext withCustomProcessor(String customRequestProcessor) {
        return this;
    }

    public boolean isMatch(HttpRequestContext requestContext) {
        if (isContextMatch(requestContext) && isHttpMethodMatch(requestContext) && isQueryParameterMatch(requestContext)
                && isRequestContentMatch(requestContext) &&
                isHeadersMatch(requestContext)) {
            return true;
        }
        return false;
    }

    public void buildPathRegex(String context) {
        this.context = context;
        String regex = buildRegex(context, path);
        this.pathRegex = Pattern.compile(regex);
    }

    private boolean isContextMatch(HttpRequestContext requestContext) {
        this.context = extractContext(requestContext.getUri());
        return pathRegex.matcher(context).find();
    }

    private boolean isHttpMethodMatch(HttpRequestContext requestContext) {
        if (method == null) {
            return true;
        }

        if (method.equals(requestContext.getHttpMethod())) {
            return true;
        }
        return false;
    }

    private boolean isRequestContentMatch(HttpRequestContext requestContext) {
        if (body == null || body.isEmpty()) {
            return true;
        }

        if (body.equalsIgnoreCase(requestContext.getRequestBody())) {
            return true;
        }
        return false;
    }

    private boolean isHeadersMatch(HttpRequestContext requestContext) {
        if (headers == null) {
            return true;
        }
        HeaderOperation operation = getOperation();
        Map<String, List<String>> headerParameters = requestContext.getHeaderParameters();

        if (operation == HeaderOperation.OR) {
            if ((headerParameters == null || headerParameters.isEmpty()) && !headers.isEmpty()) {
                return false;
            }
            for (Header header : headers) {
                List<String> headerValues = headerParameters.get(header.getName());
                String value = header.getValue();
                if (headerValues != null && headerValues.contains(value)) {
                    return true;
                }
            }
        } else {
            for (Header header : headers) {
                if (headerParameters.get(header.getName()) == null) {
                    return false;
                }

                if (!headerParameters.get(header.getName()).contains(header.getValue())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isQueryParameterMatch(HttpRequestContext requestContext) {

        if (queryParameters == null) {
            return true;
        }

        Map<String, List<String>> queryParametersMap = requestContext.getQueryParameters();
        boolean x = false;
        if (queryOperation == QueryParameterOperation.OR) {
            for (QueryParameter query : queryParameters) {
                List<String> queryParameterValues = queryParametersMap.get(query.getName());
                String value = query.getValue();
                if (queryParameterValues == null) {
                    //continue;
                } else if (queryParameterValues.contains(value)) {
                    x = true;
                    break;
                }
            }
            if (x == true) {
                return true;
            } else {
                return false;
            }
        } else {
            List<String> queryParameterValues = null;
            String value = null;

            for (QueryParameter query : queryParameters) {

                if (queryParametersMap.get(query.getName()) != null) {
                    queryParameterValues = queryParametersMap.get(query.getName());
                    value = query.getValue();
                } else {
                    return false;
                }

                if (!queryParameterValues.contains(value)) {
                    return false;
                }
            }
            return true;
        }
    }

    private String buildRegex(String context, String path) {
        String fullPath = "";

        if ((context == null || context.isEmpty()) && (path == null || path.isEmpty())) {
            return ".*";
        }

        if ("*".equals(context) && "*".equals(path)) {
            return ".*";
        }

        if (context != null && !context.isEmpty() && "*".equals(path)) {
            fullPath = context;

            if (!fullPath.startsWith("/")) {
                fullPath = "/" + fullPath;
            }

            fullPath = fullPath + ".*";
            return fullPath;
        }

        if (context != null && !context.isEmpty()) {
            fullPath = context;

            if (!fullPath.startsWith("/")) {
                fullPath = "/" + fullPath;
            }

            if (!fullPath.endsWith("/")) {
                fullPath = fullPath + "/";
            }
        } else {
            fullPath = ".*";
        }

        if (path != null && !path.isEmpty()) {
            if (fullPath.endsWith("/") && path.startsWith("/")) {
                fullPath = fullPath + path.substring(1);

            } else if (fullPath.endsWith("/") && !path.startsWith("/")) {
                fullPath = fullPath + path;

            } else if (!fullPath.endsWith("/") && path.startsWith("/")) {
                fullPath = fullPath + path;

            } else {
                fullPath = fullPath + "/" + path;
            }
        } else {
            fullPath = fullPath + ".*";
        }

        if (fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        return "^" + fullPath + "$";
    }

    private String extractContext(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }
        if (!uri.contains("?")) {
            if (path == null || path.isEmpty()) {
                uri = uri + "/";
            }
            return uri;
        }
        uri = uri.split("\\?")[0];

        if (path == null || path.isEmpty()) {
            uri = uri + "/";
        }
        return uri;
    }
}
