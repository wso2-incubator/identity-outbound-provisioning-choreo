# Writing a custom Outbound Provisioning Connector for the WSO2 IS

## Overview

This document specifies how to create and plug a custom SCIM outbound provisioning connector to the WSO2 Identity Server, that can incorporate with the Choreo APIs. 
Using this custom connector and WSO2 Choreo platform features, users will be able to perform any operations with provisioned content that is provisioned from the WSO2 Identity Server.

![provisionig flow](https://user-images.githubusercontent.com/58510122/187593297-a445cc98-4069-43ac-9921-0ab11addf1e2.png)

Before going to the implementation process, let’s discuss some important concepts related to the topic.

## What is identity outbound provisioning?

Outbound provisioning is automatically creating and removing user accounts in provisioning-enabled identity servers and web applications by scheduling or manually configuring outbound provisioning jobs. In the WSO2 identity server, identity provisioning is the main component that handles user/ role provisioning. Outbound provisioning provisions users to a trusted identity provider from the WSO2 identity server. This trusted identity provider must support identity inbound provisioning.

![outbound provisioning](https://user-images.githubusercontent.com/58510122/187600457-5d458615-09c5-425e-9637-f395be3f8717.png)

## What is an identity outbound provisioning connector?

To provision users from the WSO2 identity server to trusted external applications or servers, you need to configure a provisioning connector for your identity provider in the WSO2 identity server. Following are the connectors exits in the WSO2 identity server,

* Google 
* Salesforce
* SCIM

Now we are ready to discuss the implementation process of our custom provisioning connector. 

## The implementation procedure of the custom outbound provisioning connector

After a successful deployment of the custom connector, it will be listed under the outbound provisioning connectors under the identity provider’s configuration. And you can make it to listen to the user operations and provision users to the configured trusted external systems. The following figure shows the UI presentation after you plugged the custom connector into the WSO2 identity server.

![image5](https://user-images.githubusercontent.com/58510122/187602182-118051a7-a35c-4dd6-a65e-21f2ba25175e.jpg)

## The project structures 

The following figure shows the general project structure for creating an outbound connector. To start this structure, you need to create a maven project. 

![image8](https://user-images.githubusercontent.com/58510122/187603253-66432cd6-a692-4283-b048-2e2ac955abd8.png)

Since I will be using this to integrate with the choreo platform, I have named the custom connector as,

![image8-2](https://user-images.githubusercontent.com/58510122/187603783-b62a46be-7b8a-4ff0-bf6c-a1c08a48d106.png)

And you can change the class naming and package naming as your requirement.
Here I’m using the IntelliJ-idea platform as the developer platform to implement this connector throughout the process.

As you can see in the above structure in the figure there have two modules, where one module listed under the parent module. As the first step you need to create a maven project and fill the values in the required fields. After you create the project successfully in your parent pom.xml file should look like as follows,

```xml
<groupId>org.wso2.carbon.identity.outbound.provisioning.scim</groupId>
<artifactId>identity-outbound-provisioning-choreo-connector</artifactId>
<packaging>pom</packaging>
<name>WSO2 Carbon - SCIM - Provisioning Choreo Module</name>
<version>1.0-SNAPSHOT</version>
```

After that you need to create the second module which has the all the packages and classes, under the parent module. After you create the module successfully in your child module pom.xml file should look like as follows,

```xml
<parent>
<artifactId>identity-outbound-provisioning-choreo-connector</artifactId>
<groupId>org.wso2.carbon.identity.outbound.provisioning.scim</groupId>
<version>1.0-SNAPSHOT</version>
<relativePath>../../pom.xml</relativePath>
</parent>
 
<modelVersion>4.0.0</modelVersion>
<artifactId>org.wso2.carbon.identity.provisioning.choreo.connector.scim</artifactId>
<name>WSO2 Carbon - SCIM - Provisioning Component</name>
```

As the next step you should add following dependencies to the pom.xml files.

```xml
<dependencyManagement>
        <dependencies>
 
            <dependency>
                <groupId>org.wso2.carbon.identity.inbound.provisioning.scim</groupId>
                <artifactId>org.wso2.carbon.identity.scim.common</artifactId>
                <version>${identity.inbound.provisioning.scim.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.carbon.identity.framework</groupId>
                <artifactId>org.wso2.carbon.identity.core</artifactId>
                <version>${identity.framework.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.carbon.identity.framework</groupId>
                <artifactId>org.wso2.carbon.identity.provisioning</artifactId>
                <version>${identity.framework.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.carbon.identity.framework</groupId>
                <artifactId>org.wso2.carbon.identity.application.common</artifactId>
                <version>${identity.framework.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.carbon</groupId>
                <artifactId>org.wso2.carbon.user.core</artifactId>
                <version>${carbon.kernel.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.carbon</groupId>
                <artifactId>org.wso2.carbon.core</artifactId>
                <version>${carbon.kernel.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.carbon</groupId>
                <artifactId>org.wso2.carbon.utils</artifactId>
                <version>${carbon.kernel.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.wso2.charon</groupId>
                <artifactId>org.wso2.charon.core</artifactId>
                <version>${charon.wso2.version.identity}</version>
            </dependency>
 
            <dependency>
                <groupId>commons-lang.wso2</groupId>
                <artifactId>commons-lang</artifactId>
                <version>${commons-lang.wso2.version}</version>
            </dependency>
 
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <scope>test</scope>
                <version>${junit.version}</version>
            </dependency>
 
            <dependency>
                <groupId>org.apache.httpcomponents</groupId>
                <artifactId>httpclient</artifactId>
                <version>4.5.8</version>
            </dependency>
 
            <dependency>
             <groupId>org.wso2.carbon.identity.outbound.provisioning.scim</groupId>
             <artifactId>org.wso2.carbon.identity.provisioning.connector.scim</artifactId>
             <version>5.2.2</version>
            </dependency>
        </dependencies>
</dependencyManagement>
```
In the third step, you should implement the relevant classes inside the child module. To have a better idea about the implementation let’s discuss the usage of the above-mentioned classes in brief.

1. SCIMChoreoConnectorServiceComponent.java

This class is written inside the package “internal” under the package “org.wso2.carbon.identity.provisioning.choreo.connector.scim” as my example. It is responsible for registering the connector as an OSGI service. Because our connector will be developed as an OSGi Service.
There are some important annotations you should be aware of in this class. To identify the class as a service component “@component” annotation at the beginning of the class is used. The example case is as follows,

![Screenshot from 2022-08-31 11-38-02](https://user-images.githubusercontent.com/58510122/187606133-6a5bca78-ac00-48f5-a892-959bba82f3f0.png)

The sample code for the class is as follows,

```java
public class SCIMChoreoConnectorServiceComponent {
 
    private static final Log log = LogFactory.getLog(SCIMChoreoConnectorServiceComponent.class);
    protected void activate(ComponentContext context) {
 
        if (log.isDebugEnabled()) {
            log.debug("Activating SCIMChoreoConnectorServiceComponent");
        }
        try {
           SCIMProvisioningChoreoConnectorFactory scimProvisioningChoreoConnectorFactory =	     new SCIMProvisioningChoreoConnectorFactory();
           context.getBundleContext().registerService(AbstractProvisioningConnectorFactory.class.getName(), scimProvisioningChoreoConnectorFactory, null);
 
            if (log.isDebugEnabled()) {
                log.debug("SCIM Provisioning Choreo Connector bundle is activated");
            }
        } catch (Throwable e) {
            log.error(" Error while activating SCIM Provisioning Choreo Connector ", e);
        }
    }
}
```
2. SCIMChoreoConfigConstants.java

As a convention, to make your code cleaner and readable, we use this type of class to hold all the constants required for our custom provisioning connector. Then these constants can be easily referred from other classes.

3. SCIMProvisioningChoreoConnectorFactory.java

This class is written by extending the “AbstractProvisioningConnectorFactory” and this is the factory for the connector. This class should override the methods, “buildConnector”, “getConnectorType”, and “getConfigurationProperties” in the superclass. 
Since our connector is implemented to incorporate with the WSO2 choreo platform, for successful integration it requires some authentication details. Since choreo  APIs are using the OAuth2 as their authentication method, the connector requires configurations such as API token, and API endpoint URL to communicate. These details should be taken as user inputs. Therefore, there should be a UI to input these data and the provisioning framework is designed with flexibility such that we can define the configuration properties and it will generate the connector’s UI for us.
To get this UI configuration you should update the “getConfigurationProperties” method. For the choreo-SCIM connector, the sample code is as follows.

```java
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
```
After implementing the method, the UI will look as follows,

![image5](https://user-images.githubusercontent.com/58510122/187606726-6b85212c-26fa-4034-b372-e0ee37b7c8ad.jpg)

4. SCIMProvisioningChoreoConnector.java

This is the core class among all other classes. This class is responsible for handling the user provisioning to the configured external system with the features provided by all other classes. And this class is written by extending the superclass “AbstractOutboundProvisioningConnector”. Since the choreo-SCIM connector is using some features in scim connector,    the “SCIMProvisioningChoreoConnector” class is written by extending the “SCIMProvisioningConnector”[1] which was written by extending the “AbstractOutboundProvisioningConnector”. To get the customized behaviour for the connector you need to override the “init” and “provision” methods in the superclass.

Example code for the choreo-SCIM connector is as follows,

![image9](https://user-images.githubusercontent.com/58510122/187607043-3534d803-6fb1-479a-a0ea-2ae706d6fa38.png)

![image10](https://user-images.githubusercontent.com/58510122/187607114-27908f49-b45b-47cc-abb9-db7570136b2f.png)

In the provisioning method, it calls the provisioning operations such as “createUser”, “deleteUser”, and “updateUser”. Therefore, you need to implement the relevant logic for the provisioning operations as private methods. To send the HTTP SCIM requests in the choreo-SCIM connecter I have implemented HTTP clients and configured them with the required properties.  

## How to add a custom provisioning connector to the WSO2 identity server?

* After the implementation of all the classes and configurations, you need to build the project. To build a maven project use “mvn clean install” as the command. Then it will build the project and download the required dependencies. 
* After a successful build, the JAR type file will be created inside a directory called “target” which is in the child module’s pom.xml file directory. 

![image4](https://user-images.githubusercontent.com/58510122/187607417-598d7844-e780-4f2b-953b-454843a49cee.png)

* Then copy the JAR file of the custom connector to the <IS_HOME>/repository/components/dropins folder in the WSO2 identity server.
* Then you need to restart the identity server.
* After login into the management console, go to the Main -> Identity -> Identity providers -> Add
* Fill in the required information and expand the custom connector configuration you have created which was listed under the provisioning connectors.
* After that fill in the required fields, you have put in the UI and check the enable box.
* After that, you need to add your identity provider to the resident section which is under the service providers. 
* To check your connector functionality, you can remote-debug your connector with the IntelliJ-idea platform. For further details about remote debugging refer.[2]

## References

[1]. [Scim connector repository](https://github.com/wso2-extensions/identity-outbound-provisioning-scim/blob/master/components/org.wso2.carbon.identity.provisioning.connector.scim/src/main/java/org/wso2/carbon/identity/provisioning/connector/scim/SCIMProvisioningConnector.java)

[2]. [Remote debugging](https://anuradha-15.medium.com/remote-debugging-wso2-products-using-intellij-idea-ide-c0e828abd700)
