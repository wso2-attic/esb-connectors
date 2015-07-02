/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

import org.apache.synapse.MessageContext;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.synapse.SynapseException;
import org.apache.synapse.mediators.AbstractMediator;

public class Encoder extends AbstractMediator {

    public static final String SHA1KEY = "uri.var.sha1Key";

    public void generateHash(MessageContext msgctx) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder baseString = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        msgctx.setProperty("uri.var.ts", timeStamp);
        String input = msgctx.getProperty(Encoder.SHA1KEY).toString() + timeStamp;
        md.update(input.getBytes());
        String hash = String.format("%032x", new BigInteger(1, md.digest()));
        msgctx.setProperty("uri.var.hash", hash);
    }

    public boolean mediate(MessageContext msgctx) {
        try {
            generateHash(msgctx);
        } catch (Exception e) {
            throw new SynapseException(e);
        }
        return true;
    }
}
