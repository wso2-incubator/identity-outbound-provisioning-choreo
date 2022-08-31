# CHOREO CONNECTOR 

## What is choreo?   

![image1](https://user-images.githubusercontent.com/58510122/187618491-d4010544-aef0-48b6-89e3-f61e09d4d0ac.png)

Choreo by WSO2 is an integration platform as a service (iPaaS) for innovation, productivity, and simplicity designed in the cloud for the cloud. The heart of Choreo is Ballerina, the cloud-native, open-source programming language introduced by WSO2. Further, Choreo utilizes existing WSO2 technologies to provide API management and integration capabilities plus customer identity and access management (CIAM) functionality.
This platform provides AI-assisted, visual low-code application development environment as well as a typical code environment and Choreo’s API Publisher Portal allows API developers to design, publish, and manage the lifecycle of APIs whereas the API Developer Portal allows consumers to consume APIs. 

## How does the choreo connector work?

* Asgardeo configuration

In the WSO2 identity server, a new outbound provisioning connector named “scim-choreo” is created to incorporate with the choreo platform, because the choreo APIs are using OAuth2 as the authentication method, and to appropriately connect with the Choreo APIs, Asgardeo server should have a way to configure it.

## Ballerina library for resolve the SCIM content

![image2](https://user-images.githubusercontent.com/58510122/187619089-0cb32b65-1a53-4321-84cc-c3a04acbdbc3.jpg)


In the new outbound connector, user details are provisioned according to the standard SCIM specification. Therefore to easily access the user details coming with the SCIM request, the object binder(record type) is created and published to the ballerina central. Users can import that library into their pro-code and bind with the payload coming along with the HTTP request’s bodies. After that users can access user details simply with dot notation.

## The uses of choreo connector

After catching the user details, using the Choreo platform features, clients can perform the following operations,
Automate to create a GitHub account using the User’s details.
Automate to create a Google account.
Automate to store the User details in a google sheet and manipulate the stored data(the implementation process of a sample scenario is here ).
Any other business needs according to the clients.
