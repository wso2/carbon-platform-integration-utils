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

import java.io.File;

import static org.wso2.gw.emulator.http.server.contexts.HttpServerConfigBuilderContext.configure;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerRequestBuilderContext.request;
import static org.wso2.gw.emulator.http.server.contexts.HttpServerResponseBuilderContext.response;

/**
 * ReadFromFilesTestCase
 */
public class ReadFromFilesTestCase {

    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws InterruptedException {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testClientANDServerRequestFromTestFile() {

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user1").withMethod(HttpMethod.POST)
                        .withBody("User1")
                        .withBody(getTestFiles("testFiles" + File.separator + "test1clientRequest.txt")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User1",
                "Expected response content not found");
    }

    @Test
    public void testServerResponseFromFile() {

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user2").withMethod(HttpMethod.POST)
                        .withBody("User2")).then(HttpClientResponseBuilderContext.response().assertionIgnore())
                .operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User2",
                "Expected response content not found");
    }

    @Test
    public void testServerRequestResponseFromTestFile() {

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user3").withMethod(HttpMethod.POST)
                        .withBody("User3")).then(HttpClientResponseBuilderContext.response().assertionIgnore())
                .operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User3",
                "Expected response content not found");
    }

    @Test
    public void testServerRequestResponseANDClientRequestFromTestFile() {

        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user4").withMethod(HttpMethod.POST)
                        .withBody(getTestFiles("testFiles" + File.separator + "test4clientRequest.txt")))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User4",
                "Expected response content not found");

    }

    private HttpServerOperationBuilderContext startHttpEmulator() {
        return Emulator.getHttpEmulator().server().given(configure().host("127.0.0.1").port(6065).context("/users"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user1").withBody("User1")
                        .withBody(getTestFiles("testFiles" + File.separator + "test1serverRequest.txt")))
                .then(response().withBody("User1").withStatusCode(HttpResponseStatus.OK)
                        .withHeader("Header1", "value1"))

                .when(request().withMethod(HttpMethod.POST).withPath("/user2").withBody("User2"))
                .then(response().withBody(getTestFiles("testFiles" + File.separator + "test2serverResponse.txt"))
                        .withStatusCode(HttpResponseStatus.OK).withHeaders(new Header("Header2", "value2")))

                .when(request().withMethod(HttpMethod.POST).withPath("/user3")
                        .withBody(getTestFiles("testFiles" + File.separator + "test3serverRequest.txt")))
                .then(response().withBody("User3")
                        .withBody(getTestFiles("testFiles" + File.separator + "test3serverResponse.txt"))
                        .withStatusCode(HttpResponseStatus.OK).withHeaders(new Header("Header3", "value3")))

                .when(request().withMethod(HttpMethod.POST).withPath("/user4")
                        .withBody(getTestFiles("testFiles" + File.separator + "test4serverRequest.txt")))
                .then(response().withBody("User4")
                        .withBody(getTestFiles("testFiles" + File.separator + "test4serverResponse.txt"))
                        .withStatusCode(HttpResponseStatus.OK).withHeaders(new Header("Header4", "value4")))

                .operation().start();
    }

    private File getTestFiles(String fileName) {
        String changeLater = "/home/senduran/projects/bsenduran/product-gw/integration/emulator/src/main/resources/";
        return new File(changeLater + fileName);
    }

    @AfterClass
    public void cleanup() {
        this.emulator.stop();
    }
}
