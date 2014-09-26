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
import org.wso2.carbon.logging.view.stub.LogViewerLogViewerException;
import org.wso2.carbon.logging.view.stub.LogViewerStub;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import java.rmi.RemoteException;

/**
 * This class can use to get system logs information
 */
public class LogViewerClient {
    private static final Log log = LogFactory.getLog(LogViewerClient.class);
    String serviceName = "LogViewer";
    private LogViewerStub logViewerStub;

    public LogViewerClient(String backEndUrl, String sessionCookie)
            throws AxisFault {
        String endpoint = backEndUrl + serviceName;
        logViewerStub = new LogViewerStub(endpoint);
        logViewerStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(300000);
        AuthenticateStubUtil.authenticateStub(sessionCookie, logViewerStub);
    }

    public LogViewerClient(String backEndURL, String userName, String password)
            throws AxisFault {
        String endpoint = backEndURL + serviceName;
        logViewerStub = new LogViewerStub(endpoint);
        logViewerStub._getServiceClient().getOptions().setTimeOutInMilliSeconds(300000);
        AuthenticateStubUtil.authenticateStub(userName, password, logViewerStub);
    }

    /**
     * Getting system logs
     *
     * @param logType   Log type (INFO,WARN,ERROR,DEBUG)
     * @param searchKey searching keyword
     * @param domain    - tenant domain
     * @param serverKey - server key defined at carbon.xml
     * @return logMessage array
     * @throws java.rmi.RemoteException Exception
     */
    public LogEvent[] getRemoteLogs(String logType, String searchKey, String domain, String serverKey)
            throws RemoteException, LogViewerLogViewerException {
        return logViewerStub.getLogs(logType, searchKey, domain, serverKey);
    }

    public String[] getServiceNames() throws RemoteException, LogViewerLogViewerException {
        try {
            return logViewerStub.getServiceNames();
        } catch (LogViewerLogViewerException e) {
            log.error("Unable to get service name list");
            throw new LogViewerLogViewerException("Unable to get service name list");
        }
    }

    public LogEvent[] getAllRemoteSystemLogs() throws RemoteException, LogViewerLogViewerException {
        try {
            return logViewerStub.getAllSystemLogs();
        } catch (RemoteException e) {
            log.error("Fail to get all logs ", e);
            throw new RemoteException("Fail to get all system logs ", e);
        }
    }


    /**
     * Getting system logs
     * <p/>
     * Deprecated because api change in stub passing LogViewerLogViewerException
     * l
     *
     * @param logType   Log type (INFO,WARN,ERROR,DEBUG)
     * @param searchKey searching keyword
     * @param domain    - tenant domain
     * @param serverKey - server key defined at carbon.xml
     * @return logMessage array
     * @throws java.rmi.RemoteException Exception
     */
    @Deprecated
    public LogEvent[] getLogs(String logType, String searchKey, String domain, String serverKey)
            throws RemoteException {
        LogEvent[] logEvents = new LogEvent[0];
        try {
            logEvents = logViewerStub.getLogs(logType, searchKey, domain, serverKey);
        } catch (LogViewerLogViewerException e) {
            log.error("LogViewerException occurred" + e.getStackTrace());
            throw new RemoteException("Logviewer Exception Occurred" + e.getStackTrace());
        }
        return logEvents;
    }


    /**
     * Deprecated
     *
     * @return
     * @throws RemoteException
     */
    @Deprecated
    public LogEvent[] getAllSystemLogs() throws RemoteException {
        LogEvent[] logEvents = new LogEvent[0];
        try {
            logEvents = logViewerStub.getAllSystemLogs();
        } catch (RemoteException e) {
            log.error("Fail to get all logs ", e);
            throw new RemoteException("Fail to get all system logs ", e);
        } catch (LogViewerLogViewerException e) {
            log.error("LogViewerException occurred" + e.getStackTrace());
            throw new RemoteException("Logviewer Exception Occurred" + e.getStackTrace());
        }
        return logEvents;
    }

    public boolean clearLogs() throws RemoteException {
        return logViewerStub.clearLogs();
    }
}
