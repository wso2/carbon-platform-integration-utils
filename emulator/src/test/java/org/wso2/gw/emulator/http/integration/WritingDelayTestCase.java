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

package org.wso2.gw.emulator.http.integration;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.gw.emulator.dsl.Emulator;
import org.wso2.gw.emulator.http.client.contexts.HttpClientConfigBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientRequestBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientResponseBuilderContext;
import org.wso2.gw.emulator.http.client.contexts.HttpClientResponseProcessorContext;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.http.server.contexts.HttpServerOperationBuilderContext;

import static org.wso2.gw.emulator.http.server.contexts.HttpServerConfigBuilderContext.configure;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerRequestBuilderContext.request;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerResponseBuilderContext.response;

/**
 * WritingDelayTestCase
 */
public class WritingDelayTestCase {

    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws InterruptedException {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testServerWithReadingDelayGET() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user1").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User1",
                "Expected response content not found");
    }

    @Test
    public void testServerWithReadingDelayPUT() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user2").withMethod(HttpMethod.PUT))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User2",
                "Expected response content not found");
    }

    @Test
    public void testServerWithReadingDelayPOST() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user3").withMethod(HttpMethod.POST))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User3",
                "Expected response content not found");
    }

    @AfterClass
    public void cleanup() {
        this.emulator.stop();
    }

    private HttpServerOperationBuilderContext startHttpEmulator() {
        return Emulator.getHttpEmulator().server()
                .given(configure().host("127.0.0.1").port(6065).context("/users").withWritingDelay(1000))

                .when(request().withMethod(HttpMethod.GET).withPath("/user1"))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1"))

                .when(request().withMethod(HttpMethod.PUT).withPath("/user2"))
                .then(response().withBody("User2").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header2", "value2"))

                .when(request().withMethod(HttpMethod.POST).withPath("user3"))
                .then(response().withBody("User3").withStatusCode(HttpResponseStatus.OK)
                        .withHeaders(new Header("Header3", "value3"))).operation().start();
    }
}
