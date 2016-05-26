/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.gw.emulator.http.integration.context.path.validation;

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
 * EmulatorWithContextValidationTestCase
 */
public class EmulatorWithContextValidationTestCase {

    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws InterruptedException {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testWithServerContextGETMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User1",
                "Expected response content not found");
    }

    @Test
    public void testWithServerContextPUTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users").withMethod(HttpMethod.PUT))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User2",
                "Expected response content not found");
    }

    //@Test
    public void testWithServerContextFieldPathStarGETMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header5", "value5"), new Header("Header6", "value6")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User5",
                "Expected response content not found");
    }

    //@Test
    public void testWithServerContextFieldPathWithHeaderGETMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users/user8").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header8", "value8"), new Header("Header9", "value9")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User8",
                "Expected response content not found");
    }

    //@Test
    public void testWithServerContextFieldPathWithHeaderQueryGETMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users/user10").withMethod(HttpMethod.GET)
                        .withHeaders(new Header("Header10", "value10"), new Header("Header11", "value11"))
                        .withQueryParameter("Query10", "value10"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User10",
                "Expected response content not found");
    }

    @Test
    public void testWithServerContextFieldPathWithHeaderBodyPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users/user14").withMethod(HttpMethod.POST)
                        .withBody("User14")
                        .withHeaders(new Header("Header12", "value12"), new Header("Header13", "value13"),
                                new Header("Header14", "value14")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User14",
                "Expected response content not found");
    }

    @Test
    public void testWithServerContextFieldPathWithHeaderQueryBodyPOSTMethod() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users/user15").withMethod(HttpMethod.POST)
                        .withBody("User15")
                        .withHeaders(new Header("Header15", "value15"), new Header("Header16", "value16"))
                        .withQueryParameter("Query16", "value16"))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User15",
                "Expected response content not found");
    }

    @AfterClass
    public void cleanup() {
        this.emulator.stop();
        System.gc();
    }

    private HttpServerOperationBuilderContext startHttpEmulator() {
        return Emulator.getHttpEmulator().server().given(configure().host("127.0.0.1").port(6065).context("/users"))

                .when(request().withMethod(HttpMethod.GET))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1"))

                .when(request().withMethod(HttpMethod.PUT))
                .then(response().withBody("User2").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header2", "value2"))

                .when(request().withMethod(HttpMethod.GET).withPath("*")
                        .withHeaders(HeaderOperation.AND, new Header("Header5", "value5"),
                                new Header("Header6", "value6")))
                .then(response().withBody("User5").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res-5", "value5"))

                .when(request().withMethod(HttpMethod.POST).withPath("*").withBody("User4")
                        .withHeader("Header4", "value4"))
                .then(response().withBody("User4").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header4", "value4"))

                .when(request().withMethod(HttpMethod.GET).withPath("/user8")
                        .withHeaders(HeaderOperation.AND, new Header("Header8", "value8"),
                                new Header("Header9", "value9")))
                .then(response().withBody("User8").withHeader("Header-res8", "value8")
                        .withStatusCode(HttpResponseStatus.OK))

                .when(request().withMethod(HttpMethod.GET).withPath("/user10")
                        .withHeaders(HeaderOperation.AND, new Header("Header10", "value10"),
                                new Header("Header11", "value11")).withQueryParameter("Query10", "value10"))
                .then(response().withBody("User10").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header10", "value10"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user14").withBody("User14")
                        .withHeaders(HeaderOperation.AND, new Header("Header12", "value12"),
                                new Header("Header13", "value13"), new Header("Header14", "value14")))
                .then(response().withBody("User14").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res14", "value14"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user15").withBody("User15")
                        .withHeaders(HeaderOperation.OR, new Header("Header15", "value15"),
                                new Header("Header16", "value16")).withQueryParameter("Query16", "value16"))
                .then(response().withBody("User15").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header-res-15", "value15"))

                .operation().start();
    }
}

