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

import org.wso2.gw.emulator.dsl.contexts.AbstractThenBuilderContext;

import java.util.List;

/**
 * Http client then builder context
 */
public class HttpClientThenBuilderContext extends AbstractThenBuilderContext<HttpClientResponseBuilderContext> {
    private final HttpClientRequestBuilderContext requestContext;
    private final HttpClientInformationContext httpClientInformationContext;
    private final List<HttpClientWhenBuilderContext> whenBuilderContextList;
    private HttpClientWhenBuilderContext whenBuilderContext;

    public HttpClientThenBuilderContext(List<HttpClientWhenBuilderContext> whenBuilderContextList,
            HttpClientRequestBuilderContext requestContext, HttpClientInformationContext httpClientInformationContext) {
        this.requestContext = requestContext;
        this.httpClientInformationContext = httpClientInformationContext;
        this.whenBuilderContextList = whenBuilderContextList;
    }

    @Override
    public HttpClientWhenBuilderContext then(HttpClientResponseBuilderContext responseContext) {
        whenBuilderContext = new HttpClientWhenBuilderContext(whenBuilderContextList, httpClientInformationContext);
        this.httpClientInformationContext.addCorrelation(requestContext, responseContext);
        return whenBuilderContext;
    }
}
