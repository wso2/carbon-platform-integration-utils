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

package org.wso2.carbon.integration.common.extensions.usermgt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.configurations.AutomationConfiguration;
import org.wso2.carbon.automation.engine.configurations.UrlGenerationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.admin.client.TenantManagementServiceClient;
import org.wso2.carbon.integration.common.admin.client.UserManagementClient;
import org.wso2.carbon.integration.common.extensions.utils.AutomationXpathConstants;
import org.wso2.carbon.integration.common.extensions.utils.ExtensionCommonConstants;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;

import javax.xml.xpath.XPathExpressionException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for adding tenants and users
 * defined under userManagement entry in automation.xml to servers.
 */
public class UserPopulator {

    private static final Log log = LogFactory.getLog(UserPopulator.class);
    String sessionCookie;
    String backendURL;
    List<String> tenantsList;
    TenantManagementServiceClient tenantStub;
    String productGroupName;
    String instanceName;
	List<String> rolesList;

    public UserPopulator(String productGroupName, String instanceName) throws XPathExpressionException {
        this.productGroupName = productGroupName;
        this.instanceName = instanceName;
        tenantsList = getTenantsDomainList();
	    rolesList = getRolesList();
    }

    public void populateUsers() throws Exception {
        String tenantAdminSession;
        UserManagementClient userManagementClient;
        AutomationContext automationContext = new AutomationContext(productGroupName, instanceName,
                TestUserMode.SUPER_TENANT_ADMIN);

        backendURL = automationContext.getContextUrls().getBackEndUrl();
        LoginLogoutClient loginLogoutUtil = new LoginLogoutClient(automationContext);
        sessionCookie = loginLogoutUtil.login();
        tenantStub = new TenantManagementServiceClient(backendURL, sessionCookie);

        //tenants is the domain of the tenants elements
        for(String tenants : tenantsList) {
            if(!tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                tenantStub.addTenant(tenants, automationContext.getConfigurationValue(
                        String.format(AutomationXpathConstants.ADMIN_USER_PASSWORD,
                                AutomationXpathConstants.TENANTS, tenants)),
                        automationContext.getConfigurationValue(String.format(AutomationXpathConstants.ADMIN_USER_USERNAME,
                                AutomationXpathConstants.TENANTS, tenants)), FrameworkConstants.TENANT_USAGE_PLAN_DEMO);
            }

            log.info("Start populating users for " + tenants);
            String superTenantReplacement = AutomationXpathConstants.TENANTS;

            if(tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                superTenantReplacement = AutomationXpathConstants.SUPER_TENANT;
            }
            tenantAdminSession =
                    login(automationContext.getConfigurationValue(String.format(AutomationXpathConstants.ADMIN_USER_USERNAME,
                            superTenantReplacement, tenants)), tenants, automationContext.getConfigurationValue(
                            String.format(AutomationXpathConstants.ADMIN_USER_PASSWORD, superTenantReplacement, tenants)),
                            backendURL, UrlGenerationUtil.getManagerHost(automationContext.getInstance()));

	        userManagementClient = new UserManagementClient(backendURL, tenantAdminSession);

	        // add roles to the tenant
	        for (String role : rolesList) {
		        if (!userManagementClient.roleNameExists(role)) {
			        List<String> permissions = getPermissionList(role);
			        userManagementClient.addRole(role, null, permissions.toArray(
					        new String[permissions.size()]));
		        }
	        }

            //here we populate the user list of the current tenant
            List<String> userList = getUserList(tenants);
            for(String tenantUsername : userList) {
                boolean isUserAddedAlready = userManagementClient.getUserList().contains(automationContext.
                        getConfigurationValue(String.format(AutomationXpathConstants.TENANT_USER_USERNAME,
                                superTenantReplacement, tenants, tenantUsername)));

                if(!isUserAddedAlready) {
	                String[] roles = new String[] { FrameworkConstants.ADMIN_ROLE };
	                List<String> userRoles = new ArrayList<String>(0);
	                NodeList roleList = automationContext.getConfigurationNodeList(
			                String.format(AutomationXpathConstants.TENANT_USER_ROLES,
			                              superTenantReplacement, tenants, tenantUsername)
	                );

	                if (roleList != null && roleList.item(0) != null) {
		                roleList = roleList.item(0).getChildNodes();
		                for (int i = 0; i < roleList.getLength(); i++) {
			                String role = roleList.item(i).getTextContent();
			                if (userManagementClient.roleNameExists(role)) {
				                userRoles.add(role);
			                } else {
				                log.error("Role is not exist : " + role);
			                }
		                }
		                if (userRoles.size() > 0) {
			                roles = userRoles.toArray(new String[userRoles.size()]);
		                }
	                }

                    userManagementClient.addUser(automationContext.getConfigurationValue(String.format(AutomationXpathConstants.
                            TENANT_USER_USERNAME, superTenantReplacement, tenants, tenantUsername)),
                            automationContext.getConfigurationValue(String.format(AutomationXpathConstants.TENANT_USER_PASSWORD,
                                    superTenantReplacement, tenants, tenantUsername)), roles, null);
                    log.info("User - " + tenantUsername + " created in tenant domain of " + " " + tenants);
                } else {
                    if(!tenantUsername.equals(ExtensionCommonConstants.ADMIN_USER)) {
                        log.info(tenantUsername + " is already in " + tenants);
                    }
                }
            }
        }
    }

    public void deleteUsers() throws Exception {
        String tenantAdminSession;
        AutomationContext automationContext = new AutomationContext(productGroupName, instanceName, TestUserMode.SUPER_TENANT_ADMIN);
        UserManagementClient userManagementClient;
        for(String tenants : tenantsList) {
            String superTenantReplacement = AutomationXpathConstants.TENANTS;
            if(tenants.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
                superTenantReplacement = AutomationXpathConstants.SUPER_TENANT;
            }
            backendURL = automationContext.getContextUrls().getBackEndUrl();
            tenantAdminSession = login(automationContext.getConfigurationValue(String.
                    format(AutomationXpathConstants.ADMIN_USER_USERNAME, superTenantReplacement, tenants)),
                    tenants, automationContext.
                    getConfigurationValue(String.format(AutomationXpathConstants.ADMIN_USER_PASSWORD,
                            superTenantReplacement, tenants)), backendURL,
                    UrlGenerationUtil.getManagerHost(automationContext.getInstance()));

            userManagementClient = new UserManagementClient(backendURL, tenantAdminSession);
            List<String> userList = getUserList(tenants);
            for(String user : userList) {
                boolean isUserAddedAlready = userManagementClient.getUserList().contains(automationContext.
                        getConfigurationValue(String.format(AutomationXpathConstants.TENANT_USER_USERNAME,
                                superTenantReplacement, tenants, user)));
                if(isUserAddedAlready) {
                    if(!user.equals(FrameworkConstants.ADMIN_ROLE)) {
                        userManagementClient.deleteUser(automationContext.getConfigurationValue(String.format(AutomationXpathConstants.
                                TENANT_USER_USERNAME, superTenantReplacement, tenants, user)));
                        log.info("User was deleted successfully - " + user);
                    }
                }
            }
        }
    }

    protected String login(String userName, String domain, String password, String backendUrl, String hostName) throws
            RemoteException, LoginAuthenticationExceptionException, XPathExpressionException {
        AuthenticatorClient loginClient = new AuthenticatorClient(backendUrl);
        if(!domain.equals(AutomationConfiguration.getConfigurationValue(ExtensionCommonConstants.SUPER_TENANT_DOMAIN_NAME))) {
            userName += "@" + domain;
        }
        return loginClient.login(userName, password, hostName);
    }

    public List<String> getTenantsDomainList() throws XPathExpressionException {
        List<String> tenantDomain = new ArrayList<String>();
        tenantDomain.add(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME);
        AutomationContext automationContext = new AutomationContext();
        int numberOfTenants = automationContext.getConfigurationNodeList(AutomationXpathConstants.TENANTS_NODE).item(0).
                getChildNodes().getLength();
        for(int i = 0; i < numberOfTenants; i++) {
            tenantDomain.add(automationContext.getConfigurationNodeList(AutomationXpathConstants.TENANTS_NODE).item(0).
                    getChildNodes().
                    item(i).getAttributes().getNamedItem(AutomationXpathConstants.DOMAIN).getNodeValue());
        }
        return tenantDomain;
    }

    public List<String> getUserList(String tenantDomain) throws XPathExpressionException {
        //according to the automation xml the super tenant no has to be accessed explicitly
        List<String> userList = new ArrayList<String>();
        AutomationContext automationContext = new AutomationContext();
        int numberOfUsers;
        String superTenantReplacement = AutomationXpathConstants.TENANTS;
        if(tenantDomain.equals(FrameworkConstants.SUPER_TENANT_DOMAIN_NAME)) {
            superTenantReplacement = AutomationXpathConstants.SUPER_TENANT;
        }
        numberOfUsers = automationContext.getConfigurationNodeList(String.format(AutomationXpathConstants.USER_NODE,
                superTenantReplacement, tenantDomain)).getLength();
        for(int i = 0; i < numberOfUsers; i++) {
            String userKey = automationContext.getConfigurationNodeList(String.
                    format(AutomationXpathConstants.USERS_NODE, superTenantReplacement, tenantDomain)).item(0).getChildNodes().
                    item(i).getAttributes().getNamedItem(AutomationXpathConstants.KEY).getNodeValue();
            userList.add(userKey);
        }
        return userList;
    }

	public List<String> getRolesList() throws XPathExpressionException {
		List<String> roles = new ArrayList<String>(0);
		AutomationContext automationContext = new AutomationContext();

		NodeList rolesList = automationContext.getConfigurationNodeList(
				AutomationXpathConstants.ROLES_NODE);
		if (rolesList != null && rolesList.item(0) != null) {
			rolesList = rolesList.item(0).getChildNodes();
			for (int i = 0; i < rolesList.getLength(); i++) {
				roles.add(rolesList.item(i).getAttributes()
				                   .getNamedItem(AutomationXpathConstants.NAME)
				                   .getNodeValue());
			}
		}
		return roles;
	}

	public List<String> getPermissionList(String role) throws XPathExpressionException {
		List<String> permissions = new ArrayList<String>(0);
		AutomationContext automationContext = new AutomationContext();

		NodeList permissionList = automationContext.getConfigurationNodeList(String.format(
				AutomationXpathConstants.PERMISSIONS_NODE, role));
		if (permissionList != null && permissionList.item(0) != null) {
			permissionList = permissionList.item(0).getChildNodes();
			for (int i = 0; i < permissionList.getLength(); i++) {
				permissions.add(permissionList.item(i).getTextContent());
			}
		}

		return permissions;
	}

}


