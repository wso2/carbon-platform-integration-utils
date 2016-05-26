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

package org.wso2.gw.emulator.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.EmulatorType;
import org.wso2.gw.emulator.http.client.contexts.HttpClientInformationContext;
import org.wso2.gw.emulator.http.client.handler.HttpClientHandler;
import org.wso2.gw.emulator.http.server.contexts.HttpServerInformationContext;
import org.wso2.gw.emulator.http.server.contexts.MockServerThread;
import org.wso2.gw.emulator.http.server.handler.HttpChunkedWriteHandler;
import org.wso2.gw.emulator.http.server.handler.HttpServerHandler;

/**
 * ChannelPipelineInitializer
 */
public class ChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {
    private EmulatorType emulatorType;
    private HttpServerInformationContext serverInformationContext;
    private HttpClientInformationContext clientInformationContext;
    private MockServerThread[] handlers;
    private static final Logger log = Logger.getLogger(ChannelPipelineInitializer.class);

    public ChannelPipelineInitializer(EmulatorType emulatorType, MockServerThread[] handlers) {
        this.emulatorType = emulatorType;
        if (handlers != null) {
            this.handlers = new MockServerThread[handlers.length];
            System.arraycopy(handlers, 0, this.handlers, 0, handlers.length);
        }
    }

    @Override
    public void initChannel(SocketChannel ch) {
        if (EmulatorType.HTTP_SERVER.equals(emulatorType)) {

            initializeHttpServerChannel(ch);
        } else if (EmulatorType.HTTP_CLIENT.equals(emulatorType)) {
            initializeHttpClientChannel(ch);
        }
    }

    private void initializeHttpServerChannel(SocketChannel ch) {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpChunkedWriteHandler(serverInformationContext));
        HttpServerHandler httpServerHandler = new HttpServerHandler(serverInformationContext);
        httpServerHandler.setHandlers(handlers);
        pipeline.addLast("httpResponseHandler", httpServerHandler);
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

    }

    private void initializeHttpClientChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpClientHandler(clientInformationContext));
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));

    }

    public void setServerInformationContext(HttpServerInformationContext serverInformationContext) {
        this.serverInformationContext = serverInformationContext;
    }

    public void setClientInformationContext(HttpClientInformationContext clientInformationContext) {
        this.clientInformationContext = clientInformationContext;
    }
}
