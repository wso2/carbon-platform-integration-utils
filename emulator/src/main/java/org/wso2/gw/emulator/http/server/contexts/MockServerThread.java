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
package org.wso2.gw.emulator.http.server.contexts;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.log4j.Logger;

import java.util.concurrent.DelayQueue;

/**
 * MockServerThread
 */
public class MockServerThread extends Thread {
    private static final Logger log = Logger.getLogger(MockServerThread.class);

    private final DelayQueue<DelayedElement> queue = new DelayQueue<DelayedElement>();

    public void run() {
        DelayedElement elem;

        while (true) {
            try {
                elem = queue.take();
                beginResponse(elem.getContext(), elem.getProcessorContext());
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    public void delayEvent(ChannelHandlerContext ctx, HttpServerProcessorContext processorContext, int delay, int id) {
        DelayedElement delayedElement = new DelayedElement(ctx, processorContext, System.currentTimeMillis(), delay);
        queue.add(delayedElement);
    }

    private void beginResponse(ChannelHandlerContext ctx, HttpServerProcessorContext context) {
        writeResponse(ctx, context);
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpServerProcessorContext context) {
        // Decide whether to close the connection or not.
        HttpRequest request = context.getHttpRequest();

        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = context.getFinalResponse();
        if (keepAlive) {
            ctx.write(response);
        } else {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
        ctx.channel().flush();
    }

}
