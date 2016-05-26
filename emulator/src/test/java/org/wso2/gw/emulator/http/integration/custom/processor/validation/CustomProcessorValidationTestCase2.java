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
 * Created by dilshank on 4/11/16.
 */
public class CustomProcessorValidationTestCase2 {

    private HttpServerOperationBuilderContext emulator;

    @BeforeClass
    public void setEnvironment() throws Exception {
        this.emulator = startHttpEmulator();
        Thread.sleep(1000);
    }

    @Test
    public void testServerRequestCustomProcessor() {
        HttpClientResponseProcessorContext response = Emulator.getHttpEmulator().client()
                .given(HttpClientConfigBuilderContext.configure().host("127.0.0.1").port(6065))
                .when(HttpClientRequestBuilderContext.request().withPath("/users/user1").withMethod(HttpMethod.GET))
                .then(HttpClientResponseBuilderContext.response().assertionIgnore()).operation().send();

        Assert.assertEquals(response.getReceivedResponseContext().getResponseStatus(), HttpResponseStatus.OK,
                "Expected response status code not found");
        Assert.assertEquals(response.getReceivedResponseContext().getResponseBody(), "User1",
                "Expected response content not found");
        Assert.assertEquals(response.getReceivedResponseContext().getHeaderParameters().get("Header2").get(0),
                "value2", "Expected response header not found");
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
                .withCustomResponseProcessor(new CustomResponseProcessor())
                .withEnableWireLog())

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
