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
package org.wso2.carbon.integration.common.utils;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

/**
 * A utility for logging into & logging out of Carbon servers
 */
public class LoginLogoutClient {
    private static final Log log = LogFactory.getLog(LoginLogoutClient.class);
    private AutomationContext automationContext;
    private AuthenticatorClient loginClient;

    public LoginLogoutClient(AutomationContext context) throws AutomationUtilException {
        try {
            this.automationContext = context;
            String backendURL = context.getContextUrls().getBackEndUrl();
            this.loginClient = new AuthenticatorClient(backendURL);
        } catch (AxisFault axisFault) {
            throw new AutomationUtilException("Error while initializing authenticator client ", axisFault);
        } catch (XPathExpressionException e) {
            throw new AutomationUtilException("Error while retrieving backend URL from auto context ", e);
        }
    }

    /**
     * Provides login to carbon server
     */
    public String login() throws AutomationUtilException {
        String userName = null;
        try {
            userName = automationContext.getContextTenant().getContextUser().getUserName();

            if (log.isDebugEnabled()) {
                log.debug("Login with user name " +
                          userName);
                log.debug("Login with password " +
                          automationContext.getContextTenant().getContextUser().getPassword());
                log.debug("Login with host name " +
                          automationContext.getInstance().getHosts().get("default"));
            }
            return loginClient.login(
                    userName,
                    automationContext.getContextTenant().getContextUser().getPassword(),
                    automationContext.getInstance().getHosts().get("default"));

        } catch (LoginAuthenticationExceptionException e) {
            throw new AutomationUtilException("Error while login as " + userName, e);
        } catch (IOException e) {
            throw new AutomationUtilException("Error while login as " + userName, e);
        } catch (XPathExpressionException e) {
            throw new AutomationUtilException("Error while login as " + userName, e);
        }
    }

    /**
     * Login method using username, password and host name.
     *
     * @param userName - username
     * @param password - password
     * @param hostName - url to login
     * @return - session cookie
     * @throws LoginAuthenticationExceptionException - throws if login fails
     */
    public String login(String userName, String password, String hostName)
            throws RemoteException, LoginAuthenticationExceptionException, AutomationUtilException {
        try {
            return loginClient.login(userName, password, hostName);
        } catch (LoginAuthenticationExceptionException e) {
            throw new AutomationUtilException("Error while login as " + userName, e);
        } catch (IOException e) {
            throw new AutomationUtilException("Error while login as " + userName, e);
        }
    }

    /**
     * Log out from carbon server
     */
    public void logout() throws LogoutAuthenticationExceptionException, RemoteException {
        loginClient.logOut();
    }
}

