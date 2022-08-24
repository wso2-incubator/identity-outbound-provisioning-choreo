# identity-outbound-provisioning-choreo
Outbound Provisioning Connector for Choreo.
This connector can be use to enable outbound provisioning in the WSO2 identity server with the Choreo APIs.

# Pluggimg the connector to the Identity Server
## Step 1
First download the ZIP file or clone the repository into your PC. Then build the connector using `mvn clean install`
## Step 2
Navigate to the `<Connector_Home> -> Components -> org.wso2.carbon.identity.provisioning.choreo.connector.scim -> target` directory and get the created `JAR` file.
## Step 3
Navigate to the `<IS_HOME> -> repository -> components -> dropins` directory in your Identity Server and put the `JAR` file into that floder and restart the server.
## Step 4
Log into the WSO2 Identity Server management console, and click create new Identity provider. Fill the required details and expand the outbound provisioning connectors. 
You will see `scim-choreo` connector is listed under the outbound connectors. 

# Outbound Provisioning with `choreo-API`
To configure an identity provider for outbound provisioning with choreo-API, first you need to create a API in [Choreo](https://wso2.com/choreo/) platform. Then get the API endpoint and a token and configure the `scim-choreo` connector with `API End-point` & `API Key`.

Reffer this [example](https://docs.google.com/document/d/11FSXV6oD1TJVI8GdPXcg68wZDCHrBWOtNUI4aXKCqs0/edit?usp=sharing) to create an API for outbound provisioning with `scim-choreo` connector. 
