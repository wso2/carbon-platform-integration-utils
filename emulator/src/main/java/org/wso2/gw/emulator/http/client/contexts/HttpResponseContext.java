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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * response information of the server
 */
public class HttpResponseContext {
    private Map<String, List<String>> headerParameters;
    private Map<String, List<String>> cookieParameters;
    private StringBuffer responseBody;
    private HttpResponseStatus responseStatus;

    public void addHeaderParameter(String key, String value) {
        if (headerParameters == null) {
            this.headerParameters = new HashMap<String, List<String>>();
        }

        List<String> headerValues = this.headerParameters.get(key);
        if (headerValues == null) {
            headerValues = new ArrayList<String>();
        }
        headerValues.add(value);
        this.headerParameters.put(key, headerValues);
    }

    public void addCookieParameter(String key, String value) {
        if (cookieParameters == null) {
            this.cookieParameters = new HashMap<String, List<String>>();
        }
        List<String> cookieValues = this.cookieParameters.get(key);
        if (cookieValues == null) {
            cookieValues = new ArrayList<String>();
        }
        cookieValues.add(value);
        this.cookieParameters.put(key, cookieValues);
    }

    public Map<String, List<String>> getHeaderParameters() {
        return headerParameters;
    }

    public Map<String, List<String>> getCookieParameters() {
        return cookieParameters;
    }

    public void appendResponseContent(Object content) {
        if (responseBody == null) {
            this.responseBody = new StringBuffer();
        }
        this.responseBody.append(content);
    }

    public String getResponseBody() {
        if (responseBody == null) {
            return null;
        }
        return responseBody.toString();
    }

    public HttpResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(HttpResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
}
