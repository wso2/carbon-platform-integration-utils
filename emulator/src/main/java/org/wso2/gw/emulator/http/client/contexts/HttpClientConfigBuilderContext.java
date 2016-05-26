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

package org.wso2.gw.emulator.http.client.contexts;

import org.wso2.gw.emulator.dsl.Protocol;
import org.wso2.gw.emulator.dsl.contexts.AbstractConfigurationBuilderContext;

import java.io.File;

/**
 * Client's Configuration
 */
public class HttpClientConfigBuilderContext extends AbstractConfigurationBuilderContext {
    private static HttpClientConfigBuilderContext clientConfigBuilderContext;
    private String host;
    private int port;
    private int readingDelay;
    private Protocol protocol;
    private File keyStore;
    private String keyStorePass;
    private String certPass;
    private File trustStore;
    private String trustStorePass;

    private static HttpClientConfigBuilderContext getInstance() {
        clientConfigBuilderContext = new HttpClientConfigBuilderContext();
        return clientConfigBuilderContext;
    }

    public static HttpClientConfigBuilderContext configure() {
        return getInstance();
    }

    public HttpClientConfigBuilderContext keyStore(File keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    public HttpClientConfigBuilderContext keyStorePass(String keyStorePass) {
        this.keyStorePass = keyStorePass;
        return this;
    }

    public HttpClientConfigBuilderContext certPass(String certPass) {
        this.certPass = certPass;
        return this;
    }

    public HttpClientConfigBuilderContext trustStore(File trustStore) {
        this.trustStore = trustStore;
        return this;

    }

    public HttpClientConfigBuilderContext trustStorePass(String trustStorePass) {
        this.trustStorePass = trustStorePass;
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

    @Override
    public HttpClientConfigBuilderContext host(String host) {
        this.host = host;
        return this;
    }

    @Override
    public HttpClientConfigBuilderContext port(int port) {
        this.port = port;
        return this;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public HttpClientConfigBuilderContext withReadingDelay(int readingDelay) {
        this.readingDelay = readingDelay;
        return this;
    }

    public HttpClientConfigBuilderContext withProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReadingDelay() {
        return readingDelay;
    }

}
