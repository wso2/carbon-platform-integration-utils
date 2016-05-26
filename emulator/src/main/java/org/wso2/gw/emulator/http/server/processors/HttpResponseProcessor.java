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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import org.wso2.gw.emulator.http.params.Cookie;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.http.server.contexts.HttpRequestContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerProcessorContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerResponseBuilderContext;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

/**
 * HttpResponseProcessor
 */
public class HttpResponseProcessor extends AbstractServerProcessor {

    @Override
    public void process(HttpServerProcessorContext processorContext) {
        if (processorContext.getSelectedResponseContext() == null) {
            populate404NotFoundResponse(processorContext);
        } else {
            populateResponse(processorContext);
        }
    }

    private void populateResponse(HttpServerProcessorContext processorContext) {
        HttpRequestContext requestContext = processorContext.getHttpRequestContext();
        HttpServerResponseBuilderContext responseContext = processorContext.getSelectedResponseContext();
        boolean keepAlive = requestContext.isKeepAlive();
        Pattern pattern = processorContext.getServerInformationContext().getUtilityContext().getPattern();
        HttpResponseStatus httpResponseStatus = responseContext.getStatusCode();

        ByteBuf buf = null;
        if (patternMatcher(requestContext, responseContext, pattern) != null) {
            buf = Unpooled.copiedBuffer(patternMatcher(requestContext, responseContext, pattern), CharsetUtil.UTF_8);
        } else {
            buf = Unpooled.buffer(0);
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus, buf);
        populateHttpHeaders(response, responseContext);
        populateCookies(response, responseContext);
        if (!response.headers().contains(HttpHeaders.Names.CONTENT_LENGTH)) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        }

        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        processorContext.setFinalResponse(response);
    }

    private String patternMatcher(HttpRequestContext requestContext, HttpServerResponseBuilderContext responseContext,
            Pattern pathRegex) {
        String responseBody = responseContext.getBody();
        String requestBody = requestContext.getRequestBody();
        Matcher matcher = null;
        if (responseBody != null) {
            matcher = pathRegex.matcher(responseBody);
        } else {
            return responseBody;
        }

        while (matcher.find()) {
            String tag = "";
            tag = matcher.group(0);

            String word = tag.substring(2, tag.length() - 1);

            if (word.startsWith("body")) {
                responseBody = pathRegex.matcher(responseBody).replaceFirst(requestBody);

            } else if (word.startsWith("header")) {

                String[] split = word.split(Pattern.quote("."));
                String s = split[1];

                List<String> strings = requestContext.getHeaderParameters().get(s);
                responseBody = pathRegex.matcher(responseBody).replaceFirst(strings.get(0));

            } else if (word.startsWith("query")) {
                String[] split = word.split(Pattern.quote("."));
                String s = split[1];

                List<String> strings = requestContext.getQueryParameters().get(s);
                responseBody = pathRegex.matcher(responseBody).replaceFirst(strings.get(0));
            }
        }
        return responseBody;
    }

    private void populate404NotFoundResponse(HttpServerProcessorContext processorContext) {
        HttpRequestContext requestContext = processorContext.getHttpRequestContext();
        boolean keepAlive = requestContext.isKeepAlive();
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, NOT_FOUND);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        processorContext.setFinalResponse(response);
    }

    private void populateHttpHeaders(FullHttpResponse response, HttpServerResponseBuilderContext responseContext) {
        if (responseContext.getHeaders() != null) {
            for (Header header : responseContext.getHeaders()) {
                response.headers().add(header.getName(), header.getValue());
            }
        }
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
    }

    private void populateCookies(FullHttpResponse response, HttpServerResponseBuilderContext responseContext) {
        if (responseContext.getCookies() != null) {
            for (Cookie cookie : responseContext.getCookies()) {
                response.headers().add(HttpHeaders.Names.SET_COOKIE,
                        ServerCookieEncoder.encode(cookie.getName(), cookie.getValue()));
            }
        }
    }
}
