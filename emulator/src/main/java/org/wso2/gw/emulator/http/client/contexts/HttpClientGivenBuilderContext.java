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

import org.wso2.gw.emulator.dsl.contexts.AbstractGivenBuilderContext;

import java.util.ArrayList;

/**
 * Http Client Given Builder Context
 */
public class HttpClientGivenBuilderContext extends AbstractGivenBuilderContext<HttpClientConfigBuilderContext> {
    private final ArrayList<HttpClientWhenBuilderContext> whenBuilderContextList;
    private HttpClientInformationContext httpClientInformationContext;
    private HttpClientConfigBuilderContext configurationContext;
    private HttpClientWhenBuilderContext whenBuilderContext;

    public HttpClientGivenBuilderContext(HttpClientInformationContext httpClientInformationContext) {
        whenBuilderContextList = new ArrayList<HttpClientWhenBuilderContext>();
        this.httpClientInformationContext = httpClientInformationContext;
    }

    @Override
    public HttpClientWhenBuilderContext given(HttpClientConfigBuilderContext configurationContext) {
        this.configurationContext = configurationContext;
        httpClientInformationContext.setClientConfigBuilderContext(this.configurationContext);
        whenBuilderContext = new HttpClientWhenBuilderContext(whenBuilderContextList, httpClientInformationContext);
        return whenBuilderContext;
    }
}
