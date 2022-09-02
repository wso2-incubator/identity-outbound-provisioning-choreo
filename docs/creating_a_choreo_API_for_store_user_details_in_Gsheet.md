# Creating a Choreo API with Gsheet connector for storing user detials comming into the API endpoint.

The purpose of this connector is to show the successful integration between WSO2 identity server scim-choreo connector and the choreo platform. Here, the Gsheet connector, which is a built-in connector in choreo platform is used to store the scim user details in a Gsheet coming into the API endpoint. Let’s move to the API creation process.

## Implementation steps

Navigate to the wso2 [chore platform](https://wso2.com/choreo/) and click create REST API. After filling in the description and other informations you will be redirected to the choreo component low-code diagram, and click the `edit code`. In this example, we are going to implement an API, which can catch the SCIM outbound provisioning requests coming to the API endpoint. 

## API Architecture

![image1](https://user-images.githubusercontent.com/58510122/187621818-ba55fc80-7174-4e1c-bfac-77673e20eb10.png)

## Method descriptions

* POST -> The HTTP POST request is sent to the API endpoint when the user is created in the Asgardeo.
* PUT -> The HTTP PUT request is sent to the API endpoint when the user details are updated in the Asgardeo.
* DELETE -> The HTTP DELETE request is sent to the API endpoint when the user is deleted in the Asgardeo.

We are going to use the [Google sheet API connector](https://central.ballerina.io/ballerinax/googleapis.sheets) to store the user details, which will be extracted from the HTTP requests coming into the API endpoint. The connector can be imported as follows,

```ballerina
import ballerinax/googleapis.sheets;
```

Since all the above HTTP methods are using the same connector,  initiate the connector as a `public` parameter. You should create a google sheet in the google and note the `sheet ID` for the implementation purpose. To have successful configuration of the Gsheet connector reffer the API [documentaion](https://lib.ballerina.io/ballerinax/googleapis.sheets/3.0.1). 

We are using a library from [ballerina central](https://central.ballerina.io/) to manipulate the incoming user details with the `SCIM request`. To import the library, use the following statement in the code online editor,

```ballerina
import org/scim;
```

## Implementation steps of the HTTP methods

### HTTP POST

This method can be created to catch the HTTP `POST` requests coming into the API endpoint. The payload, which is coming with the HTTP request, was bound using the record type(`SCIMUser`) as follows. 

```ballerina
resource function post .(@http:Payload scim:SCIMUser payload)
```

After the binding, the user details which are in the request can be accessed in the following way,

```ballerina
string UserName = payload.userName; 
string MetaInfo= payload.meta; 
```

### HTTP PUT

The method can be created to catch the HTTP `PUT` requests coming into the API endpoint. The payload, which is coming with the HTTP request was bound using the above-mentioned record type(`SCIMUser`) similar to the previous method. 

```ballerina
resource function put .(@http:Payload scim:SCIMUser payload)
```

After the binding, in a similar way as earlier user details can be accessed. 

### HTTP DELETE

The method can be created to catch the HTTP `DELETE` requests coming into the API endpoint. The `username` of a particular user is coming as a path parameter of the request URL. Therefore to catch the username, the path parameter was set to the HTTP DELETE method as follows,

```ballerina
resource function delete [string userName]
```

After that finish the API creation process, and get the `service endpoint` of the API and `access token`. To learn more about creating an API in the choreo platform referrer [this reference](https://wso2.com/choreo/docs/). 
