/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.integration.common.tests;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.http.client.HttpsResponse;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;
import org.wso2.carbon.integration.common.tests.utils.JaggerySerevrTestUtils;
import org.wso2.carbon.webapp.mgt.stub.WebappAdminStub;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.VersionedWebappMetadata;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappMetadata;
import org.wso2.carbon.webapp.mgt.stub.types.carbon.WebappsWrapper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * This class validates Jaggery tests.
 * Note followings assumption/s needs to be met before running this test class:
 * - In the automation.xml you should mentioned jaggery tests hosted server as the default instance
 */
public class JaggeryServerTest {

    private static final Log log = LogFactory.getLog(JaggeryServerTest.class);

    private static JSONParser parser = new JSONParser();
    private static JaggerySerevrTestUtils result = new JaggerySerevrTestUtils();
    private List<String> appList;
    private List<String> jaggeryAppList = new ArrayList<String>();
    private ArrayList<String> testList = new ArrayList<String>();
    private HashMap<String, String> moduleMap = new HashMap<String, String>();
    private WebappAdminStub webappAdminStub;
    private String ip;
    private String port;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {

        AutomationContext automationContext = new AutomationContext();
        ip = automationContext.getDefaultInstance().getHosts().get("default");
        port = automationContext.getDefaultInstance().getPorts().get("https");
        AuthenticatorClient authenticationAdminClient
                = new AuthenticatorClient(automationContext.getContextUrls().getBackEndUrl());
        webappAdminStub = new WebappAdminStub(automationContext.getContextUrls().getBackEndUrl()
                + "WebappAdmin");
        AuthenticateStubUtil.authenticateStub(authenticationAdminClient.login(
                automationContext.getSuperTenant().
                getTenantAdmin().getUserName(), automationContext.getSuperTenant().
                getTenantAdmin().getPassword(),
                automationContext.getDefaultInstance().getHosts().get("default")), webappAdminStub);
        result.setMatchKey("specsResult");
        appList = getWebApplist("");
        jaggeryAppList();
        endpointList();
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {

        appList = null;
        testList = null;
        moduleMap = null;
        log.info("Jaggery Tests Execution Completed.");
    }

    @DataProvider
    public Object[][] endpointNameDataProvider() {    // implementing data provider with endpoints

        Iterator<String> itr = testList.iterator();

        String[][] endpoints = new String[testList.size()][];
        int i = 0;
        while (itr.hasNext()) {
            endpoints[i] = new String[]{itr.next()};
            i++;
        }
        return endpoints;
    }

    @Test(groups = "wso2.all", dataProvider = "endpointNameDataProvider",
            description = "Run Jaggery tests")
    public void testRunJaggeryTests(String endpointName) throws Exception {

        String testExecutionResults = getRequest("https://" + ip
                + ":" + port + File.separator + moduleMap.get(endpointName) +
                File.separator + "test" + File.separator, endpointName, false).getData();

        log.info("End point : " + endpointName);

        parser.reset();
        parser.parse(testExecutionResults, result, true);

        assertTrue(result.getValue().equals("Passed"), "Test Failure Expected " + "Passed "
                + "Test Execution Result " + result.getValue() + " Module Name " +
                moduleMap.get(endpointName) + " End Point " + endpointName);
    }

    /**
     * Sending the request and getting the response
     * @param Uri - request url
     * @param append - append request parameters
     * @throws IOException
     */
    private HttpsResponse getRequest(String Uri, String requestParameters, boolean append)
            throws IOException {
        if (Uri.startsWith("https://")) {
            String urlStr = Uri;
            if (requestParameters != null && requestParameters.length() > 0) {
                if (append) {
                    urlStr += "?" + requestParameters;
                } else {
                    urlStr += requestParameters;
                }
            }
            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            conn.setReadTimeout(30000);
            conn.connect();
            // Get the response
            StringBuilder sb = new StringBuilder();
            BufferedReader rd = null;
            try {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
            } catch (FileNotFoundException ignored) {
            } catch (IOException ignored) {
            } finally {
                if (rd != null) {
                    rd.close();
                }
                conn.disconnect();
            }
            return new HttpsResponse(sb.toString(), conn.getResponseCode());
        }
        return null;
    }

    /**
     * For filtering jaggeryWebapps only
     * @return List.
     * @throws RemoteException
     */
    private List jaggeryAppList() throws RemoteException {

        for (String appName : appList) {

            if (webappAdminStub.getStartedWebapp(appName).getWebappType().equals("jaggeryWebapp")) {
                jaggeryAppList.add(appName);
            }
        }
        return jaggeryAppList;
    }

    /**
     * For getting the testUrl list
     * @return ArrayList.
     * @throws IOException
     * @throws XPathExpressionException
     */
    private ArrayList endpointList() throws IOException, XPathExpressionException {

        for (String jaggeryApp : jaggeryAppList) {

            String data = getRequest("https://" + ip + ":" + port + File.separator + jaggeryApp
                    + File.separator + "test" + File.separator, "action=listsuits", true).getData();

            JaggerySerevrTestUtils url = new JaggerySerevrTestUtils();
            url.setMatchKey("url");

            if (data.contains("specsCount")) {

                try {
                    while (!url.isEnd()) {
                        parser.parse(data, url, true);
                        if (url.isFound()) {
                            url.setFound(false);
                            log.info("found url: " + url.getValue());
                            testList.add(url.getValue().toString());
                            moduleMap.put(url.getValue().toString(), jaggeryApp);
                        }
                    }
                } catch (ParseException pe) {
                    log.error("Exception: " + pe);
                }
            }
            parser.reset();
        }
        return testList;
    }

    /**
     * For getting WebAppList
     * @param webAppNameSearchString - selection criteria for selecting
     * @return List
     * @throws RemoteException
     */
    private List<String> getWebApplist(String webAppNameSearchString) throws RemoteException {
        List<String> list = new ArrayList<String>();
        WebappsWrapper wrapper = webappAdminStub.getPagedWebappsSummary(webAppNameSearchString,
                "ALL", "ALL", 0);
        VersionedWebappMetadata[] webappGroups = wrapper.getWebapps();

        if (webappGroups != null) {
            for (VersionedWebappMetadata webappGroup : webappGroups) {
                for (WebappMetadata metaData : webappGroup.getVersionGroups()) {
                    list.add(metaData.getWebappFile());
                }
            }
        }
        return list;
    }
}
