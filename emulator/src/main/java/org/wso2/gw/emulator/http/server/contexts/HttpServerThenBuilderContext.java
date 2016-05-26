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

import org.wso2.gw.emulator.dsl.contexts.AbstractThenBuilderContext;

import java.util.List;

/**
 * HttpServerThenBuilderContext
 */
public class HttpServerThenBuilderContext extends AbstractThenBuilderContext<HttpServerResponseBuilderContext> {
    private final HttpServerRequestBuilderContext requestContext;
    private final HttpServerInformationContext httpServerInformationContext;
    private HttpServerWhenBuilderContext whenBuilderContext;
    private List<HttpServerWhenBuilderContext> whenBuilderContextList;

    public HttpServerThenBuilderContext(List<HttpServerWhenBuilderContext> whenBuilderContextList,
            HttpServerRequestBuilderContext requestContext, HttpServerInformationContext httpServerInformationContext) {
        this.requestContext = requestContext;
        this.httpServerInformationContext = httpServerInformationContext;
        this.whenBuilderContextList = whenBuilderContextList;
    }

    @Override
    public HttpServerWhenBuilderContext then(HttpServerResponseBuilderContext responseContext) {
        whenBuilderContext = new HttpServerWhenBuilderContext(whenBuilderContextList, httpServerInformationContext);
        this.httpServerInformationContext.addCorrelation(requestContext, responseContext);
        return whenBuilderContext;
    }
}
