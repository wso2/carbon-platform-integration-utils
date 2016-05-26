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

import io.netty.channel.ChannelInboundHandlerAdapter;
import org.wso2.gw.emulator.dsl.Protocol;
import org.wso2.gw.emulator.dsl.contexts.AbstractConfigurationBuilderContext;
import org.wso2.gw.emulator.http.server.processors.HttpRequestCustomProcessor;
import org.wso2.gw.emulator.http.server.processors.HttpResponseCustomProcessor;

import java.io.File;

/**
 * HttpServerConfigBuilderContext
 */
public class HttpServerConfigBuilderContext extends AbstractConfigurationBuilderContext {
    private static HttpServerConfigBuilderContext config;
    private String host = null;
    private int port;
    private String context;
    private int readingDelay;
    private int writingDelay;
    private boolean randomConnectionClose;
    private ChannelInboundHandlerAdapter logicHandler;
    private int logicDelay;
    private HttpRequestCustomProcessor customRequestProcessor;
    private HttpResponseCustomProcessor customResponseProcessor;
    private int queues;
    private int delay;
    private Protocol protocol;
    private File keyStore;
    private String keyStorePass;
    private String certPass;
    private File trustStore;
    private String trustStorePass;
    private int readTimeOut = 0;
    private int writeTimeOut = 0;
    private Boolean readingConnectionDrop = false;
    private Boolean writingConnectionDrop = false;
    private int connectionTimeOut = 0;
    private int connectTimeOut = 0;
    private boolean connectionFail = false;
    private boolean wireLog = false;

    private static HttpServerConfigBuilderContext getInstance() {
        config = new HttpServerConfigBuilderContext();
        return config;
    }

    public static HttpServerConfigBuilderContext configure() {
        return getInstance();
    }

    public boolean isWireLog() {
        return wireLog;
    }

    public boolean isConnectionFail() {
        return connectionFail;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public Boolean getReadingConnectionDrop() {
        return readingConnectionDrop;
    }

    public Boolean getWritingConnectionDrop() {
        return writingConnectionDrop;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public int getWriteTimeOut() {
        return writeTimeOut;
    }

    public HttpServerConfigBuilderContext keyStore(File keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    public HttpServerConfigBuilderContext keyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
        return this;
    }

    public HttpServerConfigBuilderContext certPass(String certPass) {
        this.certPass = certPass;
        return this;
    }

    public HttpServerConfigBuilderContext trustStore(File trustStore) {
        this.trustStore = trustStore;
        return this;
    }

    public String getCertPass() {
        return certPass;
    }

    public File getKeyStore() {
        return keyStore;
    }

    public String getKeyStorePass() {
        return keyStorePass;
    }

    public File getTrustStore() {
        return trustStore;
    }

    public String getTrustStorePass() {
        return trustStorePass;
    }

    public HttpServerConfigBuilderContext trustStorePass(String trustStorePass) {
        this.trustStorePass = trustStorePass;
        return this;
    }

    public HttpRequestCustomProcessor getHttpRequestCustomProcessor() {
        return customRequestProcessor;
    }

    public HttpResponseCustomProcessor getCustomResponseProcessor() {
        return customResponseProcessor;
    }

    public HttpServerConfigBuilderContext host(String host) {
        this.host = host;
        return this;
    }

    public HttpServerConfigBuilderContext port(int port) {
        this.port = port;
        return this;
    }

    public HttpServerConfigBuilderContext context(String context) {
        this.context = context;
        return this;
    }

    public HttpServerConfigBuilderContext withReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public HttpServerConfigBuilderContext withWriteTimeOut(int writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public HttpServerConfigBuilderContext withEnableReadingConnectionDrop() {
        this.readingConnectionDrop = true;
        return this;
    }

    public HttpServerConfigBuilderContext withEnableWritingConnectionDrop() {
        this.writingConnectionDrop = true;
        return this;
    }

    public HttpServerConfigBuilderContext withReadingDelay(int readingDelay) {
        this.readingDelay = readingDelay;
        return this;
    }

    public HttpServerConfigBuilderContext withWritingDelay(int writingDelay) {
        this.writingDelay = writingDelay;
        return this;
    }

    public HttpServerConfigBuilderContext withLogicDelay(int logicDelay) {
        this.logicDelay = logicDelay;
        return this;
    }

    public HttpServerConfigBuilderContext randomConnectionClose(boolean randomConnectionClose) {
        this.randomConnectionClose = randomConnectionClose;
        return this;
    }

    public HttpServerConfigBuilderContext logic(ChannelInboundHandlerAdapter logicHandler) {
        this.logicHandler = logicHandler;
        return this;
    }

    public HttpServerConfigBuilderContext withCustomRequestProcessor(HttpRequestCustomProcessor customProcessor) {
        this.customRequestProcessor = customProcessor;
        return this;
    }

    public HttpServerConfigBuilderContext withCustomResponseProcessor(HttpResponseCustomProcessor customProcessor) {
        this.customResponseProcessor = customProcessor;
        return this;
    }

    public HttpServerConfigBuilderContext withFastBackend(int queues, int delay) {
        this.queues = queues;
        this.delay = delay;
        return this;
    }

    public HttpServerConfigBuilderContext withProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public HttpServerConfigBuilderContext withConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
        return this;
    }

    public HttpServerConfigBuilderContext withConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
        return this;
    }

    public HttpServerConfigBuilderContext withEnableConnectionFail() {
        this.connectionFail = true;
        return this;
    }
    
    public HttpServerConfigBuilderContext withEnableWireLog() {
        wireLog = true;
        return this;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getQueues() {
        return queues;
    }

    public int getDelay() {
        return delay;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getContext() {
        return context;
    }

    public int getReadingDelay() {
        return readingDelay;
    }

    public int getWritingDelay() {
        return writingDelay;
    }

    public boolean isRandomConnectionClose() {
        return randomConnectionClose;
    }

    public int getLogicDelay() {
        return logicDelay;
    }

    public ChannelInboundHandlerAdapter getLogicHandler() {
        return logicHandler;
    }
}
