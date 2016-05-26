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

package org.wso2.gw.emulator.http.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.http.server.contexts.HttpRequestContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerConfigBuilderContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerInformationContext;
import org.wso2.gw.emulator.http.server.contexts.HttpServerProcessorContext;
import org.wso2.gw.emulator.http.server.contexts.MockServerThread;
import org.wso2.gw.emulator.http.server.processors.HttpRequestCustomProcessor;
import org.wso2.gw.emulator.http.server.processors.HttpRequestInformationProcessor;
import org.wso2.gw.emulator.http.server.processors.HttpRequestResponseMatchingProcessor;
import org.wso2.gw.emulator.http.server.processors.HttpResponseCustomProcessor;
import org.wso2.gw.emulator.http.server.processors.HttpResponseProcessor;
import org.wso2.gw.emulator.util.WireLogHandler;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * HttpServerHandler
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = Logger.getLogger(HttpServerHandler.class);
    static Callable readingCallable = new Callable() {
        @Override
        public Object call() throws Exception {
            return "Reading";
        }
    };
    static Callable logicDelayCallable = new Callable() {
        @Override
        public Object call() throws Exception {
            return "Logic delay";
        }
    };
    private static ExecutorService executorService = Executors.newFixedThreadPool(160);
    private HttpRequestInformationProcessor httpRequestInformationProcessor;
    private HttpResponseProcessor httpResponseProcessor;
    private HttpServerInformationContext serverInformationContext;
    private HttpServerProcessorContext httpProcessorContext;
    private HttpRequestResponseMatchingProcessor requestResponseMatchingProcessor;
    private ScheduledExecutorService scheduledReadingExecutorService, scheduledLogicExecutorService;
    private int index, corePoolSize = 10;
    private int delay = 0;
    private MockServerThread[] handlers;

    public HttpServerHandler(HttpServerInformationContext serverInformationContext) {
        this.serverInformationContext = serverInformationContext;
        scheduledReadingExecutorService = Executors.newScheduledThreadPool(corePoolSize);
        scheduledLogicExecutorService = Executors.newScheduledThreadPool(corePoolSize);

    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpServerConfigBuilderContext serverConfigBuilderContext = serverInformationContext
                .getServerConfigBuilderContext();

        randomIndexGenerator(serverInformationContext.getServerConfigBuilderContext().isRandomConnectionClose());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        HttpServerConfigBuilderContext serverConfigBuilderContext = serverInformationContext
                .getServerConfigBuilderContext();

        if (serverConfigBuilderContext.isWireLog()) {
            WireLogHandler.requestWireLog(msg);
        }

        Boolean connectionDrop = serverConfigBuilderContext.getReadingConnectionDrop();
        if (connectionDrop != null && connectionDrop == true && ctx.channel().isOpen()) {
            ctx.close();
            log.info("101505--Connection dropped while Reading data from the channel");
        }

        int readTimeOut = serverConfigBuilderContext.getReadTimeOut();

        if (readTimeOut == 0) {
            readTimeOut = Integer.MAX_VALUE;
        }

        Thread thread = new Thread(() -> {
            if (msg instanceof HttpRequest) {
                randomConnectionClose(ctx, index, 0);
                httpRequestInformationProcessor = new HttpRequestInformationProcessor();
                httpResponseProcessor = new HttpResponseProcessor();
                httpProcessorContext = new HttpServerProcessorContext();
                httpProcessorContext.setHttpRequestContext(new HttpRequestContext());
                httpProcessorContext.setServerInformationContext(serverInformationContext);

                HttpRequest httpRequest = (HttpRequest) msg;
                httpProcessorContext.setHttpRequest(httpRequest);

                if (HttpHeaders.is100ContinueExpected(httpRequest)) {
                    send100Continue(ctx);
                }
                httpRequestInformationProcessor.process(httpProcessorContext);

            } else {
                readingDelay(serverConfigBuilderContext.getReadingDelay(), ctx);
                if (msg instanceof HttpContent) {
                    HttpContent httpContent = (HttpContent) msg;
                    if (httpContent.content().isReadable()) {
                        httpProcessorContext.setHttpContent(httpContent);
                        httpRequestInformationProcessor.process(httpProcessorContext);
                    }
                }

                if (msg instanceof LastHttpContent) {
                    HttpRequestCustomProcessor customProcessor = httpProcessorContext.getServerInformationContext()
                            .getServerConfigBuilderContext().getHttpRequestCustomProcessor();
                    if (customProcessor != null) {
                        httpProcessorContext = customProcessor.process(httpProcessorContext);
                    }
                    requestResponseMatchingProcessor = new HttpRequestResponseMatchingProcessor();
                    requestResponseMatchingProcessor.process(httpProcessorContext);
                    ctx.fireChannelReadComplete();
                }
            }
        });

        thread.start();
        long endTimeMillis = System.currentTimeMillis() + readTimeOut;
        while (thread.isAlive()) {
            if (System.currentTimeMillis() > endTimeMillis) {
                ctx.close();
                log.info("101504--Connection Timeout occurred while Reading data from the channel");
                break;
            }
        }

    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) throws IOException {

        HttpServerConfigBuilderContext serverConfigBuilderContext = serverInformationContext
                .getServerConfigBuilderContext();
        int connectionTimeOut = serverConfigBuilderContext.getConnectionTimeOut();
        if (connectionTimeOut != 0) {
            try {
                Thread.sleep(connectionTimeOut);
            } catch (InterruptedException e) {
                log.info(e.getMessage());
            }
        }

        String requestBody = httpProcessorContext.getHttpRequestContext().getRequestBody();
        if (requestBody != null) {
            WireLogHandler.logRequestBody(requestBody);
        }

        if (httpResponseProcessor != null) {
            randomConnectionClose(ctx, this.index, 1);
            businessLogicDelay(serverInformationContext.getServerConfigBuilderContext().getLogicDelay(), ctx);
            this.httpResponseProcessor.process(httpProcessorContext);

            int queues = httpProcessorContext.getServerInformationContext().getServerConfigBuilderContext().getQueues();
            delay = httpProcessorContext.getServerInformationContext().getServerConfigBuilderContext().getDelay();

            if (delay != 0 && queues > 0) {
                try {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            handlers[0].delayEvent(ctx, httpProcessorContext, delay, 0);
                        }
                    });

                } catch (Throwable throwable) {
                    log.error(throwable);
                }
            } else {
                FullHttpResponse response = httpProcessorContext.getFinalResponse();
                if (httpProcessorContext.getHttpRequestContext().isKeepAlive()) {
                    randomConnectionClose(ctx, this.index, 2);
                    ctx.write(response);
                } else {
                    randomConnectionClose(ctx, this.index, 2);
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                }
            }
            randomConnectionClose(ctx, this.index, 3);

            HttpResponseCustomProcessor customProcessor = httpProcessorContext.getServerInformationContext()
                    .getServerConfigBuilderContext().getCustomResponseProcessor();
            if (customProcessor != null) {
                httpProcessorContext = customProcessor.process(httpProcessorContext);
            }
            ctx.flush();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception occurred while processing the response", cause);
        ctx.close();
    }

    private void readingDelay(int delay, ChannelHandlerContext ctx) {
        if (delay != 0) {
            ScheduledFuture scheduledFuture = scheduledReadingExecutorService
                    .schedule(readingCallable, delay, TimeUnit.MILLISECONDS);
            try {
                scheduledFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e);
            }
            //scheduledReadingExecutorService.shutdown();
        }
    }

    private void businessLogicDelay(int delay, ChannelHandlerContext ctx) {
        if (delay != 0) {
            ScheduledFuture scheduledLogicFuture = scheduledLogicExecutorService
                    .schedule(logicDelayCallable, delay, TimeUnit.MILLISECONDS);
            try {
                scheduledLogicFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error(e);
            }
            //scheduledLogicExecutorService.shutdown();

        }
    }

    private void randomConnectionClose(ChannelHandlerContext ctx, int randomIndex, int pointIndex) {
        if (randomIndex == pointIndex) {
            log.debug("Random close");
            ctx.close();
        }
    }

    private void randomIndexGenerator(Boolean randomConnectionClose) {
        if (randomConnectionClose) {
            Random rn = new Random();
            index = (rn.nextInt(100) + 1) % 6;
        } else {
            index = -1;
        }
    }

    public MockServerThread[] getHandlers() {
        return handlers.clone();
    }

    public void setHandlers(MockServerThread[] handlers) {

        if (handlers != null) {
            this.handlers = new MockServerThread[handlers.length];
            System.arraycopy(handlers, 0, this.handlers, 0, handlers.length);
        }
    }

}
