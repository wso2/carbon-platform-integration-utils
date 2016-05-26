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
 * BodyValidationTestCase
 */
public class BodyValidationTestCase {

    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws InterruptedException {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testWithoutBodyWithPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user20").withMethod(HttpMethod.POST))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
    }

    @Test
    public void testBodyWithGETMethod() {
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
    public void testBodyWithPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user2").withMethod(HttpMethod.POST))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User2",
                "Expected response content not found");
    }

    @Test
    public void testBodyWithPUTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user3").withMethod(HttpMethod.PUT))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User3",
                "Expected response content not found");
    }

    @Test
    public void testBodyWithHeadersPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user4").withMethod(HttpMethod.POST)
                        .withHeader("Header4", "value4"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User4",
                "Expected response content not found");
    }

    @Test
    public void testBodyWithHeadersBodyPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user5").withMethod(HttpMethod.POST)
                        .withHeader("Header5", "value5").withBody("User5"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User5",
                "Expected response content not found");
    }

    @Test
    public void testBodyWithHeadersBodyQueryPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user6").withMethod(HttpMethod.POST)
                        .withHeader("Header6", "value6").withBody("User6").withQueryParameter("Query6", "value6"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User6",
                "Expected response content not found");

    }

    @Test
    public void testBodyWithHeadersBackInResponse() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user7").withMethod(HttpMethod.POST)
                        .withHeader("Header7", "value7"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "value7",
                "Expected response content not found");
    }

    @Test
    public void testBodyWithRequestBodyHeadersBackInResponse() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user8").withMethod(HttpMethod.POST)
                        .withBody("User8").withHeader("Header8", "value8"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User8 value8",
                "Expected response content not found");
    }

    @Test
    public void testBodyWithRequestBodyHeadersQueryParametersBackInResponse() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user9").withMethod(HttpMethod.POST)
                        .withBody("User9").withHeader("Header9", "headerValue9")
                        .withQueryParameter("Query9", "queryValue9"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User9 headerValue9 queryValue9",
                "Expected response content not found");
    }

    @AfterClass
    public void cleanup() {
        this.emulator.stop();
    }

    private HttpServerOperationBuilderContext startHttpEmulator() {
        return Emulator.getHttpEmulator().server().given(configure().host("127.0.0.1").port(6065).context("/users").
        withEnableWireLog())

                .when(request().withMethod(HttpMethod.POST).withPath("/user20"))
                .then(response().withStatusCode(HttpResponseStatus.OK).withHeaders(new Header("Header2", "value2")))

                .when(request().withMethod(HttpMethod.GET).withPath("/user1"))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user2"))
                .then(response().withBody("User2").withStatusCode(HttpResponseStatus.OK)
                        .withHeaders(new Header("Header2", "value2")))

                .when(request().withMethod(HttpMethod.PUT).withPath("/user3"))
                .then(response().withBody("User3").withStatusCode(HttpResponseStatus.OK)
                        .withHeaders(new Header("Header2", "value2")))

                .when(request().withMethod(HttpMethod.POST).withPath("/user4").withHeader("Header4", "value4"))
                .then(response().withBody("User4").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res4", "value4"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user5").withBody("User5")
                        .withHeader("Header5", "value5"))
                .then(response().withBody("User5").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res5", "value5"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user6").withBody("User6")
                        .withHeader("Header6", "value6").
                                withQueryParameter("Query6", "value6"))
                .then(response().withBody("User6").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res6", "value6"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user7").withHeader("Header7", "value7"))
                .then(response().withBody("@{header.Header7}").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res7", "value7"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user8").withBody("User8")
                        .withHeader("Header8", "value8"))
                .then(response().withBody("@{body} @{header.Header8}").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res8", "value8"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user9").withBody("User9")
                        .withHeader("Header9", "headerValue9").withQueryParameter("Query9", "queryValue9"))
                .then(response().withBody("@{body} @{header.Header9} @{query.Query9}")
                        .withStatusCode(HttpResponseStatus.OK).withHeader("Header-res9", "value9"))

                .operation().start();
    }
}
