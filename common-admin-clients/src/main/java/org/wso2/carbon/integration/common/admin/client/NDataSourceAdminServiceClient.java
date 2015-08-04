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
import org.wso2.carbon.integration.common.admin.client.utils.AuthenticateStubUtil;
import org.wso2.carbon.ndatasource.ui.stub.NDataSourceAdminDataSourceException;
import org.wso2.carbon.ndatasource.ui.stub.NDataSourceAdminStub;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceInfo;
import org.wso2.carbon.ndatasource.ui.stub.core.services.xsd.WSDataSourceMetaInfo;

import java.rmi.RemoteException;

public class NDataSourceAdminServiceClient {
    private static final Log log = LogFactory.getLog(NDataSourceAdminServiceClient.class);
    private final String serviceName = "NDataSourceAdmin";
    private NDataSourceAdminStub nDataSourceAdminStub;
    private String endPoint;

    public NDataSourceAdminServiceClient(String backEndUrl, String sessionCookie) throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        try {
            nDataSourceAdminStub = new NDataSourceAdminStub(endPoint);
        } catch (AxisFault axisFault) {
            log.error("nDataSourceAdminStub Initialization fail " + axisFault.getMessage());
            throw new AxisFault("nDataSourceAdminStub Initialization fail ", axisFault);
        }
        AuthenticateStubUtil.authenticateStub(sessionCookie, nDataSourceAdminStub);
    }

    public NDataSourceAdminServiceClient(String backEndUrl, String userName, String password)
            throws AxisFault {
        this.endPoint = backEndUrl + serviceName;
        try {
            nDataSourceAdminStub = new NDataSourceAdminStub(endPoint);
        } catch (AxisFault axisFault) {
            log.error("nDataSourceAdminStub Initialization fail " + axisFault.getMessage());
            throw new AxisFault("nDataSourceAdminStub Initialization fail ", axisFault);
        }
        AuthenticateStubUtil.authenticateStub(userName, password, nDataSourceAdminStub);
    }

    public void addDataSource(WSDataSourceMetaInfo dataSourceMetaInfo)
            throws RemoteException, NDataSourceAdminDataSourceException {
        validateDataSourceMetaInformation(dataSourceMetaInfo);

        if (log.isDebugEnabled()) {
            log.debug("Going to add Datasource :" + dataSourceMetaInfo.getName());
        }

        nDataSourceAdminStub.addDataSource(dataSourceMetaInfo);

    }

    public boolean testDataSourceConnection(WSDataSourceMetaInfo dataSourceMetaInfo)
            throws RemoteException, NDataSourceAdminDataSourceException {
        validateDataSourceMetaInformation(dataSourceMetaInfo);

        if (log.isDebugEnabled()) {
            log.debug("Going test connection of Datasource :" + dataSourceMetaInfo.getName());
        }
        return nDataSourceAdminStub.testDataSourceConnection(dataSourceMetaInfo);
    }

    public void deleteDataSource(String dsName)
            throws RemoteException, NDataSourceAdminDataSourceException {
        validateName(dsName);

        if (log.isDebugEnabled()) {
            log.debug("Going to delete a Data-source with name : " + dsName);
        }

        nDataSourceAdminStub.deleteDataSource(dsName);

    }

    public WSDataSourceInfo[] getAllDataSources()
            throws RemoteException, NDataSourceAdminDataSourceException {
        WSDataSourceInfo[] allDataSources = null;
        return allDataSources = nDataSourceAdminStub.getAllDataSources();
    }

    public WSDataSourceInfo getDataSource(String dsName)
            throws RemoteException, NDataSourceAdminDataSourceException {
        validateName(dsName);
        return nDataSourceAdminStub.getDataSource(dsName);

    }

    public WSDataSourceInfo[] getAllDataSourcesForType(String dsType)
            throws RemoteException, NDataSourceAdminDataSourceException {
        validateType(dsType);
        return nDataSourceAdminStub.getAllDataSourcesForType(dsType);

    }

    public String[] getDataSourceTypes() throws RemoteException,
                                                NDataSourceAdminDataSourceException {
        return nDataSourceAdminStub.getDataSourceTypes();
    }

    public boolean reloadAllDataSources()
            throws RemoteException, NDataSourceAdminDataSourceException {
        return nDataSourceAdminStub.reloadAllDataSources();
    }

    public boolean reloadDataSource(String dsName)
            throws RemoteException, NDataSourceAdminDataSourceException {
        validateName(dsName);
        return nDataSourceAdminStub.reloadDataSource(dsName);
    }

    private static void validateDataSourceMetaInformation(WSDataSourceMetaInfo dataSourceMetaInfo) {
        if (dataSourceMetaInfo == null) {
            handleException("WSDataSourceMetaInfo can not be found.");
        }
    }

    private static void validateName(String name) {
        if (name == null || "".equals(name)) {
            handleException("Name is null or empty");
        }
    }

    private static void validateType(String type) {
        if (type == null || "".equals(type)) {
            handleException("Type is null or empty");
        }
    }

    private static void handleException(String msg) {
        log.error(msg);
        throw new IllegalArgumentException(msg);
    }
}
