 /**
 * Copyright (c) 2022, WSO2 LLC. (https://www.wso2.com) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.provisioning.choreo.connector.scim;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.provisioning.connector.scim.SCIMProvisioningConnector;
import org.wso2.carbon.identity.scim.common.utils.AttributeMapper;
import org.wso2.charon.core.client.SCIMClient;
import org.wso2.charon.core.config.SCIMProvider;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.objects.AbstractSCIMObject;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.carbon.identity.provisioning.IdentityProvisioningException;
import org.wso2.carbon.identity.provisioning.IdentityProvisioningConstants;
import org.wso2.carbon.identity.provisioning.ProvisionedIdentifier;
import org.wso2.carbon.identity.provisioning.ProvisioningEntity;
import org.wso2.carbon.identity.provisioning.ProvisioningEntityType;
import org.wso2.carbon.identity.provisioning.ProvisioningOperation;
import org.wso2.carbon.identity.provisioning.ProvisioningUtil;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The connector for SCIM outbound provisioning with choreo connector.
 */
public class SCIMProvisioningChoreoConnector extends SCIMProvisioningConnector {

    private static final long serialVersionUID = -2800777211181005554L;
    private static final Log log = LogFactory.getLog(SCIMProvisioningChoreoConnector.class);
    private SCIMProvider scimProvider;
    private String userStoreDomainName;
    private SCIMObject newScimObject;

    @Override
    public void init(Property[] provisioningProperties) throws IdentityProvisioningException {

        scimProvider = new SCIMProvider();
        if (provisioningProperties != null && provisioningProperties.length > 0) {

            for (Property property : provisioningProperties) {

                if (SCIMProvisioningChoreoConnectorConstants.SCIM_API_EP.equals(property.getName())) {
                    populateSCIMProvider(property, SCIMChoreoConfigConstants.API_ENDPOINT);
                } else if (SCIMProvisioningChoreoConnectorConstants.SCIM_API_TOKEN.equals(property.getName())) {
                    populateSCIMProvider(property, SCIMChoreoConfigConstants.API_TOKEN);
                } else if (SCIMProvisioningChoreoConnectorConstants.SCIM_ENABLE_PASSWORD_PROVISIONING
                                .equals(property.getName())) {
                    populateSCIMProvider(property,
                                SCIMProvisioningChoreoConnectorConstants.SCIM_ENABLE_PASSWORD_PROVISIONING);
                }
                if (IdentityProvisioningConstants.JIT_PROVISIONING_ENABLED.equals(property
                        .getName()) && "1".equals(property.getValue())) {
                    jitProvisioningEnabled = true;
                }
            }
        }
    }

    @Override
    public ProvisionedIdentifier provision(ProvisioningEntity provisioningEntity)
            throws IdentityProvisioningException {

        if (provisioningEntity != null) {

            if (provisioningEntity.isJitProvisioning() && !isJitProvisioningEnabled()) {
                log.debug("JIT provisioning disabled for SCIM connector");
                return null;
            }
            if (provisioningEntity.getEntityType() == ProvisioningEntityType.USER) {
                if (provisioningEntity.getOperation() == ProvisioningOperation.DELETE) {
                    deleteUser(provisioningEntity);
                } else if (provisioningEntity.getOperation() == ProvisioningOperation.POST) {
                    createUser(provisioningEntity);
                } else if (provisioningEntity.getOperation() == ProvisioningOperation.PUT ||
                        provisioningEntity.getOperation() == ProvisioningOperation.PATCH) {
                    updateUser(provisioningEntity);
                }  else {
                    log.warn("Unsupported provisioning operation.");
                }
            } else {
                log.warn("Unsupported provisioning entity.");
            }
        }
        return null;
    }

    /**
     * Get deleted user details and send HTTP DELETE request.
     *
     * @param userEntity - user details
     * @throws IdentityProvisioningException - throws exception if there
     */
    private void deleteUser(ProvisioningEntity userEntity) throws IdentityProvisioningException {

        try {
            List<String> userNames = getUserNames(userEntity.getAttributes());
            String userName = null;

            if (CollectionUtils.isNotEmpty(userNames)) {
                userName = userNames.get(0);
            }
            User user = new User();
            user.setSchemaList(Collections.singletonList(SCIMConstants.CORE_SCHEMA_URI));
            user.setUserName(userName);

            try {

                String contentType = this.scimProvider.getProperty(SCIMChoreoConfigConstants.CONTENT_TYPE);
                if (contentType == null) {
                    contentType = SCIMChoreoConfigConstants.JSON_TYPE;
                }
                assert userName != null;
                String slashRemovedUserName = userName.replace('/', ',');

                String endPointUrl = scimProvider.getProperty(SCIMChoreoConfigConstants.API_ENDPOINT) + "/" + slashRemovedUserName;
                String tokenValue = scimProvider.getProperty(SCIMChoreoConfigConstants.API_TOKEN);
                String authorizationHeaderValue = SCIMChoreoConfigConstants.TOKEN_TYPE + tokenValue;

                HttpDelete delete = new HttpDelete(endPointUrl);
                delete.setHeader(SCIMChoreoConfigConstants.ACCEPT, SCIMChoreoConfigConstants.ACCEPT_TYPE);
                delete.setHeader(SCIMChoreoConfigConstants.CONTENT_TYPE, contentType);
                delete.setHeader(SCIMChoreoConfigConstants.AUTHORIZATION, authorizationHeaderValue);

                try (CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(delete)) { }

            } catch (IOException e) {
                throw new IdentityProvisioningException("Error while sending the user information to the API", e);
            }

        } catch (Exception e) {
            throw new IdentityProvisioningException("Error while deleting user.", e);
        }
    }

    /**
     * Get updated user details and send HTTP request.
     *
     * @param userEntity - user details
     * @throws IdentityProvisioningException - throws exception if there
     */
    private void updateUser(ProvisioningEntity userEntity) throws
            IdentityProvisioningException {

        try {

            List<String> userNames = getUserNames(userEntity.getAttributes());
            String userName = null;

            if (CollectionUtils.isNotEmpty(userNames)) {
                userName = userNames.get(0);
            }
            // Get single-valued claims.
            Map<String, String> singleValued = getSingleValuedClaims(userEntity.getAttributes());
            // If user created through management console, claim values are not present.
            User user = (User) AttributeMapper.constructSCIMObjectFromAttributes(singleValued,
                    SCIMConstants.USER_INT);
            user.setSchemaList(Arrays.asList(SCIMConstants.CORE_SCHEMA_URI));
            user.setUserName(userName);
            setUserPassword(user, userEntity);

            // Adding third party client.
            try {

                String contentType = this.scimProvider.getProperty(SCIMChoreoConfigConstants.CONTENT_TYPE);
                if (contentType == null) {
                    contentType = SCIMChoreoConfigConstants.JSON_TYPE;
                }
                String endPointUrl = scimProvider.getProperty(SCIMChoreoConfigConstants.API_ENDPOINT) + "/";
                String tokenValue = scimProvider.getProperty(SCIMChoreoConfigConstants.API_TOKEN);
                String authorizationHeader = SCIMChoreoConfigConstants.TOKEN_TYPE + tokenValue;

                HttpPut put = new HttpPut(endPointUrl);
                put.setHeader(SCIMChoreoConfigConstants.ACCEPT, SCIMChoreoConfigConstants.ACCEPT_TYPE);
                put.setHeader(SCIMChoreoConfigConstants.CONTENT_TYPE, contentType);
                put.setHeader(SCIMChoreoConfigConstants.AUTHORIZATION, authorizationHeader);

                // Fetching the SCIM object.
                SCIMClient newScimClient = new SCIMClient();
                this.newScimObject = user;
                String newEncodedUser = newScimClient.encodeSCIMObject((AbstractSCIMObject) newScimObject,
                        SCIMConstants.identifyFormat(contentType));

                // Send a JSON data.
                put.setEntity(new StringEntity(newEncodedUser));

                try (CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(put)) { }

            } catch (IOException e) {
                throw new IdentityProvisioningException("Error while sending the user information to the API", e);
            }

        } catch (Exception e) {
            throw new IdentityProvisioningException("Error while updating the user", e);
        }
    }

    /**
     * Get created user details and send HTTP POST request.
     *
     * @param userEntity - user details
     * @throws IdentityProvisioningException - throws exception if there
     */
    private void createUser(ProvisioningEntity userEntity) throws IdentityProvisioningException {

        try {
            List<String> userNames = getUserNames(userEntity.getAttributes());
            String userName = null;

            if (CollectionUtils.isNotEmpty(userNames)) {
                userName = userNames.get(0);
            }
            // Get single-valued claims.
            Map<String, String> singleValued = getSingleValuedClaims(userEntity.getAttributes());

            // If user created through management console, claim values are not present.
            User user = (User) AttributeMapper.constructSCIMObjectFromAttributes(singleValued,
                    SCIMConstants.USER_INT);

            user.setSchemaList(Arrays.asList(SCIMConstants.CORE_SCHEMA_URI));
            user.setUserName(userName);
            setUserPassword(user, userEntity);

            // Adding third party client.
            try {

                String contentType = this.scimProvider.getProperty(SCIMChoreoConfigConstants.CONTENT_TYPE);
                if (contentType == null) {
                    contentType = SCIMChoreoConfigConstants.JSON_TYPE;
                }

                String endPointUrl = scimProvider.getProperty(SCIMChoreoConfigConstants.API_ENDPOINT) + "/";
                String tokenValue = scimProvider.getProperty(SCIMChoreoConfigConstants.API_TOKEN);
                String authorizationHeader = SCIMChoreoConfigConstants.TOKEN_TYPE + tokenValue;

                HttpPost post = new HttpPost(endPointUrl);
                post.setHeader(SCIMChoreoConfigConstants.ACCEPT, SCIMChoreoConfigConstants.ACCEPT_TYPE);
                post.setHeader(SCIMChoreoConfigConstants.CONTENT_TYPE, contentType);
                post.setHeader(SCIMChoreoConfigConstants.AUTHORIZATION, authorizationHeader);

                // Fetching the SCIM object.
                SCIMClient newScimClient = new SCIMClient();
                this.newScimObject = user;
                String newEncodedUser = newScimClient.encodeSCIMObject((AbstractSCIMObject) newScimObject,
                        SCIMConstants.identifyFormat(contentType));

                // Send a JSON data.
                post.setEntity(new StringEntity(newEncodedUser));

                try (CloseableHttpClient httpClient = HttpClients.createDefault();
                     CloseableHttpResponse response = httpClient.execute(post)) {
                }
            } catch (IOException e) {
                throw new IdentityProvisioningException("Error while sending the user information to the API", e);
            }
        } catch (Exception e) {
            throw new IdentityProvisioningException("Error while creating the user", e);
        }
    }

    @Override
    public String getClaimDialectUri() throws IdentityProvisioningException {

        return SCIMProvisioningChoreoConnectorConstants.DEFAULT_SCIM_DIALECT;
    }

    @Override
    protected String getPassword(Map<ClaimMapping, List<String>> attributeMap) {

        String password = StringUtils.EMPTY;
        List<String> claimValues = ProvisioningUtil.getClaimValues(attributeMap,
                IdentityProvisioningConstants.PASSWORD_CLAIM_URI, null);

        if (CollectionUtils.isNotEmpty(claimValues) && StringUtils.isNotBlank(claimValues.get(0))) {
            password = claimValues.get(0);
        } else {
            log.warn("Could not get the password or a default password for the user. " +
                    "User will be provisioned with an empty password");
        }

        return password;
    }

    private void setUserPassword(User user, ProvisioningEntity userEntity) throws CharonException {

        if (Boolean.parseBoolean(scimProvider.getProperty(SCIMProvisioningChoreoConnectorConstants
                .SCIM_ENABLE_PASSWORD_PROVISIONING))) {
            user.setPassword(getPassword(userEntity.getAttributes()));
        }
    }

    @Override
    protected String getUserStoreDomainName() {
        
        return userStoreDomainName;
    }
    
    private void populateSCIMProvider(Property property, String scimPropertyName)
            throws IdentityProvisioningException {

        if (property.getValue() != null && property.getValue().length() > 0) {
            scimProvider.setProperty(scimPropertyName, property.getValue());
        } else if (property.getDefaultValue() != null) {
            scimProvider.setProperty(scimPropertyName, property.getDefaultValue());
        }
    }
}
