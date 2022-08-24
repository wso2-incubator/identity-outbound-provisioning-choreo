/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.provisioning.choreo.connector.scim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.application.common.model.SubProperty;
import org.wso2.carbon.identity.provisioning.AbstractOutboundProvisioningConnector;
import org.wso2.carbon.identity.provisioning.AbstractProvisioningConnectorFactory;
import org.wso2.carbon.identity.provisioning.IdentityProvisioningException;
import org.wso2.carbon.identity.provisioning.connector.scim.SCIMProvisioningConnectorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory setup for the UI.
 */
public class SCIMProvisioningChoreoConnectorFactory extends AbstractProvisioningConnectorFactory {

    public static final String CHOREO_SCIM = "scim-choreo";
    private static final Log log = LogFactory.getLog(SCIMProvisioningConnectorFactory.class);

    @Override
    protected AbstractOutboundProvisioningConnector buildConnector(Property[] provisioningProperties)
            throws IdentityProvisioningException {
        SCIMProvisioningChoreoConnector scimProvisioningChoreoConnector = new SCIMProvisioningChoreoConnector();
        scimProvisioningChoreoConnector.init(provisioningProperties);

        if (log.isDebugEnabled()) {
            log.debug("Created new connector of type : " + CHOREO_SCIM);
        }
        return scimProvisioningChoreoConnector;
    }

    @Override
    public String getConnectorType() {
        return CHOREO_SCIM; }

    @Override
    public List<Property> getConfigurationProperties() {

        List<Property> configProperties = new ArrayList<>();

        Property username = new Property();
        username.setName(SCIMProvisioningChoreoConnectorConstants.SCIM_API_EP);
        username.setDisplayName("API Endpoint");
        username.setRequired(true);
        username.setType("string");
        username.setDisplayOrder(1);
        configProperties.add(username);

        Property password = new Property();
        password.setName(SCIMProvisioningChoreoConnectorConstants.SCIM_API_TOKEN);
        password.setDisplayName("API Token");
        password.setRequired(true);
        password.setType("string");
        password.setDisplayOrder(2);
        configProperties.add(password);

        Property userstoreDomain = new Property();
        userstoreDomain.setName(SCIMProvisioningChoreoConnectorConstants.SCIM_USERSTORE_DOMAIN);
        userstoreDomain.setDisplayName("User Store Domain");
        userstoreDomain.setRequired(false);
        userstoreDomain.setType("string");
        userstoreDomain.setDisplayOrder(3);
        configProperties.add(userstoreDomain);

        Property enablePwdProvisioning = new Property();
        enablePwdProvisioning.setName(SCIMProvisioningChoreoConnectorConstants.SCIM_ENABLE_PASSWORD_PROVISIONING);
        enablePwdProvisioning.setDisplayName("Enable Password Provisioning (true/false)");
        enablePwdProvisioning.setRequired(false);
        enablePwdProvisioning.setDescription("Enable User password provisioning to a SCIM domain");
        enablePwdProvisioning.setType("boolean");
        enablePwdProvisioning.setDefaultValue("true");
        enablePwdProvisioning.setDisplayOrder(4);

        SubProperty defaultPwd = new SubProperty();
        defaultPwd.setName(SCIMProvisioningChoreoConnectorConstants.SCIM_DEFAULT_PASSWORD);
        defaultPwd.setDisplayName("Default Password");
        defaultPwd.setRequired(false);
        defaultPwd.setType("string");
        defaultPwd.setConfidential(true);
        enablePwdProvisioning.setSubProperties(new SubProperty[] {defaultPwd});
        configProperties.add(enablePwdProvisioning);

        return configProperties;
    }
}
