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

package org.wso2.gw.emulator.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;
import org.wso2.gw.emulator.dsl.EmulatorType;
import org.wso2.gw.emulator.http.ChannelPipelineInitializer;
import org.wso2.gw.emulator.http.server.contexts.HttpServerInformationContext;
import org.wso2.gw.emulator.http.server.contexts.MockServerThread;
import org.wso2.gw.emulator.util.ValidationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * HttpServerInitializer
 */
public class HttpServerInitializer extends Thread {
    private static final Logger log = Logger.getLogger(HttpServerInitializer.class);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private HttpServerInformationContext serverInformationContext;
    private int bossCount;
    private int workerCount;
    private Properties prop;
    private InputStream inputStream;
    private int queues;

    public HttpServerInitializer(HttpServerInformationContext serverInformationContext) {
        this.serverInformationContext = serverInformationContext;
        prop = new Properties();
        String propFileName = "server.properties";
        inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                log.error("Exception occurred while loading properties", e);
            }
        }
        setBossCount();
        setWorkerCount();
    }

    public void run() {
        queues = serverInformationContext.getServerConfigBuilderContext().getQueues();
        final MockServerThread[] handlers = new MockServerThread[queues];
        if (queues > 0) {

            for (int i = 0; i < queues; i++) {
                MockServerThread handler = new MockServerThread();
                handlers[i] = handler;
                handler.start();
            }
        } else {
            queues = 0;
        }

        //        SslContext sslCtx = null;


        /*if (protocol == Protocol.HTTPS) {
            SelfSignedCertificate ssc = null;
            try {
                ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } catch (CertificateException e) {
            log.error
                log.error(e);
            } catch (SSLException e) {
                log.error(e);
            }

        } else {
            sslCtx = null;
        }*/
        // Configure the server.

        bossGroup = new NioEventLoopGroup(bossCount);
        workerGroup = new NioEventLoopGroup(workerCount);
        ValidationUtil.validateMandatoryParameters(serverInformationContext.getServerConfigBuilderContext());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            int connectTimeOut = serverInformationContext.getServerConfigBuilderContext().getConnectTimeOut();
            if (connectTimeOut != 0) {
                try {
                    Thread.sleep(connectTimeOut);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
            ChannelPipelineInitializer channelPipelineInitializer = new ChannelPipelineInitializer(
                    EmulatorType.HTTP_SERVER, handlers);
            channelPipelineInitializer.setServerInformationContext(serverInformationContext);
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(channelPipelineInitializer);
            ChannelFuture f = serverBootstrap.bind(serverInformationContext.getServerConfigBuilderContext().getHost(),
                    serverInformationContext.getServerConfigBuilderContext().getPort()).sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("Exception occurred while initializing Emulator server", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void setBossCount() {
        this.bossCount = Integer.parseInt(prop.getProperty("boss_count"));
    }

    public void setWorkerCount() {
        this.workerCount = Integer.parseInt(prop.getProperty("worker_count"));
    }
}
