/**
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.connector.zuorasoap;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.wso2.carbon.connector.core.AbstractConnector;

public class SetupExecuteZobjects extends AbstractConnector {

	public void connect(MessageContext synCtx) {
		SynapseLog synLog = getLog(synCtx);

		if (synLog.isTraceOrDebugEnabled()) {
			synLog.traceOrDebug("Start : Zuora execute ZObjects mediator");
			if (synLog.isTraceTraceEnabled()) {
				synLog.traceTrace("Message : " + synCtx.getEnvelope());
			}
		}

		ZuoraUtil zuoraUtil = ZuoraUtil.getZuoraUtil();
		zuoraUtil.addIds("execute", ZuoraUtil.ZUORA_ZOBJECTS, synCtx, synLog);

		if (synLog.isTraceOrDebugEnabled()) {
			synLog.traceOrDebug("End : Zuora execute ZObjects mediator");
			if (synLog.isTraceTraceEnabled()) {
				synLog.traceTrace("Message : " + synCtx.getEnvelope());
			}
		}
	}
}