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

package org.wso2.gw.emulator.http.integration.custom.processor.validation;

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
 * CustomProcessorValidationTestCase
 */
public class CustomProcessorValidationTestCase {

    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws Exception {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testServerRequestCustomProcessor() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("10.100.4.37").port(8280))
                .when(HttpClientRequestBuilderContext.request().withPath("/Users")
                        .withHeader("Content-Type1", "application/xml").withBody("<df>sddfff</df>")
                        .withMethod(HttpMethod.PUT)).then(HttpClientResponseBuilderContext.response().assertionIgnore())
                .operation().send();

        /*Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "ChangedBody",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header2").get(0),
                "ChangedHeaderValue2", "Expected response header not found");*/
    }

    @Test
    public void testServerRequestResponseCustomProcessorsErrorScenario() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))

                .when(HttpClientRequestBuilderContext.request().withPath("/users/user5").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
    }

    @AfterClass
    public void cleanup() {
        this.emulator.stop();
    }

    private HttpServerOperationBuilderContext startHttpEmulator() {
        return Emulator.getHttpEmulator().server().given(configure().host("127.0.0.1").port(6065).context("/users")
                .withCustomRequestProcessor(new CustomRequestProcessor()).withEnableWireLog())

                .when(request().withMethod(HttpMethod.GET).withPath("/user1"))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1"))

                .when(request().withMethod(HttpMethod.GET).withPath("/user2"))
                .then(response().withBody("ChangedBody").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header2", "ChangedHeaderValue2"))

                .when(request().withMethod(HttpMethod.GET).withPath("user4"))
                .then(response().withBody("User4").withStatusCode(HttpResponseStatus.OK)
                        .withHeaders(new Header("Header4", "value4"))).operation().start();
    }
}
