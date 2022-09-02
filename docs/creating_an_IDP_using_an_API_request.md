# Creating an identity provider with the scim-choreo outbound provisioning connector in the WSO2 identity server using an API call

## Overview

To create an identity provider and configure it to outbound provisioning in the WSO2 identity server using the WSO2 management console, the first user needs to run the local instance of the identity server. After that, the user should log into the WSO2 management console and click Add under the identity providers which is in `Home → Identity → Identity providers`. After that user needs to set the required properties in the created identity provider. But the Asgardeo server(cloud version of identity server), doesn’t have a user interface to create an identity provider manually. To create an identity provider in the Asgardeo server user needs to provide an API request. Before creating IDP in the asgardeo server it’s better to know how to create an identity provider in the local instance of the WSO2 identity server using an API call. The following steps show how to create an IDP for the scim-choreo provisioning connector in the WSO2 IS server.

## 1. Adding scim-choreo outbound provisioning connector to the WSO2 identity server

To plug the scim-choreo outbound provisioning connector into the WSO2 identity server, clone this repository into your PC. Then build the repository(using the `mvn clean install` command) and get the `JAR` file from the `target` folder. Then plug the `JAR` file into the WSO2 identity server through the following path,
`<IS_HOME> → repository → components → dropins`.
Then restart the WSO2 IS and you will see the new scim-outbound connector has been added under the outbound connectors inside the identity providers in WSO2 IS. `(Main → Identity → Identity providers → Add)`

![image1](https://user-images.githubusercontent.com/58510122/187612814-d725d71e-8dd9-4632-a430-5affbac21e3e.png)

## 2. Configuring an identity provider with the scim-choreo outbound provisioning connector in the WSO2 identity server using an API call

To generate an API call for the WSO2 identity server, you can either use the POSTMAN IDE or cURL commands. The following code snippet shows the postman and the cURL commands for creating an IDP with the scim-choreo outbound provisioning connector in the WSO2 IS.

* With the postman IDE

First move to the website `Identity Provider Management API definition – V1`[2]. Then click the `Authorize` box in the above document. Then provide authentication details for your identity server`(admin: admin for carbon.super domain)` under the BasicAuth section. After that move to the bottom of the document and click `Run in Postman`, select `postman for web` and select a workspace for import the collection. Then open the postman IDE and you will see the imported collection is available there.
Move to the POST method `Add a new identity provider` under the collection, `wso2 identity server - IDP management API → identity-providers`. The following code snippet shows the request body for creating IDP with the scim-choreo outbound connector. You can copy the content and change the basic properties as your need such as name, description, etc. In the provisioning section, you have to fill in the property values according to the key names precisely. 

```json
{
 "name": "Choreo-SCIM",
 "description": "IDP for provisioning with choreo",
 "image": "",
 "isPrimary": false,
 "isFederationHub": false,
 "homeRealmIdentifier": "",
 "certificate": {
   "certificates": []
 },
 "alias": "https://localhost:9444/oauth2/token",
 "federatedAuthenticators": {
   "defaultAuthenticatorId": "R29vZ2xlT0lEQ0F1dGhlbnRpY2F0b3I",
   "authenticators": [
     {
       "authenticatorId": "R29vZ2xlT0lEQ0F1dGhlbnRpY2F0b3I",
       "isEnabled": false,
       "properties": []
     }
   ]
 },
 "provisioning": {
       "jit": {
           "isEnabled": false,
           "scheme": "PROVISION_SILENTLY",
           "userstore": "PRIMARY"
       },
       "outboundConnectors": {
           "defaultConnectorId": "c2NpbS1jaG9yZW8",
           "connectors": [
                   {
                       "connectorId": "c2NpbS1jaG9yZW8",
                       "isEnabled": true,
                       "blockingEnabled": false,
                       "rulesEnabled": false,
                       "properties": [
                          

                           {
                               "key": "scim-api-ep",
                               "value": ""
                           },
                           {
                               "key": "scim-api-token",
                               "value": ""
                           },
                           {
                               "key": "scim-enable-pwd-provisioning",
                               "value": ""
                           },
                           {
                               "key": "scim-user-store-domain",
                               "value": ""
                           }
                           ]
                }
            ]
    }
  }
}
```
* With the cURL command

Using the following cURL command, you can simply create IDP with the scim-choreo outbound connector. Here, we are using carbon.super as the domain and the username, and password as admin, which is encoded with base64 in the Authorization header. You can change the tenant-domain in the URL and the username, and password in the header for the admin user in that domain. And you need to replace the property values(which are annotated as `{{?}}` ) under the  provisioning section with respect to their key names. (ex: `scim-api-ep`, `scim-api-token`, `scim-enable-pwd-provisioning`, `scim-user-store-domain`)

```curl
curl -X POST "https://localhost:9443/t/carbon.super/api/server/v1/identity-providers" -H "accept: application/json" -H "Authorization: Basic YWRtaW46YWRtaW4=" -H "Content-Type: application/json" -d "{\"name\":\"Choreo-SCIM\",\"description\":\"IDP for provisioning with choreo\",\"image\":\"\",\"isPrimary\":false,\"isFederationHub\":false,\"homeRealmIdentifier\":\"\",\"certificate\":{\"certificates\":[]},\"alias\":\"https://localhost:9444/oauth2/token\",\"federatedAuthenticators\":{\"defaultAuthenticatorId\":\"R29vZ2xlT0lEQ0F1dGhlbnRpY2F0b3I\",\"authenticators\":[{\"authenticatorId\":\"R29vZ2xlT0lEQ0F1dGhlbnRpY2F0b3I\",\"isEnabled\":false,\"properties\":[]}]},\"provisioning\":{\"jit\":{\"isEnabled\":false,\"scheme\":\"PROVISION_SILENTLY\",\"userstore\":\"PRIMARY\"},\"outboundConnectors\":{\"defaultConnectorId\":\"c2NpbS1jaG9yZW8\",\"connectors\":[{\"connectorId\":\"c2NpbS1jaG9yZW8\",\"isEnabled\":true,\"blockingEnabled\":false,\"rulesEnabled\":false,\"properties\":[{\"key\":\"scim-api-ep\",\"value\":\"{{?}}\"},{\"key\":\"scim-api-token\",\"value\":\"{{?}}\"},{\"key\":\"scim-enable-pwd-provisioning\",\"value\":\"{{?}}\"},{\"key\":\"scim-user-store-domain\",\"value\":\"{{?}}\"}]}]}}}" -k
```

If the configurations are accurate, you will get status: 201 created response from the server.

## References

[1]. [Identity provider management API definition](https://is.docs.wso2.com/en/latest/develop/idp-rest-api/#identity-provider-management-api-definition-v1)
