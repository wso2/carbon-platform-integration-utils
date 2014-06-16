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

package org.wso2.carbon.integration.common.admin.client;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.user.profile.stub.UserProfileMgtServiceStub;
import org.wso2.carbon.identity.user.profile.stub.UserProfileMgtServiceUserProfileExceptionException;
import org.wso2.carbon.identity.user.profile.stub.types.UserProfileDTO;
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;

import java.rmi.RemoteException;

public class UserProfileMgtAdminServiceClient {
    private final Log log = LogFactory.getLog(UserProfileMgtAdminServiceClient.class);
    private final String serviceName = "UserProfileMgtService";
    private UserProfileMgtServiceStub userProfileMgtStub;

    public UserProfileMgtAdminServiceClient(String backendURL, String sessionCookie)
            throws AxisFault {
        String endPoint = backendURL + serviceName;
        userProfileMgtStub = new UserProfileMgtServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(sessionCookie, userProfileMgtStub);
    }

    public UserProfileMgtAdminServiceClient(String backendURL, String userName, String password)
            throws AxisFault {
        String endPoint = backendURL + serviceName;
        userProfileMgtStub = new UserProfileMgtServiceStub(endPoint);
        AuthenticateStubUtil.authenticateStub(userName, password, userProfileMgtStub);
    }

    public void setUserProfile(String userName, UserProfileDTO userProfile)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        userProfileMgtStub.setUserProfile(userName, userProfile);
    }

    public UserProfileDTO getUserProfile(String userName, String profileName)
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtStub.getUserProfile(userName, profileName);
    }

    public UserProfileDTO getProfileFieldsForInternalStore()
            throws RemoteException, UserProfileMgtServiceUserProfileExceptionException {
        return userProfileMgtStub.getProfileFieldsForInternalStore();
    }
}
