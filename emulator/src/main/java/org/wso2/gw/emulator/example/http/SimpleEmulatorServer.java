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
import io.netty.handler.codec.http.HttpResponseStatus;
import org.wso2.gw.emulator.dsl.Emulator;

import static org.wso2.gw.emulator.http.server.contexts.HttpServerConfigBuilderContext.configure;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerRequestBuilderContext.request;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerResponseBuilderContext.response;

/**
 * Simple emulator server
 */
public class SimpleEmulatorServer {

    public static void main(String args[]) {

        Emulator.getHttpEmulator().server().given(configure().host("127.0.0.1").port(6065).context("/users"))
                .when(request().withMethod(HttpMethod.GET).withPath("user1/"))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1")).when(request().withMethod(HttpMethod.GET).withPath("user2/"))
                .then(response().withStatusCode(HttpResponseStatus.NOT_FOUND)).operation().start();
    }
}
