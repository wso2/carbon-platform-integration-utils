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

package org.wso2.gw.emulator.http.client.processors;

import org.apache.log4j.Logger;
import org.wso2.gw.emulator.http.client.contexts.HttpClientResponseProcessorContext;
import org.wso2.gw.emulator.http.params.Header;
import org.wso2.gw.emulator.http.params.HeaderOperation;

import java.util.List;
import java.util.Map;

/**
 * Assert the received response with expected response
 */
public class HttpResponseAssertProcessor extends AbstractClientProcessor<HttpClientResponseProcessorContext> {

    private static final Logger log = Logger.getLogger(HttpResponseAssertProcessor.class);

    @Override
    public void process(HttpClientResponseProcessorContext processorContext) {

        if (!processorContext.getExpectedResponseContext().getAssertionStatus()) {
            assertResponseContent(processorContext);
            assertHeaderParameters(processorContext);
        }
    }

    private void assertResponseContent(HttpClientResponseProcessorContext processorContext) {

        if (processorContext.getExpectedResponseContext().getBody()
                .equalsIgnoreCase(processorContext.getReceivedResponseContext().getResponseBody())) {
            log.info("Equal content");
        } else {
            log.info("Wrong content");
        }
    }

    private void assertHeaderParameters(HttpClientResponseProcessorContext processorContext) {
        if (processorContext.getExpectedResponseContext().getHeaders() == null || processorContext
                .getExpectedResponseContext().getHeaders().isEmpty()) {
            return;
        }
        Map<String, List<String>> receivedHeaders = processorContext.getReceivedResponseContext().getHeaderParameters();
        HeaderOperation operation = processorContext.getClientInformationContext().getExpectedResponse()
                .getOperations();

        boolean value = false;
        if (operation == HeaderOperation.AND) {

            for (Map.Entry<String, List<String>> entry : processorContext.getReceivedResponseContext()
                    .getHeaderParameters().entrySet()) {
                entry.getKey();
            }

            for (Header header : processorContext.getExpectedResponseContext().getHeaders()) {
                List<String> receivedHeaderValues = receivedHeaders.get(header.getName());

                if (receivedHeaderValues == null || receivedHeaderValues.isEmpty() || !receivedHeaderValues
                        .contains(header.getValue())) {
                    log.info("Header not present");
                    break;
                } else {
                    log.info("Headers are present");
                }
            }
        } else if (operation == operation.OR) {
            for (Header header : processorContext.getExpectedResponseContext().getHeaders()) {
                List<String> receivedHeaderValues = receivedHeaders.get(header.getName());

                if (receivedHeaderValues == null || receivedHeaderValues.isEmpty() || !receivedHeaderValues
                        .contains(header.getValue())) {
                    ;
                } else {
                    value = true;
                }
            }
            if (value) {
                log.info("Headers are present");
            } else {
                log.info("Non of the Headers present");
            }
        } else {
            boolean match = false;
            for (Map.Entry<String, List<String>> header : receivedHeaders.entrySet()) {

                List<String> value1 = header.getValue();
                for (String val : value1) {
                    Header header1 = new Header(header.getKey(), val);
                    if (processorContext.getExpectedResponseContext().getHeaders().get(0).getValue()
                            .equals(header1.getValue()) && processorContext.getExpectedResponseContext().getHeaders()
                            .get(0).getName().equals(header1.getName())) {
                        match = true;
                    }
                }
            }
            if (match) {
                log.info("Header Present");
            } else {
                log.info("Header is not present");
            }
        }
    }
}
