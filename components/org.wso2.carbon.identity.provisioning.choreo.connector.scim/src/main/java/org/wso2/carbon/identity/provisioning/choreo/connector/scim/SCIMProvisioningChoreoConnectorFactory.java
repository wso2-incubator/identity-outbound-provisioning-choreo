/*
        Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com).

        WSO2 LLC. licenses this file to you under the Apache License,
        Version 2.0 (the "License"); you may not use this file except
        in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.
*/

package org.wso2.carbon.identity.provisioning.choreo.connector.scim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.provisioning.AbstractOutboundProvisioningConnector;
import org.wso2.carbon.identity.provisioning.AbstractProvisioningConnectorFactory;
import org.wso2.carbon.identity.provisioning.IdentityProvisioningException;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory setup of outbound connector for the UI .
 */
public class SCIMProvisioningChoreoConnectorFactory extends AbstractProvisioningConnectorFactory {

    public static final String CHOREO_SCIM = "scim-choreo";
    private static final Log log = LogFactory.getLog(SCIMProvisioningChoreoConnectorFactory.class);

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

        return CHOREO_SCIM; 
    }

    @Override
    public List<Property> getConfigurationProperties() {

        List<Property> configProperties = new ArrayList<>();

        Property apiEndpoint = new Property();
        setProperties(apiEndpoint, SCIMProvisioningChoreoConnectorConstants.SCIM_API_EP, "API Endpoint",
                true, "string", 1);
        configProperties.add(apiEndpoint);

        Property apiToken = new Property();
        setProperties(apiToken, SCIMProvisioningChoreoConnectorConstants.SCIM_API_TOKEN, "API Token",
                true, "string", 2);
        configProperties.add(apiToken);

        Property userStoreDomain = new Property();
        setProperties(userStoreDomain, SCIMProvisioningChoreoConnectorConstants.SCIM_USERSTORE_DOMAIN,
                "User Store Domain", false, "string", 3);
        configProperties.add(userStoreDomain);

        Property enablePwdProvisioning = new Property();
        setProperties(enablePwdProvisioning, SCIMProvisioningChoreoConnectorConstants.SCIM_ENABLE_PASSWORD_PROVISIONING,
                "Enable Password Provisioning (true/false)", false, "boolean", 4);
        enablePwdProvisioning.setDescription("Enable User password provisioning to a SCIM domain");
        enablePwdProvisioning.setDefaultValue("true");
        configProperties.add(enablePwdProvisioning);

        return configProperties;
    }

    /**
     * Setting the fields of the UI properties
     */
    private void setProperties(Property value, String propName, String dispName, Boolean requireValue, String type,
                               int dispOrder) {

        value.setName(propName);
        value.setDisplayName(dispName);
        value.setRequired(requireValue);
        value.setType(type);
        value.setDisplayOrder(dispOrder);

    }
}
