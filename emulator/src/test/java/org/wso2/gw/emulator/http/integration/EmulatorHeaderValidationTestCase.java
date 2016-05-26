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
import org.wso2.gw.emulator.http.params.HeaderOperation;
import org.wso2.gw.emulator.http.server.contexts.HttpServerOperationBuilderContext;

import static org.wso2.gw.emulator.http.server.contexts.HttpServerConfigBuilderContext.configure;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerRequestBuilderContext.request;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerResponseBuilderContext.response;

/**
 * EmulatorHeaderValidationTestCase
 */
public class EmulatorHeaderValidationTestCase {
    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws InterruptedException {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testSingleHeaderField1() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user1").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User1",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().size(), 2,
                "Expected response headers count is wrong");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header1").get(0), "value1",
                "Expected response header not found");
    }

    @Test
    public void testSingleHeaderField2() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user2").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User2",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().size(), 2,
                "Expected response headers count is wrong");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header2").get(0), "value2",
                "Expected response header not found");
    }

    @Test
    public void testMultipleHeaderFields() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user3").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User3",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().size(), 3,
                "Expected response headers count is wrong");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header3").get(0), "value3",
                "Expected response header not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header4").get(0), "value4",
                "Expected response header not found");
    }

    @Test
    public void testSingleRequestHeaderFields() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user4").withMethod(HttpMethod.GET)
                        .withHeader("Header-req-1", "value-req1"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User4",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().size(), 2,
                "Expected response headers count is wrong");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header5").get(0), "value5",
                "Expected response header not found");
    }

    @Test
    public void testMultipleRequestHeaderFieldsWithANDOperation() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user5").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header-req2", "value-req2"), new Header("Header-req3", "value-req3"),
                                new Header("Header-req4", "value-req4")))

                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User5",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().size(), 2,
                "Expected response headers count is wrong");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header6").get(0), "value6",
                "Expected response header not found");
    }

    @Test
    public void testMultipleRequestHeaderFieldsWithOROperation() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user6").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header-req5", "value-req5"), new Header("Header-req6", "value-req6")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User7",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().size(), 2,
                "Expected response headers count is wrong");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header7").get(0), "value7",
                "Expected response header not found");
    }

    @Test
    public void testMultipleRequestHeaderFieldsWithOROperationErrorScenario() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user6").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header-req15", "value-req15")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.NOT_FOUND,
                "Expected response status code not found");
    }

    @Test
    public void testMultipleRequestHeaderFieldsWithANDOperationErrorScenario() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user5").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header-req2", "value-req2"), new Header("Header-req3", "value-req3"),
                                new Header("Header-req14", "value-req14"), new Header("Header-req5", "value-req5")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();
        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.NOT_FOUND,
                "Expected response status code not found");
    }

    @AfterClass
    public void cleanup() {
        this.emulator.stop();
    }

    private HttpServerOperationBuilderContext startHttpEmulator() {
        return Emulator.getHttpEmulator().server()
                .given(configure().host("127.0.0.1").port(6065).context("/users").withEnableWireLog())

                .when(request().withMethod(HttpMethod.GET).withPath("user1/"))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1"))
                .when(request().withMethod(HttpMethod.GET).withPath("user2/"))
                .then(response().withBody("User2").withStatusCode(HttpResponseStatus.OK)
                        .withHeaders(new Header("Header2", "value2")))
                .when(request().withMethod(HttpMethod.GET).withPath("user3/"))
                .then(response().withBody("User3").withStatusCode(HttpResponseStatus.OK)
                        .withHeaders(new Header("Header3", "value3"), new Header("Header4", "value4")))
                .when(request().withMethod(HttpMethod.GET).withPath("user4/").withHeader("Header-req-1", "value-req1"))
                .then(response().withBody("User4").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header5", "value5")).when(request().withMethod(HttpMethod.GET).withPath("user5/")
                        .withHeaders(HeaderOperation.AND, new Header("Header-req2", "value-req2"),
                                new Header("Header-req3", "value-req3"), new Header("Header-req4", "value-req4")))
                .then(response().withBody("User5").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header6", "value6")).when(request().withMethod(HttpMethod.GET).withPath("user6/")
                        .withHeaders(HeaderOperation.OR, new Header("Header-req3", "value-req3"),
                                new Header("Header-req5", "value-req5"), new Header("Header-req6", "value-req6")))
                .then(response().withBody("User7").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header7", "value7")).operation().start();
    }
}

