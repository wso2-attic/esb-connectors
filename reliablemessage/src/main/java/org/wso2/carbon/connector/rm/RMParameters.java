/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */
package org.wso2.carbon.connector.rm;

public class RMParameters {

    private String wsdlUrl;
    private String serviceName;
    private String portName;
    private String ackInterval;
    private String retransmissionInterval;
    private String intraMessageThreshold;
    //Default value = false
    private boolean dynamicParam = false;

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getAckInterval() {
        return ackInterval;
    }

    public void setAckInterval(String ackInterval) {
        this.ackInterval = ackInterval;
    }

    public String getRetransmissionInterval() {
        return retransmissionInterval;
    }

    public void setRetransmissionInterval(String retransmissionInterval) {
        this.retransmissionInterval = retransmissionInterval;
    }

    public String getIntraMessageThreshold() {
        return intraMessageThreshold;
    }

    public void setIntraMessageThreshold(String intraMessageThreshold) {
        this.intraMessageThreshold = intraMessageThreshold;
    }

    public boolean isDynamicParam() {
        return dynamicParam;
    }

    public void setDynamicParam(boolean dynamicParam) {
        this.dynamicParam = dynamicParam;
    }
}
