/*
 * *
 *  * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.gw.emulator.example.http;

import io.netty.handler.codec.http.HttpMethod;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.Emulator;
import org.wso2.gw.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientResponseProcessorContext;

/**
 * Simple emulator client
 */
public class SimpleEmulatorClient {

    private static final Logger log = Logger.getLogger(SimpleEmulatorClient.class);

    public static void main(String args[]) {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user1").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        log.info("Response content =" + response.getReceivedResponseContext().getResponseBody());
        log.info("Response status code =" + response.getReceivedResponseContext().getResponseStatus());
    }

}
