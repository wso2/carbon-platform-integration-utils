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

package org.wso2.gw.emulator.http;

import org.wso2.gw.emulator.dsl.AbstractProtocolEmulator;
import org.wso2.gw.emulator.http.client.HttpClientInitializer;
import org.wso2.gw.emulator.http.client.contexts.HttpClientGivenBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientInformationContext;
import org.wso2.gw.emulator.http.server.HttpServerInitializer;
import org.wso2.gw.emulator.http.server.contexts.HttpServerGivenBuilderContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerInformationContext;

/**
 * HTTPProtocolEmulator
 */
public class HTTPProtocolEmulator extends AbstractProtocolEmulator {

    @Override
    public HttpServerGivenBuilderContext server() {
        HttpServerInformationContext serverInformationContext = new HttpServerInformationContext();
        HttpServerInitializer serverInitializer = new HttpServerInitializer(serverInformationContext);
        serverInformationContext.setHttpServerInitializer(serverInitializer);
        HttpServerGivenBuilderContext serverGivenBuilderContext = new HttpServerGivenBuilderContext(
                serverInformationContext);
        return serverGivenBuilderContext;
    }

    @Override
    public HttpClientGivenBuilderContext client() {
        HttpClientInformationContext clientInformationContext = new HttpClientInformationContext();
        HttpClientInitializer clientInitializer = new HttpClientInitializer(clientInformationContext);
        clientInformationContext.setClientInitializer(clientInitializer);
        HttpClientGivenBuilderContext clientGivenBuilderContext = new HttpClientGivenBuilderContext(
                clientInformationContext);
        return clientGivenBuilderContext;
    }
}
